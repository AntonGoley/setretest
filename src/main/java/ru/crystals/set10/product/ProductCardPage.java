package ru.crystals.set10.product;

import static ru.crystals.set10.utils.FlexMediator.doFlexProperty;
import static ru.crystals.set10.utils.FlexMediator.getElementProperty;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;

public class ProductCardPage extends AbstractPage{

	static final String ID_PRODUCTSWF = "Products";
	static final String TAB_LOCATOR = "details";
	
	
	public ProductCardPage(WebDriver driver) {
		super(driver);
		//switchWindow(false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_PRODUCTSWF)));
	}
	
	public <T> T selectTab(ProductTabs tabIndex, Class<T> tab) {
		doFlexProperty(getDriver(), ID_PRODUCTSWF, TAB_LOCATOR, new String[] {"selectedIndex", String.valueOf(tabIndex.ordinal())});
		return PageFactory.initElements(getDriver(), tab);
	}
	
	public String getTextFieldValue(String field){
		return getElementProperty(getDriver(), ID_PRODUCTSWF, field, "text");
	}
	
	 public enum ProductTabs {
		 TAB_PRODUCT_POLICY,
		 TAB_RESTRICTION,
		 TAB_MAIN_INFO,
		 TAB_ADDITION_INFO,
		 TAB_BAR_CODES
		
	}
}
