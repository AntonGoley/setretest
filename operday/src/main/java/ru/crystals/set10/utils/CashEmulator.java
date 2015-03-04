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

import ru.crystals.discount.processing.entity.LoyTransactionEntity;
import ru.crystals.pos.check.CashOnlineMessage;
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
import ru.crystals.set10.config.Config;
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
	private String db_operday;
	
	public  boolean nextShift = false;
	public  long yesterday = Long.valueOf("0")*6; //(86400000 * 130); ("-11232000000")
	
	/*
	 * Список созданных в тестах касс
	 */
	private static HashMap<String, CashEmulator> activeCashEmulators = new HashMap<String, CashEmulator>();
	
	private int reportId = 1;
	
	private DocsSender docSender;
	private LoySender loySender;
	
	private static DbAdapter db = new  DbAdapter();
	
	private static final String SQL_MAX_SHIFT_NUM = "select max(numshift) from od_shift as od_s where cashnum = %s  and shopindex = %s and shiftcreate >= '%s 00:00:00' and shiftcreate < '%s 23:59:59.999'";
	//private static final String SQL_MAX_SHIFT_NUM = "select max(numshift) from od_shift as od_s join od_purchase as od_p on od_p.id_shift = od_s.id where cashnum = %s  and shopindex = %s and shiftcreate >= '%s 00:00:00' and shiftcreate < '%s 23:59:59.999'";
	
	//private static final String SQL_SHIFT_STATUS = "select distinct(state) from od_shift where numshift = %s and cashnum = %s and shopindex = %s and shiftcreate >= '%s 00:00:00' and shiftcreate < '%s 23:59:59.999'";
	
	private static final String SQL_CHECK_NUM = "select max(numberfield) from od_shift as od_s join od_purchase as od_p on od_p.id_shift = od_s.id where cashnum = %s and numshift = %s and shopindex = %s and shiftcreate >= '%s 00:00:00' and shiftcreate < '%s 23:59:59.999'";
	
	private static  String SQL_GET_CHECK_BY_FISCALDOCNUM = "select count(*) from od_purchase where fiscaldocnum = '%s' ";
	
	private static  String SQL_GET_REPORT_BY_FISCALDOCNUM = "select count(*) from od_reportshift where fiscaldocnum = '%s' ";	
	
	private static  String SQL_GET_WITHDRAWAL_BY_FISCALDOCNUM = "select count(*) from od_withdrawal where fiscaldocnum = '%s' ";	
	
	private static  String SQL_GET_INTRODUCTION_BY_FISCALDOCNUM = "select count(*) from od_introduction where fiscaldocnum = '%s' ";	
	
	private static  String SQL_GET_SHIFT_FINAL_SUM = "select sum(checksumstart) from od_purchase where operationtype = %s  and checkstatus=0 and " +
													"id_shift = (select id from od_shift where cashnum=%s and numshift=%s and shopindex = %s and shiftcreate >= '%s 00:00:00' and shiftcreate < '%s 23:59:59.999')";
	
	private CashEmulator(String serverIP, int shopNum, int cashNum) {
	    cashNumber = cashNum;
	    shopNumber = shopNum;
	    /*
	     * Если индекс магазина не Ритейл, то
	     * это виртуальный магазин (смотрим в бд центрума)
	     */
	    if (String.valueOf(shopNumber).equals(Config.SHOP_NUMBER)) {
	    	db_operday = DB_RETAIL_OPERDAY;
	    } else {
	    	db_operday = DB_CENTRUM_OPERDAY;
	    }
	    docSender = new DocsSender(serverIP, shopNumber, cashNumber);
	    shiftNum = getCurrentShiftNum(cashNumber);
	    checkNumber =  getNextCheckNum(cashNumber, shiftNum);
	    loySender = new LoySender(serverIP, shopNumber, cashNumber);
	    log.info("Создан cashEmulator: " + cashNumber +  "; ShopNum = " + shopNum + "; ShiftNum = " + shiftNum + "; NextCheckNumber = " + checkNumber);
	    
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
		shiftNum = db.queryForInt(db_operday, String.format(SQL_MAX_SHIFT_NUM, cashNumber, shopNumber, date, date));
		if (shiftNum == 0) {
			shiftNum = 1;
		}	
		nextShift(null);	
		return shiftNum;
	}
	
	private int getNextCheckNum(int cashNumber, int shiftNumber) {
		// TODO: привести в порядок
		String date = getDate("yyyy-MM-dd", System.currentTimeMillis() - yesterday);
		checkNumber = db.queryForInt(db_operday, String.format(SQL_CHECK_NUM, cashNumber, shiftNumber, shopNumber, date, date));
		return ++checkNumber;
	}
	
	/*
	 * сумма продаж за смену
	 */
	private int getShiftSumChecks() {
		String date = getDate("yyyy-MM-dd", System.currentTimeMillis() - yesterday);
		return db.queryForInt(db_operday, String.format(SQL_GET_SHIFT_FINAL_SUM, String.valueOf("true"), cashNumber, shiftNum, shopNumber, date, date));
	}
	
	/*
	 * сумма возвратов за смену 
	 */
	private int getShiftSumChecksRefund() {
		String date = getDate("yyyy-MM-dd", System.currentTimeMillis() - yesterday);
		return db.queryForInt(db_operday, String.format(SQL_GET_SHIFT_FINAL_SUM, String.valueOf("false"), cashNumber, shiftNum, shopNumber, date, date));
	}

