package ru.crystals.disinsector2.test.tablereports;

import java.util.Date;

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
import ru.crystals.test2.utils.DisinsectorTools;
import ru.crystals.test2.utils.SoapRequestSender;
import static ru.crystals.test2.operDay.tableReports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.test2.operDay.tableReports.TableReportPage.*;


public class PriceCheckerReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	PriceCheckerConfigPage priceCheckerConfig;
	HTMLRepotResultPage htmlReportResults;

	SoapRequestSender soapSender  = new SoapRequestSender();
	String ti = soapSender.generateTI();;
	String erpCode = 47 + ti;
	String barCode = "78" + ti;
	String mac = "mac_" +  new Date().getTime();
	
	
	@BeforeClass
	public void navigateToPriceCheckerReports() {
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		// Послать товар, который будет проверен на прайсчекере
		sendGoodData();
		// Послать запрос к прайсчекеру
		sendPriceCheckerData();
		priceCheckerConfig = tableReportsPage.openReportConfigPage(PriceCheckerConfigPage.class, TAB_OTHER, REPORT_NAME_PRICE_CHECKER);
		doReport();
	}	
	
	private void sendGoodData() {
		String goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}
	
	private void sendPriceCheckerData(){
		soapSender.sendPriceCheckerRequest(mac, barCode);
		DisinsectorTools.delay(5000);
	}
	
	public void doReport(){
		htmlReportResults = priceCheckerConfig.generateReport(HTMLREPORT);
		// закрыть окно отчета
		priceCheckerConfig.switchWindow(true);
	}
	
	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по прайсчекерам", 
			alwaysRun = true,
			dataProvider = "Шапка отчета Прайс чекеры", dataProviderClass = TableReportsDataprovider.class, 
			priority = 1)
	public void testPricechekerHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
	
	@Test (	description = "Проверить, что данные от прайсчекера приходят в отчет по прайсчекерам", 
			alwaysRun = true, 
			priority = 2
			)
	public void testPricechekerHTMLReportData(){
		doReport();
		Assert.assertTrue(htmlReportResults.containsValue(mac), "В отчете не отображается информация о мак адресе " + mac);
		Assert.assertTrue(htmlReportResults.containsValue(barCode), "В отчете не отображается информация о бар коде товара " + barCode);
		getDriver().navigate().refresh();
	}

	
	
}
