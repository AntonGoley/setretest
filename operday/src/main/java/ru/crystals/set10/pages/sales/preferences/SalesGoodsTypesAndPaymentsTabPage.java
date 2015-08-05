package ru.crystals.set10.pages.sales.preferences;

import static ru.crystals.set10.utils.FlexMediator.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import ru.crystals.set10.pages.basic.SalesPage;


/*
 * Общие настройки: вкладка "Типы товаров и оплат"
 */
public class SalesGoodsTypesAndPaymentsTabPage extends SalesPage{
	
	static final String LOCATOR_PRODUCT_TYPE_LIST = "id:productTypeList";
	static final String LOCATOR_PAYMENT_TYPE_LIST = "id:paymentsTypeList";
	static final String LOCATOR_PRODUCT_PREFERENCES_BUTTON = "id:setSelectedProductTypeBtn";
	static final String LOCATOR_PAYMENT_PREFERENCES_BUTTON = "id:setSelectedPaymentTypeBtn";
	
	/*
	 * Типы товаров
	 */
	public static final String GOOD_TYPE_SINGLE_GOOD = "Штучный товар";
	public static final String GOOD_TYPE_ALCOHOL = "Крепкий алкоголь";
	public static final String GOOD_TYPE_WEIGHT_GOOD = "Весовой товар";
	public static final String GOOD_TYPE_SINGLE_WEIGHT_GOOD = "Штучно-весовой товар";
	public static final String GOOD_TYPE_GIFT_CARD = "Подарочная карта";
	
	/*
	 * Типы оплат
	 */
	public static final String PAYMENT_TYPE_CONSUMER_CREDIT = "Потребительский кредит";
	
	
	public SalesGoodsTypesAndPaymentsTabPage(WebDriver driver) {
		super(driver);
	}
	
	public <T> T selectProductTypeItem(String productTypeItem, Class<T> page) {
		return selectItem(productTypeItem, page, LOCATOR_PRODUCT_TYPE_LIST, LOCATOR_PRODUCT_PREFERENCES_BUTTON);
	}
	
	public <T> T selectPaymentTypeItem(String paymentTypeItem, Class<T> page) {
		return selectItem(paymentTypeItem, page, LOCATOR_PAYMENT_TYPE_LIST, LOCATOR_PAYMENT_PREFERENCES_BUTTON);
	}
	
	private <T> T selectItem(String item, Class<T> page, String tableLocator, String buttonLocator){
		doFlexMouseDown(getDriver(), ID_SALESSWF, tableLocator + "/label:" + item);
		/* кнопка активируется на событие click */
		clickElement(getDriver(), ID_SALESSWF, tableLocator + "/label:" + item);
		waitForProperty(getDriver(), ID_SALESSWF, buttonLocator, new String[] {"enabled", "true"});
		clickElement(getDriver(), ID_SALESSWF, buttonLocator);
		return PageFactory.initElements(getDriver(), page);
	}
	
}
