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
import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.LoginPage;
import ru.crystals.set10.pages.basic.MainPage;
import ru.crystals.set10.pages.operday.HTMLRepotResultPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckContentPage;
import ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage;
import ru.crystals.set10.pages.operday.tablereports.AbstractReportConfigPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.CheckGenerator;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.SoapRequestSender;
import static ru.crystals.set10.utils.DbAdapter.DB_RETAIL_SET;


public class AccompanyngDocumentsAbstractTest extends AbstractTest{
	
	MainPage mainPage;
	CheckSearchPage searchCheck;
	AbstractReportConfigPage RefundChecksConfigPage;
	HTMLRepotResultPage htmlReportResults;
	PurchaseEntity pe;
	CheckContentPage checkContent;
	CheckGenerator checkGenerator = new CheckGenerator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), 1);
	
	private static DbAdapter db = new  DbAdapter();
	
	/*
	 * Данные для заполнения контрагента
	 */
	static final String counterpartName = "Counterpart name";
	static final String counterpartInn = "100200300400";
	static final String counterpartKpp = "555666777";
	static final String counterpartAdress = "199123, Spb, Street 1-20";
	
	/*
	 * Данные товара, запрещенного для печати (deny_and_allow_print_goods.txt)
	 */
	static final String denyPrintMarkingOfTheGood = "019559_ST";
	static final String denyPrintFullName = "Вино Фронтера Шардоне геогр наим Долина Сентраль бел п/сух алк. 13% (Чили) 0.75L"; 
	static final String denyPrintSumTotalInWords = "Сто рублей 20 копеек";
	static final String denyPrintSumTotal = "Итого: 1,00 100,20";
	/*
	 * Данные товара, разрешенного для печати (good_deny_print.txt)
	 */
	static final String allowPrintMarkingOfTheGood = "83218";
	static final String allowPrintFullName = "Ананасы консервированные вес Кулинария (Тайланд)";
	static final String allowPrintSumTotalInWords = "Триста рублей 41 копейка";
	static final String allowPrintSumTotal = "Итого: 2,00 300,41";
	
	private static final String SQL_GOODS = 
			"SELECT  markingofthegood, barc.code as barcode, pr.name as name, fullname, lastimporttime, measure_code, vat " +
			"FROM  un_cg_product pr " +
			"JOIN " +
			"un_cg_barcode barc " +
			"on barc.product_marking = pr.markingofthegood " + 
			"where pr.markingofthegood in (%s)";
	
	
	public void navigateToCheckSearchPage() {
		pe = (PurchaseEntity) checkGenerator.nextPurchase(generatePredefinedCheck());
		mainPage = new LoginPage(getDriver(),  Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		searchCheck = mainPage.openOperDay().openCheckSearch();
		String checkNumber = String.valueOf(pe.getNumber());
 		searchCheck.setCheckNumber(checkNumber).doSearch();
 		checkContent = searchCheck.selectCheck(checkNumber);
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
		
	      List paymentEntityList = new ArrayList(1);
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
		result = CheckGenerator.parsePurchasesFromDB(
				db.queryForRowSet(DB_RETAIL_SET, String.format(SQL_GOODS, "'" + markingOfTheGood + "'")));

		if (result.size() == 0) {
			SoapRequestSender soapSender  = new SoapRequestSender();
			soapSender.sendGoodsToStartTesting(Config.RETAIL_HOST, "deny_and_allow_print_goods.txt");
			result = CheckGenerator.parsePurchasesFromDB(
					db.queryForRowSet(DB_RETAIL_SET, String.format(SQL_GOODS, "'" + markingOfTheGood + "'")));
		}
		return result.get(0);
	}
	
	
}
