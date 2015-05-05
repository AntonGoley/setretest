package ru.crystals.set10.test.weight;

import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.scales.tech.core.scales.virtual.xml.PluType;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Price;



public class WeightPricesTest extends WeightAbstractTest{
	
	/*
	 * 	Цена 1 - базовая цена, действует без карты,
		Цена 2 - цена по карте, действует только по карте
		Цена 3 - акционная цена без карты, действует без карты
		Цена 4 - акционная цена по карте, действует только по карте
	 */
	Price price1;
	Price price2;
	Price price3;
	Price price4;
	Good weightGood;
	PluType plu;
	List<Price> prices;
	
	/*
	 * Задаем значения по умолчанию: 
	 * priceVal1 > priceVal2 > priceVal3 > priceVal4
	 */
	BigDecimal priceVal100 = new BigDecimal("100.99");
	BigDecimal priceVal200 = new BigDecimal("200.01");
	BigDecimal priceVal300 = new BigDecimal("300.11");
	BigDecimal priceVal400 = new BigDecimal("400.55");
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	@BeforeClass
	public void initData(){
		
		pluNumber++;
		
		scales.clearVScalesFileData();
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNumber));
		weightGood.getBarCodes().add(goodGenerator.generateWeightBarCode(Config.WEIGHT_BARCODE_PREFIX, 7));
		soapSender.sendGood(weightGood);
		plu = scales.getPlu(pluNumber);
		
		price1 = goodGenerator.generatePrice(1);
		price2 = goodGenerator.generatePrice(2);
		price3 = goodGenerator.generatePrice(3);
		price4 = goodGenerator.generatePrice(4);
	}
	
	@Test(description = "Товар содержит цену 1 и цену 2. ц1 > ц2. На весы выгружается Цена за кг = ц1, Цена за кг по карте = ц2",
			groups = "price12")
	public void testPrice1GeaterPrice2(){
		PluType expPlu = plu;
		price1.setPrice(priceVal200);
		price2.setPrice(priceVal100);
		
		weightGood.getPrices().clear();
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal200.toPlainString().replace(".", ""), "Цена за кг не равна цене 1!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal100.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 2!");
	
	}
	
	@Test (description =  "Товар содержит цену 1 и цену 2. ц1 < ц2. ??? Прояснить, как должна обрабатываться ситуация",
			groups = "price12",
			enabled = false)
	public void testPrice1LessPrice2(){
		PluType expPlu = plu;
		price1.setPrice(priceVal100);
		price2.setPrice(priceVal200);
		
		prices = weightGood.getPrices();
		weightGood.getPrices().removeAll(prices);
		
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price1);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal200.toPlainString().replace(".", ""), "Цена за кг не равна цене 2!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal100.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 1!");
	
	}
	
	@Test (description =  "Товар содержит цену 1 и цену 2. ц1 = ц2.", 
			groups = "price12")
	public void testPrice1EqualsPrice2(){
		PluType expPlu = plu;
		price1.setPrice(priceVal100);
		price2.setPrice(priceVal100);
		
		prices = weightGood.getPrices();
		weightGood.getPrices().removeAll(prices);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal100.toPlainString().replace(".", ""), "Цена за кг не равна цене 1!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal100.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 2!");
	
	}
	
	@Test (description =  "Товар содержит цену 1, 2 и 3. ц1>ц2, ц1>ц3, ц2<ц3. Цена за кг = ц3. Цена за кг по карте = ц2",
			dependsOnGroups = "price12", 
			groups = "price123",
			alwaysRun = true)
	public void testPrice1Price3GreaterPrice2(){
		PluType expPlu = plu;
		price1.setPrice(priceVal300);
		price2.setPrice(priceVal100);
		price3.setPrice(priceVal200);
		
		prices = weightGood.getPrices();
		weightGood.getPrices().removeAll(prices);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price3);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal200.toPlainString().replace(".", ""), "Цена за кг не равна цене 3!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal100.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 2!");
	}
	
	@Test (description =  "Товар содержит цену 1, 2 и 3. ц3<ц1, ц3<ц2. Цена за кг = ц3. Цена за кг по карте = ц3",
			dependsOnGroups = "price12", 
			groups = "price123",
			alwaysRun = true)
	public void testPrice3LessPrice1Price2(){
		PluType expPlu = plu;
		price1.setPrice(priceVal300);
		price2.setPrice(priceVal200);
		price3.setPrice(priceVal100);
		
		prices = weightGood.getPrices();
		weightGood.getPrices().removeAll(prices);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price3);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal100.toPlainString().replace(".", ""), "Цена за кг не равна цене 3!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal100.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 3!");
	
	}
	
	@Test (description =  "Товар содержит цену 1, 2 и 4. ц1>ц2, ц4<ц2. Цена за кг = ц1. Цена за кг по карте = ц4",
			dependsOnGroups = "price123", 
			alwaysRun = true)
	public void testPrice1Price2GreaterPrice4(){
		PluType expPlu = plu;
		price1.setPrice(priceVal300);
		price2.setPrice(priceVal200);
		price4.setPrice(priceVal100);
		// удаляем цену 3, чтобы она не учитывалась
		price3.setDeleted(true);
		
		prices = weightGood.getPrices();
		weightGood.getPrices().removeAll(prices);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price4);
		weightGood.getPrices().add(price3);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal300.toPlainString().replace(".", ""), "Цена за кг не равна цене 1!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal100.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 4!");
	}
	
	@Test (description =  "Товар содержит цену 1, 2, 3 и 4. ц3>ц2, ц3<ц1, ц4<3. Цена за кг = ц3. Цена за кг по карте = ц4",
			dependsOnMethods = "testPrice1Price2GreaterPrice4", 
			alwaysRun = true)
	public void testPrice1Price2Price3Price4(){
		PluType expPlu = plu;
		price1.setPrice(priceVal400);
		price2.setPrice(priceVal200);
		price3.setPrice(priceVal300);
		price4.setPrice(priceVal100);
		
		prices = weightGood.getPrices();
		weightGood.getPrices().removeAll(prices);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price4);
		weightGood.getPrices().add(price3);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal300.toPlainString().replace(".", ""), "Цена за кг не равна цене 3!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal100.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 4!");
	
	}
	
}
