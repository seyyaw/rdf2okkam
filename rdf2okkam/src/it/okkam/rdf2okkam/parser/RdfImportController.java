package it.okkam.rdf2okkam.parser;

import it.okkam.rdf2okkam.ens.client.OkkamClient;
import it.okkam.rdf2okkam.ens.client.ServiceClient;

import com.hp.hpl.jena.rdf.model.Model;

public class RdfImportController {

	private static final String filename = "resources/anagrafe-unreconciled.ttl";
	
	private static final String confpath = "conf";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Model modelIn = null ;
		
		Model modelOut = null ;
		
		RdfUtil parser = new RdfUtil(modelIn, modelOut);
		
		ServiceClient client = new ServiceClient(new OkkamClient(confpath));
		
		client.createNewEntity(null,null, false);
	

	}

}
