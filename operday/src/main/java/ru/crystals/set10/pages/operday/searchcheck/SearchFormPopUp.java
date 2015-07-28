package ru.crystals.set10.pages.operday.searchcheck;


import static ru.crystals.set10.utils.FlexMediator.*;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;


/*
 * Окно фильтра поиска
 */
public class SearchFormPopUp extends OperDayPage {
	
	/*
	 * ЛОКАТОРЫ
	 */
	
	// элементы окна множественного выбора
	private static final String FILTER_MULTI_TEXT_OPEN_INPUT = "id:toogleButton";
	private static final String FILTER_MULTI_TEXT_INPUT = "id:valuesTextInput";
	private static final String FILTER_MULTI_TEXT_APPLY_BUTTON = "id:applyButton";
	private static final String FILTER_ADD_CONDITION = "label:Добавить условие";
	private static final String FILTER_DELETE_ALL = "label:Удалить все";
	
	// элементы окна текстового поля
	private static final String FILTER_TEXT_FIELD = "id:textInput";
	
	//Выбор категории поиска
	static final String FILTER_CATEGORY = "id:categorySelector";
	
	//Выпадающие списки
	static final String FILTER_SELECT_FIELD = "id:comboBox";
	public static final String FILTER_SELECT_COMPARISION = "id:comparisonTypeComboBox";
	
	//Открытие фильтра
	static final String FILTER_OPEN = "id:expandButton";
	
	static final String FILTER_ADD_CATEGORY = "id:addFilterButton";
	
	//Результат поиска внизу страницы
	static final String SEARCH_RESULT = "id:searchResultLabel";
	
	/*
	 * КАТЕГОРИИ ПОИСКА
	 */
	
	// группа "По чеку"
	public static final String FILTER_CATEGORY_CHECK_TYPE = "Тип чека";
	// Типы чека
	public static final String FILTER_CATEGORY_CHECK_TYPE_REFUND = "Чек возврата";
	public static final String FILTER_CATEGORY_CHECK_TYPE_SALE = "Чек продажи";
	public static final String FILTER_CATEGORY_CHECK_TYPE_CANCEL = "Аннулированный чек";
	public static final String FILTER_CATEGORY_SUM_CHECK = "Сумма чека";
	public static final String FILTER_CATEGORY_CASH_NUMBER = "Касса";
	public static final String FILTER_CATEGORY_SHIFT_NUMBER = "Смена";
	public static final String FILTER_CATEGORY_CHECK_NUMBER = "Номер чека";
	public static final String FILTER_CATEGORY_CASHIER_TABNUM = "Табельный номер кассира";
	public static final String FILTER_CATEGORY_CHECK_BAR_CODE = "Штрих-код чека";
	//Centrum
	public static final String FILTER_CATEGORY_SHOP_NUMBER = "Номер магазина";
	
	//Оплаты
	public static final String FILTER_CATEGORY_PAY_TYPE = "Тип оплаты";
	public static final String FILTER_CATEGORY_SUM_PAYMENT= "Сумма оплаты";
	public static final String FILTER_CATEGORY_BANK_CARD_NUMBER = "Номер банковской карты";
	public static final String FILTER_CATEGORY_BONUS_CARD_NUMBER = "Номер бонусной карты";
	public static final String FILTER_CATEGORY_CHILD_CARD_NUMBER = "Номер детской карты";
	public static final String FILTER_CATEGORY_GIFT_CARD_NUMBER = "Номер подарочной карты";
	public static final String FILTER_CATEGORY_BANK_ID = "Код банка";
	public static final String FILTER_CATEGORY_TERMINAL_NUMBER = "Номер терминала";
	public static final String FILTER_CATEGORY_AUTHORIZATION_CODE = "Код авторизации";
	public static final String FILTER_CATEGORY_SERVER_RESPONSE_CODE = "Код ответа процессингового центра";
	public static final String FILTER_CATEGORY_BANK_RESPONSE_CODE = "Код ответа сервера";
	
	//Скидки
	public static final String FILTER_CATEGORY_DISCOUNT_CARD_NUMBER = "Номер скидочной карты";
	public static final String FILTER_CATEGORY_SUM_DISCOUNT_CHECK = "Сумма скидки на чек";
	public static final String FILTER_CATEGORY_SUM_DISCOUNT_POSITION = "Сумма скидки на позицию";
	
	//Позиции
	public static final String FILTER_CATEGORY_GOOD_TYPE = "Тип товара";
	public static final String FILTER_CATEGORY_SUM_POSITION = "Сумма позиции";
	public static final String FILTER_CATEGORY_GOOD_CODE = "Код товара";
	public static final String FILTER_CATEGORY_GOOD_BAR_CODE = "Штрих-код товара";
	
	//выпадающий список сравнения
	public static final String FILTER_CATEGORY_SELECT_EQUALS = "=";
	public static final String FILTER_CATEGORY_SELECT_SMALLER = "<";
	public static final String FILTER_CATEGORY_SELECT_GREATER = ">";
	
