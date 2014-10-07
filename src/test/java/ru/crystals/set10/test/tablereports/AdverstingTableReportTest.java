package ru.crystals.set10.test.tablereports;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.AdverstingReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.*;


public class AdverstingTableReportTest extends AbstractReportTest {
	
	AdverstingReportConfigPage adverstingConfigPage;
	String goodRequest;
	String adverstingRequest;

	SoapRequestSender soapSender  = new SoapRequestSender();
	String ti = soapSender.generateTI();
	String erpCode = "47" + ti;
	String barCode = "78" + ti;
	
	@BeforeClass
	public void navigateToAdverstingReport() {
		adverstingConfigPage =  navigateToReportConfig(
				Config.CENTRUM_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				AdverstingReportConfigPage.class, 
				TAB_ADVERSTING, 
				REPORT_NAME_ADVERSTING);
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		sendData();
		doReport();
	}	
	
	@Test ( description = "Проверить название отчета и названия колонок в шапке таблицы отчета по рекламным акциям", 
			dataProvider = "Шапка отчета Рекламные акции", dataProviderClass = TableReportsDataprovider.class)
	public void testAdverstingHTMLReportTableHead(String fieldName){
		log.info("Проверить название/наличие поля: " + fieldName);
		Assert.assertTrue(this.htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test(description = "Проверить, что генерируется пустой отчет, если на товар не заведена рекламная акция")
	public void testEmptyAdverstingHTMLReport() {
		Assert.assertTrue(this.htmlReportResults.getReportSize() == 17, "Сгененированный отчет не пустой");
	}	
	
	@Test (dependsOnMethods = "testEmptyAdverstingHTMLReport",
			description = "Проверить, наличие товара в отчете, если на него заведена рекламная акция, действующая сегодня", 
			alwaysRun = true)
	public void testGoodInReport(){
		// завести рекламную акцию на товар с erpCode
		ti = soapSender.generateTI();
		adverstingRequest = DisinsectorTools.getFileContentAsString("adversting.txt");
		soapSender.sendAdversting(String.format(adverstingRequest, erpCode, ti),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
		doReport();
		Assert.assertTrue(htmlReportResults.containsValue(erpCode), "Отсутствует ERP код в отчете");
	}
	
	@Test(	dependsOnMethods = "testGoodInReport",
			description = "Если не задан код товара (поле ERP код пустое) выводятся все рекламные акции на ТК")
	public void testReportAllActionsIfNoParameters() {
		String expectedErpCode = erpCode;
		erpCode = "";
		doReport();
		// Проверяем название акции в отчете
		Assert.assertTrue(htmlReportResults.containsValue("test_" + expectedErpCode), "Не выводятся существующие рекламные акции, если не задан код товара");
	}	
	
	@Test (	description = "Проверить, что \"Отчет по товарам в Рекламных акциях\" доступен для скачивания в формате pdf/xls",
			dataProvider = "Доступные форматы для скачивания"
			)
	public void testGoodOnTKSaveFormats(String reportFormat, String reportNamePattern){
		long fileSize = 0;
		fileSize =  adverstingConfigPage.saveReportFile(reportFormat, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	private void sendData() {
		goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}
	
	@DataProvider (name = "Доступные форматы для скачивания")
	public static Object[][] reportFormats(){
		return new  Object[][] {
			{PDFREPORT, "ProductReportInAction*.pdf"},
			{EXCELREPORT, "ProductReportInAction*.xls"}
		};
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

	private void doReport(){
		adverstingConfigPage.setGoodIDs(erpCode);
		doHTMLReport(adverstingConfigPage, true);
	}
	
}