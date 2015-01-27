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
		log.info("Выполнить изъятие..");
		cashEmulator.nextWithdrawal();
		log.info("Снять z отчет..");
		cashEmulator.nextZReport();
	}
	
	
	@Test (	description = "Сгенерить чеки продажи")
	public void testSendChecks(){
		cashEmulator.nextPurchase(getCashPayment());
		cashEmulator.nextPurchase(getBankCardPayment(BankCardPaymentEntity.class));
		cashEmulator.nextPurchase(getBankCardPayment(ChildrenCardPaymentEntity.class));
		cashEmulator.nextPurchase(getBonusCardPayment());
		cashEmulator.nextPurchase(getGiftCardPayment());
		cashEmulator.nextPurchase(getDiscountCardPayment());
		log.info("Выполнить возврать последнего чека");
		cashEmulator.nextRefundAll(p1, false);
	}
	
	
	private PurchaseEntity getBankCardPayment(Class<? extends BankCardPaymentEntity> cardType){
		log.info("Чек с оплатой банковской/детской картой..");
		p1 = payments.getPurchaseWithoutPayments();
		
		String prefix = String.valueOf(System.currentTimeMillis()).substring(5);
		DisinsectorTools.delay(99);
		String invalidCardPrefix = String.valueOf(System.currentTimeMillis()).substring(5);
		
		String validBankCardNumber = String.format("1234****%s", prefix);
		String invalidBankCardNumber = String.format("1234****%s", invalidCardPrefix);
		
		BankCard bankCard = payments.setBankCardData(validBankCardNumber, "VISA");
		BankCard invalidBankCard = payments.setBankCardData(invalidBankCardNumber, "Maestro");
		
		p1 = payments.setBankCardPayment(cardType, p1, p1.getCheckSumEnd()/2, invalidBankCard, getAuthDataWithFalse());
		p1 = payments.setBankCardPayment(cardType, p1, p1.getCheckSumEnd()/2, bankCard, null);
		p1 = payments.setCashPayment(p1, p1.getCheckSumEnd() - p1.getCheckSumEnd()/2);
		return p1;
		
	}
	
	private PurchaseEntity getBonusCardPayment(){
		log.info("Чек с оплатой бонусной картой..");
		p1 = payments.getPurchaseWithoutPayments();

		String bonusCardNumber =String.valueOf(System.currentTimeMillis());
		p1 = payments.setBonusCardPayment(p1, p1.getCheckSumEnd()/2, String.valueOf(bonusCardNumber));
		p1 = payments.setCashPayment(p1, p1.getCheckSumEnd() - p1.getCheckSumEnd()/2);
		return p1;
		
	}
	
	private PurchaseEntity getGiftCardPayment(){
		log.info("Чек с оплатой подарочной картой..");
		p1 = payments.getPurchaseWithoutPayments();
		
		String giftCardNumber =String.valueOf(System.currentTimeMillis() + 99);
		p1 = payments.setGiftCardPayment(p1, p1.getCheckSumEnd() - p1.getCheckSumEnd()/2, giftCardNumber);
		p1 = payments.setCashPayment(p1, p1.getCheckSumEnd() - p1.getCheckSumEnd()/2);
		return p1;
	}
	
	private PurchaseEntity getCashPayment(){
		log.info("Чек с оплатой наличными..");
		p1 = payments.getPurchaseWithoutPayments();
		p1 = payments.setCashPayment(p1, p1.getCheckSumEnd());
		return p1;
	}
	
	private PurchaseEntity getDiscountCardPayment(){
		log.info("Чек с оплатой дисконтной картой..");
		String discountCardNumber = String.valueOf(System.currentTimeMillis());
		p1 = payments.getPurchaseWithoutPayments();
		p1 = payments.setCashPayment(p1, p1.getCheckSumEnd());
		p1 = payments.setDiscountCard(p1, discountCardNumber);
		return p1;
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
		authData.setMessage("ОДОБРЕНО");
		authData.setResponseCode("587");
		authData.setTerminalId("AA854380");
		authData.setResultCode(354L);
		return authData;
	}	
}
