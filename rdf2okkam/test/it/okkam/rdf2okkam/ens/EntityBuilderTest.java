package it.okkam.rdf2okkam.ens;

import static org.junit.Assert.*;

import it.okkam.rdf2okkam.ens.EntityBuilder;
import it.okkam.rdf2okkam.parser.Globalizer;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.okkam.client.data.AttributeType;
import org.okkam.client.data.AttributesType;
import org.okkam.client.data.ProfileType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileManager;

public class EntityBuilderTest extends TestCase {
	
	private final String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#";
	private final String ENS_LOCATION = ensNS + "location" ;
	private final String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final String RDF_TYPE = rdfNS + "type" ;
	String modelFileName = "resources/test/anagrafe_out.ttl" ;
	final String RDF_SYNTAX = "TURTLE" ;
	String baseUri = null ;
	Model _model = null ;
	Model _outModel = null ;
	EntityBuilder builder = null ;
	Globalizer global = null ;
	
	private static Log log = LogFactory.getLog(EntityBuilderTest.class);
	
	public EntityBuilderTest(String testName) {
		super(testName) ;
	}

	@Before
	public void setUp() throws Exception {
		
		// use the FileManager to find the input file
		InputStream inModelStream = FileManager.get().open( modelFileName );
		if (inModelStream == null) {		    
			log.error("File: " + modelFileName + " not found");
			System.exit(0);
		}

		// Create the input model. Models different from the default one import also the 
		// rdf and rdf-schema axioms. 
		_model = ModelFactory.createDefaultModel();
		
		// read the RDF/TURTLE file
		_model.read(inModelStream, baseUri, "TURTLE");
		
		// create the output model
		_outModel = ModelFactory.createDefaultModel() ;	
		
		builder = new EntityBuilder(_model) ;
		
		global = new Globalizer(_model, _outModel) ;
		
	}

	@Test
	public void testBuildEntity() {
		log.info("----------testGetProperties--------------") ;
		
		Set<RDFNode> distSubjs = global.getDistinctSubjects() ;
		
		Iterator<RDFNode> idistSubj = distSubjs.iterator() ;
		while(idistSubj.hasNext()) {
			RDFNode distSubj = idistSubj.next() ;
			
			ProfileType profile = null;
			profile = builder.buildEntityProfile(distSubj);
			List<AttributeType> attrTypes = profile.getAttributes().getAttributes() ;
			Iterator<AttributeType> iattrType = attrTypes.iterator() ;
			while(iattrType.hasNext()) {
				AttributeType attrType = iattrType.next() ;
				QName name = attrType.getName() ;
				String value = attrType.getValue() ;
				log.info( distSubj + ", " + name.getPrefix() + ":" + name.getLocalPart() + ", " + value) ;
			}
			
		}
	}

}
