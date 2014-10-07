package ru.crystals.set10.test.km;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.crystals.pos.catalog.ProductEntity;
import ru.crystals.pos.check.CheckStatus;
import ru.crystals.pos.check.InsertType;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.CashEmulator;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.utils.DbAdapter.DB_RETAIL_SET;


public class KM3Test extends AbstractTest{
	
	MainPage mainPage;
	CheckSearchPage searchCheck;
	ReportConfigPage RefundChecksConfigPage;
	HTMLRepotResultPage htmlReportResults;
	PurchaseEntity pe;
	CheckContentPage checkContent;

	private static String predefindCheckNumber = "0";
	
	private static DbAdapter db = new  DbAdapter();
	
	
	
	
	
}
