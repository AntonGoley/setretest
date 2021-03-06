package ru.crystals.set10.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.cashes.CashOperDayTabPage;
import ru.crystals.set10.pages.operday.cashes.CashesPage;
import ru.crystals.set10.utils.PaymentEmulator;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.cashes.CashesPage.LOCATOR_OPERDAY_TAB;
import static ru.crystals.set10.pages.operday.OperDayPage.CASHES;


@Test(groups = {"retail"})
public class CashShiftTimeInOperDayTabTest extends AbstractTest{
	
	PaymentEmulator payments = new PaymentEmulator();
	PurchaseEntity p1;
	
	MainPage mainPage;
	CashOperDayTabPage operDayTab;
	
	String message = "Смена на кассе открыта более 6 дней";
	
	@BeforeClass
	public void setup(){
		//отправляем чек, тем самым открываем смену
		mainPage = new LoginPage(getDriver(), TARGET_HOST_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		operDayTab = mainPage.openOperDay().navigatePage(CashesPage.class, CASHES).navigatePage(CashOperDayTabPage.class,  LOCATOR_OPERDAY_TAB);
		
		cashEmulator.setTimeOfset(86400000 * 6);
		cashEmulator.useNextShift();
		cashEmulator.nextPurchase();
		cashEmulator.nextZReport(100000L, 200000L);
	}
	
	@Test(description = "SRTE-83. ")
	public void testMessageIfShiftOpenMoreThan6Days(){
		
		operDayTab = operDayTab.refreshOperdayTab();
		
		
	}

	
}
