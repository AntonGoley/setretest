package ru.crystals.set10.pages.product;

import static ru.crystals.set10.utils.FlexMediator.*;


import org.openqa.selenium.WebDriver;


public class ProductMainInfoTabPage extends ProductCardPage{
	
	
	public static final String CERTIFICATION_LABEL = "certificationLabel";
	public static final String CERTIFICATION_TYPE_OBLIGATE = "_CertifiationGroup_Image1";
	public static final String CERTIFICATION_TYPE_FREE = "_CertifiationGroup_Image2";
	public static final String CERTIFICATION_TYPE_TECNICAL_REGULATION = "_CertifiationGroup_Image2";
	public static final String CERTIFICATION_TYPE_EAC = "_CertifiationGroup_Image4";
	
	
	public ProductMainInfoTabPage(WebDriver driver) {
		super(driver);
	}

	public boolean ifCertificationTypeVisible(String certificationImageId){
		waitForElementVisible(getDriver(), ID_PRODUCTSWF, CERTIFICATION_LABEL);
		String result = getElementProperty(getDriver(), ID_PRODUCTSWF, certificationImageId, "visible");
		return Boolean.valueOf(result);
	}
	
}
