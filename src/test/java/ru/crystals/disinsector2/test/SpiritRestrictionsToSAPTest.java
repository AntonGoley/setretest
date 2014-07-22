package ru.crystals.disinsector2.test;

import java.util.Date;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.crystals.disinsector2.test.dataproviders.SpiritRistrictionsDataprovider;
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
	
	@BeforeClass
	public void goToAlcoholRestrictions() {
		mainPage = new LoginPage(getDriver()).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();
		alcoholPage = salesPage.navigateMenu(SalesMenuItemsAdmin.PREFERENCES, SalesPreferencesPage.class).navigateTab(SalesPreferencesPageTabs.GOODS_TYPES_AND_PAYSMENTS).
				selectProductTypeItem(ProductTypeItems.ALCOHOL);
		alcoholRestrictionTab = alcoholPage.selectAlcoholTab(AlcoholTabs.ALCOHOL_RESTRICTIONS);
	}
	
	@BeforeMethod
	public void addRestriction(){
		alcoholRestrictionPage = alcoholRestrictionTab.addNewRestriction();
	}
	
	@Test (description = "SLR-163. Выгрузка в SAP отчета по алкогольным ограничениям. Процент содержания алкоголя", 
			dataProvider = "Процент содержания алкоголя", 
			dataProviderClass = SpiritRistrictionsDataprovider.class)
	public void spiritSAPExportSpiritPercentTest(String name, String percentValue, String xpath) {
		alcoholRestrictionPage.setPersentAlco(percentValue);
		alcoholRestrictionPage.setRestrictionName(name);
		alcoholRestrictionPage.backToRestrictionsTab();
		validateTrue(String.format(xpath, name));
	}
	
	//@Test (description = "SLR-163. Выгрузка в SAP отчета по алкогольным ограничениям. Период действия", 
	//		dataProvider = "Период действия", 
	//		dataProviderClass = SpiritRistrictionsDataprovider.class)
	public void spiritSAPExportDateRangeTest(String name, String period, String dateToValidate, String xpath) {
		alcoholRestrictionPage.setDate(period);
		alcoholRestrictionPage.setRestrictionName(name);
		alcoholRestrictionPage.backToRestrictionsTab();
		validateTrue(String.format(xpath, name, dateToValidate));
	}
	
	@Test (description = "SLR-163. Выгрузка в SAP отчета по алкогольным ограничениям. Время действия", 
			dataProvider = "Время действия", 
			dataProviderClass = SpiritRistrictionsDataprovider.class)
	public void spiritSAPExportTimeRangeTest(String name, String fromTime, String toTime, String timeToValidate, String xpath) {
		alcoholRestrictionPage.setTime(fromTime.split(":"), toTime.split(":"));
		alcoholRestrictionPage.setRestrictionName(name);
		alcoholRestrictionPage.backToRestrictionsTab();
		validateTrue(String.format(xpath, name, timeToValidate));
	}
	
	
	private void validateTrue(String xpath){
		log.info("Проверка ограничения:" + xpath);
		SoapRequestSender soapValidate = new  SoapRequestSender();
		soapValidate.setSoapServiceIP(Config.CENTRUM_HOST);
		soapValidate.getAlcoRestrictions();
		Assert.assertTrue("Неверное значение поля ", soapValidate.assertSOAPResponseXpath(xpath));
	}

}

