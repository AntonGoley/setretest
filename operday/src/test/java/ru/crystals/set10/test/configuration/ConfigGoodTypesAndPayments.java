package ru.crystals.set10.test.configuration;

import static ru.crystals.set10.config.Config.WEIGHT_BARCODEGENERATION_OFSET;
import static ru.crystals.set10.config.Config.WEIGHT_BARCODEGENERATION_PREFIX;
import static ru.crystals.set10.config.Config.WEIGHT_BARCODE_PREFIX;
import static ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage.TAB_TYPES_GOODS_PAYMENTS;

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
import ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage.ProductTypeItems;
import ru.crystals.set10.pages.sales.preferences.goodstypes.weight.WeightGoodPage;
import ru.crystals.set10.pages.sales.preferences.goodtypes.GiftCardPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.sales.preferences.goodstypes.weight.WeightGoodPage.*;
import static ru.crystals.set10.pages.sales.preferences.goodtypes.GiftCardPage.*;


public class ConfigGoodTypesAndPayments extends AbstractTest {
	
	MainPage mainPage;
	SalesPage salesPage;
	SalesPreferencesPage salesPreferencesPage;
	SalesGoodsTypesAndPaymentsTabPage salesGoodsTypesPage;
	GiftCardPage giftCardsPage; 
	WeightGoodPage weightGood;
	
	@BeforeClass
	public void doLogin(){
		mainPage = new LoginPage(getDriver(), Config.CENTRUM_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		// таймаут для певрого запуска
		DisinsectorTools.delay(1000);
		salesPage = mainPage.openSales();	
		salesPreferencesPage = salesPage.navigateMenu(11, SalesPreferencesPage.class);
		salesGoodsTypesPage = salesPreferencesPage.navigateTab(TAB_TYPES_GOODS_PAYMENTS);
	}
	
	@Test(description = "Настройка типов товаров и оплат: Подарочная карта")
	public void testAddGiftCardProperty(){
		
		giftCardsPage = salesGoodsTypesPage.selectProductTypeItem(ProductTypeItems.GIFT_CARD, GiftCardPage.class);

		giftCardsPage.setCheckBox(GiftCardPage.class, CHECKBOX_ALLOW_MANUAL_CARD_NUMBER_INPUT, true);
		Assert.assertTrue(giftCardsPage.getCheckBoxValue(CHECKBOX_ALLOW_MANUAL_CARD_NUMBER_INPUT), "");
		salesGoodsTypesPage = giftCardsPage.goBack();
	}
	
	@Test (description =  "Настройка способа генерации PLU для Весового товара")
	public void testConfigWeightPrefixes(){
		weightGood = salesGoodsTypesPage.selectProductTypeItem(ProductTypeItems.WEIGHT_GOOD, WeightGoodPage.class);
		weightGood.setPLUGeneration(PLU_GENERATION_ERP_AND_BAR_CODE);
		//salesGoodsTypesPage = giftCardsPage.goBack();
	}
	
	
	/*
	 * Уценка, 28, множитель 1, смещение 0
	 * Фасовка, 27, множитель 10, смещение 0
	 * Фасовка, 28, множитель 1, смещение 0
	 * 
	 */
	
	@DataProvider(name = "weight")
	public Object[][] weightConfig(){
		return new Object[][]{
				{ACTION_UCENKA, "28", "1", "0"},
				{ACTION_FASOVKA, "27", "10", "0"},
				{ACTION_FASOVKA, "28", "1", "0"},
				{ACTION_FASOVKA, WEIGHT_BARCODEGENERATION_PREFIX, "1", WEIGHT_BARCODEGENERATION_OFSET},
		};		
	}
	
	@Test (description = "Настройка префиксов для Весового товара", 
			dependsOnMethods = "testConfigWeightPrefixes")
	public void testConfigPLUGenerationMethod(
			String goodAction, 
			String prefix, 
			String multiplier, 
			String pluOfset)
	{
		
		weightGood
			.setGoodAction(goodAction)
			.setPrefix(prefix)
			.setMultiplier(multiplier)
			.setPLUOfset(pluOfset)
			.addGoodAction();
	};
	
	@AfterMethod(lastTimeOnly = true)
	public void goBack(){
		salesGoodsTypesPage = new AbstractGoodAndPaymentPreferencesPage(getDriver()).goBack();
	}

}
