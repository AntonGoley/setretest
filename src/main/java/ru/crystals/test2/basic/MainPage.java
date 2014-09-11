package ru.crystals.test2.basic;


import org.openqa.selenium.*;
import static ru.crystals.test2.utils.FlexMediator.*;


public class MainPage extends AbstractPage{
	
	static final String ID_SALESBUTTON = "salesButton";
	static final String ID_OPERDAYBUTTON = "operDayButton";
	static final String ID_MAINPAGESWF = "RetailX";

	
	public MainPage(WebDriver driver) {
		super(driver);
		isSWFReady();
	}
	
	public OperDayPage openOperDay() {
		waitForElement(getDriver(), ID_MAINPAGESWF, ID_OPERDAYBUTTON);
		clickElement(getDriver(), ID_MAINPAGESWF, ID_OPERDAYBUTTON);
		return new OperDayPage(getDriver()); 
	}
	
	public SalesPage openSales() {
		waitForElement(getDriver(), ID_MAINPAGESWF, ID_SALESBUTTON);
		clickElement(getDriver(), ID_MAINPAGESWF, ID_SALESBUTTON);
		// переключаемся на окно продаж т.к родажи открываются в новом окне 
		switchWindow(true);
		return new SalesPage(getDriver()); 
	}
	
}
