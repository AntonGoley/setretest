package ru.crystals.set10.test.search;


import org.openqa.selenium.internal.seleniumemulation.DeleteAllVisibleCookies;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.pos.check.PurchaseEntity;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;

@Test (groups={"centrum", "retail"})
public class SearchCheckForRequestPerformanceTest extends SearchCheckAbstractTest{
	
	private PurchaseEntity p;
	private PurchaseEntity p1;
	private PurchaseEntity p1refund;
	private PurchaseEntity p1cancel;
	
	@BeforeClass
	public void send1stCheck(){
		super.openSearchPage();
		searchCheck.openFilter();
//		sendCheck();
//		/*
//		 * убедиться, что чек в системе
//		 */
//		
//		searchCheck.setCheckBarcode(purchase);
//		searchCheck.doSearch();
//		searchCheck.getExpectedResultCount(1);
//		
//		resetFiltersAndAdd2New();
	}
	
	@Test
	public void addCashFilter(){
		searchCheck.deleteAllFilters();
		searchCheck.addFilter();
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CASH_NUMBER, String.valueOf(cashEmulatorSearchCheck.getCashNumber()));
		searchCheck.addFilter();
		searchCheck.setFilterSelectSum(FILTER_CATEGORY_SUM_CHECK, FILTER_CATEGORY_SELECT_GREATER ,"1500");
		searchCheck.doSearch();
	}
	
	
	
	@DataProvider(name = "Тип чека")	
	private Object[][] checkType(){
		p1 = cashEmulatorSearchCheck.nextPurchaseWithoutSending();
		p1refund = cashEmulatorSearchCheck.nextRefundAllWithoutSending(purchase, false);
		p1cancel = cashEmulatorSearchCheck.nextCancelledPurchaseWithoutSending();
		return new Object[][]{
				{"Чек продажи",  FILTER_CATEGORY_CHECK_TYPE_SALE, p1},
				{"Чек возврата",  FILTER_CATEGORY_CHECK_TYPE_REFUND, p1refund},
				{"Аннулированый чек",  FILTER_CATEGORY_CHECK_TYPE_CANCEL, p1cancel},
		};
	};
	
	@Test ( enabled = false,
			description = "SRTE-71. Поиск чека на ТК по типу чека", 
			dataProvider = "Тип чека")
	public void testSearchCheckByType(String category, String filterName, PurchaseEntity p){
 		searchCheck.setFilterSelect(FILTER_CATEGORY_CHECK_TYPE, filterName);
 		searchCheck.doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		
 		sendCheck(p);
 		searchCheck.doSearch();
 		
 		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "Неверный результат поиска по условию: " + category);
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
	
}
