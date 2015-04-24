package ru.crystals.set10.test;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.pos.bank.datastruct.AuthorizationData;
import ru.crystals.pos.bank.datastruct.BankCard;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.searchcheck.PaymentTransactionsPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.utils.PaymentEmulator;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.PurchaseGenerator;
import static ru.crystals.set10.pages.operday.searchcheck.PaymentTransactionsPage.LINK_SAVE_EXCEL;
import static ru.crystals.set10.pages.operday.OperDayPage.SEARCH_CHECK;

/*
 * TODO: дописать тесты
 * - проверка слипа
 * - проверка значка успешной/отклоненной транзакции
 * - дату
 * - время
 */

@Test (groups={"centrum", "retail"})
public class BankTransactionInCheckTest extends AbstractTest {
	
	MainPage mainPage;
	CheckSearchPage searchCheck;
	ReportConfigPage RefundChecksConfigPage;
	PurchaseEntity pe;
	CheckContentPage checkContent;
	PaymentTransactionsPage paymentTransactions;
	
	long checkNumber;
	String terminalId = "СА43299";
	
	private PurchaseEntity purchase;
	
	private long reportSizeNoLessThanBytes = 6000; 
	
	/*
	 * Поля для валидации отклоненной транзакций
	 */
	String inValidAuthorizationCode = String.valueOf(System.currentTimeMillis() + 99);
	String inValidMessage = "ОТКЛОНЕНО";
	String inValidResponseCode = "076";
	String inValidBankId = "Maestro";
	String prefix1 = String.valueOf(System.currentTimeMillis() + 99).substring(5);
	String inValidBankCardNumber = String.format("1234****%s", prefix1);
	long inValidResultCode = 577L;
	
	/*
	 * Поля для валидации успешной транзакций
	 */
	String validAuthorizationCode = String.valueOf(System.currentTimeMillis());
	String validMessage = "ОДОБРЕНО";
	String validResponseCode = "079";
	String validBankId = "ВТБ";
	String prefix = String.valueOf(System.currentTimeMillis()).substring(5);
	String validBankCardNumber = String.format("1234****%s", prefix);
	long validResultCode = 577L;
	
	@DataProvider (name = "inValidBankTransaction")
	private Object[][] setInValidTransactionData(){
		return new Object[][]{
				{"Код авторизации", PaymentTransactionsPage.LOCATOR_AUTHORIZATION_CODE, inValidAuthorizationCode},
				{"Сообщение", PaymentTransactionsPage.LOCATOR_MESSAGE, inValidMessage},
				{"Код ответа банка", PaymentTransactionsPage.LOCATOR_BANK_RESPONSE_CODE, inValidResponseCode},
				{"Банк", PaymentTransactionsPage.LOCATOR_BANK_ID, inValidBankId},
				{"Карта", PaymentTransactionsPage.LOCATOR_CARD_NUMBER, inValidBankCardNumber},
				{"Код ответа сервера", PaymentTransactionsPage.LOCATOR_SERVER_RESPONSE_CODE, String.valueOf(inValidResultCode)},
				{"Терминал", PaymentTransactionsPage.LOCATOR_TERMINAL, terminalId},
				//TODO: заменить String.replace
				//{"Запрошено", PaymentTransactionsPage.LOCATOR_AMOUNT_REQUESTED, String.valueOf(purchase.getCheckSumEndBigDecimal()).replace(".", ",")},
		};
	}
	
	@DataProvider (name = "validBankTransaction")
	private Object[][] setValidTransactionData(){
		return new Object[][]{
				{"Код авторизации", PaymentTransactionsPage.LOCATOR_AUTHORIZATION_CODE, validAuthorizationCode},
				{"Сообщение", PaymentTransactionsPage.LOCATOR_MESSAGE, validMessage},
				{"Код ответа банка", PaymentTransactionsPage.LOCATOR_BANK_RESPONSE_CODE, validResponseCode},
				{"Банк", PaymentTransactionsPage.LOCATOR_BANK_ID, validBankId},
				{"Карта", PaymentTransactionsPage.LOCATOR_CARD_NUMBER, validBankCardNumber},
				{"Код ответа сервера", PaymentTransactionsPage.LOCATOR_SERVER_RESPONSE_CODE, String.valueOf(validResultCode)},
				{"Терминал", PaymentTransactionsPage.LOCATOR_TERMINAL, terminalId},
				//TODO: заменить String.replace
				//{"Запрошено", PaymentTransactionsPage.LOCATOR_AMOUNT_REQUESTED, String.valueOf(purchase.   getCheckSumEndBigDecimal()).replace(".", ",")},
		};
	}

