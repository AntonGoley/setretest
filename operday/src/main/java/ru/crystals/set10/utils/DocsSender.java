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
import ru.crystals.httpclient.HttpClient;
//import java.net.HttpURLConnection;
//import ru.crystals.httpclient.HttpClient;
//import ru.crystals.pos.check.DocumentEntity;
//import ru.crystals.pos.check.SentToServerStatus;
import ru.crystals.httpclient.HttpFileConnection;
import ru.crystals.httpclient.HttpFileTransport;
import ru.crystals.operday.transport.CashTransportBeanRemote;
import ru.crystals.transport.TransferObject;

public class DocsSender {
	
	private static final Logger log = LoggerFactory.getLogger(DocsSender.class);
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	private HttpFileTransport httpFileTransport = new HttpFileTransport();
	private String serverIP;
	private int shopNumber;
	private int cashNumber;
//	private static int od_purchase_id;
	CashTransportBeanRemote cashTransportManager;

	public DocsSender(String serverIP, int shopNumber, int cashNumber) {
		this.serverIP = serverIP;
		this.shopNumber = shopNumber;
		this.cashNumber = cashNumber;
//		od_purchase_id = new DbAdapter().queryForInt(DbAdapter.DB_RETAIL_OPERDAY,
//						"select max(id) from od_inbound_files") + 7;
		HttpClient client = new HttpClient();
        client.setUrl("http://" + serverIP + ":8090/SET-OperDay-Web/OperDayTransportServlet");
        cashTransportManager = client.find(CashTransportBeanRemote.class, "java:app/SET-OperDay/SET/OperDay/CashTransportBean!ru.crystals.operday.transport.CashTransportBeanRemote");
	}

	
	public  Object sendObject(int type, Serializable object) {
        
		 httpFileTransport.setUrl("http://" + serverIP + ":8091");

		
		log.info("Послать документ на ip: " + serverIP);
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
               
//               if (HttpURLConnection.HTTP_CREATED == connect.getResponseCode()) {
//                   //documentSetStatus((DocumentEntity) object, SentToServerStatus.WAIT_ACKNOWLEDGEMENT, docName);
//                   Long result = cashTransportManager.registerDocument(docName, shopNumber, cashNumber, 1);
//
//                   if (result != null && result > 0) {
//                       log.info("document {} has been registreated", docName);
//                       //documentSetStatus((DocumentEntity) object, SentToServerStatus.SENT, docName);
//                       return result;
//                   } else {
//                       log.error("document {} has NOT been registreated on server", docName);
//                       return null;
//                   }
//               }
               
                if (connect.getResponseMessage().equals("Created")){
                	log.info("Имя документа: " + docName);
                	cashTransportManager.registerDocument(docName , shopNumber, cashNumber, 1);
//                	new DbAdapter().updateDb(DbAdapter.DB_RETAIL_OPERDAY, 
//                			String.format("INSERT INTO od_inbound_files( id, cash_number, shop_number, documents_count, file_path, status) " +
//                				"VALUES (%s, %s, %s, 1, '%s', 0)", od_purchase_id++, cashNumber, shopNumber, docName));
                }
                
        } catch (Exception e) {
        	log.error("Ошибка отправки документа: ", e);
        	e.printStackTrace();
        }
        return null;
    }
	
	 public HttpFileConnection getHttpFileOutputConnection(String fileAddress) throws MalformedURLException, IOException {
	        return httpFileTransport.getServerOutput(fileAddress);
	 }

}
