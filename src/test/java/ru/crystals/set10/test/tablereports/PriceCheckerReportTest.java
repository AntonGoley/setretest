package ru.crystals.set10.test.tablereports;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.PriceCheckerConfigPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;


public class PriceCheckerReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	PriceCheckerConfigPage priceCheckerConfig;
	HTMLRepotResultPage htmlReportResults;

	SoapRequestSender soapSender  = new SoapRequestSender();
	String ti = soapSender.generateTI();;
	String erpCode = 47 + ti;
	String barCode = "78" + ti;
	String mac = "mac_" +  new Date().getTime();
	
	
	@BeforeClass
	public void navigateToPriceCheckerReports() {
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		// Послать товар, который будет проверен на прайсчекере
		sendGoodData();
		// Послать запрос к прайсчекеру
		sendPriceCheckerData();
		priceCheckerConfig = tableReportsPage.openReportConfigPage(PriceCheckerConfigPage.class, TAB_OTHER, REPORT_NAME_PRICE_CHECKER);
		doReport();
	}	
	
	@Test (	description = "Проверить название отчета и название колонок в шапке таблицы отчета по прайсчекерам", 
			alwaysRun = true,
			dataProvider = "Шапка отчета Прайс чекеры", dataProviderClass = TableReportsDataprovider.class)
	public void testPricechekerHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	//TODO: валидировать последнюю строку полностью
	@Test (	description = "Проверить, что данные от прайсчекера приходят в отчет по прайсчекерам"
			)
	public void testPricechekerHTMLReportData(){
		Assert.assertTrue(htmlReportResults.containsValue(mac), "В отчете не отображается информация о мак адресе " + mac);
		Assert.assertTrue(htmlReportResults.containsValue(barCode), "В отчете не отображается информация о бар коде товара " + barCode);
	}

	@Test (	description = "Проверить, что отчет \"Отчёт для Прайс чекеров\" доступен для скачивания в формате xls",
			dataProvider = "Доступные форматы для скачивания")
	public void testPricechekerSaveFormats(String reportFormat, String reportNamePattern){
		long fileSize = 0;
		fileSize =  priceCheckerConfig.saveReportFile(reportFormat, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@DataProvider (name = "Доступные форматы для скачивания")
	public static Object[][] reportFormats(){
		return new  Object[][] {
			{AbstractReportConfigPage.EXCELREPORT, "PriceCheckerReport_*.xls"}
		};
	}
	
	private void sendGoodData() {
		String goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}
	
	private void sendPriceCheckerData(){
		soapSender.sendPriceCheckerRequest(mac, barCode);
		DisinsectorTools.delay(5000);
	}
	
	public void doReport(){
		htmlReportResults = priceCheckerConfig.generateReport(HTMLREPORT);
		// закрыть окно отчета
		priceCheckerConfig.switchWindow(true);
	}
}
