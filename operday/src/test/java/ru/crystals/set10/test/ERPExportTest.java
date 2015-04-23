package ru.crystals.set10.test;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.crystals.pos.check.PurchaseEntity;
import ru.crystals.set10.utils.CashEmulator;
import ru.crystals.set10.utils.PaymentGenerator;

public class ERPExportTest {
	
	protected static final Logger log = Logger.getLogger(ERPExportTest.class);
	
	CashEmulator cashEmulator;
	CashEmulator cashEmulatorVirtualShop;
	HashMap<Long, Long>  returnPositions = new HashMap<Long, Long>(); 
	
	PaymentGenerator payments = new PaymentGenerator();
	PurchaseEntity p1;
	PurchaseEntity p2;
	
	@BeforeClass
	public void setupCash(){
	}
	
	@Test (	description = "Экспорт документов в SAP. Чек экспортируется в SAP")
	public void testExportChecks(){
	}
	
	@Test (	description = "Экспорт документов в SAP. Внесение экспортируется в SAP")
	public void testExportIntroduction(){
	}
	
	@Test (	description = "Экспорт документов в SAP. Изъятие экспортируется в SAP")
	public void testExportWithDrawals(){
	}
	
	@Test (	description = "Экспорт документов в SAP. Z-отчет экспортируется в SAP")
	public void testExportZReport(){
	}
	
}
