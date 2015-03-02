package ru.crystals.set10.test.tablereports;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.PriceCheckerConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.*;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;

@Test (groups = "centrum")
public class PriceCheckerReportTest extends AbstractReportTest{

	PriceCheckerConfigPage priceCheckerConfig;

	SoapRequestSender soapSender  = new SoapRequestSender();
	String ti = soapSender.generateTI();;
	String erpCode = 47 + ti;
	String barCode = "78" + ti;
	String mac = "mac_" +  new Date().getTime();
	
	@BeforeClass
	public void navigateToProceCheckerReport() {
		priceCheckerConfig =  navigateToReportConfig(
				TARGET_HOST_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				PriceCheckerConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_PRICE_CHECKER);
		soapSender.setSoapServiceIP(TARGET_HOST);
		// Послать товар, который будет проверен на прайсчекере
		sendGoodData();
		// Послать запрос к прайсчекеру
		sendPriceCheckerData();
		doHTMLReport(priceCheckerConfig, true);
	}	
	
	@Test (	description = "SRL-176. Проверить название отчета и название колонок в шапке таблицы отчета по прайсчекерам", 
			alwaysRun = true,
			dataProvider = "Шапка отчета Прайс чекеры", dataProviderClass = TableReportsDataprovider.class)
	public void testPricechekerHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	//TODO: валидировать последнюю строку полностью
	@Test (	description = "SRL-176. Проверить, что данные от прайсчекера приходят в отчет по прайсчекерам"
			)
	public void testPricechekerHTMLReportData(){
		Assert.assertTrue(htmlReportResults.containsValue(mac), "В отчете не отображается информация о мак адресе " + mac);
		Assert.assertTrue(htmlReportResults.containsValue(barCode), "В отчете не отображается информация о бар коде товара " + barCode);
	}

	@Test (	description = "SRL-176. Проверить, что отчет \"Отчёт для Прайс чекеров\" доступен для скачивания в формате xls",
			dataProvider = "Доступные форматы для скачивания")
	public void testPricechekerSaveFormats(String reportFormat, String reportNamePattern){
		long fileSize = 0;
		fileSize =  priceCheckerConfig.exportFileData(chromeDownloadPath, reportNamePattern, priceCheckerConfig, EXCELREPORT).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@DataProvider (name = "Доступные форматы для скачивания")
	public static Object[][] reportFormats(){
		return new  Object[][] {
			{EXCELREPORT, "PriceCheckerReport_*.xls"}
		};
	}
	
	private void sendGoodData() {
		String goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
	}
	
	private void sendPriceCheckerData(){
		soapSender.sendPriceCheckerRequest(mac, barCode);
		DisinsectorTools.delay(5000);
	}
}
