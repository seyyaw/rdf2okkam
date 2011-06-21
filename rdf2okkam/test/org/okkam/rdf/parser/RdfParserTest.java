package org.okkam.rdf.parser;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.okkam.client.data.AttributeType;
import org.okkam.client.data.AttributesType;
import org.okkam.dataset.parser.RdfParser;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class RdfParserTest {
	
	String filename = "resources/anagrafe1.ttl";
	RdfParser parser = null;

	@Before
	public void setUp() throws Exception {
		
		parser = new RdfParser(filename);
	}
	
	@Test
	public void testListEnsClasses(){
		
		List<QName> classList = parser.listEnsClasses();
		System.out.println("Number of classes: " + classList.size());
		Iterator<QName> iclass = classList.iterator();
		while(iclass.hasNext()){
			System.out.println("Class: " + iclass.next().getLocalPart());
		}
		
	}
	
	@Test
	public void testListSubjectByType(){
		 
		List<Resource> subjects = parser.listSubjectsByType("person");
		Iterator<Resource> i = subjects.iterator();
		while(i.hasNext()){
			Resource subject = i.next();
			System.out.println("Subject: " + subject.toString());			
		}
		
	}
	
	@Test
	public void testGetType(){
		//SString subjectUri = "http://models.okkam.org/anagrafe.owl#2048";
		String subjectUri = "43f03245:1308ecd9c6b:3110" ;
		QName q = parser.getType(subjectUri);
		System.out.println(subjectUri + "'s type : " + q.getLocalPart());
		
	}
	
	@Test
	public void testListProperties(){
		String subjectUri = "http://models.okkam.org/anagrafe.owl#2048";
		System.out.println("Attributes for : " + subjectUri);
		AttributesType attributesType = parser.listProperties(subjectUri);
		List<AttributeType> attributes = attributesType.getAttributes();
		Iterator<AttributeType> i = attributes.iterator();
		while(i.hasNext()){
			AttributeType attribute = i.next();
			String attributeName = attribute.getName().getLocalPart();
			String prefix = attribute.getName().getPrefix();
			String attributeValue = attribute.getValue();
			System.out.println("Attribute: " + attributeName + ", prefix: " + prefix + ", value: " + attributeValue);
		}
		
	}
	

	
	@Test
	public void testListEnsDatatypeProperties(){
		List<QName> ensProperties = parser.listEnsDatatypeProperties();
		Iterator<QName> i = ensProperties.iterator();
		while(i.hasNext()){
			QName q = i.next();
			System.out.println("Namespace: " + q.getNamespaceURI() + ", prefix: " + q.getPrefix() + ", local part: " + q.getLocalPart());
		}
	}
	
	@Test
	public void testGetQName(){
		String uri = "http://models.okkam.org/ENS-core-vocabulary.owl#first_name";
		QName q = parser.getQName(uri);
		System.out.println("Namespace: " + q.getNamespaceURI() + ", prefix: " + q.getPrefix() + ", local part: " + q.getLocalPart());
	}
	
	@Test
	public void testListSubjects() {
		List<RDFNode> resources = parser.listSubjects() ;
		Iterator<RDFNode> i = resources.iterator() ;
		while(i.hasNext()) {
			RDFNode subject = i.next();
			if(subject.isURIResource()) {
				System.out.println("Subject: " + subject.toString() + " is URI resource") ;
			}
			if(subject.isResource()) {
				System.out.println("Subject: " + subject.toString() + " is blank node") ;
			}
		}
	}
	
	@Test
	public void testAllPropertiesWithoutBNodes() {
		List<RDFNode> resources = parser.listSubjects() ;
		Iterator<RDFNode> i = resources.iterator() ;
		while(i.hasNext()) {
			RDFNode subject = i.next();						
			System.out.println("Subject: " + subject.toString() + " all properties with BNodes: " + parser.allPropertiesWithoutBNodes(subject)) ;			
										
		}
	}
	

}
