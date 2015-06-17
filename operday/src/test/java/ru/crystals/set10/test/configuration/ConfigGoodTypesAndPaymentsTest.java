package ru.crystals.set10.test.configuration;

import static ru.crystals.set10.config.Config.WEIGHT_BARCODEGENERATION_OFSET;
import static ru.crystals.set10.config.Config.WEIGHT_BARCODEGENERATION_PREFIX;
import static ru.crystals.set10.config.Config.WEIGHT_BARCODE_PREFIX;
import static ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage.TAB_TYPES_GOODS_PAYMENTS;
import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.pages.sales.preferences.AbstractGoodAndPaymentPreferencesPage;
import ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage;
import ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholRestrictionsTabPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.NewAlcoholRestrictionPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.weight.WeightGoodPage;
import ru.crystals.set10.pages.sales.preferences.goodtypes.GiftCardPage;
import ru.crystals.set10.pages.sales.preferences.paymenttypes.ConsumerCreditPage;
import ru.crystals.set10.test.AbstractTest;
import static ru.crystals.set10.pages.sales.preferences.goodstypes.weight.WeightGoodPage.*;
import static ru.crystals.set10.pages.sales.preferences.goodtypes.GiftCardPage.*;
import static ru.crystals.set10.pages.sales.preferences.paymenttypes.ConsumerCreditPage.CHECKBOX_ALLOW_RETURN;
import static ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage.*;
import static ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholPage.ALCOHOL_RESTRICTIONS;


public class ConfigGoodTypesAndPaymentsTest extends AbstractTest {
	
	MainPage mainPage;
	SalesPage salesPage;
	SalesPreferencesPage salesPreferencesPage;
	SalesGoodsTypesAndPaymentsTabPage salesGoodsTypesPage;
	GiftCardPage giftCardsPage; 
	WeightGoodPage weightGood;
	ConsumerCreditPage consumerCreditPage;
	AlcoholPage alcoholPage;
	AlcoholRestrictionsTabPage alcoholRestrictionsTab;
	NewAlcoholRestrictionPage newAlcoholRestriction;
	
	
	@BeforeClass
	public void doLogin(){
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();
		/*
		 * TODO:!!! разрулить разницу в меню Retail и Центрум
		 */
		salesPreferencesPage = salesPage.navigateMenu(10, SalesPreferencesPage.class);
		salesGoodsTypesPage = salesPreferencesPage.navigateTab(TAB_TYPES_GOODS_PAYMENTS);
	}
	
	@Test(description = "Настройка типов товаров и оплат: Подарочная карта")
	public void testAddGiftCardProperty(){
		
		giftCardsPage = salesGoodsTypesPage.selectProductTypeItem(GOOD_TYPE_GIFT_CARD, GiftCardPage.class);

		giftCardsPage.setCheckBox(GiftCardPage.class, CHECKBOX_ALLOW_MANUAL_CARD_NUMBER_INPUT, true);
		Assert.assertTrue(giftCardsPage.getCheckBoxValue(CHECKBOX_ALLOW_MANUAL_CARD_NUMBER_INPUT), "");
	}
	
	@Test (description =  "Настройка типов товаров и оплат: способ генерации PLU для Весового товара")
	public void testConfigWeightPrefixes(){
		weightGood = salesGoodsTypesPage.selectProductTypeItem(GOOD_TYPE_WEIGHT_GOOD, WeightGoodPage.class);
		weightGood.setPLUGeneration(PLU_GENERATION_ERP_AND_BAR_CODE);
	}
	
	@DataProvider(name = "weight")
	public Object[][] weightConfig(){
		/*
		 * Посмотреть, насколько это хорошо выполнять шаг в dataProvider?
		 */
		weightGood = salesGoodsTypesPage.selectProductTypeItem(GOOD_TYPE_WEIGHT_GOOD, WeightGoodPage.class);
		return new Object[][]{
				/*
				 * Выносить ли в конфиг?
				 */
				{ACTION_UCENKA, "28", "1", "0"},
				{ACTION_FASOVKA, "27", "10", "0"},
				{ACTION_FASOVKA, WEIGHT_BARCODE_PREFIX, "1", "0"},
				{ACTION_FASOVKA, WEIGHT_BARCODEGENERATION_PREFIX, "1", WEIGHT_BARCODEGENERATION_OFSET},
		};		
	}
	
	@Test ( description = "Настройка типов товаров и оплат: префиксы для весового товара", 
			dependsOnMethods = "testConfigWeightPrefixes",
			dataProvider = "weight")
	public void testConfigPLUGenerationMethod(
			String goodAction, 
			String prefix, 
			String multiplier, 
			String pluOfset )
	{
		weightGood
			.setGoodAction(goodAction)
			.setPrefix(prefix)
			.setMultiplier(multiplier)
			.setPLUOfset(pluOfset)
			.addGoodAction();
	};
	
	@Test (description = "Настройка типов товаров и оплат: потребительский кредит")
	public void testConfigConsumerCredit(){
		consumerCreditPage = salesGoodsTypesPage.selectPaymentTypeItem(PAYMENT_TYPE_CONSUMER_CREDIT, ConsumerCreditPage.class);
		consumerCreditPage.setCheckBox(AbstractGoodAndPaymentPreferencesPage.class, CHECKBOX_ALLOW_RETURN, true);
		
		Assert.assertTrue(consumerCreditPage.getCheckBoxValue("Разрешить возврат"), "Не установлен флаг: " + CHECKBOX_ALLOW_RETURN);
	}
	
	
	@DataProvider(name = "alcohol")
	public Object[][] alcoholRestrictions(){
		alcoholPage = salesGoodsTypesPage.selectProductTypeItem(GOOD_TYPE_ALCOHOL, AlcoholPage.class);
		return new Object[][]{
				{"Ограничение для 90,5%", "90,5", true},
				{"Ограничение для 60,5%", "60,5", true}
		};
	}
	
	/*
	 * Для центрума
	 */
	@Test ( enabled = false,
			description = "Настройка типов товаров и оплат: алкогольные ограничения",
		   dataProvider = "alcohol")
	public void testConfigAlcoRestrictions(String name, String persentageValue, boolean spreadAllShops){
		int restrictionsOnPage = 0;
		alcoholRestrictionsTab =  alcoholPage.selectAlcoholTab(ALCOHOL_RESTRICTIONS, AlcoholRestrictionsTabPage.class);
		restrictionsOnPage = alcoholRestrictionsTab.getRestrictionsCount();
		
		newAlcoholRestriction =  alcoholRestrictionsTab.addNewRestriction();
		
		alcoholRestrictionsTab = newAlcoholRestriction
					.setPersentAlco(persentageValue)
					.setRestrictionName(name)
					//.setSpreadAllShopsCentrum(true)
					.backToRestrictionsTab();
		
		Assert.assertEquals(alcoholRestrictionsTab.getRestrictionsCount(), restrictionsOnPage + 1, "Новое алкогольное ограничение не добавлено в таблицу ограничений!");
		
	}
	
	
	@AfterMethod(lastTimeOnly = true)
	public void goBack(Method method){
		log.info(method.getName());
		if (method.getName().equals("testConfigAlcoRestrictions")){
			salesGoodsTypesPage = alcoholRestrictionsTab.goBack();
			return;
		}
		
		salesGoodsTypesPage = new AbstractGoodAndPaymentPreferencesPage(getDriver()).goBack();
	}

}
