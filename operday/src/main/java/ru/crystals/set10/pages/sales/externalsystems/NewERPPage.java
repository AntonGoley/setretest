package ru.crystals.set10.pages.sales.externalsystems;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;

public class NewERPPage extends SalesPage {
	
	static final String BUTTON_BACK = "id:buttonBack";

	
	public NewERPPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public NewERPPage addERP(String processingName){

		return this;
	}
}
