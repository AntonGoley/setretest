package ru.crystals.set10.test.weight;

import java.util.HashMap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.SoapRequestSender;

public class UnloadWeightWithInvalidPrice extends WeightAbstractTest { 
	
	
	SoapRequestSender soapSender = new SoapRequestSender();

	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@BeforeMethod
	public void clearScales(){
		scales.clearVScalesFileData();
	}
	
	
	@Test
	public void testUnloadGoodIf1stPriceZero(){
		
	}
	
	@Test
	public void testUnloadGoodIfPriceBanSelling(){
		
	}
	
	@Test
	public void testUnLoadPriceIfNewPriceStartsFromNow(){
		
	}

	
}
