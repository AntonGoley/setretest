package ru.crystals.set10.test.search;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.utils.PaymentGenerator;
import ru.crystals.set10.utils.PurchaseGenerator;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;


@Test (groups={"centrum", "retail"})
public class SearchCheckByOtherGroupsTest extends SearchCheckAbstractTest{
	
	PaymentGenerator payments = new PaymentGenerator();
	
	@BeforeClass
	public void openFilter(){
		super.openSearchPage();
		searchCheck.openFilter();
		
		resetFiltersAndAdd2New();
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по штрих коду товара")
	public void testSearchCheckByGoodBarCode(){
		sendCheck();
		String searchBarcode = purchase.getPositions().get(0).getBarCode();
		/*
		 * убедиться, что чек в системе
		 */
		searchCheck.setCheckBarcode(purchase);
		searchCheck.doSearch();
		searchCheck.getExpectedResultCount(1);
		/*
		 * проверить, сколько чеков в системе, где есть штрих код searchBarcode
		 */
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_GOOD_BAR_CODE, String.valueOf(searchBarcode));
 		searchCheck.doSearch();
 		
 		searchResult = searchCheck.getSearchResultCount();
 		sendRefundCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
		
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по коду товара (артикулу)")
	public void testSearchCheckByGoodCode(){
		sendCheck();
		String goodCode = purchase.getPositions().get(0).getItem();
		/*
		 * убедиться, что чек в системе
		 */
		searchCheck.setCheckBarcode(purchase);
		searchCheck.doSearch();
		searchCheck.getExpectedResultCount(1);
		/*
		 * проверить, сколько чеков в системе, где есть  код товара goodCode
		 */
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_GOOD_CODE, String.valueOf(goodCode));
 		searchCheck.doSearch();
 		
 		searchResult = searchCheck.getSearchResultCount();
 		sendRefundCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
		
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
	@Test (description = "SRTE-71. Поиск чека по номеру скидочной карты")
	public void testSearchCheckByDiscountCardNumber(){
		/*
		 * сгенерить номер скидочной карты
		 */
		String discountCardNumber = String.valueOf(System.currentTimeMillis());
		searchCheck.setFilterMultiText(FILTER_CATEGORY_DISCOUNT_CARD_NUMBER, String.valueOf(discountCardNumber));
		searchCheck.doSearch();
		searchResult = searchCheck.getSearchResultCount();
		/*
		 * Добавить карту к чеку
		 */
		purchase = PurchaseGenerator.getPurchaseWithoutPayments();
		purchase = payments.setCashPayment(purchase, purchase.getCheckSumEnd());
		purchase = payments.setDiscountCard(purchase, discountCardNumber);
		sendCheck(purchase);
		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
		
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
//	@Test (enabled = false, description = "SRTE-71. Поиск чека на ТК по номеру магазина")
//	public void testSearchCheckByShopNumber(){
// 		searchCheck.setFilterText(FILTER_CATEGORY_SHOP_NUMBER, String.valueOf(shopNumber));
// 		searchCheck.doSearch();
// 		searchResult = searchCheck.getSearchResultCount();
// 		sendCheck();
// 		
//		searchCheck.doSearch();
//		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
//	}
	
}
