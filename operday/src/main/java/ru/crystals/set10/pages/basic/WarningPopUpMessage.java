package ru.crystals.set10.pages.basic;

import org.openqa.selenium.WebDriver;

import static ru.crystals.set10.utils.FlexMediator.getElementProperty;
import static ru.crystals.set10.utils.FlexMediator.clickElement;

public class WarningPopUpMessage extends AbstractPage {
	
	
	public static String BUTTON_YES = "id:yesButton";
	public static String BUTTON_NO = "id:noButton";
	public static String LOCATOR_MESSAGE_BOX = "className:WarningPopup";
	
	public WarningPopUpMessage(WebDriver driver) {
		super(driver);
	}
	
	//TODO: убрать хардкод названия swf
	public String getMessage(){
		return getElementProperty(getDriver(), "application", LOCATOR_MESSAGE_BOX, "message");
	}
	
	public String getTitle(){
		return getElementProperty(getDriver(), "application", LOCATOR_MESSAGE_BOX, "title");
	}
	
	public void makeDecision(String button){
		clickElement(getDriver(), "application", button);
		waitSpinner("application");
	}
}
