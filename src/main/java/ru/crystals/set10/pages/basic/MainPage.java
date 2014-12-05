package ru.crystals.set10.pages.basic;


import org.openqa.selenium.*;

import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.product.ProductCardPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class MainPage extends AbstractPage{
	
	static final String ID_SALESBUTTON = "salesButton";
	static final String ID_OPERDAYBUTTON = "operDayButton";
	static final String ID_MAINPAGESWF = "RetailX";
	
	static final String ID_SEARCH_GOOD = "searchParam";
	public static final String LOCATOR_SUGGEST_CODE = "codeLabel";
	public static final String LOCATOR_SELECT_RESULT= "className=ListBaseContentHolder";
	
	public MainPage(WebDriver driver) {
		super(driver);
		//isSWFReady();
	}
	
	public OperDayPage openOperDay() {
		DisinsectorTools.delay(5000);
		clickElement(getDriver(), ID_MAINPAGESWF, ID_OPERDAYBUTTON);
		return new OperDayPage(getDriver(), true); 
	}
	
	public SalesPage openSales() {
		clickElement(getDriver(), ID_MAINPAGESWF, ID_SALESBUTTON);
		// переключаемся на окно продаж т.к родажи открываются в новом окне 
		switchWindow(true);
		return new SalesPage(getDriver()); 
	}
	
	public ProductCardPage findGood(String good){
				//getDriver().navigate().refresh();
				typeText(getDriver(), ID_MAINPAGESWF, ID_SEARCH_GOOD, good);
				waitForElement(getDriver(), ID_MAINPAGESWF, LOCATOR_SUGGEST_CODE);
				//выбрать элемент
				doFlexProperty(getDriver(), ID_MAINPAGESWF, ID_SEARCH_GOOD, new String[] {"selectedIndex", "0"});
				clickElement(getDriver(), ID_MAINPAGESWF, LOCATOR_SELECT_RESULT);
				switchWindow(false);
				return new ProductCardPage(getDriver());
			}
	
}
