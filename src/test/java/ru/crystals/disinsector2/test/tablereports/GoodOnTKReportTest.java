package ru.crystals.disinsector2.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.disinsector2.test.AbstractTest;
import ru.crystals.disinsector2.test.dataproviders.TableReportsDataprovider;
import ru.crystals.test2.basic.LoginPage;
import ru.crystals.test2.basic.MainPage;
import ru.crystals.test2.config.Config;
import ru.crystals.test2.operDay.tableReports.GoodsOnTKConfigPage;
import ru.crystals.test2.operDay.tableReports.TableReportPage;
import ru.crystals.test2.operDay.tableReports.HTMLRepotResultPage;
import ru.crystals.test2.utils.DisinsectorTools;
import ru.crystals.test2.utils.SoapRequestSender;
import static ru.crystals.test2.operDay.tableReports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.test2.operDay.tableReports.TableReportPage.*;


public class GoodOnTKReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	GoodsOnTKConfigPage goodOnTKConfig;
	HTMLRepotResultPage htmlReportResults;
	String goodRequest = "";
	
	static SoapRequestSender soapSender  = new SoapRequestSender();
	static String ti = soapSender.generateTI();;
	static String  erpCode = 47 + ti;
	static String barCode = "78" + ti;
	
	@DataProvider (name = "Данные отчета")
	public static Object[][] adverstingReportTableHead() {
		return new Object[][] {
		{"Название рекламной акции", "test_" + erpCode},
		// TODO: разобраться, почему в сьюте на товар генерятся 2 акции
		//	{"id товара", erpCode},
		{"Штрих-код", barCode},
		};	
	}
	
	@BeforeClass
	public void navigateToGoodOnTKReports() {
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		goodOnTKConfig = tableReportsPage.openReportConfigPage(GoodsOnTKConfigPage.class, TAB_OTHER, REPORT_NAME_GOOD_ON_TK);
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		sendGoodData();
		sendAdverstingForGood();
		goodOnTKConfig.setErpCode(erpCode);
		doReport();
	}	
	
	private void sendGoodData() {
		log.info("Загрузить товар с erpCode = " + erpCode + ", barCode = " + barCode);
		goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}
	
	private void sendAdverstingForGood() {
	// завести рекламную акцию на товар с erpCode
		ti = soapSender.generateTI();
		soapSender = new SoapRequestSender();
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		String adverstingRequest = DisinsectorTools.getFileContentAsString("adversting.txt");
		soapSender.sendAdversting(String.format(adverstingRequest, erpCode, ti),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}		
	
	public void doReport(){
		htmlReportResults = goodOnTKConfig.generateReport(HTMLREPORT);
		goodOnTKConfig.switchWindow(true);
	}
	
	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по товарам на ТК", 
			alwaysRun = true,
			dataProvider = "Шапка отчета Товары на ТК", dataProviderClass = TableReportsDataprovider.class)
	public void testGoodOnTKHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	
	@Test (	description = "Проверить наличие данных в отчете по товарам на ТК", 
			alwaysRun = true,
			dataProvider = "Данные отчета")
	public void testGoodOnTKHTMLReportData(String field, String value){
		log.info(field);
		Assert.assertTrue(htmlReportResults.containsValue(value), "Неверное значение поля в отчете по ТК: " + field);
	}
	
	
}
