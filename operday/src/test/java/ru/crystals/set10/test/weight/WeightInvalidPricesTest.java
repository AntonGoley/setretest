package ru.crystals.set10.test.weight;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.SoapRequestSender;

public class WeightInvalidPricesTest extends WeightAbstractTest { 
	
	
	SoapRequestSender soapSender = new SoapRequestSender();
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@BeforeMethod
	public void clearScales(){
	}
	
	
	@Test (description = "SRTE-96. Весовой товар выгружается из весов, если пришла цена 1 равная 0")
	public void testUnloadGoodIf1stPriceZero(){

	}
	
	@Test (description = "SRTE-96. Весовой товар выгружается из весов, если приходит срок завершения продажи, меньший, чем текущая дата",
			enabled = false)
	public void testUnloadPriceIfPriceBanSelling(){
	}
	
	@Test (description = "SRTE-96. Весовой товар не загружается в весы, если начало срока продажи в будущем",
			enabled = false)
	public void testGoodNotLoadedIfPriceInFuture(){
	}
	
}
