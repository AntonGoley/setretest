package ru.crystals.disinsector2.test;

import java.util.Date;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.test2.basic.LoginPage;
import ru.crystals.test2.basic.MainPage;
import ru.crystals.test2.basic.SalesPage;
import ru.crystals.test2.basic.SalesPage.SalesMenuItemsAdmin;
import ru.crystals.test2.config.Config;
import ru.crystals.test2.sales.preferences.SalesGoodsTypesAndPaymentsTabPage;
import ru.crystals.test2.sales.preferences.SalesGoodsTypesAndPaymentsTabPage.ProductTypeItems;
import ru.crystals.test2.sales.preferences.SalesPreferencesPage;
import ru.crystals.test2.sales.preferences.SalesPreferencesPage.SalesPreferencesPageTabs;
import ru.crystals.test2.sales.preferences.goodstypes.alcohol.AlcoholPage;
import ru.crystals.test2.sales.preferences.goodstypes.alcohol.AlcoholPage.AlcoholTabs;
import ru.crystals.test2.sales.preferences.goodstypes.alcohol.AlcoholRestrictionPage;
import ru.crystals.test2.sales.preferences.goodstypes.alcohol.AlcoholTabsRestrictionsPage;
import ru.crystals.test2.utils.SoapRequestSender;

public class SpiritRestrictionsToSAPTest extends AbstractTest{
	
	MainPage mainPage;
	SalesPage salesPage;
	SalesGoodsTypesAndPaymentsTabPage goodsAndTypeTab; 
	AlcoholPage alcoholPage;
	AlcoholTabsRestrictionsPage alcoholRestrictionTab;
	AlcoholRestrictionPage alcoholRestrictionPage;
	SoapRequestSender soapSender  = new SoapRequestSender();
	String restrictionName = "New_restriction" + String.valueOf(new Date().getTime());
	
	
	@BeforeClass
	public void goToAlcoholRestrictions() {
		mainPage = new LoginPage(getDriver()).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();
		alcoholPage = salesPage.navigateMenu(SalesMenuItemsAdmin.PREFERENCES, SalesPreferencesPage.class).navigateTab(SalesPreferencesPageTabs.GOODS_TYPES_AND_PAYSMENTS).
				selectProductTypeItem(ProductTypeItems.ALCOHOL);
		alcoholRestrictionPage = alcoholPage.selectAlcoholTab(AlcoholTabs.ALCOHOL_RESTRICTIONS).addNewRestriction();
	}
	
	
	@Test
	public void test() {
		alcoholRestrictionPage.setRestrictionName(restrictionName);
		alcoholRestrictionPage.backToRestrictionsTab();
		SoapRequestSender soapValidate = new  SoapRequestSender();
		soapValidate.setSoapServiceIP(Config.CENTRUM_HOST);
		soapValidate.getAlcoRestrictions();
		//Assert.assertEquals("В ответе отсутствует поле name ", !soapValidate.assertSOAPResponseXpath("//restriction[@name][1]").equals(null));
		Assert.assertEquals("Неверное значение поля name ", restrictionName.equals(
				soapValidate.assertSOAPResponseXpath(String.format("//restriction[@name = \"%s\"]", restrictionName))));
	}
	
	
	/*
	 * 	alcoholRestrictionPage.setMinPrice("100.05");
		alcoholRestrictionPage.setDate("08.07.14 (00:00) — 15.07.14 (23:59)");
		alcoholRestrictionPage.setPersentAlco("5.55");
		alcoholRestrictionPage.setTime("21:00".split(":"), "11:00".split(":"));
		
		soapSender.getAlcoRestrictions();
		soapSender.assertSOAPResponseXpath("//alcoholic-content-percentage").contains("10");
	 */
}

