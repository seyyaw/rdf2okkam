package org.okkam.mockups;
import org.okkam.dataset.parser.GetSubjects;
import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.query.*;

/**
 * This Class is used to Retrieve Okkam ID from ENS repository and modify the
 * RDF repository Accordingly.
 * 
 */
public class GetOkkamIDAndModifyRDFDataSet {
	static Model model = null;
	static String inputFileName = "resources/anagrafe1.ttl";
	static String inputFileName2="resources/mockup.ttl";

	public static void main(String[] args) throws Exception {
		loadModel(inputFileName);		
		GetSubjects.loadModel(inputFileName);
		modifyRDF();
	}
	/**
	 * This Function is Used to get Okkam ID from the ENS repository and return
	 * array of subjects, properties and objects accordingly
	 * 
	 * @param statment
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String[][] getOkkamId(String[][] statment)
			throws FileNotFoundException {
		for (int i = 0; i < GetSubjects.getsize(); i++) {
			String subject = statment[i][0], property = statment[i][1], object = statment[i][2];
			if (object.startsWith("http:"))// here, it should be modified to
											// detect RDF Resources,....
				object = "<" + object + ">";
			else
				object = "'" + object + "'";
			Query SPARQL_QUERY = QueryFactory
					.create("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ "prefix ens: <http://models.okkam.org/ENS-core-vocabulary.owl#>  "
							+ "SELECT ?okkamid "
							+ "WHERE {?okkamid <"
							+ property + "> " + object + " }");
			InputStream in = new FileInputStream(new File(
					"resources/mockup.ttl"));
			Model model = ModelFactory.createDefaultModel();
			model.read(in, null, "TURTLE");
			QueryExecution qe = QueryExecutionFactory.create(SPARQL_QUERY,
					model);
			try {
				ResultSet results = qe.execSelect();
				while (results.hasNext()) {
					QuerySolution result = results.next();
					statment[i][0] = result.get("okkamid").toString();
				}
			} finally {
				qe.close();
			}
		}
		return statment;
	}

	/**
	 * A function that will modify the RDF dataset based on the retrieved Okkam
	 * ID from the ENS Repository
	 * 
	 * @throws FileNotFoundException
	 */
	public static void modifyRDF() throws FileNotFoundException {
		File outrdf = new File("resources/test.ttl");
	    OutputStream out = new FileOutputStream(outrdf); 
		Iterator it = GetSubjects.getSubjects(inputFileName).iterator();
		String[][] statment = GetSubjects.getProperties(it);
		String[][] stmt = getOkkamId(statment);
		for (int i = 0; i < GetSubjects.getsize(); i++) {
			String subject = stmt[i][0], property = stmt[i][1], object = stmt[i][2];
			if (object.startsWith("http:"))// here, it should be modified to
											// detect RDF Resources,....
				object = "<" + object + ">";
			else
				object = "'" + object + "'";
			UpdateRequest request2 = UpdateFactory.create("DELETE WHERE { ?x <"
					+ property + "> " + object + " }");
			// And perform the operations.
			request2 = UpdateFactory.create("INSERT DATA { <" + subject + "> <"
					+ property + "> " + object + " }");
			UpdateAction.execute(request2, model);
		}
		model.write(System.out, "TTL");
		//Write the RDF/TTL file to filesystem
		model.write(out,"TTL");
	}

	public static void loadModel(String filePath) {
		// create an empty model
		model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(filePath);
		if (in == null) {
			throw new IllegalArgumentException("File: " + filePath
					+ " not found");
		}
		// read the RDF/TTL file
		model.read(in, null, "TURTLE");
	}
}
