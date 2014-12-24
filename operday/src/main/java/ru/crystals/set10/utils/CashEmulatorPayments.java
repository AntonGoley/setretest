package ru.crystals.set10.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.crystals.pos.bank.datastruct.AuthorizationData;
import ru.crystals.pos.bank.datastruct.BankCard;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.pos.payments.BankCardPaymentTransactionEntity;
import ru.crystals.pos.payments.BonusCardPaymentEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import ru.crystals.pos.payments.GiftCardPaymentEntity;
import ru.crystals.pos.payments.PaymentEntity;
import ru.crystals.pos.payments.PaymentTransactionEntity;
import static ru.crystals.set10.utils.GoodsParser.peListWithoutPayments;

public class CashEmulatorPayments {
	
	/*
	 * Чек без оплаты
	 */
	public PurchaseEntity getPurchaseWithoutPayments(){
		PurchaseEntity result;
		int idx = (int)random(peListWithoutPayments.size() - 2) + 1;
		result = (PurchaseEntity) peListWithoutPayments.get(idx);
		/*
		 *  Удаление чека из списка, для предотвращения добавления одному чеку нескольких оплат
		 */
		peListWithoutPayments.remove(idx);
	    return result;
	}
	
	/*
	 * Добавить оплату в чек к уже существующим оплатам
	 */
	private PurchaseEntity addPayments(PurchaseEntity purchase, PaymentEntity payment){
		List<PaymentEntity> payments = purchase.getPayments();
		payments.add(payment);
		purchase.setPayments(payments);
		return purchase;
	}

	/*
	 * Добавить банковскую транзакцию для детской или банковской карты
	 */
	private List<PaymentTransactionEntity> addPaymentTransaction(Class<? extends BankCardPaymentEntity> paymentType, PurchaseEntity purchase, AuthorizationData authData ){
		PaymentTransactionEntity bankTransaction = new  BankCardPaymentTransactionEntity(authData);
		bankTransaction.setDiscriminator(paymentType.getSimpleName());
		List<PaymentTransactionEntity> purchasePaymenTransactions = new ArrayList<PaymentTransactionEntity>();
		purchasePaymenTransactions.add(bankTransaction);
		return purchasePaymenTransactions;
	}
	
	/*
	 * Добавить оплату наличными
	 */
	public PurchaseEntity setCashPayment(PurchaseEntity purchase, Long sum){
		CashPaymentEntity cashPayment = new CashPaymentEntity();
		cashPayment.setDateCreate(new Date(System.currentTimeMillis()));
		cashPayment.setDateCommit(new Date(System.currentTimeMillis()));
		cashPayment.setChange(Long.valueOf(random(1000) * 11L));
		cashPayment.setSumPay(sum + cashPayment.getChange());
		cashPayment.setPaymentType("CashPaymentEntity");
		cashPayment.setCurrency("RUB");
	    return addPayments(purchase, cashPayment);
	}
	
	/*
	 * Оплата бонусной картой
	 */
	public PurchaseEntity setBonusCardPayment(PurchaseEntity purchase, Long sum, String cardNumber){
		BonusCardPaymentEntity bonusCardPayment = new BonusCardPaymentEntity();
		bonusCardPayment.setDateCreate(new Date(System.currentTimeMillis()));
		bonusCardPayment.setDateCommit(new Date(System.currentTimeMillis()));
		bonusCardPayment.setPaymentType("BonusCardPaymentEntity");
		bonusCardPayment.setSumPay(sum);
		bonusCardPayment.setCurrency("RUB");
		bonusCardPayment.setCardNumber(cardNumber);
		bonusCardPayment.setAuthCode(String.valueOf(random(1000) + 100L));
		addPayments(purchase, bonusCardPayment);
		return purchase;
	}
	
	/*
	 * Оплата банковской/детской картой
	 */
	public PurchaseEntity setBankCardPayment(Class<? extends BankCardPaymentEntity> cardType, PurchaseEntity purchase, Long sum, BankCard card) {
		
		BankCardPaymentEntity bankCardPayment = null;
		try {
			bankCardPayment = cardType.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		AuthorizationData authData = setAuthorizationData(sum, card);
		bankCardPayment.setDateCreate(new Date(System.currentTimeMillis()));
		bankCardPayment.setDateCommit(new Date(System.currentTimeMillis()));
		bankCardPayment.setPaymentType(cardType.getSimpleName());
		bankCardPayment.setSumPay(sum);
		bankCardPayment.setCurrency("RUB");
		bankCardPayment.setCardNumber(card.getCardNumber());
		bankCardPayment.setAuthCode(String.valueOf(random(1000) + 100L));
		bankCardPayment.setAuthorizationData(authData);
		purchase = addPayments(purchase, bankCardPayment);
		purchase.setTransactions(addPaymentTransaction(cardType, purchase, authData));
		return purchase;
	}
	
	/*
	 * Оплата подарочной картой
	 */
	public PurchaseEntity setGiftCardPayment(PurchaseEntity purchase, Long sum, String cardNumber){
		GiftCardPaymentEntity giftCardPayment = new GiftCardPaymentEntity();
		giftCardPayment.setDateCreate(new Date(System.currentTimeMillis()));
		giftCardPayment.setDateCommit(new Date(System.currentTimeMillis()));
		giftCardPayment.setPaymentType("GiftCardPaymentEntity");
		giftCardPayment.setSumPay(sum);
		giftCardPayment.setCurrency("RUB");
		//giftCardPayment.setNumber(cardNumber);
		giftCardPayment.setCardNumber(cardNumber);
		giftCardPayment.setAmount(sum);
		addPayments(purchase, giftCardPayment);
		return purchase;
	}
	
	/*
	 *	Генерим новую банковскую карту 
	 */
	public BankCard setBankCardData(String numberMasked, String cardType){
		BankCard card = new BankCard();
			card.setCardNumber(numberMasked);
			card.setCardType(cardType);
			card.setExpiryDate(new Date(System.currentTimeMillis() + 365*24*3600*1000));
		return card;
	}
	
	/*
	 * Успешная транзакция
	 */
	public AuthorizationData setAuthorizationData(long sum, BankCard card){
		String authorizationCode = String.valueOf(System.currentTimeMillis());
		String message = "ОДОБРЕНО";
		String responseCode = "076";
		String terminalID = "MM489301";
		String bankID = "Сбербанк";
		long resultCode = 123L;
		
		AuthorizationData authData = new  AuthorizationData();
	 	authData.setAmount(Long.valueOf(sum));
	 	authData.setCurrencyCode("RUB");
	 	authData.setDate(new Date(System.currentTimeMillis() + 120*1000));
	 	authData.setHostTransId(System.currentTimeMillis());
	 	authData.setCashTransId(System.currentTimeMillis() + 1);
	 	
	 	authData.setCard(card);		
	 	authData.setStatus(true);
	 	authData.setBankid(bankID);
	 	authData.setAuthCode(authorizationCode);
	 	authData.setMessage(message);
	 	authData.setResponseCode(responseCode);
	 	authData.setResultCode(resultCode);
	 	authData.setTerminalId(terminalID);
	 	List<List<String>> slips = new ArrayList<List<String>>();
	 		List<String> slip = new ArrayList<String>();
	 		slip.add("Простой слип \n транзакции");
	 		slips.add(slip);
	 	authData.setSlips(slips);
	 
	 return authData;
	}
	
	
	
	private static long random(int max) {
	    return Math.round(Math.random() * max);
	}
}

