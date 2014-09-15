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
import ru.crystals.pos.check.ReportPaymentTypeEntity;
import ru.crystals.pos.check.ReportShiftEntity;
import ru.crystals.pos.check.SessionEntity;
import ru.crystals.pos.check.ShiftEntity;
import ru.crystals.pos.check.UserEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.transport.DataTypesEnum;
import static ru.crystals.set10.utils.DbAdapter.*;

public class CheckGenerator {

	private static final Logger log = LoggerFactory.getLogger(DocsSender.class);
	private static ArrayList<ProductEntity> catalogGoods = new ArrayList();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
	private static ArrayList<DocumentEntity> peList = new ArrayList();
	
	private int cashNumber;
	public  int checkNumber;
	private  int shiftNum;
	private int shopNumber = -1;
	private  ShiftEntity shift;
	
	public  boolean nextShift = false;
	public  long yesterday = 0; //Long.valueOf("-11232000000"); //(86400000 * 130);
	
	private int reportId = 1;

	
	private DocsSender docSender;
	
	private static DbAdapter db = new  DbAdapter();
	
	private static final String SQL_GOODS = 
				"SELECT  markingofthegood, barc.code as barcode, pr.name as name, fullname, lastimporttime, measure_code, vat " +
				"FROM  un_cg_product pr " +
				"JOIN " +
				"un_cg_barcode barc " +
				"on barc.product_marking = pr.markingofthegood";
	
	private static final String SQL_GOODS_COUNT = "select count(*) from un_cg_product";
		
	private static final String SQL_MAX_SHIFT_NUM = "select max(numshift) from od_shift as od_s join od_purchase as od_p on od_p.id_shift = od_s.id where cashnum = %s";
														
	private static final String SQL_SHIFT_STATUS = "select state from od_shift where numshift = %s and cashnum = %s";
	
	private static final String SQL_CHECK_NUM = "select max(numberfield) from od_shift as od_s join od_purchase as od_p on od_p.id_shift = od_s.id where cashnum = %s and numshift = %s";
	
	private static  String SQL_GET_CHECK_BY_FISCALDOCNUM = "select count(*) from od_purchase where fiscaldocnum = '%s' ";
	
	private static  String SQL_GET_REPORT_BY_FISCALDOCNUM = "select count(*) from od_reportshift where fiscaldocnum = '%s' ";	
	
	private static  String SQL_GET_SHIFT_FINAL_SUM = "select sum(checksumstart) from od_purchase where operationtype = %s  and " +
													"id_shift = (select id from od_shift where cashnum=%s and numshift=%s)";
	
	static
	  {
		// проверить, есть ли товары в set_operday, и если нет, импортировать через ERP импорт
		if ((db.queryForInt(DB_RETAIL_SET, SQL_GOODS_COUNT)) < 10 ) {
			SoapRequestSender soapSender  = new SoapRequestSender();
			soapSender.sendGoodsToStartTesting(Config.RETAIL_HOST);
		}
		parsePurchasesFromDB();
	    generateChecks();
	  }
	
	public CheckGenerator(String serverIP, int shopNumber, int cashNumber) {
	    this.cashNumber = cashNumber;
	    this.shopNumber = shopNumber;
	    this.shiftNum = getCurrentShiftNum(cashNumber);
	    this.checkNumber =  getNextCheckNum(cashNumber, shiftNum);
	    docSender = new DocsSender(serverIP, shopNumber, cashNumber);
	    log.info("CashNumber = " + cashNumber +  "; ShiftNum = " + shiftNum + "; NextCheckNumber = " + checkNumber);
	} 
	
