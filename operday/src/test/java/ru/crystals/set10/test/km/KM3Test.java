package ru.crystals.set10.test.km;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.pages.operday.cashes.CashDocsAbstractPage;
import ru.crystals.set10.pages.operday.cashes.CashesPage;
import ru.crystals.set10.pages.operday.cashes.KmPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.setretailx.cash.CashVO;
import static ru.crystals.set10.pages.operday.cashes.KmPage.*;
import static ru.crystals.set10.pages.operday.OperDayPage.CASHES;
import static ru.crystals.set10.pages.operday.cashes.CashDocsAbstractPage.SQL_MAIN_CASH;

@Test(groups = {"retail", "centrum"})
public class KM3Test extends AbstractTest{
	
	KmPage km3;
	OperDayPage operDay;
	HTMLRepotResultPage htmlReportResults;
	CashesPage cashesPage;
	
	private String ifMainCash = "false";
	
	private static PurchaseEntity purchase;
	private static PurchaseEntity purchaseReturn;
	
	private int km3Tablerows = 0;
	
	private static String SQL_CLEAN_KM3 = "delete from od_km3";
	private static String SQL_CLEAN_KM3_ROW = "delete from od_km3_row";

	private HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	private String reportText;
	private boolean reportOpened = false;
	
	@BeforeClass
	public void prepareData(){
		/*  удалить km3 из базы*/
		dbAdapter.batchUpdateDb(DB_OPERDAY, new String[] {SQL_CLEAN_KM3, SQL_CLEAN_KM3_ROW} );
		log.info("Записи в таблице od_km3 и в таблице od_km3_row удалены в базе " + DB_OPERDAY);
		
		/* удалить файлы отчетов KM3 на диске*/
		DisinsectorTools.removeOldReport(chromeDownloadPath, KM3_PDF);
		
		/* проверка, включена ли главная касса*/
		ifMainCash = dbAdapter.queryForString(DB_SET, SQL_MAIN_CASH);
		
		/* перейти на страницу с документами КМ3*/
		cashesPage = new LoginPage(getDriver(), TARGET_HOST_URL)
				.openOperDay(Config.MANAGER, Config.MANAGER_PASSWORD)
				.navigatePage(CashesPage.class, CASHES);

		if (ifMainCash.equals("true")) {
			km3 = (KmPage) cashesPage.openTab(CashDocsAbstractPage.class, CashesPage.LOCATOR_MAINCASH_TAB)
				.switchToTable(LOCATOR_KM3);
		} else {
			km3 = (KmPage)cashesPage.openTab(KmPage.class, CashesPage.LOCATOR_ACTS_TAB)
					.switchToTable(LOCATOR_KM3);
		}
		
		/* количество актов km3 */
		km3Tablerows = km3.getDocCountOnPage();
		
		purchase = (PurchaseEntity) purchaseGenerator.generatePurchaseWithPositions(10);
		cashEmulator.nextPurchase(purchase);
		/* Возвращаем 1-ю позицию */
		returnPositions.put(1L, 1L * 1000);
		
		purchaseReturn = (PurchaseEntity) cashEmulator.nextRefundPositions(purchase, returnPositions, false);
	}
	
	
	
	@DataProvider (name = "Поля КМ3")
	private static Object[][] km3Fields(){
		BigDecimal sumRetunPositions  = purchaseReturn.getCheckSumEndBigDecimal();
		
		CashVO cashVO = new CashVO();
		cashVO = cashEmulator.setCashVO(cashEmulator.getCashNumber(), TARGET_SHOP, new Date().getTime());
		cashEmulator.sendCashVO(cashVO);
		
		return new Object[][]{
				{"Название формы", "О ВОЗВРАТЕ ДЕНЕЖНЫХ СУММ ПОКУПАТЕЛЯМ (КЛИЕНТАМ)\nПО НЕИСПОЛЬЗОВАННЫМ КАССОВЫМ ЧЕКАМ"},
				{"Строка Итого", (String.valueOf(sumRetunPositions)).replace(".", ",") + " р.\n" + "Итого"},
				{"Содержит ККМ: номер производителя (factory num)", cashVO.getFactoryNum()},
				{"Содержит ККМ: рег. номер (fisc num)", cashVO.getFiscalNum()},
		};
	}
	
