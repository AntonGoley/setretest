package ru.crystals.set10.pages.operday.cashes;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
	private static final String DOCTYPE_COMBOBOX = "id:docSubTypeComboBox";
	
	public static final String FIELD_DOC_NUMBER = "Номер документа";
	public static final String FIELD_DATE_OPERDAY = "Операционный день";
	public static final String FIELD_DATE_CREATE = "Дата и время создания документа";
	public static final String FIELD_AUTHOR = "Автор";
	public static final String FIELD_HEAD_ACCOUNTANT = "Главный бухгалтер ФИО";
	public static final String FIELD_PERSON_RECEIVED = "Получил ФИО";
	public static final String FIELD_RECEIVED_FROM = "Принято от ФИО";
	public static final String FIELD_PERSON_GIVE_TO= "Выдать ФИО";
	public static final String FIELD_DOC_SUM = "Сумма документа";
	public static final String FIELD_CODE_DEBET = "Дебет - код операции";
	public static final String FIELD_CODE_CREDIT = "Кредит - код операции";
	public static final String FIELD_COMMENTS = "Комментарии";
	
	private static final String LOCATOR_DOCUMENT_SAVED = "id:savedMsgLabel";
	
	/*
	 * Инкассация торговой выручки
	 */
	public static final String FIELD_ENCASHMENT_BANKNOTE_5000 = "Купюр 5000 р.";
	public static final String FIELD_ENCASHMENT_BANKNOTE_1000 = "Купюр 1000 р.";
	public static final String FIELD_ENCASHMENT_BANKNOTE_500 = "Купюр 500 р.";
	public static final String FIELD_ENCASHMENT_BANKNOTE_100 = "Купюр 100 р.";
	public static final String FIELD_ENCASHMENT_BAG_NUMBER= "Номер инкассаторской сумки";
	
	
	
	/*
	 * Документы ПКО
	 */
	public static final String DOC_TYPE_PKO_CASH_EXCESS = "Излишек по кассе";
	public static final String DOC_TYPE_PKO_UNENCLOSURE_ENCASHMENT = "Недовложение инкассация";
	public static final String DOC_TYPE_PKO_UNENCLOSURE_FROM_COUNTERPARTS = "Недовложение от контрагентов";
	public static final String DOC_TYPE_PKO_INCOME_FROM_OTHER_COUNTERPARTS = "Поступление от прочих контрагентов";
	public static final String DOC_TYPE_PKO_INCOME_FROM_EMPLOYEES = "Поступление от сотрудников магазина";
	public static final String DOC_TYPE_PKO_EXCHANGE_INCOME = "Размен денег приход";
	
	/*
	 * Документы РКО
	 */
	public static final String DOC_TYPE_RKO_ENCASHMENT= "Инкассация торговой выручки";
	public static final String DOC_TYPE_RKO_PAYMENT_FROM_DEPOSITOR = "Выдача с депонента";
	public static final String DOC_TYPE_RKO_SALARY_PAYMENT = "Выдача зарплаты";
	public static final String DOC_TYPE_RKO_CASH_LACK = "Недостача по кассе";
	public static final String DOC_TYPE_RKO_EXCESS_ENCASHMENT = "Перевложение Инкассация";
	public static final String DOC_TYPE_RKO_EXCHANGE_WITHDRAWAL = "Размен денег расход";
	
	
	
	/* HBox - parent, в котором лежит поле для ввода значения (текстовое, дата) */
	private static final String HBOX_REGEXP = "HBox(\\d+)"; 
	Pattern pattern = Pattern.compile(HBOX_REGEXP);
	
	public MainCashManualDocPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public MainCashManualDocPage saveChanges(){
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, BUTTON_SAVE_CHANGES);
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SAVE_CHANGES);
		waitForElementVisible(getDriver(), ID_OPERDAYSWF, LOCATOR_DOCUMENT_SAVED);
		waitSpinner(ID_OPERDAYSWF);
		return this;
	}
	
	private String getParentElement(String textField){
		Matcher matcher = pattern.matcher(getElementProperty(getDriver(), ID_OPERDAYSWF, String.format("className:Label;text:%s", textField), "parent"));
		matcher.find();
		//log.info(matcher.group());
		return matcher.group() + "/";
	}
	
	/* 
	 * установить значение текстового поля
	 */
	public MainCashManualDocPage setTextField(String textField, String value){
		/*
		 * Локатор отличается для поля Комментарий
		 */
		String textInputLocator = "FocusedTextInput";
		if (textField.equals(FIELD_COMMENTS)) {
			textInputLocator = "FocusedTextArea";
		}
		
		/*
		 * Локатор для купюр
		 */
		if (textField.contains("Купюр")) {
			textInputLocator = "ExNumberInput";
		}
		
		
		
		typeText(getDriver(), ID_OPERDAYSWF, "name:" + getParentElement(textField) + String.format("className:%s", textInputLocator), value);
		doFlexProperty(getDriver(), 
				ID_OPERDAYSWF, 
				"name:" + getParentElement(textField) + String.format("className:%s", textInputLocator), 
				new String[]{"text", value});
		log.info("Установлено значение поля " + textField + ": " + value);
		return this;
	}
	
	public String getTextField(String textField){
		/*
		 * Локатор отличается для поля Комментарий
		 */
		String textInputLocator = "FocusedTextInput";
		if (textField.equals(FIELD_COMMENTS)) {
			textInputLocator = "FocusedTextArea";
		}
		
		return getElementProperty(getDriver(), ID_OPERDAYSWF, "name:" + getParentElement(textField) + String.format("className:%s", textInputLocator), "text");
	}
	
	
	
	public String getAutogeneratedFieldValue(String textField){
		/* поле Дата и время создание не такое, как остальные*/
		if (textField.equals(FIELD_DATE_CREATE)) {
			return getElementProperty(getDriver(), ID_OPERDAYSWF, "name:" + getParentElement(textField) + "className:DateViewer", "text");	
		}
		
		return getElementProperty(getDriver(), ID_OPERDAYSWF, "name:" + getParentElement(textField) + "className:Label|1", "text");		
	}
	
	
	/*
	 * формат даты: dd:mm:HH
	 */
	public MainCashManualDocPage setOperDayDate(String textField, String date){
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, "name:" + getParentElement(textField) + "className:Button");
		log.info("Создание документа на дату: " + date);
		typeText(getDriver(), ID_OPERDAYSWF, "className:ExDateInput/id:box1", date.split("\\.")[0]);
		typeText(getDriver(), ID_OPERDAYSWF, "className:ExDateInput/id:box2", date.split("\\.")[1]);
		typeText(getDriver(), ID_OPERDAYSWF, "className:ExDateInput/id:box3", date.split("\\.")[2]);
		
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, BUTTON_SELECT_DATE);
		doFlexMouseUp(getDriver(), ID_OPERDAYSWF, BUTTON_SELECT_DATE, true);
		return this;
	}
	
	public MainCashDocsPage backToMainCash(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_BACK_TO_MAINCASH);
		waitSpinner(ID_OPERDAYSWF);
		return new MainCashDocsPage(getDriver());
	}
	
	/* 
	 * сколько полей редактирования докумена на форме 
	 */
	public int getFieldsEditCount(){
		return Integer.valueOf(
				getElementsNum(getDriver(), ID_OPERDAYSWF, LOCATOR_EDIT_DOC_FORM));
	}
	
	public MainCashManualDocPage selectDocType(String docType){
		log.info("Выбрать тип документа: " + docType);
		selectElement(getDriver(), ID_OPERDAYSWF, DOCTYPE_COMBOBOX, docType);
		return this;
	}
	
	
	
	
	
