package ru.crystals.set10.test.tablereports;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.utils.SoapRequestSender.RETURN_MESSAGE_CORRECT;

public class MRCPriceReportTest extends AbstractReportTest{
	
	AbstractReportConfigPage MRCConfigPage;
	SoapRequestSender soapRequestSender;
	String mrcNameDataFilePattern = "${name}";
	String price = "${price}";

	
	@BeforeClass
	public void navigateToPLUReport() {
		MRCConfigPage =  navigateToReportConfig(
				Config.RETAIL_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				AbstractReportConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_MRC_PRICE);
		doHTMLReport(MRCConfigPage);
	}	
	
	@Test (	groups = "MRC_Report_Smoke",
			description = "Проверить названия отчета и название колонок в шапке таблицы прейскуранта на табачные изделия", 
			dataProvider = "Шапка отчета MRC", dataProviderClass = TableReportsDataprovider.class)
	public void testMRCHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test ( groups = "MRC_Report_Smoke",
			description = "Проверить, что \"Прейскурант на тобачные изделия\" доступен для скачивания в формате xls"
			)
	public void testMRCReportSaveXls(){
		long fileSize = 0;
		String reportNamePattern = "TobaccoPrice*.xls";
		fileSize =  MRCConfigPage.saveReportFile(AbstractReportConfigPage.EXCELREPORT, chromeDownloadPath, reportNamePattern).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@Test (	enabled = false,
			dependsOnGroups = "MRC_Report_Smoke",
			description = "В прейскуранте на табачные изделия проверить, что отображается 1-я цена, в случае, если при импорте нет plugin-property mrc")
	public void test1stPriceSetIfNoMRCPlugin(){
		//Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke",
			description = "В прейскуранте на табачные изделия проверить, что МРЦ и ЦП заполняются, в случае, если они приходят в plugin-property")
	public void testMRCandCPSetIfAllInPlugin(){
		String goodName;
		String request = DisinsectorTools.getFileContentAsString("MRC_Report/good_mrc_prices.txt"); 
		String mrc_price = String.valueOf((new Date().getTime())).substring(8, 13) +  ".55";
		String sale_price = String.valueOf((new Date().getTime())).substring(8, 13) +  ".99";
		
		goodName = setPriceAndSendRequest(request, mrc_price + ";" + sale_price);
		doHTMLReport(MRCConfigPage);

		Assert.assertTrue(htmlReportResults.containsValue(goodName), "Не найден товар в отчете " + goodName);
		Assert.assertTrue(htmlReportResults.containsValue(mrc_price.replace(".", ",")), "Не найдена цена МРЦ" + mrc_price);
		Assert.assertTrue(htmlReportResults.containsValue(sale_price.replace(".", ",")), "Не найдена цена продажи " + sale_price);
	}
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke",
			description = "В прейскуранте на табачные изделия проверить, что МРЦ=ЦП если в plugin-property не приходит цена продажи")
	public void testMRCEqualsCPIfNoCPInPlugin(){
		String goodName;
		String request = DisinsectorTools.getFileContentAsString("MRC_Report/good_mrc_prices.txt"); 
		String mrc_price = String.valueOf((new Date().getTime())).substring(8, 13) +  ".55";
		
		goodName = setPriceAndSendRequest(request, mrc_price);
		doHTMLReport(MRCConfigPage);

		Assert.assertTrue(htmlReportResults.containsValue(goodName), "Не найден товар в отчете " + goodName);
		Assert.assertTrue(htmlReportResults.containsValue(mrc_price.replace(".", ",")), "Не найдена цена МРЦ (или ПЦ)" + mrc_price);
		htmlReportResults.removeValue(mrc_price);
		Assert.assertTrue(htmlReportResults.containsValue(mrc_price.replace(".", ",")), "Не найдена цена МРЦ(или ПЦ)" + mrc_price);
		
	}
	
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke", alwaysRun = true,
			description = "В прейскуранте на табачные изделия есть все МРЦ на данный товар, перечисленные в plugin-property")
	public void testAllMRCInPlugin(){
		int totalMRCInDataFile = 4;
		String request = DisinsectorTools.getFileContentAsString("MRC_Report/good_mrc_4_positions.txt"); 

		String mrc_good_name = "Tabaco_" + String.valueOf(new Date().getTime());
		soapRequestSender = new SoapRequestSender();
		soapRequestSender.setSoapServiceIP(Config.RETAIL_HOST);
		soapRequestSender.sendGoods(request.replace(mrcNameDataFilePattern, mrc_good_name), soapRequestSender.generateTI());
		soapRequestSender.assertSOAPResponse(RETURN_MESSAGE_CORRECT, soapRequestSender.getTI());
		doHTMLReport(MRCConfigPage);
		
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
		soapRequestSender.assertSOAPResponse(RETURN_MESSAGE_CORRECT, soapRequestSender.getTI());
		return mrc_good_name;
	}
}


