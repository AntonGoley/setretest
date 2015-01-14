package ru.crystals.set10.test.search;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;


public class SearchCheckByCheckTypeGroupTest extends SearchCheckAbstractTest{
	
	@BeforeClass
	public void send1stCheck(){
		sendCheck();
	}
	
	@Test ( enabled = false, description = "SRTE-71. Поиск чека на ТК по типу чека")
	public void testSearchCheckByType(){
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_CHECK_NUMBER, String.valueOf(checkNumber + 1)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по номеру кассы")
	public void testSearchCheckByCashNumber(){
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_CASH_NUMBER, String.valueOf(cashNumber)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по номеру смены")
	public void testSearchCheckBySiftNumber(){
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_SHIFT_NUMBER, String.valueOf(shiftNumber)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
	@Test ( description = "SRTE-71. Поиск чека на ТК по номеру чека")
	public void testSearchCheckByNumber(){
		// Найдем чек продажи с номером checkNumber + 1 (номер следующего чека продажи, который будет отправлен после)
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_CHECK_NUMBER, String.valueOf(checkNumber + 1)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по штрих коду чека")
	public void testSearchCheckByCheckBarCode(){
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CHECK_BAR_CODE, searchCheck.getCheckBarcode(purchase)).doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(1), 1, "");
	}
	
	@Test (enabled = false, description = "SRTE-71. Поиск чека на ТК по табельному номеру кассира")
	public void testSearchCheckByCashierTabNum(){
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CHECK_BAR_CODE, searchCheck.getCheckBarcode(purchase)).doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(1), 1, "");
	}
	
}
