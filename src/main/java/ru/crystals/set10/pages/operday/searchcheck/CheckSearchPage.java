package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CheckSearchPage extends OperDayPage{
	

	static final String BUTTON_SEARCH = "findChecksButton";
	static final String FILTER_OPEN = "expandButton";
	static final String SEARCH_RESULT = "searchResultLabel";

	
	
	static final String FILTER_CATEGORY = "categorySelector";
	
	// типы фильтров
	public static final String FILTER_CATEGORY_CHECK_NUMBER = "Номер чека";
	public static final String FILTER_CATEGORY_SHIFT_NUMBER = "Смена";
	public static final String FILTER_CATEGORY_CASH_NUMBER = "Касса";
	public static final String FILTER_CATEGORY_SHOP_NUMBER = "Номер магазина";
	public static final String FILTER_CATEGORY_GOOD_BAR_CODE = "Штрих-код товара";
	
	
	// элементы окна множественного выбора
	private static final String FILTER_MULTI_TEXT_OPEN_INPUT = "toogleButton";
	private static final String FILTER_MULTI_TEXT_INPUT = "valuesTextInput";
	private static final String FILTER_MULTI_TEXT_APPLY_BUTTON = "applyButton";
	
	// элементы окна текстового поля
	private static final String FILTER_TEXT_FIELD = "textInput";
	
	
	static final String INPUT_CHECK_NUMBER = "checkNumberInput";
	static final String BUTTON_GO_TO_CHECK = "label=Перейти к чеку";
	// tab выбора условия поиска: по номеру карты, по штрихкоду и т.д 
	static final String SEARCH_TAB = "tabNav";
	
	static final String SEARCH_RESULTS_GRID = "adg";

	
	
	public CheckSearchPage(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public CheckSearchPage doSearch(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SEARCH);
		return new CheckSearchPage(getDriver());
	}
	
	
	public int getSearchResultCount(){
		try {
			waitForElement(getDriver(), ID_OPERDAYSWF, SEARCH_RESULT);
		} catch (Exception e) {
			log.info("По данному запросу чеков не найдено");
			return 0;
		}
		
		String[] result = getElementProperty(getDriver(), ID_OPERDAYSWF, SEARCH_RESULT, "text").split(" ");
		// строка длины 6
		log.info("Результат поиска: " + result[0] + " " + result[1] + " " + result[2] + " " + result[3] + " " + result[4] + " " + result[5]);
		return Integer.valueOf(result[3]);
	}
	
	
	//TODO: new search form
	public CheckSearchPage setCheckNumber(String checkNumber){
		return setFilterText(FILTER_CATEGORY_CHECK_NUMBER, checkNumber);
	}

	public CheckSearchPage setFilterMultiText(String filter, String filterValue){
		
		// открыть фильтр и задать категорию
		ifSearchFiltersOpen();
		selectElement(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY, filter);

		//Открыть и заполнить множественный выбор
		clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_MULTI_TEXT_OPEN_INPUT);
		typeText(getDriver(), ID_OPERDAYSWF, FILTER_MULTI_TEXT_INPUT, filterValue);
		clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_MULTI_TEXT_APPLY_BUTTON);
		
		log.info("Задать условие поиска: " + filter + "; Значение: " + filterValue);
		return new CheckSearchPage(getDriver());
	}
	
	public CheckSearchPage setFilterText(String filter, String filterValue){
		ifSearchFiltersOpen();
		selectElement(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY, filter);

		//Открыть и заполнить множественный выбор
		typeText(getDriver(), ID_OPERDAYSWF, FILTER_TEXT_FIELD, filterValue);
		
		log.info("Задать условие поиска: " + filter + "; Значение: " + filterValue);
		return new CheckSearchPage(getDriver());
	}
	
	
	private void ifSearchFiltersOpen(){
		/*
		 * Проверка, открыто ли окно фильтра
		 * если нет, открыть его
		 */
		if ( !getElementProperty(getDriver(), ID_OPERDAYSWF, "name=filtersContainerPopup", "isPopUp").equals("true")){
			log.info(getElementProperty(getDriver(), ID_OPERDAYSWF, "name=filtersContainerPopup", "isPopUp"));
			clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_OPEN);
		};
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
