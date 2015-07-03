package ru.crystals.set10.pages.operday.cashes;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CashDocsAbstractPage extends CashesPage{
	
	public static final String LOCATOR_KM3 = "КМ-3";
	public static final String LOCATOR_KM6 = "КМ-6";
	public static final String LOCATOR_DOCS = "Документы";
	
	private static final String BUTTON_SELECT_ALL_UNPRINTED = "label:Выбрать все ненапечатанные";
	private static final String BUTTON__PRINTALL  = "label=Распечатать выбранные";

	private static String ID = "";
	private static final String KM6_ID = "id:km6Acts/";
	private static final String KM3_ID = "id:km3Acts/";
	private static final String DOCS_ID = "id:mainCashDeskDocuments/";
	private static final String NAVIGATOR = "className:Tab;label:";
	
	/* локатор разный для таблицы Документы и КМ-3, КМ-6*/
	private static String DOC_TABLE_LOCATOR = "";
	
	/* запрос определяет, включена главная касса или нет,
	 * проверка необходима только для проверки актов КМ-3, КМ-6*/
	public static final String SQL_MAIN_CASH = "select property_value from sales_management_properties where property_key = 'main.cash.enabled'";
	
	public CashDocsAbstractPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	/* переключение между таблицами КМ-3, КМ-6, Документы,
	 * и генерация соотв. страницы*/
	public CashDocsAbstractPage switchToTable(String type){
		CashDocsAbstractPage result = null;
		switch (type){
			case LOCATOR_KM6 : ID = KM6_ID;
				result =  new KmPage(getDriver());
				DOC_TABLE_LOCATOR = "id:actsTable";
				break;
			case LOCATOR_KM3 : ID = KM3_ID;
				result = new KmPage(getDriver());
				DOC_TABLE_LOCATOR = "id:actsTable";
				break;
			case LOCATOR_DOCS : ID = DOCS_ID;
				DOC_TABLE_LOCATOR = "id:documentsTable";
				result = new MainCashDocsPage(getDriver());
				break;
		}
		clickElement(getDriver(), ID_OPERDAYSWF, NAVIGATOR + type);
		waitSpinner(ID_OPERDAYSWF);
		return result;
	}
	
	/* возвращает количество документов в таблице КМ-3, КМ-6, Документы */
	public int getDocCountOnPage(){
		DisinsectorTools.delay(1000);
		int rowsInTable = getElementsNum(getDriver(), ID_OPERDAYSWF, ID + DOC_TABLE_LOCATOR + "/id:groupRenderer;visible:true");
		int hiddenrows = getElementsNum(getDriver(), ID_OPERDAYSWF, ID + DOC_TABLE_LOCATOR + "/name:hiddenItem/id:groupRenderer;visible:true");
		return Integer.valueOf(
				getElementProperty(getDriver(), ID_OPERDAYSWF, ID + DOC_TABLE_LOCATOR, "length")) - (rowsInTable - hiddenrows);
	}
	
	/* возвращает ожидаемое количество документов в таблице,
	 * по истечении таймаута возвратит реальное количество*/
	public int getExpectedDocsCountOnPage(int expectedCount){
		int result = 0;
		int timeout = 10;
		
		while (timeout > 0) {
			timeout--;
			result = getDocCountOnPage();
			if (result == expectedCount) {
				return result;
			}
			DisinsectorTools.delay(1000);
		}	
		return result;
	}
	
	/* выбор и печать всех ненапечатанных документов*/
	public CashDocsAbstractPage printAllDocs(){
		clickElement(getDriver(), ID_OPERDAYSWF, ID  + BUTTON_SELECT_ALL_UNPRINTED);
		waitForProperty(getDriver(), ID_OPERDAYSWF, ID + BUTTON__PRINTALL, new String[]{"enabled", "true"});
		clickElement(getDriver(), ID_OPERDAYSWF, ID + BUTTON__PRINTALL);
		return this;
	}
	
	/*
	 * Метод не используется, пока  в хроме не пофиксят открытие 
	 * окна для просмотра печати
	 */
	@Deprecated
	public String printAllKmFormsWhenPrinPreviewEnable(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SELECT_ALL_UNPRINTED);
		waitForProperty(getDriver(), ID_OPERDAYSWF, BUTTON__PRINTALL, new String[]{"enabled", "true"});
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON__PRINTALL);
		return getReportText();
	}
	
}
