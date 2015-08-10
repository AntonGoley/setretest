 package ru.crystals.set10.test.configuration;


import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.*;
import ru.crystals.set10.pages.sales.module.preferences.SalesMainCashPreferencesTabPage;
import ru.crystals.set10.pages.sales.module.preferences.SalesModulesPreferencesPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.test.maincash.MainCashConfigTool;


@Test (groups = "retail")
public class ConfigMainCashTest extends AbstractTest {
	
	MainPage mainPage;
	SalesPage salesPage;
	SalesMainCashPreferencesTabPage mainCashPrefs;
	
	@BeforeClass
	public void doLogin(){
		MainCashConfigTool.addPrivileges();
		/* сделать внесение для открытия ОД*/
		cashEmulator.nextIntroduction();
		mainPage = new LoginPage(getDriver(), TARGET_HOST_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();		
	}

	
	@Test (	description = "Включить главну кассу начиная с сегодняшнего дня")
	public void testTurnMainCash(){
		mainCashPrefs = salesPage.navigateMenu(9, SalesModulesPreferencesPage.class)
				.openTab(SalesMainCashPreferencesTabPage.class, SalesModulesPreferencesPage.TAB_MAINCASH);
		mainCashPrefs.setInitialBalance(new BigDecimal("100.99"))
			.turnMainCash(true);
		
		Assert.assertTrue(mainCashPrefs.ifMainCashTurned("100.99") , "Главная касса включена некорректно!");
	}
	


}
