package ru.crystals.set10.test.weight;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;

public class UnloadWeightWithInvalidPrice extends WeightAbstractTest { 
	
	
	SoapRequestSender soapSender = new SoapRequestSender();
	LinkToPluProcessor processor;
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@BeforeMethod
	public void clearScales(){
		scales.clearVScalesFileData();
	}
	
	
	@Test (description = "Весовой товар выгружается из весов, если пришла цена 1 равная 0")
	public void testUnloadGoodIf1stPriceZero(){
		HashMap<String, String> weightGood = new HashMap<String, String>();
		weightGood = generateGoodData();
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		
		Assert.assertEquals(scales.getPluActionType(weightGood.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_LOAD, "Товар 1 не выгружен из весов");
		
		/*
		 * Отправить товар с ценой 1=0
		 */
		scales.clearVScalesFileData();
		weightGood.put(GOOD_PRICE1_PARAM, "0.00");
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		
		Assert.assertEquals(scales.getPluActionType(weightGood.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_CLEAR, "Товар c 0 ценой не выгружен из весов");
	}
	
	@Test
	public void testUnloadPriceIfPriceBanSelling(){
		HashMap<String, String> weightGood = new HashMap<String, String>();
		weightGood = generateGoodData();
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		
		Assert.assertEquals(scales.getPluActionType(weightGood.get(PLU_NUMBER_PARAM)), 
				ACTION_TYPE_LOAD, "Товар 1 не выгружен из весов");
		
		/*
		 * Отправить товар с ценой 1=0
		 */
		scales.clearVScalesFileData();
		long now = System.currentTimeMillis();
		weightGood.put(GOOD_PRICE1_PARAM, "20.01");
		weightGood.put(GOOD_PRICE1_BEGIN_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day/2));
		weightGood.put(GOOD_PRICE1_END_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now + day));
		
		weightGood.put(GOOD_PRICE2_PARAM, "10.01");
		weightGood.put(GOOD_PRICE2_BEGIN_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day/2));
		weightGood.put(GOOD_PRICE2_END_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now + day));
		
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		
		Assert.assertEquals(scales.getPluPriceValue(weightGood.get(PLU_NUMBER_PARAM), "price1", "2001"), 
				"2001", "Не выгрузилась новая цена 1");
		
		Assert.assertEquals(scales.getPluPriceValue(weightGood.get(PLU_NUMBER_PARAM), "price2", "1001"), 
				"1001", "Не выгрузилась новая цена 2");
	}
	
	@Test
	public void testUnLoadPriceIfNewPriceStartsFromNow(){
	}
	
}
