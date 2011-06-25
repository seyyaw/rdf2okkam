package org.okkam.model;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.okkam.service.client.ServiceClient;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class ModelLoader {
	
	public static final String ensOntology = "resources/ENS-core-vocabulary.owl";
	
	public static final String ensNamespace = "http://models.okkam.org/ENS-core-vocabulary.owl#";
	
	public static final String ENS_PREFIX = "ens";
	
	public static final String rdfNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	
	private Model inputModel = null ;
	
	private Model outputModel = null ;
	
	private OntModel ensModel = null;
	
	private String baseUri = null;
	
	private static Log log = LogFactory.getLog(ModelLoader.class);
	
	public void loadInputModel(String fileName){
		
		 // use the FileManager to find the input file
		InputStream in = FileManager.get().open( fileName );
		if (in == null) {		    
			log.error("File: " + fileName + " not found");
			System.exit(0);
		}
		
		// Create the input model. Models different from the default one import also the 
		// rdf and rdf-schema axioms. 
		inputModel = ModelFactory.createDefaultModel();
		
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
	
	private void loadEnsOntology(){
		
		// create an empty model
		ensModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
		
		OntDocumentManager dm = ensModel.getDocumentManager();
		dm.addAltEntry( ensNamespace, "file:" + ensOntology );
		ensModel.read( ensNamespace );	
		
		ExtendedIterator<Ontology> iOntologies = ensModel.listOntologies();
		while(iOntologies.hasNext()){
			Ontology ont = iOntologies.next();
			System.out.println("ENS Ontology: " + ont.getURI());
		}
		
	}
	

}
