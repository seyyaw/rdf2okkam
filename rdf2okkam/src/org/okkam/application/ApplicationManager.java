package org.okkam.application;

import org.okkam.client.data.AttributesType;
import org.okkam.client.data.ProfileType;
import org.okkam.dataset.parser.EntityBuilder;
import org.okkam.service.client.OkkamClient;
import org.okkam.service.client.ServiceClient;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ApplicationManager {
	
	private final String confpath = "conf";
	
	Model _model = null ;
	
	EntityBuilder builder = null ;
	
	OkkamClient okkam = null;
	
	ServiceClient service = null ;
	
	public ApplicationManager(Model model) {
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
