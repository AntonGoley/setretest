package ru.crystals.set10.test.search;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.pages.operday.searchcheck.TransactionSearchPage;
import ru.crystals.set10.utils.CashEmulatorPayments;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;
import static ru.crystals.set10.pages.operday.OperDayPage.SEARCH_TRANSACTIONS;

public class SearchTransactionsTest extends SearchCheckAbstractTest{
	
	TransactionSearchPage transactions;
	
	CashEmulatorPayments payments = new CashEmulatorPayments();
	
	@BeforeClass
	public void navigateToTransactionsPage(){
		transactions = searchCheck.navigatePage(TransactionSearchPage.class, SEARCH_TRANSACTIONS);
		purchase = payments.getPurchaseWithoutPayments();
		//TODO:добавить транзакцию
		
		/*
		 * убедиться, что чек в системе
		 */
		transactions.setCheckBarcode(purchase);
		transactions.doSearch();
		transactions.getExpectedResultCount(1);
	}
	
	@DataProvider(name = "Положительная транзакция в чеке")	
	public Object[][] positiveTransactionCreteria(){
		return new Object[][]{
				{"Поиск транзакции по номеру чека", FILTER_CATEGORY_CHECK_NUMBER, String.valueOf(purchase.getNumber())},
				{"Поиск транзакции по номеру кассы", FILTER_CATEGORY_CASH_NUMBER, String.valueOf(purchase.getShift().getCashNum())},	
		};
	};
	
	@BeforeMethod
	public void resetSearch(){
		/*
		 * Сбрасываем рез-т поиска, указывая баркод чека,
		 * которого нет в системе
		 */
		transactions.setFilterMultiText(FILTER_CATEGORY_CHECK_BAR_CODE, "000");
		transactions.getExpectedResultCount(0);
	}
	
	@Test (description = "SRTE-79. Поиск чека, содержащего положительную транзакцию")
	public void testSearchTransactions(String field, String filterLocator , String filterValue){

		transactions.setFilterMultiText(filterLocator, filterValue);
		transactions.doSearch();
 		
 		searchResult = searchCheck.getSearchResultCount();
 		sendRefundCheck();
 		
 		transactions.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
		
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
 		
 		
	}
	
	
	
}
