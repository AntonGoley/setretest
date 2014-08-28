package ru.crystals.set10.utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.crystals.pos.catalog.BarcodeEntity;
import ru.crystals.pos.catalog.MeasureEntity;
import ru.crystals.pos.catalog.ProductEntity;
import ru.crystals.pos.check.CheckStatus;
import ru.crystals.pos.check.DocumentEntity;
import ru.crystals.pos.check.InsertType;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.check.SessionEntity;
import ru.crystals.pos.check.ShiftEntity;
import ru.crystals.pos.check.UserEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import static ru.crystals.set10.utils.DbAdapter.*;

public class CheckGenerator {

	private static final Logger log = LoggerFactory.getLogger(DocsSender.class);
	private static ArrayList<ProductEntity> catalogGoods = new ArrayList();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
	private static ArrayList<DocumentEntity> peList = new ArrayList(10);
	
	private int cashId;
	private int shopNumber = -1;
	private  ShiftEntity shift;
	private static int checkNumber = 0;
	private static int shiftNum = 0;

	private DocsSender docSender;
	
	private static DbAdapter db = new  DbAdapter();
	
	private static final String SQL_GOODS = 
				"SELECT  markingofthegood, barc.code as barcode, pr.name as name, fullname, lastimporttime, measure_code, vat " +
				"FROM  un_cg_product pr " +
				"JOIN " +
				"un_cg_barcode barc " +
				"on barc.product_marking = pr.markingofthegood";
	
	private static final String SQL_GOODS_COUNT = "select count(*) from un_cg_product";
		
	private static final String SQL_MAX_SHIFT_NUM = "select max(numshift) as shiftnum from od_shift";
	
	private static final String SQL_CHECK_NUM = "select max(numberfield) from od_purchase where id_shift = " +
												"(select max(id) from od_shift where numshift = " +
												"(select  max(numshift) from od_shift))";
	
	private static  String SQL_GET_CHECK_BY_FISCALDOCNUM = "select count(*) from od_purchase where fiscaldocnum = '%s' ";

	
	
	static
	  {
		// задать номер смены и номер чека
		shiftNum = db.queryForInt(DB_RETAIL_OPERDAY, SQL_MAX_SHIFT_NUM);
		
		if (shiftNum == 0) {
			shiftNum = 1;
			checkNumber = 1;
		} else {
			// выбрать последний номер чека, созданный в смене shiftNum
			checkNumber = db.queryForInt(DB_RETAIL_OPERDAY, SQL_CHECK_NUM) + 1;
			shiftNum = db.queryForInt(DB_RETAIL_OPERDAY, SQL_MAX_SHIFT_NUM);
		}
		
		// проверить, есть ли товары в set_operday, и если нет, импортировать через ERP импорт
		if ((db.queryForInt(DB_RETAIL_SET, SQL_GOODS_COUNT)) < 30 ) {
			//TODO отправить товары
		}
		parsePurchasesFromDB();
	    generateChecks();
	  }
	
	
	public CheckGenerator(String serverIP, int shopNumber, int cashNumber) {
	    this.cashId = cashNumber;
	    this.shopNumber = shopNumber;
	    docSender = new DocsSender(serverIP, shopNumber, cashNumber);
	} 
	
	
	protected void sendDocument(Serializable document) {
		log.info("Try send one document - {}", document);
		int type = 201;
        
//        type = DataTypesEnum.PURCHASE_TYPE.code;
//        if (document instanceof PurchaseEntity) {
//            type = DataTypesEnum.PURCHASE_TYPE.code;
//        } else if (document instanceof ReportShiftEntity) {
//            type = DataTypesEnum.REPORT_TYPE.code;
//        } else if (document instanceof WithdrawalEntity) {
//            type = DataTypesEnum.WITHDRAWAL_TYPE.code;
//        } else if (document instanceof IntroductionEntity) {
//            type = DataTypesEnum.INTRODUCTION_TYPE.code;
//        } else {
//            LOG.warn("Неизвестный тип документа: {}", document);
//        }
		docSender.sendObject(type, document);
    }
	
	
	
