package ru.crystals.set10.test.search;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.discount.processing.entity.LoyTransactionEntity;
import ru.crystals.operday.util.FormatHelper;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.set10.utils.CashEmulatorDiscounts;
import ru.crystals.set10.utils.CashEmulatorPayments;
import ru.crystals.set10.utils.GoodsParser;
import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;
import static ru.crystals.set10.pages.operday.searchcheck.SearchFormPopUp.FILTER_CATEGORY_CHECK_BAR_CODE;


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
	private int expectedCountSmaller = 0;
	
	CashEmulatorPayments payments = new CashEmulatorPayments();
	LoyTransactionEntity loyTransaction = new LoyTransactionEntity();
	CashEmulatorDiscounts discountEmulator = new CashEmulatorDiscounts();
	
	@BeforeClass
	public void send1stCheck(){
		/*
		 * Чек продажи с оплатой наличными
		 */
		searchCheck.openFilter();
		p1 = cashEmulatorSearchCheck.nextPurchaseWithoutSending();
		
		/*
		 * Чек продажи с оплатой наличными
		 * и оплатой банковской картой
		 */
		p2 = payments.getPurchaseWithoutPayments();
		p2 = payments.setBankCardPayment(BankCardPaymentEntity.class, p2, p2.getCheckSumEnd()/2, payments.generateCardData("VISA"), null);
		p2 = payments.setCashPayment(p2, p2.getCheckSumEnd() - p2.getCheckSumEnd()/2);
		
		/*
		 * Чек продажи с оплатой наличными (поиск суммы позиции)
		 */
		p3 = GoodsParser.generatePurchaseWithPositions(1);
		
		/*
		 * Чек продажи с транзакцией лояльности
		 */
		p4 =  cashEmulator.nextPurchaseWithoutSending();
		loyTransaction = discountEmulator.addDiscount(p4);
		// отправим транзакцию лояльности, а в тесте отправим чек
		cashEmulator.sendLoy(loyTransaction, p4);
		p4.setDiscountValueTotal(loyTransaction.getDiscountValueTotal());
		
		/*
		 * Чек продажи с транзакцией лояльности
		 */
//		p5 =  cashEmulator.nextPurchaseWithoutSending();
//		loyTransaction = discountEmulator.addDiscountForPosition(p5, 1, true);
//		// отправим транзакцию лояльности, а в тесте отправим чек
//		cashEmulator.sendLoy(loyTransaction, p5);
//		p5.setDiscountValueTotal(loyTransaction.getDiscountValueTotal());
		
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
//				{FILTER_CATEGORY_SUM_DISCOUNT_POSITION, p5.getDiscountValueTotal(), p5},
		};
	};
	
	@Test (description = "SRTE-131. Поиск чека на ТК по суммам", 
			dataProvider = "Суммы")
	public void testSearchCheckByType(String filter, long sum, PurchaseEntity p1){
		/*
		 * Определить, сколько зарегистрировано чеков, с суммой sum, до отправки чека p1 
		 */
 		expectedCountEquals = getResults(filter, FILTER_CATEGORY_SELECT_EQUALS, convertSum(sum));
 		expectedCountGreater = getResults(filter, FILTER_CATEGORY_SELECT_GREATER, convertSum(sum));
 		expectedCountSmaller = getResults(filter, FILTER_CATEGORY_SELECT_SMALLER, convertSum(sum));
 		
 		sendCheck(p1);
 		
 		/*
 		 * Проверить "="
 		 */
 		searchCheck.setFilterSelectSum(filter, FILTER_CATEGORY_SELECT_EQUALS, convertSum(sum));
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(expectedCountEquals + 1), expectedCountEquals + 1, 
 				"Чек не попал в результат поиска, если условие поиска = " + filter + " = " + convertSum(sum));
 		
 		/*
 		 *  Чек НЕ попадает в результат поиска, если условие поиска > сумма чека  
 		 */
 		searchCheck.setFilterSelectSum(filter, FILTER_CATEGORY_SELECT_GREATER, convertSum(sum));
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(expectedCountGreater), expectedCountGreater, 
 				"Чек не должен попадать результат поиска, если условие поиска " +  filter + " > " + convertSum(sum));
 		
 		/*
 		 *  Чек попадает в результат поиска, если условие поиска > (сумма - 100 копеек) 
 		 */
 		searchCheck.setFilterSelectSum(filter, FILTER_CATEGORY_SELECT_GREATER, convertSum(sum - 100));
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(expectedCountGreater + 1), expectedCountGreater + 1, 
 				"Чек НЕ попал в результат поиска, если условие поиска (сумма - 100копеек)" +  filter + " > " + convertSum(sum - 100));
 		
 		/*
 		 *  Чек попадает в результат поиска, если условие поиска < (сумма+ 100копеек) 
 		 */
 		searchCheck.setFilterSelectSum(filter, FILTER_CATEGORY_SELECT_SMALLER, convertSum(sum + 100));
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(expectedCountSmaller + 1), expectedCountSmaller + 1, 
 				"Чек НЕ попал в результат поиска, если условие поиска (сумма + 100копеек)"  + filter + " < " + convertSum(sum + 100));
 		
 		/*
 		 *  Чек НЕ попадает в результат поиска, если условие поиска < сумма чека  
 		 */
 		searchCheck.setFilterSelectSum(filter, FILTER_CATEGORY_SELECT_SMALLER, convertSum(sum));
 		searchCheck.doSearch();
 		Assert.assertEquals(searchCheck.getExpectedResultCount(expectedCountSmaller), expectedCountSmaller, 
 				"Чек не должен попадать в результат поиска, если условие поиска "  + filter + " < " + convertSum(sum));
 		
 		testExcelExport(LOCATOR_XLS_CHECK_CONTENT, XLS_REPORT_CONTENT_PATTERN);
 		testExcelExport(LOCATOR_XLS_CHECK_HEADERS, XLS_REPORT_HEADERS_PATTERN);
	}

	private int getResults(String filterName, String filterClause, String filterValue){
 		searchCheck.setFilterSelectSum(filterName, filterClause, filterValue);
 		searchCheck.doSearch();
 		return searchCheck.getSearchResultCount();
	}
	
	private String convertSum(long sum){
		return FormatHelper.formatCurrency(sum).replaceAll(" ", "");
	}
	
}
