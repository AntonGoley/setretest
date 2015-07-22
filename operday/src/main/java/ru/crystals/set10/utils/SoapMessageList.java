package ru.crystals.set10.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.apache.commons.codec.binary.Base64;


public class SoapMessageList {
	
	private SOAPMessage message;
	
    public static final String ERP_INTEGRATION_GOOD_CATALOG = "http://plugins.products.ERPIntegration.crystals.ru/";
    public static final String ERP_INTEGRATION_FEEDBACK = "http://feedback.ERPIntegration.crystals.ru/";
    public static final String ERP_INTEGRATION_ADVERSTING = "http://ws.discounts.ERPIntegration.crystals.ru/";
    public static final String PRODUCTS_MANAGER_NAMESPACE = "http://products.setretailx.crystals.ru/";
    public static final String SPIRITS_LIMITS_NAMESPACE = "http://erpiservice.limits.crystals.ru/";
    
    
    private static final String METHOD_GOODS_WITHTI = "getGoodsCatalogWithTi";
	private static final String METHOD_ACTIONS_WITHTI = "importActionsWithTi";
	private static final String METHOD_ALCO_RESTRICTIONS = "getSpiritRestrictions";
	private static final String METHOD_PRICECHECKER_SHUTTLE = "getProductInfoForShuttle";
	private static final String METHOD_PACKAGE_STATUS = "getPackageStatus";
	
	
    public SoapMessageList () {
		try {
			MessageFactory messageFactory = MessageFactory.newInstance();
			message = messageFactory.createMessage();
			
			/* задать базовую кодировку*/
			MimeHeaders headers = message.getMimeHeaders();
	        headers.addHeader("Content-Type", "text/xml; charset=utf-8");
		}
		
        catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	private void setNameSpaceDeclaration(String prefix, String namespace) throws SOAPException{
		SOAPPart soapPart = message.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(prefix, namespace);
	}
	
	/*
	 * Товары
	 */
	public SOAPMessage getGoodMessage(String request, String ti){
		SOAPBody soapBody;
		String prefix = "plug";
		
		try {
			setNameSpaceDeclaration(prefix, ERP_INTEGRATION_GOOD_CATALOG);
			message.getMimeHeaders().addHeader("SOAPAction",  ERP_INTEGRATION_GOOD_CATALOG + METHOD_GOODS_WITHTI);
			
			soapBody = this.message.getSOAPBody();
			SOAPElement goodsCatalogElem = soapBody.addChildElement(METHOD_GOODS_WITHTI, prefix);
			goodsCatalogElem.addChildElement("goodsCatalogXML").addTextNode(encodeBase64(request.toString()));
			goodsCatalogElem.addChildElement("TI").addTextNode(ti);
			this.message.saveChanges();
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return this.message;
	}
	
	/*
	 * Рекламные акции
	 */
	public SOAPMessage getAdversting(String request, String ti){
		SOAPBody soapBody;
		String prefix = "ws";
		try {
			setNameSpaceDeclaration(prefix, ERP_INTEGRATION_ADVERSTING);
			message.getMimeHeaders().addHeader("SOAPAction",  ERP_INTEGRATION_ADVERSTING + METHOD_ACTIONS_WITHTI);
			
			soapBody = this.message.getSOAPBody();
			SOAPElement catalogElem = soapBody.addChildElement(METHOD_ACTIONS_WITHTI, prefix);
			catalogElem.addChildElement("xmlData").addTextNode(encodeBase64(request.toString()));
			catalogElem.addChildElement("TI").addTextNode(ti);
			this.message.saveChanges();
			
			try {
				message.writeTo(System.out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return this.message;
	}
	
	/*
	 * Обратная связь по ti
	 */
	public SOAPMessage getFeedBackMessage(String ti){
		SOAPBody soapBody;
		String prefix = "feed";
		try {
			setNameSpaceDeclaration(prefix, ERP_INTEGRATION_FEEDBACK);
			message.getMimeHeaders().addHeader("SOAPAction",  ERP_INTEGRATION_FEEDBACK + METHOD_PACKAGE_STATUS);
			
			soapBody = this.message.getSOAPBody();
			SOAPElement catalogElem = soapBody.addChildElement(METHOD_PACKAGE_STATUS, prefix);
			catalogElem.addChildElement("xmlGetstatus")
				.addChildElement("import")
				.setAttribute("ti", ti);
			this.message.saveChanges();
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return this.message;
	}
	
	/*
	 * Запрос к прайсчекеру
	 */
	public SOAPMessage getPriceCheckerRequest(String mac, String barcode){
		SOAPBody soapBody;
		String prefix = "prod";
		
		try {
			setNameSpaceDeclaration(prefix, PRODUCTS_MANAGER_NAMESPACE);
			message.getMimeHeaders().addHeader("SOAPAction",  PRODUCTS_MANAGER_NAMESPACE + METHOD_PRICECHECKER_SHUTTLE);
			
			soapBody = this.message.getSOAPBody();
			SOAPElement shuttleCatalogElem = soapBody.addChildElement(METHOD_PRICECHECKER_SHUTTLE, prefix);
			shuttleCatalogElem.addChildElement("CLIENTMAC")
				.addTextNode(mac);
			shuttleCatalogElem.addChildElement("REQUEST")
				.addTextNode(barcode);
			this.message.saveChanges();
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return this.message;
	}
	
	/*
	 * Алкогольные ограничения
	 */
	public SOAPMessage getAlcoRestrictions(String from, String till){
		SOAPBody soapBody;
		String prefix = "erp";
		
		try {
			setNameSpaceDeclaration(prefix, SPIRITS_LIMITS_NAMESPACE);
			message.getMimeHeaders().addHeader("SOAPAction",  SPIRITS_LIMITS_NAMESPACE + METHOD_ALCO_RESTRICTIONS);
			
			soapBody = this.message.getSOAPBody();
			SOAPElement ctalogElem = soapBody.addChildElement(METHOD_ALCO_RESTRICTIONS, prefix);
			ctalogElem.addChildElement("from")
				.addTextNode(from);
				ctalogElem.addChildElement("till")
				.addTextNode(till);
			this.message.saveChanges();
			try {
				message.writeTo(System.out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return this.message;
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
	
	
	
}
