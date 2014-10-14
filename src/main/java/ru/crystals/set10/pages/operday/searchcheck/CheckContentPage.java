package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.operday.OperDayPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CheckContentPage extends OperDayPage{
	
	public static final String LINK_NOMENCLATURE = "label=номенклатуру чека";
	public static final String LINK_GOODS_CHECK = "label=товарный чек";
	public static final String LINK_INVOICE = "label=товарную накладную";
	public static final String LINK_GOODS_BILL = "label=счет-фактуру";
	
	/*
	 * Поля модального окна контрагента.
	 * Оставлено в классе  CheckContentPage, пока не появятся
	 * требования к контрагенту
	 */
	public static final String INPUT_JURISTIC_NAME = "juristicPersonName";
	public static final String INPUT_INN = "inn";
	public static final String INPUT_KPP = "kpp";
	public static final String INPUT_JURISTIC_ADRESS = "address";
	public static final String BUTTON_OK = "id:spinnerPanel/label:OK";
	
	/* 
	 * Флаг, что сопроводительный документ еще не загружался
	 */
	private boolean ifFirstDocument = true;
	
	public CheckContentPage(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public String generateReport(String docType){
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, docType);
		clickElement(getDriver(), ID_OPERDAYSWF, docType);
		return getReportText();
	}

	public String generateReportWithCounterpart(
			String docType,
			String name,
			String inn,
			String kpp,
			String juristicAdress)
	{	
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, docType);
		clickElement(getDriver(), ID_OPERDAYSWF, docType);
		// Заполнить данные контрагента
		typeText(getDriver(), ID_OPERDAYSWF , INPUT_JURISTIC_NAME, name);
		typeText(getDriver(), ID_OPERDAYSWF , INPUT_INN, inn);
		typeText(getDriver(), ID_OPERDAYSWF , INPUT_KPP, kpp);
		typeText(getDriver(), ID_OPERDAYSWF , INPUT_JURISTIC_ADRESS, juristicAdress);
		clickElement(getDriver(), ID_OPERDAYSWF , BUTTON_OK);
		return getReportText();
	}
	
	private String getReportText(){
	// если это первый документ, который печатаем	
		if (ifFirstDocument) {
			log.info("Ожидание первой загрузки отчета");
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
}
