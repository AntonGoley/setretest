package ru.crystals.set10.utils;

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


public class SoapMessageFactory {
	
	private SOAPMessage message;
	
    public static final String ERP_INTEGRATION_NAMESPACE = "http://plugins.products.ERPIntegration.crystals.ru/";
    public static final String ERP_INTEGRATION_FEEDBACK = "http://feedback.ERPIntegration.crystals.ru/";
    
    private static final String METHOD_GOODS_WITHTI = "getGoodsCatalogWithTi";
	private static final String METHOD_ACTIONS_WITHTI = "importActionsWithTi";
	private static final String METHOD_ALCO_RESTRICTIONS = "getSpiritRestrictions";
	private static final String METHOD_PRICECHECKER_SHUTTLE = "getProductInfoForShuttle";
	private static final String METHOD_PACKAGE_STATUS = "getPackageStatus";
	
	
    public SoapMessageFactory () {
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
			setNameSpaceDeclaration(prefix, ERP_INTEGRATION_NAMESPACE);
			message.getMimeHeaders().addHeader("SOAPAction",  ERP_INTEGRATION_NAMESPACE + METHOD_GOODS_WITHTI);
			
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
	 * Обратная связь по ti
	 */
	public SOAPMessage getFeedBackMessage(String ti){
		SOAPBody soapBody;
		String prefix = "feed";
		try {
			setNameSpaceDeclaration(prefix, ERP_INTEGRATION_FEEDBACK);
			
			soapBody = this.message.getSOAPBody();
			SOAPElement goodsCatalogElem = soapBody.addChildElement(METHOD_PACKAGE_STATUS, prefix);
			goodsCatalogElem.addChildElement("xmlGetstatus")
				.addChildElement("import")
				.setAttribute("ti", ti);
			this.message.saveChanges();
			
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		return this.message;
	}
	
	public SOAPMessage adversting(){
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
