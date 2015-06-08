package ru.crystals.set10.test.weight;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.Department;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.GoodsCatalog;
import ru.crystals.setretailx.products.catalog.Price;

@Test(groups = {"retail"})
public class WeightLoadTest extends WeightAbstractTest { 
	
	protected static final Logger log = Logger.getLogger(WeightLoadTest.class);
	
	private int goodsAmount = 1000;
	
	List<Good> goods = new ArrayList<Good>(goodsAmount);
	GoodsCatalog goodsCatalog;
	
	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	
	@BeforeClass
	public void initData(){
		//prerareSuite();
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		Good good;
		Price price1;
		
		for (int i=0; i<goodsAmount; i++){
			good = goodGenerator.generateWeightGood(String.valueOf(pluNumber++));
			
			for (int y=0; y<good.getPrices().size(); y++){
				Price price = good.getPrices().get(y);
				if (price.getNumber().equals(1L)){
					price1 =  new Price();
					price1.setNumber(1L);
					price1.setPrice(price.getPrice());
					price1.setSinceDate(price.getSinceDate());
					price1.setTillDate(price.getSinceDate());
					
					
					Department department = new Department();
					department.setName("Отдел 2");
					department.setNumber(2L);
					price1.setDepartment(department);
					good.getPrices().add(price1);
					goods.add(good);
					break;
				}
			}
			
		}
	}
	
	@Test (description = "Отправка 2000 весовых товаров в весы")
	public void testLoad2000GoodsToScales(){
		goodsCatalog = new GoodsCatalog();
		goodsCatalog.getGoods().addAll(goods);
		soapSender.send(goodsCatalog);
	}
	
	@Test (description = "Прогрузка изменения цены для 2000 товаров")
	public void testUpdate2000PricesForWeightGoods(){
		Good good;
		Price price;
		BigDecimal priceOfset = new BigDecimal("10.00");
		
		for (int i=0; i<goods.size(); i++){
			good = goods.get(i);
			
			for (int y=0; y<good.getPrices().size(); y++){
				price = good.getPrices().get(y);
				if (price.getNumber().equals(1L)){
					price.setPrice(price.getPrice().add(priceOfset));
				}
			}
		}
		soapSender.send(goodsCatalog);
	}
	
}
