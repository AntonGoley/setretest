package ru.crystals.set10.pages.operday.searchcheck;

import java.util.Date;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.SaveFile;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CheckSearchPage extends SearchFormPopUp implements SaveFile {
	
	/*
	 * Локаторы элементов страницы
	 */
	static final String BUTTON_SEARCH = "findChecksButton";
	
	// результат поиска
	static final String SEARCH_RESULT = "searchResultLabel";
	
	public static final String LOCATOR_XLS_CHECK_CONTENT = "label:Позиции чеков";
	public static final String LOCATOR_XLS_CHECK_HEADERS = "label:Заголовки чеков";
	public static final String XLS_REPORT_HEADERS_PATTERN = "PurchasePayments*.xlsx";
	public static final String XLS_REPORT_CONTENT_PATTERN = "PurchasePositions*.xlsx";
	
	static final String BUTTON_GO_TO_CHECK = "label:Перейти к чеку";
	
	static final String SEARCH_RESULTS_GRID = "checksListGrid";

	
	public CheckSearchPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	
	public void doSearch(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SEARCH);
		log.info("Выполнить поиск!");
		long timeBefore = new Date().getTime();
		waitSpinner(ID_OPERDAYSWF);
		log.info("Время выполнения поиска на UI: " + (new Date().getTime() - timeBefore));
		new CheckSearchPage(getDriver());
	}
	
	public CheckContentPage selectFirstCheck(){
		doFlexProperty(getDriver(), ID_OPERDAYSWF, SEARCH_RESULTS_GRID, new String[] {"selectedIndex", "1" } );
		waitForProperty(getDriver(), ID_OPERDAYSWF, BUTTON_GO_TO_CHECK, new String[]{"enabled", "true"});
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_GO_TO_CHECK);
		return new CheckContentPage(getDriver());
	}
	
	/*
	 * Метод нажимает кнопку поиск, до тех пор, пока в рез-х поиска не 
	 * появится значение @expectedResult
	 */
	public int getExpectedResultCount(int expectedResult){
		int result = getSearchResultCount();	
		long delay = 1500;
		long timeout = 0;
		while (timeout < (delay * 10)) {
			if(result == expectedResult) {
				break;
			};
			DisinsectorTools.delay(delay);
			timeout += delay;
			doSearch();
			result = getSearchResultCount();
		};	
		return result;
	}

}
