package ru.crystals.set10.pages.operday.cashes;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.operday.OperDayPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  Km3Page extends OperDayPage{
	
	public static final String LOCATOR_KM3 = "КМ-3";
	public static final String LOCATOR_KM6 = "КМ-6";
	private static final String NAVIGATOR_KM_TYTE = "tabNav";
	private static final String LOCATOR_PRINTALL_CHECKBOX = "selectedAllCheckBox";
	private static final String LOCATOR_PRINTALL_BUTTON  = "label=Распечатать выбранные";
	
	public static final String LOCATOR_KM3_TABLE = "km3Table";
	public static final String LOCATOR_KM6_TABLE = "km6Table";
	
	
	
	public Km3Page(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public Km3Page switchToKm(String kmType){
		clickElement(getDriver(), ID_OPERDAYSWF, NAVIGATOR_KM_TYTE, kmType);
		return new Km3Page(getDriver());
	}
	
	public int getKmCountOnPage(String formType){
		return Integer.valueOf(
				getElementProperty(getDriver(), ID_OPERDAYSWF, formType, "length"));
	}
	
	public String printAllKmForms(){
		checkBoxValue(getDriver(), ID_OPERDAYSWF, LOCATOR_PRINTALL_CHECKBOX, true);
		waitForProperty(getDriver(), ID_OPERDAYSWF, LOCATOR_PRINTALL_BUTTON, new String[]{"enabled", "true"});
		clickElement(getDriver(), ID_OPERDAYSWF, LOCATOR_PRINTALL_BUTTON);
		return getReportText();
	}
	
}
