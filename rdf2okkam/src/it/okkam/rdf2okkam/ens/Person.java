package it.okkam.rdf2okkam.ens;


import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.okkam.client.data.AttributeType;
import org.okkam.client.data.AttributesType;
import org.okkam.client.data.ProfileType;
import org.okkam.core.data.api.SemanticType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class Person implements EntityBuildStrategy {
	
	private final String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#";
	private final String ENS_LOCATION = ensNS + "location" ;
	private final String taxNS = "http://localhost/TaxOntology.owl#" ;
	private final String TAX_PREFIX = "tax" ;
	private final String ENS_PREFIX = "ens";
	private final String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final String RDF_PREFIX = "rdf" ;
	
	
	private Model _model ;
	
	private static Log log = LogFactory.getLog(Person.class);
	
	public Person(Model model){
		_model = model ;
	}

	@Override
	public ProfileType getProperties(RDFNode subjectNode) {
		ProfileType profile = new ProfileType() ;
		AttributesType attributes =  new AttributesType();
		Resource subject = subjectNode.asResource() ;
		Property rdfType = ResourceFactory.createProperty(rdfNS + "type") ;
		Statement typeStmt = subject.getProperty(rdfType) ;
		Resource typeObj = typeStmt.getObject().asResource() ;
		
		Selector selector = new SimpleSelector(subject, null, (RDFNode)null) ;
		StmtIterator i = _model.listStatements(selector) ;	
		
		while(i.hasNext()){	
			String prefix = "";
			Statement stmt = i.next();			
			AttributeType attribute = new AttributeType();
			Property predicate = stmt.getPredicate();
			log.debug("predicate: " + predicate.getURI());
			if( ensNS.equals( predicate.getNameSpace() )){
				prefix = ENS_PREFIX ;
			}
			if( taxNS.equals( predicate.getNameSpace() )){
				prefix = TAX_PREFIX ;
			}
			if( rdfNS.equals( predicate.getNameSpace() )){
				prefix = RDF_PREFIX ;
			}
			QName name = new QName(predicate.getURI(), predicate.getLocalName() , prefix);
			attribute.setName(name);
			RDFNode object = stmt.getObject() ;
			if ( object.isAnon() ) {
				log.debug("virtual entity object is a blank node" + object.toString()) ;
				// look for the properties to be used to describe a person
				// follow a list of properties to search for
				// log.debug("predicate: " + predicate.getLocalName() + ". blank node value: " + object) ;
				String comune_nascita_prop = ensNS + "comune_nascita" ;
				String city_of_residence_prop = ensNS + "city_of_residence" ;
				if( comune_nascita_prop.equals(predicate.getURI()) || city_of_residence_prop.equals(predicate.getURI())) {
					Property location_name_prop = ResourceFactory.createProperty( ensNS + "location_name" ) ; 					
					Statement stmtprop = _model.getProperty(object.asResource(), location_name_prop) ;
					String location_name = stmtprop.getObject().toString() ;
					log.debug(predicate.getLocalName() + " = " + location_name) ;
					attribute.setValue( location_name ) ;
				}
				else
					attribute.setValue( object.toString() ) ;
			}
			else if (object.isLiteral()) {
				log.debug("virtual entity object literal value = " + object.toString() ) ;
				attribute.setValue( object.toString() );
			}
			else if (object.isURIResource()) {
				log.debug("virtual entity object is URI resource = " + object.toString() ) ;
				attribute.setValue( object.toString() );
			}
			else {
				log.debug("virtual entity object is not literal, uri or blank node = " + object.toString() ) ;
				attribute.setValue( object.toString() );
			}
			
			
			// Set access control metadata to send or not attributes values to the public node
			// Set to "private" to not send the attributes values.
			// attribute.getMetadata().getAccessControl().setDisplayable("private");			
			
			attributes.getAttributes().add(attribute);	
			profile.setAttributes(attributes) ;
			profile.setSemanticType(SemanticType.PERSON) ;
			
		}
		
		return profile;		

	}

}
