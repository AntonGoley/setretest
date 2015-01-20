package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;

import static ru.crystals.set10.utils.FlexMediator.*;


public class  TableReportPage extends AbstractPage{
	
	public static final String ID_OPERDAYSWF = "OperDay";
	static final String ID_TAB_OTHER_LOCATOR = "id:shiftsNavigator/label:Прочее;className:Tab";
	static final String REPORT_LOCATOR = "label=";
	
	
	public static final String TAB_ADVERSTING = "Рекламные";
	public static final String TAB_FINANCIAL = "Финансовые";
	public static final String TAB_OTHER = "Прочее";
	
	
	public static final String REPORT_NAME_ADVERSTING = "Отчет по товарам в рекламных акциях"; 
	public static final String REPORT_NAME_GOOD_ON_TK = "Отчет по товару на ТК"; 
	public static final String REPORT_NAME_PRICE_CHECKER = "Отчёт по обращениям к прайсчекерам по всем ТК"; 
	public static final String REPORT_NAME_PLU_ON_WEIGHT = "Отчёт по количеству PLU в весах на ТК";
	public static final String REPORT_NAME_REFUND_CHECKS = "Отчёт по возвратам";
	public static final String REPORT_NAME_MRC_PRICE = "Прейскурант на табачные изделия";
	public static final String REPORT_NAME_CASH_REGNUMBERS = "Отчёт по регистрационным номерам касс";
	public static final String REPORT_NAME_WRONG_ADVERSTING_PRICE = "Некорректная акционная цена";
	
	
	public TableReportPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public <T> T openReportConfigPage(Class<T> report, String reportName) {
		clickElement(getDriver(), ID_OPERDAYSWF, ID_TAB_OTHER_LOCATOR);
		clickElement(getDriver(), ID_OPERDAYSWF, REPORT_LOCATOR + reportName);
		return PageFactory.initElements(getDriver(), report);
	}
	
}
