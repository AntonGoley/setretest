package ru.crystals.set10.pages.operday.cashes;


import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static ru.crystals.set10.utils.FlexMediator.*;


public class  MainCashManualDocPage extends CashesPage{
	
	private static final String BUTTON_SAVE_CHANGES = "id:saveChangesButton";
	private static final String BUTTON_BACK_TO_MAINCASH = "id:backButton";
	private static final String BUTTON_SELECT_DATE = "label:Выбрать дату";
	private static final String LOCATOR_EDIT_DOC_FORM = "id:documentForm/className:HBox";
	
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
	
	public static final String DOC_TYPE_PKO_CASH_EXCESS = "Излишек по кассе";
	public static final String DOC_TYPE_PKO_UNENCLOSURE_ENCASHMENT = "Недовложение инкассация";
	public static final String DOC_TYPE_PKO_UNENCLOSURE_FROM_COUNTERPARTS = "Недовложение от контрагентов";
	public static final String DOC_TYPE_PKO_INCOME_FROM_OTHER_COUNTERPARTS = "Поступление от прочих контрагентов";
	public static final String DOC_TYPE_PKO_INCOME_FROM_EMPLOYEES = "Поступление от сотрудников в магазин";
	public static final String DOC_TYPE_PKO_EXCHANGE_INCOME = "Размен денег приход";
	
	/* HBox - parent, в котором лежит поле для ввода значения (текстовое, дата)*/
	private static final String HBOX_REGEXP = "HBox(\\d+)"; 
	Pattern pattern = Pattern.compile(HBOX_REGEXP);
	
	public MainCashManualDocPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public MainCashManualDocPage saveChanges(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SAVE_CHANGES);
		return this;
	}
	
	private String getParentElement(String textField){
		Matcher matcher = pattern.matcher(getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("className:Label;text:%s", textField), "parent"));
		matcher.find();
		log.info(matcher.group());
		return matcher.group() + "/";
	}
	
	/** установить значение текстового поля*/
	public MainCashManualDocPage setTextField(String textField, String value){
		typeText(getDriver(), ID_OPERDAYSWF, "name:" + getParentElement(textField) + "className:FocusedTextInput", value);
		log.info("Установлено значение поля " + textField + ": " + value);
		return this;
	}
	
	/**
	 * формат даты: dd:mm:HH
	 **/
	public MainCashManualDocPage setOperDayDate(String textField, String date){
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, "name:" + getParentElement(textField) + "className:Button");
		log.info(date.split("\\.")[0]);
		typeText(getDriver(), ID_OPERDAYSWF, "className:ExDateInput/id:box1", date.split("\\.")[0]);
		typeText(getDriver(), ID_OPERDAYSWF, "className:ExDateInput/id:box2", date.split("\\.")[1]);
		typeText(getDriver(), ID_OPERDAYSWF, "className:ExDateInput/id:box3", date.split("\\.")[2]);
		
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, BUTTON_SELECT_DATE);
		doFlexMouseUp(getDriver(), ID_OPERDAYSWF, BUTTON_SELECT_DATE);
		return this;
	}
	
	public MainCashDocsPage backToMainCash(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_BACK_TO_MAINCASH);
		return new MainCashDocsPage(getDriver());
	}
	
	/** сколько полей редактирования докумена на форме*/
	public int getFieldsEditCount(){
		return Integer.valueOf(
				getElementsNum(getDriver(), ID_OPERDAYSWF, LOCATOR_EDIT_DOC_FORM));
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
