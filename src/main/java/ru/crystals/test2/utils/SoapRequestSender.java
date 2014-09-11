package ru.crystals.test2.utils;
import org.apache.commons.codec.binary.Base64;
import javax.xml.bind.DatatypeConverter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.crystals.test2.config.Config;


public class SoapRequestSender {
	protected static final Logger log = Logger.getLogger(SoapRequestSender.class);
	
	private static final String METHOD_GOODS_WITHTI = "#getGoodsCatalogWithTi";
	private static final String METHOD_ACTIONS_WITHTI = "#importActionsWithTi";
	private static final String METHOD_ALCO_RESTRICTIONS = "#getSpiritRestrictions";
	
	String soapServiceIP = ""; 
	String soapRequest = "";
	String service = "";
	String method = "";
	String response = "";
	
	private static  String soapRequestGoods = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:plug=\"http://plugins.products.ERPIntegration.crystals.ru/\"> " +
			"<soapenv:Header/> <soapenv:Body>" + 
			"<plug:getGoodsCatalogWithTi> " +
			"<goodsCatalogXML>%s</goodsCatalogXML>" +
			"<TI>%s</TI>" +
			"</plug:getGoodsCatalogWithTi> </soapenv:Body> </soapenv:Envelope>";
	
	private static String soapRequestAdversting = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.discounts.ERPIntegration.crystals.ru/\">" +
	   "<soapenv:Header/>" +
	   "<soapenv:Body>" +
	      "<ws:importActionsWithTi>" +
	         "<!--Optional:-->" +
	         "<xmlData>%s</xmlData>" +
	         "<!--Optional:-->" +
	         "<TI>%s</TI>" +
	      "</ws:importActionsWithTi>" +
	   "</soapenv:Body>" +
	"</soapenv:Envelope>";
	
	private static String soapGetFeedBack = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:feed=\"http://feedback.ERPIntegration.crystals.ru/\">" +
	   "<soapenv:Header/>" +
	   "<soapenv:Body>" +
	      "<feed:getPackageStatus>" +
	         "<xmlGetstatus>" +
	           "<import ti=\"%s\"/>" +
	         "</xmlGetstatus>" +
	      "</feed:getPackageStatus>" +
	   "</soapenv:Body>" +
	"</soapenv:Envelope>";
	
	private static String soapGetAlcoRestrictions = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://erpiservice.alcohol.crystals.ru/\">" +
		"<soapenv:Header/>" +
		"<soapenv:Body>" +
		"<erp:getSpiritRestrictions/>" +
		"</soapenv:Body>" +
		"</soapenv:Envelope>";
	
	public void setSoapServiceIP(String ip){
		this.soapServiceIP = ip;
	}
	
	public String encodeBase64(String stringToEncode){
		Base64 codec = new Base64();
		return new String(codec.encode(stringToEncode.getBytes()));
	}
	
	public String generateTI(){
		String erpCode;
		// get last numbers of date value
		erpCode =  String.valueOf((new Date().getTime())).substring(6, 13);
		log.info("TI = " + erpCode);
		return erpCode;
	}
	
	public void getAlcoRestrictions(){
		this.soapRequest = soapGetAlcoRestrictions;
		this.service = Config.ALCO_RESTRICTIONS; 
		this.method = METHOD_ALCO_RESTRICTIONS;
		sendSOAPRequest();
	}
	
	
	/*
	 * 
	 * goodRequest = DisinsectorTools.getFileContentAsString("good.txt");
		soapSender.sendGoods(String.format(goodRequest, erpCode, barCode),ti);
		soapSender.assertSOAPResponse("status-message=\"correct\"", ti);
	 * 
	 * 
	 * 
	 * 
	 */
	
	public void sendGoods(String request, String ti){
		this.soapRequest = String.format(soapRequestGoods, encodeBase64(request), ti);
		this.service = Config.ERP_INTEGRATION_GOOSERVICE; 
		this.method = METHOD_GOODS_WITHTI;
		sendSOAPRequest();
	}
	
