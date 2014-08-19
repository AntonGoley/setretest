package ru.crystals.set10.test.tablereports;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
//import ru.crystals.pos.check.PositionEntity;
//import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.CheckGenerator;
import static ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;


public class RefundChecksReportTest extends AbstractTest{
	LoginPage loginPage;
	MainPage mainPage;
	TableReportPage tableReportsPage;
	AbstractReportConfigPage RefundChecksConfigPage;
	HTMLRepotResultPage htmlReportResults;

	
	//@BeforeClass
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
	
	
	@Test
	public void testSendCheck(){
//		CheckGenerator cheGenerator = new CheckGenerator(Config.RETAIL_HOST, 2103, 1);
//		PurchaseEntity pe = (PurchaseEntity) cheGenerator.nextPurchase();
//		cheGenerator.logCheckEntities(pe);
	}
	
//	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по возвратам", 
//			alwaysRun = true,
//			dataProvider = "Шапка отчета по возвратам", dataProviderClass = TableReportsDataprovider.class)
	public void testPLUTKHTMLReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
}
