package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CheckSearchPage extends OperDayPage{
	

	// id кнопки "Найти", если выделен таб "по данным чека"
	static final String BUTTON_SEARCH = "findCheckByCheckDataButton";
	static final String INPUT_CHECK_NUMBER = "checkNumberInput";
	static final String BUTTON_GO_TO_CHECK = "label=Перейти к чеку";
	// tab выбора условия поиска: по номеру карты, по штрихкоду и т.е 
	static final String SEARCH_TAB = "tabNav";
	
	static final String SEARCH_RESULTS_GRID = "adg";
	
	static final String INDEX_CARDNUMBER_LINK = "0";
	static final String INDEX_BARCODE_LINK = "1";
	static final String INDEX_CHECKDATA_LINK = "2";
	static final String INDEX_GOODDATA_LINK= "3";
	
	
	public CheckSearchPage(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public CheckSearchPage doSearch(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SEARCH);
		return new CheckSearchPage(getDriver());
	}
	
	public CheckSearchPage setCheckNumber(String checkNumber){
		doFlexProperty(getDriver(), ID_OPERDAYSWF, SEARCH_TAB, new String[] {"selectedIndex", INDEX_CHECKDATA_LINK } );
		typeText(getDriver(), ID_OPERDAYSWF, INPUT_CHECK_NUMBER, checkNumber);
		return new CheckSearchPage(getDriver());
	}
	
	public CheckContentPage selectFirstCheck(){
		doFlexProperty(getDriver(), ID_OPERDAYSWF, SEARCH_RESULTS_GRID, new String[] {"selectedIndex", "1" } );
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, BUTTON_GO_TO_CHECK);
		return new CheckContentPage(getDriver());
	}
	
	public boolean isCheckInOperDay(String checkNumber){
		long timeoutCount = 0;
		
		DisinsectorTools.delay(5000);
		doSearch();
		while (timeoutCount <= 3 ){
			try {
				waitForElement(getDriver(), ID_OPERDAYSWF, "text=" + checkNumber);
				return true;
			} catch (Exception e) {
				log.info("Чек с номером % не найден. ");
				timeoutCount++;
			}
		}	
		return false;
	}
	
	
}
