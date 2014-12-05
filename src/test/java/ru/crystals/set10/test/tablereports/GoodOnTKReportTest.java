package ru.crystals.set10.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.GoodsOnTKConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;


public class GoodOnTKReportTest extends AbstractReportTest{

	GoodsOnTKConfigPage goodOnTKConfig;
	String goodRequest;
	String adverstingRequest;

	static SoapRequestSender soapSender  = new SoapRequestSender();
	static String ti = soapSender.generateTI();
	static String  erpCode = 47 + ti;
	static String barCode = 78 + ti;
	
	@BeforeClass
	public void navigateToGoodOnTKReports() {
		goodOnTKConfig =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				GoodsOnTKConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_GOOD_ON_TK);
		
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		
		// послать товар и акцию
		sendGoodData();
		sendAdverstingForGood();
		
		// сгенерить отчет
		goodOnTKConfig.setErpCode(erpCode);
		doHTMLReport(goodOnTKConfig, true);
	}	

	
	@Test (	description = "SRL-174. Проверить названия отчета и название колонок в шапке таблицы отчета по товарам на ТК", 
			dataProvider = "Шапка отчета Товары на ТК", dataProviderClass = TableReportsDataprovider.class)
	public void testGoodOnTKHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test (	description = "SRL-174. Проверить наличие данных в отчете по товарам на ТК", 
			dataProvider = "Данные отчета")
	public void testGoodOnTKHTMLReportData(String field, String value){
		log.info(field);
		Assert.assertTrue(htmlReportResults.containsValue(value), "Неверное значение поля в отчете по ТК: " + field);
	}
	
	@Test (	description = "SRL-174. Проверить, что \"Отчет по товару на ТК\" доступен для скачивания в формате pdf/xls",
			dataProvider = "Доступные форматы для скачивания"
			)
	public void testGoodOnTKSaveFormats(String reportFormat, String reportNamePattern){
		long fileSize = 0;
		fileSize =  goodOnTKConfig.saveReportFile(reportFormat, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
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
			{ReportConfigPage.PDFREPORT, "ProductReport_*.pdf"},
			{ReportConfigPage.EXCELREPORT, "ProductReport_*.xls"}
		};
	}
	
	private void sendGoodData() {
		ti = soapSender.generateTI();
		log.info("Загрузить товар с erpCode = " + erpCode + ", barCode = " + barCode);
		goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
	}
	
	private void sendAdverstingForGood() {
	// завести рекламную акцию на товар с erpCode
		ti = soapSender.generateTI();
		log.info("Добавить рекламную акцию для товара с erpCode = " + erpCode + ", barCode = " + barCode);
		adverstingRequest = DisinsectorTools.getFileContentAsString("adversting.txt");
		soapSender.sendAdversting(String.format(adverstingRequest, erpCode, ti),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
		
	}
}
