package ru.crystals.set10.pages.operday.searchcheck;


import java.io.File;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.operday.tablereports.ReportConfigPage.EXCELREPORT;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  PaymentTransactionsPage extends OperDayPage{
	
	public static final String LINK_SAVE_EXCEL = "label=в excel";
	public static final String ID_OPERDAYSWF = "OperDay";
	
	public PaymentTransactionsPage(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public boolean validateData(String locatorText){
		return (boolean) waitForElementVisible(getDriver(), ID_OPERDAYSWF, String.format("text=%s", locatorText));
	}
	
	public File saveExcel(String chromeDownloadPath, String reportNamePattern ){
		if (DisinsectorTools.fileFilter(chromeDownloadPath, reportNamePattern).length != 0) {
			log.info(String.format("Предыдущие файлы отчетов %s не удалены перед выполнением теста", reportNamePattern));
			return new File("");
		}
		
		clickElement(getDriver(), ID_OPERDAYSWF, LINK_SAVE_EXCEL);
		
		return DisinsectorTools.getDownloadedFile(chromeDownloadPath, reportNamePattern);
		
	}
	
}
