package ru.crystals.set10.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.PluginProperty;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.EXCELREPORT;
import static ru.crystals.set10.utils.GoodGenerator.GOODTYPE_CIGGY;

@Test(groups = "retail")
public class MRCPriceReportTest extends AbstractReportTest{
	
	ReportConfigPage MRCConfigPage;

	SoapRequestSender soapRequestSender = new SoapRequestSender(Config.RETAIL_HOST);
	GoodGenerator goodGenerator = new GoodGenerator();
	
	String mrcNameDataFilePattern = "${name}";
	String price = "${price}";
	
	Good good;
	
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
			description = "SRL-360. Проверить, что \"Прейскурант на табачные изделия\" доступен для скачивания в формате xls"
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
		good = goodGenerator.generateGood(GOODTYPE_CIGGY);
		soapRequestSender.sendGood(good);

		String first_price = goodGenerator.getPriceValue(good, 1L).toPlainString().replace(".", ",");
		
		doHTMLReport(MRCConfigPage, true);
		Assert.assertTrue(htmlReportResults.containsValue(good.getName()), "Не найден товар в отчете " + good.getName());
		
		Assert.assertTrue(htmlReportResults.containsValue(first_price), "Не найдена первая цена МРЦ " + first_price);
		htmlReportResults.removeValue(first_price);
		Assert.assertTrue(htmlReportResults.containsValue(first_price.replace(".", ",")), "Не найдена первая ПЦ" + first_price);
		
	}
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke",
			description = "SRL-360. В прейскуранте на табачные изделия проверить, что МРЦ и ЦП заполняются, в случае, если они приходят в plugin-property")
	public void testMRCandCPSetIfAllInPlugin(){
		good = goodGenerator.generateGood(GOODTYPE_CIGGY);
		
		String mrc_price = DisinsectorTools.randomMoney(100, ".");
		String sale_price = goodGenerator.getPriceValue(good, 1L).toPlainString();
		
		/*
		 * Добавить PluginProperty mrc,
		 * это свойство вложенное в PluginProperty:
		 * 	plugin-property key="mrc">
		 *		<plugin-property key="price" value="mrc_price;sale_price"/>
		 *	</plugin-property>
		 * 
		 */
		
		/*
		 * Создать PluginPropertyс ценами mrc
		 */
		PluginProperty mrc = generateMrcProperty(mrc_price + ";" + sale_price);
		
		PluginProperty property = new PluginProperty();
		property.getProperties().add(mrc);
		property.setKey("mrc");
		
		good.getPluginProperties().add(property);
		
		/*отправить товар с новым свойством*/
		soapRequestSender.sendGood(good);
		
		doHTMLReport(MRCConfigPage, true);

		Assert.assertTrue(htmlReportResults.containsValue(good.getName()), "Не найден товар в отчете " + good.getName());
		Assert.assertTrue(htmlReportResults.containsValue(mrc_price.replace(".", ",")), "Не найдена цена МРЦ" + mrc_price);
		Assert.assertTrue(htmlReportResults.containsValue(sale_price.replace(".", ",")), "Не найдена цена продажи " + sale_price);
	}
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke",
			description = "SRL-360. В прейскуранте на табачные изделия проверить, что МРЦ=ЦП если в plugin-property не приходит цена продажи")
	public void testMRCEqualsCPIfNoCPInPlugin(){

		String mrc_price =  DisinsectorTools.randomMoney(100, ".");
		good = goodGenerator.generateGood(GOODTYPE_CIGGY);
		
		/*
		 * В PluginProperty mrc не приходит цена продажи 
		 */
		PluginProperty mrc = generateMrcProperty(mrc_price);
		
		PluginProperty property = new PluginProperty();
		property.getProperties().add(mrc);
		property.setKey("mrc");
		good.getPluginProperties().add(property);
		
		soapRequestSender.sendGood(good);
		doHTMLReport(MRCConfigPage, true);

		Assert.assertTrue(htmlReportResults.containsValue(good.getName()), "Не найден товар в отчете " + good.getName());
		Assert.assertTrue(htmlReportResults.containsValue(mrc_price.replace(".", ",")), "Не найдена цена МРЦ (или ПЦ)" + mrc_price);
		htmlReportResults.removeValue(mrc_price);
		Assert.assertTrue(htmlReportResults.containsValue(mrc_price.replace(".", ",")), "Не найдена цена МРЦ(или ПЦ)" + mrc_price);
		
	}
	
	
	@Test (	dependsOnGroups = "MRC_Report_Smoke", alwaysRun = true,
			description = "SRL-360. В прейскуранте на табачные изделия есть все МРЦ на данный товар, перечисленные в plugin-property")
	public void testAllMRCInPlugin(){
		int totalMRCInDataFile = 4;
		
		good = goodGenerator.generateGood(GOODTYPE_CIGGY);
		
		PluginProperty property = new PluginProperty();
		property.setKey("mrc");
		
		
		for (int i=1; i<=4; i++){
			String mrc_price = DisinsectorTools.randomMoney(100, ".");
			String price = DisinsectorTools.randomMoney(100, ".");
			PluginProperty mrc = generateMrcProperty(mrc_price + ";" + price);
			property.getProperties().add(mrc);
		}
		
		good.getPluginProperties().add(property);
		soapRequestSender.sendGood(good);
		
		doHTMLReport(MRCConfigPage, true);
		
		int counter = 0;
		while (htmlReportResults.containsValue(good.getName())){
			counter++;
			htmlReportResults.removeValue(good.getName());
		}

		Assert.assertEquals(totalMRCInDataFile, counter, "Неверное количество МРЦ в отчете для товара " + good.getName());
	}
	
	private PluginProperty generateMrcProperty(String prices){
		PluginProperty mrc = new PluginProperty();
		mrc.setKey("price");
		mrc.setValue(prices);
		return mrc;
	}

}


