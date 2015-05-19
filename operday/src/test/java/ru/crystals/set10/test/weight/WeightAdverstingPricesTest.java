package ru.crystals.set10.test.weight;

import java.math.BigDecimal;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.crystals.scales.tech.core.scales.virtual.xml.PluType;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Price;


@Test(groups = {"retail"})
public class WeightAdverstingPricesTest extends WeightAbstractTest{
	
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
	 * Значение цен
	 */
	BigDecimal priceVal1; 
	BigDecimal priceVal2; 
	BigDecimal priceVal3; 
	BigDecimal priceVal4; 
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	@BeforeClass
	public void initData(){
		/* тесты проводятся на одном товаре с PLU = pluNum */
		int pluNum = pluNumber++;
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		
		weightGood = goodGenerator.generateWeightGood(String.valueOf(pluNum));
		soapSender.sendGood(weightGood);
		plu = scales.getPlu(pluNum);
	}
	
	@BeforeMethod
	public void resetPricesValues(){
		/* удалить цены у товара */
		List<Price> prices = weightGood.getPrices();
		weightGood.getPrices().removeAll(prices);
		
		/* сгенерить новые действующие цены*/
		price1 = goodGenerator.generatePrice(1);
		price2 = goodGenerator.generatePrice(2);
		price3 = goodGenerator.generatePrice(3);
		price4 = goodGenerator.generatePrice(4);
		
	}
	
	@Test(description = "SRTE-93. Товар содержит цену 1 и цену 2. ц1 > ц2. На весы выгружается Цена за кг = ц1, Цена за кг по карте = ц2",
			groups = "price12")
	public void testPrice1GeaterPrice2(){
		PluType expPlu = plu;
		
		priceVal1 = new BigDecimal("10.99");
		priceVal2 = new BigDecimal("9.99");
		
		price1.setPrice(priceVal1);
		price2.setPrice(priceVal2);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal1.toPlainString().replace(".", ""), "Цена за кг не равна цене 1!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal2.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 2!");
	
	}
	
	@Test (description =  "SRTE-93. Товар содержит цену 1 и цену 2. ц1 < ц2. ??? Прояснить, как должна обрабатываться ситуация",
			groups = "price12",
			enabled = false)
	public void testPrice1LessPrice2(){
		PluType expPlu = plu;
		priceVal1 = new BigDecimal("15.55");
		priceVal2 = new BigDecimal("16.66");
		
		price1.setPrice(priceVal1);
		price2.setPrice(priceVal2);
		
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price1);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal2.toPlainString().replace(".", ""), "Цена за кг не равна цене 2!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal1.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 1!");
	
	}
	
	@Test (description =  "SRTE-93. Товар содержит цену 1 и цену 2. ц1 = ц2.", 
			groups = "price12")
	public void testPrice1EqualsPrice2(){
		PluType expPlu = plu;
		
		priceVal1 = new BigDecimal("25.55");
		priceVal2 = new BigDecimal("25.55");
		
		price1.setPrice(priceVal1);
		price2.setPrice(priceVal2);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal1.toPlainString().replace(".", ""), "Цена за кг не равна цене 1!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal2.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 2!");
	
	}
	
	@Test (description =  "SRTE-93. Товар содержит цену 1, 2 и 3. ц1>ц2, ц1>ц3, ц2<ц3. Цена за кг = ц3. Цена за кг по карте = ц2",
			dependsOnGroups = "price12", 
			groups = "price123",
			alwaysRun = true)
	public void testPrice1Price3GreaterPrice2(){
		PluType expPlu = plu;
		
		priceVal1 = new BigDecimal("35.33");
		priceVal2 = new BigDecimal("31.11");
		priceVal3 = new BigDecimal("33.12");
		
		price1.setPrice(priceVal1);
		price2.setPrice(priceVal2);
		price3.setPrice(priceVal3);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price3);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal3.toPlainString().replace(".", ""), "Цена за кг не равна цене 3!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal2.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 2!");
	}
	
	@Test (description =  "SRTE-93. Товар содержит цену 1, 2 и 3. ц3<ц1, ц3<ц2. Цена за кг = ц3. Цена за кг по карте = ц3",
			dependsOnGroups = "price12", 
			groups = "price123",
			alwaysRun = true)
	public void testPrice3LessPrice1Price2(){
		PluType expPlu = plu;
		
		priceVal1 = new BigDecimal("144.44");
		priceVal2 = new BigDecimal("143.11");
		priceVal3 = new BigDecimal("142.43");
		
		price1.setPrice(priceVal1);
		price2.setPrice(priceVal2);
		price3.setPrice(priceVal3);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price3);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal3.toPlainString().replace(".", ""), "Цена за кг не равна цене 3!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal3.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 3!");
	
	}
	
	@Test (description =  "SRTE-93. Товар содержит цену 1, 2 и 4. ц1>ц2, ц4<ц2. Цена за кг = ц1. Цена за кг по карте = ц4",
			dependsOnGroups = "price123", 
			alwaysRun = true)
	public void testPrice1Price2GreaterPrice4(){
		PluType expPlu = plu;
		
		priceVal1 = new BigDecimal("200.78");
		priceVal2 = new BigDecimal("198.12");
		priceVal4 = new BigDecimal("112.10");
		
		price1.setPrice(priceVal1);
		price2.setPrice(priceVal2);
		price4.setPrice(priceVal4);
		
		price3.setDeleted(true);

		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price4);
		weightGood.getPrices().add(price3);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal1.toPlainString().replace(".", ""), "Цена за кг не равна цене 1!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal4.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 4!");
	}
	
	@Test (description =  "SRTE-93. Товар содержит цену 1, 2, 3 и 4. ц3>ц2, ц3<ц1, ц4<3. Цена за кг = ц3. Цена за кг по карте = ц4",
			dependsOnMethods = "testPrice1Price2GreaterPrice4", 
			alwaysRun = true)
	public void testPrice1Price2Price3Price4(){
		PluType expPlu = plu;
		
		priceVal1 = new BigDecimal("510.18");
		priceVal2 = new BigDecimal("450.22");
		priceVal3 = new BigDecimal("500.00");
		priceVal4 = new BigDecimal("99.10");
		
		price1.setPrice(priceVal1);
		price2.setPrice(priceVal2);
		price3.setPrice(priceVal3);
		price4.setPrice(priceVal4);
		
		weightGood.getPrices().add(price1);
		weightGood.getPrices().add(price2);
		weightGood.getPrices().add(price4);
		weightGood.getPrices().add(price3);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal3.toPlainString().replace(".", ""), "Цена за кг не равна цене 3!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal4.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 4!");
	
	}
	
}
