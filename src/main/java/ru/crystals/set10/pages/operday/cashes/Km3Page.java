package ru.crystals.set10.pages.operday.cashes;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.operday.OperDayPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  Km3Page extends OperDayPage{
	
	private final String LOCATOR_KM3 = "1";
	private final String LOCATOR_KM6 = "0";
	private final String NAVIGATOR_KM_TYTE = "tabNav";
	
	public Km3Page(WebDriver driver) {
		super(driver, false);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	
	public void switchToKm(String kmType){
		doFlexProperty(getDriver(), ID_OPERDAYSWF, NAVIGATOR_KM_TYTE, new String[]{"selectedIndex", kmType});
	}
	
	
	
}
