package ru.crystals.set10.pages.operday.cashes;


import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import ru.crystals.set10.pages.operday.OperDayPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CashesPage extends OperDayPage{
	
	public static final String LOCATOR_OPERDAY_TAB = "id:shiftsNavigator/label:Операционный день;className:Tab";
	public static final String LOCATOR_PROBLEM_SHIFTS_TAB = "id:shiftsNavigator/label:Проблемные смены;className:Tab";
	
	public static final String LOCATOR_MAINCASH_TAB = "id:shiftsNavigator/label:Главная касса;className:Tab";
	public static final String LOCATOR_ACTS_TAB = "id:shiftsNavigator/label:Акты;className:Tab";
	
	/* локаторы календаря */
	public static final String LOCATOR_CALENDAR = "id:showCalendarLink";
	public static final String LOCATOR_CALENDAR_DAY = "id:dateChooser/text:%day";
	
	public CashesPage(WebDriver driver) {
		super(driver);
	}
	
	public <T> T openTab(Class<T> tabPage, String tab){
		clickElement(getDriver(), ID_OPERDAYSWF, tab);
		return PageFactory.initElements(getDriver(), tabPage);
	}
	
	public void selectODFromCalendar(String day){
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, LOCATOR_CALENDAR);
		doFlexMouseUp(getDriver(), ID_OPERDAYSWF, String.format(LOCATOR_CALENDAR_DAY, day), true);
	}
	
}
