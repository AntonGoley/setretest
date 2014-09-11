package ru.crystals.test2.basic;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.test2.operDay.tableReports.TableReportPage;
import static ru.crystals.test2.utils.FlexMediator.*;


public class OperDayPage extends AbstractPage{
	
	static final String ID_OPERDAYSWF = "OperDay";
	static final String LOCATOR_TABLEREPORTS = "label=Табличные отчеты";
	
	
	public OperDayPage(WebDriver driver) {
		super(driver);
		switchWindow(true);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	
	public TableReportPage openTableReports() {
		waitForElement(getDriver(), ID_OPERDAYSWF, LOCATOR_TABLEREPORTS);
		clickElement(getDriver(), ID_OPERDAYSWF, LOCATOR_TABLEREPORTS);
		return new TableReportPage(getDriver());
	}
	
}
