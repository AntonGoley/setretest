package ru.crystals.set10.pages.operday.cashes;


import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;

import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CashesPage extends OperDayPage{
	
	public static final String LOCATOR_OPERDAY_TAB = "id:shiftsNavigator/label:Операционный день;className:Tab";
	public static final String LOCATOR_PROBLEM_SHIFTS_TAB = "id:shiftsNavigator/label:Проблемные смены;className:Tab";
	
	public static final String LOCATOR_MAINCASH_TAB = "id:shiftsNavigator/label:Главная касса;className:Tab";
	public static final String LOCATOR_ACTS_TAB = "id:shiftsNavigator/label:Акты;className:Tab";
	
	/* локаторы календаря */
	public static final String LOCATOR_CALENDAR = "id:showCalendarLink";
	public static final String LOCATOR_CALENDAR_DAY = "id:dateChooser/text:%s|0";
	
	public CashesPage(WebDriver driver) {
		super(driver);
	}
	
	public <T> T openTab(Class<T> tabPage, String tab){
		clickElement(getDriver(), ID_OPERDAYSWF, tab);
		return PageFactory.initElements(getDriver(), tabPage);
	}
	
	public void selectODFromCalendar(Long date){
		String calendarlabel = getElementProperty(getDriver(), ID_OPERDAYSWF, LOCATOR_CALENDAR, "label");
		
		if (calendarlabel.equals("показать календарь")) {
			doFlexMouseDown(getDriver(), ID_OPERDAYSWF, LOCATOR_CALENDAR);
		};
		
		/*
		 * Выбираем год, месяц и день
		 */
		doFlexProperty(getDriver(), ID_OPERDAYSWF, "id:dateChooser", new String[]{"displayedYear", DisinsectorTools.getDate("yyyy", date)} );
		/* Месяц в календаре начинается с 0 */
		Integer  month = Integer.valueOf(DisinsectorTools.getDate("M", date)) - 1;
		doFlexProperty(getDriver(), ID_OPERDAYSWF, "id:dateChooser", new String[]{"displayedMonth", String.valueOf(month)});
		String day = DisinsectorTools.getDate("d", date);
		doFlexMouseUp(getDriver(), ID_OPERDAYSWF, String.format(LOCATOR_CALENDAR_DAY, day), false);
		
	}
	
}
