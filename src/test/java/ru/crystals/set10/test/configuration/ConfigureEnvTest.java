package ru.crystals.set10.test.configuration;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import ru.crystals.set10.amfcall.AMFRequester;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.test.AbstractTest;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;


public class ConfigureEnvTest extends AbstractTest{
	
	protected static final Logger log = Logger.getLogger(ConfigureEnvTest.class);
	
	String amfTopologyServiceUrl = "/SET-Topology-Web/messagebroker/amf";
	String amfUsers = "/SET-Users-Web/messagebroker/amf";
	AMFRequester requester;

	
	@Test (description = "Подключить магазин к центруму",
			groups = {"Config"})
	public void connectShop(){
		log.info("Подключить магазин " + Config.RETAIL_HOST + " к центруму " + Config.CENTRUM_HOST);
		try {
			requester = new AMFRequester(String.format("http://%s:%s/%s", Config.RETAIL_HOST, Config.DEFAULT_PORT, amfTopologyServiceUrl));
			requester.call("topologyEditService", "setCentrumUrl", new Object[]{Config.CENTRUM_HOST}, null);
			// topologySearchService
			requester.call("topologySearchService", "isConnectionToCentrum", new Object[]{}, null);
		} catch (ClientStatusException e) {
			e.printStackTrace();
		}
	}
		
//		try {
//			
//			requester = new AMFRequester(String.format("http://%s:%s/%s", Config.RETAIL_HOST, Config.DEFAULT_PORT, amfUsers));
//			requester.call("usersLocalService", "addRolesToUser", new Object[]{Config.CENTRUM_HOST}, null);
//		} catch (ClientStatusException e) {
//			
//		}

	
//	@Test (description = "Создать новую топологию с одним магазином", 
//			groups = {"Config", "Topology"})
	public void setTopology(){
		
		try {
			requester = new AMFRequester(String.format("http://%s:%s/%s", Config.CENTRUM_HOST, Config.DEFAULT_PORT, amfTopologyServiceUrl));
			requester.call("topologyRemoteService", "addRegion", new Object[]{"AutoConfigRegion", "0"}, null);
			requester.call("topologyRemoteService", "addCity", new Object[]{"AutoConfigCity"}, null);
			requester.call("topologyRemoteService", "addShop", new Object[]{"AutoConfigShop"}, null);
		} catch (ClientStatusException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
