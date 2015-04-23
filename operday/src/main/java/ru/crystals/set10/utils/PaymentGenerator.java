package ru.crystals.set10.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ru.crystals.cards.common.CardTypes;
import ru.crystals.pos.bank.datastruct.AuthorizationData;
import ru.crystals.pos.bank.datastruct.BankCard;
import ru.crystals.pos.check.PurchaseCardsEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.pos.payments.BankCardPaymentTransactionEntity;
import ru.crystals.pos.payments.BonusCardPaymentEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import ru.crystals.pos.payments.GiftCardPaymentEntity;
import ru.crystals.pos.payments.PaymentEntity;
import ru.crystals.pos.payments.PaymentTransactionEntity;

public class PaymentGenerator {
	
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
	private List<PaymentTransactionEntity> addPaymentTransaction(
			Class<? extends BankCardPaymentEntity> paymentType, 
			PurchaseEntity purchase, 
			AuthorizationData authData,
			PaymentEntity pe)
	{
		List<PaymentTransactionEntity> paymenTransactions = new ArrayList<PaymentTransactionEntity>();
		paymenTransactions.addAll(purchase.getTransactions());

		
		/*
		 * Создаем новую банковскую транзакцию и привязываем ее к оплате,
		 * если она валидная
		 */
		PaymentTransactionEntity bankTransaction;
		if (authData.isStatus()){
			bankTransaction = new  BankCardPaymentTransactionEntity(pe, authData);
			List<PaymentTransactionEntity> paymenTransactionsValid = new ArrayList<PaymentTransactionEntity>();
			paymenTransactionsValid.add(bankTransaction);
			pe.setTransactions(paymenTransactionsValid);
			
		} else {
			bankTransaction = new  BankCardPaymentTransactionEntity(authData);
		}
		bankTransaction.setDiscriminator(paymentType.getSimpleName());
		/*
		 * Привязываем транзакцию к оплате
		 */
		bankTransaction.setPurchase(purchase);
		
		paymenTransactions.add(bankTransaction);
		return paymenTransactions;
	}
	
	/*
	 * Добавить оплату наличными
	 */
	public PurchaseEntity setCashPayment(PurchaseEntity purchase, Long sum){
		CashPaymentEntity cashPayment = new CashPaymentEntity();
		cashPayment.setDateCreate(new Date(System.currentTimeMillis()));
		cashPayment.setDateCommit(new Date(System.currentTimeMillis()));
		//cashPayment.setChange(Long.valueOf(random(1000) * 11L));
		//cashPayment.setSumPay(sum + cashPayment.getChange());
		cashPayment.setSumPay(sum);
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
	public PurchaseEntity setBankCardPayment(
			Class<? extends BankCardPaymentEntity> cardType, 
			PurchaseEntity purchase, 
			Long sum, 
			BankCard card, 
			AuthorizationData basicAuthData) 
	{
		
		/*
		 * Задержка, на случай, если генерим несколько транзакций в чеке
		 * иначе, они могут быть сгенерены с одним временем
		 */
		DisinsectorTools.delay(99);
		BankCardPaymentEntity bankCardPayment = null;
		
		try {
			bankCardPayment = cardType.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		AuthorizationData authData = generateAuthorizationData(sum, card, basicAuthData);
		bankCardPayment.setDateCreate(new Date(System.currentTimeMillis()));
		bankCardPayment.setDateCommit(new Date(System.currentTimeMillis()));
		bankCardPayment.setPaymentType(cardType.getSimpleName());
		bankCardPayment.setSumPay(sum);
		bankCardPayment.setCurrency("RUB");
		bankCardPayment.setCardNumber(card.getCardNumber());
		bankCardPayment.setAuthCode(String.valueOf(random(1000) + 100L));
		purchase.setTransactions(addPaymentTransaction(cardType, purchase, authData, (PaymentEntity)bankCardPayment));
		if (authData.isStatus()){
			bankCardPayment.setAuthorizationData(authData);
			purchase = addPayments(purchase, bankCardPayment);
			
		}	
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
		giftCardPayment.setAmount(1000L);
		//giftCardPayment.setNumber(cardNumber);
		giftCardPayment.setCardNumber(cardNumber);
		giftCardPayment.setAmount(sum);
		addPayments(purchase, giftCardPayment);
		return purchase;
	}
	
	/*
	 * Скидочная внутренняя карта
	 */
	public PurchaseEntity setDiscountCard(PurchaseEntity purchase, String cardNumber){

		PurchaseCardsEntity card = new PurchaseCardsEntity();
		card.setNumber(cardNumber);
		card.setType(CardTypes.InternalCard);

		purchase.addCard(card);
		
		return purchase;
	}
	
	/*
	 *	Генерим новую банковскую карту c заданным номером и типом карты
	 */
	public BankCard setBankCardData(String numberMasked, String cardType){
		BankCard card = new BankCard();
			card.setCardNumber(numberMasked);
			card.setCardType(cardType);
			card.setExpiryDate(new Date(System.currentTimeMillis() + 365*24*3600*1000));
		return card;
	}
	
	/*
	 * Сгеренирить банковскую карту VISA
	 */
	public BankCard generateCardData(String cardType){
		String prefix = String.valueOf(System.currentTimeMillis()).substring(5);
		String cardNumber = String.format("1234****%s", prefix);
		return setBankCardData(cardNumber, cardType);
	}
	
	/*
	 * Данные авторизации
	 */
	private AuthorizationData generateAuthorizationData(long sum, BankCard card, AuthorizationData authBasicData){
		
		/*
		 * По умолчанию статус true 
		 */
		String authorizationCode = String.valueOf(System.currentTimeMillis());
		String message = "ОДОБРЕНО";
		String responseCode = "076";
		String terminalID = "MM489301";
		String bankID = "Сбербанк";
		long resultCode = 123L;
		boolean transactionStatus = true;
		
		AuthorizationData authData = new  AuthorizationData();
		
		if (authBasicData == null) {
			authData.setStatus(transactionStatus);
		 	authData.setBankid(bankID);
		 	authData.setAuthCode(authorizationCode);
		 	authData.setMessage(message);
		 	authData.setResponseCode(responseCode);
		 	authData.setResultCode(resultCode);
		 	authData.setTerminalId(terminalID);
		 	
		} else {
			authData = authBasicData;
		}
		
	 	authData.setAmount(Long.valueOf(sum));
	 	authData.setCurrencyCode("RUB");
	 	authData.setDate(new Date(System.currentTimeMillis() + 120*1000));
	 	authData.setHostTransId(System.currentTimeMillis());
	 	authData.setCashTransId(System.currentTimeMillis() + 1);
	 	authData.setCard(card);		
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

