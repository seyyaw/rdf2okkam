package it.okkam.rdf2okkam.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import it.okkam.rdf2okkam.ens.EntityBuilder;
import it.okkam.rdf2okkam.ens.client.OkkamClient;
import it.okkam.rdf2okkam.ens.client.ServiceClient;
import it.okkam.rdf2okkam.model.ModelLoader;
import it.okkam.rdf2okkam.model.Tax2EnsMapper;
import it.okkam.rdf2okkam.parser.Globalizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	
	Globalizer global = null ;
	
	Tax2EnsMapper mapper = null ;
	
	private static Log log = LogFactory.getLog(ApplicationController.class);
	
	public ApplicationController() {
		
		loader = ModelLoader.getInstance() ;
		
		global = new Globalizer(loader.getInputModel(), loader.getOutputModel()) ;
		builder = new EntityBuilder( loader.getInputModel() ) ;
		okkam = new OkkamClient( confpath ) ;
		service = new ServiceClient( okkam ) ;
	}
	
	public String createEntity(RDFNode node) {
		
		String result = "" ; // okkamid
		
		ProfileType profile = builder.buildEntityProfile( node ) ;
		
		boolean ignoreDuplicates = true ;
		
		
		result = service.createNewEntity(profile.getSemanticType(), 
				profile.getAttributes(), ignoreDuplicates) ;
				
		
		return result ;
	}
	
	/*
	 * Create entities from the input model. Returns a map of blank node / URI
	 * pairs.
	 * 
	 */
	public Map<String, String> createEntities() {
		Map<String, String> result = new HashMap<String, String>() ;
		Set<RDFNode> distSubjs = global.getDistinctSubjects() ;		
		Iterator<RDFNode> idistSubj = distSubjs.iterator() ;
		while(idistSubj.hasNext()) {
			RDFNode distSubj = idistSubj.next() ;
						
			String okkamid = createEntity(distSubj) ;
			result.put(distSubj.toString(), okkamid) ;
			
			log.info("New entity's okkam id: " + okkamid) ;		
		}
		
		return result ;
		
	}
	
	

}
