package ru.crystals.set10.test.search;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.pos.bank.datastruct.AuthorizationData;
import ru.crystals.pos.bank.datastruct.BankCard;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.set10.pages.operday.searchcheck.TransactionSearchPage;
import ru.crystals.set10.utils.CashEmulatorPayments;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;
import static ru.crystals.set10.pages.operday.searchcheck.TransactionSearchPage.*;

@Test (groups={"centrum", "retail"})
public class SearchTransactionsTest extends SearchCheckAbstractTest{
	
	TransactionSearchPage transactions;
	
	CashEmulatorPayments payments = new CashEmulatorPayments();
	
	BankCard card;
	AuthorizationData validAuth;
	AuthorizationData invalidAuth;
	
	
	@BeforeClass
	public void navigateToTransactionsPage(){
		super.openSearchPage();
		transactions = searchCheck.navigatePage(TransactionSearchPage.class, SEARCH_TRANSACTIONS);
		transactions.openFilter();
		
		purchase = payments.getPurchaseWithoutPayments();
		String prefix = String.valueOf(System.currentTimeMillis()).substring(5);
		String bankCardNumber = String.format("1234****%s", prefix);
		
		/*
		 * Формируем чек с транзакциями:
		 *   - отклоненная банковская транзакция (сумма оплаты = сумме чека)
		 *   - положительная транзакция (сумма оплаты = (сумма чека)/2)
		 *   - оплата наличными (сумма оплаты = (сумма чека)/2);
		 *   - в чеке банковская транзакция по одной банковской карте
		 */
		
		validAuth = generateAuthData(true);
		invalidAuth = generateAuthData(false);
		
		card = payments.setBankCardData(bankCardNumber, "VISA");
		purchase = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase, purchase.getCheckSumEnd(), card, invalidAuth);
		purchase = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase, purchase.getCheckSumEnd()/2, card, validAuth);
		purchase = payments.setCashPayment(purchase, purchase.getCheckSumEnd() - purchase.getCheckSumEnd()/2);
		sendCheck(purchase);
		
		/*
		 * убедиться, что чек в системе и найдено 2 транзакции
		 */
		transactions.setCheckBarcode(purchase);
		transactions.doSearch();
		transactions.getExpectedResultCount(2);
	}
	
	@BeforeMethod
	public void resetSearch(){
		/*
		 * Сбрасываем рез-т поиска (находим 0 транзакций), указывая баркод чека,
		 * которого нет в системе
		 */
		transactions.setFilterText(FILTER_CATEGORY_CHECK_BAR_CODE, "000");
		transactions.getExpectedResultCount(0);
	}
	
	@DataProvider(name = "Количество найденных транзакций")	
	public Object[][] transactionsCount(){
		return new Object[][]{
				{"Поиск транзакции по коду авторизации", FILTER_CATEGORY_AUTHORIZATION_CODE, validAuth.getAuthCode() , 1},
				{"Поиск транзакции по номеру банковской карты", FILTER_CATEGORY_BANK_CARD_NUMBER, card.getCardNumber() , 2},
				{"Поиск транзакции по номеру терминала", FILTER_CATEGORY_TERMINAL_NUMBER, validAuth.getTerminalId() , 1},
				{"Поиск транзакции по коду ответа процессингового центра", FILTER_CATEGORY_SERVER_RESPONSE_CODE, validAuth.getResponseCode(), 1},
//				{"Поиск транзакции по сумме оплаты отклоненной транзакции", FILTER_CATEGORY_SERVER_RESPONSE_CODE, "", 1},
//				{"Поиск транзакции по сумме оплаты положительной транзакции", 1},
//				{"Поиск транзакции по сумме чека", 2},
		};
	};
	
	@DataProvider(name = "Поля найденных транзакций")	
	public Object[][] transactionsContent(){
		/*
		 * Добавить hashMap для проверки полей найденных транзакций
		 */
		return new Object[][]{
				{"Поиск транзакции по коду авторизации", FILTER_CATEGORY_AUTHORIZATION_CODE, ""},
				{"Поиск транзакции по номеру банковской карты", FILTER_CATEGORY_BANK_CARD_NUMBER, ""},
				{"Поиск транзакции по номеру терминала", FILTER_CATEGORY_TERMINAL_NUMBER, ""},
				{"Поиск транзакции по коду ответа процессингового центра", FILTER_CATEGORY_SERVER_RESPONSE_CODE, ""},
//				{"Поиск транзакции по сумме оплаты отклоненной транзакции", FILTER_CATEGORY_SERVER_RESPONSE_CODE},
//				{"Поиск транзакции по сумме оплаты положительной транзакции"},
//				{"Поиск транзакции по сумме чека"},
		};
	};
	
	@DataProvider(name = "Поиск транзакций по суммам")	
	public Object[][] transactionsSum(){
		return new Object[][]{
				{"Поиск транзакции по сумме оплаты отклоненной транзакции", FILTER_CATEGORY_SERVER_RESPONSE_CODE},
				{"Поиск транзакции по сумме оплаты положительной транзакции"},
				{"Поиск транзакции по сумме чека"},
				{"Поиск транзакции по сумме позиции"},
		};
	};
	
	
	
	
	@Test (description = "SRTE-79. Поиск транзакций оплаты по основным параметрам", 
			dataProvider = "Количество найденных транзакций")
	public void testSearchTransactions(String field, String filterLocator , String filterValue, int transactionsCount){
		transactions.setFilterMultiText(filterLocator, filterValue);
		transactions.doSearch();
 		
 		searchResult = searchCheck.getSearchResultCount();
 		
		Assert.assertEquals(searchCheck.getExpectedResultCount(transactionsCount), transactionsCount, "Неверное число найденных трназакций оплаты");
 		testExcelExport(LOCATOR_XLS_TRANSACTIONS, XLS_REPORT_TRANSACTIONS);
	}
	
	
	private AuthorizationData generateAuthData(boolean transactionStatus){
		String message = transactionStatus ?  "ОДОБРЕНО" : "ОТКЛОНЕНО";
		
		DisinsectorTools.delay(99);
		AuthorizationData authData = new AuthorizationData();
		authData.setStatus(transactionStatus);
		authData.setBankid("ВТБ");
		authData.setAuthCode(String.valueOf(System.currentTimeMillis()));
		authData.setMessage(message);
		authData.setResponseCode(String.valueOf(System.currentTimeMillis()).substring(5));
		authData.setTerminalId("AA" + String.valueOf(System.currentTimeMillis()).substring(4));
		authData.setResultCode(System.currentTimeMillis() - 10000000000L);
		return authData;
	}
	
	
}
