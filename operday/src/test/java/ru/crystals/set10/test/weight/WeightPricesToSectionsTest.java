package ru.crystals.set10.test.weight;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;


@Test(groups = {"retail"})
public class WeightPricesToSectionsTest extends WeightAbstractTest { 
	

	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		
	}
	
	@Test (description = "SRB-1202. ")
	public void testGoodWeightLoadToScales(){
	}
	
}
