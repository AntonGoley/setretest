package ru.crystals.set10.utils;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ru.crystals.ERPIntegration.discounts.model.xml.imp.ActionModeType;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.ActionResultsType;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.AdvertisingActionType;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.MarkingOfTheGoodType;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.RowType;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.SetType;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.XmlDiscountActionResult;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.XmlDiscountActionResultType;


public class AdverstingActionsGenerator {
	
	/*
	 * TODO: добавить возможность гибко задавать акции
	 */
	public AdvertisingActionType generateAdversting(String markingOfTheGood){
		
		AdvertisingActionType action = new AdvertisingActionType();
		long dateBegin = new Date().getTime() - 86400000L;
		long dateEnd = new Date().getTime() +  86400000L;
				
		action.setBeginDate(getDate(dateBegin));
		action.setEndDate(getDate(dateEnd));
		
		action.setName("Action_" + dateBegin);
		action.setActive(true);
		action.setExternalCode("ExCode_" + dateBegin);
		action.setWorksAnyTime(true);
		action.setMode(ActionModeType.AUTOMATIC);
		action.setPriority(new Double(1));
		
		/*
		 * Заполнить результат акции
		 */
		ActionResultsType results = new ActionResultsType();
		
		// тип акции
		SetType actionType = new SetType();
		// Название товарного набора
		actionType.setName("Набор_" + dateBegin);

		
		// множество товаров, на которое может действовать акция
		RowType row = new RowType();
		row.setRequiedQuantity(new Double(1));
		row.setFixedPrice(new Double(100));
		
		MarkingOfTheGoodType good = new MarkingOfTheGoodType();
		good.setId(markingOfTheGood);
		
		row.getMarkingOfTheGood().add(good);
		actionType.getRow().add(row);
		results.getSet().add(actionType);
		
//		results.setCalculation(discount);
		
		action.setActionResults(results);
		
		return action;
	}
	
	
	/*
	 * Добавить товарный набор
	 */
	public AdvertisingActionType addDiscountActionResult(AdvertisingActionType action){
//		XmlDiscountActionResult discount = new XmlDiscountActionResult();
//		discount.setType(XmlDiscountActionResultType.PERCENT);
//		discount.setValue(new BigDecimal("50.00"));
//		
//		results.setCalculation(discount);
		return action;
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
