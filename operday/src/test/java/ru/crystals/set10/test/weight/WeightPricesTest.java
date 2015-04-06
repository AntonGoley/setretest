package ru.crystals.set10.test.weight;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.crystals.scales.tech.core.scales.virtual.xml.LinkToPluType;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;



public class WeightPricesTest extends WeightAbstractTest{

	
SoapRequestSender soapSender = new SoapRequestSender();
	
	PluParserInterface pluParser;
	HashMap<String, String> weightGood = new HashMap<String, String>();
	
	
	@BeforeClass
	public void initData(){
		scales.clearVScalesFileData();
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		weightGood = generateGoodData();
		weightGood.put("${plu-number}", "9");
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
	}
	
	@BeforeMethod
	public void clearScales(){
		//scales.clearVScalesFileData();
	}
	
	
	@Test()
	public void testUnloadPriceIfPriceBanSelling(){
		
		Assert.assertEquals(scales.waitPluActionType(weightGood.get(PLU_NUMBER_PARAM), ACTION_TYPE_LOAD), 
				ACTION_TYPE_LOAD, "Товар не загрузился в весы");
		
		
		scales.clearVScalesFileData();
		long now = System.currentTimeMillis();
		weightGood.put(GOOD_PRICE1_PARAM, "20.01");
		weightGood.put(GOOD_PRICE1_BEGIN_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day/2));
		weightGood.put(GOOD_PRICE1_END_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now + day));
		
		weightGood.put(GOOD_PRICE2_PARAM, "10.01");
		weightGood.put(GOOD_PRICE2_BEGIN_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now - day/2));
		weightGood.put(GOOD_PRICE2_END_DATE_PARAM, DisinsectorTools.getDate(LECOND_DATE_FORMAT, now + day));
		
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		
		
//		Assert.assertEquals(scales.getPluParameterExpectedValue(weightGood.get(PLU_NUMBER_PARAM), price1Parser(), "2001"), 
//				"2001", "Не выгрузилась новая цена 1");
//		
//		Assert.assertEquals(scales.getPluParameterExpectedValue(weightGood.get(PLU_NUMBER_PARAM), price2Parser(), "1001"), 
//				"1001", "Не выгрузилась новая цена 2");
	}
	

	public PluParserInterface price1Parser(){
		return new PluParserInterface() {
			
			@Override
			public String getParameter(LinkToPluType linkToPlu) {
				linkToPlu.getPlu().getPrice();
				return null;
			}
		};
		
	}
	
	public PluParserInterface price2Parser(){
		return new PluParserInterface() {
			
			@Override
			public String getParameter(LinkToPluType linkToPlu) {
				linkToPlu.getPlu().getExPrice();
				return null;
			}
		};
		
	}
	
	
}
