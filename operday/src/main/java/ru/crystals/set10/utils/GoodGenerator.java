package ru.crystals.set10.utils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

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
	
	private static long prefix = System.currentTimeMillis();
	private String goodprefix;
	
	public static String GOODTYPE_WEIGHT = "ProductWeightEntity";
	public static String GOODTYPE_PIECE = "ProductPieceEntity";
	public static String GOODTYPE_SPIRIT = "ProductSpiritsEntity";
	public static String GOODTYPE_CIGGY = "ProductCiggyEntity";
	

	public Good generateWeightGoodWithNoBarCode(String pluNumber){
		Good weightGood = generateGood(GOODTYPE_WEIGHT);
		weightGood.getPluginProperties().add(generatePluginProperty("plu-number", pluNumber));
		
		weightGood.getPluginProperties().add(generatePluginProperty("composition", "Composition_" + weightGood.getMarkingOfTheGood() + "_PLU = "+ pluNumber));
		weightGood.getPluginProperties().add(generatePluginProperty("food-value", "Food_value_" + weightGood.getMarkingOfTheGood() + "_PLU = "+ pluNumber));
		weightGood.getPluginProperties().add(generatePluginProperty("storage-conditions", "Storage-condidions_" + weightGood.getMarkingOfTheGood() + "_PLU = "+ pluNumber));
		weightGood.getPluginProperties().add(generatePluginProperty("producer", "Producer_" + weightGood.getMarkingOfTheGood() + "_PLU="+ pluNumber));
		
		weightGood.getMeasure().setCode("2");
		weightGood.getMeasure().setName("кг.");
		return weightGood;
	}
	
	public Good generateWeightGood(String pluNumber){
		Good weightGood = generateWeightGoodWithNoBarCode(pluNumber);
		/* генерим по умолчанию дополнительный весовой бар код*/
		weightGood.getBarCodes().add(generateWeightBarCode(Config.WEIGHT_BARCODE_PREFIX));
		return weightGood;
	}
	
	/*
	 * Возвращает автоматически сгенерированный PLU, 
	 */
	public int  getWeightAutoGenPlu(BarCode weightCode, String ofset){
		return Integer.valueOf(weightCode.getCode().substring(2, 7)) + Integer.valueOf(ofset);
	}
	
	
	public Good generateGood(String goodType){
		goodprefix = String.valueOf(prefix++).substring(7);
		Good good = new Good();
		good.setName("Товар_" + goodprefix);
		good.setMarkingOfTheGood(goodprefix);
		good.setCertificationType(4);
		good.setDeleteFromCash(false);
		good.setErpCode(goodprefix);
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
		
		Price price1 = generatePrice(1L);
		good.getPrices().add(price1);
		good.getPrices().add(generatePriceWithValue(2L, price1.getPrice().subtract(new BigDecimal("5.99")))); /*цена 1 генерится не меньше 10*/
		return good;
	}
	
	public PluginProperty generatePluginProperty(String key, String value){
		PluginProperty property = new PluginProperty();
		property.setKey(key);
		property.setValue(value);
		return property;
	}
	
	/*
	 * Генерация баркода весового товара: prefix + 5 первых знаков кода товара 
	 */
	public BarCode generateWeightBarCode(String barcodePrefix){
		BarCode bc = new BarCode();
		bc.setCode(barcodePrefix + goodprefix.substring(1));
		bc.setCount(new BigDecimal(1));
		bc.setDefaultCode(false);
		goodprefix = String.valueOf(prefix++);
		return bc;
	}
	
	public int getWeightPluNumber(Good weightGood){
		Iterator<PluginProperty> it =  weightGood.getPluginProperties().iterator();
		while (it.hasNext()){
			PluginProperty property;
			property = it.next();
			if (property.getKey().equals("plu-number")){
				return Integer.valueOf(property.getValue());
			}
		}
		/* если нет PluginProperty, возвращаем 0*/
		return 0; 
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
	
	public Price generatePriceWithValue(long number, BigDecimal value){
		Price price =  generatePrice(number);
		price.setPrice(value);
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
