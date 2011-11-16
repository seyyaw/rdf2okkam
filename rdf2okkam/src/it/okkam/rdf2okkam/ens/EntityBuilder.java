package it.okkam.rdf2okkam.ens;

import it.okkam.rdf2okkam.parser.VocabConstants;

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
	
	
	private final String ENS_LOCATION = VocabConstants.ensNS + "location" ;
	private final String ENS_PERSON = VocabConstants.ensNS + "person" ;
	
	Model _model = null ;
	
	private static Log log = LogFactory.getLog(EntityBuilder.class);
	
	public EntityBuilder(Model model) {
		_model = model ;
	}
	
	EntityBuildContext context = null ;
	
	public ProfileType buildEntityProfile(RDFNode subjectNode) {
		
		Resource subject = subjectNode.asResource() ;
		
		context = new EntityBuildContext( _model ) ;
		
		String subjectType = getType( subjectNode ) ;
		
		if( ENS_LOCATION.equals( subjectType ) ) {
			//if subject is a location
			context.setLocationEntityBuilding() ;
		
		}
		if( "http://localhost/TaxOntology.owl#Location".equals( subjectType ) ) {
			//if subject is a location
			context.setLocationEntityBuilding() ;
		
		}
		
		if( "http://localhost/TaxOntology.owl#LuogoResidenza".equals( subjectType ) ) {
			//if subject is a location
			context.setLocationEntityBuilding() ;
		
		}		
		
		if( "http://localhost/TaxOntology.owl#LuogoNascita".equals( subjectType ) ) {
			//if subject is a location
			context.setLocationEntityBuilding() ;
		
		}
		
		
		if( ENS_PERSON.equals( subjectType ) ) {
			//if subject is a person
			context.setPersonEntityBuilding() ;
		}
		if( "http://localhost/TaxOntology.owl#Persona_Fisica".equals( subjectType ) ) {
			//if subject is a location
			context.setPersonEntityBuilding() ;
		
		}
		
		log.info(subject + " is a " + subjectType ) ;
		
		return context.getProfile(subjectNode) ;
		
	}

	
	public String getType(RDFNode subjectNode) {
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
