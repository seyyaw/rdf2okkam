package it.okkam.rdf2okkam.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import it.okkam.rdf2okkam.ens.EnsEntity;
import it.okkam.rdf2okkam.ens.EnsEntityFactory;
import it.okkam.rdf2okkam.ens.EntityTypeSelector;
import it.okkam.rdf2okkam.ens.client.OkkamClient;
import it.okkam.rdf2okkam.ens.client.ServiceClient;
import it.okkam.rdf2okkam.model.ModelLoader;
import it.okkam.rdf2okkam.model.Tax2EnsMapper;
import it.okkam.rdf2okkam.parser.RdfUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ApplicationController {

	
	private final String confpath = "conf";
	
	Model _model = null ;
		
	EnsEntityFactory factory = null;
	
	OkkamClient okkam = null;
	
	ServiceClient service = null ;
	
	ModelLoader loader = null ;
	
	EntityTypeSelector selector = null;
	
	Tax2EnsMapper mapper = null ;
	
	RdfUtil rdfutil = null;
	
	private static Log log = LogFactory.getLog(ApplicationController.class);
	
	public ApplicationController() {
		
		loader = ModelLoader.getInstance() ; 
		okkam = new OkkamClient( confpath ) ;
		service = new ServiceClient( okkam ) ;
		_model = ModelLoader.getInstance().getInputModel();
		rdfutil = new RdfUtil(_model);
		selector = new EntityTypeSelector(_model) ;
	}
	
	/*
	 * Create entities from the input model. 
	 * 
	 */
	public void createEntities() {
		Map<String, String> result = new HashMap<String, String>() ;
		Set<RDFNode> distSubjs = rdfutil.getDistinctSubjects() ;		
		Iterator<RDFNode> idistSubj = distSubjs.iterator() ;
		while(idistSubj.hasNext()) {
			RDFNode subject = idistSubj.next() ;					
			String okkamid = createEntity(subject) ;
			result.put(subject.toString(), okkamid) ;
			
			log.info("New entity's okkam id: " + okkamid) ;		
		}		
		
	}
	
	public String createEntity(RDFNode node) {
		
		String result = "" ; // okkamid
		
		factory = selector.selectEntityFactory(node);
		
		EnsEntity entity = factory.createEnsEntity(_model, node);
		
		
		
		boolean ignoreDuplicates = true ;
		
		
		result = service.createNewEntity(entity.getSemanticType(), 
				entity.getAttributesType(), ignoreDuplicates) ;
				
		
		return result ;
	}
	
	
	
	

}
