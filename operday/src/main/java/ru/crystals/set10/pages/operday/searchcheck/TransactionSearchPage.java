package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  TransactionSearchPage extends CheckSearchPage {
	
	/*
	 * Локаторы элементов страницы
	 */
	static final String BUTTON_SEARCH = "label:Найти транзакции";
	static final String FILTER_OPEN = "expandButton";
	
	// результат поиска
	static final String SEARCH_RESULT = "searchResultLabel";
	
	public static final String LOCATOR_XLS_TRANSACTIONS = "label:Транзакции в Excel";
	public static final String XLS_REPORT_TRANSACTIONS = "TransactionHistory*.xlsx";
	
	static final String INPUT_CHECK_NUMBER = "checkNumberInput";
	static final String BUTTON_GO_TO_CHECK = "label:Перейти к чеку";
	// tab выбора условия поиска: по номеру карты, по штрихкоду и т.д 
	static final String SEARCH_TAB = "tabNav";
	
	static final String SEARCH_RESULTS_GRID = "checksListGrid";
	
	
	public TransactionSearchPage(WebDriver driver) {
		super(driver);
	}
	
	public void doSearch(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SEARCH);
		log.info("Выполнить поиск!");
		new TransactionSearchPage(getDriver());
	}
	
	public void getFieldValue(){
		
	}
	
	/*
	 * Метод нажимает кнопку поиск, до тех пор, пока в рез-х поиска не 
	 * появится значение @expectedResult
	 */
	public int getExpectedResultCount(int expectedResult){
		int result = getSearchResultCount();	
		long delay = 1000;
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
