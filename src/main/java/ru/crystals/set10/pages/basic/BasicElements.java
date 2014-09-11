package ru.crystals.set10.pages.basic;


import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;


public class BasicElements {
	
	public static final String ID_SALESSWF = "Sales";
	
	public static <T> T goBack(WebDriver driver, Class<T> page, String ID_SWF, String LOCATOR_BACK_BUTTON){
		waitForElement(driver, ID_SWF, LOCATOR_BACK_BUTTON);
		clickElement(driver, ID_SWF, LOCATOR_BACK_BUTTON);
		return PageFactory.initElements(driver, page);
	}
	
}
