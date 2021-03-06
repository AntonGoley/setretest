package ru.crystals.set10.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage;
import ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.NewAlcoholRestrictionPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholRestrictionsTabPage;
import ru.crystals.set10.test.dataproviders.SpiritRistrictionsDataprovider;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage.*;
import static ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage.GOOD_TYPE_ALCOHOL;
import static ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholPage.ALCOHOL_RESTRICTIONS;
import static ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.NewAlcoholRestrictionPage.LOCATOR_CHECKBOX_ISANYPRESENT;

@Test (groups = {"retail", "centrum"})
public class SpiritRestrictionsToSAPTest extends AbstractTest{
	
	MainPage mainPage;
	SalesPage salesPage;
	SalesGoodsTypesAndPaymentsTabPage goodsAndTypeTab; 
	AlcoholPage alcoholPage;
	AlcoholRestrictionsTabPage alcoholRestrictionTab;
	NewAlcoholRestrictionPage alcoholRestrictionPage;
	SoapRequestSender soapSender  = new SoapRequestSender();
	
	
	protected static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	/*
	 * Дефолтовые значения периода,
	 * за который выбираем ограничения
	 */
	String periodFrom = DisinsectorTools.getDate(DATE_FORMAT, System.currentTimeMillis() - 60*60*24*100);
	String periodTill = DisinsectorTools.getDate(DATE_FORMAT, System.currentTimeMillis() + 60*60*24*100);
	
	
	@BeforeClass
	public void goToAlcoholRestrictions() {
		soapSender.setSoapServiceIP(TARGET_HOST);
		mainPage = new LoginPage(getDriver(), TARGET_HOST_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();
		alcoholPage = salesPage
				.navigateMenu(Config.SALES_PREFERENCES_INDEX, SalesPreferencesPage.class)
				.navigateTab(TAB_TYPES_GOODS_PAYMENTS)
				.selectProductTypeItem(GOOD_TYPE_ALCOHOL, AlcoholPage.class);
		alcoholRestrictionTab = alcoholPage.selectAlcoholTab(ALCOHOL_RESTRICTIONS, AlcoholRestrictionsTabPage.class);
	}
	
	@BeforeMethod
	public void addRestriction(){
		alcoholRestrictionPage = alcoholRestrictionTab.addNewRestriction();
	}
	
	@Test (description = "SRL-163. Выгрузка в SAP отчета по алкогольным ограничениям. Процент содержания алкоголя", 
			dataProvider = "Процент содержания алкоголя", 
			dataProviderClass = SpiritRistrictionsDataprovider.class)
	public void spiritSAPExportSpiritPercentTest(String name, String percentValue, String xpath) {
		alcoholRestrictionPage.setCheckBoxValue(LOCATOR_CHECKBOX_ISANYPRESENT, false);  
		alcoholRestrictionPage.setPersentAlco(percentValue);
		alcoholRestrictionPage.setRestrictionName(name);
		alcoholRestrictionPage.goBack();
		Assert.assertTrue(validateResult(String.format(xpath, name), periodFrom, periodTill));
	}
	
	@Test ( enabled = false,
			description = "SRL-163. Выгрузка в SAP отчета по алкогольным ограничениям. Период действия", 
			dataProvider = "Период действия", 
			dataProviderClass = SpiritRistrictionsDataprovider.class)
	public void spiritSAPExportDateRangeTest(String name, String period, String dateToValidate, String xpath) {
		alcoholRestrictionPage.setDate(period);
		alcoholRestrictionPage.setRestrictionName(name);
		alcoholRestrictionPage.goBack();
		validateResult(String.format(xpath, name, dateToValidate), periodFrom, periodTill);
	}
	
	@Test (description = "SRL-163. Выгрузка в SAP отчета по алкогольным ограничениям. Время действия", 
			dataProvider = "Время действия", 
			dataProviderClass = SpiritRistrictionsDataprovider.class)
	public void spiritSAPExportTimeRangeTest(String name, String fromTime, String toTime, String timeToValidate, String xpath) {
		alcoholRestrictionPage.setTime(fromTime.split(":"), toTime.split(":"));
		alcoholRestrictionPage.setRestrictionName(name);
		alcoholRestrictionPage.goBack();
		validateResult(String.format(xpath, name, timeToValidate), periodFrom, periodTill);
	}
	
	
	private boolean validateResult(String xpath, String from, String till){
		boolean result = false;
		long delay = 0;
		log.info("Проверка ограничения:" + xpath);
		while (delay < 15) {
			DisinsectorTools.delay(1000);
			delay++;
			soapSender.getAlcoRestrictions(from, till);	
			result =  soapSender.assertSOAPResponseXpath(xpath);
			if (result) return true;
		}	
		return false;
	}

}

