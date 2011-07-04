package org.okkam.dataset.parser;

import javax.xml.namespace.QName;

import org.okkam.client.data.AttributeType;
import org.okkam.client.data.AttributesType;

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
	
	public Person(Model model){
		_model = model ;
	}

	@Override
	public AttributesType getProperties(RDFNode subjectNode) {
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
			//System.out.println("predicate: " + predicate.getURI());
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
			String value = stmt.getObject().toString();
			//System.out.println("value: " + value) ;
			attribute.setValue(value);
			
			// Set access control metadata to send or not attributes values to the public node
			// Set to "private" to not send the attributes values.
			// attribute.getMetadata().getAccessControl().setDisplayable("private");			
			
			attributes.getAttributes().add(attribute);	
			
		}
		
		return attributes;		

	}

}
