package ru.crystals.set10.test.search;


import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.crystals.discount.processing.entity.LoyTransactionEntity;
import ru.crystals.operday.util.FormatHelper;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.set10.utils.CashEmulatorDiscounts;
import ru.crystals.set10.utils.CashEmulatorPayments;
import ru.crystals.set10.utils.DisinsectorTools;
import ru.crystals.set10.utils.PurchaseGenerator;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;
import static ru.crystals.set10.pages.operday.searchcheck.SearchFormPopUp.FILTER_CATEGORY_CHECK_BAR_CODE;

@Test (groups={"centrum", "retail"})
public class SearchCheckBySumsTest extends SearchCheckAbstractTest{
	
	// чек для поиска по сумме чека
	private PurchaseEntity p1;
	// чек для поиска по сумме оплаты (банковской картой)
	private PurchaseEntity p2;
	// чек для поиска по сумме позиции
	private PurchaseEntity p3;
	// чек для поиска по сумме скидки на чек
	private PurchaseEntity p4;
	// чек для поиска по сумме скидки на позицию
	private PurchaseEntity p5;
	
	private int expectedCountEquals = 0;
	private int expectedCountGreater = 0;
	private int expectedCountGreater_100 = 0;
	private int expectedCountSmaller = 0;
	private int expectedCountSmaller_100 = 0;
	
	CashEmulatorPayments payments = new CashEmulatorPayments();
	LoyTransactionEntity loyTransaction = new LoyTransactionEntity();
	CashEmulatorDiscounts discountEmulator = new CashEmulatorDiscounts();
	
	long sumDiscount;
	
	@BeforeClass
	public void send1stCheck(){
		/*
		 * Чек продажи с оплатой наличными
		 * для поиска по сумме чека
		 */
		super.openSearchPage();
		searchCheck.openFilter();
		
		resetFiltersAndAdd2New();
		
		p1 = cashEmulatorSearchCheck.nextPurchaseWithoutSending();
		
		/*
		 * Чек продажи с оплатой наличными =  оплатой банковской картой
		 * для поиска чека по сумме оплаты. Суммы оплаты должны быть равны
		 */
		p2 = payments.getPurchaseWithoutPayments();
		p2 = payments.setBankCardPayment(BankCardPaymentEntity.class, p2, p2.getCheckSumEnd()/2, payments.generateCardData("VISA"), null);
		p2 = payments.setCashPayment(p2, p2.getCheckSumEnd()/2);
		
		/*
		 * Чек продажи с одной позицией для поиска по сумме позиции
		 */
		p3 = PurchaseGenerator.generatePurchaseWithPositions(1);
		
		/*
		 * Чек продажи с транзакцией лояльности,
		 * для поиска по сумме скидки на чек
		 */
		p4 =  cashEmulatorSearchCheck.nextPurchaseWithoutSending();
		loyTransaction = discountEmulator.addDiscount(p4);
		// отправим транзакцию лояльности, а в тесте отправим чек
		cashEmulatorSearchCheck.sendLoy(loyTransaction, p4);
		p4.setDiscountValueTotal(loyTransaction.getDiscountValueTotal());
		
		/*
		 * Чек продажи с транзакцией лояльности
		 */
//		p5 =  cashEmulatorSearchCheck.nextPurchaseWithoutSending();
//		loyTransaction = discountEmulator.addDiscountForPosition(p5, 1, true);
//		// отправим транзакцию лояльности, а в тесте отправим чек
//		cashEmulatorSearchCheck.sendLoy(loyTransaction, p5);
//		p5.setDiscountValueTotal(loyTransaction.getDiscountValueTotal());
		
		/*TODO:
		 * Разобраться и генерить через транзакцию лояльности
		 * 
		 * Чек продажи с 1 позицией со скидкой на эту позицию
		 * для поиска чека по скидке на позицию 
		 */
		sumDiscount = DisinsectorTools.random(10000) + 15023L;
		p5 = PurchaseGenerator.generatePurchaseWithPositions(1);
		PositionEntity position = p5.getPositions().get(0);
		position.setSumDiscount(sumDiscount);
		List<PositionEntity> positions = new ArrayList<PositionEntity>();
		positions.add(position);
		p5.setPositions(positions);
		
	}
	
	@BeforeMethod
	public void resetResults(){
		/*
		 * Сбрасываем результаты поиска
		 */
		searchCheck.setFilterText(FILTER_CATEGORY_CHECK_BAR_CODE, "000");
		searchCheck.doSearch();
		searchCheck.getExpectedResultCount(0);
	}
	
