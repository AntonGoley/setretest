package ru.crystals.set10.utils;

import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.crystals.ERPIntegration.discounts.model.xml.imp.AdvertisingActionType;
import ru.crystals.ERPIntegration.discounts.model.xml.imp.AdvertisingActionsType;
import ru.crystals.set10.config.Config;
import ru.crystals.setretailx.products.catalog.BarcodeExt;
import ru.crystals.setretailx.products.catalog.Good;
import ru.crystals.setretailx.products.catalog.Likond;
import ru.crystals.setretailx.products.catalog.GoodsCatalog;


public class SoapRequestSender{
	protected static final Logger log = Logger.getLogger(SoapRequestSender.class);
	
    public static final String ERP_INTEGRATION_GOOSERVICE = "/SET-ERPIntegration/SET/WSGoodsCatalogImport";
    public static final String ERP_INTEGRATION_ADVERTSING_ACTIONS = "/SET-ERPIntegration/AdvertisingActionsImport";
    public static final String ERP_INTEGRATION_FEDDBACK = "/SET-ERPIntegration/SET/FeedbackWS";
    public static final String SERVICE_ALCO_RESTRICTIONS = "/SET-Alcohol/SET/SpiritRestrictionsExportWS";
    public static final String SERVICE_PRICE_CHECKER = "/SET-Products/SET/Products";
	
    
    public static final String ERP_INTEGRATION_NAMESPACE = "http://plugins.products.ERPIntegration.crystals.ru/";
    public static final String ERP_INTEGRATION_FEEDBACK = "http://feedback.ERPIntegration.crystals.ru/";
	
	private static final String METHOD_GOODS_WITHTI = "getGoodsCatalogWithTi";
	private static final String METHOD_ACTIONS_WITHTI = "#importActionsWithTi";
	private static final String METHOD_ALCO_RESTRICTIONS = "#getSpiritRestrictions";
	private static final String METHOD_PRICECHECKER_SHUTTLE = "#getProductInfoForShuttle";
	private static final String METHOD_PACKAGE_STATUS = "getPackageStatus";
	
	
	public static final String RETURN_MESSAGE_CORRECT = "correct"; 
	
	String soapServiceIP = ""; 
	String soapRequest = "";
	String service = "";
	String method = "";
	String response = "";
	SOAPMessage soapResponse;
	
	private static long tiPrefix = new Date().getTime();
	private String ti;
	
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
	
	private static String soapGetAlcoRestrictions = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:erp=\"http://erpiservice.limits.crystals.ru/\">" +
		"<soapenv:Header/>" +
		"<soapenv:Body>" +
		 "<erp:getSpiritRestrictions>" +
        	"<from>%s</from>" +
        	"<till>%s</till>" +
        "</erp:getSpiritRestrictions>" +
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
	
	
	public SoapRequestSender (){
	}
	
