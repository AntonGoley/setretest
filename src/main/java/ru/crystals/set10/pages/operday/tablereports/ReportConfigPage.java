package ru.crystals.set10.pages.operday.tablereports;


import java.io.File;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  ReportConfigPage extends AbstractPage{
	
	static final String ID_OPERDAYSWF = "OperDay";
	public static final String HTMLREPORT = "download_html";
	public static final String EXCELREPORT = "download_excel";
	public static final String PDFREPORT = "download_pdf";
	
	/*
	 * необходимо немного подождать, при первом формировании отчета 
	 *  
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
		switchWindow(false);
		
		if (ifFirstReport) {
			log.info("Ожидание первой загрузки отчета");
			DisinsectorTools.delay(20000);
			ifFirstReport = false;
		}
		
		return new HTMLRepotResultPage(getDriver());
	}
	
	// for excel and pdf reports
	public File saveReportFile(String reportType, String path, String pattern){
		
		if (DisinsectorTools.fileFilter(path, pattern).length != 0) {
			log.info(String.format("Предыдущие файлы отчетов %s не удалены перед выполнением теста", pattern));
			return new File("");
		}
		
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, reportType);
		clickElement(getDriver(), ID_OPERDAYSWF, reportType);
		return DisinsectorTools.getDownloadedFile(path, pattern);
	}
	
}
