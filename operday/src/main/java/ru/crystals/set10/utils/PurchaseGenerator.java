package ru.crystals.set10.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ru.crystals.pos.catalog.ProductEntity;
import ru.crystals.pos.check.CheckStatus;
import ru.crystals.pos.check.DocumentEntity;
import ru.crystals.pos.check.InsertType;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import ru.crystals.pos.payments.PaymentEntity;

public class PurchaseGenerator {

	/** Список чеков с оплатой наличными*/
	public static List<DocumentEntity> peList = new ArrayList<DocumentEntity>();

	/** Список чеков БЕЗ оплат */
	public static List<DocumentEntity> peListWithoutPayments = new ArrayList<DocumentEntity>();
	
	
	static {
		for (int i=1; i<=100; i++) {
			int positions = (int)random(20) + 5;
			peList.add(generatePurchase(positions, true));
			positions = (int)random(20) + 5;
			peListWithoutPayments.add(generatePurchase(positions, false));
		}
	}
	
	/** Сгенерить чек с заданным числом позиций */
	public static PurchaseEntity generatePurchase(int positionsNumber, boolean generatePayment){
		PurchaseEntity pe = new PurchaseEntity();	
		pe.setCheckStatus(CheckStatus.Registered);
	    pe.setOperationType(Boolean.valueOf(true));
	    List<PositionEntity> positions = new ArrayList<PositionEntity>(positionsNumber);
	      long qnt = 0L;
	      long summ = 0L;
	      for (int i = 1; i <= positionsNumber; i++) {
	        PositionEntity pos = new PositionEntity();
	        ProductEntity product = GoodParser.catalogAllGoods.get((int)(Math.random() * GoodParser.catalogAllGoods.size()));
	        pos.setProduct(product);
	        pos.setNumber(Long.valueOf(i));

	        qnt = random(5) + 1L;
	        pos.setQnty(Long.valueOf(qnt * 1000L));
	        
	        long price = Long.valueOf(random(1000) * 17L);
	        pos.setPriceStart(price);
	        pos.setPriceEnd(price);
	        pos.setSum(Long.valueOf(qnt * pos.getPriceEnd().longValue()));
	        summ += pos.getSum().longValue();
	        pos.setNdsSum(Long.valueOf(Math.round(pos.getSum().longValue() * 0.2D)));
	        pos.setInsertType(InsertType.Hand);
	        pos.setCalculateDiscount(Boolean.valueOf(true));
	        pos.setSumDiscount(Long.valueOf(0L));
	        pos.setDeleted(Boolean.valueOf(false));
	        pos.setSuccessProcessed(true);
	        pos.setDateTime(new Date(System.currentTimeMillis()));
	        positions.add(pos);
	      }
	      pe.setFiscalDocNum("test;" + String.valueOf(System.currentTimeMillis()));
	      pe.setPositions(positions);
	      pe.setDiscountValueTotal(Long.valueOf(0L));
	      pe.setCheckSumEnd(Long.valueOf(summ));
	      pe.setCheckSumStart(Long.valueOf(summ));
	      
	      /** сгенерить оплату наличными */
	      if (generatePayment) {
		      List<PaymentEntity> paymentEntityList = new ArrayList<PaymentEntity>(1);
		      CashPaymentEntity payE = new CashPaymentEntity();
			      payE.setDateCreate(new Date(System.currentTimeMillis()));
			      payE.setDateCommit(new Date(System.currentTimeMillis()));
			      payE.setChange(Long.valueOf(random(1000) * 11L));
			      payE.setSumPay(summ + payE.getChange());
			      payE.setPaymentType("CashPaymentEntity");
			      payE.setCurrency("RUB");
		      
		      paymentEntityList.add(payE);
		      pe.setPayments(paymentEntityList);
	      }   
	      
	      return pe;
	}
	

	public PurchaseEntity generatePurchaseWithPositions(int positionsNumber){
		return generatePurchase(positionsNumber, true);
	}
	
	/*
	 * Чек без оплаты
	 */
	public static PurchaseEntity getPurchaseWithoutPayments(){
		PurchaseEntity result;
		int idx = (int)random(peListWithoutPayments.size() - 2) + 1;
		result = (PurchaseEntity) peListWithoutPayments.get(idx);
		/*
		 *  Удаление чека из списка
		 */
		peListWithoutPayments.remove(idx);
	    return result;
	}
	
	
	
	
	
	public static long random(int max) {
	    return Math.round(Math.random() * max);
	}
	
}
