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


public class PLUinWeightReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	AbstractReportConfigPage PLUConfigPage;
	HTMLRepotResultPage htmlReportResults;

	
	@BeforeClass
	public void navigateToPLUReport() {
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		tableReportsPage = mainPage.openOperDay().openTableReports();
		PLUConfigPage = tableReportsPage.openReportConfigPage(AbstractReportConfigPage.class, TAB_OTHER, REPORT_NAME_PLU_ON_WEIGHT);
		doReport();
	}	
	
	public void doReport(){
		htmlReportResults = PLUConfigPage.generateReport(HTMLREPORT);
		PLUConfigPage.switchWindow(true);
	}
	
	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по количеству PLU в весах", 
			alwaysRun = true,
			dataProvider = "Шапка отчета по количеству PLU в весах", dataProviderClass = TableReportsDataprovider.class)
	public void testPLUTKHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
}