	public DocumentEntity nextPurchase() {
	    generateChecks();
	    if (this.shift == null) {
	      this.shift = nextShift(null);
	    }
	    int idx = (int)random(peList.size() - 1);
	    DocumentEntity de = (DocumentEntity)peList.get(idx);
	    Date d = new Date(System.currentTimeMillis());
	    de.setDateCommit(d);
	    de.setShift(this.shift);
	    de.setNumber((long) checkNumber++);
	    de.setSession(this.shift.getSessionStart());
	    de.setId(System.currentTimeMillis());
	    if ((de instanceof PurchaseEntity)) {
	    	PurchaseEntity pe = (PurchaseEntity)de;
	      for (PositionEntity pos : pe.getPositions()) {
	        pos.setDateTime(d);
	      }
	    }  
	    sendDocument(de);
	    logCheckEntities((PurchaseEntity) de);
	    return de;
	}
	
	public DocumentEntity nextRefundCheck(
				PurchaseEntity superPurchase, 
				PositionEntity returnEntity, 
				long qnty,
				// произвольный возврат
				boolean arbitraryReturn) {
		
	    if (this.shift == null) {
	      this.shift = nextShift(null);
	    }
	    
	    ifCheckInRetail(superPurchase);
	    
	    DocumentEntity de = refundCheck(superPurchase, returnEntity, qnty, arbitraryReturn);
	    Date d = new Date(System.currentTimeMillis());
	    de.setDateCommit(d);
	    de.setShift(this.shift);
	    de.setNumber((long) checkNumber++);
	    de.setSession(this.shift.getSessionStart());
	    de.setId(System.currentTimeMillis());
	    if ((de instanceof PurchaseEntity)) {
	    	PurchaseEntity pe = (PurchaseEntity)de;
	      for (PositionEntity pos : pe.getPositions()) {
	        pos.setDateTime(d);
	      }
	    }  
	    sendDocument(de);
	    return de;
	}
	
	
    private ShiftEntity nextShift(SessionEntity session) {
      SessionEntity sess = session != null ? session : nextSession();
      ShiftEntity shift = new ShiftEntity();
      shift.setFiscalNum("Emulator." + this.shopNumber + "." + this.cashId);
      shift.setNumShift(Long.valueOf(shiftNum));
      shift.setShiftOpen(new Date(System.currentTimeMillis()));
      shift.setCashNum(new Long(this.cashId));
      shift.setShopIndex(Long.valueOf(this.shopNumber));
      shift.setSessionStart(sess);
      return shift;
    }
    
    
    private SessionEntity nextSession() {
      SessionEntity se = new SessionEntity();
      se.setDateBegin(new Date(System.currentTimeMillis()));
      UserEntity ue = new UserEntity();
      ue.setFirstName("Admin");
      ue.setLastName("Admin");
      ue.setMiddleName("Admin");
      ue.setSessions(new ArrayList());
      ue.getSessions().add(se);
      se.setUser(ue);
      return se;
    }
	  
