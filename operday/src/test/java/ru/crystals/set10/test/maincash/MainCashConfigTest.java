package ru.crystals.set10.test.maincash;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.operday.cashes.CashesPage;
import ru.crystals.set10.pages.operday.cashes.MainCashDocsPage;
import ru.crystals.set10.pages.operday.cashes.MainCashManualDocPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.OperDayPage.CASHES;
import static ru.crystals.set10.pages.operday.cashes.CashesPage.LOCATOR_MAINCASH_TAB;
import static ru.crystals.set10.pages.operday.cashes.CashDocsAbstractPage.*;


@Test (groups= "retail")
public class MainCashConfigTest extends AbstractTest {
	
	MainCashDocsPage docs;
	int docsOnPage = 0;
	
	BigDecimal balanceStart;
	BigDecimal balance;
	
	protected long today = new Date().getTime();
	
	//@BeforeSuite
	private void turnMainCash(){
		MainCashConfigTool.addPrivileges();
		MainCashConfigTool.enableMainCash(true);
		MainCashConfigTool.clearOD();
		Long[] operdays = new Long[1];
		operdays[0] = 0L;
		MainCashConfigTool.createODWithCashDocs(operdays);
		MainCashConfigTool.turnMainCash(new Date().getTime(), 10000);
	}
	
	protected void openMainDocsPage(){
//		turnMainCash();
		//TODO: добавить проверку существования од и включенной ГК
		docs = new LoginPage(getDriver(), Config.RETAIL_URL)
		.openOperDay(Config.MANAGER, Config.MANAGER_PASSWORD)
		.navigatePage(CashesPage.class, CASHES)
		.openTab(MainCashDocsPage.class, LOCATOR_MAINCASH_TAB);
		docs.switchToTable(LOCATOR_DOCS);
	}
	
	protected void removeFileReports(){
		DisinsectorTools.removeOldReport(chromeDownloadPath, "merged_maincash_document.pdf");
	}
	
	protected String getFileContent(Integer pageNumber){
		File file = DisinsectorTools.getDownloadedFile(chromeDownloadPath, "merged_maincash_document.pdf");
		return docs.getPDFFilePageContent(file, pageNumber);
	}
}
