package ru.crystals.set10.utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.crystals.commons.amf.io.Utils;
import ru.crystals.httpclient.HttpFileConnection;
import ru.crystals.httpclient.HttpFileTransport;
import ru.crystals.transport.TransferObject;

public class DocsSender {
	
	private static final Logger log = LoggerFactory.getLogger(DocsSender.class);
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	private HttpFileTransport httpFileTransport = new HttpFileTransport();
	private String serverIP;
	private int shopNumber;
	private int cashNumber;
	private static int od_purchase_id;
	

	public DocsSender(String serverIP, int shopNumber, int cashNumber) {
		this.serverIP = serverIP;
		this.shopNumber = shopNumber;
		this.cashNumber = cashNumber;
		od_purchase_id = new DbAdapter().queryForInt(DbAdapter.DB_RETAIL_OPERDAY,
						"select max(id) from od_inbound_files");
	}

	
	public  Object sendObject(int type, Serializable object) {
        
		 httpFileTransport.setUrl("http://" + serverIP + ":8091");

		
		log.info("Послать чек на ip: " + serverIP);
		try {
            TransferObject tObject;
                tObject = new TransferObject(Utils.serialize(object), type);
                tObject.load = true;
                Date date = new Date();
                char sep = '_';
                String docName = String.valueOf(cashNumber) + "/Document" + sep + formatter.format(date) + sep + (date.getTime() % 1000) + sep +
                		String.valueOf(shopNumber) + sep + String.valueOf(cashNumber) + ".ser";
                
                HttpFileConnection connect = getHttpFileOutputConnection("/documents/" + docName);
                ObjectOutputStream oos = new ObjectOutputStream(connect.getOutputStream());
                oos.writeObject(tObject);
                oos.close();

                log.info("response message = " + connect.getResponseMessage());
                // сетим в базу опердня (od_inbound_files) новый чек, который подложен в nginx для обработки
                if (connect.getResponseMessage().equals("Created")){
                	log.info("DocName: " + docName);
                	new DbAdapter().updateDb(DbAdapter.DB_RETAIL_OPERDAY, 
                			String.format("INSERT INTO od_inbound_files( id, cash_number, shop_number, documents_count, file_path, status) " +
                				"VALUES (%s, %s, %s, 1, '%s', 0)", od_purchase_id++ + 7, cashNumber, shopNumber, docName));
                }
                
        } catch (Exception e) {
        	log.error("Send document error: ", e);
        }
        return null;
    }
	
	 public HttpFileConnection getHttpFileOutputConnection(String fileAddress) throws MalformedURLException, IOException {
	        return httpFileTransport.getServerOutput(fileAddress);
	 }

}
