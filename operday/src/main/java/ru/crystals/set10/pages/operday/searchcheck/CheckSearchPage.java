package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CheckSearchPage extends OperDayPage{
	

	static final String BUTTON_SEARCH = "findChecksButton";
	static final String FILTER_OPEN = "expandButton";
	
	// результат поиска
	static final String SEARCH_RESULT = "searchResultLabel";
	
	static final String FILTER_CATEGORY = "categorySelector";
	
	// типы фильтров
	public static final String FILTER_CATEGORY_CHECK_NUMBER = "Номер чека";
	public static final String FILTER_CATEGORY_SHIFT_NUMBER = "Смена";
	public static final String FILTER_CATEGORY_CASH_NUMBER = "Касса";
	public static final String FILTER_CATEGORY_SHOP_NUMBER = "Номер магазина";
	public static final String FILTER_CATEGORY_GOOD_BAR_CODE = "Штрих-код товара";
	public static final String FILTER_CATEGORY_CHECK_BAR_CODE = "Штрих-код чека";
	// Типы чека
	public static final String FILTER_CATEGORY_CHECK_TYPE = "Тип чека";
	public static final String FILTER_CATEGORY_CHECK_TYPE_REFUND = "Чек возврата";
	public static final String FILTER_CATEGORY_CHECK_TYPE_SALE = "Чек продажи";
	
	//Типы оплат
	public static final String FILTER_CATEGORY_PAY_TYPE = "Тип оплаты";
	
	//Номер карты оплаты
	public static final String FILTER_CATEGORY_BANK_CARD_NUMBER = "Номер банковской карты";
	public static final String FILTER_CATEGORY_CHILD_CARD_NUMBER = "Номер детской карты";
	public static final String FILTER_CATEGORY_GIFT_CARD_NUMBER = "Номер подарочной карты";
	public static final String FILTER_CATEGORY_BONUS_CARD_NUMBER = "Номер бонусной карты";
	
	
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
	
	public CheckSearchPage openFilter(){
		clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_OPEN);
		return new CheckSearchPage(getDriver());
	}
	
	public int getSearchResultCount(){
		
		if (! waitForElementVisible(getDriver(), ID_OPERDAYSWF, SEARCH_RESULT)) {
			log.info("По данному запросу чеков не найдено");
			return 0;
		}
		/*
		 * TODO: 
		 * убрать задержку
		 * сделать тесты таким образом, чтобы всегда возвращался рез-т, отличный от предыдущего
		 * оставить задержку для тестов(в самих тестах), где необходимо проверит, что рез-т не изменился 
		 */
		DisinsectorTools.delay(2000);
		String[] result = getElementProperty(getDriver(), ID_OPERDAYSWF, SEARCH_RESULT, "text").split(" ");
		
		if (result.length<6){
			log.info("По данному запросу чеков не найдено");
			//log.info("Ошибка отображаения результатов поиска!");
			return 0;
		}
		// строка длины 6
		
		log.info("Результат поиска: " + result[0] + " " + result[1] + " " + result[2] + " " + result[3] + " " + result[4] + " " + result[5]);
		return Integer.valueOf(result[3]);
	}
	
	
	//TODO: new search form
	public CheckSearchPage setCheckNumber(String checkNumber){
		clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_OPEN);
		return setFilterText(FILTER_CATEGORY_CHECK_NUMBER, checkNumber);
	}

	public CheckSearchPage setFilterMultiText(String filter, String filterValue){
		
		// открыть фильтр и задать категорию
		ifSearchFiltersOpen();
		
		if (!getSelectedElement(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY).equals(filter)){
			selectElement(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY, filter);
		}
		
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
	
	public CheckSearchPage setFilterSelect(String filter, String filterValue){
		ifSearchFiltersOpen();
		selectElement(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY, filter);
		
		selectElement(getDriver(), ID_OPERDAYSWF, FILTER_TEXT_FIELD, filterValue);
		
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
	
}
