package ru.crystals.set10.refundrequests;


import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;
import ru.crystals.set10.pages.operday.searchcheck.PaymentTransactionsPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;


public class SearchCheckTest extends AbstractTest{
	
	MainPage mainPage;
	CheckSearchPage searchCheck;
	ReportConfigPage RefundChecksConfigPage;
	PurchaseEntity pe;
	CheckContentPage checkContent;
	PaymentTransactionsPage paymentTransactions;
	
	static PurchaseEntity purchase;
	static long checkNumber = 0;
	static long shiftNumber = 0;
	static long shopNumber = 0;
	static long cashNumber = 0;
	static long checkBarcode = 0;
	static int searchResult = 0;
	static String barcode = ""; 
	
	@BeforeClass
	public void openSearchPage() {
		cashEmulatorSearchCheck.useNextShift();
		sendCheck();
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().openCheckSearch().openFilter();
	}	
	
	@Test (description = "SRTE-71. Поиск чека на ТК по номеру чека")
	public void testSearchCheckByNumber(){
		// Найдем чек продажи с номером checkNumber + 1 (номер следующего чека продажи, который будет отправлен после)
 		searchCheck.setFilterText(FILTER_CATEGORY_CHECK_NUMBER, String.valueOf(checkNumber + 1)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		
 		searchCheck.setFilterText(FILTER_CATEGORY_CHECK_NUMBER, String.valueOf(checkNumber)).doSearch();
		Assert.assertEquals("", searchResult + 1, searchCheck.getSearchResultCount());
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по номеру кассы")
	public void testSearchCheckByCashNumber(){
 		searchCheck.setFilterText(FILTER_CATEGORY_CASH_NUMBER, String.valueOf(cashNumber)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals("", searchResult + 1, searchCheck.getSearchResultCount());
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по номеру смены")
	public void testSearchCheckBySiftNumber(){
 		searchCheck.setFilterText(FILTER_CATEGORY_SHIFT_NUMBER, String.valueOf(shiftNumber)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals("", searchResult + 1, searchCheck.getSearchResultCount());
	}
	
	@Test (enabled = false, description = "SRTE-71. Поиск чека на ТК по номеру магазина")
	public void testSearchCheckByShopNumber(){
 		searchCheck.setFilterText(FILTER_CATEGORY_SHOP_NUMBER, String.valueOf(shopNumber)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals("", searchResult + 1, searchCheck.getSearchResultCount());
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по штрих коду товара")
	public void testSearchCheckByGoodBarCode(){
		String searchBarcode = barcode;
 		searchCheck.setFilterMultiText(FILTER_CATEGORY_GOOD_BAR_CODE, String.valueOf(searchBarcode)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		sendRefundCheck();
 		
		searchCheck.doSearch();
		Assert.assertEquals("", searchResult + 1, searchCheck.getSearchResultCount());
	}
	
	@Test (description = "SRTE-71. Поиск чека на ТК по штрих коду чека")
	public void testSearchCheckByCheckBarCode(){
		searchCheck.setFilterText(FILTER_CATEGORY_CHECK_BAR_CODE, String.valueOf(generateCheckBarCode())).doSearch();
		Assert.assertEquals("", 1, searchCheck.getSearchResultCount());
	}
	
	@Test (enabled = false, description = "SRTE-71. Поиск чека на ТК по типу чека Возвратные")
	public void testSearchCheckByTypeRefund(){
		searchCheck.setFilterSelect(FILTER_CATEGORY_CHECK_TYPE, FILTER_CATEGORY_CHECK_TYPE_REFUND);
		Assert.assertEquals("", 1, searchCheck.getSearchResultCount());
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
	
	private static void sendCheck(){
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextPurchase();
		setCheckData();
	}
	
	private static void sendRefundCheck(){
		PurchaseEntity superPurchase = new PurchaseEntity();
		superPurchase = purchase;
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextRefundAll(superPurchase, false);
		setCheckData();
	}
	
	private static void setCheckData(){
		checkNumber = purchase.getNumber();
		shiftNumber = purchase.getShift().getNumShift();
		shopNumber = purchase.getShift().getShopIndex();
		cashNumber = purchase.getShift().getCashNum();
		barcode = purchase.getPositions().get(0).getBarCode();
	}
}
