package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.OperDayPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  CheckContentPage extends OperDayPage{
	
	public static final String LINK_NOMENCLATURE = "label=номенклатуру чека";
	public static final String LINK_GOODS_CHECK = "label=товарный чек";
	public static final String LINK_INVOICE = "label=товарную накладную";
	
	
	
	public CheckContentPage(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public void generateReport(String docType){
		// doFlexMouseDown чтобы убрать flexSuggest
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, docType);
		clickElement(getDriver(), ID_OPERDAYSWF, docType);
		getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//embed")));
		switchWindow(false);

	}
	
	
}