	@BeforeClass
	public void prepareData() {
		/*
		 *  послать чек, в котором присутствует отклоненная и пройденная банковские транзакции
		 *  и открыть его
		 */
		mainPage = new LoginPage(getDriver(), TARGET_HOST_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().navigatePage(CheckSearchPage.class, SEARCH_CHECK);
		
		purchase = (PurchaseEntity)cashEmulator.nextPurchase(setPayments());
		
		searchCheck.openFilter().setCheckBarcode(purchase);
		searchCheck.doSearch();
 		checkContent = searchCheck.selectFirstCheck();
 		paymentTransactions = checkContent.openPaymentTransactionsForm();
 		
	}	
	
	@Test (description = "SRTE-75. Просмотр в чеке информации о успешной банковской транзакции",
			dataProvider = "validBankTransaction")
	public void testValidBankTransactionExist(String fieldName, String fieldLocator, String fieldValue){
		Assert.assertEquals(paymentTransactions.getTransactionElementValue(fieldLocator, 2), 
				fieldName + ":" + fieldValue,
				"Не отображается/неверное значение поля для положительной банковской транзакции");
	}
	
	@Test (description = "SRTE-75. Просмотр в чеке информации о отклоненной банковской транзакции",
			dataProvider = "inValidBankTransaction")
	public void testInvalidBankTransactionExist(String fieldName, String fieldLocator, String fieldValue){
		Assert.assertEquals(paymentTransactions.getTransactionElementValue(fieldLocator, 1), 
				fieldName + ":" + fieldValue,
				"Не отображается/неверное значение поля для отклоненной банковской транзакции");
	}
	
	@Test(description = "SRTE-75. Выгрузка банковских транзакций в excel")
	public void saveExcelBankTransactionTest(){
		long fileSize = 0;
		String reportNamePattern = "TransactionHistory_*.xls";
		fileSize =  paymentTransactions.exportFileData(chromeDownloadPath, reportNamePattern, paymentTransactions, LINK_SAVE_EXCEL).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > reportSizeNoLessThanBytes, "Файл отчета сохранился некорректно");
	}
	
	
	private PurchaseEntity setPayments(){
		
		PaymentEmulator payments = new PaymentEmulator();
		PurchaseEntity purchase = PurchaseGenerator.getPurchaseWithoutPayments();
		
		AuthorizationData authDataInvalid = new AuthorizationData();
			authDataInvalid.setStatus(false);
			authDataInvalid.setBankid(inValidBankId);
			authDataInvalid.setAuthCode(inValidAuthorizationCode);
			authDataInvalid.setMessage(inValidMessage);
			authDataInvalid.setResponseCode(inValidResponseCode);
			authDataInvalid.setTerminalId(terminalId);
			authDataInvalid.setResultCode(inValidResultCode);

		AuthorizationData authData = new AuthorizationData();
			authData.setStatus(true);
			authData.setBankid(validBankId);
			authData.setAuthCode(validAuthorizationCode);
			authData.setMessage(validMessage);
			authData.setResponseCode(validResponseCode);
			authData.setTerminalId(terminalId);
			authData.setResultCode(validResultCode);
			
		BankCard invalidCard = payments.setBankCardData(inValidBankCardNumber, "Maestro");
		purchase = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase, purchase.getCheckSumEnd(), invalidCard, authDataInvalid);
		/*
		 * Задержка м/д генерацией отклоненной и одобренной транзакциями 
		 */
		DisinsectorTools.delay(500);
		BankCard card = payments.setBankCardData(validBankCardNumber, "Visa");
		purchase = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase, purchase.getCheckSumEnd(), card, authData);	
		
		return purchase;
	}
	
}
