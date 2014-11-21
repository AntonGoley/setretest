package ru.crystals.set10.test.tablereports;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.WrongAdverstingPriveConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.*;


public class WrongAdverstingPriceTest extends AbstractReportTest{
	
	WrongAdverstingPriveConfigPage reportConfigPage;
	SoapRequestSender soapSender = new SoapRequestSender();
	
	
	private static HashMap<String, String> price4BiggerThanPrice2;
	private static HashMap<String, String> price3BiggerThanPrice1;
	private static HashMap<String, String> price3BiggerThanPrice2;
	private static HashMap<String, String> price2BiggerThanPrice1;
	
	
	//@BeforeClass
	public void navigateToReport() {
		reportConfigPage =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				WrongAdverstingPriveConfigPage.class, 
				TAB_ADVERSTING, 
				REPORT_NAME_WRONG_ADVERSTING_PRICE);
	}	
	
	
	//@BeforeMethod(firstTimeOnly = true)
	public void refreshReport(){
		doHTMLReport(reportConfigPage, false);
	}
	
	private static void setTnputData(){
		 price4BiggerThanPrice2 =  new HashMap<String, String>();
			price4BiggerThanPrice2.put("${marking-of-the-good}","test");
			price4BiggerThanPrice2.put("${barcode}","test");
			price4BiggerThanPrice2.put("${price_2}","99.11");
			price4BiggerThanPrice2.put("${price_4}","100.10");
			price4BiggerThanPrice2.put("${adversting_identifier}","Price_4 > price_2");
			
		price3BiggerThanPrice1 =  new HashMap<String, String>();
			price3BiggerThanPrice1.put("${marking-of-the-good}","test");
			price3BiggerThanPrice1.put("${barcode}","test");
			price3BiggerThanPrice1.put("${price_3}","201.33");
			price3BiggerThanPrice1.put("${price_1}","201.32");
			price3BiggerThanPrice1.put("${adversting_identifier}","Price_3 > price_1");	
			
		price3BiggerThanPrice2 =  new HashMap<String, String>();
			price3BiggerThanPrice2.put("${marking-of-the-good}","test");
			price3BiggerThanPrice2.put("${barcode}","test");
			price3BiggerThanPrice2.put("${price_3}","301.33");
			price3BiggerThanPrice2.put("${price_2}","301.32");
			price3BiggerThanPrice2.put("${adversting_identifier}","Price_3 > price_2");			
			
		price2BiggerThanPrice1 =  new HashMap<String, String>();
			price2BiggerThanPrice1.put("${marking-of-the-good}","test");
			price2BiggerThanPrice1.put("${barcode}","test");
			price2BiggerThanPrice1.put("${price_3}","301.33");
			price2BiggerThanPrice1.put("${price_2}","301.32");
			price2BiggerThanPrice1.put("${adversting_identifier}","Price_3 > price_2");				
	}
	
	
	
	@DataProvider(name = "Цены")
	public Object[][] priceData(){
		setTnputData();
		return new Object[][]{
				{"Price_4> price_2", DisinsectorTools.getFileContentAsString("wrong_adversting_price_good.txt"), price4BiggerThanPrice2},
				{"Price_3> price_1", DisinsectorTools.getFileContentAsString("wrong_adversting_price_good.txt"), price3BiggerThanPrice1},
				{"Price_3> price_2", DisinsectorTools.getFileContentAsString("wrong_adversting_price_good.txt"), price3BiggerThanPrice2},
				{"Price_2> price_1", DisinsectorTools.getFileContentAsString("wrong_adversting_price_good.txt"), price2BiggerThanPrice1},
		};
	}
	
	@Test (	description = "SRTE-67. Проверить условие попадания рекламной цены в отчет на ТК",
			dataProvider = "Цены"
			)
	public void testAdverstingPrice(String description, String request, HashMap<String, String> params){
		soapSender.sendGoods(request, params);
	}
	
//	@Test (	description = "SRTE-67. Проверить названия отчета и название колонок в шапке таблицы отчета \"Некорректная акционная цена\"", 
//			dataProvider = "Некорректная акционная цена", dataProviderClass = TableReportsDataprovider.class)
//	public void testAdverstingPriceHTMLReportTableHead(String fieldName){
//		log.info(fieldName);
//		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
//	}
//	
//	@Test (	description = "SRTE-67. Проверить, что \"Некорректная акционная цена\" доступен для скачивания в формате xls"
//			)
//	public void testAdverstingPriceReportSaveFormats(){
//		long fileSize = 0;
//		String reportNamePattern = "IncorrectActionPrice*.xls";
//		fileSize =  reportConfigPage.saveReportFile(EXCELREPORT, chromeDownloadPath, reportNamePattern).length();
//		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
//		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
//	}
	
}
