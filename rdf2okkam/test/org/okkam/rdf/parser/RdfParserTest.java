package org.okkam.rdf.parser;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.okkam.client.data.AttributeType;
import org.okkam.client.data.AttributesType;
import org.okkam.dataset.parser.RdfParser;
import org.okkam.model.ModelLoader;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class RdfParserTest {
	
	String fileName = "resources/anagrafe1.ttl";
	RdfParser parser = null;
	ModelLoader loader = null ;

	@Before
	public void setUp() throws Exception {
		loader = new ModelLoader() ;
		loader.loadInputModel(fileName) ;
		parser = new RdfParser(loader.getInputModel());
	}
	
	@Test
	public void testListEnsClasses(){
		System.out.println("---------------Test listEnsClasses()----------------") ;
		List<QName> classList = parser.listEnsClasses();
		System.out.println("Number of classes: " + classList.size());
		Iterator<QName> iclass = classList.iterator();
		while(iclass.hasNext()){
			System.out.println("Class: " + iclass.next().getLocalPart());
		}
		
	}
	
	@Test
	public void testListSubjectByType(){
		System.out.println("---------------Test listSubjectByType()----------------") ; 
		List<Resource> subjects = parser.listSubjectsByType("person");
		Iterator<Resource> i = subjects.iterator();
		while(i.hasNext()){
			Resource subject = i.next();
			System.out.println("Subject: " + subject.toString());			
		}
		
	}
	
	@Test
	public void testGetType(){
		System.out.println("---------------Test getType()----------------") ;
		//SString subjectUri = "http://models.okkam.org/anagrafe.owl#2048";
		String subjectId = "_:node16273n1dhx1" ;
		QName q = parser.getType(subjectId);
		System.out.println(subjectId + "'s type : " + q.getLocalPart());
		
	}
	
	@Test
	public void testListProperties(){
		System.out.println("---------------Test listProperties()----------------") ;
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
		
		System.out.println();
		
	}
	
	@Test
	public void testListSubjectProperties(){
		System.out.println("---------------Test listSubjectProperties()----------------") ;
		Set<RDFNode> subjects = parser.getSubjectsWithoutBNodes() ;
		Iterator<RDFNode> isub = subjects.iterator() ;
		while(isub.hasNext()) {			
			RDFNode subjectNode = isub.next() ;			
			System.out.println("Attributes for : " + subjectNode.toString());
			AttributesType attributesType = parser.listSubjectProperties(subjectNode);
			List<AttributeType> attributes = attributesType.getAttributes();
			Iterator<AttributeType> i = attributes.iterator();
			while(i.hasNext()){
				AttributeType attribute = i.next();
				String attributeName = attribute.getName().getLocalPart();
				String prefix = attribute.getName().getPrefix();
				String attributeValue = attribute.getValue();
				System.out.println("Attribute: " + attributeName + ", prefix: " + prefix + ", value: " + attributeValue);
			}
			System.out.println() ;
		}
		
	}
	

	
	@Test
	public void testListEnsDatatypeProperties(){
		System.out.println("---------------Test listEnsDataTypeProperties()----------------") ;
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
		System.out.println("---------------Test listSubjects()----------------") ;
		List<RDFNode> resources = parser.listSubjects() ;
		Iterator<RDFNode> i = resources.iterator() ;
		int counter = 0 ;
		while(i.hasNext()) {
			RDFNode subject = i.next();
			counter++ ;
			if(subject.isURIResource()) {
				System.out.println(counter + ") subject: " + subject.toString() + " is URI resource") ;
			}
			if(subject.isAnon()) {
				System.out.println(counter + ") subject: " + subject.toString() + " is blank node") ;
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
	
	@Test
	public void testGetDistinctSubjects() {
		System.out.println("---------------Test getDistincSubjects()----------------") ;
		Set<RDFNode> subjects = parser.getDistinctSubjects() ;
		System.out.println("Number of subjects: " + subjects.size()) ;		
		
		Iterator<RDFNode> i = subjects.iterator() ;
		while(i.hasNext()) {
			RDFNode subject = i.next() ;
		
			System.out.println("subject: " + subject.toString()) ;
		}
		
	}
	
	@Test 
	public void testGetSubjectsWithoutBNodes() {
		System.out.println("---------------Test getSubjectsWithoutBNodes()----------------") ;
		Set<RDFNode> subjects = parser.getSubjectsWithoutBNodes() ;
		System.out.println("Number of subjects without blank nodes: " + subjects.size()) ;
		Iterator<RDFNode> i = subjects.iterator() ;
		int count = 0 ;
		while(i.hasNext()) {
			RDFNode subject = i.next() ;
			count++ ;
			System.out.println(count + ") subject: " + subject.toString()) ;
		}
	}
	

}
