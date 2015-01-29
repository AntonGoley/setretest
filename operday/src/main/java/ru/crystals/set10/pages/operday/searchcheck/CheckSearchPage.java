package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.pages.basic.SaveFile;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CheckSearchPage extends OperDayPage implements SaveFile{
	
	/*
	 * Локаторы элементов страницы
	 */
	static final String BUTTON_SEARCH = "findChecksButton";
	static final String FILTER_OPEN = "expandButton";
	
	// результат поиска
	static final String SEARCH_RESULT = "searchResultLabel";
	
	public static final String LOCATOR_XLS_CHECK_CONTENT = "label:Позиции чеков";
	public static final String LOCATOR_XLS_CHECK_HEADERS = "label:Заголовки чеков";
	public static final String XLS_REPORT_HEADERS_PATTERN = "PurchasePayments*.xlsx";
	public static final String XLS_REPORT_CONTENT_PATTERN = "PurchasePositions*.xlsx";
	
	
	static final String INPUT_CHECK_NUMBER = "checkNumberInput";
	static final String BUTTON_GO_TO_CHECK = "label:Перейти к чеку";
	// tab выбора условия поиска: по номеру карты, по штрихкоду и т.д 
	static final String SEARCH_TAB = "tabNav";
	
	static final String SEARCH_RESULTS_GRID = "checksListGrid";
	
	/*
	 * Лоакаторы для фильтров поиска
	 */
	static final String FILTER_CATEGORY = "categorySelector";
	static final String FILTER_SELECT_FIELD = "comboBox";
	
	// группа "По чеку"
	public static final String FILTER_CATEGORY_CHECK_NUMBER = "Номер чека";
	public static final String FILTER_CATEGORY_SHIFT_NUMBER = "Смена";
	public static final String FILTER_CATEGORY_CASH_NUMBER = "Касса";
	public static final String FILTER_CATEGORY_SHOP_NUMBER = "Номер магазина";
	public static final String FILTER_CATEGORY_GOOD_BAR_CODE = "Штрих-код товара";
	public static final String FILTER_CATEGORY_GOOD_CODE = "Код товара";
	public static final String FILTER_CATEGORY_CHECK_BAR_CODE = "Штрих-код чека";
	
	public static final String FILTER_CATEGORY_DISCOUNT_CARD_NUMBER = "Номер скидочной карты";
	
	// Типы чека
	public static final String FILTER_CATEGORY_CHECK_TYPE = "Тип чека";
	public static final String FILTER_CATEGORY_CHECK_TYPE_REFUND = "Чек возврата";
	public static final String FILTER_CATEGORY_CHECK_TYPE_SALE = "Чек продажи";
	
	//Типы оплат
	public static final String FILTER_CATEGORY_PAY_TYPE = "Тип оплаты";

	public static final String FILTER_CATEGORY_BANK_CARD_NUMBER = "Номер банковской карты";
	public static final String FILTER_CATEGORY_CHILD_CARD_NUMBER = "Номер детской карты";
	public static final String FILTER_CATEGORY_GIFT_CARD_NUMBER = "Номер подарочной карты";
	public static final String FILTER_CATEGORY_BONUS_CARD_NUMBER = "Номер бонусной карты";
	
	public static final String FILTER_CATEGORY_SERVER_RESPONSE_CODE = "Код ответа процессингового центра";
	public static final String FILTER_CATEGORY_TERMINAL_NUMBER = "Номер терминала";
	public static final String FILTER_CATEGORY_BANK_RESPONSE_CODE = "Код ответа сервера";
	public static final String FILTER_CATEGORY_AUTHORIZATION_CODE = "Код авторизации";
	public static final String FILTER_CATEGORY_BANK_ID = "Код банка";
	
	// элементы окна множественного выбора
	private static final String FILTER_MULTI_TEXT_OPEN_INPUT = "toogleButton";
	private static final String FILTER_MULTI_TEXT_INPUT = "valuesTextInput";
	private static final String FILTER_MULTI_TEXT_APPLY_BUTTON = "applyButton";
	
	// элементы окна текстового поля
	private static final String FILTER_TEXT_FIELD = "textInput";

	
	public CheckSearchPage(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public CheckSearchPage doSearch(){
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_SEARCH);
		log.info("Выполнить поиск!");
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
		
		//DisinsectorTools.delay(500);
		String[] result = getElementProperty(getDriver(), ID_OPERDAYSWF, SEARCH_RESULT, "text").split(" ");
		
		if (result.length<6){
			log.info("По данному запросу чеков не найдено");
			return 0;
		}

		// строка длины 6
		log.info("Результат поиска: " + result[0] + " " + result[1] + " " + result[2] + " " + result[3] + " " + result[4] + " " + result[5]);
		return Integer.valueOf(result[3]);
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
	
	public CheckSearchPage setCheckBarcode(PurchaseEntity purchase){
		//clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_OPEN);
		return setFilterText(FILTER_CATEGORY_CHECK_BAR_CODE, getCheckBarcode(purchase));
	}

	public CheckSearchPage setFilterMultiText(String filter, String filterValue){
		// открыть фильтр и задать категорию
		ifSearchFiltersOpen();
		/*
		 * если выбираем уже выбранный фильтр, то происходит удаление фильтра - обход этого поведения
		 */
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
		
		selectElement(getDriver(), ID_OPERDAYSWF, FILTER_SELECT_FIELD, filterValue);
		
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
		clickElement(getDriver(), ID_OPERDAYSWF, BUTTON_GO_TO_CHECK);
		return new CheckContentPage(getDriver());
	}
	
	public String getCheckBarcode(PurchaseEntity purchase){
		
			int cash = (int)(100 + purchase.getShift().getCashNum());
			int shift = (int)(1000 + purchase.getShift().getNumShift());
			String date = DisinsectorTools.getDate("ddMMyy", purchase.getDateCommit().getTime());
			int check = (int)(1000 + purchase.getNumber() );
			
			StringBuffer result = new StringBuffer();
			/*
			 * Формат чека ccc.ssss.dddddd.nnnn
			 * ccc - касса, ssss - смена, dddddd - дата, nnnn - номер чека
			 */
			result.append(String.valueOf(cash).replaceFirst("^.", String.valueOf((long)Math.floor(cash/100) - 1))).append(".");
			result.append(String.valueOf(shift).replaceFirst("^.", String.valueOf((long)Math.floor(shift/1000) - 1))).append(".");
			result.append(date).append(".");
			result.append(String.valueOf(check).replaceFirst("^.", String.valueOf((long)Math.floor(check/1000) - 1)));
			return result.toString();
	}
	
	public void saveFile(String fileType){
		clickElement(getDriver(), ID_OPERDAYSWF, fileType);
	};
}
