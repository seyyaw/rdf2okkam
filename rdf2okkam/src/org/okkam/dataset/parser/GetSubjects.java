package org.okkam.dataset.parser;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
/**
 * This program returns all subjects which have literal or URI value fir each RDF statement.
 *
 */
public class GetSubjects extends Object {
	
	
	static Model model = null;
	
	public static void main(String args[]) throws IOException {
		 String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		 String inputFileName = "resources/anagrafe1.ttl";
		 Iterator it=getSubjects(inputFileName).iterator();
		 Property type = model.getProperty(rdfNS + "type");
		 int i=0;
		 while(it.hasNext()){
			 Resource subject = (Resource) it.next();
			 Statement stmt = model.getProperty(subject, type);
			 System.out.println(++i+": "+ subject + " , object " + stmt.getObject().toString() + "\n");
		 }
	}
	public static Set getSubjects(String filePath){
		// create an empty model
		model = ModelFactory.createDefaultModel();

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
		Set  tempSubjects = new HashSet();
		if (iter.hasNext()) {
			while (iter.hasNext()) {
				Statement stmt = iter.nextStatement(); // get next statement
				Resource subject = stmt.getSubject(); // get the subject
				RDFNode object = stmt.getObject(); // get the object
				//if the object is not anonymous/Blank node, return the subject
				if (object.isAnon())
					tempSubjects.add(subject);
				else 
					{
					subjects.add(subject);
					}
			}
		} 
		Iterator subit=tempSubjects.iterator();
		while(subit.hasNext()){
			RDFNode object =(RDFNode) subit.next();
			if(subjects.contains(object))
				subjects.remove(object);
		}
		return subjects;
	}
}