	@DataProvider(name = "Суммы")	
	public Object[][] paySum(){
		return new Object[][]{
				{FILTER_CATEGORY_SUM_CHECK, p1.getCheckSumEnd(), p1},
				{FILTER_CATEGORY_SUM_PAYMENT, p2.getCheckSumEnd()/2, p2},
				{FILTER_CATEGORY_SUM_POSITION, p3.getPositions().get(0).getSum(), p3},
				{FILTER_CATEGORY_SUM_DISCOUNT_CHECK, p4.getDiscountValueTotal(), p4},
				{FILTER_CATEGORY_SUM_DISCOUNT_POSITION, sumDiscount, p5},
		};
	};
	
	@Test (description = "SRTE-131. Поиск чека на ТК по суммам", 
			dataProvider = "Суммы" )
	public void testSearchCheckBySum(String filter, long sum, PurchaseEntity p){
		/*
		 * Определить, сколько зарегистрировано чеков, с суммой sum, до отправки чека p 
		 */
 		expectedCountEquals = getResults(filter, FILTER_CATEGORY_SELECT_EQUALS, convertSum(sum));
 		expectedCountGreater = getResults(filter, FILTER_CATEGORY_SELECT_GREATER, convertSum(sum));
 		expectedCountGreater_100 = getResults(filter, FILTER_CATEGORY_SELECT_GREATER, convertSum(sum - 100));
 		expectedCountSmaller = getResults(filter, FILTER_CATEGORY_SELECT_SMALLER, convertSum(sum));
 		expectedCountSmaller_100 = getResults(filter, FILTER_CATEGORY_SELECT_SMALLER, convertSum(sum + 100));
 		
 		sendCheck(p);
 		log.info("Проверка поиска по фильтру: " + filter);
 		/*
 		 *  Чек НЕ попадает в результат поиска, если условие поиска > сумма чека  
 		 */
 		verifyResult(filter, FILTER_CATEGORY_SELECT_GREATER, convertSum(sum), expectedCountGreater, 
 				"Чек не должен попадать результат поиска, если условие поиска ");

 		/*
 		 *  Чек попадает в результат поиска, если условие поиска > (сумма - 100 копеек) 
 		 */
 		
 		verifyResult(filter, FILTER_CATEGORY_SELECT_GREATER, convertSum(sum - 100), expectedCountGreater_100 + 1, 
 				"Чек НЕ попал в результат поиска, если условие поиска (сумма - 100копеек)");
 		
 		/*
 		 *  Чек НЕ попадает в результат поиска, если условие поиска < сумма чека  
 		 */
 		verifyResult(filter, FILTER_CATEGORY_SELECT_SMALLER, convertSum(sum), expectedCountSmaller, 
 				"Чек не должен попадать в результат поиска, если условие поиска ");
 		
 		/*
 		 *  Чек попадает в результат поиска, если условие поиска < (сумма+ 100копеек) 
 		 */
 		verifyResult(filter, FILTER_CATEGORY_SELECT_SMALLER, convertSum(sum + 100), expectedCountSmaller_100 + 1, 
 				"Чек НЕ попал в результат поиска, если условие поиска (сумма + 100копеек) ");
 		
 		/*
 		 * Проверить "="
 		 */
 		verifyResult(filter, FILTER_CATEGORY_SELECT_EQUALS, convertSum(sum), expectedCountEquals + 1, 
 				"Чек не попал в результат поиска, если условие поиска = ");
 		
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}
	
	private void verifyResult(String filter, String condition, String sumConverted, int expectedResult, String message){
		try{
			searchCheck.setFilterSelectSum(filter, condition, sumConverted);
	 		searchCheck.doSearch();
	 		Assert.assertEquals(searchCheck.getExpectedResultCount(expectedResult), expectedResult, 
	 				message + filter + " = " + sumConverted);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int getResults(String filterName, String filterClause, String filterValue){
 		int result = 0;
		searchCheck.setFilterSelectSum(filterName, filterClause, filterValue);
 		searchCheck.doSearch();
 		result = searchCheck.getSearchResultCount();
 		log.info("Найдено " + result + " чеков");
 		return result;
	}
	
	private String convertSum(long sum){
		return FormatHelper.formatCurrency(sum).replaceAll(" ", "");
	}
	
}
