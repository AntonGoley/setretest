package ru.crystals.set10.pages.operday.cashes;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.*;
import static ru.crystals.set10.utils.FlexMediator.*;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.utils.DisinsectorTools;


public class  CashOperDayTabPage extends OperDayPage{
	
	private  String LOCATOR_TABLE_ROW_CLASS_NAME = ""; 
	private static final String LOCATOR_CASH_NUMBER_VALUE = "id:_cashDeskNumber;text:%s|0";
	private static final String LOCATOR_CASHIER_NAME = "className:SoftCuttingLabel";
	private static final String LOCATOR_NOSHIFT_CASHIER_NAME = "id:noCashierLabel";
	private static final String LOCATOR_CASHLIST_TABLE = "id:cashmachinesList2";
	
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
	
//	public int getLastCahsNumber(){
//		scrollTableDown(getDriver(), ID_OPERDAYSWF, LOCATOR_CASHLIST_TABLE);
//		return getElementsNum(getDriver(), ID_OPERDAYSWF, LOCATOR_CASH_NUMBER) - 1;
//	}
//	
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
	
	
}