	@Test( description = "SRTE-28. Если в систему пришел первый возвратный чек, создается форма КМ3")
	public void testKM3CreatesAfter1stRefund(){
		km3.switchToTable(LOCATOR_KM6).switchToTable(LOCATOR_KM3);
		km3 = new KmPage(getDriver());
		++km3Tablerows;
		Assert.assertEquals(km3.getExpectedDocsCountOnPage(km3Tablerows), km3Tablerows, "Не появилась форма КМ3");
	}
	
	@Test (	dependsOnMethods ="testKM3CreatesAfter1stRefund",
			description = "SRTE-28. Правильность заполнения формы КМ3 данными",
			dataProvider = "Поля КМ3")
	public void testKM3Data(String fiels, String expectedValue){
		if (!reportOpened) {
			reportOpened = true;
			km3.printAllDocs();
			reportText = km3.getPDFContent(DisinsectorTools.getDownloadedFile(chromeDownloadPath, KM3_PDF), 1);
		}
		log.info("Значение поля: " + fiels);
		Assert.assertTrue(reportText.contains(expectedValue), "Неверное значение поля в форме КМ3");
	}
	
	@Test(  dependsOnMethods ="testKM3Data",
			description = "SRTE-28. Если форма КМ3 распечатана, следующий возвратный чек попадает в новую форму КМ3",
			alwaysRun = true
			)
	public void testNewKM3CreatesIfcurrentPrinted(){
		
		km3Tablerows = km3.getDocCountOnPage();
		
		returnPositions.clear();
		returnPositions.put(2L, 1L * 1000);
		purchaseReturn = (PurchaseEntity) cashEmulator.nextRefundPositions(purchase, returnPositions, false);
		km3.switchToTable(LOCATOR_KM6).switchToTable(LOCATOR_KM3);
		
		Assert.assertEquals(km3.getExpectedDocsCountOnPage(km3Tablerows + 1), km3Tablerows + 1, "Новая форма КМ3 не создалась для нового чека возврата, после печати существующей КМ3");
	}
	
	@Test(  dependsOnMethods ="testNewKM3CreatesIfcurrentPrinted",
			description = "SRTE-28. Новая форма КМ3 создается для новой смены",
			alwaysRun = true)
	public void testKM3CreatesForNewShift(){
		
		km3Tablerows = km3.getDocCountOnPage();
		
		returnPositions.clear();
		returnPositions.put(3L, 1L * 1000);
		cashEmulator.useNextShift();
		purchaseReturn = (PurchaseEntity) cashEmulator.nextRefundPositions(purchase, returnPositions, false);
		km3.switchToTable(LOCATOR_KM6).switchToTable(LOCATOR_KM3);
		Assert.assertEquals(km3.getExpectedDocsCountOnPage(km3Tablerows + 1), km3Tablerows + 1, "Новая форма КМ3 не создалась для новой смены");
	}
	
	@Test( 	dependsOnMethods ="testNewKM3CreatesIfcurrentPrinted",
			description = "SRTE-28. Новая форма КМ3 создается для новой кассы",
			alwaysRun = true)
	public void testKM3CreatesForNewCash(){
		
		km3Tablerows = km3.getDocCountOnPage();
		
		PurchaseEntity p1 = (PurchaseEntity) cashEmulatorMainCash.nextPurchase();
		returnPositions.clear();
		returnPositions.put(1L, 1L * 1000);
		purchaseReturn = (PurchaseEntity) cashEmulatorMainCash.nextRefundPositions(p1, returnPositions, true);
		km3.switchToTable(LOCATOR_KM6).switchToTable(LOCATOR_KM3);
		Assert.assertEquals(km3.getExpectedDocsCountOnPage(km3Tablerows + 1), km3Tablerows + 1, "Новая форма КМ3 не создалась для новой смены");

	}
	
//	
//	@Test( description = "SRTE-28. Если в форме КМ3 12 чеков возврата, следующий возвратный чек попадает в новую форму КМ3")
//	public void testNewKM3After12RefundChecks(){
//	}
//	
//
//	
//	@Test( description = "SRTE-28. Создается новая форма КМ3, если чек возврата с датой следующего дня (после 00:00:00 часов)")
//	public void testNewKM3AfterMidnight(){
//	}
	
}
