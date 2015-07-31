package ru.crystals.set10.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import static ru.crystals.set10.utils.GoodGenerator.*;

@Test (groups = {"retail", "centrum"}  )
public class GoodsPropertiesWithLongValuesTest extends AbstractTest{

	GoodGenerator goodGenerator = new GoodGenerator();
	
	@BeforeClass
	public void setUpTestData(){
		goodGenerator.longValue = "Все хотят, чтобы: Импорт данных с превышением размера в полях плагинных свойств товара не должен приводить к ошибкам "
				+ "сервера и отклонению всего импортируемого пакета данных Acceptance: Корректно обрезать поля по следующим импортируемым плагинным свойствам "
				+ "для каждого типа товара. Обрезать значения текстовых полей, по размеру этих полей в соответствующих таблицах. 1. "
				+ "Разновидность товара: \"Штучный товар\" (ProductPieceEntityBillet) composition character varying(1024), "
				+ "– Состав продукта foodvalue character varying(255), – Пищевая ценность storageconditions character varying(255), – Условия хранения";
	}
	
	
	@DataProvider (name = "Goods",
			parallel = true)
	private Object[][] goods(){
		
		/*
		 * Плагинные свойства весового товара
		 */
		Good goodWeight = goodGenerator.generateWeightGood(GOODTYPE_WEIGHT);
		
		/*
		 * Плагинные свойства табачного товара
		 */
		Good goodCiggy = goodGenerator.generateGood(GOODTYPE_CIGGY);
		goodGenerator.addPluginProperty(goodCiggy, "composition", "composition" + goodGenerator.longValue + goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodCiggy, "storage-conditions", "storage-conditions" + goodGenerator.longValue);
		

		/*
		 * Плагинные свойства штучного товара
		 */
		Good goodPiece = goodGenerator.generateGood(GOODTYPE_PIECE);
		goodGenerator.addPluginProperty(goodPiece, "composition", "composition" + goodGenerator.longValue +  goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodPiece, "food-value", "food-value" + goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodPiece, "storage-conditions", "storage-conditions" + goodGenerator.longValue);
		
		/*
		 * Плагинные свойства алкогольного товара
		 */
		Good goodSpirit = goodGenerator.generateGood(GOODTYPE_SPIRIT);
		goodGenerator.addPluginProperty(goodSpirit, "composition", "Composition" + goodGenerator.longValue +  goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodSpirit, "food-value", "Food-value" + goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodSpirit, "storage-conditions", "Storage-conditions" + goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodSpirit, "sale-disabled-infos", "Sale-disabled-info" + goodGenerator.longValue);
		
		/*
		 * Плагинные свойства штучно весового товара
		 */
		Good goodPieceWeight = goodGenerator.generateGood(GOODTYPE_PIECE_WEIGHT);
		/*свойство Состав имеет ограничение 1024 символа*/
		goodGenerator.addPluginProperty(goodPieceWeight, "composition", "Composition_" + goodGenerator.longValue + goodGenerator.longValue);
		/*остальные свойства имеют ограничения в 255 символов*/
		goodGenerator.addPluginProperty(goodPieceWeight, "food-value", "Food_value_" + goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodPieceWeight, "storage-conditions", "Storage-condidions_" + goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodPieceWeight, "producer", "Producer_" + goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodPieceWeight, "button-on-scale",  "Button-on-scale" + goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodPieceWeight, "description-on-scale-screen",  "Description-on-scale-screen" + goodGenerator.longValue);
		goodGenerator.addPluginProperty(goodPieceWeight, "name-on-scale-screen",  "Name-on-scale-screen" + goodGenerator.longValue);
		
		return new Object[][]{
				{"Весовой товар", goodWeight},
				{"Тобачный товар", goodCiggy},
				{"Алкогольный товар", goodSpirit},
				{"Штучный товар", goodPieceWeight},
				{"Штучно-весовой товар", goodPiece}
		};
	}
	
	@Test (description = "SRL-853. Корректный импорт плагинных свойств товара с длинными значениями плагинных свойств (более 255 символов, более 1024 символов) ",
			dataProvider = "Goods"
			)
	public void testLongValuesPluginPropsWeightGood(String goodType, Good good){
		SoapRequestSender soapSender = new SoapRequestSender(TARGET_HOST);
		soapSender.sendGood(good);
		Assert.assertTrue(soapSender.assertSOAPResponse(SoapRequestSender.RETURN_MESSAGE_CORRECT, soapSender.getTi()), "Ошибка импорта товара с длинными значениями плагинных свойств");
	}
	

	
}
