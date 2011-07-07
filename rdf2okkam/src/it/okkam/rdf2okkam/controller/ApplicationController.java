package it.okkam.rdf2okkam.controller;

import it.okkam.rdf2okkam.ens.EntityBuilder;

import org.okkam.client.data.AttributesType;
import org.okkam.client.data.ProfileType;
import org.okkam.service.client.OkkamClient;
import org.okkam.service.client.ServiceClient;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ApplicationController {
	
	private final String confpath = "conf";
	
	Model _model = null ;
	
	EntityBuilder builder = null ;
	
	OkkamClient okkam = null;
	
	ServiceClient service = null ;
	
	public ApplicationController(Model model) {
		_model = model ;
		builder = new EntityBuilder( _model ) ;
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
