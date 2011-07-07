package it.okkam.rdf2okkam.controller;

import it.okkam.rdf2okkam.ens.EntityBuilder;
import it.okkam.rdf2okkam.ens.client.OkkamClient;
import it.okkam.rdf2okkam.ens.client.ServiceClient;
import it.okkam.rdf2okkam.model.ModelLoader;

import org.okkam.client.data.AttributesType;
import org.okkam.client.data.ProfileType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ApplicationController {

	
	private final String confpath = "conf";
	
	Model _model = null ;
	
	EntityBuilder builder = null ;
	
	OkkamClient okkam = null;
	
	ServiceClient service = null ;
	
	ModelLoader loader = null ;
	
	public ApplicationController() {
		
		loader = ModelLoader.getInstance() ;
		builder = new EntityBuilder( loader.getInputModel() ) ;
		okkam = new OkkamClient( confpath ) ;
		service = new ServiceClient( okkam ) ;
	}
	
	public String createEntity(RDFNode node) {
		
		String result = "" ; // okkamid
		
		ProfileType profile = builder.buildEntity( node ) ;
		
		boolean ignoreDuplicates = false ;
		
		
		result = service.createNewEntity(profile.getSemanticType(), 
				profile.getAttributes(), ignoreDuplicates) ;
				
		
		return result ;
	}

}
