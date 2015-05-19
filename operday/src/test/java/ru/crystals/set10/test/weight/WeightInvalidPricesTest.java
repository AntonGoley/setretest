package ru.crystals.set10.test.weight;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Price;

@Test(groups = {"retail"})
public class WeightInvalidPricesTest extends WeightAbstractTest { 
	
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
	}
	
	@Test (description = "SRTE-96. Весовой товар выгружается из весов, если пришла цена 1 равная 0")
	public void testUnloadGoodIf1stPriceZero(){
		int pluNum = pluNumber++;
		Good weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		soapSender.sendGood(weightGood);
		Assert.assertTrue(scales.waitPluLoaded(pluNum), "Товар не загрузился в весы. PLU = " + pluNum);
		
		List<Price> prices = weightGood.getPrices();
		for (int i=0; i<prices.size(); i++){
			if (prices.get(i).getNumber().equals(1L)){
				prices.get(i).setPrice(new BigDecimal("0"));
			}
		}
		
		soapSender.sendGood(weightGood);
		Assert.assertTrue(scales.waitPluUnLoaded(pluNum), "Товар не выгрузился из весов, если цена 1 равна 0. PLU = " + pluNum);
		
	}

	@Test (description = "SRTE-96. Весовой товар не загружается в весы, если начало срока действия 1й цены в будущем", 
			// файл весов должен быть создан, до запуска этого теста
			dependsOnMethods = "testUnloadGoodIf1stPriceZero")
	public void testGoodNotLoadedIfPriceInFuture(){
		int pluNum = pluNumber++;
		/* дата начала действия 1й цены через 8 часов*/
		long sinceDate = new Date().getTime() + 360000 * 8;
		
		Good weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		
		List<Price> prices = weightGood.getPrices();
		for (int i=0; i<prices.size(); i++){
			if (prices.get(i).getNumber().equals(1L)){
				prices.get(i).setSinceDate(goodGenerator.getDate(sinceDate));;
			}
		}
		
		soapSender.sendGood(weightGood);
		Assert.assertFalse(scales.waitPluLoaded(pluNum), "Товар не должен загрузиться в весы, если срок действия 1й цены в будущем. PLU = " + pluNum);
	}
	
}