	public SoapRequestSender (String ip){
		setSoapServiceIP(ip);
	}
	
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
		this.ti =  String.valueOf((tiPrefix++)).substring(6, 13);
		log.info("TI = " + this.ti);
		return this.ti;
	}
	
	public void getAlcoRestrictions(String from, String till){
	}
	
	public void sendPriceCheckerRequest(String mac, String barcode){
		this.soapRequest = String.format(getProductInfoForShuttle, mac, barcode);
		this.service = SERVICE_PRICE_CHECKER; 
		this.method = METHOD_PRICECHECKER_SHUTTLE;
		log.info("Отправить запрос с парайс чекера. SOAP request: \n" + this.soapRequest);
		//sendSOAPRequest();
	}
	
	public void sendGoods(String request, String ti){
		this.soapRequest = String.format(soapRequestGoods, encodeBase64(request), ti);
		this.service = ERP_INTEGRATION_GOOSERVICE; 
		this.method = METHOD_GOODS_WITHTI;
		log.info("Отправить товары. SOAP request: \n" + this.soapRequest);
		//sendSOAPRequest();
		assertSOAPResponse(RETURN_MESSAGE_CORRECT, ti);
	}
	
	public void sendAdversting(String request, String ti){
		this.soapRequest = String.format(soapRequestAdversting, encodeBase64(request), ti);
		this.service = ERP_INTEGRATION_ADVERTSING_ACTIONS; 
		this.method = METHOD_ACTIONS_WITHTI;
		log.info("Отправить рекламную акцию. SOAP request: \n" + this.soapRequest);
		//sendSOAPRequest();
	}
	
	public void sendAdversting(){
		SoapMessageFactory goodMessage = new SoapMessageFactory();
		SOAPMessage message = goodMessage.getFeedBackMessage(ti);
		sendSOAPRequest(message, ERP_INTEGRATION_FEDDBACK);
	}
	
	public void getFeedBack(String ti){
		SoapMessageFactory goodMessage = new SoapMessageFactory();
		SOAPMessage message = goodMessage.getFeedBackMessage(ti);
		sendSOAPRequest(message, ERP_INTEGRATION_FEDDBACK);
	}
	
	
	/*
	 * Отправить товар
	 */
	public String sendGood(Good good){
		GoodsCatalog goodsCatalog = new GoodsCatalog();
		List<Good> gList = new ArrayList<Good>();
		gList.add(good);
		goodsCatalog.getGoods().addAll(gList);
		return send(goodsCatalog);
	}	
	
	/*
	 * Отправить ликонд
	 */
	public String sendLicond(Likond likond){
		GoodsCatalog goodsCatalog = new GoodsCatalog();
		List<Likond> likondList = new ArrayList<Likond>();
		likondList.add(likond);
		goodsCatalog.getLikonds().addAll(likondList);
		return send(goodsCatalog);
	}	
	
	/*
	 * Отправить баркоды
	 */
	public String sendBarcode(BarcodeExt barcode){
		GoodsCatalog goodsCatalog = new GoodsCatalog();
		List<BarcodeExt> barcodes = new ArrayList<BarcodeExt>();
		barcodes.add(barcode);
		goodsCatalog.getBarcodes().addAll(barcodes);
		return send(goodsCatalog);
	}
	
	
	public void sendAdversting(AdvertisingActionType action){
		AdvertisingActionsType actions = new AdvertisingActionsType();
		actions.getAdvertisingAction().add(action);
		sendAdverstings(actions);
		
	}
	
	public String sendAdverstings(AdvertisingActionsType action){
		generateTI();
		StringWriter request = new StringWriter();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(AdvertisingActionsType.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			QName qName = new QName("", "AdvertisingActions");
	        JAXBElement<AdvertisingActionsType> root = new JAXBElement<AdvertisingActionsType>(qName, AdvertisingActionsType.class, action);
			
			jaxbMarshaller.marshal(root, request);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		try {
			
			log.info("Отправить рекламные акции. SOAP request: \n" + request.toString()); 
			
			SoapMessageFactory goodMessage = new SoapMessageFactory();
			SOAPMessage message = goodMessage.getAdversting(request.toString(), this.ti);
			sendSOAPRequest(message, ERP_INTEGRATION_ADVERTSING_ACTIONS);
			assertSOAPResponseNew(RETURN_MESSAGE_CORRECT, this.ti);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	
	/*
	 *	Отправить группу товаров (ликондов, бар кодов) 
	 */
	public String send(GoodsCatalog catalog){	
		generateTI();
		StringWriter request = new StringWriter();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GoodsCatalog.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(catalog, request);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		try {
			
			log.info("Отправить товары. SOAP request: \n" + request.toString()); 
			
			SoapMessageFactory goodMessage = new SoapMessageFactory();
			SOAPMessage message = goodMessage.getGoodMessage(request.toString(), this.ti);
			sendSOAPRequest(message, ERP_INTEGRATION_GOOSERVICE);
			assertSOAPResponseNew(RETURN_MESSAGE_CORRECT, this.ti);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Залогировать результат запроса в файл
		 * TODO: добавить опцию вкл/выкл, имя файла
		 */
		File f = new File("weightGoods.txt"); 
		try {
			FileWriter fis = new FileWriter(f);
			fis.write(request.toString());
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		return ti;
	}
	
	/*
	 * Отправить SOAP запрос
	 */
	private void sendSOAPRequest(SOAPMessage message, String service){

		String serviceUrl ="http://" + this.soapServiceIP + ":" + Config.DEFAULT_PORT; 
		
		try {
			
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		      
		    SOAPMessage soapResponse = soapConnection.call(message, serviceUrl + service);
	        soapConnection.close();
	        
	        this.soapResponse = soapResponse;
	        
	        //TODO:обработать ответ!!!
	        
	        
		} catch (SOAPException se) {
			se.printStackTrace();
		}	
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
	
	
	public boolean assertSOAPResponseNew(String expectedResult, String ti){
		int timeout = 0;
		String result = "";
		
		log.info("Ожидаемое значение в SOAP response: " + expectedResult + " ; ti = " + ti); 

		while (timeout <=20) {
			getFeedBack(ti);
			DisinsectorTools.delay(1000);
			
			try {
				NodeList nodes =  soapResponse.getSOAPPart().getEnvelope().getBody().getElementsByTagName("import");

				Element  resultElement = (Element) nodes.item(0);
				result = resultElement.getAttribute("status-message");
			
				if (result.contains(expectedResult)){ 
					return true;
				}
			
				timeout +=1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		
		try {
			throw new Exception("Пакет с ti " + ti + " не содержит " + expectedResult);
		} catch (Exception e) {
			log.info("Запрос вернул status-message: " + result);
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean assertSOAPResponse(String expectedResult, String ti){
		int timeout = 0;
		getFeedBack(ti);
		log.info("Ожидаемое значение в SOAP response: " + expectedResult + " ; ti = " + ti); 

		while (timeout <=20) {
			if (this.response.contains(expectedResult)){ 
				return true;
			}
			DisinsectorTools.delay(1000);
			timeout +=1;
			//sendSOAPRequest();
	}
		
		try {
			throw new Exception("Пакет с ti " + ti + " не содержит " + expectedResult);
		} catch (Exception e) {
			e.printStackTrace();
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
