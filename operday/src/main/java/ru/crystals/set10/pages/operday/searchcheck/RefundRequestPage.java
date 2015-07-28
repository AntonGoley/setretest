package ru.crystals.set10.pages.operday.searchcheck;


import org.openqa.selenium.*;
import ru.crystals.set10.pages.operday.OperDayPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  RefundRequestPage extends OperDayPage{
	
	public static final String BUTTON_PRINT_REQUEST = "label=Распечатать заявление";
	
	public RefundRequestPage(WebDriver driver) {
		super(driver);
	}
	
	public String printRefundRequest(){
		clickElement(getDriver(),ID_OPERDAYSWF, BUTTON_PRINT_REQUEST);
		return getReportText();
	}
	
}
