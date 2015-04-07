package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.operday.OperDayPage;
import static ru.crystals.set10.utils.FlexMediator.*;
import ru.crystals.set10.pages.basic.*;

public class  PaymentTransactionsPage extends OperDayPage implements SaveFile{
	
	public static final String LABEL_CLASS_LOCATOR = "className:Label";
	public static final String LINK_SAVE_EXCEL = "label=в excel";
	public static final String ID_OPERDAYSWF = "OperDay";
	
	
	public static final String LOCATOR_DATE = "id:dateRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_TIME = "id:timeRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_CARD_NUMBER = "id:cardRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_CARD_TYPE = "id:cardTypeRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_AMOUNT_REQUESTED = "id:amountRequestedRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_BANK_ID = "id:bankRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_TERMINAL = "id:terminalRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_SLIP = "id:slipRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_AUTHORIZATION_CODE = "id:authorizationCodeRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_BANK_RESPONSE_CODE = "id:bankResponseCodeRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_SERVER_RESPONSE_CODE = "id:serverResponseCodeRow/id:_KeyValueRow_Label%s|%s";
	public static final String LOCATOR_MESSAGE = "id:messageRow/id:_KeyValueRow_Label%s|%s";
	
	
	public PaymentTransactionsPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	@Override
	public void saveFile(String fileType){
		clickElement(getDriver(), ID_OPERDAYSWF, fileType);
	}
	
	/* Возвращает  "Название поля:значение"	
	 * rowIndex = 1 - берет данные из 1ой строки таблицы,
	 * rowIndex = 2 - из второй строки таблицы и т.д 
	 */
	public String getTransactionElementValue(String elementLocator, int rowIndex) {
		String tableRowIndex = String.valueOf(rowIndex);
		String fieldName = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format(elementLocator, "1", tableRowIndex), "text");
		String fieldValue = getElementProperty(getDriver(), ID_OPERDAYSWF, String.format(elementLocator, "2", tableRowIndex), "text");
		return fieldName + ":" + fieldValue;
	}
}