	public static void parsePurchasesFromDB() {
		
		SqlRowSet goods = db.queryForRowSet(DB_RETAIL_SET, SQL_GOODS);

		try {
	      while (goods.next()) {
	        ProductEntity pe = new ProductEntity();
	        pe.setItem(goods.getString("markingofthegood"));
	        pe.setLastImportTime(sdf.parse(goods.getString("lastimporttime").substring(1, goods.getString("lastimporttime").length() - 1)));
	        MeasureEntity me = new MeasureEntity();
	        me.setCode(goods.getString("measure_code"));
	        pe.setMeasure(me);
	        pe.setName(goods.getString("name"));
	        pe.setNds(Float.valueOf(18.0F));
	        pe.setNdsClass("NDS");
	        BarcodeEntity be = new BarcodeEntity();
	        be.setBarCode(goods.getString("barcode"));
	        pe.setBarCode(be);
	        catalogGoods.add(pe);
	      }
	    } catch (Exception e) {
	      log.warn("Error: " + e.getMessage());
	    }
	  }
	
	
//	public static void addPe(){
	//	ProductEntity pe = new ProductEntity();
	//    pe.setItem("284406_KG");
	//    try {
	//		pe.setLastImportTime(sdf.parse("2014-08-08 12:34:52.069".substring(1, "2014-08-08 12:34:52.069".length() - 1)));
	//	} catch (ParseException e) {
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
	//    MeasureEntity me = new MeasureEntity();
	//    me.setCode("1");
	//    me.setName("1");
	//    pe.setMeasure(me);
	//    pe.setName("");
	//    pe.setNds(Float.valueOf(18.0F));
	//    pe.setNdsClass("NDS");
	//    BarcodeEntity be = new BarcodeEntity();
	//    be.setBarCode("1268044977064");
	//    pe.setBarCode(be);
	//    catalogGoods.add(pe);
//}
	
	public static DocumentEntity refundCheck( 
						PurchaseEntity superPurchase, 
						PositionEntity returnEntity, 
						long qnty, 
						boolean arbitraryReturn){
		
		 long summ = 0L; 
		 List positions = new ArrayList(1);
		 PurchaseEntity returnPe = new PurchaseEntity();
		 returnPe.setCheckStatus(CheckStatus.Registered);
		 returnPe.setOperationType(Boolean.valueOf(true));
		 
		 returnEntity.setProduct(returnEntity.getProduct());
		 summ += returnEntity.getSum().longValue();
		 positions.add(returnEntity);
	     returnPe.setFiscalDocNum("test; refund" + String.valueOf(System.currentTimeMillis()));
	     returnPe.setPositions(positions);
	     returnPe.setReturn();
	     
	     List paymentEntityList = new ArrayList(1);
	      CashPaymentEntity payE = new CashPaymentEntity();
	      payE.setDateCreate(new Date(System.currentTimeMillis()));
	      payE.setDateCommit(new Date(System.currentTimeMillis()));
	      payE.setSumPay(Long.valueOf(summ + 10000L));
	      payE.setPaymentType("CashPaymentEntity");
	      payE.setCurrency("RUB");
	
	      paymentEntityList.add(payE);
	      returnPe.setPayments(paymentEntityList);
	      returnPe.setDiscountValueTotal(Long.valueOf(0L));
	      returnPe.setCheckSumEnd(Long.valueOf(summ));
	      returnPe.setCheckSumStart(Long.valueOf(summ));
	      
	      // смотрим если произвольный возврат 
	      if (!arbitraryReturn)
	    	  returnPe.setSuperPurchase(superPurchase);
	      
	      return returnPe;
	      
	}
	
