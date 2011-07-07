package org.okkam.ens.client;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.okkam.client.data.AttributesType;
import org.okkam.dataset.parser.RdfParser;

import org.okkam.service.client.OkkamClient;
import org.okkam.service.client.ServiceClient;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;



public class ServiceClientTest {
	
	private static final String filename = "resources/anagrafe1.ttl";
	private static final String confpath = "conf";
	RdfParser parser = null;
	OkkamClient okkam = null;
	ServiceClient client = null;
	AttributesType attributesType = null;
	

	@Before
	public void setUp() throws Exception {
		parser = new RdfParser(filename);
		okkam = new OkkamClient(confpath) ;
		client = new ServiceClient(okkam) ;
		// Get the logger for the test class
	    
		
	}

	@Test
	public void testCreateNewEntity() {
		String subjectUri = "http://spreadsheet.number001#4";	
		String semanticType = parser.getType(subjectUri).getLocalPart();
		attributesType = parser.listProperties(subjectUri);
		boolean ignoreDuplicates = false ;
		String okkamid = client.createNewEntity(semanticType, attributesType, ignoreDuplicates);
		System.out.println("Identifier for " + subjectUri + ": " + okkamid);
		
	}
	
	@Test
	public void testCreateNewEntities(){
		List<RDFNode> subjects = parser.listSubjects() ;
		Iterator<RDFNode> i = subjects.iterator();
		boolean ignoreDuplicates = false ;
		while(i.hasNext()){
			RDFNode subject = i.next();
			if(parser.allPropertiesWithoutBNodes(subject)) {				
				String semanticType = parser.getType(subject.asResource()).getLocalPart();
				attributesType = parser.listProperties(subject.asResource().getURI());
				String okkamid = client.createNewEntity(semanticType, attributesType, ignoreDuplicates);
				System.out.println("Identifier for " + subject.asResource().getURI() + ": " + okkamid);
			}
		}
	}

}
