package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import ru.crystals.set10.pages.operday.OperDayPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  TableReportPage extends OperDayPage{
	
	static final String ID_TAB_LOCATOR = "id:reportsTabBar/className:Tab;label:";
	static final String REPORT_LOCATOR = "className:LinkButton;label:";
	
	
	public static final String TAB_ADVERSTING = "Рекламные";
	public static final String TAB_FINANCIAL = "Финансовые";
	public static final String TAB_OTHER = "Прочее";
	
	
	public static final String REPORT_NAME_ADVERSTING = "Отчет по товарам в рекламных акциях"; 
	public static final String REPORT_NAME_GOOD_ON_TK = "Отчет по товару на ТК"; 
	public static final String REPORT_NAME_PRICE_CHECKER = "Обращения к прайсчекерам по всем ТК"; 
	public static final String REPORT_NAME_PLU_ON_WEIGHT = "Отчёт по количеству PLU в весах на ТК";
	public static final String REPORT_NAME_REFUND_CHECKS = "Отчёт по возвратам";
	public static final String REPORT_NAME_MRC_PRICE = "Прейскурант на табачные изделия";
	public static final String REPORT_NAME_CASH_REGNUMBERS = "Отчёт по регистрационным номерам касс";
	public static final String REPORT_NAME_WRONG_ADVERSTING_PRICE = "Некорректная акционная цена";
	public static final String REPORT_NAME_ADVERSTING_IN_CHECK = "Применение рекламной акции в чеках";
	
	public TableReportPage(WebDriver driver) {
		super(driver);
	}
	
	public <T> T openReportConfigPage(Class<T> report, String tabName, String reportName) {
		clickElement(getDriver(), ID_OPERDAYSWF, ID_TAB_LOCATOR + tabName);
		clickElement(getDriver(), ID_OPERDAYSWF, REPORT_LOCATOR + reportName + "|0");
		return PageFactory.initElements(getDriver(), report);
	}
	
}
