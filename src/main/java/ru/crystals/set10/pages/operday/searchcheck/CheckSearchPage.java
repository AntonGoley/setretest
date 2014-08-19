package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CheckSearchPage extends AbstractPage{
	
	static final String ID_OPERDAYSWF = "OperDay";
	static final String BUTTON_SEARCH = "label=Найти";
	static final String INPUT_CHECK_NUMBER = "checkNumberInput";
	static final String BUTTON_GO_TO_CHECK = "label=Перейти к чеку";
	
	
	public CheckSearchPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	
	public CheckSearchPage doSearch(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SEARCH);
		return new CheckSearchPage(getDriver());
	}
	
	public CheckSearchPage setCheckNumber(String checkNumber){
		typeText(getDriver(), ID_OPERDAYSWF, INPUT_CHECK_NUMBER, checkNumber);
		return new CheckSearchPage(getDriver());
	}
	
	public void selectCheck (String checkId){
		String checkNumberLocator = "text=" + checkId;  
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, checkNumberLocator);
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, BUTTON_GO_TO_CHECK);
	}
	
	public boolean isCheckInOperDay(String checkNumber){
		long timeoutCount = 0;
		delay(5000);
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
