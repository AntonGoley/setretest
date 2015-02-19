package ru.crystals.set10.utils;

import static ru.crystals.set10.utils.DbAdapter.DB_RETAIL_SET;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.crystals.pos.catalog.BarcodeEntity;
import ru.crystals.pos.catalog.MeasureEntity;
import ru.crystals.pos.catalog.ProductEntity;
import ru.crystals.pos.check.CheckStatus;
import ru.crystals.pos.check.DocumentEntity;
import ru.crystals.pos.check.InsertType;
import ru.crystals.pos.check.PositionEntity;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.pos.payments.CashPaymentEntity;
import ru.crystals.pos.payments.PaymentEntity;
import ru.crystals.set10.config.Config;

public class GoodsParser {
	
	private static final Logger log = LoggerFactory.getLogger(GoodsParser.class);
	
	public static List<ProductEntity> catalogGoods = new ArrayList<ProductEntity>();
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
	/*
	 * Список чеков с оплатой наличными
	 */
	public static List<DocumentEntity> peList = new ArrayList<DocumentEntity>();
	
	/*
	 * Список чеков БЕЗ оплат
	 */
	public static List<DocumentEntity> peListWithoutPayments = new ArrayList<DocumentEntity>();
	
	
	private static DbAdapter db = new  DbAdapter();
	private static final String SQL_GOODS_COUNT = "select count(*) from un_cg_product";
	private static final String SQL_GOODS = 
			"SELECT  markingofthegood, barc.code as barcode, pr.name as name, lastimporttime, measure_code, vat, plugin_class_name " +
			"FROM  un_cg_product pr " +
			"JOIN " +
			"un_cg_barcode barc " +
			"on barc.product_marking = pr.markingofthegood";
	
	static
	  {
		// проверить, есть ли товары в set_operday, и если нет, импортировать через ERP импорт
		if ((db.queryForInt(DB_RETAIL_SET, SQL_GOODS_COUNT)) < 30 ) {
			SoapRequestSender soapSender  = new SoapRequestSender();
			soapSender.sendGoodsToStartTesting(Config.RETAIL_HOST, "goods.txt");
		}
		catalogGoods = parsePurchasesFromDB(db.queryForRowSet(DB_RETAIL_SET, SQL_GOODS));
		peList.addAll(generateChecks(true));
		peListWithoutPayments.addAll(generateChecks(false));
	  }
	
	private static List<DocumentEntity> generateChecks(boolean generatePayments) {

		List<DocumentEntity> list = new ArrayList<DocumentEntity>();
	    while (list.size() < 100) {
	      PurchaseEntity pe = new PurchaseEntity();	
	      
	      pe.setCheckStatus(CheckStatus.Registered);
	      pe.setOperationType(Boolean.valueOf(true));
	      List<PositionEntity> positions = new ArrayList<PositionEntity>(100);
	      // в чеке не меньше 5 товаров
	      int end = (int)random(20) + 5;
	      long qnt = 0L;
	      long summ = 0L;
	      for (int i = 1; i < end; i++) {
	        PositionEntity pos = new PositionEntity();
	        ProductEntity product = catalogGoods.get((int)(Math.random() * catalogGoods.size() - 1.0D));
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
	      
	      /*
	       * Добавить оплаты, если необходимо
	       */
	      if (generatePayments) {
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
	      list.add(pe);
	    }
	    return list;
	}
	
	/*TODO: вынести метод формирование чека в отдельный метод
	 * Сгенерить чек с заданным числом позиций с оплатой наличными
	 */
	public static PurchaseEntity generatePurchaseWithPositions(int positionsNumber){
		PurchaseEntity pe = new PurchaseEntity();	
		pe.setCheckStatus(CheckStatus.Registered);
	    pe.setOperationType(Boolean.valueOf(true));
	    List<PositionEntity> positions = new ArrayList<PositionEntity>(positionsNumber);
	      long qnt = 0L;
	      long summ = 0L;
	      for (int i = 1; i <= positionsNumber; i++) {
	        PositionEntity pos = new PositionEntity();
	        ProductEntity product = catalogGoods.get((int)(Math.random() * catalogGoods.size() - 1.0D));
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
	      return pe;
	}
	
	public static ArrayList<ProductEntity> parsePurchasesFromDB(SqlRowSet goods) {
		ArrayList<ProductEntity> result = new ArrayList<ProductEntity>();
		String discriminator[];
		try {
	      while (goods.next()) {
	        ProductEntity pe = new ProductEntity();
	        pe.setItem(goods.getString("markingofthegood"));
	        pe.setLastImportTime(sdf.parse(goods.getString("lastimporttime").substring(1, goods.getString("lastimporttime").length() - 1)));
	        MeasureEntity me = new MeasureEntity();
	        me.setCode(goods.getString("measure_code"));
	        pe.setMeasure(me);
	        pe.setName(goods.getString("name"));
	        pe.setNds(Float.valueOf(18.0F));
	        pe.setNdsClass("NDS");
	        // В поле дискриминатор берем только название класса без "Billet"
	        discriminator = goods.getString("plugin_class_name").split("\\.");
	        pe.setDiscriminator(discriminator[discriminator.length - 1].replace("Billet", ""));
	        
	        BarcodeEntity be = new BarcodeEntity();
	        be.setBarCode(goods.getString("barcode"));
	        pe.setBarCode(be);
	        result.add(pe);
	      }
	    } catch (Exception e) {
	      log.warn("Error: " + e.getMessage());
	    }
		return result;
	}	
	
	public static long random(int max) {
	    return Math.round(Math.random() * max);
	}
	
}
