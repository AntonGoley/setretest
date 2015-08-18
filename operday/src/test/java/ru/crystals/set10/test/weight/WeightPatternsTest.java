package ru.crystals.set10.test.weight;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.GoodGenerator;
import ru.crystals.set10.utils.SoapRequestSender;
import ru.crystals.setretailx.products.catalog.BarCode;
import ru.crystals.setretailx.products.catalog.BarcodeExt;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.GoodsCatalog;


@Test(groups = {"retail"})
public class WeightPatternsTest extends WeightAbstractTest { 
	

	SoapRequestSender soapSender = new SoapRequestSender();
	GoodGenerator goodGenerator = new GoodGenerator();
	Good weightGood1;
	Good weightGood2;
	Good weightGood3;
	
	Long groupValue = DisinsectorTools.random(900) + 100; 
	String group1 = String.valueOf(groupValue + 1L);
	String group2 =  String.valueOf(groupValue + 2L);
	String group3 =  String.valueOf(groupValue + 3L);
	
	
	@BeforeClass
	public void initData(){
		soapSender.setSoapServiceIP(Config.RETAIL_HOST);
		GoodsCatalog goodsCatalog = new GoodsCatalog();
		List<Good> goods = new ArrayList<Good>(0);
		
		
		weightGood1 = goodGenerator.generateWeightGood(GoodGenerator.GOODTYPE_WEIGHT);
		BarcodeExt barCodeDeleted = goodGenerator.generateBarcode();
		barCodeDeleted.setDefaultCode(false);
		barCodeDeleted.setDeleted(true);
		weightGood1.getBarCodes().add(barCodeDeleted);
		soapSender.sendGood(weightGood1);
	
		barCodeDeleted.setDeleted(false);
		soapSender.sendBarcode((BarcodeExt) barCodeDeleted);
		
		for (int i=1; i<5; i++){
		
			/*сгенерить товары с 3 разными номерами групп*/
			weightGood1 = goodGenerator.generateWeightGood(GoodGenerator.GOODTYPE_WEIGHT);
			weightGood1.getGroup().setCode(group1);
			weightGood1.getGroup().setName(group1);
			
			/*сгенерить товары с 3 разными номерами групп*/
			weightGood2 = goodGenerator. generateWeightGood(GoodGenerator.GOODTYPE_WEIGHT);
			weightGood2.getGroup().setCode(group2);
			weightGood2.getGroup().setName(group2);
			
			/*сгенерить товары с 3 разными номерами групп*/
			weightGood3 = goodGenerator.generateWeightGood(GoodGenerator.GOODTYPE_WEIGHT);
			weightGood3.getGroup().setCode(group3);
			weightGood3.getGroup().setName(group3);
			goods.add(weightGood1);
			goods.add(weightGood2);
			goods.add(weightGood3);
		}
		goodsCatalog.getGoods().addAll(goods);
		soapSender.send(goodsCatalog);
		
	}
	
	
	@Test ( description = "SRL-717")
	public void testGood(){
		
	}
	
	
	@Test (enabled = false, description = "SRB-1202. ")
	public void testGoodWeightLoadToScales(){
	}
	
}
