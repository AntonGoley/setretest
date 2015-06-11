package ru.crystals.set10.test.weight;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.scales.tech.core.scales.virtual.xml.PluType;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.product.ProductAdditionalInfoTabPage;
import ru.crystals.set10.pages.product.ProductCardPage;
import ru.crystals.set10.pages.product.ProductMainInfoTabPage;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import static ru.crystals.set10.pages.product.ProductAdditionalInfoTabPage.*;
import static ru.crystals.set10.pages.product.ProductCardPage.TAB_ADDITION_INFO;

@Test(groups = {"retail"})
public class WeightDateOfManufactureTest extends WeightAbstractTest { 
	
	MainPage mainPage;
	ProductCardPage product;
	
	int pluNum = pluNumber++;
	Good weightGood;
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	ProductAdditionalInfoTabPage   productAdditionalInfo;

	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		soapSender.sendGood(weightGood);
			
		mainPage = new LoginPage(getDriver(), TARGET_HOST_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		product = mainPage.findGood(weightGood.getMarkingOfTheGood());
		productAdditionalInfo = product.selectTab(TAB_ADDITION_INFO, ProductAdditionalInfoTabPage.class);
		
	}
	
	@Test (description = "SRL-758. Для нового товара, пришедшего из ERP, по умолчанию дата и время изготовления не заданы.",
			priority = 1)
	public void testWeightProductManufactureEmptyOnImport(){
		
		PluType plu; 
		plu = scales.getPlu(pluNum);
		
		Assert.assertTrue(scales.waitPluLoaded(pluNum), "Весовой товар не загрузился в весы. PLU = " + pluNum);
		Assert.assertEquals(plu.getAlternativeText4() , "", "Значение поля даты изготовления не пустое, после загрузки нового товара");
		
	}
	
	@DataProvider (name = "dateOfManufacture")
	public Object[][] datesOfManufacture() {
		return new Object[][]{
				{ RADIO_CURRENT_DATE_AND_TIME, DisinsectorTools.getDate("Изготовлено: " + "dd.MM.yyyy", new Date().getTime()) },  // время добавится во время теста
				{ RADIO_LABEL_PRINT_DATE_AND_TIME, "Изготовлено,"},
				{ RADIO_SELECT_DATE, DisinsectorTools.getDate("Изготовлено: " + "dd.MM.yyyy", new Date().getTime())},
				{ RADIO_DATE_NOT_SPECIFIED, ""}
		};
	}
	
	@Test (description = "SRL-758. Проверить, что дата изготовления изменяется в соответствии с выбраным условием в карточке товра",
			priority = 2,
			dataProvider = "dateOfManufacture")
	public void testWeightProductManufactureUpdatedFromProductCard(String property, String result ){
		PluType plu; 
		plu = scales.getPlu(pluNum);
		
		productAdditionalInfo.setProperty(property);
		
		if (property.equals(RADIO_CURRENT_DATE_AND_TIME)){
			result = result + " " + productAdditionalInfo.getManufactureDateCurrentTime();
		}
		
		/* переключаем между вкадками, чтобы убедиться, что флаг не сбросился
		 */
		productAdditionalInfo.selectTab(TAB_MAIN_INFO, ProductMainInfoTabPage.class);
		productAdditionalInfo = product.selectTab(TAB_ADDITION_INFO, ProductAdditionalInfoTabPage.class);
		
		scales.getPluUpdated(plu);
		Assert.assertEquals(scales.getPlu(pluNum).getAlternativeText4() , result, "Не обновилось поле даты изготовления в весах, после выбора свойства в карточке товара: " + productAdditionalInfo.getPropertyFieldValue(property)) ;
	}
	
	@Test (enabled = false, description = "SRL-758. При изменении даты изготовления, остальные текстовые поля этикетки (информация о товаре в полях altmessagetext) остаются неизменны/не перетираются",
			priority = 2)
	public void testAllMessagesUnchangedIfNewDateOfManufature(){
	}
	
	@Test (enabled = false, description = "SRL-758. При обновлении информации о товаре, ранее введенная информация о дате производства не меняется/не удаляется",
			priority = 2)
	public void testDateOfManufatureUnchangedIfNewProductInformation(){
	}
	
	
	@Test (enabled = false, description = "SRL-758")
	public void testDateOfManufatureResetStatus(){
		/*
		 * Статус сбрасывается на 0 для линков у которых action_type = 0
		 * Дата изготовления апдейтится у всех scales_productentity
		 */
	}
	
	
}