	private static HashMap<String, String> filtersClassNames = new HashMap<String, String>();
	
	
	static {
		filtersClassNames.put(FILTER_CATEGORY_CHECK_BAR_CODE, "className:CheckBarcodeFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_CASH_NUMBER, "className:CashDeckNumbersFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_CHECK_TYPE, "className:CheckTypeFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_SHIFT_NUMBER, "className:ShiftNumbersFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_SHOP_NUMBER, "className:ShopNumbersFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_CHECK_NUMBER, "className:CheckNumbersFilterModule/");
		
		filtersClassNames.put(FILTER_CATEGORY_GOOD_BAR_CODE, "className:GoodsBarcodesFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_GOOD_CODE, "className:GoodsCodesFilterModule/");
		
		filtersClassNames.put(FILTER_CATEGORY_DISCOUNT_CARD_NUMBER, "className:DiscountCardNumbersFilterModule/");
		
		filtersClassNames.put(FILTER_CATEGORY_AUTHORIZATION_CODE, "className:AuthorizationCodeFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_TERMINAL_NUMBER, "className:TerminalNumberFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_SERVER_RESPONSE_CODE, "className:ProcessingCenterResultCodeFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_BANK_RESPONSE_CODE, "className:ServerResponseCodeFilterModule/");
		
		filtersClassNames.put(FILTER_CATEGORY_BANK_CARD_NUMBER, "className:BankCardFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_BONUS_CARD_NUMBER, "className:BonusCardFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_GIFT_CARD_NUMBER, "className:GiftCardFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_CHILD_CARD_NUMBER, "className:ChildCardFilterModule/");
		
		
		filtersClassNames.put(FILTER_CATEGORY_PAY_TYPE, "className:PaymentTypeFilterModule/");

		filtersClassNames.put(FILTER_CATEGORY_SUM_PAYMENT, "className:PaymentSumFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_SUM_CHECK, "className:CheckSumFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_SUM_POSITION, "className:PositionSumFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_SUM_DISCOUNT_CHECK, "className:CheckDiscountSumFilterModule/");
		filtersClassNames.put(FILTER_CATEGORY_SUM_DISCOUNT_POSITION, "className:PositionDiscountSumFilterModule/");
	}
	
	
	public SearchFormPopUp(WebDriver driver) {
		super(driver);
	}
	
	public SearchFormPopUp openFilter(){
		clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_OPEN);
		return new SearchFormPopUp(getDriver());
	}
	
	public SearchFormPopUp addFilter(){
		clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_ADD_CONDITION);
		return new SearchFormPopUp(getDriver());
	}
	
	public SearchFormPopUp deleteAllFilters(){
		clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_DELETE_ALL);
		return new SearchFormPopUp(getDriver());
	}
	
	public SearchFormPopUp setCheckBarcode(PurchaseEntity purchase){
		return setFilterText(FILTER_CATEGORY_CHECK_BAR_CODE, getCheckBarcode(purchase));
	}

	public SearchFormPopUp setFilterMultiText(String filter, String filterValue){
		ifSearchFiltersOpen(filter);
		
		log.info("Задать условие поиска: " + filter + "; Значение: " + filterValue);
		//Открыть и заполнить множественный выбор
		clickElement(getDriver(), ID_OPERDAYSWF,  filtersClassNames.get(filter) + FILTER_MULTI_TEXT_OPEN_INPUT);
		typeText(getDriver(), ID_OPERDAYSWF, FILTER_MULTI_TEXT_INPUT, filterValue);
		clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_MULTI_TEXT_APPLY_BUTTON);
		
		return new SearchFormPopUp(getDriver());
	}
	
	public SearchFormPopUp setFilterText(String filter, String filterValue){
		ifSearchFiltersOpen(filter);

		//Открыть и заполнить множественный выбор
		typeText(getDriver(), ID_OPERDAYSWF, filtersClassNames.get(filter) + FILTER_TEXT_FIELD, filterValue);
		
		log.info("Задать условие поиска: " + filter + "; Значение: " + filterValue);
		return new SearchFormPopUp(getDriver());
	}
	
	public SearchFormPopUp setFilterSelect(String filter, String filterValue){
		ifSearchFiltersOpen(filter);
		
		selectElement(getDriver(), ID_OPERDAYSWF, filtersClassNames.get(filter) + FILTER_SELECT_FIELD, filterValue);
		
		log.info("Задать условие поиска: " + filter + "; Значение: " + filterValue);
		return new SearchFormPopUp(getDriver());
	}
	
	public SearchFormPopUp setFilterSelectSum(String filter, String clause, String filterValue){
		ifSearchFiltersOpen(filter);
		
		typeText(getDriver(), ID_OPERDAYSWF, filtersClassNames.get(filter) + FILTER_TEXT_FIELD, filterValue);
		
		selectElement(getDriver(), ID_OPERDAYSWF, filtersClassNames.get(filter) + FILTER_SELECT_COMPARISION, clause);
		log.info("Задать условие поиска: " + filter + "; Значение: " + clause + " " + filterValue);
		return new SearchFormPopUp(getDriver());
	}
	
	public int getSearchResultCount(){
		if (! waitForElementVisible(getDriver(), ID_OPERDAYSWF, SEARCH_RESULT)) {
			log.info("По данному запросу чеков не найдено");
			return 0;
		}
		
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
	 * Метод проверяет, открыто ли окно фильтра (pop-up)
	 * и задает категорию поиска
	 */
	private void ifSearchFiltersOpen(String filter){
		/*
		 * Проверка, открыто ли окно фильтра
		 * если нет, открыть его
		 */
		if ( !getElementProperty(getDriver(), ID_OPERDAYSWF, "name=filtersContainerPopup", "isPopUp").equals("true")){
			log.info(getElementProperty(getDriver(), ID_OPERDAYSWF, "name=filtersContainerPopup", "isPopUp"));
			clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_OPEN);
		};
		
		if ( !waitForElementVisible(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY)){
			log.info("В фильтре поиска сбросился фильтр по дате!!! ".toUpperCase());
			clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_ADD_CATEGORY);
		};
		
		/*
		 * если выбираем уже выбранный фильтр, то происходит удаление фильтра - обход этого поведения
		 */

		if (!getSelectedElement(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY + "|0").equals(filter)){
			selectElement(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY + "|0", filter);
		}
	}
	
	public void saveFile(String fileType){
		clickElement(getDriver(), ID_OPERDAYSWF, fileType);
	};
	
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

}
