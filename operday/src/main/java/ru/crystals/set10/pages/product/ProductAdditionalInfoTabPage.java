package ru.crystals.set10.pages.product;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import static ru.crystals.set10.utils.FlexMediator.*;

public class ProductAdditionalInfoTabPage extends ProductCardPage{
	
	public static String FIELD_BUTTON_NUMBER_ON_SCALES = "button-on-scaleText";
	public static String FIELD_PRODUCER = "producerText";
	public static String FIELD_VALID_FOR_HOURS = "good-for-hoursText";
	public static String FIELD_VALID_FOR_DAYS = "good-for-daysText";
	
	public static String RADIO_DATE_NOT_SPECIFIED = "notSpecifiedButton";
	public static String RADIO_SELECT_DATE = "selectedDateButton";
	public static String RADIO_CURRENT_DATE_AND_TIME = "currentDateButton";
	public static String RADIO_LABEL_PRINT_DATE_AND_TIME  = "labelPrintingDateButton";
	
	public ProductAdditionalInfoTabPage(WebDriver driver) {
		super(driver);
	}
	
	public String getManufactureDateCurrentTime(){
		String result = 
				getElementProperty(getDriver(), ID_PRODUCTSWF, RADIO_CURRENT_DATE_AND_TIME, "label");
		
		Pattern pattern = Pattern.compile("(\\d+:\\d+)");
		Matcher matcher = pattern.matcher(result);
		matcher.find();
		log.info(matcher.group());
		return matcher.group();
	}
	
}
