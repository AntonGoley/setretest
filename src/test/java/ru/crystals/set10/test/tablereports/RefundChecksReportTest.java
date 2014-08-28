package ru.crystals.set10.test.tablereports;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.HTMLRepotResultPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.CheckGenerator;
import static ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;


public class RefundChecksReportTest extends AbstractTest{
	
	MainPage mainPage;
	LoginPage loginPage;
	AbstractReportConfigPage refundChecksConfigPage;
	HTMLRepotResultPage htmlReportResults;
	CheckGenerator checkGenerator = new CheckGenerator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), 1);
	
	
	@BeforeClass
	public void navigateToRefundChecksReports() {
		mainPage = new LoginPage(getDriver(),  Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		refundChecksConfigPage = mainPage.openOperDay().openTableReports().openReportConfigPage(AbstractReportConfigPage.class, TAB_OTHER, REPORT_NAME_REFUND_CHECKS);
	}	
	
	public void doReport(){
		htmlReportResults = refundChecksConfigPage.generateReport(HTMLREPORT);
		refundChecksConfigPage.switchWindow(true);
	}
	
	public PurchaseEntity sendRefundCheck(){
		PurchaseEntity pe = (PurchaseEntity) checkGenerator.nextPurchase();
		PositionEntity pos = pe.getPositions().get(0);
		return (PurchaseEntity) checkGenerator.nextRefundCheck(pe, pos, pos.getQnty(), false);
	} 
	
	
	@Test (	description = "Проверить, что в отчет по возвратам попал новый возврартный чек (возврат позиции)")
	public void testRefundReportContainsRefunds(){
		htmlReportResults = refundChecksConfigPage.generateReport(HTMLREPORT);
		int reportSizeBefore = htmlReportResults.getReportSize();
		
		PurchaseEntity refundCheck = sendRefundCheck();
		
		getDriver().navigate().refresh();
		htmlReportResults = new HTMLRepotResultPage(getDriver());
		refundChecksConfigPage.switchWindow(true);
		Assert.assertTrue(reportSizeBefore < htmlReportResults.getReportSize(), "В отчете не отображается новый возвратный чек");
	}
	
	
	@Test (	description = "Проверить, что отчет по возвратам содержит произвольный возврартный чек (произвольный возврат)", 
			alwaysRun = true,
			enabled = false)
	public void testRefundReportContainsRefundsArbitraryReturn(){
	}

	
	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по возвратам", 
			alwaysRun = true,
			dataProvider = "Шапка отчета по возвратам", dataProviderClass = TableReportsDataprovider.class)
	public void testRefundReportTableHead(String fieldName){
		log.info(fieldName);
		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
}
