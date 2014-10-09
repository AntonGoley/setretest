package ru.crystals.set10.pages.operday;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.pages.operday.cashes.CashesPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class OperDayPage extends AbstractPage{
	
	protected static final String ID_OPERDAYSWF = "OperDay";
	static final String LOCATOR_TABLEREPORTS = "label=Табличные отчеты";
	static final String LOCATOR_SEARCH_CHECK = "label=Поиск чеков";
	static final String LOCATOR_SEARCH_CAHSES= "label=Кассы";
	
	
	public OperDayPage(WebDriver driver, boolean switchWindow) {
		super(driver);
		switchWindow(switchWindow);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	
	public TableReportPage openTableReports() {
		clickElement(getDriver(), ID_OPERDAYSWF, LOCATOR_TABLEREPORTS);
		return new TableReportPage(getDriver());
	}
	
	public CheckSearchPage openCheckSearch() {
		clickElement(getDriver(), ID_OPERDAYSWF, LOCATOR_SEARCH_CHECK);
		return new CheckSearchPage(getDriver());
	}
	
	public CashesPage openCashes() {
		clickElement(getDriver(), ID_OPERDAYSWF, LOCATOR_SEARCH_CAHSES);
		return new CashesPage(getDriver());
	}
	
}
