package ru.crystals.set10.utils;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ru.crystals.set10.config.Config;
import ru.crystals.setretailx.products.catalog.BarCode;
import ru.crystals.setretailx.products.catalog.Department;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.GoodsGroup;
import ru.crystals.setretailx.products.catalog.Measure;
import ru.crystals.setretailx.products.catalog.PluginProperty;
import ru.crystals.setretailx.products.catalog.Price;


public class GoodGenerator {
	
	private static long prefix = System.currentTimeMillis();;
	private String goodprefix;
	
	public static String GOODTYPE_WEIGHT = "ProductWeightEntity";
	public static String GOODTYPE_PIECE = "ProductPieceEntity";
	public static String GOODTYPE_SPIRIT = "ProductSpiritsEntity";
	public static String GOODTYPE_CIGGY = "ProductCiggyEntity";
	
	
	
	public Good generateWeightGood(String pluNumber){
		Good weightGood = generateGood(GOODTYPE_WEIGHT);
		weightGood.getPluginProperties().add(generatePluginProperty("plu-number", pluNumber));
		/* генерим по умолчанию дополнительный весовой бар код*/
		weightGood.getBarCodes().add(generateWeightBarCode(Config.WEIGHT_BARCODE_PREFIX, 7));
		weightGood.getMeasure().setCode("2");
		weightGood.getMeasure().setName("кг.");;
		return weightGood;
	}
	
	private Good generateGood(String goodType){
		goodprefix = String.valueOf(prefix++);
		Good good = new Good();
		good.setName("Товар_" + goodprefix);
		good.setMarkingOfTheGood(goodprefix);
		good.setCertificationType(4);
		good.setDeleteFromCash(false);
		good.setErpCode(goodprefix.substring(8, 13));
		good.setFullname("Товар_" + goodprefix + "_полное имя товара - (fullname)");
		good.setVat((float) 18.00);
		good.setProductType(goodType);
		good.setGroup(generateGrop());
		
		BarCode bc = new BarCode();
		bc.setCode(goodprefix);
		bc.setCount(new BigDecimal(1));
		bc.setDefaultCode(true);
		good.getBarCodes().add(bc);
		
		/*
		 * spirit - 14
		 * weight - 2
		 * piece - 7
		 * ciggy - 16
		 */
		Measure measure = new Measure();
		measure.setCode("7");
		measure.setName("шт.");
		good.setMeasure(measure);
		
		good.getPrices().add(generatePrice(1L));
		good.getPrices().add(generatePrice(2L));
		
		return good;
	}
	
	public PluginProperty generatePluginProperty(String key, String value){
		PluginProperty property = new PluginProperty();
		property.setKey(key);
		property.setValue(value);
		return property;
	}
	
	public BarCode generateWeightBarCode(String barcodePrefix, int barcodeLength){
		BarCode bc = new BarCode();
		bc.setCode(barcodePrefix + goodprefix.substring(13 - (barcodeLength - 2)));
		bc.setCount(new BigDecimal(1));
		bc.setDefaultCode(false);
		return bc;
	}
	
	private GoodsGroup generateGrop(){
		GoodsGroup mainGroup = new GoodsGroup(); 
		mainGroup.setCode("52");
		mainGroup.setName("Группа 52");
		
		GoodsGroup group = new GoodsGroup(); 
		group.setCode("52001");
		group.setName("Группа 5201");
		group.setParent(mainGroup);
		return group;
	}
	
	public Price generatePrice(long number){
		long rub = 0;
		long kop = 0;
		XMLGregorianCalendar dateSince = null;
		XMLGregorianCalendar dateFrom = null;
		
		dateSince = getDate(new Date().getTime() - 86400000 * 10);
		dateFrom = getDate(new Date().getTime() + 86400000 * 10);
		
		Department dep = new Department();
		dep.setName("Отдел 1");
		dep.setNumber(1L);
		
		Price price = new Price();
		price.setNumber(number);
		price.setSinceDate(dateSince);
		price.setTillDate(dateFrom);
		price.setDepartment(dep);
		rub = DisinsectorTools.random(10000) + 10;
		kop = DisinsectorTools.random(95) + 5;
		price.setPrice(new BigDecimal(String.valueOf(rub) + "." + String.valueOf(kop)));
		
		return price;
	}
	
	public XMLGregorianCalendar getDate(long date){
		XMLGregorianCalendar result = null;
		String strDateRepresentation = "";
		strDateRepresentation = DisinsectorTools.getDate("yyyy-MM-dd", date) + "T" + DisinsectorTools.getDate("HH:mm:ss", date);
		try {
			result =  DatatypeFactory.newInstance().newXMLGregorianCalendar(strDateRepresentation);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
