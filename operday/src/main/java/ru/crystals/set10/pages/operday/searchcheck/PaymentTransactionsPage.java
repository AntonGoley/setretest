package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.operday.OperDayPage;
import static ru.crystals.set10.utils.FlexMediator.*;
import ru.crystals.set10.pages.basic.*;

public class  PaymentTransactionsPage extends OperDayPage implements SaveFile{
	
	
	public static final String LINK_SAVE_EXCEL = "label=в excel";
	public static final String ID_OPERDAYSWF = "OperDay";
	
	public PaymentTransactionsPage(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public boolean validateData(String locatorText){
		return (boolean) waitForElementVisible(getDriver(), ID_OPERDAYSWF, locatorText);
		//return (boolean) waitForElementVisible(getDriver(), ID_OPERDAYSWF, String.format("text=%s", locatorText));
	}
	
	@Override
	public void saveFile(String fileType){
		clickElement(getDriver(), ID_OPERDAYSWF, fileType);
	}
}
