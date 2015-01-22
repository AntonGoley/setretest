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
import ru.crystals.set10.utils.CashEmulatorPayments;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.searchcheck.PaymentTransactionsPage.LINK_SAVE_EXCEL;

public class BankTransactionInCheckTest extends AbstractTest {

	public static final String LABEL_CLASS_LOCATOR = "className:Label";

	MainPage mainPage;
	CheckSearchPage searchCheck;
	ReportConfigPage RefundChecksConfigPage;
	PurchaseEntity pe;
	CheckContentPage checkContent;
	PaymentTransactionsPage paymentTransactions;
	
	long checkNumber;
	String terminalId = "СА43299";
	
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
	
	CashEmulatorPayments payments = new CashEmulatorPayments();
	
	@DataProvider (name = "inValidBankTransaction")
	public Object[][] setInValidTransactionData(){
		return new Object[][]{
				{"Код авторизации", getTransactionElementLabelLocator("authorizationCodeRow", inValidAuthorizationCode)},
				{"Сообщение", getTransactionElementLabelLocator("messageRow", inValidMessage)},
				{"Код ответа", getTransactionElementLabelLocator("bankResponseCodeRow", inValidResponseCode)},
				{"Код банка", getTransactionElementLabelLocator("bankRow", inValidBankId)},
				{"Номер карты", getTransactionElementLabelLocator("cardRow", inValidBankCardNumber)},
				{"Код ответа сервера", getTransactionElementLabelLocator("serverResponseCodeRow", String.valueOf(inValidResultCode))},
		};
	}
	
	@DataProvider (name = "validBankTransaction")
	public Object[][] setValidTransactionData(){
		return new Object[][]{
				{"Код авторизации", getTransactionElementLabelLocator("authorizationCodeRow", validAuthorizationCode)},
				{"Сообщение", getTransactionElementLabelLocator("messageRow", validMessage)},
				{"Код ответа", getTransactionElementLabelLocator("bankResponseCodeRow", validResponseCode)},
				{"Код банка", getTransactionElementLabelLocator("bankRow", validBankId)},
				{"Номер карты", getTransactionElementLabelLocator("cardRow", validBankCardNumber)},
				{"Код ответа сервера", getTransactionElementLabelLocator("serverResponseCodeRow", String.valueOf(validResultCode))}
		};
	}

	@BeforeClass
	public void prepareData() {
		/*
		 *  послать чек, в котором присутствует отклоненная и пройденная банковские транзакции
		 *  и открыть его
		 */
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().openCheckSearch();
		
 		searchCheck.openFilter().setCheckBarcode((PurchaseEntity)cashEmulator.nextPurchase(setPayments())).doSearch();
 		checkContent = searchCheck.selectFirstCheck();
 		paymentTransactions = checkContent.openPaymentTransactionsForm();
 		
	}	
	
	@Test (description = "SRTE-75. Просмотр в чеке информации о успешной банковской транзакции",
			dataProvider = "validBankTransaction")
	public void testValidBankTransactionExist(String field, String fieldValue){
		Assert.assertTrue(paymentTransactions.validateData(fieldValue), "Не отображается значения поля для банковской транзакции");
	}
	
	@Test (description = "SRTE-75. Просмотр в чеке информации о отклоненной банковской транзакции",
			dataProvider = "inValidBankTransaction")
	public void testInvalidBankTransactionExist(String field, String fieldValue){
		Assert.assertTrue(paymentTransactions.validateData(fieldValue), "Не отображается значения поля для банковской транзакции");
	}
	
	@Test(description = "SRTE-75. Выгрузка банковских транзакций в excel")
	public void saveExcelBankTransactionTest(){
		long fileSize = 0;
		String reportNamePattern = "TransactionHistory_*.xls";
		fileSize =  paymentTransactions.exportFileData(chromeDownloadPath, reportNamePattern, paymentTransactions, LINK_SAVE_EXCEL).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 6300, "Файл отчета сохранился некорректно");
	}
	
	
	private PurchaseEntity setPayments(){
		
		PurchaseEntity purchase = payments.getPurchaseWithoutPayments();
		
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

	private String getTransactionElementLabelLocator(String parentID, String text) {
		return "id:" + parentID + "/text:" + text + ";" + LABEL_CLASS_LOCATOR;
	}
	
}
