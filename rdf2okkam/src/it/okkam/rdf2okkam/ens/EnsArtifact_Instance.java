package it.okkam.rdf2okkam.ens;

import it.okkam.rdf2okkam.ens.client.EnsQuery;
import it.okkam.rdf2okkam.parser.VocabConstants;

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

public class EnsArtifact_Instance implements EnsEntity {

	private Model _model ;
	
	private RDFNode _subjectNode ;
	
	private static Log log = LogFactory.getLog(EnsArtifact_Instance.class);
	
	public EnsArtifact_Instance(Model model, RDFNode node){
		
		_model = model ;
		_subjectNode = node ;
	}
	
	@Override
	public String getSemanticType() {
		
		return SemanticType.ARTIFACT_INSTANCE ;
	}
	
	/*
	 * Returns person's list of attributes from the model
	 */
	public AttributesType getAttributesType() {
		
		AttributesType attributes =  new AttributesType();
		Resource subject = _subjectNode.asResource() ;
		Property rdfType = ResourceFactory.createProperty(VocabConstants.rdfNS + "type") ;
		Statement typeStmt = subject.getProperty(rdfType) ;
		Resource typeObj = typeStmt.getObject().asResource() ;
		
		Selector selector = new SimpleSelector(subject, null, (RDFNode)null) ;
		StmtIterator i = _model.listStatements(selector) ;	
		
		while(i.hasNext()){	
			String prefix = "";
			Statement stmt = i.next();			
			AttributeType attribute = new AttributeType();
			Property predicate = stmt.getPredicate();
			
			// set the prefix
			if( VocabConstants.ensNS.equals( predicate.getNameSpace() )){
				prefix = VocabConstants.ENS_PREFIX ;
			}
			if( VocabConstants.taxNS.equals( predicate.getNameSpace() )){
				prefix = VocabConstants.TAX_PREFIX ;
			}
			if( VocabConstants.rdfNS.equals( predicate.getNameSpace() )){
				prefix = VocabConstants.RDF_PREFIX ;
			}
			
			// select only predicates used as entity attributes
			if(isAttribute(predicate)) {
				if (prefix.equals("")){ //if the attribute was not one of the ontology term 
					predicate=ResourceFactory.createProperty(predicate.toString().replace("+", "_"));	
				}
				QName name = new QName("", predicate.getLocalName() , prefix);	
				
				attribute.setName(name);			
				String value = stmt.getObject().toString();
				//System.out.println("value: " + value) ;
				attribute.setValue(value);
				
				// Set access control metadata to send or not attributes values to the public node
				// Set to "private" to not send the attributes values.
				// attribute.getMetadata().getAccessControl().setDisplayable("private");
				
				attributes.getAttributes().add(attribute);
			}
	
		}
			
			
		
		return attributes ;
	}


	/*
	 * Returns person's profile from the model
	 */
	@Override
	public ProfileType getProfile() {
		ProfileType profile = new ProfileType() ;
		
		profile.setAttributes(getAttributesType()) ;
		profile.setSemanticType(getSemanticType()) ;
		
		return profile;	
	}
	
	public String getQuery() {
		
		return EnsQuery.getQuery(getAttributesType(), getSemanticType()) ; 
	}
	
	/*
	 *  Select predicates that are entity's attribute. If a predicate is not an entity
	 *  attribute the method returns false.
	 */
	
	private boolean isAttribute(Property predicate) {
		boolean result = true ;
		
		Property rdfType = ResourceFactory.createProperty(VocabConstants.rdfNS + "type") ;
		
		if (rdfType.getLocalName().equals(predicate.getLocalName()))
			result = false ; 
		
		return result ;		
	}


}
