package ru.crystals.set10.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.crystals.pos.catalog.BarcodeEntity;
import ru.crystals.pos.catalog.MeasureEntity;
import ru.crystals.pos.catalog.ProductEntity;

public class GoodParser {
	
	private static final Logger log = LoggerFactory.getLogger(GoodParser.class);
	
	public static List<ProductEntity> catalogAllGoods = new ArrayList<ProductEntity>();
	public static List<ProductEntity> catalogWeightGoods = new ArrayList<ProductEntity>();
	public static List<ProductEntity> catalogSpiritsGoods = new ArrayList<ProductEntity>();
	public static List<ProductEntity> catalogCiggyGoods = new ArrayList<ProductEntity>();
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
	
	private static String weightEntity = "%ProductWeightEntityBillet";
	private static String spiritsEntity = "%ProductSpiritsEntityBillet";
	private static String ciggyEntity = "%ProductCiggyEntityBillet";
	
	
	private static DbAdapter db = new  DbAdapter();
	private static final String SQL_GOODS_COUNT = "select count(*) from un_cg_product";
	
	private static final String SQL_GOODS_ALL = 
			"SELECT  markingofthegood, barc.code as barcode, pr.name as name, lastimporttime, measure_code, vat, plugin_class_name, erpcode " +
			"FROM  un_cg_product pr " +
			"JOIN " +
			"un_cg_barcode barc " +
			"on barc.product_marking = pr.markingofthegood";
	
	private static final String SQL_GOODS = 
			"SELECT  markingofthegood, barc.code as barcode, pr.name as name, lastimporttime, measure_code, vat, plugin_class_name, erpcode " + 
			"FROM  un_cg_product pr " +
			"JOIN 		un_cg_barcode barc " +
			"on barc.product_marking = pr.markingofthegood " +
			"where pr.plugin_class_name like '%s'";

	
	public static void importGoods(String targetHost,String db_set){
		// проверить, есть ли товары в set, и если нет, импортировать через ERP импорт
		if ((db.queryForInt(db_set, SQL_GOODS_COUNT)) < 30 ) {
			SoapRequestSender soapSender  = new SoapRequestSender();
			soapSender.sendGoodsToStartTesting(targetHost, "goods.txt");
		}
		
		catalogAllGoods = parsePurchasesFromDB(db.queryForRowSet(db_set, SQL_GOODS_ALL));
		catalogWeightGoods = parsePurchasesFromDB(db.queryForRowSet(db_set, String.format(SQL_GOODS, weightEntity)) );
		catalogCiggyGoods = parsePurchasesFromDB(db.queryForRowSet(db_set, String.format(SQL_GOODS, ciggyEntity)) );
		catalogSpiritsGoods = parsePurchasesFromDB(db.queryForRowSet(db_set, String.format(SQL_GOODS, spiritsEntity)));
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
	        pe.setErpCode(goods.getString("erpcode"));
	        
	        /** В поле дискриминатор берем только название класса без "Billet" */
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
