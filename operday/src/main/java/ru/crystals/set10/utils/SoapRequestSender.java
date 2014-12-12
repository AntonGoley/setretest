package ru.crystals.set10.utils;
import org.apache.commons.codec.binary.Base64;
import javax.xml.bind.DatatypeConverter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.crystals.set10.config.Config;


public class SoapRequestSender {
	protected static final Logger log = Logger.getLogger(SoapRequestSender.class);
	
    public static final String ERP_INTEGRATION_GOOSERVICE = "/SET-ERPIntegration/SET/WSGoodsCatalogImport";
    public static final String ERP_INTEGRATION_ADVERTSING_ACTIONS = "/SET-ERPIntegration/AdvertisingActionsImport";
    public static final String ERP_INTEGRATION_FEDDBACK = "/SET-ERPIntegration/SET/FeedbackWS";
    public static final String SERVICE_ALCO_RESTRICTIONS = "/SET-Alcohol/SET/SpiritRestrictionsExportWS";
    public static final String SERVICE_PRICE_CHECKER = "/SET-Products/SET/Products";
	
	
	private static final String METHOD_GOODS_WITHTI = "#getGoodsCatalogWithTi";
	private static final String METHOD_ACTIONS_WITHTI = "#importActionsWithTi";
	private static final String METHOD_ALCO_RESTRICTIONS = "#getSpiritRestrictions";
	private static final String METHOD_PRICECHECKER_SHUTTLE = "#getProductInfoForShuttle";
	
	public static final String RETURN_MESSAGE_CORRECT = "status-message=\"correct\""; 
	
	String soapServiceIP = ""; 
	String soapRequest = "";
	String service = "";
	String method = "";
	String response = "";
	
	public String ti;
	
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
	
	private static String getProductInfoForShuttle = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:prod=\"http://products.setretailx.crystals.ru/\">" +
		   "<soapenv:Header/>" +
		   "<soapenv:Body>" +
		      "<prod:getProductInfoForShuttle>" +
		         "<CMDMNEMONIC></CMDMNEMONIC>" +
		         "<CLIENTIP></CLIENTIP>" +
		         "<CLIENTMAC>%s</CLIENTMAC>" +
		         "<REQUEST>%s</REQUEST>" +
		      "</prod:getProductInfoForShuttle>" +
		   "</soapenv:Body>" +
		"</soapenv:Envelope>";
	
	
	public void setSoapServiceIP(String ip){
		log.info("Таргет хост для отправки soap запроса: " + ip);
		this.soapServiceIP = ip;
	}
	
	public String encodeBase64(String stringToEncode){
		try {
			Base64 codec = new Base64();
			return new String(codec.encode(stringToEncode.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String generateTI(){
		// get last numbers of date value
		this.ti =  String.valueOf((new Date().getTime())).substring(6, 13);
		log.info("TI = " + this.ti);
		return this.ti;
	}
	
	public String getTI(){
		return this.ti;
	}
	
	public void getAlcoRestrictions(){
		DisinsectorTools.delay(1000);
		this.soapRequest = soapGetAlcoRestrictions;
		this.service = SERVICE_ALCO_RESTRICTIONS; 
		this.method = METHOD_ALCO_RESTRICTIONS;
		log.info("Выгрузить алкогольные ограничения. SOAP request: \n" + this.soapRequest);
		sendSOAPRequest();
	}
	
	public void sendPriceCheckerRequest(String mac, String barcode){
		this.soapRequest = String.format(getProductInfoForShuttle, mac, barcode);
		this.service = SERVICE_PRICE_CHECKER; 
		this.method = METHOD_PRICECHECKER_SHUTTLE;
		log.info("Отправить запрос с парайс чекера. SOAP request: \n" + this.soapRequest);
		sendSOAPRequest();
	}
	
	public void sendGoods(String request, String ti){
		this.soapRequest = String.format(soapRequestGoods, encodeBase64(request), ti);
		this.service = ERP_INTEGRATION_GOOSERVICE; 
		this.method = METHOD_GOODS_WITHTI;
		log.info("Отправить товары. SOAP request: \n" + this.soapRequest);
		sendSOAPRequest();
		assertSOAPResponse(RETURN_MESSAGE_CORRECT, ti);
	}
	
	/*
	 * Метод посылает товары, и ждет ответа status="3" (товары успешно импортировались)
	 */
	public HashMap<String, String> sendGoods(String request, HashMap<String, String> params){
		ti = generateTI();
		this.soapRequest = String.format(soapRequestGoods, encodeBase64(processRequestParams(request, params)), ti);
		this.service = ERP_INTEGRATION_GOOSERVICE; 
		this.method = METHOD_GOODS_WITHTI;
		params.put("ti", ti);
		log.info("Отправить товары. SOAP request: \n" + this.soapRequest);
		sendSOAPRequest();
		assertSOAPResponse(RETURN_MESSAGE_CORRECT, ti);
		return params;
	}
	
	private String processRequestParams(String request, HashMap<String, String> params){
		for (String param:params.keySet()){
			request = request.replace(param, params.get(param));
		}
		return request;
	}
	
	public void sendAdversting(String request, String ti){
		this.soapRequest = String.format(soapRequestAdversting, encodeBase64(request), ti);
		this.service = ERP_INTEGRATION_ADVERTSING_ACTIONS; 
		this.method = METHOD_ACTIONS_WITHTI;
		log.info("Отправить рекламную акцию. SOAP request: \n" + this.soapRequest);
		sendSOAPRequest();
	}
	
	public void getFeedBack(String ti){
		this.soapRequest = String.format(soapGetFeedBack, ti);
		this.service = ERP_INTEGRATION_FEDDBACK; 
		this.method = METHOD_ACTIONS_WITHTI;
		sendSOAPRequest();
	}
	
	private void sendSOAPRequest(){
		URL resourceURL;
		HttpURLConnection con = null;
		String result = "";
		String serviceUrl ="http://" + this.soapServiceIP + ":" + Config.DEFAULT_PORT; 

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
		//log.info("SOAP RESPONSE: " + this.response);
	}
	
	
	public boolean assertSOAPResponseXpath(String xpathExpression)  {
		boolean xpathResult = false;
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
		    xpathResult = (boolean) xPath.compile(xpathExpression).evaluate(xmlDocument, XPathConstants.BOOLEAN);
		    
		
		} catch (ParserConfigurationException e) {
		    e.printStackTrace();  
		} catch (SAXException saxEx) {
			saxEx.printStackTrace();
		} catch ( IOException ioEx) {
			ioEx.printStackTrace();
		} catch (XPathExpressionException e) {
			 e.printStackTrace();  
		}
		return xpathResult;
	}
	
	public boolean assertSOAPResponse(String expectedResult, String ti){
		int timeout = 0;
		getFeedBack(ti);
		log.info("Ожидаемое значение в SOAP response: " + expectedResult + " ; ti = " + ti); 
		try {
		while (timeout <=20) {
			if (this.response.contains(expectedResult)){ 
				return true;
			}
			Thread.sleep(1000);
			timeout +=1;
			sendSOAPRequest();
		}
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.info("Пакет с ti " + ti + "не содержит " + expectedResult);
		}
		return false;
	}
	
	public void sendGoodsToStartTesting(String targetHost, String fileName){
		setSoapServiceIP(targetHost);
		String ti = generateTI();
		String goodRequest = DisinsectorTools.getFileContentAsString(fileName);
		sendGoods(goodRequest,ti);
		assertSOAPResponse(RETURN_MESSAGE_CORRECT, ti);
	}
	
}
