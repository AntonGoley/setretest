package ru.crystals.set10.test.tablereports;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.tablereports.AdverstingReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.AdverstingReportConfigPage.*;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;


public class TableReportAdverstingTest extends AbstractTest {
	
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	AdverstingReportConfigPage adverstingConfigPage;
	String goodRequest = "";
	String adverstingRequest = "";
	HTMLRepotResultPage htmlReportResults;

	SoapRequestSender soapSender  = new SoapRequestSender();
	String ti = soapSender.generateTI();
	String erpCode = 47 + ti;
	String barCode = "78" + ti;
	
	
	@BeforeClass
	public void navigateToAdverstingReports() {
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		adverstingConfigPage = tableReportsPage.openReportConfigPage(AdverstingReportConfigPage.class, TAB_ADVERSTING, REPORT_NAME_ADVERSTING);
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		sendData();
		doReport();
	}	
	
	public void doReport(){
		adverstingConfigPage.setGoodIDs(erpCode);
		htmlReportResults = adverstingConfigPage.generateReport(HTMLREPORT);
		// закрыть окно отчета
		adverstingConfigPage.switchWindow(true);
	}
	
	private void sendData() {
		goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}
	
	@Test (	priority = 1,
			description = "Проверить название отчета и названия колонок в шапке таблицы отчета по рекламным акциям", 
			alwaysRun = true,
			dataProvider = "Шапка отчета Рекламные акции", dataProviderClass = TableReportsDataprovider.class)
	public void testAdverstingHTMLReportTableHead(String fieldName){
		log.info("Проверить название/наличие поля: " + fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test(description = "Проверить, что генерируется пустой отчет, если на товар не заведена рекламная акция")
	public void testEmptyAdverstingHTMLReport() {
		doReport();
		Assert.assertTrue(htmlReportResults.getReportSize() == 17, "Сгененированный отчет не пустой");
	}	
	 
	@Test (dependsOnMethods = "testEmptyAdverstingHTMLReport",
			description = "Проверить, наличие товара в отчете, если на него заведена рекламная акция, действующая сегодня", 
			alwaysRun = true)
	public void testGoodInReport(){
		// завести рекламную акцию на товар с erpCode
		ti = soapSender.generateTI();
		soapSender = new SoapRequestSender();
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		adverstingRequest = DisinsectorTools.getFileContentAsString("adversting.txt");
		soapSender.sendAdversting(String.format(adverstingRequest, erpCode, ti),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
		doReport();

		Assert.assertTrue(htmlReportResults.containsValue(erpCode), "Отсутствует ERP код в отчете");
	}
	
	@Test(	dependsOnMethods = "testGoodInReport",
			description = "Проверить, что генерируется пустой отчет, если на товар не заведена рекламная акция")
	public void testReportAllActionsIfNoParameters() {
		String expectedErpCode = erpCode;
		erpCode = "";
		doReport();
		// Проверяем название акции в отчете
		Assert.assertTrue(htmlReportResults.containsValue("test_" + expectedErpCode), "Не выводятся существующие рекламные акции, если не задан код товара");
	}	
	
	
	
	
//	@Test(	dependsOnMethods = "testGoodInReport",
//			description = "Если не указаны магазины, отчет генерится для всей сети (все магазины)", 
//			alwaysRun = true) 
	public void testWithEmptyShop(){
		Assert.assertTrue(htmlReportResults.containsValue(Config.SHOP_NUMBER), "В отчете отсутствует информация по магазину: " + Config.SHOP_NUMBER);
		Assert.assertTrue(htmlReportResults.containsValue(Config.SHOP_NUMBER), "В отчете отсутствует информация по центруму" + Config.SHOP_NUMBER);
	}
	
	
	//@Test(description = "Если указан магазин, отчет генерится только для этого магазина")
	public void testParticularShop() {
	}
	
	//@Test(description = "Проверить генерацию отчета для двух ERP кодов, указанных через запятую")
	public void testTwoERPCodes() {
	}

	
	
}
