package ru.crystals.set10.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Measure;


public class GoodGenerator{
	
	
	private Good good; 
	private GoodsCatalog goodsCatalog;
	private static long prefix = System.currentTimeMillis();
	
	public void createGoodCatalog(){
		goodsCatalog = new GoodsCatalog();
		List<Good> gList = new ArrayList<Good>();
		gList.add(generateGood());
		goodsCatalog.setGoods(gList);
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GoodsCatalog.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(goodsCatalog, System.out);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public Good generateGood(){
		String goodprefix = String.valueOf(prefix++);
		
		
		good = new Good();
		good.setName("Товар_" + goodprefix);
		good.setMarkingOfTheGood(goodprefix);
		good.setCertificationType(4);
		good.setDeleteFromCash(false);
		good.setErpCode(goodprefix.substring(8, 13));
		good.setFullname("Товар_" + goodprefix + "_полное имя товара - (fullname)");
		//good.setMeasure(Measure ); 
		good.setVat((float) 18.00);
		good.setProductType("ProductPieceEntity");
		
		
		return good;
	}
	
	

}
