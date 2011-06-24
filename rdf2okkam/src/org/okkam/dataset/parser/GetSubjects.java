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

/**
 * This program returns all unique statements for those subjects which have literal or URI value for each
 * RDF statement.
 * 
 */
public class GetSubjects extends Object {

	static Model model = null;
	static String inputFileName = "resources/anagrafe1.ttl";

	public static void main(String args[]) throws IOException {
		loadModel(inputFileName);
		Iterator it = getSubjects(inputFileName).iterator();
		Set statments = getProperties(it);
		Iterator itstmt = statments.iterator();
		int i = 0;
		// System.out.println(statments);
		while (itstmt.hasNext()) {
			System.out.println(++i + ": " + itstmt.next().toString());
		}
	}

	private static Set getProperties(Iterator it) {
		Set statments = new HashSet();
		Set tmpstatments = new HashSet();
		while (it.hasNext()) {
			StmtIterator iter = model.listStatements();
			Resource subject2 = (Resource) it.next();
			while (iter.hasNext()) {
				Statement stmt = iter.nextStatement(); // get next statement
				Resource subject = stmt.getSubject(); // get the subject
				RDFNode object = stmt.getObject(); // get the object
				if (subject.equals(subject2) && !object.isAnon()) {
					String stmts = " Property: " + stmt.getPredicate()+ " Object: " + stmt.getObject();
					// check if different subjects do have the same properties and objects
					if (!tmpstatments.contains(stmts))
						statments.add("Subject: " + subject + stmts);
					tmpstatments.add(stmts);
				}
			}
		}
		return statments;
	}

	private static void loadModel(String filePath) {
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
		return subjects;
	}
}