package ru.crystals.set10.product;


import org.openqa.selenium.WebDriver;


public class ProductAdditionalInfoTabPage extends ProductCardPage{
	
	public static String FIELD_BUTTON_NUMBER_ON_SCALES = "button-on-scaleText";
	public static String FIELD_PRODUCER = "producerText";
	public static String FIELD_VALID_FOR_HOURS = "good-for-hoursText";
	public static String FIELD_VALID_FOR_DAYS = "good-for-daysText";
	
	public ProductAdditionalInfoTabPage(WebDriver driver) {
		super(driver);
	}
	
}
