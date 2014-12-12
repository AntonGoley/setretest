package ru.crystals.set10.utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.crystals.pos.check.CheckStatus;
import ru.crystals.pos.check.DocumentEntity;
import ru.crystals.pos.check.IntroductionEntity;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.check.ReportPaymentTypeEntity;
import ru.crystals.pos.check.ReportShiftEntity;
import ru.crystals.pos.check.SessionEntity;
import ru.crystals.pos.check.ShiftEntity;
import ru.crystals.pos.check.UserEntity;
import ru.crystals.pos.check.WithdrawalEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import ru.crystals.pos.payments.PaymentEntity;
import ru.crystals.pos.payments.PaymentTransactionEntity;
import ru.crystals.transport.DataTypesEnum;
import static ru.crystals.set10.utils.DbAdapter.*;
import static ru.crystals.set10.utils.GoodsParser.*;

public class CashEmulator {
	
	private static final Logger log = LoggerFactory.getLogger(CashEmulator.class);
	
	private  int cashNumber;
	private  int checkNumber;
	private  int shiftNum;
	private  int shopNumber = -1;
	private  ShiftEntity shift;
	
	public  boolean nextShift = false;
	public  long yesterday = Long.valueOf("-0"); //(86400000 * 130); ("-11232000000")
	
	/*
	 * Список созданных в тестах касс
	 */
	private static HashMap<String, CashEmulator> activeCashEmulators = new HashMap<String, CashEmulator>();
	
	private int reportId = 1;
	
	private DocsSender docSender;
	
	private static DbAdapter db = new  DbAdapter();
		
	private static final String SQL_MAX_SHIFT_NUM = "select max(numshift) from od_shift as od_s join od_purchase as od_p on od_p.id_shift = od_s.id where cashnum = %s  and shopindex = %s and shiftcreate >= '%s 00:00:00' and shiftcreate < '%s 23:59:59.999'";
														
	private static final String SQL_SHIFT_STATUS = "select state from od_shift where numshift = %s and cashnum = %s and shopindex = %s and shiftcreate >= '%s 00:00:00' and shiftcreate < '%s 23:59:59.999'";
	
	private static final String SQL_CHECK_NUM = "select max(numberfield) from od_shift as od_s join od_purchase as od_p on od_p.id_shift = od_s.id where cashnum = %s and numshift = %s and shopindex = %s and shiftcreate >= '%s 00:00:00' and shiftcreate < '%s 23:59:59.999'";
	
	private static  String SQL_GET_CHECK_BY_FISCALDOCNUM = "select count(*) from od_purchase where fiscaldocnum = '%s' ";
	
	private static  String SQL_GET_REPORT_BY_FISCALDOCNUM = "select count(*) from od_reportshift where fiscaldocnum = '%s' ";	
	
	private static  String SQL_GET_WITHDRAWAL_BY_FISCALDOCNUM = "select count(*) from od_withdrawal where fiscaldocnum = '%s' ";	
	
	private static  String SQL_GET_INTRODUCTION_BY_FISCALDOCNUM = "select count(*) from od_introduction where fiscaldocnum = '%s' ";	
	
	private static  String SQL_GET_SHIFT_FINAL_SUM = "select sum(checksumstart) from od_purchase where operationtype = %s  and " +
													"id_shift = (select id from od_shift where cashnum=%s and numshift=%s and shopindex = %s and shiftcreate >= '%s 00:00:00' and shiftcreate < '%s 23:59:59.999')";
	
	private CashEmulator(String serverIP, int shopNum, int cashNum) {
	    cashNumber = cashNum;
	    shopNumber = shopNum;
	    shiftNum = getCurrentShiftNum(cashNumber);
	    checkNumber =  getNextCheckNum(cashNumber, shiftNum);
	    docSender = new DocsSender(serverIP, shopNumber, cashNumber);
	    log.info("Создан cashEmulator: " + cashNumber +  "; ShiftNum = " + shiftNum + "; NextCheckNumber = " + checkNumber);
	} 
	
