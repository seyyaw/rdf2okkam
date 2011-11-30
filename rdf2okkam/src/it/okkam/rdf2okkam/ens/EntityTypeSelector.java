package it.okkam.rdf2okkam.ens;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.okkam.rdf2okkam.parser.VocabConstants;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class EntityTypeSelector {
	
	EnsEntityFactory factory = null;
	
	Model _model = null ;
	
	private static Log log = LogFactory.getLog(EntityTypeSelector.class);
	
	public EntityTypeSelector(Model model) {
		_model = model ;
	}
	
	public EnsEntityFactory selectEntityFactory(RDFNode node) {
		
		String entityType = getEntityType( node ) ;
		
		if(VocabConstants.ENS_PERSON.equals(entityType)){
			factory = new EnsPersonFactory();
		}
		
		if(VocabConstants.ENS_LOCATION.equals(entityType)){
			factory = new EnsLocationFactory();
		}
		
		if(VocabConstants.ENS_EVENT.equals(entityType)){
			factory = new EnsEventFactory();
		}
		
		if(VocabConstants.ENS_OTHER.equals(entityType)){
			factory = new EnsOtherFactory();
		}
		
		return factory;
	
	}
	
	private String getEntityType(RDFNode subjectNode) {
		String result = null ;
		Property rdfType = ResourceFactory.createProperty(VocabConstants.rdfNS + "type") ;
		Selector selector = new SimpleSelector(subjectNode.asResource(), rdfType, (RDFNode) null) ;
		StmtIterator istmt = _model.listStatements(selector) ;
		while(istmt.hasNext()) {
			Statement stmt = istmt.next() ;
			RDFNode object = stmt.getObject() ;
			
			result = object.asResource().getURI() ;
		}
		
		return result ;
	}

}
