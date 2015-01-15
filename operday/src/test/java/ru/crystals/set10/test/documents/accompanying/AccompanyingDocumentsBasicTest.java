package ru.crystals.set10.test.documents.accompanying;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.crystals.pos.catalog.ProductEntity;
import ru.crystals.pos.check.CheckStatus;
import ru.crystals.pos.check.InsertType;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import ru.crystals.pos.payments.PaymentEntity;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.tablereports.ReportConfigPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.utils.DbAdapter.DB_RETAIL_SET;
import ru.crystals.set10.utils.GoodsParser;

public class AccompanyingDocumentsBasicTest extends AbstractTest{
	
	MainPage mainPage;
	CheckSearchPage searchCheck;
	ReportConfigPage RefundChecksConfigPage;
	HTMLRepotResultPage htmlReportResults;
	CheckContentPage checkContent;
	
	protected static PurchaseEntity pe;
	/*
	 * Данные для заполнения контрагента
	 */
	static final String counterpartName = "Counterpart name";
	static final String counterpartInn = "100200300400";
	static final String counterpartKpp = "555666777";
	static final String counterpartAdress = "199123, Spb, Street 1-20";
	
	/*
	 * Данные юридического адреса магазина 
	 */
	static final String shopJuristicAdress = Config.SHOP_ADRESS;
	static final String shopJuristicName = Config.SHOP_NAME;
	static final String shopJuristicINN = Config.SHOP_INN;
	static final String shopJuristicKPP = Config.SHOP_KPP;
	
	/*
	 * Данные товара, запрещенного для печати (deny_and_allow_print_goods.txt)
	 */
	static final String denyPrintMarkingOfTheGood = "019559_ST";
	static final String denyPrintFullName = "Вино Фронтера Шардоне геогр наим Долина\nСентраль бел п/сух алк. 13% (Чили) 0.75L"; 
	static final String denyPrintName = "Вино Фронтера";
	static final String denyPrintSumTotalInWords = "Сто рублей 20 копеек";
	/*
	 * Данные товара, разрешенного для печати (good_deny_print.txt)
	 */
	static final String allowPrintMarkingOfTheGood = "83218";
	static final String allowPrintFullName = "Ананасы консервированные вес Кулинария (Тайланд)";
	static final String allowPrintName = "Ананасы консервированные вес";
	static final String allowPrintSumTotalInWords = "Триста рублей 41 копейка";
	
	/*
	 * Строка Итого в документах
	 */
	
	static final String denyPrintSumTotal = "Итого: 1,00 100,20";
	static final String allowPrintSumTotal = "Итого: 2,00 300,41";
	static final String denyPrintSumTotalInvoice = "Всего по накладной 1 X 80,16 X 20,04 100,20";
	static final String denyPrintSumTotalGoodsBill = "Всего к оплате 80,16 Х 20,04 100,20";
	
	private static final String SQL_GOODS = 
			"SELECT  markingofthegood, barc.code as barcode, pr.name as name, fullname, lastimporttime, measure_code, vat " +
			"FROM  un_cg_product pr " +
			"JOIN " +
			"un_cg_barcode barc " +
			"on barc.product_marking = pr.markingofthegood " + 
			"where pr.markingofthegood in (%s)";
	
	public void navigateToCheckSearchPage() {
		
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().openCheckSearch();
		
		/*
		 *  Сгенерим только один чек для всех тестов на сопроводительные документы
		 */
		if ( pe == null) {
			pe = (PurchaseEntity) cashEmulator.nextPurchase(generatePredefinedCheck());
		}
		
 		searchCheck.setCheckBarcode(pe).doSearch();
 		checkContent = searchCheck.selectFirstCheck();
	}	
	
	/*
	 * Сгенерировать чек с двумя позициями:
	 * 	1. Товар разрешенный к печати
	 * 	2. Товар запрещенный к печати
	 */
	
