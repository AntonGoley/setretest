package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;

import static ru.crystals.set10.utils.FlexMediator.*;


public class  TableReportPage extends AbstractPage{
	
	static final String ID_OPERDAYSWF = "OperDay";
	static final String ID_TABBAR = "name=tabBar";
	static final String REPORT_LOCATOR = "label=";
	
	
	public static final int TAB_ADVERSTING = 0;
	public static final int TAB_FINANCIAL = 1;
	public static final int TAB_OTHER = 2;
	
	
	public static final String REPORT_NAME_ADVERSTING = "Отчет по товарам в рекламных акциях"; 
	public static final String REPORT_NAME_GOOD_ON_TK = "Отчет по товару на ТК"; 
	public static final String REPORT_NAME_PRICE_CHECKER = "Отчёт по обращениям к прайсчекерам по всем ТК"; 
	public static final String REPORT_NAME_PLU_ON_WEIGHT = "Отчёт по количеству PLU в весах на ТК";
	public static final String REPORT_NAME_REFUND_CHECKS= "Отчёт по возвратам";
	public static final String REPORT_NAME_MRC_PRICE= "Прейскурант на табачные изделия";
	public static final String REPORT_NAME_CASH_REGNUMBERS= "Отчет по регистрационным номерам касс";
	
	
	public TableReportPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	
	public <T> T openReportConfigPage(Class<T> report, int tabIndex, String reportName) {
		openReportOnTab(tabIndex, REPORT_LOCATOR + reportName);
		return PageFactory.initElements(getDriver(), report);
	}
	
	private void openReportOnTab(int tabIndex, String reportUrl){
		selectTab(tabIndex);
		clickElement(getDriver(), ID_OPERDAYSWF, reportUrl);
	}
	
	public TableReportPage selectTab(int tabIndex){
		doFlexProperty(getDriver(), ID_OPERDAYSWF, ID_TABBAR, new String[]{"selectedIndex", String.valueOf(tabIndex)});
		return this;
	}
	
}
