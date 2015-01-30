package ru.crystals.set10.pages.operday.searchcheck;

import static ru.crystals.set10.utils.FlexMediator.clickElement;
import static ru.crystals.set10.utils.FlexMediator.getElementProperty;
import static ru.crystals.set10.utils.FlexMediator.getSelectedElement;
import static ru.crystals.set10.utils.FlexMediator.selectElement;
import static ru.crystals.set10.utils.FlexMediator.typeText;
import static ru.crystals.set10.utils.FlexMediator.waitForElementVisible;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;

public class SearchFormPopUp extends OperDayPage {

	/*
	 * Локаторы элементов страницы
	 */
	static final String FILTER_OPEN = "expandButton";
	
	// результат поиска
	static final String SEARCH_RESULT = "searchResultLabel";

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
	
	
	public SearchFormPopUp(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public SearchFormPopUp openFilter(){
		clickElement(getDriver(), ID_OPERDAYSWF,  FILTER_OPEN);
		return new SearchFormPopUp(getDriver());
	}
	
//	public void doSearch(){
//		log.info("Выполнить поиск!");
//	}
	
	public SearchFormPopUp setCheckBarcode(PurchaseEntity purchase){
		return setFilterText(FILTER_CATEGORY_CHECK_BAR_CODE, getCheckBarcode(purchase));
	}

	public SearchFormPopUp setFilterMultiText(String filter, String filterValue){
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
		return new SearchFormPopUp(getDriver());
	}
	
	public SearchFormPopUp setFilterText(String filter, String filterValue){
		ifSearchFiltersOpen();
		
		selectElement(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY, filter);

		//Открыть и заполнить множественный выбор
		typeText(getDriver(), ID_OPERDAYSWF, FILTER_TEXT_FIELD, filterValue);
		
		log.info("Задать условие поиска: " + filter + "; Значение: " + filterValue);
		return new SearchFormPopUp(getDriver());
	}
	
	public SearchFormPopUp setFilterSelect(String filter, String filterValue){
		ifSearchFiltersOpen();
		
		selectElement(getDriver(), ID_OPERDAYSWF, FILTER_CATEGORY, filter);
		
		selectElement(getDriver(), ID_OPERDAYSWF, FILTER_SELECT_FIELD, filterValue);
		
		log.info("Задать условие поиска: " + filter + "; Значение: " + filterValue);
		return new SearchFormPopUp(getDriver());
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
