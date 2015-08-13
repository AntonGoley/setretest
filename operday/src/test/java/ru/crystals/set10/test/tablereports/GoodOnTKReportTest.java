package ru.crystals.set10.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.AdvertisingActionType;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.GoodsOnTKConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.AdverstingActionsGenerator;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.utils.GoodGenerator.GOODTYPE_PIECE;


@Test (groups = {"centrum", "retail"})
public class GoodOnTKReportTest extends AbstractReportTest{

	GoodsOnTKConfigPage goodOnTKConfig;

	SoapRequestSender soapSender  = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	AdverstingActionsGenerator adverstingGenerator = new AdverstingActionsGenerator();
	
	Good good;
	AdvertisingActionType advertising;
	
	@BeforeClass
	public void navigateToGoodOnTKReports() {
		goodOnTKConfig =  navigateToReportConfig(
				TARGET_HOST_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				GoodsOnTKConfigPage.class, 
				TAB_OTHER, 
				REPORT_NAME_GOOD_ON_TK);
		
		soapSender.setSoapServiceIP(TARGET_HOST);
		
		/* сгенерить и отправить товар*/
		good = goodGenerator.generateGood(GOODTYPE_PIECE);
		soapSender.sendGood(good);
		
		/* сгенерить акцию */
		advertising = adverstingGenerator.generateAdversting(good.getMarkingOfTheGood());
		soapSender.sendAdvertising(advertising);
		
		/* сгенерить отчет */
		goodOnTKConfig.setErpCode(good.getErpCode());
		doHTMLReport(goodOnTKConfig, true);
	}	

	
	@Test (	description = "SRL-174. Проверить названия отчета и название колонок в шапке таблицы отчета по товарам на ТК", 
			dataProvider = "Шапка отчета Товары на ТК", dataProviderClass = TableReportsDataprovider.class)
	public void testGoodOnTKHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@DataProvider (name = "Данные отчета")
	private  Object[][] adverstingReportTableHead() {
		return new Object[][] {
		{"Название рекламной акции",  advertising.getName() },
		//TODO: почему в отчете обрезается код до 10 символов??
		{"Код акции",  advertising.getExternalCode().substring(0, 10)},
		{"id товара", good.getErpCode()},
		{"Штрих-код", good.getBarCodes().get(0).getCode()},
		};	
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
		fileSize =  goodOnTKConfig.exportFileData(chromeDownloadPath, reportNamePattern, goodOnTKConfig, reportFormat).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	@DataProvider (name = "Доступные форматы для скачивания")
	private static Object[][] reportFormats(){
		return new  Object[][] {
			{ReportConfigPage.PDFREPORT, "ProductReport_*.pdf"},
			{ReportConfigPage.EXCELREPORT, "ProductReport_*.xls"}
		};
	}
}
