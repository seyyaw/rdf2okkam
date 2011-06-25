package org.okkam.ens.client;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.okkam.client.data.AttributesType;
import org.okkam.dataset.parser.RdfParser;

import org.okkam.model.ModelLoader;
import org.okkam.service.client.OkkamClient;
import org.okkam.service.client.ServiceClient;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;



public class ServiceClientTest {
	private final String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String filename = "resources/anagrafe1.ttl";
	private static final String confpath = "conf";
	ModelLoader loader = null ;
	RdfParser parser = null;
	OkkamClient okkam = null;
	ServiceClient client = null;
	AttributesType attributesType = null;
	

	@Before
	public void setUp() throws Exception {
		
		loader = new ModelLoader() ;		
		loader.loadInputModel(filename) ; 
		parser = new RdfParser(loader.getInputModel());
		okkam = new OkkamClient(confpath) ;
		client = new ServiceClient(okkam) ;		
	    		
	}

//	@Test
//	public void testCreateNewEntity() {
//		String subjectUri = "" ; 	
//		String semanticType = parser.getType(subjectUri).getLocalPart();
//		attributesType = parser.listProperties(subjectUri);
//		boolean ignoreDuplicates = true ;
//		String okkamid = client.createNewEntity(semanticType, attributesType, ignoreDuplicates);
//		System.out.println("Identifier for " + subjectUri + ": " + okkamid);
//		
//	}
	
	@Test
	public void testCreateNewEntity(){
		System.out.println("----------------Test createNewEntities()-----------------") ;
		Set<RDFNode> subjects = parser.getSubjectsWithoutBNodes() ;
		Iterator<RDFNode> i = subjects.iterator();
		boolean ignoreDuplicates = true ;
		while(i.hasNext()){
			RDFNode subject = i.next();
			System.out.println("subject " + subject.toString()) ;
			Property typep = loader.getInputModel().getProperty(rdfNS + "type") ;
			String semanticType = "location" ;
			System.out.println("subject: " + subject.toString() + ", sem. type: " + semanticType) ;
			attributesType = parser.listSubjectProperties(subject);
			String okkamid = client.createNewEntity(semanticType, attributesType, ignoreDuplicates);
			System.out.println("Identifier for " + subject.asResource().getURI() + ": " + okkamid);
			
		}
	}

}
