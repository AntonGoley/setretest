package ru.crystals.set10.test.tablereports;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.PriceCheckerConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.*;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.utils.GoodGenerator.*;


@Test (groups = "centrum")
public class PriceCheckerReportTest extends AbstractReportTest{

	PriceCheckerConfigPage priceCheckerConfig;
	Good good;
	String mac = "mac_" +  new Date().getTime();
	
	SoapRequestSender soapSender  = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	@BeforeClass
	public void navigateToProceCheckerReport() {
		priceCheckerConfig =  navigateToReportConfig(
				Config.CENTRUM_URL,
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				PriceCheckerConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_PRICE_CHECKER);
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		// Послать товар, который будет проверен на прайсчекере
		good = goodGenerator.generateGood(GOODTYPE_PIECE);
		soapSender.sendGood(good);
		
		// Послать запрос к прайсчекеру
		soapSender.sendPriceCheckerRequest(mac, good.getBarCodes().get(0).getCode());
		
		doHTMLReport(priceCheckerConfig, false);
	}	
	
	@Test (	description = "SRL-176. Проверить название отчета и название колонок в шапке таблицы отчета по прайсчекерам", 
			alwaysRun = true,
			dataProvider = "Шапка отчета Прайс чекеры", dataProviderClass = TableReportsDataprovider.class)
	public void testPricechekerHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@DataProvider (name = "reportData")
	private Object[][] reportFieldsValues(){
		return new Object[][]{
				{"Значение мак адреса ", mac, 4},
				{"Баркод товара ", good.getBarCodes().get(0).getCode(), 6},
				{"Код товара ", good.getMarkingOfTheGood(), 7},
				{"Наименование товара", good.getName(), 8},
		};
	}
	
	@Test (	description = "SRL-176. Проверить, что данные от прайсчекера приходят в отчет по прайсчекерам",
			dataProvider = "reportData")
	public void testPricechekerHTMLReportData(String field, String value, int columnNumber){
		log.info("Поле " + field);
		Assert.assertEquals(htmlReportResults.getLastLineColumnValue(columnNumber), value, "В отчете не отображается информация о " + field);
	}

	@Test (	description = "SRL-176. Проверить, что отчет \"Отчёт для Прайс чекеров\" доступен для скачивания в формате xls",
			priority = 2)
	public void testPricechekerSaveFormats(){
		String reportNamePattern = "PriceCheckerReport_*.xls";
		long fileSize = 0;
		
		priceCheckerConfig.switchWindow(true);
		fileSize =  priceCheckerConfig.exportFileData(chromeDownloadPath, reportNamePattern, priceCheckerConfig, EXCELREPORT).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}

}
