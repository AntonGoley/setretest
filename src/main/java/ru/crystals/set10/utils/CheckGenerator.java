package ru.crystals.set10.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import ru.crystals.pos.catalog.BarcodeEntity;
//import ru.crystals.pos.catalog.MeasureEntity;
//import ru.crystals.pos.catalog.ProductEntity;
//import ru.crystals.pos.check.CheckStatus;
//import ru.crystals.pos.check.DocumentEntity;
//import ru.crystals.pos.check.InsertType;
//import ru.crystals.pos.check.PositionEntity;
//import ru.crystals.pos.check.PurchaseEntity;
//import ru.crystals.pos.check.SessionEntity;
//import ru.crystals.pos.check.ShiftEntity;
//import ru.crystals.pos.check.UserEntity;
//import ru.crystals.pos.payments.CashPaymentEntity;



public class CheckGenerator {
/*	
	private static final Logger log = LoggerFactory.getLogger(DocsSender.class);
	//private final String SERVER_MODULE_NAME = "OPERDAY";
	
	private static ArrayList<ProductEntity> catalogGoods = new ArrayList();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
	private static ArrayList<DocumentEntity> peList = new ArrayList(10);

	private int cashId;
	private int shopNumber = -1;
	private ShiftEntity shift;
	DocsSender docSender;
	
	static
	  {
	    parsePurchasesDBFile("src/test/resources/ru/crystals/test2/dataFiles/goods.db");
	    generateChecks();
	  }
	
	public CheckGenerator(String serverIP, int shopNumber, int cashNumber) {
	    this.cashId = cashNumber;
	    this.shopNumber = shopNumber;
	    docSender = new DocsSender(serverIP, shopNumber, cashNumber);
	} 
	
	
	public static void addPe(){
		ProductEntity pe = new ProductEntity();
        pe.setItem("284406_KG");
        try {
			pe.setLastImportTime(sdf.parse("2014-08-08 12:34:52.069".substring(1, "2014-08-08 12:34:52.069".length() - 1)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        MeasureEntity me = new MeasureEntity();
        me.setCode("1");
        me.setName("1");
        pe.setMeasure(me);
        pe.setName("");
        pe.setNds(Float.valueOf(18.0F));
        pe.setNdsClass("NDS");
        BarcodeEntity be = new BarcodeEntity();
        be.setBarCode("1268044977064");
        pe.setBarCode(be);
        catalogGoods.add(pe);
	}
	
	
	protected void sendDocument(Serializable document) {
		log.info("Try send one document - {}", document);
		// PurchaseEntity
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
	    //de.setNumber(System.currentTimeMillis());
	    de.setNumber((long) 1);
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
      Calendar c = Calendar.getInstance();
      SessionEntity sess = session != null ? session : nextSession();
      ShiftEntity shift = new ShiftEntity();
      shift.setFiscalNum("Emulator." + this.shopNumber + "." + this.cashId);
      shift.setNumShift(Long.valueOf(c.get(5)));
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
	  
	public static int parsePurchasesDBFile(String fileName) {
	    catalogGoods.clear();
	    BufferedReader br = null;
	    try {
	      FileInputStream fstream = new FileInputStream(fileName);
	      DataInputStream in = new DataInputStream(fstream);
	      br = new BufferedReader(new InputStreamReader(in));

	      br.readLine();
	      String strLine;
	      while ((strLine = br.readLine()) != null) {
	        String[] params = strLine.split(";");
	        ProductEntity pe = new ProductEntity();
	        pe.setItem(params[0]);
	       // pe.setDiscriminator(params[1]);
	        //pe.setLastImportTime(sdf.parse(params[2].substring(1, params[2].length() - 1)));
	        pe.setLastImportTime(sdf.parse(params[1].substring(1, params[1].length() - 1)));
	        MeasureEntity me = new MeasureEntity();
	        //me.setCode(params[3]);
	        me.setCode(params[2]);
	        //me.setName(params[3]);
	        me.setName(params[2]);
	        pe.setMeasure(me);
	        //pe.setName(params[4]);
	        pe.setName(params[3]);
	        pe.setNds(Float.valueOf(18.0F));
	        //pe.setNdsClass(params[6]);
	        pe.setNdsClass("NDS");
	        BarcodeEntity be = new BarcodeEntity();
	        be.setBarCode(String.valueOf(System.currentTimeMillis()));
	        pe.setBarCode(be);
	        //pe.setPrecision(Double.parseDouble(params[7]));
	        //pe.setPrecision(Double.parseDouble(params[7]));
	        //pe.setStatus(Integer.parseInt(params[8]));
	        //pe.setStatus(Integer.parseInt(params[8]));
	        //pe.setHasRestriction(Boolean.parseBoolean(params[9]));
	       // pe.setHasRestriction(Boolean.parseBoolean(params[9]));
	       // pe.setCategoryMask((short) 0);
	        catalogGoods.add(pe);
	      }
	    } catch (Exception e) {
	      log.warn("Error: " + e.getMessage());
	      int in = -1;
	      return in;
	    }
	    finally
	    {
	      try
	      {
	        br.close();
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
	    return 0;
	  }
	
	private static void generateChecks() {
	    long reportId = 1L;
	    while (peList.size() < 5) {
	      peList.add(new PurchaseEntity());
	      peList.get(peList.size() - 1);

	      PurchaseEntity pe = (PurchaseEntity)peList.get(peList.size() - 1);

	      pe.setCheckStatus(CheckStatus.Registered);
	      pe.setOperationType(Boolean.valueOf(true));
	      List positions = new ArrayList(100);
	      int end = (int)random(20);
	      //int end = 100;
	      long qnt = 0L;
	      long summ = 0L;
	      for (int i = 0; i < end; i++) {
	        PositionEntity pos = new PositionEntity();
	        if (i == 0) {
	        	addPe();
	        	pos.setProduct((ProductEntity)catalogGoods.get(catalogGoods.size() - 1));
	        } else {
	        	pos.setProduct((ProductEntity)catalogGoods.get((int)(Math.random() * catalogGoods.size() - 1.0D)));
	        }
	        //pos.setProduct((ProductEntity)catalogGoods.get((int)(Math.random() * catalogGoods.size() - 1.0D)));
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
	        pe.setFiscalDocNum("4012;80" + i);
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
*/	
}
