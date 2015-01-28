package ru.crystals.set10.test.search;

import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.pos.bank.datastruct.AuthorizationData;
import ru.crystals.pos.bank.datastruct.BankCard;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.pos.payments.ChildrenCardPaymentEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.CashEmulatorPayments;
import ru.crystals.set10.utils.DisinsectorTools;


public class SearchCheckByPaymentGroupTest extends SearchCheckAbstractTest{
	
	long checkNumber;
	PurchaseEntity purchase1;
	PurchaseEntity purchase2;
	PurchaseEntity purchase3;
	PurchaseEntity purchase4;
	PurchaseEntity purchase5;
	PurchaseEntity purchase6;
	PurchaseEntity purchase7;
	PurchaseEntity purchase8;
	PurchaseEntity purchase9;
	PurchaseEntity purchase10;
	PurchaseEntity purchase11;
	
	String childCardNumber;
	String giftCardNumber;
	String bankCardNumber;
	String bonusCardNumber;
	String responseCode = String.valueOf(System.currentTimeMillis()).substring(4) + 11;
	long resultCode = DisinsectorTools.random(100) + 100;
	String authorizationCode = String.valueOf(System.currentTimeMillis());
	String bankId = Config.BANK_NAME_1;
	
	String terminalNumber = "MM" + String.valueOf(System.currentTimeMillis()).substring(5);
	
	@BeforeClass
	public void prepareData() {
		
		CashEmulatorPayments payments = new CashEmulatorPayments();
		
		/*
		 * Оплата банковской картой	
		 */
		// берем 8 символов из возвращаемых миллисекунд и подставляем в номер карты
		String prefix = String.valueOf(System.currentTimeMillis()).substring(5);
		bankCardNumber = String.format("1234****%s", prefix);
		purchase1 = payments.getPurchaseWithoutPayments();
		BankCard card = payments.setBankCardData(bankCardNumber, "Maestro");
		purchase1 = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase1, purchase1.getCheckSumEnd(), card, null);
		
		/*
		 * Оплата бонусной картой	
		 */
		bonusCardNumber =String.valueOf(System.currentTimeMillis());
		purchase2 = payments.getPurchaseWithoutPayments();
		purchase2 = payments.setBonusCardPayment(purchase2, purchase2.getCheckSumEnd(), bonusCardNumber);

		/*
		 * Оплата подарочной картой	
		 */
		giftCardNumber =String.valueOf(System.currentTimeMillis() + 99);
		purchase3 = payments.getPurchaseWithoutPayments();
		long cashSum = purchase3.getCheckSumEnd() - purchase3.getCheckSumEnd()/2;
		purchase3 = payments.setCashPayment(purchase3, cashSum);
		purchase3 = payments.setGiftCardPayment(purchase3, purchase3.getCheckSumEnd() - cashSum, giftCardNumber);
		
		/*
		 * Оплата детской картой	
		 */
		String prefixChild = String.valueOf(System.currentTimeMillis()).substring(5);
		childCardNumber = String.format("5678****%s", prefixChild);
		purchase4 = payments.getPurchaseWithoutPayments();
		long cashSum4 = purchase4.getCheckSumEnd() - purchase4.getCheckSumEnd()/2;
		purchase4 = payments.setCashPayment(purchase4, cashSum4);
		BankCard childrenCard = payments.setBankCardData(childCardNumber, "VISA");
		purchase4 = payments.setBankCardPayment(ChildrenCardPaymentEntity.class, purchase4, purchase4.getCheckSumEnd() - cashSum4, childrenCard, null);
		
		
		AuthorizationData authData = new AuthorizationData();
		authData.setStatus(false);
		authData.setBankid("ВТБ");
		authData.setAuthCode(String.valueOf(System.currentTimeMillis()));
		authData.setMessage("ОДОБРЕНО");
		authData.setResponseCode("587");
		authData.setTerminalId("AA854380");
		authData.setResultCode(354L);
		
		purchase5 = payments.getPurchaseWithoutPayments();
		purchase6 = payments.getPurchaseWithoutPayments();
		purchase7 = payments.getPurchaseWithoutPayments();
		purchase8 = payments.getPurchaseWithoutPayments();
		purchase9 = payments.getPurchaseWithoutPayments();
		purchase10 = payments.getPurchaseWithoutPayments();
		purchase11 = payments.getPurchaseWithoutPayments();
		
		//возьмем карту из оплаты банковской карты
		
