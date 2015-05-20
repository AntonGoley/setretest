package ru.crystals.set10.test.search;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.searchcheck.PaymentTransactionsPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.OperDayPage.SEARCH_CHECK;
import static ru.crystals.set10.pages.operday.searchcheck.SearchFormPopUp.FILTER_CATEGORY_CASH_NUMBER;


@Test (groups={"centrum", "retail"})
public class SearchCheckAbstractTest extends AbstractTest{
	
	MainPage mainPage;
	CheckSearchPage searchCheck;
	
	ReportConfigPage RefundChecksConfigPage;
	PurchaseEntity pe;
	CheckContentPage checkContent;
	PaymentTransactionsPage paymentTransactions;
	
	static PurchaseEntity purchase;
	static int searchResult = 0;
	
	
	@AfterSuite
	public void closeShift(){
		cashEmulatorSearchCheck.nextZReport();
	}
	
	/*
	 *  Переход на страницу поиска
	 *  выполняется для всех классов, наследованных от SearchCheckAbstractTest
	 *  все тесты должны выполняться последовательно в одном потоке
	 */

	protected void openSearchPage() {
		mainPage = new LoginPage(getDriver(), TARGET_HOST_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().navigatePage(CheckSearchPage.class, SEARCH_CHECK);
	}	
	
	
	//TODO: следующие 2 метода выглядят как ненужные..
	protected static void sendCheck(){
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextPurchase();
	}
	
	protected static void sendCheck(PurchaseEntity p){
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextPurchase(p);
	}
	
	/*
	 * Возвращаем весь чек
	 */
	protected static void sendRefundCheck(){
		PurchaseEntity superPurchase = new PurchaseEntity();
		superPurchase = purchase;
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextRefundAll(superPurchase, false);
	}
	
	protected void testExcelExport(String reportLocator, String reportFileNamePattern){
		long fileSize = 0;
		log.info("Проверить сохранение файла результатов поиска " + reportFileNamePattern);
		fileSize =  searchCheck.exportFileData(chromeDownloadPath, reportFileNamePattern, searchCheck, reportLocator).length();
		log.info("Размер сохраненного файла: " + reportFileNamePattern + " равен " +  fileSize);
		DisinsectorTools.removeOldReport(chromeDownloadPath, reportFileNamePattern);
		
		Assert.assertTrue(fileSize > 500, "Файл отчета " + reportFileNamePattern + " сохранился некорректно");
		
	}
	
	protected void resetFiltersAndAdd2New(){
		/*
		 * Сбросить фильтр и добавить 2 новых, одни из которых Касса
		 */
		searchCheck.deleteAllFilters();
		searchCheck.addFilter();
		searchCheck.setFilterMultiText(FILTER_CATEGORY_CASH_NUMBER, String.valueOf(cashEmulatorSearchCheck.getCashNumber()));
		searchCheck.addFilter();
	}
	
	
}