	private PurchaseEntity generatePredefinedCheck(){
		
		PurchaseEntity pe = new PurchaseEntity();
		ArrayList<PositionEntity> positions = new ArrayList<PositionEntity>();
		/*
		 * Заполним позицию с товаром, разрешенным к печати
		 */
		PositionEntity printPosition = new PositionEntity();
			printPosition.setNumber(Long.valueOf(1));
			ProductEntity printProduct = getPurchasePosition(allowPrintMarkingOfTheGood);
			
			printPosition.setProduct(printProduct);
			printPosition.setQnty(Long.valueOf(1 * 1000L));
			printPosition.setPriceEnd(Long.valueOf(10020));
			printPosition.setSum(Long.valueOf(10020));
			printPosition = completePositionEntity(printPosition);
			positions.add(printPosition);
		/*
		 * Заполним позицию с товаром, запрещенным к печати
		 */	
		PositionEntity denyPrintPosition = new PositionEntity();
			denyPrintPosition.setNumber(Long.valueOf(2));
			ProductEntity denyPrintProduct = getPurchasePosition(denyPrintMarkingOfTheGood);
			
			denyPrintPosition.setProduct(denyPrintProduct);
			denyPrintPosition.setQnty(Long.valueOf(1 * 1000L));
			denyPrintPosition.setPriceEnd(Long.valueOf(20021));
			denyPrintPosition.setSum(Long.valueOf(20021));
			denyPrintPosition = completePositionEntity(denyPrintPosition);
			positions.add(denyPrintPosition);
		
        pe.setFiscalDocNum("test;" + String.valueOf(System.currentTimeMillis()));
		pe.setCheckStatus(CheckStatus.Registered);
	    pe.setOperationType(Boolean.valueOf(true));
		pe.setPositions(positions);
		
	      List<PaymentEntity> paymentEntityList = new ArrayList<PaymentEntity>(1);
	      CashPaymentEntity payE = new CashPaymentEntity();
	      payE.setDateCreate(new Date(System.currentTimeMillis()));
	      payE.setDateCommit(new Date(System.currentTimeMillis()));
	      payE.setSumPay(Long.valueOf(30042));
	      //payE.setChange(Long.valueOf(10000L));
	      payE.setPaymentType("CashPaymentEntity");
	      payE.setCurrency("RUB");
	      paymentEntityList.add(payE);
	      pe.setPayments(paymentEntityList);
	      pe.setDiscountValueTotal(Long.valueOf(0L));
	      pe.setCheckSumEnd(Long.valueOf(30042));
	      pe.setCheckSumStart(Long.valueOf(30042));
	    
	      return(pe);
	}
	
	private PositionEntity completePositionEntity(PositionEntity pos){
		pos.setNdsSum(Long.valueOf(Math.round(pos.getSum().longValue() * 0.2D)));
		pos.setInsertType(InsertType.Hand);
		pos.setCalculateDiscount(Boolean.valueOf(true));
		pos.setSumDiscount(Long.valueOf(0L));
		pos.setDeleted(Boolean.valueOf(false));
		pos.setSuccessProcessed(true);
		pos.setDateTime(new Date(System.currentTimeMillis()));
	return pos;
	}
	
	private static ProductEntity getPurchasePosition(String markingOfTheGood){
		ArrayList<ProductEntity> result = new ArrayList<ProductEntity>();
		result = GoodsParser.parsePurchasesFromDB(
				dbAdapter.queryForRowSet(DB_RETAIL_SET, String.format(SQL_GOODS, "'" + markingOfTheGood + "'")));

		if (result.size() == 0) {
			SoapRequestSender soapSender  = new SoapRequestSender();
			soapSender.sendGoodsToStartTesting(Config.RETAIL_HOST, "deny_and_allow_print_goods.txt");
			result = GoodsParser.parsePurchasesFromDB(
					dbAdapter.queryForRowSet(DB_RETAIL_SET, String.format(SQL_GOODS, "'" + markingOfTheGood + "'")));
		}
		return result.get(0);
	}
	
	
}
