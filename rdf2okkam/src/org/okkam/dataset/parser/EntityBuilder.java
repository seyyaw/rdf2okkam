package org.okkam.dataset.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.okkam.client.data.AttributesType;
import org.okkam.client.data.ProfileType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class EntityBuilder {
	
	private final String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final String RDF_TYPE = rdfNS + "type" ;
	private final String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#";
	private final String ENS_LOCATION = ensNS + "location" ;
	private final String ENS_PERSON = ensNS + "person" ;
	
	Model _model = null ;
	
	private static Log log = LogFactory.getLog(EntityBuilder.class);
	
	public EntityBuilder(Model model) {
		_model = model ;
	}
	
	EntityBuildContext context = null ;
	
	public ProfileType buildEntity(RDFNode subjectNode) {
		
		Resource subject = subjectNode.asResource() ;
		
		context = new EntityBuildContext( _model ) ;
		
		String subjectType = getType( subjectNode ) ;
		
		if( ENS_LOCATION.equals( subjectType ) ) {
			//if subject is a location
			context.setLocationEntityBuilding() ;
		
		}
		if( ENS_PERSON.equals( subjectType ) ) {
			//if subject is a person
			context.setPersonEntityBuilding() ;
		}
		
		log.info(subject + " is a " + subjectType ) ;
		
		return context.getProperties(subjectNode) ;
		
	}
	
	private String getType(RDFNode subjectNode) {
		String result = null ;
		Property rdfType = ResourceFactory.createProperty(RDF_TYPE) ;
		Selector selector = new SimpleSelector(subjectNode.asResource(), rdfType, (RDFNode) null) ;
		StmtIterator istmt = _model.listStatements(selector) ;
		while(istmt.hasNext()) {
			Statement stmt = istmt.next() ;
			RDFNode object = stmt.getObject() ;
			if( ensNS.equals( object.asResource().getNameSpace() ) )
				result = object.asResource().getURI() ;
		}
		return result ;
	}

}
