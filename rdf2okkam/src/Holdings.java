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


import org.w3c.dom.*;

import java.net.URL;
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
public class Holdings
{
    private static final String BASE_URL = "http://api.talis.com/1/bib/holdings?output=rdf";

    private static final Query SPARQL_QUERY = QueryFactory.create(
            "PREFIX dctype: <http://purl.org/dc/dcmitype/> " +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
            "PREFIX h: <http://schemas.talis.com/2005/holdings/schema#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "SELECT ?title ?id WHERE {?x rdf:type dctype:Collection. " +
            "?x dc:title ?title. ?x dc:identifier ?id}");
    /**
     * Main method for holdings class
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        if (args==null||args.length!=3)
        {
            System.out.print("Usage:\njava com.talis.Holdings mode apiKey " +
                "isbn\nmode=rdf (RDF-XML output) or human (human readable " +
                "output)\napiKey=Your API key, available at www.talis.com/tdn\n" +
                "isbn=A valid ISBN identifier");
            return;
        }

        Holdings holdings = new Holdings();

        String mode = args[0];

        if (mode.toUpperCase().equals("RDF"))
        {
            // build document
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document d = builder.parse(holdings.getHoldings(args[1],args[2]));

            // transform result to a String
            Source source = new DOMSource(d);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);

            // print out result
            System.out.println("Document: \n"+stringWriter.getBuffer().toString());
        }
        else if (mode.toUpperCase().equals("HUMAN"))
        {
            Model model = ModelFactory.createDefaultModel();
            model.read(holdings.getHoldings(args[1],args[2]),"");

            QueryExecution qe = QueryExecutionFactory.create(SPARQL_QUERY, model);

            try
            {
                ResultSet results = qe.execSelect();
                while (results.hasNext())
                {
                    QuerySolution soln = results.nextSolution();

                    RDFNode title = soln.get("title");
                    RDFNode id = soln.get("id");

                    StringBuilder result = new StringBuilder();

                    result.append(title.toString());
                    result.append(" holds a copy of this book (http://api.talis.com/1/node/items/");
                    result.append(id.toString());
                    result.append("/bib?api_key=");
                    result.append(args[1]);
                    result.append("&isbn=");
                    result.append(args[2]);
                    result.append(")");

                    System.out.println(result.toString());
                }
            }
            finally
            {
                qe.close();
            }

        }
        else
        {
            System.out.println("Unrecognised mode: "+mode);
        }
    }

    /**
     * Retrieves a result from the holdings lookup service in RDF-XML format
     * @param apiKey
     * @param isbn
     * @return
     * @throws Exception
     */
    public InputStream getHoldings(String apiKey, String isbn) throws Exception
    {
        StringBuilder urlString = new StringBuilder();

        urlString.append(BASE_URL);

        urlString.append("&api_key=");
        urlString.append(apiKey);

        urlString.append("&isbn=");
        urlString.append(isbn);

        URL url = new URL(urlString.toString());
        url.openConnection();

        return url.openStream();
    }
}
