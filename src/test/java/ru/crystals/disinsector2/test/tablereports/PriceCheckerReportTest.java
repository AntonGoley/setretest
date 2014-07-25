package ru.crystals.disinsector2.test.tablereports;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.disinsector2.test.AbstractTest;
import ru.crystals.disinsector2.test.dataproviders.TableReportsDataprovider;
import ru.crystals.test2.basic.LoginPage;
import ru.crystals.test2.basic.MainPage;
import ru.crystals.test2.config.Config;
import ru.crystals.test2.operDay.tableReports.PriceCheckerConfigPage;
import ru.crystals.test2.operDay.tableReports.TableReportPage;
import ru.crystals.test2.operDay.tableReports.HTMLRepotResultPage;
import ru.crystals.test2.utils.SoapRequestSender;
import static ru.crystals.test2.operDay.tableReports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.test2.operDay.tableReports.TableReportPage.*;


public class PriceCheckerReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	PriceCheckerConfigPage priceCheckerConfig;
	String goodRequest = "";
	String adverstingRequest = "";
	HTMLRepotResultPage htmlReportResults;
	SoapRequestSender soapSender  = new SoapRequestSender();

	
	@BeforeClass
	public void navigateToPriceCheckerReports() {
		mainPage = new LoginPage(getDriver()).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		priceCheckerConfig = tableReportsPage.openReportConfigPage(PriceCheckerConfigPage.class, TAB_OTHER, REPORT_NAME_PRICE_CHECKER);
//		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
//		sendData();
		doReport();
	}	
	
	private void sendData() {
//		goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
//		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
//		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}
	
	public void doReport(){
		htmlReportResults = priceCheckerConfig.generateReport(HTMLREPORT);
		// закрыть окно отчета
		priceCheckerConfig.switchWindow(true);
	}
	
	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по прайсчекерам", 
			alwaysRun = true,
			dataProvider = "Шапка отчета Прайс чекеры", dataProviderClass = TableReportsDataprovider.class)
	public void testAdverstingHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}

	
	
}
