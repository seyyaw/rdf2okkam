package it.okkam.rdf2okkam.parser;

import static org.junit.Assert.*;

import it.okkam.rdf2okkam.ens.client.EnsQuery;
import it.okkam.rdf2okkam.exception.SameNodeException;
import it.okkam.rdf2okkam.model.ModelLoader;
import it.okkam.rdf2okkam.model.Tax2EnsMapper;
import it.okkam.rdf2okkam.parser.RdfUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.okkam.client.data.AttributeType;
import org.okkam.client.data.AttributesType;


import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RdfUtilTest extends TestCase {
	
	RdfUtil parser = null;
	ModelLoader loader = null ;
	Tax2EnsMapper mapper = null ;
	EnsQuery query = null ;
	
	private static Log log = LogFactory.getLog(RdfUtilTest.class);
	
	public RdfUtilTest(String testName) {
		super(testName) ;
	}
	
	@Before
	public void setUp() throws Exception {
		loader = ModelLoader.getInstance() ;
		mapper = new Tax2EnsMapper() ; //makes mapping inferences		
		mapper.startInference( ModelLoader.getInstance().getInputModel() ) ;
		parser = new RdfUtil(loader.getInputModel(), loader.getOutputModel() );
		query = new EnsQuery() ;
	}
	
	@Test
	public void testListEnsClasses(){
		log.debug("---------------Test listEnsClasses()----------------") ;
		List<QName> classList = parser.listEnsClasses();
		log.debug("Number of classes: " + classList.size());
		Iterator<QName> iclass = classList.iterator();
		while(iclass.hasNext()){
			log.debug("Class: " + iclass.next().getLocalPart());
		}
		
	}
	
	@Test
	public void testListSubjectByType(){
		log.debug("---------------Test listSubjectByType()----------------") ; 
		List<Resource> subjects = parser.listSubjectsByType("person");
		Iterator<Resource> i = subjects.iterator();
		while(i.hasNext()){
			Resource subject = i.next();
			log.debug("Subject: " + subject.toString());			
		}
		
	}
	
	@Test
	public void testGetType(){
		log.debug("---------------Test getType()----------------") ;
		//SString subjectUri = "http://models.okkam.org/anagrafe.owl#2048";
		String subjectId = "_:node16273n1dhx1" ;
		QName q = parser.getType(subjectId);
		log.debug(subjectId + "'s type : " + q.getLocalPart());
		
	}
	
	@Test
	public void testListProperties(){
		log.debug("---------------Test listProperties()----------------") ;
		String subjectUri = "http://models.okkam.org/anagrafe.owl#2048";
		log.debug("Attributes for : " + subjectUri);
		AttributesType attributesType = parser.listProperties(subjectUri);
		List<AttributeType> attributes = attributesType.getAttributes();
		Iterator<AttributeType> i = attributes.iterator();
		while(i.hasNext()){
			AttributeType attribute = i.next();
			String attributeName = attribute.getName().getLocalPart();
			String prefix = attribute.getName().getPrefix();
			String attributeValue = attribute.getValue();
			log.debug("Attribute: " + attributeName + ", prefix: " + prefix + ", value: " + attributeValue);
		}
		
		
		
	}
	
	@Test
	public void testListSubjectProperties(){
		log.debug("---------------Test listSubjectProperties()----------------") ;
		Set<RDFNode> subjects = parser.getSubjectsWithoutBNodes() ;
		Iterator<RDFNode> isub = subjects.iterator() ;
		while(isub.hasNext()) {			
			RDFNode subjectNode = isub.next() ;			
			log.debug("Attributes for : " + subjectNode.toString());
			AttributesType attributesType = parser.listSubjectProperties(subjectNode);
			log.debug( query.getQuery(attributesType, "semantic type") );
			List<AttributeType> attributes = attributesType.getAttributes();
			Iterator<AttributeType> i = attributes.iterator();
			while(i.hasNext()){
				AttributeType attribute = i.next();
				String attributeName = attribute.getName().getLocalPart();
				String prefix = attribute.getName().getPrefix();
				String attributeValue = attribute.getValue();
				log.debug("Attribute: " + attributeName + ", prefix: " + prefix + ", value: " + attributeValue);
			}
			
		}
		
	}
	

	
	@Test
	public void testListEnsDatatypeProperties(){
		log.debug("---------------Test listEnsDataTypeProperties()----------------") ;
		List<QName> ensProperties = parser.listEnsDatatypeProperties();
		Iterator<QName> i = ensProperties.iterator();
		while(i.hasNext()){
			QName q = i.next();
			log.debug("Namespace: " + q.getNamespaceURI() + ", prefix: " + q.getPrefix() + ", local part: " + q.getLocalPart());
		}
	}
	
	@Test
	public void testGetQName(){
		String uri = "http://models.okkam.org/ENS-core-vocabulary.owl#first_name";
		QName q = parser.getQName(uri);
		log.debug("Namespace: " + q.getNamespaceURI() + ", prefix: " + q.getPrefix() + ", local part: " + q.getLocalPart());
	}
	
	@Test
	public void testListSubjects() {
		log.debug("---------------Test listSubjects()----------------") ;
		ResIterator ires = loader.getInputModel().listSubjects() ;
		while(ires.hasNext()) {
			Resource res = ires.next() ;
			log.debug("Subject resourec: " + res.getURI()) ;
		}
		
		List<RDFNode> resources = parser.listSubjects() ;
		Iterator<RDFNode> i = resources.iterator() ;
		int counter = 0 ;
		while(i.hasNext()) {
			RDFNode subject = i.next();
			counter++ ;
			if(subject.isURIResource()) {
				log.debug(counter + ") subject: " + subject.toString() + " is URI resource") ;
			}
			if(subject.isAnon()) {
				log.debug(counter + ") subject: " + subject.toString() + " is blank node") ;
			}
			
		}
	}
	
	@Test
	public void testAllPropertiesWithoutBNodes() {
		List<RDFNode> resources = parser.listSubjects() ;
		Iterator<RDFNode> i = resources.iterator() ;
		while(i.hasNext()) {
			RDFNode subject = i.next();						
			log.debug("Subject: " + subject.toString() + " all properties with BNodes: " + parser.allPropertiesWithoutBNodes(subject)) ;			
										
		}
	}
	
	@Test
	public void testGetDistinctSubjects() {
		log.debug("---------------Test getDistincSubjects()----------------") ;
		
		Set<RDFNode> subjects = parser.getDistinctSubjects() ;
		log.debug("Number of subjects: " + subjects.size()) ;		
		
		Iterator<RDFNode> i = subjects.iterator() ;
		while(i.hasNext()) {
			RDFNode subject = i.next() ;
		
			log.debug("subject: " + subject.toString()) ;
		}
		
	}
	
	
	@Test 
	public void testGetSubjectsWithoutBNodes() {
		log.debug("---------------Test getSubjectsWithoutBNodes()----------------") ;
		Set<RDFNode> subjects = parser.getSubjectsWithoutBNodes() ;
		log.debug("Number of subjects without blank nodes: " + subjects.size()) ;
		Iterator<RDFNode> i = subjects.iterator() ;
		int count = 0 ;
		while(i.hasNext()) {
			RDFNode subject = i.next() ;
			count++ ;
			log.debug(count + ") subject: " + subject.toString()) ;
		}
	}
	
	@Test
	public void testGetStrictSubject() {
		log.debug("---------------Test getStrictSubjects()----------------") ;
		Set<RDFNode> subjects = parser.getStrictSubjects() ;
		log.debug("Number of strict subject node: " + subjects.size()) ;
		Iterator<RDFNode> i = subjects.iterator() ;
		int count = 0 ;
		while(i.hasNext()) {
			RDFNode subject = i.next() ;
			count++ ;
			log.debug(count + ") strict subject node: " + subject.toString()) ;
			AttributesType attributesType = parser.listSubjectProperties(subject);
			List<AttributeType> attributes = attributesType.getAttributes();
			Iterator<AttributeType> iattributes = attributes.iterator() ;						
			while(iattributes.hasNext()){
				AttributeType attribute = iattributes.next();
				String attributeName = attribute.getName().getLocalPart();
				String prefix = attribute.getName().getPrefix();
				String attributeValue = attribute.getValue();
				log.debug("Attribute: " + attributeName + ", prefix: " + prefix + ", value: " + attributeValue);
			}
		}
		
	}
	
	@Test
	public void testGetNotStrictSubjects() {
		log.debug("---------------Test getNotStrictSubjects()----------------") ;
		Set<RDFNode> subjects = parser.getNotStrictSubjects() ;
		log.debug("Number of not strict subject node: " + subjects.size()) ;
		Iterator<RDFNode> i = subjects.iterator() ;
		int count = 0 ;
		while(i.hasNext()) {
			RDFNode subject = i.next() ;
			count++ ;
			log.debug(count + ") not strict subject node: " + subject.toString()) ;
			AttributesType attributesType = parser.listSubjectProperties(subject);
			List<AttributeType> attributes = attributesType.getAttributes();
			Iterator<AttributeType> iattributes = attributes.iterator() ;						
			while(iattributes.hasNext()){
				AttributeType attribute = iattributes.next();
				String attributeName = attribute.getName().getLocalPart();
				String prefix = attribute.getName().getPrefix();
				String attributeValue = attribute.getValue();
				log.debug("Attribute: " + attributeName + ", prefix: " + prefix + ", value: " + attributeValue);
			}
		}
		
	}
	
	@Test
	public void testCompareSubjects() {
		log.debug("---------------Test compareSubjects()----------------") ;
		final String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#" ; 
		Property subj1Predicate = loader.getInputModel().getProperty(ensNS + "location_name") ;		
		Selector selector1 = new SimpleSelector(null, subj1Predicate, "FOLGARIA" ) ;
		StmtIterator isubj1 = loader.getInputModel().listStatements(selector1) ;
		while(isubj1.hasNext()) {
			Statement stmt1 = isubj1.next() ;
			RDFNode subj1 = stmt1.getSubject() ;
			Selector selector2 = new SimpleSelector(null, subj1Predicate, (RDFNode) null) ;
			StmtIterator isubj2 = loader.getInputModel().listStatements(selector2) ;
			while(isubj2.hasNext()) {
				Statement stmt2 = isubj2.next() ;
				RDFNode subj2 = stmt2.getSubject() ;
				
				if( parser.compareSubjects(subj1, subj2) ) 
					log.debug( stmt1.toString() + " same as " + stmt2.toString()) ; 
				else
					log.debug( stmt1.toString() + " different from " + stmt2.toString()) ;
				 
			}
		}
						
	}
	
	@Test
	public void testGetDuplicateSubjects() {
		log.debug("---------------Test getDuplicateSubjects()----------------") ;
		final String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#" ; 
		Property subj1Predicate = loader.getInputModel().getProperty(ensNS + "location_name") ;		
		Selector selector1 = new SimpleSelector(null, subj1Predicate, "FOLGARIA" ) ;
		StmtIterator isubj1 = loader.getInputModel().listStatements(selector1) ;
		Set<RDFNode> duplicates = null ;
		RDFNode subj1 = isubj1.next().getSubject() ;		
		Statement stmt1 = isubj1.next() ;					
		duplicates = parser.getDuplicateSubjects(subj1) ;				
		Iterator<RDFNode> i = duplicates.iterator() ;
		while(i.hasNext()) {
			RDFNode duplicate = i.next() ;
			log.debug(subj1 + " has " + duplicate + " as a duplicate resource.") ;
		}
		
	}
	
//	@Test
//	public void testRemoveDuplicates() {
//		log.debug("---------------Test removeDuplicateS()----------------") ;		
//		RDFNode subj1 = getSubject() ;
//		Model resultModel = parser.removeDuplicates(subj1) ;
//		resultModel.write(System.out, "TURTLE") ;
//		
//	}
	
	@Test
	public void testGlobalizeIdentifier() {
		log.debug("---------------Test globalizeIdentifier()----------------") ;
		Set<RDFNode> bnodes = parser.getNotStrictSubjects() ;
		Iterator<RDFNode> inode = bnodes.iterator() ;
		while (inode.hasNext()) {
			RDFNode bnode = inode.next() ;			
			int idlength = bnode.toString().length() ;
			String uri = "http://okkam.org/ens/" + bnode.toString().substring(idlength - 4) ;
			log.debug("Bnode: " + bnode.toString() + " URI: " + uri ) ;
			parser.globalizeIdentifier(bnode, uri) ;
		}
		
		log.debug("---updated model---") ;		
		loader.getOutputModel().write(System.out, "TURTLE") ;			
		
	}
	
//	@Test 
//	public void testIsEntity() {
//		RDFNode node = getSubject() ;				
//		
////		if(parser.isEntity(node)) 
////			log.debug(node + " is already a recognized entity.") ;
////		else 
////			log.debug(node + " is not a recognized entity.") ;
//		
//		loader.writeOutputModel() ;
//	}
	
	private RDFNode getSubject() {
		RDFNode subject = null ;				
		Property subj1Predicate = loader.getInputModel().getProperty(VocabConstants.ensNS + "location_name") ;		
		Selector selector1 = new SimpleSelector(null, subj1Predicate, "FOLGARIA" ) ;
		StmtIterator isubj1 = loader.getInputModel().listStatements(selector1) ;
		Statement stmt = isubj1.next() ;
		subject = stmt.getSubject() ;
		return subject ;
	}

}
