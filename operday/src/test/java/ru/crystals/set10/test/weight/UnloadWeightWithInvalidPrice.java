package ru.crystals.set10.test.weight;

import java.util.HashMap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;

public class UnloadWeightWithInvalidPrice extends WeightAbstractTest { 

	HashMap<String, String> weightGood = new HashMap<String, String>();
	SoapRequestSender soapSender = new SoapRequestSender();
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		scales.clearVScalesFileData();
	}
	
	
	@Test (description = "Весовой товар загружается в весы")
	public void testGoodWeightLoadToScales(){
		weightGood = generateGoodData();
		weightGood = soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		scales.readVirtualScales();
	}
	
	@Test (description = "Весовой товар выгружается из весов")
	public void testGoodWeightUnLoadFromScales(){
		
	}
	
	
	
	
	
	
	
	
}
