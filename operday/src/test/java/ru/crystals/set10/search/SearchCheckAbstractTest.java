package ru.crystals.set10.search;

import org.testng.annotations.BeforeClass;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.searchcheck.PaymentTransactionsPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.AbstractTest;


public class SearchCheckAbstractTest extends AbstractTest{
	
	MainPage mainPage;
	CheckSearchPage searchCheck;
	ReportConfigPage RefundChecksConfigPage;
	PurchaseEntity pe;
	CheckContentPage checkContent;
	PaymentTransactionsPage paymentTransactions;
	
	static PurchaseEntity purchase;
	static long checkNumber = 0;
	static long shiftNumber = 0;
	static long shopNumber = 0;
	static long cashNumber = 0;
	static long checkBarcode = 0;
	static int searchResult = 0;
	static String barcode = ""; 
	
	@BeforeClass
	public void openSearchPage() {
		cashEmulatorSearchCheck.useNextShift();
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().openCheckSearch().openFilter();
	}	
	
	protected static void sendCheck(){
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextPurchase();
		setCheckData();
	}
	
	protected static void sendCheck(PurchaseEntity purchase){
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextPurchase(purchase);
	}
	
	protected static void sendRefundCheck(){
		PurchaseEntity superPurchase = new PurchaseEntity();
		superPurchase = purchase;
		// Сгенерим чек продажи
		purchase = (PurchaseEntity) cashEmulatorSearchCheck.nextRefundAll(superPurchase, false);
		setCheckData();
	}
	
	protected static void setCheckData(){
		checkNumber = purchase.getNumber();
		shiftNumber = purchase.getShift().getNumShift();
		shopNumber = purchase.getShift().getShopIndex();
		cashNumber = purchase.getShift().getCashNum();
		barcode = purchase.getPositions().get(0).getBarCode();
	}
}
