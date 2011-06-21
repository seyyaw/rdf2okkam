package org.okkam.model;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.okkam.service.client.ServiceClient;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class ModelLoader {
	
	private Model inputModel = null ;
	
	private Model outputModel = null ;
	
	private String baseUri = null;
	
	private static Log log = LogFactory.getLog(ModelLoader.class);
	
	public void loadInputModel(String fileName){
		
		 // use the FileManager to find the input file
		InputStream in = FileManager.get().open( fileName );
		if (in == null) {		    
			log.error("File: " + fileName + " not found");
			System.exit(0);
		}
		
		// create the input model
		inputModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		
		// read the RDF/TURTLE file
		inputModel.read(in, baseUri, "TURTLE");
		
		// create the output model
		outputModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);				
		
	}
	
	public Model getInputModel() {
		return inputModel ;
	}
	
	public Model getOutputModel() {
		return outputModel ;
	}
	

}
