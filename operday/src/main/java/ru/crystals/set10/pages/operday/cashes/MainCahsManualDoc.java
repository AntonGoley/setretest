package ru.crystals.set10.pages.operday.cashes;


import java.util.HashMap;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static ru.crystals.set10.utils.FlexMediator.*;


public class  MainCahsManualDoc extends CashesPage{
	
	private static final String BUTTON_SAVE_CHANGES = "id:saveChangesButton";
	private static final String BUTTON_BACK_TO_MAINCASH = "id:backButton";
	
	private static final String LOCATOR_EDIT_DOC_FORM = "id:documentForm/className:HBox";
	
	private HashMap<String, Fields> docFields;
	
	public static final String FIELD_DOC_NUMBER = "Номер документа";
	public static final String FIELD_DATE_OPERDAY = "Операционный день";
	public static final String FIELD_DATE_CREATE = "Дата и время создания документа";
	public static final String FIELD_AUTHOR = "Автор";
	public static final String FIELD_HEAD_ACCOUNTANT = "Главный бухгалтер ФИО";
	public static final String FIELD_PERSON_RECEIVE = "Получил ФИО";
	public static final String FIELD_RECEIVED_FROM = "Принято от ФИО";
	public static final String FIELD_DOC_SUM = "Сумма документа";
	public static final String FIELD_CODE_DEBET = "Дебет - код операции";
	public static final String FIELD_CODE_CREDIT = "Кредит - код операции";
	public static final String FIELD_COMMENTS = "Комментарии";
	
	
	public MainCahsManualDoc(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public MainCahsManualDoc saveChanges(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SAVE_CHANGES);
		return this;
	}
	
	public MainCashPage backToMainCash(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_BACK_TO_MAINCASH);
		return new MainCashPage(getDriver());
	}
	
	/** сколько полей редактирования докумена на форме*/
	public int getFieldsEditCount(){
		return Integer.valueOf(
				getElementsNum(getDriver(), ID_OPERDAYSWF, LOCATOR_EDIT_DOC_FORM));
	}
	
	/** установить значение текстового поля*/
	public MainCahsManualDoc setTextFieldValue(String fiels, String value){
		
		return this;
	}
	
	public class Fields {
		private HashMap<String, Boolean> fieldsPKO;
		private HashMap<String, Boolean> fieldsRKO;
		String[] fieldsName = {
			"",	
		};
		
		public Fields(){
			
		}
		
	}
}
