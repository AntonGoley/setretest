package ru.crystals.set10.test;

import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage;
import ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage;
import ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage.ProductTypeItems;
import ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage.SalesPreferencesPageTabs;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholRestrictionPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholTabsRestrictionsPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholPage.AlcoholTabs;
import ru.crystals.set10.test.dataproviders.SpiritRistrictionsDataprovider;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.basic.SalesPage.*;

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
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();
		alcoholPage = salesPage.navigateMenu(SALES_MENU_MODULES_PREFERENCES, "11", SalesPreferencesPage.class).navigateTab(SalesPreferencesPageTabs.GOODS_TYPES_AND_PAYSMENTS).
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

