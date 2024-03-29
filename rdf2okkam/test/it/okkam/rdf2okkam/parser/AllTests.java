package it.okkam.rdf2okkam.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(
			"Test for it.okkam.rdf2okkam.parser");
		//Use addTestSuite to run all the tests in the test class
		//suite.addTestSuite(RdfUtilTest.class);
		//suite.addTestSuite(GlobalizerTest.class);
		
		//Use addTest to run specific test in a test class
		//suite.addTest( new RdfUtilTest( "testListProperties" ) );
		//suite.addTest( new RdfUtilTest( "testListSubjects" ) );
		suite.addTest( new RdfUtilTest( "testListSubjectProperties" ) );
		
		return suite;
	}
}