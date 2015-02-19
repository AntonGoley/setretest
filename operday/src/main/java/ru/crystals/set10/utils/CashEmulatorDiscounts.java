package ru.crystals.set10.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.crystals.discount.processing.entity.LoyAdvActionInPurchaseEntity;
import ru.crystals.discount.processing.entity.LoyDiscountPositionEntity;
import ru.crystals.discount.processing.entity.LoyTransactionEntity;
import ru.crystals.discounts.ActionType;
import ru.crystals.discounts.ApplyMode;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.check.SentToServerStatus;

public class CashEmulatorDiscounts {
	
	
	/*
	 * Расчитать скидки для определенного количество позиций начиная с первой
	 * @isDiscountPurchase = true, если скидка на позицию, false - скидка на чек
	 */
	public LoyTransactionEntity addDiscountForPosition(PurchaseEntity purchase, int discountFirstPositions, boolean isDiscountPurchase){
		return addDiscount(purchase, discountFirstPositions, isDiscountPurchase);
	}
	
	/*
	 * Расчитать скидки для половины позиций чека (скидку на чек)
	 */
	public LoyTransactionEntity addDiscount(PurchaseEntity purchase){
		return addDiscount(purchase, purchase.getPositions().size()/2, false);
	}
	
	private LoyTransactionEntity addDiscount(PurchaseEntity purchase, int discountFirstPositions, boolean isDiscountPurchase){
		
		LoyTransactionEntity loyTransaction = new LoyTransactionEntity();
		loyTransaction.setCashNumber(purchase.getShift().getCashNum());
		loyTransaction.setShopNumber(purchase.getShift().getShopIndex());
		loyTransaction.setShiftNumber(purchase.getShift().getNumShift());
		loyTransaction.setPurchaseNumber(purchase.getNumber());
		
		loyTransaction.setSaleTime(purchase.getDateCommit());
		loyTransaction.setOperationType(purchase.isSale());
		
		loyTransaction.setNeedSendAccumulation(true);
		loyTransaction.setNeedSendBonus(true);
		loyTransaction.setNeedSendToErp(true);
		/*
		 * 
		 */
		loyTransaction.setStatus(0);
		loyTransaction.setSentToServerStatus(SentToServerStatus.NO_SENT);
		loyTransaction.setTransactionTime(loyTransaction.getSaleTime());
		
		List<LoyDiscountPositionEntity> discounts = new ArrayList<LoyDiscountPositionEntity>();
		long sumDiscount = 0;
		for (int i=0; i<discountFirstPositions; i++) {
			LoyDiscountPositionEntity discountPosition = new LoyDiscountPositionEntity();
			PositionEntity firstPosition = purchase.getPositions().get(i);
			
			discountPosition.setPositionOrder(Integer.valueOf(String.valueOf(firstPosition.getNumber())));
			discountPosition.setGoodCode(firstPosition.getItem());
			discountPosition.setQnty(firstPosition.getQnty());
			discountPosition.setDiscountAmount(firstPosition.getPriceEnd()/10);
			discountPosition.setDiscountFullId("ADV_ACTION_FULL_ID");
			
			LoyAdvActionInPurchaseEntity loyAdvAction = new LoyAdvActionInPurchaseEntity();
			loyAdvAction.setActionName("Test adv name");
			loyAdvAction.setGuid(1000L);
			loyAdvAction.setApplyMode(ApplyMode.AUTOMATIC);
			loyAdvAction.setActionType(ActionType.BONUS_CFT);
			loyAdvAction.setDiscountType("Discount_type_" + String.valueOf(i));
			loyAdvAction.setExternalCode("C000_100_" + String.valueOf(i));
			
			discountPosition.setAdvAction(loyAdvAction);
			discounts.add(discountPosition);

			// скидка на чек, или на позиции
			discountPosition.setDiscountPurchase(isDiscountPurchase);
			
			sumDiscount = sumDiscount + firstPosition.getPriceEnd()/10;
			
			//purchase.getPositions().get(0).setSumDiscount(sumDiscount);
			
		}
		
		loyTransaction.setDiscountValueTotal(sumDiscount);
		loyTransaction.setDiscountPositions(discounts);

		
		return loyTransaction;
	}
	

}
