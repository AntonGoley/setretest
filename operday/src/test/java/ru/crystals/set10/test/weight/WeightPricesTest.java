package ru.crystals.set10.test.weight;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.scales.tech.core.scales.virtual.xml.PluType;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Price;



public class WeightPricesTest extends WeightAbstractTest{

	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	Good weightGood;
	
	BigDecimal priceVal1 = new BigDecimal("100.99");
	BigDecimal priceVal2 = new BigDecimal("90.01");
	BigDecimal priceVal3 = new BigDecimal("80.11");
	BigDecimal priceVal4 = new BigDecimal("70.55");
	
	Price price1;
	Price price2;
	Price price3;
	Price price4;
	
	int pluNumber = 16;
	
	PluType plu;
	
	List<Price> prices;
	
	@BeforeClass
	public void initData(){
		
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
	
	@Test()
	public void test(){
		PluType expPlu = plu;
		price1.setPrice(priceVal2);
		price2.setPrice(priceVal1);
		price3.setPrice(priceVal3);
		
		prices = weightGood.getPrices();
		weightGood.getPrices().removeAll(prices);
		
		weightGood.getPrices().add(0, price2);
		weightGood.getPrices().add(1, price1);
		weightGood.getPrices().add(2,price3);
		
		soapSender.sendGood(weightGood);
		plu = scales.getPluUpdated(expPlu);
		
		log.info(priceVal1.toPlainString().replace(".", ""));
		log.info(priceVal3.toPlainString().replace(".", ""));
		
		Assert.assertEquals(String.valueOf(plu.getPrice()), priceVal2.toPlainString().replace(".", ""), "Цена за кг не равна цене 3!");
		Assert.assertEquals(String.valueOf(plu.getExPrice()), priceVal1.toPlainString().replace(".", ""), "Цена за кг по карте не равна цене 1!");
		
		plu.getPrice();
		
	
	}
	
}
