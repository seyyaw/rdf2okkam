package it.okkam.rdf2okkam.model;

import it.okkam.rdf2okkam.ens.client.ServiceClient;
import it.okkam.rdf2okkam.parser.VocabConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class ModelLoader {
		
	
	private final String inputDatasetFileName = "resources/test/anagrafe2.ttl" ;
	
	private final String outputDatasetFileName = "resources/test/anagrafe1_out.ttl" ;
	
	private Model inputModel = null ;
	
	private Model outputModel = null ;
	
	private OntModel ensModel = null;
	
	
	private static String baseUri = null;
	
	
	/** Creates a new instance of ModelLoader (singleton) */
	private static final ModelLoader loader = new ModelLoader();
	/** Private constructor */
	private ModelLoader() {
		
		loadInputModel() ;
		loadOutputModel() ;
		
	}
	
	 
	
	private static Log log = LogFactory.getLog(ModelLoader.class);
	
	/** Acces the static instance of wife */
    public static ModelLoader getInstance(){

    	return loader;
    }
	
	private void loadInputModel(){
		
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open( inputDatasetFileName );
		if (in == null) {		    
			log.error("File: " + inputDatasetFileName + " not found");
			System.exit(0);
		}
		
		// Create the input model. Models different from the default one import also the 
		// rdf and rdf-schema axioms. The input model usually comes with blank nodes. 
		inputModel = ModelFactory.createDefaultModel();
		
		// read the RDF/TURTLE file
		inputModel.read(in, baseUri, "TURTLE");
		
		
		
	}
	
	private void loadOutputModel(){
		
		
		 // use the FileManager to find the input file
		InputStream out = FileManager.get().open( outputDatasetFileName );
		if (out == null) {		    
			log.error("File: " + outputDatasetFileName + " not found");
			System.exit(0);
		}
		
		// Create the input model. Models different from the default one import also the 
		// rdf and rdf-schema axioms. The input model usually comes with blank nodes. 
		outputModel = ModelFactory.createDefaultModel();
		
		// read the RDF/TURTLE file
		outputModel.read(out, baseUri, "TURTLE");
		
		// Create the output model. No blank nodes allowed here.
		outputModel = ModelFactory.createDefaultModel() ;	
		
		PrefixMapping prefix = PrefixMapping.Factory.create() ;
		String ensNS = "http://models.okkam.org/ENS-core-vocabulary.owl#" ;
		String ensPrefix = "ens" ;
		
		prefix.setNsPrefix(ensPrefix, ensNS) ;
		
		outputModel.setNsPrefixes(prefix) ;
		
	}
	
	public Model getInputModel() {
		return inputModel ;
	}
	
	public Model getOutputModel() {
		return outputModel ;
	}
	
	public void writeOutputModel() {
		
		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(outputDatasetFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.error("File: " + outputDatasetFileName + " not found");
			System.exit(0);
			e.printStackTrace();
		}
		
		outputModel.write(out) ;
		
	}
	
	private void loadEnsOntology(){
		
		// create an empty model
		ensModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM) ;
		String ensOntologyFileName = "resources/vocabulary/ENS-core-vocabulary.owl" ;
		OntDocumentManager dm = ensModel.getDocumentManager() ;
		dm.addAltEntry( VocabConstants.ensNS, "file:" + ensOntologyFileName );
		ensModel.read( VocabConstants.ensNS );	
		
		ExtendedIterator<Ontology> iOntologies = ensModel.listOntologies();
		while(iOntologies.hasNext()){
			Ontology ont = iOntologies.next();
			System.out.println("ENS Ontology: " + ont.getURI());
		}
		
	}
	

}
