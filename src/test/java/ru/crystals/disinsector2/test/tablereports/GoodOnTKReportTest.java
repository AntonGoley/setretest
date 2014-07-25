package ru.crystals.disinsector2.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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
	
	SoapRequestSender soapSender  = new SoapRequestSender();
	String ti = soapSender.generateTI();;
	String erpCode = 47 + ti;

	
	@BeforeClass
	public void navigateToPriceCheckerReports() {
		mainPage = new LoginPage(getDriver()).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		goodOnTKConfig = tableReportsPage.openReportConfigPage(GoodsOnTKConfigPage.class, TAB_OTHER, REPORT_NAME_GOOD_ON_TK);
		soapSender.setSoapServiceIP(Config.CENTRUM_HOST);
		sendData();
		goodOnTKConfig.setErpCode(erpCode);
		doReport();
	}	
	
	private void sendData() {
		goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, erpCode),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	}
	
	public void doReport(){
		htmlReportResults = goodOnTKConfig.generateReport(HTMLREPORT);
		// закрыть окно отчета
		goodOnTKConfig.switchWindow(true);
	}
	
	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по товарам на ТК", 
			alwaysRun = true,
			dataProvider = "Шапка отчета Товары на ТК", dataProviderClass = TableReportsDataprovider.class)
	public void testAdverstingHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}

	
	
}
