package ru.crystals.set10.search;

import static ru.crystals.set10.pages.operday.searchcheck.CheckSearchPage.*;
import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ru.crystals.pos.bank.datastruct.BankCard;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.pos.payments.ChildrenCardPaymentEntity;
import ru.crystals.set10.utils.CashEmulatorPayments;


public class SearchCheckPaymentsCardTest extends SearchCheckAbstractTest{
	
	long checkNumber;
	PurchaseEntity purchase1;
	PurchaseEntity purchase2;
	PurchaseEntity purchase3;
	PurchaseEntity purchase4;
	String childCardNumber;
	String giftCardNumber;
	String bankCardNumber;
	String bonusCardNumber;
	
	@BeforeClass
	public void prepareData() {
		
		CashEmulatorPayments payments = new CashEmulatorPayments();
		
		/*
		 * Оплата банковской картой	
		 */
		// берем 8 символов из возвращаемых миллисекунд и подставляем в номер карты
		String prefix = String.valueOf(System.currentTimeMillis()).substring(5);
		bankCardNumber = String.format("1234****%s", prefix);
		purchase1 = payments.getPurchaseWithoutPayments();
		BankCard card = payments.setBankCardData(bankCardNumber, "Maestro");
		purchase1 = payments.setBankCardPayment(BankCardPaymentEntity.class, purchase1, purchase1.getCheckSumEnd(), card);
		
		/*
		 * Оплата бонусной картой	
		 */
		bonusCardNumber =String.valueOf(System.currentTimeMillis());
		purchase2 = payments.getPurchaseWithoutPayments();
		purchase2 = payments.setBonusCardPayment(purchase2, purchase2.getCheckSumEnd(), bonusCardNumber);

		/*
		 * Оплата подарочной картой	
		 */
		giftCardNumber =String.valueOf(System.currentTimeMillis() + 99);
		purchase3 = payments.getPurchaseWithoutPayments();
		long cashSum = purchase3.getCheckSumEnd() - purchase3.getCheckSumEnd()/2;
		purchase3 = payments.setCashPayment(purchase3, cashSum);
		purchase3 = payments.setGiftCardPayment(purchase3, purchase3.getCheckSumEnd() - cashSum, giftCardNumber);
		
		/*
		 * Оплата детской картой	
		 */
		String prefixChild = String.valueOf(System.currentTimeMillis()).substring(5);
		childCardNumber = String.format("5678****%s", prefixChild);
		purchase4 = payments.getPurchaseWithoutPayments();
		long cashSum4 = purchase4.getCheckSumEnd() - purchase4.getCheckSumEnd()/2;
		purchase4 = payments.setCashPayment(purchase4, cashSum4);
		BankCard childrenCard = payments.setBankCardData(childCardNumber, "VISA");
		purchase4 = payments.setBankCardPayment(ChildrenCardPaymentEntity.class, purchase4, purchase4.getCheckSumEnd() - cashSum4, childrenCard);

	}	
	
	@DataProvider (name = "Карты оплаты")
	public Object[][] setCardPaymentsData(){
		return new Object[][]{
				{FILTER_CATEGORY_BANK_CARD_NUMBER, bankCardNumber, purchase1},
				{FILTER_CATEGORY_BONUS_CARD_NUMBER, bonusCardNumber, purchase2},
				{FILTER_CATEGORY_GIFT_CARD_NUMBER, giftCardNumber, purchase3},
				{FILTER_CATEGORY_CHILD_CARD_NUMBER, childCardNumber, purchase4},
		};
	}
	
	@Test (description = "SRTE-73. Поиск чеков по карте оплаты (SRTE-74. SRTE-76)",
			dataProvider = "Карты оплаты")
	public void testSearchByPayCardNumber(String filter, String cardNumber, PurchaseEntity purchase){
 		/*
 		 *  поиск чека с номером карты, которого еще нет в системе
 		 */
		searchCheck.setFilterText(filter, String.valueOf(cardNumber)).doSearch();
 		searchResult = searchCheck.getSearchResultCount();
 		/*
 		 * Отправить чек purchase с оплатой по карте cardNumber
 		 */
 		sendCheck(purchase);
 		searchCheck.doSearch();
		Assert.assertEquals("", searchResult + 1, searchCheck.getSearchResultCount());
	}
}
