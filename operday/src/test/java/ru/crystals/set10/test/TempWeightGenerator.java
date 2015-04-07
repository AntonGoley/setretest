package ru.crystals.set10.test;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.test.weight.WeightAbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;

public class TempWeightGenerator extends WeightAbstractTest{
	
	protected static final Logger log = Logger.getLogger(WeightAbstractTest.class);
	

	String WEIGHT_GOOD_FILE = "/weight/weight_generator.txt";
	HashMap<String, String> weightGood;
	
	SoapRequestSender soapSender = new  SoapRequestSender();
	
	@BeforeTest
	public void setup(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@Test
	public void weightGoodGenerator(){
		weightGood = generateGoodData();
		weightGood.put("${group_id}", "100501");
		weightGood.put("${parent_group_id}", "10050");

		log.info(weightGood);
		
		soapSender.sendGoods(DisinsectorTools.getFileContentAsString(WEIGHT_GOOD_FILE), weightGood);
		
	}
	
	
}
