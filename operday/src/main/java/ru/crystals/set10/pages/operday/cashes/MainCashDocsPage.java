package ru.crystals.set10.pages.operday.cashes;


import static ru.crystals.set10.utils.FlexMediator.clickElement;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class  MainCashDocsPage extends CashDocsAbstractPage {
	
	private static final String BUTTON_SELECT_ALL = "label:Выбрать все"; 
	private static final String BUTTON_ADD_DOC = "label:Добавить";
	private static final String BUTTON_DELETE_DOC = "label:Удалить";
	private static final String BUTTON_EDIT_DOC = "label:Редактировать";
	
	private static final String BALANCE_DEGIN = "";
	private static final String BALANCE_END = "";
	
	private static final String ID = "id:mainCashDeskDocuments/";

	public MainCashDocsPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public MainCashManualDocPage editDoc(){
		//TODO: добавить выбор документа
		clickElement(getDriver(), ID_OPERDAYSWF, ID + BUTTON_EDIT_DOC);
		return new MainCashManualDocPage(getDriver());
	}
	
	public MainCashDocsPage deleteDoc(){
		//TODO: добавить выбор документа
		clickElement(getDriver(), ID_OPERDAYSWF, ID + BUTTON_DELETE_DOC);
		return this;
	}
	
	public MainCashManualDocPage addDoc(){
		clickElement(getDriver(), ID_OPERDAYSWF, ID + BUTTON_ADD_DOC);
		return new MainCashManualDocPage(getDriver());
	}
	
}
