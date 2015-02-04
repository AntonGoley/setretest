package ru.crystals.set10.test.weight;

import java.util.HashMap;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;


public class WeightBaseTest extends WeightAbstractTest { 
	
	/*
	 * Товары для провеки 2-х различных способов выгрузки из весов
	 */
	HashMap<String, String> weightGood_1 = new HashMap<String, String>();
	HashMap<String, String> weightGood_2 = new HashMap<String, String>();

	SoapRequestSender soapSender = new SoapRequestSender();
	
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		weightGood_1 = generateGoodData();
		weightGood_2 = generateGoodData();
		
		scales.clearVScalesFileData();
		/*
		 * Прогружаем 2 товара для проверки загрузки/выгрузки из весов
		 */
		weightGood_1 = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood_1);
		weightGood_2 = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood_2);
		
	}
	
	//TODO: сделать одновременную загрузку и выгрузку товаров
	
	@Test (description = "Весовой товар загружается в весы")
	public void testGoodWeightLoadToScales(){
		Assert.assertEquals(scales.getPluActionType(weightGood_1.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_LOAD, "Товар 1 не загружен в весы");
		Assert.assertEquals(scales.getPluActionType(weightGood_2.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_LOAD, "Товар 2 не загружен в весы");
	}
	
	@Test (description = "Весовой товар выгружается из весов, если у баркода поле do-not-send-to-scales=true",
			dependsOnMethods = "testGoodWeightLoadToScales")
	public void testGoodWeightUnloadFromScalesBarCode(){
		scales.clearVScalesFileData();
		weightGood_1.put(DONT_SENT_TO_SCALES_PARAM, "true");
		weightGood_1 = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood_1);

		Assert.assertEquals(scales.getPluActionType(weightGood_1.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_CLEAR, "Товар не выгружен из весов");
	}
	
	@Test (description = "Весовой товар выгружается из весов, если у товара поле delete-from-cash true",
			dependsOnMethods = "testGoodWeightLoadToScales")
	public void testGoodWeightUnloadFromScalesIfDeleteFromCache(){
		scales.clearVScalesFileData();
		weightGood_2.put(DELETE_FROM_CASHE_PARAM, "true");
		weightGood_2 = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood_2);

		Assert.assertEquals(scales.getPluActionType(weightGood_2.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_CLEAR, "Товар не выгружен из весов");
	}
	
	
	
	
	
	
	
	
}
