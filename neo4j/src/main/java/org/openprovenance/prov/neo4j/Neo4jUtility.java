package org.openprovenance.prov.neo4j;
import  org.openprovenance.prov.notation.Utility;
import  org.openprovenance.prov.notation.TreeTraversal;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Hashtable;

import  org.antlr.runtime.CommonTokenStream;
import  org.antlr.runtime.ANTLRFileStream;
import  org.antlr.runtime.CharStream;
import  org.antlr.runtime.Token;
import  org.antlr.runtime.tree.Tree;
import  org.antlr.runtime.tree.CommonTree;
import  org.antlr.runtime.tree.CommonTreeAdaptor;
import  org.antlr.runtime.tree.TreeAdaptor;

import org.openprovenance.prov.xml.ProvFactory;
import org.openprovenance.prov.xml.ProvSerialiser;
import org.openprovenance.prov.xml.Bundle;
import org.openprovenance.prov.xml.BeanTraversal;


public  class Neo4jUtility extends Utility {

    public String convertTreeToNeo4j(CommonTree tree) {
        Object o=new TreeTraversal(new Neo4jConstructor()).convert(tree);
        return (String)o;
    }


	static public String asn2neo4j(String file, String file2) throws java.io.IOException, javax.xml.bind.JAXBException, Throwable {

		Neo4jUtility u=new Neo4jUtility();
        CommonTree tree = u.convertASNToTree(file);

        String s=u.convertTreeToNeo4j(tree);

        return s;

    }


}





