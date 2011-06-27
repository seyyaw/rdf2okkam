package org.okkam.dataset.parser;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.okkam.model.ModelLoader;

/**
 * This program returns all unique statements for those subjects which have literal or URI value for each
 * RDF statement.
 * 
 */
public class GetSubjects extends Object {

	static Model model = null;
	static String inputFileName = "resources/anagrafe1.ttl";
	static int size=0;
	
	private static Log log = LogFactory.getLog(GetSubjects.class);

	public static void main(String args[]) throws IOException {
		loadModel(inputFileName);
		Iterator it = getSubjects(inputFileName).iterator();
		String[][] statments = getProperties(it);
		// System.out.println(statments);
		for(int i=0;i<size;i++) {
		//	System.out.println(i + "Subject: " + statments[i][0]+"Property: "+statments[i][1]+"Object: "+statments[i][2]);
		}
	}

	public static String[][] getProperties(Iterator it) {
		Set statments = new HashSet();
		String[][] results=new String[size][3];
		Set tmpstatments = new HashSet();
		int i=0;
		while (it.hasNext()) {
			StmtIterator iter = model.listStatements();
			Resource subject2 = (Resource) it.next();
			while (iter.hasNext()) {
				Statement stmt = iter.nextStatement(); // get next statement
				Resource subject = stmt.getSubject(); // get the subject
				RDFNode object = stmt.getObject(); // get the object
				if (subject.equals(subject2) && object.isLiteral()) {
					
					String stmts = " Property: " + stmt.getPredicate()+ " Object: " + stmt.getObject();
					// check if different subjects do have the same properties and objects
					if (!tmpstatments.contains(stmts)){
						log.info(stmt);
						results[i][0]=subject.toString();
						results[i][1]=stmt.getPredicate().toString();
						results[i][2]=stmt.getObject().toString();
						i++;
					}
					tmpstatments.add(stmts);
				}
			}
		}
		return results;
	}

	public static void loadModel(String filePath) {
		// create an empty model
		model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(filePath);
		if (in == null) {
			throw new IllegalArgumentException("File: " + filePath + " not found");
		}
		// read the RDF/TTL file
		model.read(in, null, "TURTLE");
	}

	public static Set getSubjects(String filePath) {
		// select all the subjects with literal or uri value
		StmtIterator iter = model.listStatements();
		Set subjects = new HashSet();
		Set tempSubjects = new HashSet();
		if (iter.hasNext()) {
			while (iter.hasNext()) {
				Statement stmt = iter.nextStatement(); // get next statement
				Resource subject = stmt.getSubject(); // get the subject
				RDFNode object = stmt.getObject(); // get the object
				// if the object is not anonymous/Blank node, return the subject
				if (object.isAnon())
					tempSubjects.add(subject);
				else {
					subjects.add(subject);
				}
			}
		}
		Iterator subit = tempSubjects.iterator();
		while (subit.hasNext()) {
			RDFNode object = (RDFNode) subit.next();
			if (subjects.contains(object))
				subjects.remove(object);
		}
		size=subjects.size();
		return subjects;
	}
	public static int getsize(){
		return size;
	}
}