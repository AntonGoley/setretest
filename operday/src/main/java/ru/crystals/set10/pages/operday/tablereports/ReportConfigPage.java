package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.pages.basic.SaveFile;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  ReportConfigPage extends AbstractPage implements SaveFile {
	
	static final String ID_OPERDAYSWF = "OperDay";
	public static final String HTMLREPORT = "downloadHTMLButton";
	public static final String EXCELREPORT = "downloadExcelButton";
	public static final String PDFREPORT = "downloadPDFButton";
	
	/*
	 * необходимо немного подождать, при первом формировании отчета 
	 * т.к генерятся все шаблоны jasper на сервере
	 */
	static boolean ifFirstReport = true;
	
	public ReportConfigPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public HTMLRepotResultPage generateReport(String reportType){
		// doFlexMouseDown чтобы убрать flexSuggest
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, reportType);
		clickElement(getDriver(), ID_OPERDAYSWF, reportType);
		if (ifFirstReport) {
			log.info("Ожидание первой загрузки HTML отчета");
			DisinsectorTools.delay(2000);
			ifFirstReport = false;
		}

		switchWindow(false);
		return new HTMLRepotResultPage(getDriver());
	}

	@Override
	public void saveFile(String fileType) {
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, fileType);
		clickElement(getDriver(), ID_OPERDAYSWF, fileType);
	}
	
}
