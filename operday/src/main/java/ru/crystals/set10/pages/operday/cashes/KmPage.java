package ru.crystals.set10.pages.operday.cashes;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  KmPage extends CashesPage{
	
	public static final String LOCATOR_KM3 = "КМ-3";
	public static final String LOCATOR_KM6 = "КМ-6";
	private static final String NAVIGATOR_KM_TYTE = "id:tabNav/className:Tab;label:";
	private static final String BUTTON_SELECT_ALL_UNPRINTED = "label:Выбрать все ненапечатанные";
	private static final String LOCATOR_PRINTALL_BUTTON  = "label=Распечатать выбранные";
	
	public static final String LOCATOR_KM3_TABLE = "km3Table";
	public static final String LOCATOR_KM6_TABLE = "km6Table";
	
	public static final String KM3_PDF = "KM3.pdf";
	public static final String KM6_PDF = "KM6.pdf";
	
	
	public KmPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public KmPage switchToKm(String kmType){
		clickElement(getDriver(), ID_OPERDAYSWF, NAVIGATOR_KM_TYTE + kmType);
		return new KmPage(getDriver());
	}
	
	public int getKmCountOnPage(String formType){
		return Integer.valueOf(
				getElementProperty(getDriver(), ID_OPERDAYSWF, formType, "length"));
	}
	
	
	public KmPage printAllKmForms(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SELECT_ALL_UNPRINTED);
		waitForProperty(getDriver(), ID_OPERDAYSWF, LOCATOR_PRINTALL_BUTTON, new String[]{"enabled", "true"});
		clickElement(getDriver(), ID_OPERDAYSWF, LOCATOR_PRINTALL_BUTTON);
		return this;
	}
	
	/*
	 * Метод не используется, пока  в хроме не пофиксят открытие 
	 * окна для просмотра печати
	 */
	@Deprecated
	public String printAllKmFormsWhenPrinPreviewEnable(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SELECT_ALL_UNPRINTED);
		waitForProperty(getDriver(), ID_OPERDAYSWF, LOCATOR_PRINTALL_BUTTON, new String[]{"enabled", "true"});
		clickElement(getDriver(), ID_OPERDAYSWF, LOCATOR_PRINTALL_BUTTON);
		return getReportText();
	}
	
}
