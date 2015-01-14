package ru.crystals.set10.test.search;


import org.testng.Assert;
import org.testng.annotations.Test;

import ru.crystals.set10.utils.CashEmulatorPayments;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;


public class SearchCheckByOtherGroupsTest extends SearchCheckAbstractTest{
	
	CashEmulatorPayments payments = new CashEmulatorPayments();
	
	@Test (description = "SRTE-71. Поиск чека на ТК по штрих коду товара")
	public void testSearchCheckByGoodBarCode(){
		sendCheck();
		String searchBarcode = purchase.getPositions().get(0).getBarCode();
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_GOOD_BAR_CODE, String.valueOf(searchBarcode)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendRefundCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по коду товара (артикулу)")
	public void testSearchCheckByGoodCode(){
		sendCheck();
		String goodCode = purchase.getPositions().get(0).getItem();
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_GOOD_CODE, String.valueOf(goodCode)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendRefundCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals(searchCheck.getExpectedResultCount(searchResult + 1), searchResult + 1, "");
	}
	
	@Test (description = "SRTE-71. Поиск чека по номеру скидочной карты")
	public void testSearchCheckByDiscountCardNumber(){
		String discountCardNumber = String.valueOf(System.currentTimeMillis());
		searchCheck.setFilterMultiText(FILTER_CATEGORY_DISCOUNT_CARD_NUMBER, String.valueOf(discountCardNumber)).doSearch();
		searchResult = searchCheck.getSearchResultCount();
		
		purchase = payments.getPurchaseWithoutPayments();
		purchase = payments.setCashPayment(purchase, purchase.getCheckSumEnd());
		purchase = payments.setDiscountCard(purchase, discountCardNumber);
		sendCheck(purchase);
		
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
	
}
