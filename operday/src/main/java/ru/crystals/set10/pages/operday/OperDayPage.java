package ru.crystals.set10.pages.operday;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class OperDayPage extends AbstractPage{
	
	protected static final String ID_OPERDAYSWF = "OperDay";
	public static final String TABLEREPORTS = "label:Табличные отчеты";
	public static final String SEARCH_CHECK = "label:Поиск чеков";
	public static final String CASHES = "label:Кассы";
	public static final String SEARCH_TRANSACTIONS = "label:Поиск транзакций";
	protected String LINK_SAVE_EXCEL;
	
	/* 
	 * Флаг, что документ(табличный отчет, форма КМ, сопроводительный документ) еще не загружался
	 */
	private static boolean ifFirstDocument = true;
	
	/*
	 * Конструктор используется при открытии
	 * страницы опердня с разводящего экрана
	 */
	public OperDayPage(WebDriver driver, boolean switchWindow) {
		super(driver);
		switchWindow(switchWindow);
		isSWFReady();
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	/*
	 * Конструктор используется наследниками класса OperDayPage
	 */
	public OperDayPage(WebDriver driver) {
		super(driver);
		isSWFReady();
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	
	public <T> T navigatePage(Class<T> page, String linkLocator){
		clickElement(getDriver(), ID_OPERDAYSWF, linkLocator);
		return PageFactory.initElements(getDriver(), page);
	}
	
	/*
	 * Копируем в консоль содержание документа
	 * и возвращаем как String 
	 */
	public synchronized String getReportText(){
		// если это первый документ, который печатаем	
			if (ifFirstDocument) {
				log.info("Ожидание первой загрузки документа");
				DisinsectorTools.delay(15000);
				ifFirstDocument = false;
			}
			String reportText = "";
			switchWindow(false);
			
			//TODO: хрупкая конструкция
			/*
			 * Задержка перед загрузкой страницы 
			 */
			DisinsectorTools.delay(2000);
			getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//embed")));
			
			reportText = DisinsectorTools.getConsoleOutput(getDriver());
			switchWindow(true);
			return reportText;
	}

}
