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
public class CashierNameOperDayTabTest extends AbstractTest{
	
	PaymentEmulator payments = new PaymentEmulator();
	PurchaseEntity p1;
	
	MainPage mainPage;
	CashOperDayTabPage operDayTab;
	
	String cashierName = ""; 
	
	@BeforeClass
	public void setup(){
		//отправляем чек, тем самым открываем смену
		cashEmulator.nextPurchase();
		mainPage = new LoginPage(getDriver(), TARGET_HOST_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		operDayTab = mainPage.openOperDay().navigatePage(CashesPage.class, CASHES).navigatePage(CashOperDayTabPage.class,  LOCATOR_OPERDAY_TAB);
		cashierName = operDayTab.getCashierNameForLastShift(cashEmulator.getCashNumber());
	}
	
	@Test(description = "SRTE-117. Отображается фамилия кассира, работающего на кассе в данный момент")
	public void testActiveCashierNameExist(){
		String surname = cashEmulator.changeCashUser(DisinsectorTools.random(100));
		operDayTab = operDayTab.refreshOperdayTab();
		
		Assert.assertTrue(operDayTab.getCashierNameForLastShift(cashEmulator.getCashNumber()).contains(surname), 
				"Неверная фамилия кассира работающего на кассе, после смены пользователя на открытой смене "); 
	}
	
	@Test (description = "SRTE-117. Отображается фамилия кассира, закрывшего смену", 
			dependsOnMethods = {"testActiveCashierNameExist", "testNoCashierIfNoActivity"},
			alwaysRun = true)
	public void testCashierWhoCloseShift(){
		String surname = cashEmulator.changeCashUser(DisinsectorTools.random(100));
		cashEmulator.nextZReport(100000L, 200000L);
		
		operDayTab = operDayTab.refreshOperdayTab();
		Assert.assertTrue(operDayTab.getCashierNameForLastShift(cashEmulator.getCashNumber()).contains(surname), 
				"Неверно отображается фамилия кассира, закрывшего смену "); 
		
	}
	
	@Test (description = "SRTE-117. Отображается \"-\" на кассе N10, если смена на кассе не была открыта")
	public void testCashierIfNoShiftOpened(){
		operDayTab = operDayTab.refreshOperdayTab();
		log.info("Проверить значение кассира на кассе номер 10, где нет открытых смен");
		Assert.assertTrue(operDayTab.getCashierNameWithNoShift(10).equals("-"), 
				String.format("Неверно отображатся значение \"-\", на кассе %s, для смены, которая не была открыта", "10"));
	}
	
	@Test (description = "SRTE-117. Если нет активности в течение 3 минут, отображается \"нет кассира\"")
	public void testNoCashierIfNoActivity(){
		DisinsectorTools.delay(200000);
		operDayTab = operDayTab.refreshOperdayTab();
		
		Assert.assertTrue(operDayTab.getCashierNameForLastShift(cashEmulator.getCashNumber()).equals("нет кассира"), 
				"Неверно отображатся значение \"нет кассира\", если на кассе никто не работает"); 
	}
	
}
