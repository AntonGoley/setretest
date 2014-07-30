package ru.crystals.disinsector2.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.disinsector2.test.AbstractTest;
import ru.crystals.disinsector2.test.dataproviders.TableReportsDataprovider;
import ru.crystals.test2.basic.LoginPage;
import ru.crystals.test2.basic.MainPage;
import ru.crystals.test2.config.Config;
import ru.crystals.test2.operDay.tableReports.AbstractReportConfigPage;
import ru.crystals.test2.operDay.tableReports.TableReportPage;
import ru.crystals.test2.operDay.tableReports.HTMLRepotResultPage;
import static ru.crystals.test2.operDay.tableReports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.test2.operDay.tableReports.TableReportPage.*;


public class RefundChecksReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	AbstractReportConfigPage RefundChecksConfigPage;
	HTMLRepotResultPage htmlReportResults;

	
	@BeforeClass
	public void navigateToRefundChecksReports() {
		mainPage = new LoginPage(getDriver(),  Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		RefundChecksConfigPage = tableReportsPage.openReportConfigPage(AbstractReportConfigPage.class, TAB_OTHER, REPORT_NAME_REFUND_CHECKS);
		doReport();
	}	
	
	public void doReport(){
		htmlReportResults = RefundChecksConfigPage.generateReport(HTMLREPORT);
		RefundChecksConfigPage.switchWindow(true);
	}
	
	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по возвратам", 
			alwaysRun = true,
			dataProvider = "Шапка отчета по возвратам", dataProviderClass = TableReportsDataprovider.class)
	public void testPLUTKHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
}
