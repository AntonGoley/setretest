package ru.crystals.set10.search;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;
import ru.crystals.set10.utils.DisinsectorTools;


public class SearchCheckTest extends SearchCheckAbstractTest{
	
	
	@BeforeClass
	public void send1stCheck(){
		sendCheck();
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
	
	@Test (enabled = false, description = "SRTE-71. Поиск чека на ТК по номеру магазина")
	public void testSearchCheckByShopNumber(){
 		searchCheck.setFilterText(FILTER_CATEGORY_SHOP_NUMBER, String.valueOf(shopNumber)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по штрих коду товара")
	public void testSearchCheckByGoodBarCode(){
		String searchBarcode = barcode;
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_GOOD_BAR_CODE, String.valueOf(searchBarcode)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendRefundCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по штрих коду чека")
	public void testSearchCheckByCheckBarCode(){
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CHECK_BAR_CODE, String.valueOf(generateCheckBarCode())).doSearch();
		Assert.assertEquals(searchCheck.getSearchResultCount(), 1, "");
	}
	
	@Test (enabled = false, description = "SRTE-71. Поиск чека на ТК по типу чека Возвратные")
	public void testSearchCheckByTypeRefund(){
		searchCheck.setFilterSelect(FILTER_CATEGORY_CHECK_TYPE, FILTER_CATEGORY_CHECK_TYPE_REFUND);
		Assert.assertEquals(searchCheck.getExpectedResultCount(1), 1, "");
	}
	
	
	
	private String generateCheckBarCode(){
		int cash = 100 + (int)cashNumber;
		int shift = 1000 + (int)shiftNumber;
		String date = DisinsectorTools.getDate("ddMMyy", purchase.getDateCommit().getTime());
		int check = 1000 + (int)checkNumber;
		
		StringBuffer result = new StringBuffer();
		/*
		 * Формат чека ccc.ssss.dddddd.nnnn
		 * ccc - касса, ssss - смена, dddddd - дата, nnnn - номер чека
		 */
		result.append(String.valueOf(cash).replaceFirst("^.", String.valueOf((long)Math.floor(cash/100) - 1))).append(".");
		result.append(String.valueOf(shift).replaceFirst("^.", String.valueOf((long)Math.floor(shift/1000) - 1))).append(".");
		result.append(date).append(".");
		result.append(String.valueOf(check).replaceFirst("^.", String.valueOf((long)Math.floor(check/1000) - 1)));
		return result.toString();
	}
}
