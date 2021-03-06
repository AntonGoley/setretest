package ru.crystals.set10.pages.product;

import static ru.crystals.set10.utils.FlexMediator.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.AbstractPage;

public class ProductCardPage extends AbstractPage{

	static final ByXPath XPATH_PRODUCTSWF = new ByXPath("//embed[contains(@src, 'Products')]");
	static final String ID_PRODUCTSWF = "application";
	static final String TAB_LOCATOR = "id:details/className:Tab;label:";
	

	public static final String TAB_MAIN_INFO = "Общая информация";
	public static final String TAB_ADDITION_INFO = "Дополнительная информация";
	public static final String TAB_BAR_CODES = "Штриховые коды";
	
	public ProductCardPage(WebDriver driver) {
		super(driver);
		isSWFReady();
		getWait().until(ExpectedConditions.visibilityOfElementLocated(XPATH_PRODUCTSWF));
	}
	
	public <T> T selectTab(String tabName, Class<T> tab) {
		clickElement(getDriver(), ID_PRODUCTSWF, TAB_LOCATOR + tabName);
		return PageFactory.initElements(getDriver(), tab);
	}
	
	/*
	 * Значение нередактируемого текствого поля
	 */
	public String getTextFieldValue(String field){
		return getElementProperty(getDriver(), ID_PRODUCTSWF, field, "text");
	}
	
	/*
	 * Значение свойсвт (сейчас пока radiobutton)
	 */
	public String getPropertyFieldValue(String field){
		return getElementProperty(getDriver(), ID_PRODUCTSWF, field, "label");
	}
	
	/*
	 * выбрать свойство: чек бокс, radiobutton
	 */
	public void setProperty(String property){
		boolean ifSelected = Boolean.valueOf(getElementProperty(getDriver(), ID_PRODUCTSWF, property, "selected") );
		if (!ifSelected) {
			clickElement(getDriver(), ID_PRODUCTSWF, property);
		}
	}
	
}
