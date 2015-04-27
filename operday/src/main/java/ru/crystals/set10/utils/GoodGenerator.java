package ru.crystals.set10.utils;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ru.crystals.setretailx.products.catalog.BarCode;
import ru.crystals.setretailx.products.catalog.Department;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.GoodsGroup;
import ru.crystals.setretailx.products.catalog.Price;


public class GoodGenerator {
	
	private GoodsCatalog goodsCatalog;
	private static long prefix = System.currentTimeMillis();
	
	
	public String createGoodCatalog(){
		
		StringWriter result = new StringWriter();
		
		goodsCatalog = new GoodsCatalog();
		List<Good> gList = new ArrayList<Good>();
		gList.add(generateGood());
		goodsCatalog.setGoods(gList);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GoodsCatalog.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(goodsCatalog, result);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	public Good generateGood(){
		String goodprefix = String.valueOf(prefix++);
		
		Good good = new Good();
		good.setName("Товар_" + goodprefix);
		good.setMarkingOfTheGood(goodprefix);
		good.setCertificationType(4);
		good.setDeleteFromCash(false);
		good.setErpCode(goodprefix.substring(8, 13));
		good.setFullname("Товар_" + goodprefix + "_полное имя товара - (fullname)");
		good.setVat((float) 18.00);
		good.setProductType("ProductPieceEntity");
		good.setChild(true);
		good.setGroup(generateGroup());
		
		BarCode bc = new BarCode();
		bc.setCode(goodprefix.substring(8, 13));
		bc.setCount(new BigDecimal(1));
		bc.setDefaultCode(true);
		
		good.getBarCodes().add(bc);
		good.getPrices().add(generatePrice(1L));
		good.getPrices().add(generatePrice(2L));
		
		return good;
	}
	
	public GoodsGroup generateGroup(){
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
		
		dateSince = getDate(new Date().getTime());
		dateFrom = getDate(new Date().getTime() + 86400000 * 365);;
		
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
		//2015-04-26T23:59:59
		strDateRepresentation = DisinsectorTools.getDate("yyyy-MM-dd", date) + "T" + DisinsectorTools.getDate("hh:mm:ss", date);
		try {
			result =  DatatypeFactory.newInstance().newXMLGregorianCalendar(strDateRepresentation);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
