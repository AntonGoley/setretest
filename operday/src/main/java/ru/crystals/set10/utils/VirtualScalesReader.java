package ru.crystals.set10.utils;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import ru.crystals.httpclient.HttpFileConnection;
import ru.crystals.httpclient.HttpFileTransport;
import ru.crystals.scales.tech.core.scales.virtual.xml.LinkToPluType;
import ru.crystals.scales.tech.core.scales.virtual.xml.Links;
import ru.crystals.setretailx.scales.LinkPLUtoScales;
import static ru.crystals.set10.config.Config.VIRTUAL_WEIGHT_PATH;

public class VirtualScalesReader {
	
	private static final Logger log = Logger.getLogger(VirtualScalesReader.class);
	private HttpFileTransport httpFileTransport = new HttpFileTransport();
	private URL virtualScales;
	HttpURLConnection  connection;
	
	public VirtualScalesReader(){
		try {
			virtualScales = new URL(VIRTUAL_WEIGHT_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<LinkPLUtoScales> readVirtualScales(){
		List<LinkPLUtoScales> result = null;
		
		try {	
			Links links = new Links();
			
			Unmarshaller unmarchaller;
			JAXBContext context = null;
            context = JAXBContext.newInstance(Links.class.getPackage().getName());
            unmarchaller = context.createUnmarshaller();
            links = (Links)unmarchaller.unmarshal(virtualScales);
            
            List<LinkToPluType> linksToPlue = links.getLinkToPlu();
            
            log.info(linksToPlue.get(0).getPlu().getNumber());

	        } catch (JAXBException e) {
	            e.printStackTrace();
	        }

		return result;
	}
	
	
	public void getLinkByLPUNumber(String pluNumber){
		
	}
	
	
	public void clearVScalesFileData(){
		try {
			connection = (HttpURLConnection) virtualScales.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("DELETE");
			connection.connect();
			log.info(connection.getResponseCode());
			connection.disconnect();
			
			log.info("Файл " + VIRTUAL_WEIGHT_PATH + " удален!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HttpFileConnection getHttpFileOutputConnection(String fileAddress) throws MalformedURLException, IOException {
        return httpFileTransport.getServerOutput(fileAddress);
 }

}
