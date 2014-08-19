package ru.crystals.set10.test.documents.accompanying;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
//import ru.crystals.pos.check.PositionEntity;
//import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.pages.operday.tablereports.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.test.dataproviders.TableReportsDataprovider;
import ru.crystals.set10.utils.CheckGenerator;
import static ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage.HTMLREPORT;
import static ru.crystals.set10.pages.operday.tablereports.TableReportPage.*;


public class NomenclatureDocumentTest extends AbstractTest{

	MainPage mainPage;
	CheckSearchPage searchCheck;
	AbstractReportConfigPage RefundChecksConfigPage;
	HTMLRepotResultPage htmlReportResults;
//	PurchaseEntity pe;

	
	@BeforeClass
	public void navigateToheckSearchPage() {
		mainPage = new LoginPage(getDriver(),  Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().openCheckSearch();
	}	
	
	public void sendCheck() {
//		CheckGenerator checkGenerator = new CheckGenerator(Config.RETAIL_HOST, 2103, 1);
//		pe = (PurchaseEntity) checkGenerator.nextPurchase();
//		checkGenerator.logCheckEntities(pe);
	}
	
	public void doReport(){
		htmlReportResults = RefundChecksConfigPage.generateReport(HTMLREPORT);
		RefundChecksConfigPage.switchWindow(true);
	}
	
	
	@Test
	public void testSendCheck(){
 		searchCheck.doSearch();
		//sendCheck();
	}
	
	
	
	
//	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по возвратам", 
//			alwaysRun = true,
//			dataProvider = "Шапка отчета по возвратам", dataProviderClass = TableReportsDataprovider.class)
	public void test(){
//		log.info(fieldName);
//		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
}
