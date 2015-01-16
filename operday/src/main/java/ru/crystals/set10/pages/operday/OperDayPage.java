package ru.crystals.set10.pages.operday;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.pages.operday.cashes.CashesPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.tablereports.TableReportPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class OperDayPage extends AbstractPage{
	
	protected static final String ID_OPERDAYSWF = "OperDay";
	static final String LOCATOR_TABLEREPORTS = "label=Табличные отчеты";
	static final String LOCATOR_SEARCH_CHECK = "label=Поиск чеков";
	static final String LOCATOR_SEARCH_CAHSES= "label=Кассы";
	protected String LINK_SAVE_EXCEL;
	
	/* 
	 * Флаг, что документ(форма КМ, сопроводительный документ) еще не загружался
	 */
	private static boolean ifFirstDocument = true;
	
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
	
	/*
	 * Копируем в консоль содержание документа
	 * и возвращаем как String 
	 */
	public String getReportText(){
		// если это первый документ, который печатаем	
			if (ifFirstDocument) {
				log.info("Ожидание первой загрузки сопроводительного документа");
				DisinsectorTools.delay(10000);
				ifFirstDocument = false;
			}
			String reportText = "";
			getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//embed")));
			//TODO: убрать задержку
			DisinsectorTools.delay(2000);
			switchWindow(false);
			reportText = DisinsectorTools.getConsoleOutput(getDriver());
			switchWindow(true);
			return reportText;
	}
	
//	public File exportFileData(String chromeDownloadPath, String reportNamePattern, SaveFile saveLInk, String fileType){
//		if (DisinsectorTools.fileFilter(chromeDownloadPath, reportNamePattern).length != 0) {
//			log.info(String.format("Предыдущие файлы отчетов %s не удалены перед выполнением теста", reportNamePattern));
//			return new File("");
//		}
//		
//		//clickElement(getDriver(), ID_OPERDAYSWF, LINK_SAVE_EXCEL);
//		saveLInk.saveFile(fileType);
//		
//		return DisinsectorTools.getDownloadedFile(chromeDownloadPath, reportNamePattern);
//	}
}
