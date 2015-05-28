package ru.crystals.set10.pages.sales.preferences;

import static ru.crystals.set10.utils.FlexMediator.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;


/*
 * Типы товаров и оплат: абстрактная страница настроек
 */
public class AbstractGoodAndPaymentPreferencesPage extends SalesPage{
	
	private static final String BUTTON_BACK = "id:backButton";
	static final String CHECKBOX_LOCATOR = "id:checkBox;label:%s";
	
	public AbstractGoodAndPaymentPreferencesPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	/*
	 * Назад к Вкладке "Типы товаров и оплат"
	 */
	public SalesGoodsTypesAndPaymentsTabPage goBack(){
		clickElement(getDriver(), ID_SALESSWF, BUTTON_BACK);
		DisinsectorTools.delay(1000);
		return new SalesGoodsTypesAndPaymentsTabPage(getDriver());
	}
	
	/*
	 * Установить\снять чек бокс
	 */
	public <T> T setCheckBox(Class<T> page, String checkBox, boolean status){
		doFlexProperty(getDriver(), ID_SALESSWF, String.format(CHECKBOX_LOCATOR, checkBox), new String[]{"selected", String.valueOf(status)});
		return PageFactory.initElements(getDriver(), page);
	}
	
	/*
	 * Выделен или нет чек бокс
	 */
	public boolean getCheckBoxValue(String checkBox){
		return 
				Boolean.valueOf(getElementProperty(getDriver(), ID_SALESSWF, String.format(CHECKBOX_LOCATOR, checkBox), "selected"));
	}
	

}