	public int getCurrentShiftNum(int cashNumber) {
		shiftNum = db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_MAX_SHIFT_NUM, cashNumber));
		if (shiftNum == 0) {
			shiftNum = 1;
		} else {
			ifShiftClosed(cashNumber, shiftNum);
		}
		return shiftNum;
	}
	
	public int getNextCheckNum(int cashNumber, int shiftNumber) {
		checkNumber = db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_CHECK_NUM, cashNumber, shiftNumber));
		return ++checkNumber;
	}
	
	public int getShiftSumChecks() {
		return db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_GET_SHIFT_FINAL_SUM, String.valueOf("true"), cashNumber, shiftNum));
	}
	
	public int getShiftSumChecksRefund() {
		return db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_GET_SHIFT_FINAL_SUM, String.valueOf("false"), cashNumber, shiftNum));
	}
	
	private boolean ifShiftClosed(int cashNumber, int shiftNumber) {
		int querryResult;
		querryResult = db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_SHIFT_STATUS, shiftNumber, cashNumber)); 
		if ((int) querryResult == 0) { 
			return false;
		} else {
			useNextShift();
			return true;
		}
	}
	
	protected void sendDocument(Serializable document) {
		log.info("Try send one document - {}", document);
		int type;
		type = DataTypesEnum.PURCHASE_TYPE.code;
		
        if (document instanceof PurchaseEntity) {
            type = DataTypesEnum.PURCHASE_TYPE.code;
        } else if (document instanceof ReportShiftEntity) {
            type = DataTypesEnum.REPORT_TYPE.code;
//        } else if (document instanceof WithdrawalEntity) {
//            type = DataTypesEnum.WITHDRAWAL_TYPE.code;
//        } else if (document instanceof IntroductionEntity) {
//            type = DataTypesEnum.INTRODUCTION_TYPE.code;
//        } else {
//            LOG.warn("Неизвестный тип документа: {}", document);
        }
		docSender.sendObject(type, document);
    }
	
	public DocumentEntity nextPurchase() {
	    generateChecks();
	    if (this.shift == null || nextShift || ifShiftClosed(cashNumber, shiftNum)) {
	      this.shift = nextShift(null);
	      nextShift = false;
	    }
	    
	    int idx = (int)random(peList.size() - 2) + 1;
	    DocumentEntity de = (DocumentEntity)peList.get(idx);
	    Date d = new Date(System.currentTimeMillis() - yesterday);
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
	    ifCheckInRetail((PurchaseEntity) de);
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

	    DocumentEntity de = refundCheck(superPurchase, returnEntity, qnty, arbitraryReturn);
	    Date d = new Date(System.currentTimeMillis() - yesterday);
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
	    ifCheckInRetail((PurchaseEntity) de);
	    return de;
	}
	
	// закрывает текущую смену
	public DocumentEntity nextZReport(){
		if (this.shift == null) {
		      this.shift = nextShift(null);
		    }
	    ReportShiftEntity rse = new ReportShiftEntity();
	    rse.setReportZ(true);
	    rse.setCountPurchase(Long.valueOf(1L));
	    rse.setSumCashEnd(Long.valueOf(2851771786L));
	    rse.setFiscalDocNum("testZ;" + String.valueOf(System.currentTimeMillis()));
	    // сумма чеков продажи за смену в ФР
	    rse.setSumPurchaseFiscal(Long.valueOf(getShiftSumChecks()));
	    // сумма возвратов по ФР
	    rse.setSumReturnFiscal((long) getShiftSumChecksRefund());
	    shift.setShiftClose( new Date(System.currentTimeMillis() - yesterday));
	    rse.setId(Long.valueOf(reportId++));

	    ReportPaymentTypeEntity reportPaymentTypeEntity = new ReportPaymentTypeEntity(rse.getId().longValue(), "CashPaymentEntity", 'P');
	    //reportPaymentTypeEntity.setPSumm(getShiftSum(false));
	    
	    List listRPTE = new ArrayList();
	    listRPTE.add(reportPaymentTypeEntity);
	    rse.setPaymentsTypesList(listRPTE);
	    rse.setShift(shift);
	    rse.setNumber((long) checkNumber);
	    rse.setSession(shift.getSessionStart());

	    
	    nextShift = true;
	    sendDocument(rse);
	    //logCheckEntities((PurchaseEntity) rse);
	    ifCheckInRetail((ReportShiftEntity) rse);
	    return rse;
	}
	
	
    private ShiftEntity nextShift(SessionEntity session) {
      SessionEntity sess = session != null ? session : nextSession();
      ShiftEntity shift = new ShiftEntity();
      shift.setFiscalNum("Emulator." + this.shopNumber + "." + this.cashNumber);
      if (nextShift) {
    	  nextShift = false;
      }
      shift.setNumShift(Long.valueOf(shiftNum));
      shift.setShiftOpen(new Date(System.currentTimeMillis() - yesterday));
      shift.setCashNum(new Long(this.cashNumber));
      shift.setShopIndex(Long.valueOf(this.shopNumber));
      shift.setSessionStart(sess);
      return shift;
    }
    
   
    private SessionEntity nextSession() {
      SessionEntity se = new SessionEntity();
      se.setDateBegin(new Date(System.currentTimeMillis() - yesterday));
      UserEntity ue = new UserEntity();
      ue.setFirstName(String.format("Cashier_%s", String.valueOf(cashNumber) + "_first_name"));
      ue.setLastName(String.format("Cashier_%s", String.valueOf(cashNumber) + "_last_name"));
      ue.setMiddleName(String.format("Cashier_%s", String.valueOf(cashNumber) + "_middle_name"));
      ue.setTabNum(String.valueOf(cashNumber));
      ue.setSessions(new ArrayList());
      ue.getSessions().add(se);
      se.setUser(ue);
      return se;
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
	      payE.setSumPay(Long.valueOf(summ));
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
	    while (peList.size() < 20) {
	      peList.add(new PurchaseEntity());
	      peList.get(peList.size() - 1);

	      PurchaseEntity pe = (PurchaseEntity)peList.get(peList.size() - 1);
	      pe.setCheckStatus(CheckStatus.Registered);
	      pe.setOperationType(Boolean.valueOf(true));
	      List positions = new ArrayList(100);
	      int end = (int)random(20) + 1;
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
	      payE.setSumPay(Long.valueOf(summ));
	      //payE.setChange(Long.valueOf(10000L));
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
	public boolean ifCheckInRetail(DocumentEntity purchase){
	    String fiscalDocNum =   purchase.getFiscalDocNum();
	    String dbRequest = "";
	    if (purchase instanceof PurchaseEntity) {
	    	dbRequest = SQL_GET_CHECK_BY_FISCALDOCNUM;
	    } else if (purchase instanceof ReportShiftEntity) {
	    	dbRequest = SQL_GET_REPORT_BY_FISCALDOCNUM;
	    }
	    
    	// ждем в течение минуты
	    int timeOut = 60;
    	int tryCount = 0;
    	while (tryCount < timeOut) {
    		tryCount++;
    		DisinsectorTools.delay(1000);
    		if (db.queryForInt(DB_RETAIL_OPERDAY, String.format(dbRequest, fiscalDocNum)) == 1) {
    			return true;
    		}	
    	}
    	log.info(String.format("Check transport timeout! No check found with number:  %s ", purchase.getNumber()));
		return false;
	}
	
	public void useNextShift(){
		this.nextShift = true;
		shiftNum++;
		checkNumber = 1;
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
	
	public static long random(int max) {
	    return Math.round(Math.random() * max);
	}
	
}
