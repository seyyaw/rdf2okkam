package it.okkam.rdf2okkam.model;



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
	static Model model2 = null;
	static String inputFileName = "resources/anagrafe.ttl";
	static String inputFileName2="resources/mockup.ttl";
	static String inputFileName3="resources/test.ttl";
	static String inputFileName4="resources/test2.ttl";
	
	static String inputFileName5="resources/test3.ttl";
	
	static ModelLoader loader = null ;
	static GetSubjects getsubjects;
	public static void main(String[] args) throws Exception {
		loader = ModelLoader.getInstance() ;
		model=loader.getInputModel();
		//File outrdf = new File(inputFileName3);
		//GetSubjects.loadModel(inputFileName);
		//loadModel(inputFileName);	
		 getsubjects=new GetSubjects();
		Iterator it = getsubjects.getSubjects().iterator();
	//	String[][] statment = GetSubjects.getProperties(it);
		Map<String,String> bnodeokkamid=bnodeOkkamId(it);
		modifyRDF(bnodeokkamid);
		//ArrayList modstatments=modifyRDF(it);
		//loadmodify(modstatments,inputFileName3,outrdf);
		
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
		ArrayList sub=new ArrayList();ArrayList prop=new ArrayList();ArrayList obj=new ArrayList();
		for (int i = 0; i < GetSubjects.getsize(); i++) {
			String subject = statment[i][0], property = statment[i][1], object = statment[i][2];
				object = "\"" + object + "\"";
			Query SPARQL_QUERY = QueryFactory
					.create("prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
							+ "prefix ens: <http://models.okkam.org/ENS-core-vocabulary.owl#>  "
							+ "SELECT ?okkamid "
							+ "WHERE {?okkamid <"
							+ property + "> " + object + " . }");
			InputStream in = new FileInputStream(new File(
					"resources/test/mockup.ttl"));
			Model model = ModelFactory.createDefaultModel();
			model.read(in, null, "TURTLE");
			QueryExecution qe = QueryExecutionFactory.create(SPARQL_QUERY,
					model);
			try {
				ResultSet results = qe.execSelect();
				while (results.hasNext()) {
					QuerySolution result = results.next();
					sub.add(result.get("okkamid").toString());
					prop.add(statment[i][1]);
					obj.add(statment[i][2]);
				}
			} finally {
				qe.close();
			}
		}
		statment=new String[sub.size()][3];
		for(int i=0;i<sub.size();i++){
			statment[i][0]=sub.get(i).toString();
			statment[i][1]=prop.get(i).toString();
			statment[i][2]=obj.get(i).toString();
		}
		return statment;
	}
	/**
	 * A function that will modify the RDF dataset based on the retrieved Okkam
	 * ID from the ENS Repository
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException 
	 */
	public static Map<String,String> bnodeOkkamId(Iterator it) throws FileNotFoundException{
		loader = ModelLoader.getInstance() ;
		model=loader.getInputModel();
		String[][] statment = getsubjects.getProperties(it);
		String[][] stmt = getOkkamId(statment);
		int size=getOkkamId(statment).length;
		Map<String, String> bnodeokamid=new HashMap<String, String>();
		for (int i = 0; i < size; i++) {
			String subject = stmt[i][0];
			String predicate = stmt[i][1];
			String object = stmt[i][2];
			Property property=model.createProperty(predicate);
			StmtIterator iter = model.listStatements();
			while(iter.hasNext()){
				Statement tempstatment=iter.next();
				String temproperty=tempstatment.getPredicate().toString();
				String tempobject=tempstatment.getObject().toString();
				String tempsubject=tempstatment.getSubject().toString();
				if(temproperty.equals(property.toString())&&tempobject.equals(object)){
				bnodeokamid.put(tempsubject, subject);//okk
				}
			}
		}
		return bnodeokamid;
	}
	public static ArrayList modifyRDF(Iterator it) throws FileNotFoundException, InterruptedException { 
		//get subjects to be modified, okkam ID and thier corresponding BLANK NODEs
		String[][] statment = GetSubjects.getProperties(it);
		String[][] stmt = getOkkamId(statment);
		int size=getOkkamId(statment).length;
		String [][]  blanknodesubjects=new  String[size][2];
		for (int i = 0; i < size; i++) {
			String subject = stmt[i][0];
			String predicate = stmt[i][1];
			String object = stmt[i][2];
			Property property=model.createProperty(predicate);
			StmtIterator iter = model.listStatements();
			while(iter.hasNext()){
				Statement tempstatment=iter.next();
				String temproperty=tempstatment.getPredicate().toString();
				String tempobject=tempstatment.getObject().toString();
				String tempsubject=tempstatment.getSubject().toString();
				if(temproperty.equals(property.toString())&&tempobject.equals(object)){
				blanknodesubjects[i][0]=tempsubject;//bla
				blanknodesubjects[i][1]=subject;//okk
				}
			}
		}
		//replace every occurrence of BLank NODE, even in the object place by thier corresponding OKKAM ID
		ArrayList newstatments=new ArrayList();
			for(int j=0;j<blanknodesubjects.length;j++){
				String tempsubject=blanknodesubjects[j][0];
				String subject=blanknodesubjects[j][1];
				Resource subj=model.createResource(subject);
				StmtIterator iter = model.listStatements();
						while(iter.hasNext()){
							Statement tmpstmt=iter.next();
							String tmproperty=tmpstmt.getPredicate().toString();
							RDFNode object=tmpstmt.getObject();
							String tmpsubject=tmpstmt.getSubject().toString();
							Resource tmpsubj=model.createResource(tmpsubject);
							if(tempsubject.equals(tmpsubject)&&!object.isAnon()){
								Property tmpproperty=model.createProperty(tmproperty);
								Statement newstmt=ResourceFactory.createStatement(subj, tmpproperty, object);
								newstatments.add(newstmt);
								}
							//if a blank node is already a subject of the other statment, get it out.
							else if(tempsubject.equals(object.toString())){
								for(int k=0;k<blanknodesubjects.length;k++){
									if(blanknodesubjects[k][0]==tmpsubject){
										tmpsubj=model.createResource(blanknodesubjects[k][1]);//getting the correct subject of a blank node 
										Property tmpproperty=model.createProperty(tmproperty);
										Statement newstmt=ResourceFactory.createStatement(tmpsubj, tmpproperty, subj);
										newstatments.add(newstmt);
									}
								}
								
							}
				}
		
			}
			return newstatments;
			
	}
	
	/**
	 * A function that will modify the RDF dataset based on the retrieved Okkam
	 * ID from the ENS Repository
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException 
	 */
	public static Model modifyRDF(Map<String,String> bnodeOkkamId) {
		Model result = null ;
		result=loader.getOutputModel();
		Iterator okkamiIdIterator=bnodeOkkamId.keySet().iterator();
		ArrayList newstatments=new ArrayList();
		while(okkamiIdIterator.hasNext()){
			String tempsubject=okkamiIdIterator.next().toString();
			String subject=bnodeOkkamId.get(tempsubject);
			Resource subj=model.createResource(subject);
			StmtIterator iter = model.listStatements();
					while(iter.hasNext()){
						Statement tmpstmt=iter.next();
						String tmproperty=tmpstmt.getPredicate().toString();
						RDFNode object=tmpstmt.getObject();
						String tmpsubject=tmpstmt.getSubject().toString();
						Resource tmpsubj=model.createResource(tmpsubject);
						/*
						 * replace all blank node subjects with okkam id.
						 */
						if(tempsubject.equals(tmpsubject)){
							Property tmpproperty=model.createProperty(tmproperty);
							Statement newstmt=ResourceFactory.createStatement(subj, tmpproperty, object);
							newstatments.add(newstmt);
							}
						
					}
		}
		/*
		 * Create a temporary model that is used to navigate all blank node objects and
		 * replace with thier corresponding okkam id
		 */
		Model tempmodel = ModelFactory.createDefaultModel();
		tempmodel.add(newstatments);
		okkamiIdIterator=bnodeOkkamId.keySet().iterator();
		while(okkamiIdIterator.hasNext()){
			String tempsubject=okkamiIdIterator.next().toString();
			String subject=bnodeOkkamId.get(tempsubject);
			Resource subj=tempmodel.createResource(subject);
			StmtIterator iter = tempmodel.listStatements();
					while(iter.hasNext()){
						Statement tmpstmt=iter.next();
						String tmproperty=tmpstmt.getPredicate().toString();
						RDFNode object=tmpstmt.getObject();
						String tmpsubject=tmpstmt.getSubject().toString();
						Resource tmpsubj=tempmodel.createResource(tmpsubject);
						RDFNode checkAnonSubj=tmpstmt.getSubject();
						 if(tempsubject.equals(object.toString())){
							 newstatments.remove(tmpstmt);
							 Property tmpproperty=tempmodel.createProperty(tmproperty);
							 Statement newstmt=ResourceFactory.createStatement(tmpsubj, tmpproperty, subj);
							newstatments.add(newstmt);
								//}							
						}
					}
			}
		result.add(newstatments);
		StmtIterator iter = tempmodel.listStatements();
		while(iter.hasNext()){
			Statement tmpstmt=iter.next();
			RDFNode object=tmpstmt.getObject();
			if(object.isAnon())
				result.remove(tmpstmt);
		}
		//result.write(System.out,"TTL");
		return result ;		
	}
	public static void loadmodify(ArrayList newstatments,String inputfile, File outrdf) throws FileNotFoundException{	
		loadModel(inputfile);
		//File outrdf = new File("resources/test2.ttl");
	    OutputStream out = new FileOutputStream(outrdf);
		// read the RDF/TTL file
		model.add(newstatments);
		model.write(System.out, "TURTLE");
		//Write the RDF/TTL file to filesystem
		model.write(out,"TURTLE");
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
