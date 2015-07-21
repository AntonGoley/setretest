package ru.crystals.set10.test.tablereports;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.AdvertisingActionType;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.operday.tablereports.AdverstingReportConfigPage;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.AdverstingActionsGenerator;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.*;
import static ru.crystals.set10.utils.GoodGenerator.GOODTYPE_PIECE;


@Test (groups = {"centrum"})
public class AdverstingTableReportTest extends AbstractReportTest {
	
	AdverstingReportConfigPage adverstingConfigPage;
	String goodRequest;
	String adverstingRequest;

	SoapRequestSender soapSender  = new SoapRequestSender(Config.CENTRUM_HOST);
	GoodGenerator goodGenerator = new GoodGenerator();
	AdverstingActionsGenerator adverstingGenerator = new AdverstingActionsGenerator();
	
	Good good;
	AdvertisingActionType advertising;
	
	@BeforeClass
	public void navigateToAdverstingReport() {
		
		adverstingConfigPage =  navigateToReportConfig(
				Config.CENTRUM_URL, 
				Config.MANAGER,
				Config.MANAGER_PASSWORD,
				AdverstingReportConfigPage.class, 
				TAB_ADVERSTING, 
				REPORT_NAME_ADVERSTING);
		
		/* сгенерить и отправить товар*/
		good = goodGenerator.generateGood(GOODTYPE_PIECE);
		soapSender.sendGood(good);
		
		/* сгенерить акцию и не отправлять*/
		advertising = adverstingGenerator.generateAdversting(good.getMarkingOfTheGood());
		
		doReport(good.getErpCode());
	}	
	
	@Test ( description = "SRL-182. Проверить название отчета и названия колонок в шапке таблицы отчета по рекламным акциям", 
			dataProvider = "Шапка отчета Рекламные акции", dataProviderClass = TableReportsDataprovider.class)
	public void testAdverstingHTMLReportTableHead(String fieldName){
		log.info("Проверить название/наличие поля: " + fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	@Test(description = "SRL-182. Проверить, что генерируется пустой отчет, если на товар не заведена рекламная акция")
	public void testEmptyAdverstingHTMLReport() {
		log.info("Количество полей в отчете по рекламным акциям " +  htmlReportResults.getReportSize());
		Assert.assertTrue(htmlReportResults.getReportSize() < 20, "Сгененированный отчет не пустой");
	}	
	
	@Test (dependsOnMethods = "testEmptyAdverstingHTMLReport",
			description = "SRL-182. Проверить, наличие товара в отчете, если на него заведена рекламная акция, действующая сегодня", 
			alwaysRun = true)
	public void testGoodInAdverstingReport(){

		/* отправить РА*/
		soapSender.sendAdversting(advertising);
		doReport(good.getErpCode());
		Assert.assertTrue(htmlReportResults.containsValue(good.getErpCode()), "Отсутствует ERP код в отчете");
	}
	
	@Test(	dependsOnMethods = "testGoodInAdverstingReport",
			description = "SRL-182. Если не задан код товара (поле ERP код пустое) выводятся все рекламные акции на ТК")
	public void testReportAllActionsIfNoParameters() {
		doReport("");
		// Проверяем название акции в отчете
		Assert.assertTrue(htmlReportResults.containsValue(advertising.getName()), "Не выводятся существующие рекламные акции, если не задан код товара");
	}	
	
	@Test (	description = "SRL-182. Проверить, что \"Отчет по товарам в Рекламных акциях\" доступен для скачивания в формате pdf/xls",
			dataProvider = "Доступные форматы для скачивания"
			)
	public void testAdverstingSaveFormats(String reportFormat, String reportNamePattern){
		long fileSize = 0;
		fileSize =  adverstingConfigPage.exportFileData(chromeDownloadPath, reportNamePattern, adverstingConfigPage, reportFormat).length();
		log.info("Размер сохраненного файла: " + reportNamePattern + " равен " +  fileSize);
		Assert.assertTrue(fileSize > 0, "Файл отчета сохранился некорректно");
	}
	
	
	@DataProvider (name = "Доступные форматы для скачивания")
	public static Object[][] reportFormats(){
		return new  Object[][] {
			{PDFREPORT, "ProductReportInAction*.pdf"},
			{EXCELREPORT, "ProductReportInAction*.xls"}
		};
	}
		
	
	@Test(	enabled = false, dependsOnMethods = "testGoodInReport",
			description = "Если не указаны магазины, отчет генерится для всей сети (все магазины)", 
		alwaysRun = true) 
	public void testWithEmptyShop(){

	}
	
	
	@Test(enabled = false, description = "Если указан магазин, отчет генерится только для этого магазина")
	public void testParticularShop() {
	}
	
	@Test(enabled = false, description = "Проверить генерацию отчета для двух ERP кодов, указанных через запятую")
	public void testTwoERPCodes() {
	}

	private void doReport(String erpCode){
		if (!erpCode.equals("")){
			adverstingConfigPage.setGoodIDs(erpCode);
		} else {
			adverstingConfigPage.clearGoodField();
		}
		doHTMLReport(adverstingConfigPage, true);
	}
	
}