	public void sendAdversting(String request, String ti){
		this.soapRequest = String.format(soapRequestAdversting, encodeBase64(request), ti);
		this.service = Config.ERP_INTEGRATION_ADVERTSING_ACTIONS; 
		this.method = METHOD_ACTIONS_WITHTI;
		sendSOAPRequest();
	}
	
	public void getFeedBack(String ti){
		this.soapRequest = String.format(soapGetFeedBack, ti);
		this.service = Config.ERP_INTEGRATION_FEDDBACK; 
		this.method = METHOD_ACTIONS_WITHTI;
		sendSOAPRequest();
	}
	
	
	public void sendSOAPRequest(){
		
		URL resourceURL;
		HttpURLConnection con = null;
		String result = "";
		String serviceUrl ="http://" + this.soapServiceIP+ ":" + Config.DEFAULT_PORT; 
		
		log.info("Send SOAP request: " + this.soapRequest);

		try {
			resourceURL = new URL(serviceUrl + this.service);
			con = (HttpURLConnection) resourceURL.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
			   con.setRequestProperty("SOAPAction", 
					   serviceUrl + this.service + this.method);
			   
		   OutputStream reqStream = con.getOutputStream();
		   reqStream.write(this.soapRequest.getBytes()); 
		   reqStream.close();
		   
		   InputStreamReader inR = new InputStreamReader(con.getInputStream());
		   BufferedReader bufReader = new BufferedReader(inR);
		   
		   String res = "";
		   while ((res = bufReader.readLine()) != null){
			   result +=res; 
		   }

		   bufReader.close();
		   inR.close();
		   con.disconnect();

		} catch (IOException e) {
			try {
				int respCode = con.getResponseCode();
				InputStreamReader inR = new InputStreamReader(con.getErrorStream());
				BufferedReader bufReader = new BufferedReader(inR);
				 String res = "";
				   while ((res = bufReader.readLine()) != null){
					   result +=res; 
				   }
				   result = "Error response " + respCode +  ": "+ result;
				   bufReader.close();
				   inR.close();
				   con.disconnect();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
				
		}
		this.response = result;
		log.info("SOAP RESPONSE: " + this.response);
	}
	
	
	public String assertSOAPResponseXpath(String xpathExpression)  {
		String result = null;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
		    builder = builderFactory.newDocumentBuilder();
		    Document xmlDocument = builder.parse(new ByteArrayInputStream(this.response.getBytes()));
		    
		    XPath xPath =  XPathFactory.newInstance().newXPath();
	    	// получаем Base64 response
		    result = xPath.compile("//return").evaluate(xmlDocument);
	    	// декодируем
		    byte[] decoded = DatatypeConverter.parseBase64Binary(result);
		    xmlDocument = builder.parse(new ByteArrayInputStream(decoded));
		    // и проверяем
		    Object xpathResult = xPath.compile(xpathExpression).evaluate(xmlDocument, XPathConstants.BOOLEAN);
		    NodeList nodes = (NodeList) xpathResult;
	        log.info("Have I found anything? " + (nodes.getLength() > 0 ? "Yes": "No"));
		    
	        for (int i = 0; i < nodes.getLength(); i++) {
	            System.out.println("nodes: "+ nodes.item(i).getNodeValue()); 
	        }
		    
		
		} catch (ParserConfigurationException e) {
		    e.printStackTrace();  
		} catch (SAXException saxEx) {
			saxEx.printStackTrace();
		} catch ( IOException ioEx) {
			ioEx.printStackTrace();
		} catch (XPathExpressionException e) {
			 e.printStackTrace();  
		}
		return result;
	}
	
	public boolean assertSOAPResponse(String expectedResult, String ti){
		int timeout = 0;
		getFeedBack(ti);
		try {
		while (timeout <=20) {
			if (this.response.contains(expectedResult)) return true;
				Thread.sleep(1000);
				timeout +=1;
				sendSOAPRequest();
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
