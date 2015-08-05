package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import ru.crystals.set10.pages.basic.SaveFile;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  ReportConfigPage extends OperDayPage implements SaveFile {
	
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
	}
	
	public HTMLRepotResultPage generateReport(String reportType){
		// doFlexMouseDown чтобы убрать flexSuggest
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, reportType);
		clickElement(getDriver(), ID_OPERDAYSWF, reportType);
		if (ifFirstReport) {
			log.info("Ожидание первой загрузки HTML отчета");
			DisinsectorTools.delay(20000);
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
