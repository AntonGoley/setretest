package ru.crystals.set10.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import ru.crystals.setretailx.scales.LinkPLUtoScales;

public class VirtualScalesReader {
	
	
	private URLConnection connection = null;
	
	public VirtualScalesReader(String virtualScalesUrl){
		try {
			URL virtualScales = new URL(virtualScalesUrl);
			this.connection = virtualScales.openConnection();
			connection.setDoInput(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public List<LinkPLUtoScales> readVirtualScales(){
		List<LinkPLUtoScales> result = null;
		
		try (
				BufferedReader  ir = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		){
		
			XStream xstream = new XStream();
			StringBuffer st = new StringBuffer();
			
			
			String value = "";
			while ((value = ir.readLine()) !=null){
				st.append(value);
			}
			
			System.out.println(st.toString());
			result = (List<LinkPLUtoScales>)xstream.fromXML(st.toString());
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	

}