	/*
	 * Смотрим, есть ли созданный эмулятор (ip + номер магазина + номер кассы)
	 * если нет, создаем его
	 */
	public static CashEmulator getCashEmulator(String serverIP, int shopNum, int cashNum) {
	    String key = serverIP + String.valueOf(shopNum) + String.valueOf(cashNum);
	    if (!activeCashEmulators.containsKey(key)) {
	    	activeCashEmulators.put(key, new CashEmulator(serverIP, shopNum, cashNum));
	    }
	    return activeCashEmulators.get(key);
	}
	
	
	private int getCurrentShiftNum(int cashNumber) {
		// TODO: привести в порядок
		String date = getDate("yyyy-MM-dd", System.currentTimeMillis() - yesterday);
		shiftNum = db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_MAX_SHIFT_NUM, cashNumber, shopNumber, date, date));
		if (shiftNum == 0) {
			shiftNum = 1;
		} else {
			/* если смена уже создана
			 * проверяем, закрыта ли она 
			 */
			ifShiftClosed(cashNumber, shiftNum);
		}
		return shiftNum;
	}
	
	private int getNextCheckNum(int cashNumber, int shiftNumber) {
		// TODO: привести в порядок
		String date = getDate("yyyy-MM-dd", System.currentTimeMillis() - yesterday);
		checkNumber = db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_CHECK_NUM, cashNumber, shiftNumber, shopNumber, date, date));
		return ++checkNumber;
	}
	
	/*
	 * сумма продаж за смену
	 */
	private int getShiftSumChecks() {
		String date = getDate("yyyy-MM-dd", System.currentTimeMillis() - yesterday);
		return db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_GET_SHIFT_FINAL_SUM, String.valueOf("true"), cashNumber, shiftNum, shopNumber, date, date));
	}
	
	/*
	 * сумма возвратов за смену 
	 */
	private int getShiftSumChecksRefund() {
		String date = getDate("yyyy-MM-dd", System.currentTimeMillis() - yesterday);
		return db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_GET_SHIFT_FINAL_SUM, String.valueOf("false"), cashNumber, shiftNum, shopNumber, date, date));
	}

	private boolean ifShiftClosed(int cashNumber, int shiftNumber) {
		// TODO: привести в порядок
		String date = getDate("yyyy-MM-dd", System.currentTimeMillis() - yesterday);
		int querryResult;
		querryResult = db.queryForInt(DB_RETAIL_OPERDAY, String.format(SQL_SHIFT_STATUS, shiftNumber, cashNumber, shopNumber, date, date)); 
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
        } else if (document instanceof WithdrawalEntity) {
            type = DataTypesEnum.WITHDRAWAL_TYPE.code;
        } else if (document instanceof IntroductionEntity) {
            type = DataTypesEnum.INTRODUCTION_TYPE.code;
        } else {
            log.warn("Неизвестный тип документа: {}", document);
        }
		docSender.sendObject(type, document);
    }
	
	/*
	 * выполнить изъятие
	 */
	public DocumentEntity nextWithdrawal(){
		 if (shift == null || nextShift || ifShiftClosed(cashNumber, shiftNum)) {
		      shift = nextShift(null);
		      nextShift = false;
		    }
		
		WithdrawalEntity wdr = new WithdrawalEntity();
		wdr.setCurrency("RUB");
		wdr.setWasBefore((long) 0);
		wdr.setValue((long) 10000);
		wdr.setShift(shift);
		wdr.setFiscalDocNum("testWdr;" + String.valueOf(System.currentTimeMillis()));
		wdr.setNumber((long) checkNumber++);
		wdr.setId(new Date().getTime());
		wdr.setSession(shift.getSessionStart());
	    Date d = new Date(System.currentTimeMillis() - yesterday);
	    wdr.setDateCommit(d);
	    
		sendDocument(wdr);
		log.info("Выполнить изъятие..");
		ifCheckInRetail(wdr);
		return wdr;
	}
	
	/*
	 * выполнить внесение
	 */
	public DocumentEntity nextIntroduction(){
		 if (shift == null || nextShift || ifShiftClosed(cashNumber, shiftNum)) {
		      shift = nextShift(null);
		      nextShift = false;
		    }
		
		IntroductionEntity intr = new IntroductionEntity();
		intr.setCurrency("RUB");
		intr.setWasBefore((long) 10000);
		intr.setValue((long) 20000);
		intr.setShift(shift);
		intr.setFiscalDocNum("testIntr;" + String.valueOf(System.currentTimeMillis()));
		intr.setNumber((long) checkNumber++);
		intr.setId(new Date().getTime());
		intr.setSession(shift.getSessionStart());
	    Date d = new Date(System.currentTimeMillis() - yesterday);
	    intr.setDateCommit(d);

		sendDocument(intr);
		log.info("Выполнить внесение..");
		ifCheckInRetail(intr);
		return intr;
	}
	
	/*
	 * закрыть текущую смену
	 */
	public DocumentEntity nextZReport(){
		if (shift == null) {
		      shift = nextShift(null);
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
	    shift.setShiftClose(new Date(System.currentTimeMillis() - yesterday));
	    rse.setId(Long.valueOf(reportId++));
	    rse.setDateCommit(new Date(System.currentTimeMillis() - yesterday));

	    ReportPaymentTypeEntity reportPaymentTypeEntity = new ReportPaymentTypeEntity(rse.getId().longValue(), "CashPaymentEntity", 'P');
	    //reportPaymentTypeEntity.setPSumm(getShiftSum(false));
	    
	    List<ReportPaymentTypeEntity> listRPTE = new ArrayList<ReportPaymentTypeEntity>();
	    listRPTE.add(reportPaymentTypeEntity);
	    rse.setPaymentsTypesList(listRPTE);
	    rse.setShift(shift);
	    rse.setNumber((long) checkNumber);
	    rse.setSession(shift.getSessionStart());
	    
	    rse.setIncresentTotalStart(100L);
	    rse.setIncresentTotalFinish(200L);
	    
	    sendDocument(rse);
	    log.info("Отправить Z отчет..");
	    ifCheckInRetail((ReportShiftEntity) rse);
	    nextShift = true;
	    return rse;
	}
	
	/*
	 * Сгенерить чек с рандомным набором позиций
	 */
	public DocumentEntity nextPurchase() {

	    if (shift == null || nextShift || ifShiftClosed(cashNumber, shiftNum)) {
	      shift = nextShift(null);
	      nextShift = false;
	    }
	    
	    int idx = (int)random(peList.size() - 2) + 1;
	    return completeAndSendPurchase((DocumentEntity)peList.get(idx));
	    
	}
	
	/*
	 * Сгенерить чек с определенным набором позиций
	 */
	public DocumentEntity nextPurchase(PurchaseEntity purchase) {

	    if (shift == null || nextShift || ifShiftClosed(cashNumber, shiftNum)) {
	      shift = nextShift(null);
	      nextShift = false;
	    }

	    return completeAndSendPurchase((DocumentEntity)purchase);
	}
	
	/*
	 * сгенерить возврат позиций в чеке
	 */
	public DocumentEntity nextRefundPositions(
				PurchaseEntity superPurchase, 
				/*
				 * @key - номер позиции
				 * @value - возвращаемое количество
				 */
				HashMap<Long, Long> returnPositions,
				// произвольный возврат
				boolean arbitraryReturn) {
		
	    if (shift == null) {
	      shift = nextShift(null);
	    }
	    
	    DocumentEntity de = refundCheck(superPurchase, returnPositions, arbitraryReturn);
	    log.info("Выполнить возврат...");
	    return completeAndSendPurchase(de);
	}
	
	/*
	 * Вернуть весь чек 
	 */
	public DocumentEntity nextRefundAll(
				PurchaseEntity superPurchase, 
				// произвольный возврат
				boolean arbitraryReturn) 
	{
		
		DocumentEntity de = new PurchaseEntity();
		if (shift == null) {
		  shift = nextShift(null);
		}
		
		HashMap<Long, Long> returnPositions = new HashMap<Long, Long>();
		
		for (int i=0; i<superPurchase.getPositions().size(); i++){
			PositionEntity pe = superPurchase.getPositions().get(i);
			returnPositions.put(pe.getNumber(), pe.getQnty());
		}
		
		de = refundCheck(superPurchase, returnPositions, arbitraryReturn);
		log.info("Выполнить возврат всего чека..");
		return completeAndSendPurchase(de);
	}
	
	/*
     * заполнить возврат
     */
	public static DocumentEntity refundCheck( 
						PurchaseEntity superPurchase, 
						/*
						 * из superPurchase:
						 * @key - номер позиции
						 * @value - возвращаемое количество
						 */
						HashMap<Long, Long> returnPositions, 
						// является ли чек возврата произвольным (т.е не привязан к чеку продажи)
						boolean arbitraryReturn){
		
		 long summ = 0L;
		 long refundCheckPosition = 0;
		 Long returnQnty = 0L;
		 List<PositionEntity> positions = new ArrayList<PositionEntity>(returnPositions.size());
		 
		 PurchaseEntity returnPe = new PurchaseEntity();
		 returnPe.setCheckStatus(CheckStatus.Registered);
		 returnPe.setOperationType(Boolean.valueOf(true));
		 
		 for(Long checkPositionNumber:returnPositions.keySet()){
			 PositionEntity position =  new PositionEntity();
			 PositionEntity superPurchasePosition = null;
			 
			 /*
			  * берем позизию из superPurchase, 
			  * номер которой соответствует checkPositionNumber
			  */
			 List<PositionEntity> superPurchasePositions = superPurchase.getPositions();
			 for (int i=0; i<superPurchasePositions.size(); i++){
				 if (superPurchasePositions.get(i).getNumber() == checkPositionNumber) {
					 superPurchasePosition = superPurchasePositions.get(i);
				 }
			 }
			 returnQnty =  returnPositions.get(checkPositionNumber);
			 position.setProduct(superPurchasePosition.getProduct());
			 position.setNumber(++refundCheckPosition);
			 position.setNumberInOriginal(checkPositionNumber);
			 position.setPriceEnd(superPurchasePosition.getPriceEnd());
			 position.setDateTime(new Date(System.currentTimeMillis()));
			 position.setQnty(returnQnty);
			 position.setSum(Long.valueOf(returnQnty/1000 * position.getPriceEnd().longValue()));
			 position.setDeleted(Boolean.valueOf(false));
			 position.setSuccessProcessed(true);
			 position.setNumberInOriginal(superPurchasePosition.getNumber());
			 
			 summ += position.getSum().longValue();
			 positions.add(position);
		 }
		 
	     returnPe.setFiscalDocNum("test; refund" + String.valueOf(System.currentTimeMillis()));
	     returnPe.setPositions(positions);
	     returnPe.setReturn();
	     
	     List<PaymentEntity> paymentEntityList = new ArrayList<PaymentEntity>(1);
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
	
	private DocumentEntity completeAndSendPurchase(DocumentEntity de){
		Date d = new Date(System.currentTimeMillis() - yesterday);
	    de.setDateCommit(d);
	    de.setShift(shift);
	    de.setNumber((long) checkNumber++);
	    de.setSession(shift.getSessionStart());
	    de.setId(System.currentTimeMillis());
	    
	    if ((de instanceof PurchaseEntity)) {
	    	PurchaseEntity pe = (PurchaseEntity)de;
	      for (PositionEntity pos : pe.getPositions()) {
	        pos.setDateTime(d);
	      }
	      
	      /*
	       * Добавить данные о номере магазина, смены, кассы в транзакции оплаты чека
	       */
		    List<PaymentTransactionEntity> purchaseTransactions = new ArrayList<PaymentTransactionEntity>();
		    
		    Iterator<PaymentTransactionEntity> iterator = pe.getTransactions().iterator();
		    while (iterator.hasNext()){
		    	PaymentTransactionEntity pTransaction = iterator.next();
			    	pTransaction.setCashNum(shift.getCashNum());
			    	pTransaction.setShopIndex(shift.getShopIndex());
			    	pTransaction.setNumShift(shift.getNumShift());
			    	purchaseTransactions.add(pTransaction);
		    }
		    pe.setTransactions(purchaseTransactions);	      
	      
	    }  
	    sendDocument(de);
	    logCheckEntities((PurchaseEntity) de);
	    ifCheckInRetail((PurchaseEntity) de);
	    return de;
	}
	
    private ShiftEntity nextShift(SessionEntity session) {
      SessionEntity sess = session != null ? session : nextSession();
      ShiftEntity shift = new ShiftEntity();
      shift.setFiscalNum("Emulator." + shopNumber + "." + cashNumber);
      if (nextShift) {
    	  nextShift = false;
      }
      shift.setNumShift(Long.valueOf(++shiftNum));
      shift.setShiftOpen(new Date(System.currentTimeMillis() - yesterday));
      shift.setCashNum(new Long(cashNumber));
      shift.setShopIndex(Long.valueOf(shopNumber));
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
      // Сейчас, при настройке заводим только одного кассира с номером 1
      ue.setTabNum(String.valueOf(1L));
      ue.setSessions(new ArrayList<SessionEntity>());
      ue.getSessions().add(se);
      se.setUser(ue);
      return se;
    }
	
	public void useNextShift(){
		nextShift = true;
		checkNumber = 1;
	}
	
	private void logCheckEntities(PurchaseEntity pe){
		Iterator<PositionEntity> i = pe.getPositions().iterator();
		PositionEntity  poe;
		log.info("Номер смены: " + pe.getShift());
		log.info("Номер чека: " + pe.getNumber());
		while (i.hasNext()) {
			poe = (PositionEntity) i.next();
			log.info("Позиция N: " + poe.getNumber() + " - " + poe.getName() +  " - " +  poe.getQnty()/1000 + "; Баркод: "  + poe.getBarCode());
		}
	} 
	
	/*
	 *  проверить, что чек покупки зарегистрирован в od_purchase (ищем по fiscaldocnum)
	 */
	private boolean ifCheckInRetail(DocumentEntity purchase){
	    String fiscalDocNum =   purchase.getFiscalDocNum();
	    String dbRequest = "";
	    if (purchase instanceof PurchaseEntity) {
	    	dbRequest = SQL_GET_CHECK_BY_FISCALDOCNUM;
	    } else if (purchase instanceof ReportShiftEntity) {
	    	dbRequest = SQL_GET_REPORT_BY_FISCALDOCNUM;
	    } else if (purchase instanceof WithdrawalEntity) {
	    	dbRequest = SQL_GET_WITHDRAWAL_BY_FISCALDOCNUM;
	    }  else if (purchase instanceof IntroductionEntity) {
	    	dbRequest = SQL_GET_INTRODUCTION_BY_FISCALDOCNUM;
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
    	log.info(String.format("Check transport timeout! No check found with number:  %s and fiscalDocNum: %s ", purchase.getNumber(), fiscalDocNum));
		return false;
	}
	
	private static long random(int max) {
	    return Math.round(Math.random() * max);
	}
	
	private  String getDate(String format, long date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
}