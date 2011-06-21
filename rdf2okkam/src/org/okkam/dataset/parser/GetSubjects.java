package org.okkam.dataset.parser;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
/**
 * This program returns all subjects which have literal or URI value fir each RDF statement.
 *@author Seid
 */
public class GetSubjects extends Object {
	public static void main(String args[]) throws IOException {
		 String inputFileName = "resources/anagrafe1.ttl";
		 Iterator it=getSubjects(inputFileName).iterator();
		 int i=0;
		 while(it.hasNext()){
			 System.out.println(++i+": "+it.next()+"\n");
		 }
	}
	public static Set getSubjects(String filePath){
		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(filePath);
		if (in == null) {
			throw new IllegalArgumentException("File: " + filePath+ " not found");
		}
		// read the RDF/TTL file
		model.read(in, null, "TURTLE");

		// select all the subjects with literal or uri value
		StmtIterator iter = model.listStatements();
		Set  subjects = new HashSet();
		if (iter.hasNext()) {
			while (iter.hasNext()) {
				Statement stmt = iter.nextStatement(); // get next statement
				Resource subject = stmt.getSubject(); // get the subject
				RDFNode object = stmt.getObject(); // get the object
				//if the object is not anonymous/Blank node, return the subject
				if (!object.isAnon())
					subjects.add(subject);
			}
		} else {
			return subjects;
		}
		return subjects;
	}
}