//	private boolean ifShiftClosed(int cashNumber, int shiftNumber) {
//		// TODO: привести в порядок
//		String date = getDate("yyyy-MM-dd", System.currentTimeMillis() - yesterday);
//		int querryResult = 0;
//		querryResult = db.queryForInt(db_operday, String.format(SQL_SHIFT_STATUS, shiftNumber, cashNumber, shopNumber, date, date)); 
//		if ((int) querryResult == 0) { 
//			return false;
//		} else {
//			useNextShift();
//			return true;
//		}
//	}
	
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
	
	
	private void openShiftOnFirstDocument(){
		if (shift.getShiftOpen() == null) {
			shift.setShiftOpen(new Date(System.currentTimeMillis() - yesterday));
		}
	}
	
	/*
	 * выполнить изъятие
	 */
	public DocumentEntity nextWithdrawal(){

		openShiftOnFirstDocument();
		 
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
		
		openShiftOnFirstDocument();
		
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
		
		//Date dateClose = new Date(System.currentTimeMillis() - yesterday);
		Date dateClose = new Date(System.currentTimeMillis());
		
		openShiftOnFirstDocument();
		
	    ReportShiftEntity rse = new ReportShiftEntity();
	    rse.setReportZ(true);
	    rse.setCountPurchase(Long.valueOf(1L));
	    rse.setSumCashEnd(Long.valueOf(285177L));
	    rse.setFiscalDocNum("testZ;" + String.valueOf(System.currentTimeMillis()));
	    // сумма чеков продажи за смену в ФР
	    rse.setSumPurchaseFiscal(Long.valueOf(getShiftSumChecks()));
	    // сумма возвратов по ФР
	    rse.setSumReturnFiscal((long) getShiftSumChecksRefund());
	    shift.setShiftClose(dateClose);
	    rse.setId(Long.valueOf(reportId++));
	    rse.setDateCommit(dateClose);

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
	    useNextShift();
	    return rse;
	}
	
	
	/*
	 * Сгенерить чек с рандомным набором позиций,
	 * заполнить данные о смене
	 * и не отправлять на сервер
	 */
	public PurchaseEntity nextPurchaseWithoutSending() {

		openShiftOnFirstDocument();
		
	   	log.info("Сгенерить чек для транзакций лояльности"); 
	   	int idx = (int)random(peList.size() - 2) + 1;
	    return (PurchaseEntity)completeDocument(peList.get(idx));
	}
	
	/*
	 * Сгенерить чек с рандомным набором позиций,
	 * заполнить данные о смене
	 * и не отправлять на сервер
	 */
	public PurchaseEntity nextRefundAllWithoutSending(
			PurchaseEntity superPurchase, 
			// произвольный возврат
			boolean arbitraryReturn) {

		openShiftOnFirstDocument();
		
		HashMap<Long, Long> returnPositions = new HashMap<Long, Long>();
		
		for (int i=0; i<superPurchase.getPositions().size(); i++){
			PositionEntity pe = superPurchase.getPositions().get(i);
			returnPositions.put(pe.getNumber(), pe.getQnty());
		}
		return  (PurchaseEntity)refundCheck(superPurchase, returnPositions, arbitraryReturn);
	}
	
	public PurchaseEntity nextCancelledPurchaseWithoutSending(PurchaseEntity purchase) {
		/*
		 * Уменьшаем нумерацию на 1, т.к будет нарушение последовательности
		 * нумерации чека (т.к purchase отменен)
		 */
		checkNumber--;
		openShiftOnFirstDocument();
	    purchase.setCheckStatus(CheckStatus.Cancelled);
	    return  purchase;
	}
	
	/*
	 * Сгенерить чек с рандомным набором позиций
	 */
	public DocumentEntity nextPurchase() {
		openShiftOnFirstDocument();
	   	int idx = (int)random(peList.size() - 2) + 1;
	   	log.info("Отправить  чек..");
	   	return completeAndSendPurchase((DocumentEntity)peList.get(idx));
	}
	
	/*
	 * Отрпавить существующий чек
	 */
	public DocumentEntity nextPurchase(DocumentEntity purchase) {

		openShiftOnFirstDocument();
		
	    log.info("Отправить  чек..");
	    return completeAndSendPurchase((DocumentEntity)purchase);
	}
	
	/*
	 * Отрпавить аннулированый чек
	 */
	public DocumentEntity nextCancelledPurchase(PurchaseEntity purchase) {
		log.info("Отправить аннулированный чек..");
	    return (DocumentEntity)completeAndSendPurchase(nextCancelledPurchaseWithoutSending(purchase));
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

		openShiftOnFirstDocument();
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
		DocumentEntity de = 
				(DocumentEntity) nextRefundAllWithoutSending(superPurchase, arbitraryReturn);
		log.info("Выполнить возврат всего чека..");
		return completeAndSendPurchase(de);
	}
	
	/*
     * заполнить возврат
     */
	private static DocumentEntity refundCheck( 
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
	
	public void sendLoy(LoyTransactionEntity loy, PurchaseEntity pe){
		pe.setDiscountValueTotal(loy.getDiscountValueTotal());
		loySender.sendLoyTransaction(loy, pe);
	}
	
	private DocumentEntity completeAndSendPurchase(DocumentEntity de){
		de = completeDocument(de);
		sendDocument(de);
		logCheckEntities((PurchaseEntity) de);
		ifCheckInRetail((PurchaseEntity) de);
		return de;
	}
	
	/*
	 * В документ добваляется информация о смене;
	 * в транзакции оплаты добавляется информация о смене
	 */
	private DocumentEntity completeDocument(DocumentEntity de){
		//Date d = new Date(System.currentTimeMillis() - yesterday);
		Date d = new Date(System.currentTimeMillis());
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
		    List<PaymentTransactionEntity> paymentTransactions = new ArrayList<PaymentTransactionEntity>();
		    
		    Iterator<PaymentTransactionEntity> iterator = pe.getTransactions().iterator();
		    while (iterator.hasNext()){
		    	PaymentTransactionEntity pTransaction = iterator.next();
			    	pTransaction.setCashNum(shift.getCashNum());
			    	pTransaction.setShopIndex(shift.getShopIndex());
			    	pTransaction.setNumShift(shift.getNumShift());
			    	paymentTransactions.add(pTransaction);
		    }
		    pe.setTransactions(paymentTransactions);
	    }  
	    return de;
	}
	
	/*
	 * Метод используется для отправки чеков с заполненной информацией о скидках
	 */
	public DocumentEntity sendPurchase(PurchaseEntity de){
		 sendDocument(de);
		 logCheckEntities((PurchaseEntity) de);
		 ifCheckInRetail((PurchaseEntity) de);
		 return de;
	}
	
    private ShiftEntity nextShift(SessionEntity session) {
      SessionEntity sess = session != null ? session : nextSession();
      shift = new ShiftEntity();
      shift.setFiscalNum("Emulator." + shopNumber + "." + cashNumber);

      shift.setNumShift(Long.valueOf(++shiftNum));
      shift.setShiftCreate(new Date(System.currentTimeMillis() - yesterday));
      shift.setCashNum(new Long(cashNumber));
      shift.setShopIndex(Long.valueOf(shopNumber));
      shift.setSessionStart(sess);
      sendCashMessage();
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
      ue.setTabNum(String.valueOf(2L));
      ue.setSessions(new ArrayList<SessionEntity>());
      ue.getSessions().add(se);
      se.setUser(ue);
      return se;
    }
	
	public void useNextShift(){
		//nextShift = true;
		checkNumber = 1;
		nextShift(null);
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
	    
    	// ждем в течение 30 секунд
	    int timeOut = 60;
    	int tryCount = 0;
    	while (tryCount < timeOut) {
    		tryCount++;
    		DisinsectorTools.delay(500);
    		if (db.queryForInt(db_operday, String.format(dbRequest, fiscalDocNum)) == 1) {
    			log.info(String.format("Чек зарегистрирован в операционном дне; fiscalDocNum: %s ", fiscalDocNum));
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

	public ShiftEntity getShift(){
		return this.shift;
	}
	
	public void sendCashMessage(){
		CashOnlineMessage message = new CashOnlineMessage();
		docSender.sendObject(DataTypesEnum.CASHONLINE_TYPE.code, message);
	}
	
}
