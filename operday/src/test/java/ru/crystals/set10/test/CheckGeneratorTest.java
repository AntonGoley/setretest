package ru.crystals.set10.test;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import ru.crystals.set10.config.*;
import ru.crystals.pos.bank.datastruct.AuthorizationData;
import ru.crystals.pos.bank.datastruct.BankCard;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.BankCardPaymentEntity;
import ru.crystals.pos.payments.ChildrenCardPaymentEntity;
import ru.crystals.set10.utils.CashEmulator;
import ru.crystals.set10.utils.CashEmulatorPayments;
import ru.crystals.set10.utils.DisinsectorTools;

public class CheckGeneratorTest {
	
	protected static final Logger log = Logger.getLogger(CheckGeneratorTest.class);
	CashEmulator cashEmulator;

	HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	CashEmulatorPayments payments = new CashEmulatorPayments();
	PurchaseEntity p1;
		
	@BeforeClass
	public void setupCash(){
		cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
		cashEmulator.nextIntroduction();
	}
	
	@AfterClass
	public void sendZreport(){
		cashEmulator.nextWithdrawal();
		cashEmulator.nextZReport();
	}
	
	@Test (description = "Сгенерить чеки продажи")
	public void testSendChecks(){

		// оплата банковской картой и аннулирование чека
		p1 = getBankCardPayment(BankCardPaymentEntity.class);
		cashEmulator.nextCancelledPurchase(p1);
		
		// оплата наличными
		p1 = (PurchaseEntity) cashEmulator.nextPurchase(getCashPayment());
		//возврат всего чека
		cashEmulator.nextRefundAll(p1, false);
		
		//оплата банковской картой
		p1 = (PurchaseEntity) cashEmulator.nextPurchase(getBankCardPayment(BankCardPaymentEntity.class));
		
		//возвращаем первую позицию в кол-ве 1шт
		HashMap<Long, Long> returnPositions = new HashMap<Long, Long>();
		returnPositions.put(1L, 100L);
		cashEmulator.nextRefundPositions(p1, returnPositions, false);
		
		//оплата детской картой
		p1 = (PurchaseEntity) cashEmulator.nextPurchase(getBankCardPayment(ChildrenCardPaymentEntity.class));
		
		//оплата бонусной картой
		p1 = (PurchaseEntity) cashEmulator.nextPurchase(getBonusCardPayment());
		
		//оплата подарочной картой
		p1 = (PurchaseEntity) cashEmulator.nextPurchase(getGiftCardPayment());
		
		//оплата скидочной картой
		p1 = (PurchaseEntity) cashEmulator.nextPurchase(getDiscountCardPayment());
		
	}
	
	
	private PurchaseEntity getBankCardPayment(Class<? extends BankCardPaymentEntity> cardType){
		log.info("Чек с оплатой банковской/детской картой..");
		PurchaseEntity p;
		p = payments.getPurchaseWithoutPayments();
		
		String prefix = String.valueOf(System.currentTimeMillis()).substring(5);
		DisinsectorTools.delay(99);
		String invalidCardPrefix = String.valueOf(System.currentTimeMillis()).substring(5);
		
		String validBankCardNumber = String.format("1234****%s", prefix);
		String invalidBankCardNumber = String.format("1234****%s", invalidCardPrefix);
		
		BankCard bankCard = payments.setBankCardData(validBankCardNumber, "VISA");
		BankCard invalidBankCard = payments.setBankCardData(invalidBankCardNumber, "Maestro");
		
		p = payments.setBankCardPayment(cardType, p, p.getCheckSumEnd()/4, invalidBankCard, getAuthDataWithFalse());
		p = payments.setBankCardPayment(cardType, p, p.getCheckSumEnd()/4, bankCard, null);
		p = payments.setBankCardPayment(cardType, p, p.getCheckSumEnd()/2, bankCard, null);
		
		p = payments.setCashPayment(p, p.getCheckSumEnd() - p.getCheckSumEnd()/4 -  p.getCheckSumEnd()/2);
		return p;
	}
	
	private PurchaseEntity getBonusCardPayment(){
		log.info("Чек с оплатой бонусной картой..");
		PurchaseEntity p;
		p = payments.getPurchaseWithoutPayments();

		String bonusCardNumber =String.valueOf(System.currentTimeMillis());
		p = payments.setBonusCardPayment(p, p.getCheckSumEnd()/2, String.valueOf(bonusCardNumber));
		p = payments.setCashPayment(p, p.getCheckSumEnd() - p.getCheckSumEnd()/2);
		return p;
		
	}
	
	private PurchaseEntity getGiftCardPayment(){
		log.info("Чек с оплатой подарочной картой..");
		PurchaseEntity p;
		p = payments.getPurchaseWithoutPayments();
		
		String giftCardNumber =String.valueOf(System.currentTimeMillis() + 99);
		p = payments.setGiftCardPayment(p, p.getCheckSumEnd()/2, giftCardNumber);
		p = payments.setCashPayment(p, p.getCheckSumEnd() - p.getCheckSumEnd()/2);
		return p;
	}
	
	private PurchaseEntity getCashPayment(){
		log.info("Чек с оплатой наличными..");
		PurchaseEntity p;
		p = payments.getPurchaseWithoutPayments();
		p = payments.setCashPayment(p, p.getCheckSumEnd());
		return p;
	}
	
	private PurchaseEntity getDiscountCardPayment(){
		log.info("Чек с оплатой дисконтной картой..");
		String discountCardNumber = String.valueOf(System.currentTimeMillis());
		PurchaseEntity p;
		p = payments.getPurchaseWithoutPayments();
		p = payments.setCashPayment(p, p.getCheckSumEnd());
		p = payments.setDiscountCard(p, discountCardNumber);
		return p;
	}
	
	
	@Test (enabled = false, description = "")
	public void testSendPartialReturnCheck(){
		cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
		
		log.info("Send checks to " + Config.SHOP_NUMBER);

		PurchaseEntity pe = (PurchaseEntity) cashEmulator.nextPurchase();
		
		for (int i=0; i<pe.getPositions().size(); i++){
			PositionEntity position = pe.getPositions().get(i);
			returnPositions.put(position.getNumber(), 1L * 1000);
		}
		cashEmulator.nextRefundPositions(pe, returnPositions, false);
		
		returnPositions = new HashMap<Long, Long>(); 
		for (int i=0; i<pe.getPositions().size(); i++){
			PositionEntity position = pe.getPositions().get(i);
			if (position.getQnty() > 1L * 1000) {
				returnPositions.put(position.getNumber(), position.getQnty() - 1L * 1000);
			}	
		}
		cashEmulator.nextRefundPositions(pe, returnPositions, false);
	}
	
	private AuthorizationData getAuthDataWithFalse(){
		AuthorizationData authData = new AuthorizationData();
		authData.setStatus(false);
		authData.setBankid("ВТБ");
		authData.setAuthCode(String.valueOf(System.currentTimeMillis()));
		authData.setMessage("ОТКЛОНЕНО");
		authData.setResponseCode("587");
		authData.setTerminalId("AA854380");
		authData.setResultCode(354L);
		return authData;
	}	
}