//	/*
//	 * Документы ПКО, добавляемые вручную
//	 */
//	public enum PKOtypes {
//	
////		PKO_REVENUE_STORE,						//Выручка магазина
//		PKO_SURPLUS_BY_CASH,					//Излишек по кассе
//		PKO_FAILURE_TO_INVEST_ENCASHMENT,		//Недовложение инкассация
//		PKO_DEBIT_FROM_COUNTERPARTIES,			//Недовложение от контрагентов
//		PKO_DEBIT_FROM_OTHER_COUNTERPARTIES,	//Поступление от прочих контрагентов
//		PKO_DEBIT_FROM_STORE_EMPLOYEES,			//Поступление от сотрудников магазина
//		PKO_MONEY_CHANGE;						//Размен денег приход
//	}
//	
//	
//	/*
//	 * Документы РКО, добавляемые вручную
//	 */
//	public enum RKOtypes {
//		
//		RKO_ENCASHMENT,							//Инкассация торговой выручки
//		RKO_ISSUANCE_OF_DEPOSITOR,				//Выдача с депонента
//		RKO_PAYMENT_OF_SALARY,					//Выдача зарплаты
//		RKO_DEFICIT,							//Недостача по кассе
//		RKO_REINVESTMENT,						//Перевложение Инкассация
//		RKO_MONEY_CHANGE;						//Размен денег расход
//	}
	
}
