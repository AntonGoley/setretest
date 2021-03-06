package ru.crystals.set10.pages.sales.preferences;

import static ru.crystals.set10.utils.FlexMediator.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;


/*
 * Типы товаров и оплат: абстрактная страница настроек
 */
public class AbstractGoodAndPaymentPreferencesPage extends SalesPage{
	
	protected static final String BUTTON_BACK = "id:backButton;label:К типам товаров и оплат";
	static final String CHECKBOX_LOCATOR = "id:checkBox;label:%s";
	
	public AbstractGoodAndPaymentPreferencesPage(WebDriver driver) {
		super(driver);
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
		boolean currentStatus = false;
		currentStatus = Boolean.valueOf(getElementProperty(getDriver(), ID_SALESSWF, String.format(CHECKBOX_LOCATOR, checkBox), "selected"));
		if (currentStatus != status){
			clickElement(getDriver(), ID_SALESSWF, String.format(CHECKBOX_LOCATOR, checkBox));
			//doFlexProperty(getDriver(), ID_SALESSWF, String.format(CHECKBOX_LOCATOR, checkBox), new String[]{"selected", String.valueOf(status)});
		};	
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