	private static void generateChecks() {
	    //long reportId = 1L;
	    while (peList.size() < 5) {
	      peList.add(new PurchaseEntity());
	      peList.get(peList.size() - 1);

	      PurchaseEntity pe = (PurchaseEntity)peList.get(peList.size() - 1);
	      pe.setCheckStatus(CheckStatus.Registered);
	      pe.setOperationType(Boolean.valueOf(true));
	      List positions = new ArrayList(100);
	      int end = (int)random(20) + 1;
	      //int end = 100;
	      long qnt = 0L;
	      long summ = 0L;
	      for (int i = 1; i < end; i++) {
	        PositionEntity pos = new PositionEntity();
//	        if (i == 0) {
//	        	addPe();
//	        	pos.setProduct((ProductEntity)catalogGoods.get(catalogGoods.size() - 1));
//	        } else {
//	        	pos.setProduct((ProductEntity)catalogGoods.get((int)(Math.random() * catalogGoods.size() - 1.0D)));
//	        }
	        pos.setProduct((ProductEntity)catalogGoods.get((int)(Math.random() * catalogGoods.size() - 1.0D)));
	        pos.setNumber(Long.valueOf(i));
	        if (i == 0) {
	        	qnt = (long) 1.235;
	        } else {
	        	qnt = random(5) + 1L;
	        }
	        pos.setQnty(Long.valueOf(qnt * 1000L));
	        pos.setPriceEnd(Long.valueOf(random(1000) * 100L));
	        pos.setSum(Long.valueOf(qnt * pos.getPriceEnd().longValue()));
	        summ += pos.getSum().longValue();
	        pos.setNdsSum(Long.valueOf(Math.round(pos.getSum().longValue() * 0.2D)));
	        pos.setInsertType(InsertType.Hand);
	        pos.setCalculateDiscount(Boolean.valueOf(true));
	        pos.setSumDiscount(Long.valueOf(0L));
	        pos.setDeleted(Boolean.valueOf(false));
	        pos.setSuccessProcessed(true);
	        pos.setDateTime(new Date(System.currentTimeMillis()));

	        positions.add(pos);
	        pe.setFiscalDocNum("test;" + String.valueOf(System.currentTimeMillis()));
	      }
	      pe.setPositions(positions);
	      List paymentEntityList = new ArrayList(1);
	      CashPaymentEntity payE = new CashPaymentEntity();
	      payE.setDateCreate(new Date(System.currentTimeMillis()));
	      payE.setDateCommit(new Date(System.currentTimeMillis()));
	      payE.setSumPay(Long.valueOf(summ + 10000L));
	      payE.setChange(Long.valueOf(10000L));
	      payE.setPaymentType("CashPaymentEntity");
	      payE.setCurrency("RUB");
	      paymentEntityList.add(payE);
	      pe.setPayments(paymentEntityList);
	      pe.setDiscountValueTotal(Long.valueOf(0L));
	      pe.setCheckSumEnd(Long.valueOf(summ));
	      pe.setCheckSumStart(Long.valueOf(summ));
	    }

//	    ReportShiftEntity rse = new ReportShiftEntity();
//	    rse.setReportZ(true);
//	    rse.setSumPurchaseFiscal(Long.valueOf(34650L));
//	    rse.setCountPurchase(Long.valueOf(1L));
//	    rse.setSumCashEnd(Long.valueOf(2851771786L));
//	    rse.setFiscalDocNum("1707:2465");
//
//	    rse.setId(Long.valueOf(reportId++));
//
//	    ReportPaymentTypeEntity reportPaymentTypeEntity = new ReportPaymentTypeEntity(rse.getId().longValue(), "CashPaymentEntity", 'P');
//	    reportPaymentTypeEntity.setPSumm(34650L);
//	    List listRPTE = new ArrayList();
//	    listRPTE.add(reportPaymentTypeEntity);
//	    rse.setPaymentsTypesList(listRPTE);
//	    peList.add(rse);
	  }

	public static long random(int max) {
	    return Math.round(Math.random() * max);
	}
	
	public void logCheckEntities(PurchaseEntity pe){
		Iterator<PositionEntity> i = pe.getPositions().iterator();
		PositionEntity  poe;
		log.info("Номер чека: " + pe.getNumber());
		log.info("Номер смены: " + pe.getShift());
		
		while (i.hasNext()) {
			poe = (PositionEntity) i.next();
			log.info("Позиция в товаре: " + poe.getName() + "; Баркод: " + poe.getBarCode());
		}
	} 
	
	// проверить, что чек покупки зарегистрирован в od_purchase (ищем по fiscaldocnum)
	public boolean ifCheckInRetail(PurchaseEntity purchase){
	    String fiscalDocNum =   purchase.getFiscalDocNum();
    	// ждем в течение минуты
    	int tryCount = 0;
    	while (tryCount < 60) {
    		tryCount++;
    		DisinsectorTools.delay(1000);
    		if (db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_GET_CHECK_BY_FISCALDOCNUM, fiscalDocNum)) >= 1) {
    			return true;
    		}	
    	}
    	log.info(String.format("Check transport timeout! No check found with number:  %s ", purchase.getNumber()));
		return false;
	}
	
	
}
