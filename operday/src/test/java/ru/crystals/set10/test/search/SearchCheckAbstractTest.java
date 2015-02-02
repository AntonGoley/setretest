package ru.crystals.set10.test.search;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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
import static ru.crystals.set10.pages.operday.OperDayPage.SEARCH_CHECK;;


public class SearchCheckAbstractTest extends AbstractTest{
	
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
	
	static {
		cashEmulatorSearchCheck.useNextShift();
	}
	
	/*
	 *  Переход на страницу поиска
	 *  выполняется для всех классов, наследованных от SearchCheckAbstractTest
	 *  все тесты должны выполняться последовательно в одном потоке
	 */
	@BeforeClass
	public void openSearchPage() {
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().navigatePage(CheckSearchPage.class, SEARCH_CHECK);
	}	
	
	protected static void sendCheck(){
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextPurchase();
		setCheckData();
	}
	
	protected static void sendCheck(PurchaseEntity pe){
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextPurchase(pe);
		setCheckData();
	}
	
	/*
	 * Возвращаем весь чек
	 */
	protected static void sendRefundCheck(){
		PurchaseEntity superPurchase = new PurchaseEntity();
		superPurchase = purchase;
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextRefundAll(superPurchase, false);
		setCheckData();
	}
	
	protected static void setCheckData(){
		checkNumber = purchase.getNumber();
		shiftNumber = purchase.getShift().getNumShift();
		shopNumber = purchase.getShift().getShopIndex();
		cashNumber = purchase.getShift().getCashNum();
		/*
		 * Задержка, после отправки чека, т.к в системе не все сущности 
		 * успевают обновиться до нажатия кнопки поиск.
		 */
		DisinsectorTools.delay(1000);
	}
	
	public void testExcelExport(String reportLocator, String reportFileNamePattern){
		long fileSize = 0;
		log.info("Проверить сохранение файла результатов поиска " + reportFileNamePattern);
		fileSize =  searchCheck.exportFileData(chromeDownloadPath, reportFileNamePattern, searchCheck, reportLocator).length();
		log.info("Размер сохраненного файла: " + reportFileNamePattern + " равен " +  fileSize);
		DisinsectorTools.removeOldReport(chromeDownloadPath, reportFileNamePattern);
		
		Assert.assertTrue(fileSize > 1000, "Файл отчета " + reportFileNamePattern + " сохранился некорректно");
		
	}
}
