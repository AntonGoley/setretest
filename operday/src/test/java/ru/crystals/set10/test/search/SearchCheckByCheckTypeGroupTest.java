package ru.crystals.set10.test.search;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.pos.check.PurchaseEntity;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;

@Test (groups={"centrum", "retail"})
public class SearchCheckByCheckTypeGroupTest extends SearchCheckAbstractTest{
	
	private PurchaseEntity p1;
	private PurchaseEntity p1refund;
	private PurchaseEntity p1cancel;
	
	
	@BeforeClass
	public void send1stCheck(){
		super.openSearchPage();
		searchCheck.openFilter();
		sendCheck();
		/*
		 * убедиться, что чек в системе
		 */
		
		searchCheck.setCheckBarcode(purchase);
		searchCheck.doSearch();
		searchCheck.getExpectedResultCount(1);
		
		resetFiltersAndAdd2New();
	}
	
	@BeforeMethod
	public void addCashFilter(){
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CASH_NUMBER, String.valueOf(cashEmulatorSearchCheck.getCashNumber()));
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
	
	@Test (
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
	
	@Test (	description = "SRTE-71. Поиск чека на ТК по номеру кассы")
	public void testSearchCheckByCashNumber(){
 		/*
 		 * Сбросить фильтр, т.к 
 		 */
		searchCheck.deleteAllFilters();
		searchCheck.addFilter();
		
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CASH_NUMBER, String.valueOf(cashNumber));
 		searchCheck.doSearch();
 		
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
 		
 		searchCheck.addFilter();
	}
	
	@Test (
			description = "SRTE-71. Поиск чека на ТК по номеру смены")
	public void testSearchCheckBySiftNumber(){
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_SHIFT_NUMBER, String.valueOf(shiftNumber));
 		searchCheck.doSearch();
 		
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
	@Test ( 
			description = "SRTE-71. Поиск чека на ТК по номеру чека")
	public void testSearchCheckByNumber(){
		// Найдем чек продажи с номером checkNumber + 1 (номер следующего чека продажи, который будет отправлен после)
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_CHECK_NUMBER, String.valueOf(checkNumber + 1));
 		searchCheck.doSearch();
 		
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
	@Test ( 
			description = "SRTE-71. Поиск чека на ТК по штрих коду чека")
	public void testSearchCheckByCheckBarCode(){
		searchCheck.setFilterText(FILTER_CATEGORY_CHECK_BAR_CODE, searchCheck.getCheckBarcode(purchase));
		searchCheck.doSearch();
		
		Assert.assertEquals(searchCheck.getExpectedResultCount(1), 1, "");
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
	@Test (enabled = false, description = "SRTE-71. Поиск чека на ТК по табельному номеру кассира")
	public void testSearchCheckByCashierTabNum(){
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CHECK_BAR_CODE, searchCheck.getCheckBarcode(purchase));
		searchCheck.doSearch();
		
		Assert.assertEquals(searchCheck.getExpectedResultCount(1), 1, "");
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
}
