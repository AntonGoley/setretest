package ru.crystals.set10.pages.operday.cashes;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.*;

import static ru.crystals.set10.utils.FlexMediator.*;
import ru.crystals.set10.pages.basic.WarningPopUpMessage;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.utils.DisinsectorTools;


public class  CashOperDayTabPage extends OperDayPage{
	
	private  String LOCATOR_TABLE_ROW_CLASS_NAME = ""; 
	private static final String LOCATOR_CASH_NUMBER_VALUE = "id:_cashDeskNumber;text:%s|0";
	private static final String LOCATOR_CASHIER_NAME = "className:SoftCuttingLabel";
	private static final String LOCATOR_NOSHIFT_CASHIER_NAME = "id:noCashierLabel";
	private static final String LOCATOR_CASHLIST_TABLE = "id:cashmachinesList2";
	private static final String BUTTON_CLOSE_OPERDAY = "label:Закрыть операционный день";
	private static final String BUTTON_OPEN_OPERDAY = "label:Открыть операционный день";
	
	public CashOperDayTabPage(WebDriver driver) {
		super(driver);
	}
	
	/* 
	 * определяем элемент родителя по номеру кассы
	 */
	private void setLocatorTableRowClassName(long cashNumber){
		Pattern pattern = Pattern.compile("FlexTable2RowRenderer(\\d+)");
		Matcher matcher = pattern.matcher(getElementProperty(getDriver(), ID_OPERDAYSWF, String.format(LOCATOR_CASH_NUMBER_VALUE, cashNumber), "parent"));
		matcher.find();
		LOCATOR_TABLE_ROW_CLASS_NAME = matcher.group();
	}
	
	public String getCashierNameForLastShift(long cashNumber){
		int shiftsForCah = 0; 
		String result = "";
		setLocatorTableRowClassName(cashNumber);
		
		/*
		 * сколько смен в кассе
		 */
		shiftsForCah = getElementsNum(getDriver(), ID_OPERDAYSWF, "name:" + LOCATOR_TABLE_ROW_CLASS_NAME + "/" + LOCATOR_CASHIER_NAME);
		
		/*
		 * возвращает фамилию кассира для последней смены
		 */
		result =  getElementProperty(getDriver(), ID_OPERDAYSWF, "name:" + LOCATOR_TABLE_ROW_CLASS_NAME + "/" + LOCATOR_CASHIER_NAME + "|" + String.valueOf(shiftsForCah - 1), "text");
		log.info("Фамилия кассира, работающего на кассе " + cashNumber + " - "  + result);
		
		return result;
	}

	public String getCashierNameWithNoShift(int cashNumber){
		scrollTableDown(getDriver(), ID_OPERDAYSWF, LOCATOR_CASHLIST_TABLE);
		setLocatorTableRowClassName(cashNumber);
		return  getElementProperty(getDriver(), ID_OPERDAYSWF, "name:" + LOCATOR_TABLE_ROW_CLASS_NAME + "/" + LOCATOR_NOSHIFT_CASHIER_NAME, "text");
	}
	
	public CashOperDayTabPage refreshOperdayTab(){
		navigatePage(CheckSearchPage.class, TABLEREPORTS);
		navigatePage(CashesPage.class, CASHES);
		DisinsectorTools.delay(1000);
		return this;
	}
	
	public CashOperDayTabPage closeOperDay() throws Exception{
		if (canCloseOperday()) {
			clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_CLOSE_OPERDAY);
			waitSpinner(ID_OPERDAYSWF);
			return this;
		} else {
			throw new Exception("Невозможно закрыть опердень. Кнопка закрытия опердня неактивна!");
		}
	}
	
	public WarningPopUpMessage reopenOperDay() throws Exception{
		if (canReopenOperday()) {
			clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_OPEN_OPERDAY);
			return new WarningPopUpMessage(getDriver());
		} else {
			throw new Exception("Невозможно переоткрыть опердень. Кнопка переотрытия опердня неактивна!");
		}
	}
	
	public Boolean canCloseOperday(){
		return Boolean.parseBoolean(getElementProperty(getDriver(), ID_OPERDAYSWF, BUTTON_CLOSE_OPERDAY, "enabled"));
	}
	
	public Boolean canReopenOperday() throws Exception{
		boolean buttonExist = Boolean.parseBoolean(getPresentElementProperty(getDriver(), ID_OPERDAYSWF, BUTTON_OPEN_OPERDAY, "visible"));
		if (!buttonExist){
			throw new Exception("Невозможно переоткрыть открытый опердень!");
			
		} else {
			return Boolean.parseBoolean(getElementProperty(getDriver(), ID_OPERDAYSWF, BUTTON_OPEN_OPERDAY, "enabled"));
		}	
	}
	
}
