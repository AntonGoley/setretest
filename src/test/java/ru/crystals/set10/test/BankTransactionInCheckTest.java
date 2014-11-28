package ru.crystals.set10.test;

import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.EXCELREPORT;
import static ru.crystals.set10.utils.GoodsParser.peList;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.pos.bank.datastruct.AuthorizationData;
import ru.crystals.pos.bank.datastruct.BankCard;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.pos.payments.BankCardPaymentTransactionEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import ru.crystals.pos.payments.PaymentEntity;
import ru.crystals.pos.payments.PaymentTransactionEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.searchcheck.PaymentTransactionsPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;


public class BankTransactionInCheckTest extends AbstractTest{
	
	MainPage mainPage;
	CheckSearchPage searchCheck;
	ReportConfigPage RefundChecksConfigPage;
	PurchaseEntity pe;
	CheckContentPage checkContent;
	PaymentTransactionsPage paymentTransactions;
	
	long checkNumber;
	
	/*
	 * Поля для валидации успешной транзакций
	 */
	
	String validAuthorization = String.valueOf(System.currentTimeMillis());
	String validMessage = "ОДОБРЕНО";
	String validRsponseCode = "076";
	
	@DataProvider (name = "validBankTransaction")
	public Object[][] setValidTransactionData(){
		return new Object[][]{
				{"Код авторизации", validAuthorization},
				{"Сообщение", validMessage},
				{"Код ответа", validRsponseCode},
		};
	}
	
	/*
	 * Поля для валидации отклоненной транзакций
	 */
	
	String inValidAuthorization = String.valueOf(System.currentTimeMillis() + 100);
	String inValidMessage = "ОТКАЗАНО";
	String inValidRsponseCode = "075";
	
	@DataProvider (name = "inValidBankTransaction")
	public Object[][] setInValidTransactionData(){
		return new Object[][]{
				{"Код авторизации", inValidAuthorization},
				{"Сообщение", inValidMessage},
				{"Код ответа", inValidRsponseCode},
		};
	}
	
	@BeforeClass
	public void prepareData() {
		/*
		 *  послать чек, в котором присутствует банковская транзакция
		 *  и открыть его
		 */
		checkNumber = cashEmulator.nextPurchase(setPayments()).getNumber();
		
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().openCheckSearch();
		
 		searchCheck.setCheckNumber(String.valueOf(checkNumber)).doSearch();
 		checkContent = searchCheck.selectFirstCheck();
 		paymentTransactions = checkContent.openPaymentTransactionsForm();
 		
	}	
	
	@Test (description = "SRTE-75. Просмотр в чеке информации о успешной банковской транзакции",
			dataProvider = "validBankTransaction")
	public void testValidBankTransactionExist(String field, String fieldValue){
		Assert.assertTrue("Не отображается значения поля для банковской транзакции", paymentTransactions.validateData(fieldValue));
	}
	
