package ru.crystals.set10.utils;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
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
	
	public static final String RETURN_MESSAGE_CORRECT = "correct"; 
	
	private String soapServiceIP = ""; 
	private SOAPMessage soapResponse;
	
	private static long tiPrefix = new Date().getTime();
	private String ti;
	
	public SoapRequestSender (){
	}
	
	public SoapRequestSender (String ip){
		setSoapServiceIP(ip);
	}
	
	public void setSoapServiceIP(String ip){
		log.info("Таргет хост для отправки soap запроса: " + ip);
		this.soapServiceIP = ip;
	}
	
	public String generateTI(){
		this.ti =  String.valueOf((tiPrefix++)).substring(6, 13);
		log.info("TI = " + this.ti);
		return this.ti;
	}
	
	public void getAlcoRestrictions(String from, String till){
		SoapMessageList soapMessage = new SoapMessageList();
		SOAPMessage message = soapMessage.getAlcoRestrictions(from, till);
		sendSOAPRequest(message, SERVICE_ALCO_RESTRICTIONS);
	}
	
	public void sendPriceCheckerRequest(String mac, String barcode){
		SoapMessageList soapMessage = new SoapMessageList();
		SOAPMessage message = soapMessage.getPriceCheckerRequest(mac, barcode);
		sendSOAPRequest(message, SERVICE_PRICE_CHECKER);
	}
	
	public void getFeedBack(String ti){
		SoapMessageList goodMessage = new SoapMessageList();
		SOAPMessage message = goodMessage.getFeedBackMessage(ti);
		sendSOAPRequest(message, ERP_INTEGRATION_FEDDBACK);
	}
	
	/*
	 * Отправить товар
	 */
	public void sendGood(Good good){
		GoodsCatalog goodsCatalog = new GoodsCatalog();
		List<Good> gList = new ArrayList<Good>();
		gList.add(good);
		goodsCatalog.getGoods().addAll(gList);
		send(goodsCatalog);
	}	
	
	/*
	 * Отправить ликонд
	 */
	public void sendLicond(Likond likond){
		GoodsCatalog goodsCatalog = new GoodsCatalog();
		List<Likond> likondList = new ArrayList<Likond>();
		likondList.add(likond);
		goodsCatalog.getLikonds().addAll(likondList);
		send(goodsCatalog);
	}	
	
	/*
	 * Отправить баркоды
	 */
	public void sendBarcode(BarcodeExt barcode){
		GoodsCatalog goodsCatalog = new GoodsCatalog();
		List<BarcodeExt> barcodes = new ArrayList<BarcodeExt>();
		barcodes.add(barcode);
		goodsCatalog.getBarcodes().addAll(barcodes);
		send(goodsCatalog);
	}
	
	public void sendAdvertising(AdvertisingActionType action){
		AdvertisingActionsType actions = new AdvertisingActionsType();
		actions.getAdvertisingAction().add(action);
		sendAdverstings(actions);
		
	}
	
	/*
	 * Отправить список РА
	 */
	private void sendAdverstings(AdvertisingActionsType action){
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
			
			SoapMessageList goodMessage = new SoapMessageList();
			SOAPMessage message = goodMessage.getAdversting(request.toString(), this.ti);
			sendSOAPRequest(message, ERP_INTEGRATION_ADVERTSING_ACTIONS);
			assertSOAPResponse(RETURN_MESSAGE_CORRECT, this.ti);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 *	Отправить группу товаров (ликондов, бар кодов) 
	 */
	public void send(GoodsCatalog catalog){	
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
			
			SoapMessageList goodMessage = new SoapMessageList();
			SOAPMessage message = goodMessage.getGoodMessage(request.toString(), this.ti);
			sendSOAPRequest(message, ERP_INTEGRATION_GOOSERVICE);
			assertSOAPResponse(RETURN_MESSAGE_CORRECT, this.ti);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Залогировать результат запроса в файл
		 * TODO: добавить опцию вкл/выкл, имя файла
		 */
//		File f = new File("weightGoods.txt"); 
//		try {
//			FileWriter fis = new FileWriter(f);
//			fis.write(request.toString());
//			fis.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	

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
	
	/*
	 * Проверка статуса пакета по ti
	 */
	public boolean assertSOAPResponse(String expectedResult, String ti){
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
	
	
	/*
	 *	Валидация soap респонса, используется только
	 * для выгрузки алк. ограничений в SAP 
	 */
	public boolean assertSOAPResponseXpath(String xpathExpression)  {
		boolean xpathResult = false;
		String result = null;
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
		    builder = builderFactory.newDocumentBuilder();
		    
		    ByteArrayOutputStream bout = new ByteArrayOutputStream();
		    soapResponse.writeTo(bout);
		    
		    ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		    Document xmlDocument1 = builder.parse(bin);
		    
		    XPath xPath =  XPathFactory.newInstance().newXPath();
		    
	    	// получаем Base64 response
		    result = xPath.compile("//return").evaluate(xmlDocument1);
	    	// декодируем
		    byte[] decoded = DatatypeConverter.parseBase64Binary(result);
		    xmlDocument1 = builder.parse(new ByteArrayInputStream(decoded));
		    // и проверяем
		    xpathResult = (boolean) xPath.compile(xpathExpression).evaluate(xmlDocument1, XPathConstants.BOOLEAN);
		    return false;
		
		} catch (ParserConfigurationException e) {
		    e.printStackTrace();  
		} catch (SAXException saxEx) {
			saxEx.printStackTrace();
		} catch ( IOException ioEx) {
			ioEx.printStackTrace();
		} catch (XPathExpressionException e) {
			 e.printStackTrace();  
		} catch (SOAPException e) {
			 e.printStackTrace();  
		} 
		
		return xpathResult;
	}
	
	/*
	 * Метод используется для отправки товаров из текстового файла
	 */
	public void sendGoodsToStartTesting(String targetHost, String fileName){
		setSoapServiceIP(targetHost);
		generateTI();
		String request = DisinsectorTools.getFileContentAsString(fileName);
		try {
			SoapMessageList goodMessage = new SoapMessageList();
			SOAPMessage message = goodMessage.getGoodMessage(request, this.ti);
			sendSOAPRequest(message, ERP_INTEGRATION_GOOSERVICE);
			assertSOAPResponse(RETURN_MESSAGE_CORRECT, this.ti);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
