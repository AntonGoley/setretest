package ru.crystals.set10.test.documents.accompanying;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.CheckGenerator;


public class AccompanyngDocumentsAbstractTest extends AbstractTest{

	MainPage mainPage;
	CheckSearchPage searchCheck;
	AbstractReportConfigPage RefundChecksConfigPage;
	HTMLRepotResultPage htmlReportResults;
	PurchaseEntity pe;
	CheckContentPage checkContent;
	CheckGenerator checkGenerator = new CheckGenerator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), 1);
	
	public void navigateToheckSearchPage() {
		pe = (PurchaseEntity) checkGenerator.nextPurchase();
		mainPage = new LoginPage(getDriver(),  Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().openCheckSearch();
		String checkNumber = String.valueOf(pe.getNumber());
 		searchCheck.setCheckNumber(checkNumber).doSearch();
 		checkContent = searchCheck.selectCheck(checkNumber);
	}	
	
}
