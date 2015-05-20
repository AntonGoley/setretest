package ru.crystals.set10.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.tablereports.WrongAdverstingPriceConfigPage;
import ru.crystals.set10.product.ProductCardPage;
import ru.crystals.set10.product.ProductAdditionalInfoTabPage;
import ru.crystals.set10.product.ProductMainInfoTabPage;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.PluginProperty;
import static ru.crystals.set10.product.ProductAdditionalInfoTabPage.*;
import static ru.crystals.set10.product.ProductMainInfoTabPage.*;


@Test (groups = {"retail", "centrum"})
public class GoodsAdditionalFieldsTest extends AbstractTest{
	
	MainPage mainPage;
	ProductAdditionalInfoTabPage productAdditionalInfo;
	ProductMainInfoTabPage productMainInfo;
	ProductCardPage product;
	
	WrongAdverstingPriceConfigPage reportConfigPage;
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	private Good weightGood;
	private PluginProperty producer;
	private PluginProperty buttonOnScale;
	private PluginProperty goodForHours;
	private PluginProperty goodForDays;
	String prefix = String.valueOf(new Date().getTime());
	
	
	
	private void setInputData(){
		weightGood = goodGenerator.generateWeightGood("0");
		
		List<PluginProperty> weightProperties = new ArrayList<PluginProperty>();
		
		producer = goodGenerator.generatePluginProperty("producer", "producer_" + prefix);
		buttonOnScale = goodGenerator.generatePluginProperty("button-on-scale", "99");
		goodForHours = goodGenerator.generatePluginProperty("good-for-hours", "48");
		
		weightProperties.add(producer);
		weightProperties.add(buttonOnScale);
		weightProperties.add(goodForHours);
		weightGood.getPluginProperties().addAll(weightProperties);

		weightGood.setCertificationType(15);
	}
	
	
	@BeforeClass
	public void openProductCard() {
		soapSender.setSoapServiceIP(TARGET_HOST);
		
		setInputData();
		/* отправить товар*/
		soapSender.sendGood(weightGood);
		
		mainPage = new LoginPage(getDriver(), TARGET_HOST_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		product = mainPage.findGood(weightGood.getMarkingOfTheGood());
	}	
	
	@DataProvider(name = "Поля весового товара")
	private Object[][] priceData(){
		return new Object[][]{
				{"Производитель", FIELD_PRODUCER, producer.getValue()},
				{"Номер кнопки в весах", FIELD_BUTTON_NUMBER_ON_SCALES, buttonOnScale.getValue() },
				{"Срок годности (в часах)", FIELD_VALID_FOR_HOURS, goodForHours.getValue()} 
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
	
	@Test (	description = "SRL-757. Смена и отображение срока годности весового товара. Если отображается срок годности в часах, то скрыто значение срока годности в днях (и наоборот)",
			dependsOnMethods = "testWeightProperties", 
			alwaysRun = true)
	public void testGoodForHoursDays(){
		List<PluginProperty> weightProperties = new ArrayList<PluginProperty>();
		/*почистим PluginProperties у товара и добавим новый */
		weightGood.getPluginProperties().addAll(weightProperties);
		goodForDays = goodGenerator.generatePluginProperty("good-for-days", "10");
		weightGood.getPluginProperties().add(goodForDays);
		
		soapSender.sendGood(weightGood);
		
		getDriver().get(TARGET_HOST_URL);
		mainPage = new MainPage(getDriver());
		product = mainPage.findGood(weightGood.getMarkingOfTheGood());
		productAdditionalInfo = product.selectTab(TAB_ADDITION_INFO, ProductAdditionalInfoTabPage.class);
		
		Assert.assertEquals(productAdditionalInfo.getTextFieldValue(FIELD_VALID_FOR_DAYS), "10",  
				String.format("Неверное значение поля \"Срок годности (в днях) \" в карточке товара"));
		
		Assert.assertEquals(productAdditionalInfo.getTextFieldValue(FIELD_VALID_FOR_HOURS), "", 
				String.format("В карточке товара не должны одновременно отображаться значения полей \"Срок годности (в днях)\" и \"Срок годности (в часах) \" "));
	}
	
}
