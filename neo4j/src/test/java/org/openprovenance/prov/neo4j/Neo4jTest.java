package org.openprovenance.prov.neo4j;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Neo4jTest extends TestCase
{
    public void testNeo4j() throws  java.lang.Throwable {

    	Neo4jUtility u = new Neo4jUtility();

    	u.asn2neo4j("../asn/src/test/resources/prov/file-example1.asn",null);
	
    }
}


