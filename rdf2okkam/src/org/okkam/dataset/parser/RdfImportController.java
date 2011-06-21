package org.okkam.dataset.parser;

import org.okkam.service.client.OkkamClient;
import org.okkam.service.client.ServiceClient;

public class RdfImportController {

	private static final String filename = "resources/anagrafe-unreconciled.ttl";
	
	private static final String confpath = "conf";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RdfParser parser = new RdfParser(filename);
		
		ServiceClient client = new ServiceClient(new OkkamClient(confpath));
		
		client.createNewEntity(null,null, false);
	

	}

}