	@Test (description = "SRTE-75. Просмотр в чеке информации о отклоненной банковской транзакции",
			dataProvider = "inValidBankTransaction")
	public void testInvalidBankTransactionExist(String field, String fieldValue){
		Assert.assertTrue("Не отображается значения поля для банковской транзакции", paymentTransactions.validateData(fieldValue));
	}
	
	
	@Test(description = "SRTE-75. Выгрузка банковских транзакций в excel")
	public void saveExcelBankTransactionTest(){
		long fileSize = 0;
		String reportNamePattern = "TransactionHistory*.xls*";
		fileSize =  paymentTransactions.saveExcel(chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue("Файл отчета сохранился некорректно", fileSize > 0);
	}
	
	
	private PurchaseEntity setPayments(){
		/*
		 *  получить ранее сгенеренный чек у которого изменим количество оплат
		 */
		int idx = (int)random(peList.size() - 2) + 1;
	    PurchaseEntity peWithBankTransactions = (PurchaseEntity) peList.get(idx);
	    
	    CashPaymentEntity originalPayment = (CashPaymentEntity) peWithBankTransactions.getPayments().get(0);
	    long originalSumm = originalPayment.getSumPay();
	    
	    /*
	     * Оплата наличными: меняем сумму оплаты originalSumm/2
	     */
	    CashPaymentEntity payE = new CashPaymentEntity();
	    payE.setDateCreate(new Date(System.currentTimeMillis()));
	      payE.setDateCommit(new Date(System.currentTimeMillis()));
	      payE.setSumPay(Long.valueOf(originalSumm/2));
	      payE.setChange(Long.valueOf(random(1000) * 11L));
	      payE.setPaymentType("CashPaymentEntity");
	      payE.setCurrency("RUB");
	    
	    /*
	     * Оплата банковской картой  Сбербанк
	     */
	    BankCardPaymentEntity payBank = new BankCardPaymentEntity();
		  payBank.setDateCreate(new Date(System.currentTimeMillis()));
		  payBank.setDateCommit(new Date(System.currentTimeMillis()));
		  payBank.setPaymentType("BankCardPaymentEntity");
		  payBank.setBankid("Сбербанк");
		  payBank.setSumPay(originalSumm - originalSumm/2);
		  payBank.setCurrency("RUB");
		 
		 /*
		  * Отклоненная транзакция Сбербанк
		  */
	 	AuthorizationData authDataDenied = new  AuthorizationData();
		 	authDataDenied.setAmount(Long.valueOf(originalSumm - originalSumm/2));
		 	authDataDenied.setCurrencyCode("RUB");
		 	authDataDenied.setDate(new Date(System.currentTimeMillis()));
		 	authDataDenied.setHostTransId(System.currentTimeMillis());
		 	authDataDenied.setCashTransId(System.currentTimeMillis() + 1);
			 		BankCard card = new BankCard();
			 			card.setCardNumber("240899******0123");
			 			card.setCardType("VISA");
			 			card.setExpiryDate(new Date(System.currentTimeMillis() + 365*24*3600*1000));
		 	authDataDenied.setCard(card);		
		 	authDataDenied.setStatus(false);
		 	authDataDenied.setBankid("Сбербанк");
		 	authDataDenied.setAuthCode(inValidAuthorization);
		 	authDataDenied.setMessage(inValidMessage);
		 	authDataDenied.setResponseCode(inValidRsponseCode);
		 	authDataDenied.setResultCode(1L);
		 	authDataDenied.setTerminalId("MM489301");
			 	List<List<String>> slips_ = new ArrayList<List<String>>();
			 		List<String> slip_ = new ArrayList<String>();
			 		slip_.add("Простой слип \n отклоненной транзакции");
			 		slips_.add(slip_);
			 	authDataDenied.setSlips(slips_);	
		  
		 /*
		  * Успешная транзакция Сбербанк
		  */
		 AuthorizationData authData = new  AuthorizationData();
		 	authData.setAmount(Long.valueOf(originalSumm - originalSumm/2));
		 	authData.setCurrencyCode("RUB");
		 	authData.setDate(new Date(System.currentTimeMillis() + 120*1000));
		 	authData.setHostTransId(System.currentTimeMillis());
		 	authData.setCashTransId(System.currentTimeMillis() + 1);
		 	/*
		 	 *  используем ту же карту, что и 
		 	 *  при неуспешной транзакции
		 	 */
		 	authData.setCard(card);		
		 	authData.setStatus(true);
		 	authData.setBankid("Сбербанк");
		 	authData.setAuthCode(validAuthorization);
		 	authData.setMessage(validMessage);
		 	authData.setResponseCode(validRsponseCode);
		 	authData.setResultCode(1L);
		 	authData.setTerminalId("MM489301");
		 	List<List<String>> slips = new ArrayList<List<String>>();
		 		List<String> slip = new ArrayList<String>();
		 		slip.add("Простой слип \n успешной транзакции");
		 		slips.add(slip);
		 	authData.setSlips(slips);
		 
		
		BankCardPaymentTransactionEntity canceledBankTransaction = new  BankCardPaymentTransactionEntity(authDataDenied);
		BankCardPaymentTransactionEntity successBankTransaction = new  BankCardPaymentTransactionEntity(authData);
		
		List<PaymentTransactionEntity> allTransactions = new ArrayList<PaymentTransactionEntity>();
		allTransactions.add(canceledBankTransaction);
		allTransactions.add(successBankTransaction);
		
		List<PaymentTransactionEntity> successTransactions = new ArrayList<PaymentTransactionEntity>();
		successTransactions.add(successBankTransaction);
		/*
		 * связываем успешную транзакцию с оплатой (банковской картой) и с чеком
		 * отмененная транзакция добавляется только в чек	
		*/
		payBank.setTransactions(successTransactions);

		/*
		 * Создаем 2 оплаты: наличные + банковская карта и добавляем в чек
		*/
		List<PaymentEntity> paymentEntityList = new ArrayList<PaymentEntity>(2);
		paymentEntityList.add(payE);
		paymentEntityList.add(payBank);
		peWithBankTransactions.setPayments(paymentEntityList);
		
		peWithBankTransactions.setTransactions(allTransactions);
		return peWithBankTransactions;
	}
	
	private static long random(int max) {
	    return Math.round(Math.random() * max);
	}
}
