package ru.crystals.set10.test.documents.accompanying;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.pos.check.PurchaseEntity;
//import ru.crystals.pos.check.PositionEntity;
//import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.CheckGenerator;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.searchcheck.CheckContentPage.*;


public class NomenclatureDocumentTest extends AbstractTest{

	MainPage mainPage;
	CheckSearchPage searchCheck;
	AbstractReportConfigPage RefundChecksConfigPage;
	HTMLRepotResultPage htmlReportResults;
	PurchaseEntity pe;
	CheckContentPage checkContent;
	CheckGenerator checkGenerator = new CheckGenerator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), 1);
	
	@BeforeClass
	public void navigateToheckSearchPage() {
		pe = (PurchaseEntity) checkGenerator.nextPurchase();
		mainPage = new LoginPage(getDriver(),  Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().openCheckSearch();
	}	
	
	public void sendCheck() {
	}
	
	@Test
	public void testSendCheck(){
		String checkNumber = String.valueOf(pe.getNumber());
 		searchCheck.setCheckNumber(checkNumber).doSearch();
 		checkContent = searchCheck.selectCheck(checkNumber);
 		checkContent.generateReport(LINK_NOMENCLATURE);
 		DisinsectorTools.getConsoleOutput(getDriver());
	}
	
	private void validateReportResult(){

	}
	
	
	
//	@Test (	description = "Проверить названия отчета и название колонок в шапке таблицы отчета по возвратам", 
//			alwaysRun = true,
//			dataProvider = "Шапка отчета по возвратам", dataProviderClass = TableReportsDataprovider.class)
	public void test(){
//		log.info(fieldName);
//		Assert.assertTrue(htmlReportResults.containsValue(fieldName), "Неверное значение поля в шапке отчета: " + fieldName);
	}
	
}
