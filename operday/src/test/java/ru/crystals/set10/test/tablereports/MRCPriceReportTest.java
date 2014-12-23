package ru.crystals.set10.test.tablereports;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.EXCELREPORT;

public class MRCPriceReportTest extends AbstractReportTest{
	
	ReportConfigPage MRCConfigPage;
	SoapRequestSender soapRequestSender;
	String mrcNameDataFilePattern = "${name}";
	String price = "${price}";

	
	@BeforeClass
	public void navigateToMRCReport() {
		MRCConfigPage =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				ReportConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_MRC_PRICE);
		doHTMLReport(MRCConfigPage, true);
	}	
	
	@Test (	groups = "MRC_Report_Smoke",
			description = "SRL-360. Проверить названия отчета и название колонок в шапке таблицы прейскуранта на табачные изделия", 
			dataProvider = "Шапка отчета MRC", dataProviderClass = TableReportsDataprovider.class)
	public void testMRCHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test ( groups = "MRC_Report_Smoke",
			description = "SRL-360. Проверить, что \"Прейскурант на тобачные изделия\" доступен для скачивания в формате xls"
			)
	public void testMRCReportSaveXls(){
		long fileSize = 0;
		String reportNamePattern = "TobaccoPrice*.xls";
		fileSize =  MRCConfigPage.exportFileData(chromeDownloadPath, reportNamePattern, MRCConfigPage, EXCELREPORT).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke",
			description = "SRL-360. В прейскуранте на табачные изделия проверить, что отображается 1-я цена, в случае, если при импорте отсутсвует plugin-property mrc")
	public void test1stPriceSetIfNoMRCPlugin(){
		String goodName;
		String request = DisinsectorTools.getFileContentAsString("mrc_report/good_mrc_no_mrc_plugin.txt"); 
		String first_price =  getPrice() +  ".11";
		goodName = setPriceAndSendRequest(request, first_price);
		
		doHTMLReport(MRCConfigPage, true);
		Assert.assertTrue(htmlReportResults.containsValue(goodName), "Не найден товар в отчете " + goodName);
		Assert.assertTrue(htmlReportResults.containsValue(first_price.replace(".", ",")), "Не найдена первая цена МРЦ " + first_price);
		htmlReportResults.removeValue(first_price);
		Assert.assertTrue(htmlReportResults.containsValue(first_price.replace(".", ",")), "Не найдена первая ПЦ" + first_price);
		
	}
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke",
			description = "SRL-360. В прейскуранте на табачные изделия проверить, что МРЦ и ЦП заполняются, в случае, если они приходят в plugin-property")
	public void testMRCandCPSetIfAllInPlugin(){
		String goodName;
		String request = DisinsectorTools.getFileContentAsString("mrc_report/good_mrc_prices.txt"); 
		String mrc_price =  getPrice() +  ".55";
		String sale_price =  getPrice() +  ".99";
		
		goodName = setPriceAndSendRequest(request, mrc_price + ";" + sale_price);
		doHTMLReport(MRCConfigPage, true);

		Assert.assertTrue(htmlReportResults.containsValue(goodName), "Не найден товар в отчете " + goodName);
		Assert.assertTrue(htmlReportResults.containsValue(mrc_price.replace(".", ",")), "Не найдена цена МРЦ" + mrc_price);
		Assert.assertTrue(htmlReportResults.containsValue(sale_price.replace(".", ",")), "Не найдена цена продажи " + sale_price);
	}
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke",
			description = "SRL-360. В прейскуранте на табачные изделия проверить, что МРЦ=ЦП если в plugin-property не приходит цена продажи")
	public void testMRCEqualsCPIfNoCPInPlugin(){
		String goodName;
		String request = DisinsectorTools.getFileContentAsString("mrc_report/good_mrc_prices.txt"); 
		String mrc_price =  getPrice() +  ".55";
		
		goodName = setPriceAndSendRequest(request, mrc_price);
		doHTMLReport(MRCConfigPage, true);

		Assert.assertTrue(htmlReportResults.containsValue(goodName), "Не найден товар в отчете " + goodName);
		Assert.assertTrue(htmlReportResults.containsValue(mrc_price.replace(".", ",")), "Не найдена цена МРЦ (или ПЦ)" + mrc_price);
		htmlReportResults.removeValue(mrc_price);
		Assert.assertTrue(htmlReportResults.containsValue(mrc_price.replace(".", ",")), "Не найдена цена МРЦ(или ПЦ)" + mrc_price);
		
	}
	
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke", alwaysRun = true,
			description = "SRL-360. В прейскуранте на табачные изделия есть все МРЦ на данный товар, перечисленные в plugin-property")
	public void testAllMRCInPlugin(){
		int totalMRCInDataFile = 4;
		String request = DisinsectorTools.getFileContentAsString("mrc_report/good_mrc_4_positions.txt"); 
		String mrc_good_name = setPriceAndSendRequest(request, "");

		doHTMLReport(MRCConfigPage, true);
		
		int counter = 0;
		while (htmlReportResults.containsValue(mrc_good_name)){
			counter++;
			htmlReportResults.removeValue(mrc_good_name);
		}

		Assert.assertEquals(totalMRCInDataFile, counter, "Неверное количество МРЦ в отчете для товара " + mrc_good_name);
	}
	
	private String setPriceAndSendRequest(String request, String mrc_price){
		String mrc_good_name = "Tabaco_" + String.valueOf(new Date().getTime());
		soapRequestSender = new SoapRequestSender();
		soapRequestSender.setSoapServiceIP(Config.RETAIL_HOST);
		soapRequestSender.sendGoods(request
				.replace(mrcNameDataFilePattern, mrc_good_name)
				.replace(price, mrc_price)
				, soapRequestSender.generateTI());
		return mrc_good_name;
	}
	
	private String getPrice(){
		return String.valueOf((new Date().getTime())).substring(8, 13).replaceFirst("0", "1"); //замещаем первый 0, чтобы цена не начиналась с 0
	}
}


