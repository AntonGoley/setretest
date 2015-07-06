package ru.crystals.set10.test.maincash;

import java.math.BigDecimal;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.operday.cashes.CashesPage;
import ru.crystals.set10.pages.operday.cashes.MainCashDocsPage;
import ru.crystals.set10.test.AbstractTest;
import static ru.crystals.set10.pages.operday.OperDayPage.CASHES;
import static ru.crystals.set10.pages.operday.cashes.CashesPage.LOCATOR_MAINCASH_TAB;
import static ru.crystals.set10.pages.operday.cashes.CashDocsAbstractPage.*;


@Test (groups= "retail")
public class MainCashConfigTest extends AbstractTest {
	
	MainCashDocsPage docs;
	int docsOnPage = 0;
	
	String headAccountant = "Главбухова О.А";
	BigDecimal balanceStart;
	BigDecimal balance;
	
	@BeforeClass
	public void setUp(){
		
		//TODO: добавить проверку существования од и включенной ГК
		
		docs = new LoginPage(getDriver(), Config.RETAIL_URL)
		.openOperDay(Config.MANAGER, Config.MANAGER_PASSWORD)
		.navigatePage(CashesPage.class, CASHES)
		.openTab(MainCashDocsPage.class, LOCATOR_MAINCASH_TAB);
		docs.switchToTable(LOCATOR_DOCS);
	}
	
	
	
}
