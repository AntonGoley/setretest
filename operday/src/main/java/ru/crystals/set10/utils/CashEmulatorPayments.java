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
import ru.crystals.pos.payments.ChildrenCardPaymentEntity;
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
	 * Добавить банковскую транзакцию
	 */
	private List<PaymentTransactionEntity> addPaymentTransaction(PurchaseEntity purchase, AuthorizationData authData ){
		PaymentTransactionEntity bankTransaction = new  BankCardPaymentTransactionEntity(authData);
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
	 * Оплата банковской картой
	 */
	public PurchaseEntity setBankCardPayment(PurchaseEntity purchase, Long sum, BankCard card) {
		
		BankCardPaymentEntity bankCardPayment = new BankCardPaymentEntity();
		AuthorizationData authData = setAuthorizationData(sum, card);
		bankCardPayment.setDateCreate(new Date(System.currentTimeMillis()));
		bankCardPayment.setDateCommit(new Date(System.currentTimeMillis()));
		bankCardPayment.setPaymentType("BankCardPaymentEntity");
		bankCardPayment.setSumPay(sum);
		bankCardPayment.setCurrency("RUB");
		bankCardPayment.setCardNumber(card.getCardNumber());
		//bankCardPayment.setNumber(System.currentTimeMillis() + random(10000));
		bankCardPayment.setAuthCode(String.valueOf(random(1000) + 100L));
		bankCardPayment.setAuthorizationData(authData);
		purchase = addPayments(purchase, bankCardPayment);
		purchase.setTransactions(addPaymentTransaction(purchase, authData));
		return purchase;
	}
	
	/*
	 * Оплата детской картой
	 */
	public PurchaseEntity setChildCardPayment(PurchaseEntity purchase, Long sum, BankCard card) {
		ChildrenCardPaymentEntity childCardPayment = new ChildrenCardPaymentEntity();
		AuthorizationData authData = setAuthorizationData(sum, card);
		childCardPayment.setDateCreate(new Date(System.currentTimeMillis()));
		childCardPayment.setDateCommit(new Date(System.currentTimeMillis()));
		childCardPayment.setPaymentType("ChildrenCardPaymentEntity");
		childCardPayment.setSumPay(sum);
		childCardPayment.setCurrency("RUB");
		childCardPayment.setCardNumber(card.getCardNumber());
		//bankCardPayment.setNumber(System.currentTimeMillis() + random(10000));
		childCardPayment.setAuthCode(String.valueOf(random(1000) + 100L));
		childCardPayment.setAuthorizationData(authData);
		purchase = addPayments(purchase, childCardPayment);
		purchase.setTransactions(addPaymentTransaction(purchase, authData));
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
	private AuthorizationData setAuthorizationData(long sum, BankCard card){
		String validAuthorization = String.valueOf(System.currentTimeMillis());
		String validMessage = "ОДОБРЕНО";
		String validRsponseCode = "076";
		String terminalID = "MM489301";
		String bankID = "Сбербанк";
		
		AuthorizationData authData = new  AuthorizationData();
	 	authData.setAmount(Long.valueOf(sum));
	 	authData.setCurrencyCode("RUB");
	 	authData.setDate(new Date(System.currentTimeMillis() + 120*1000));
	 	authData.setHostTransId(System.currentTimeMillis());
	 	authData.setCashTransId(System.currentTimeMillis() + 1);
	 	
	 	authData.setCard(card);		
	 	authData.setStatus(true);
	 	authData.setBankid(bankID);
	 	authData.setAuthCode(validAuthorization);
	 	authData.setMessage(validMessage);
	 	authData.setResponseCode(validRsponseCode);
	 	authData.setResultCode(1L);
	 	authData.setTerminalId(terminalID);
	 	authData.setAuthCode("100500");
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

