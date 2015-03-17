package ru.crystals.set10.test.configuration;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import ru.crystals.set10.amfcall.AMFRequester;
import ru.crystals.set10.config.Config;
import ru.crystals.set10.test.AbstractTest;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;

@Test(groups = "config")
public class ConfigureEnvTest extends AbstractTest{
	
	protected static final Logger log = Logger.getLogger(ConfigureEnvTest.class);
	
	String amfTopologyServiceUrl = "/SET-Topology-Web/messagebroker/amf";
	String amfUsers = "/SET-Users-Web/messagebroker/amf";
	AMFRequester requester;

	
	@Test (description = "Подключить магазин к центруму")
	public void connectShop(){
		log.info("Подключить магазин " + Config.RETAIL_HOST + " к центруму " + Config.CENTRUM_HOST);
		try {
			requester = new AMFRequester(String.format("http://%s:%s/%s", Config.RETAIL_HOST, Config.DEFAULT_PORT, amfTopologyServiceUrl));
			requester.call("topologyEditService", "setCentrumUrl", new Object[]{Config.CENTRUM_HOST}, null);
			// topologySearchService
			requester.call("topologySearchService", "isConnectionToCentrum", new Object[]{}, null);
			
			requester.call("topologySearchService", "eventNewShop", new Object[]{}, null);
		} catch (ClientStatusException e) {
			e.printStackTrace();
		}
	}
	
}