		//проверка кода авторизации
		authData.setAuthCode(authorizationCode);
		purchase5 = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase5, purchase5.getCheckSumEnd(), card, authData);
		purchase5 = payments.setCashPayment(purchase5, purchase5.getCheckSumEnd());
		
		//проверка номера терминала
		authData.setTerminalId(terminalNumber);
		purchase6 = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase6, purchase6.getCheckSumEnd(), card, authData);
		purchase6 = payments.setCashPayment(purchase6, purchase6.getCheckSumEnd());
		
		//провека кода ответа сервера
		authData.setResponseCode(responseCode);
		purchase7 = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase7, purchase7.getCheckSumEnd(), card, authData);
		purchase7 = payments.setCashPayment(purchase7, purchase7.getCheckSumEnd());
		
		//провека кода ответа процессингового центра
		authData.setResultCode(resultCode);
		purchase8 = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase8, purchase8.getCheckSumEnd(), card, authData);
		purchase8 = payments.setCashPayment(purchase8, purchase8.getCheckSumEnd());
		
		//провека поиска по номеру банковской карты в отклоненной транзакции
		purchase9 = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase9, purchase9.getCheckSumEnd(), card, authData);
		purchase9 = payments.setCashPayment(purchase9, purchase9.getCheckSumEnd());
		
		//провека поиска по номеру десткой карты в отклоненной транзакции
		purchase10 = payments.setBankCardPayment(ChildrenCardPaymentEntity.class, purchase10, purchase10.getCheckSumEnd(), childrenCard, authData);
		purchase10 = payments.setCashPayment(purchase10, purchase10.getCheckSumEnd());
		
		//провека кода банка (название банка)
		authData.setBankid(bankId);
		purchase11 = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase11, purchase11.getCheckSumEnd(), card, authData);
		purchase11 = payments.setCashPayment(purchase11, purchase11.getCheckSumEnd());
		
	}	
	
	@DataProvider (name = "Коды ответа")
	public Object[][] setPurchaseTransactionData(){
		return new Object[][]{
				{FILTER_CATEGORY_AUTHORIZATION_CODE, authorizationCode, purchase5},
				{FILTER_CATEGORY_TERMINAL_NUMBER, terminalNumber, purchase6},
				{FILTER_CATEGORY_SERVER_RESPONSE_CODE, responseCode, purchase7},
				{FILTER_CATEGORY_BANK_RESPONSE_CODE, String.valueOf(resultCode), purchase8},
		};
	}
	
	@DataProvider (name = "Карты оплаты")
	public Object[][] setCardPaymentsData(){
		return new Object[][]{
				{FILTER_CATEGORY_BANK_CARD_NUMBER, bankCardNumber, purchase1},
				{FILTER_CATEGORY_BONUS_CARD_NUMBER, bonusCardNumber, purchase2},
				{FILTER_CATEGORY_GIFT_CARD_NUMBER, giftCardNumber, purchase3},
				{FILTER_CATEGORY_CHILD_CARD_NUMBER, childCardNumber, purchase4},
		};
	}
	
	@DataProvider (name = "Карты оплаты. Отклоненные транзакции")
	public Object[][] setCardPaymentsDataWithRefusedTransactions(){
		return new Object[][]{
				{FILTER_CATEGORY_BANK_CARD_NUMBER, bankCardNumber, purchase9},
				{FILTER_CATEGORY_CHILD_CARD_NUMBER, childCardNumber, purchase10},
		};
	}
	
	@Test (description = "Поиск чекa по дополнительным параметрам оплаты. В чеке есть 1 отклоненная банковская транзакция и оплата наличными",
			dataProvider = "Коды ответа")
	public void testSearchByCodesNumber(String filter, String parameter, PurchaseEntity purchase){
 		/*
 		 *  поиск чека с заданным условием
 		 *  и фиксирование результата поиска
 		 */
		searchCheck.setFilterText(filter, String.valueOf(parameter)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		/*
 		 * Отправить чек purchase с оплатой по карте cardNumber
 		 */
 		sendCheck(purchase);
 		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
		
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
	@Test (description = "SRTE-76 (SRTE-73, SRTE-74). Поиск чека по карте оплаты. В чеке содержится только 1 транзакция оплаты со статусом true",
			dataProvider = "Карты оплаты")
	public void testSearchByPayCardNumber(String filter, String cardNumber, PurchaseEntity purchase){

		searchCheck.setFilterText(filter, String.valueOf(cardNumber)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		/*
 		 * Отправить чек purchase с оплатой по карте cardNumber
 		 */
 		sendCheck(purchase);
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
 		
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
	@Test (description = "SRTE-73. Поиск чека по банковской/детской карте в отклоненных транзакциях",
			dataProvider = "Карты оплаты. Отклоненные транзакции")
	public void testSearchByPayCardNumberWithRefusedTransaction(String filter, String cardNumber, PurchaseEntity purchase){

		searchCheck.setFilterText(filter, String.valueOf(cardNumber)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		/*
 		 * Отправить чек purchase с оплатой по карте cardNumber
 		 */
 		sendCheck(purchase);
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
	@Test (enabled = false, description = "SRTE-73. Поиск чека по коду банка")
	public void testSearchByBankId(){
		searchCheck.setFilterSelect(FILTER_CATEGORY_BANK_ID, bankId).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		/*
 		 * Отправить чек purchase с оплатой по карте cardNumber
 		 */
 		sendCheck(purchase11);
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
}
