/**
 * The person or persons who have associated work with this document (the "Dedicator" or "Certifier")
 * hereby either (a) certifies that, to the best of his knowledge, the work of authorship identified is 
 * in the public domain of the country from which the work is published, or (b) hereby dedicates 
 * whatever copyright the dedicators holds in the work of authorship identified below (the "Work") to 
 * the public domain. A certifier, moreover, dedicates any copyright interest he may have in the 
 * associated work, and for these purposes, is described as a "dedicator" below.
 *
 * A certifier has taken reasonable steps to verify the copyright status of this work. Certifier 
 * recognizes that his good faith efforts may not shield him from liability if in fact the work 
 * certified is not in the public domain.
 *
 * Dedicator makes this dedication for the benefit of the public at large and to the detriment of the    
 * Dedicator's heirs and successors. Dedicator intends this dedication to be an overt act of 
 * relinquishment in perpetuity of all present and future rights under copyright law, whether vested or 
 * contingent, in the Work. Dedicator understands that such relinquishment of all rights includes the 
 * relinquishment of all rights to enforce (by lawsuit or otherwise) those copyrights in the Work.
 *
 * Dedicator recognizes that, once placed in the public domain, the Work may be freely reproduced, 
 * distributed, transmitted, used, modified, built upon, or otherwise exploited by anyone for any 
 * purpose, commercial or non-commercial, and in any way, including by methods that have not yet been 
 * invented or conceived.
 */
package org.okkam.mockups;

import org.w3c.dom.*;

import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.InputStream;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.query.*;

/**
 * Can be used to extract holdings information from the Talis Platform
 */
public class SPARQLExample
{
    private static final String BASE_URL = null;
    private static final Query SPARQL_QUERY = QueryFactory.create(
    		"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
    		"prefix ens: <http://models.okkam.org/ENS-core-vocabulary.owl#>  " +
    		"SELECT ?okkamid " +
    		"WHERE {?okkamid a ens:location}");
    /**
     * Main method for holdings class
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
           	InputStream in = new FileInputStream(new File("resources/mockup.ttl"));
            Model model = ModelFactory.createDefaultModel();
            model.read(in, null, "TURTLE");

            QueryExecution qe = QueryExecutionFactory.create(SPARQL_QUERY, model);

            try
            {
                ResultSet results = qe.execSelect();
                ResultSetFormatter.out(System.out, results, SPARQL_QUERY);
            }
            finally
            {
                qe.close();
            }

        }

    /**
     * Retrieves a result from the holdings lookup service in RDF-XML format
     * @param apiKey
     * @param isbn
     * @return
     * @throws Exception
     */
}
