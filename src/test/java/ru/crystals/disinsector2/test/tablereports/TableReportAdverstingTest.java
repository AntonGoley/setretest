package ru.crystals.disinsector2.test.tablereports;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.disinsector2.test.AbstractTest;
import ru.crystals.disinsector2.test.dataproviders.TableReportsDataprovider;
import ru.crystals.test2.basic.LoginPage;
import ru.crystals.test2.basic.MainPage;
import ru.crystals.test2.config.Config;
import ru.crystals.test2.operDay.tableReports.AdverstingReportConfigPage;
import ru.crystals.test2.operDay.tableReports.HTMLRepotResultPage;
import ru.crystals.test2.operDay.tableReports.TableReportPage;
import ru.crystals.test2.utils.DisinsectorTools;
import ru.crystals.test2.utils.SoapRequestSender;
import static ru.crystals.test2.operDay.tableReports.AdverstingReportConfigPage.*;
import static ru.crystals.test2.operDay.tableReports.TableReportPage.*;


public class TableReportAdverstingTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	AdverstingReportConfigPage adverstingConfigPage;
	String goodRequest = "";
	String adverstingRequest = "";
	HTMLRepotResultPage htmlReportResults;

	SoapRequestSender soapSender  = new SoapRequestSender();
	String ti = soapSender.generateTI();;
	String erpCode = 47 + ti;
	String barCode = "78" + ti;
	
	
	@BeforeClass
	public void navigateToAdverstingReports() {
		mainPage = new LoginPage(getDriver()).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
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
			description = "Проверить названия отчета и название колонок в шапке таблицы отчета по рекламным акциям", 
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
		// сгенерировать отчет
		doReport();

		Assert.assertTrue(htmlReportResults.containsValue(erpCode), "Отсутствует ERP код в отчете");
	}
	
//	@Test(	dependsOnMethods = "testGoodInReport",
//			description = "Если не указаны магазины, отчет генерится для всей сети (все магазины)", 
//			alwaysRun = true) 
	public void testWithEmptyShop(){
		Assert.assertTrue(htmlReportResults.containsValue(Config.SHOP_NUMBER), "В отчете отсутствует информация по магазину: " + Config.SHOP_NUMBER);
		Assert.assertTrue(htmlReportResults.containsValue(Config.RETAIL_NUMBER), "В отчете отсутствует информация по центруму" + Config.RETAIL_NUMBER);
	}
	
	
	//@Test(description = "Если указан магазин, отчет генерится только для этого магазина")
	public void testParticularShop() {
	}
	
	//@Test(description = "Проверить генерацию отчета для двух ERP кодов, указанных через запятую")
	public void testTwoERPCodes() {
	}
	
	
	
//	@Test(dependsOnMethods = "testHTMLFormat",
//			alwaysRun = true)
//	public void testPDFFormat() {
//		adverstingConfigPage.switchWindow(true);
//		adverstingConfigPage.generateReport(PDFREPORT);
//		adverstingConfigPage.saveReportFile();
//		savedReportFileName = new DisinsectorTools().getReportFileName(savedReportsLocalPath, ".pdf");
//	}
	
	
}
