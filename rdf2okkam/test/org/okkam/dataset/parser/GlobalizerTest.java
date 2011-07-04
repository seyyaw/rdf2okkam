package org.okkam.dataset.parser;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.okkam.model.ModelLoader;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;


public class GlobalizerTest {
	
	String inputModelFileName = "resources/anagrafe1.ttl" ;
	String outputModelFileName = "resources/datasetout.ttl" ;
	final String RDF_SYNTAX = "TURTLE" ;
	String baseUri = null ;
	Model _inModel = null ;
	Model _outModel = null ;
	Globalizer global = null ;

	private static Log log = LogFactory.getLog(GlobalizerTest.class);
	@Before
	public void setUp() throws Exception {
		
		// use the FileManager to find the input file
		InputStream inModelStream = FileManager.get().open( inputModelFileName );
		if (inModelStream == null) {		    
			log.error("File: " + inputModelFileName + " not found");
			System.exit(0);
		}

		// Create the input model. Models different from the default one import also the 
		// rdf and rdf-schema axioms. 
		_inModel = ModelFactory.createDefaultModel();
		
		// read the RDF/TURTLE file
		_inModel.read(inModelStream, baseUri, "TURTLE");
		
		// create the output model
		_outModel = ModelFactory.createDefaultModel() ;				
		
		global = new Globalizer(_inModel, _outModel) ;
	}

	@Test
	public void testIsCandidateEntity() {
		log.info("----------testIsCandidateEntity--------------") ;
		Set<RDFNode> subjects = global.getDistinctSubjects() ;
		Iterator<RDFNode> isubject = subjects.iterator() ;
		while(isubject.hasNext()) {
			Resource subject = isubject.next().asResource() ;			
			if( global.isCandidateEntity(subject) ) 
				log.info(subject + " is a candidate entity") ;
			else
				log.info(subject + " is not a candidate entity") ;			
		}
	}
	
	@Test
	public void testGetDistinctSubjects() {
		log.info("----------testGetDistinctSubjects--------------") ;
		Set<RDFNode> subjects = global.getDistinctSubjects() ;
		Iterator<RDFNode> isubject = subjects.iterator() ;
		while(isubject.hasNext()) {
			RDFNode subject = isubject.next() ;
			log.info(subject) ;
		}
	}

	@Test
	public void testGlobalizeId() throws FileNotFoundException {
		log.info("----------testGlobalizeId--------------") ;
		Set<RDFNode> distSubjs = global.getDistinctSubjects() ;
		Iterator<RDFNode> idistSubj = distSubjs.iterator() ;
		int count = 1 ;
		while(idistSubj.hasNext()) {			
			RDFNode subj = idistSubj.next() ;
			if(global.isCandidateEntity(subj)) { 
				log.info(subj + " is a candidate entity") ;
				_outModel.add( global.globalizeId(subj, "http://okkam.org/ens/" + count) ) ;
				count++ ;
			}
			else
				log.info(subj + " is not a candidate entity") ;
			}
			
		_outModel.write(new PrintWriter( outputModelFileName ), RDF_SYNTAX) ;
		
		
	}

}
