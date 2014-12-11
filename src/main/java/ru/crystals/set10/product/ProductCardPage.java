package ru.crystals.set10.product;

import static ru.crystals.set10.utils.FlexMediator.*;
import static ru.crystals.set10.utils.FlexMediator.getElementProperty;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;

public class ProductCardPage extends AbstractPage{

	static final String ID_PRODUCTSWF = "Products";
	static final String TAB_LOCATOR = "details";
	

	public static final String TAB_MAIN_INFO = "Общая информация";
	public static final String TAB_ADDITION_INFO = "Дополнительная информация";
	public static final String TAB_BAR_CODES = "Штриховые коды";
	
	public ProductCardPage(WebDriver driver) {
		super(driver);
		//switchWindow(false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_PRODUCTSWF)));
	}
	
	public <T> T selectTab(String tabName, Class<T> tab) {
		clickElement(getDriver(), ID_PRODUCTSWF, TAB_LOCATOR, tabName);
		//doFlexProperty(getDriver(), ID_PRODUCTSWF, TAB_LOCATOR, new String[] {"selectedIndex", String.valueOf(tabIndex.ordinal())});
		return PageFactory.initElements(getDriver(), tab);
	}
	
	public String getTextFieldValue(String field){
		return getElementProperty(getDriver(), ID_PRODUCTSWF, field, "text");
	}

}
