package ru.crystals.set10.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.GoodsOnTKConfigPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;


public class GoodOnTKReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	GoodsOnTKConfigPage goodOnTKConfig;
	HTMLRepotResultPage htmlReportResults;
	String goodRequest = "";
	
	static SoapRequestSender soapSender  = new SoapRequestSender();
	static String ti = soapSender.generateTI();
	static String  erpCode = 47 + ti;
	static String barCode = "78" + ti;
	
	@BeforeClass
	public void navigateToGoodOnTKReports() {
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		goodOnTKConfig = tableReportsPage.openReportConfigPage(GoodsOnTKConfigPage.class, TAB_OTHER, REPORT_NAME_GOOD_ON_TK);
		
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		sendGoodData();
		sendAdverstingForGood();
		goodOnTKConfig.setErpCode(erpCode);
		doReport();
	}	
	
	private void sendGoodData() {
		log.info("Загрузить товар с erpCode = " + erpCode + ", barCode = " + barCode);
		goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}
	
	private void sendAdverstingForGood() {
	// завести рекламную акцию на товар с erpCode
		ti = soapSender.generateTI();
		soapSender = new SoapRequestSender();
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		String adverstingRequest = DisinsectorTools.getFileContentAsString("adversting.txt");
		soapSender.sendAdversting(String.format(adverstingRequest, erpCode, ti),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}		
	
	public void doReport(){
		htmlReportResults = goodOnTKConfig.generateReport(HTMLREPORT);
		goodOnTKConfig.switchWindow(true);
	}
	
	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по товарам на ТК", 
			//alwaysRun = true,
			dataProvider = "Шапка отчета Товары на ТК", dataProviderClass = TableReportsDataprovider.class)
	public void testGoodOnTKHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test (	description = "Проверить наличие данных в отчете по товарам на ТК", 
			//alwaysRun = true,
			dataProvider = "Данные отчета")
	public void testGoodOnTKHTMLReportData(String field, String value){
		log.info(field);
		Assert.assertTrue(htmlReportResults.containsValue(value), "Неверное значение поля в отчете по ТК: " + field);
	}
	
	@Test (	description = "Проверить, что отчет доступен для скачивания в формате pdf/xls",
			dataProvider = "Доступные форматы для скачивания"
			)
	public void testGoodOnTKSaveFormats(String reportFormat, String reportNamePattern){
		long fileSize = 0;
		fileSize =  goodOnTKConfig.saveReportFile(reportFormat, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@DataProvider (name = "Данные отчета")
	public static Object[][] adverstingReportTableHead() {
		return new Object[][] {
		// TODO: Сейчас условие акции такое же как и название акции		
		{"Название рекламной акции", "test_" + erpCode},
		// TODO: разобраться, почему в сьюте на товар генерятся 2 акции
		//{"id товара", erpCode},
		{"Штрих-код", barCode},
		//{"Код акции", "TEST_" + ti},
		};	
	}
	
	@DataProvider (name = "Доступные форматы для скачивания")
	public static Object[][] reportFormats(){
		return new  Object[][] {
			{AbstractReportConfigPage.PDFREPORT, "ProductReport*.pdf"},
			{AbstractReportConfigPage.EXCELREPORT, "ProductReport*.xls"}
		};
	}
}
