package ru.crystals.set10.utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.crystals.commons.amf.io.Utils;
import ru.crystals.discount.processing.entity.LoyTransactionEntity;
import ru.crystals.discounts.CashTransportBeanRemote;
import ru.crystals.httpclient.HttpClient;
import ru.crystals.httpclient.HttpFileConnection;
import ru.crystals.httpclient.HttpFileTransport;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.transport.TransferObject;

public class LoySender {
	
	private static final Logger log = LoggerFactory.getLogger(LoySender.class);
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	final String defaultExtension = ".ser";
    final String zipExtension = ".zip";
	
	private String serverIP;
	private int shopNumber;
	private int cashNumber;
//	private static int od_purchase_id;

	private CashTransportBeanRemote cashTransportManagerLoyal;
	
	private HttpFileTransport httpFileTransport = new HttpFileTransport();

	
	public LoySender(String serverIP, int shopNumber, int cashNumber) {
		this.serverIP = serverIP;
		this.shopNumber = shopNumber;
		this.cashNumber = cashNumber;
		
//		od_purchase_id = new DbAdapter().queryForInt(DbAdapter.DB_RETAIL_LOY,
//				"select max(id) from loy_inbound_files") + 7;
		
		httpFileTransport.setUrl("http://" + this.serverIP + ":8091");
		HttpClient client = new HttpClient();
        client.setUrl("http://" + serverIP + ":8090/SET-OperDay-Web/OperDayTransportServlet");
        cashTransportManagerLoyal = client.find(CashTransportBeanRemote.class, "java:app/SET-ProcessingDiscount/SET/ProcessingDiscounts/CashTransportBean!ru.crystals.discounts.CashTransportBeanRemote");
	}

	public void sendLoyTransaction(LoyTransactionEntity loyTransaction, PurchaseEntity purchase){
		Date now = new Date();
		String transactionName = "LoyTransaction_" + formatter.format(now) + "_" + (now.getTime() % 1000) + "_" + purchase.getShift().getShopIndex() + "_" + purchase.getShift().getCashNum();
        String serverFileName = purchase.getShift().getCashNum() + "/" + transactionName + defaultExtension;
        
		try {
	        TransferObject tObject = new TransferObject(Utils.serialize(loyTransaction), loyTransaction.getDataType());
	        
	        HttpFileConnection connect = getHttpFileOutputConnection("/loyaltransactions/" + serverFileName);
	        ObjectOutputStream oos = new ObjectOutputStream(connect.getOutputStream());
	        oos.writeObject(tObject);
	        oos.close();
	        
	        log.info("response message = " + connect.getResponseMessage());
	        
	        if (connect.getResponseMessage().equals("Created")){
            	log.info("Имя документа: " + serverFileName);
            	cashTransportManagerLoyal.registerLoyalTransactions(serverFileName, cashNumber, shopNumber, 1);
            	
//            	new DbAdapter().updateDb(DbAdapter.DB_RETAIL_LOY, 
//            			String.format("INSERT INTO loy_inbound_files( id, cash_number, shop_number, transactions_count, file_path, status) " +
//            				"VALUES (%s, %s, %s, 1, '%s', 0)", od_purchase_id++, cashNumber, shopNumber, serverFileName));
            	
            }
	        
		} catch (Exception e) {
			log.error("Ошибка отправки документа: ", e);
        	e.printStackTrace();
		}
	}
	
	private HttpFileConnection getHttpFileOutputConnection(String fileAddress) throws MalformedURLException, IOException {
	        return httpFileTransport.getServerOutput(fileAddress);
	}

}
