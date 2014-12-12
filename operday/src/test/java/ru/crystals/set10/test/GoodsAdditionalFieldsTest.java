package ru.crystals.set10.test;

import java.util.Date;
import java.util.HashMap;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.tablereports.WrongAdverstingPriveConfigPage;
import ru.crystals.set10.product.ProductCardPage;
import ru.crystals.set10.product.ProductAdditionalInfoTabPage;
import ru.crystals.set10.product.ProductMainInfoTabPage;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.product.ProductAdditionalInfoTabPage.*;
import static ru.crystals.set10.product.ProductMainInfoTabPage.*;


public class GoodsAdditionalFieldsTest extends AbstractTest{
	
	MainPage mainPage;
	ProductAdditionalInfoTabPage productAdditionalInfo;
	ProductMainInfoTabPage productMainInfo;
	ProductCardPage product;
	
	WrongAdverstingPriveConfigPage reportConfigPage;
	SoapRequestSender soapSender = new SoapRequestSender();
	
	private static String mask_marking_of_the_good = "${marking-of-the-good}";
	private static String mask_barcode = "${barcode}";
	private static String mask_name = "${name}";
	private static String mask_certificationType = "${certification-type}";
	private static String mask_producer = "${producer}";
	private static String mask_buttonOnScale = "${button-on-scale}";
	private static String request = ""; 
	
	
	private static HashMap<String, String> weightAdditionalFieldsPrice;
	
	private static void setInputData(){
		String goodPrefix = String.valueOf(new Date().getTime());
		weightAdditionalFieldsPrice =  new HashMap<String, String>();
			weightAdditionalFieldsPrice.put(mask_marking_of_the_good, goodPrefix);
			weightAdditionalFieldsPrice.put(mask_barcode,String.valueOf(new Date().getTime() - 100) );
			weightAdditionalFieldsPrice.put(mask_name,"name_" + goodPrefix);
			weightAdditionalFieldsPrice.put(mask_producer,"producer_" + goodPrefix);
			weightAdditionalFieldsPrice.put(mask_buttonOnScale,"99");
			weightAdditionalFieldsPrice.put(mask_certificationType,"15");
	}
	
	@BeforeClass
	public void openProductCard() {
		
		setInputData();
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		
		/*
		 * Послать весовой товар
		 */
		request = 	DisinsectorTools.getFileContentAsString("good_additional_fields.txt");
		weightAdditionalFieldsPrice = soapSender.sendGoods(request, weightAdditionalFieldsPrice);
		
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		product = mainPage.findGood(weightAdditionalFieldsPrice.get(mask_barcode));
	}	
	
	@DataProvider(name = "Поля весового товара")
	public Object[][] priceData(){
		return new Object[][]{
				{"Производитель", FIELD_PRODUCER, weightAdditionalFieldsPrice.get(mask_producer)},
				{"Номер кнопки в весах", FIELD_BUTTON_NUMBER_ON_SCALES,  weightAdditionalFieldsPrice.get(mask_buttonOnScale)}
						};
	}
	
	@Test (	description = "SRTE-110. Проверка полей в карточке товара специфических для весового товара",
			dataProvider = "Поля весового товара"
			)
	public void testWeightProperties(String description, String flexId, String expectedValue){
		productAdditionalInfo = product.selectTab(TAB_ADDITION_INFO, ProductAdditionalInfoTabPage.class);
		log.info(description);
		Assert.assertEquals(productAdditionalInfo.getTextFieldValue(flexId), expectedValue, 
				String.format("Неверное значение поля %s в карточке товара", description));

	}
	
	@Test (	description = "SRTE-110. Поле в карточке весового товара Тип сертификации. Проверить что отображаются все типы сертификации, если указано знаение 15"
			)
	public void testGoodSTProperties(){
		productMainInfo = product.selectTab(TAB_MAIN_INFO, ProductMainInfoTabPage.class);
		//CERTIFICATION_TYPE_OBLIGATE
		Assert.assertTrue(productMainInfo.ifCertificationTypeVisible(CERTIFICATION_TYPE_OBLIGATE), "Не отображается обязательная сертификация");
		Assert.assertTrue(productMainInfo.ifCertificationTypeVisible(CERTIFICATION_TYPE_FREE), "Не отображается добровольная сертификация");
		Assert.assertTrue(productMainInfo.ifCertificationTypeVisible(CERTIFICATION_TYPE_TECNICAL_REGULATION), "Не отображается технический регламент");
		Assert.assertTrue(productMainInfo.ifCertificationTypeVisible(CERTIFICATION_TYPE_EAC), "Не отображается обязательная EAC");
	}
	
}
