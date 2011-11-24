package it.okkam.rdf2okkam.model;

import it.okkam.rdf2okkam.ens.client.ServiceClientTest;
import it.okkam.rdf2okkam.parser.GetSubjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.query.*;

/**
 * This Class is used to Retrieve Okkam ID from ENS repository and modify the
 * RDF repository Accordingly.
 * 
 */
public class ModelUtil {
	static Model model = null;

	static ModelLoader loader = null;

	private static Log log = LogFactory.getLog(ModelUtil.class);

	//replace all blank node subject with okkam ID
	private ArrayList<String> replaceSubject(Map<String, String> bnodeOkkamId) {
		
		Iterator okkamiIdIterator = bnodeOkkamId.keySet().iterator() ;
		//Statements blank node replaced with okkam ID
		ArrayList newstatments = new ArrayList() ;
		//for every okkam ID
		while (okkamiIdIterator.hasNext()) {
			String tempsubject = okkamiIdIterator.next().toString() ;
			String subject = bnodeOkkamId.get(tempsubject) ;
			Resource subj = model.createResource(subject) ;
			StmtIterator iter = model.listStatements() ;
			//for every statements in the RDF data set, replace blank node subject with
			//okkam ID
			while (iter.hasNext()) {
				Statement tmpstmt = iter.next() ;
				String tmproperty = tmpstmt.getPredicate().toString() ;
				RDFNode object = tmpstmt.getObject() ;
				String tmpsubject = tmpstmt.getSubject().toString() ;
				Resource tmpsubj = model.createResource(tmpsubject) ;
				/*
				 * replace all blank node subjects with okkam id.
				 */
				if (tempsubject.equals(tmpsubject)) {
					Property tmpproperty = model.createProperty(tmproperty) ;
					Statement newstmt = 
						ResourceFactory.createStatement(subj,tmpproperty, object) ;
					newstatments.add(newstmt) ;
					log.debug(newstmt) ;
				}

			}
		}
		//Statements with all subjects replaced by thier corresponding okkm ID
		return newstatments ;
	}
	
	//replace all blank node objects with okkam ID
	private ArrayList replaceObject(ArrayList statments, Map<String,String> bnodeOkkamId){
		
		//a temporary model of statements with okkam ID
		Model tempmodel = ModelFactory.createDefaultModel() ;

		tempmodel.add(statments);
		
		Iterator okkamiIdIterator = bnodeOkkamId.keySet().iterator() ;
		okkamiIdIterator = bnodeOkkamId.keySet().iterator() ;
		//for every okkam ID
		while (okkamiIdIterator.hasNext()) {
			String tempsubject = okkamiIdIterator.next().toString() ;
			String subject = bnodeOkkamId.get(tempsubject) ;
			Resource subj = tempmodel.createResource(subject) ;
			StmtIterator iter = tempmodel.listStatements() ;
			//for every statements in the temporary model
			while (iter.hasNext()) {
				Statement tmpstmt = iter.next() ;
				
				String tmproperty = tmpstmt.getPredicate().toString() ;
				RDFNode object = tmpstmt.getObject() ;
				String tmpsubject = tmpstmt.getSubject().toString() ;
				Resource tmpsubj = tempmodel.createResource(tmpsubject) ;
				RDFNode checkAnonSubj = tmpstmt.getSubject() ;
				//if a match of blank node with object occurs, replace it with
				//it okkam ID
				if (tempsubject.equals(object.toString())) {
					//first remove the statment
					statments.remove(tmpstmt);
					Property tmpproperty = tempmodel.createProperty(tmproperty) ;
					//recreate the statement with correct okkam ID
					Statement newstmt = 
						ResourceFactory.createStatement(tmpsubj, tmpproperty, subj) ;
					statments.add(newstmt) ;
				}
			}
		}
		return statments;
	}
	public Model modifyRDF(Map<String, String> bnodeOkkamId) {
		loader = ModelLoader.getInstance() ;
		model = loader.getInputModel() ;
		Model result = null;
		result = loader.getOutputModel() ;
		
		//replace all blank nodes in the subject with their okkam ID
		ArrayList okkamizedsubject = replaceSubject(bnodeOkkamId) ;
		//replace all blank nodes in the object with their okkam ID
		ArrayList okkamizedobect=replaceObject(okkamizedsubject, bnodeOkkamId) ;
		
		result.add(okkamizedobect) ;
		StmtIterator iter = result.listStatements() ;
		//remove any subject that have still blank node after the replaceObject method is called
		while (iter.hasNext()) {
			Statement tmpstmt = iter.next() ;
			RDFNode object = tmpstmt.getObject() ;
			if (object.isAnon())
				result.remove(tmpstmt) ;
		}

		ModelLoader.getInstance().getOutputModel().add(result) ;
		ModelLoader.getInstance().writeOutputModel() ;
		return result;
	}

}
