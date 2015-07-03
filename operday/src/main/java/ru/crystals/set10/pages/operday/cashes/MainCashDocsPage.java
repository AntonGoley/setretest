package ru.crystals.set10.pages.operday.cashes;


import static ru.crystals.set10.utils.FlexMediator.clickElement;
import static ru.crystals.set10.utils.FlexMediator.getElementProperty;

import java.math.BigDecimal;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class  MainCashDocsPage extends CashDocsAbstractPage {
	
	private static final String BUTTON_SELECT_ALL = "label:Выбрать все"; 
	private static final String BUTTON_ADD_DOC = "label:Добавить";
	private static final String BUTTON_DELETE_DOC = "label:Удалить";
	private static final String BUTTON_EDIT_DOC = "label:Редактировать";
	private static final String ID = "id:mainCashDeskDocuments/";
	
	public static final String BALANCE_START = "id:startBalanceLabel";
	public static final String BALANCE_END = "id:balanceLabel";
	

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
	
	public BigDecimal getBalance(String balance){
		String balanceOnScreen = getElementProperty(getDriver(), ID_OPERDAYSWF, balance, "text");
		log.info("Баланс главной кассы = " + balanceOnScreen);
		return new BigDecimal(balanceOnScreen.replace(" ", "").replace(",", "."));
		
	}
	
	
}
