package org.openprovenance.prov.neo4j;

import  org.openprovenance.prov.notation.TreeConstructor;
import  org.antlr.runtime.tree.CommonTree;

import org.openprovenance.prov.neo4j.CreateSimpleGraph;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.String;

    public class Neo4jConstructor implements TreeConstructor {
	int i=0;
    public String genId() {
	return "al_" + (i++);
    }

    public String optionalAttributes(Object attrs) {
        String s_attrs=(String)attrs;
        if ("".equals(s_attrs)) {
            return ", nil";
        } else {
	    String val=genId();
            return "," + val;
        }
    }

    public String optionalTime(Object time) {
        return ((time==null)? ", nil" : (", " + time));
    }            

    public Object optional(Object str) {
        return ((str==null)? "nil" : str);
    }



    public Object convertActivity(Object id,Object startTime,Object endTime, Object aAttrs) {
        String s="" + id + "";
        String st="" + optional(startTime) + "";
        String et="" + optional(endTime) + "";   
        URI firstNode = CreateSimpleGraph.createNode();
        CreateSimpleGraph.addNodeToIndex( "id", s, firstNode);
    	CreateSimpleGraph.addProperty(firstNode, "type", "activity");
        CreateSimpleGraph.addProperty(firstNode, "id", s);
        CreateSimpleGraph.addProperty(firstNode, "starttime", st);
        CreateSimpleGraph.addProperty(firstNode, "endtime", et);
        String u="" + aAttrs + "";   
        String v=u.replaceAll("\\:","\\_");     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                      
	    		CreateSimpleGraph.addProperty(firstNode, v1, v3 );
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }   
        CreateSimpleGraph.nodeMap.put(s, firstNode);
        return null;
    }



    public Object convertEntity(Object id, Object attrs) {       
        String s="" + id  + "";
        URI firstNode = CreateSimpleGraph.createNode();	
        CreateSimpleGraph.addNodeToIndex( "id", s, firstNode);
        CreateSimpleGraph.addProperty(firstNode, "type", "entity"); 
        CreateSimpleGraph.addProperty(firstNode, "id", s);
        String u="" + attrs + ""; 
        String v=u.replaceAll("\\:","\\_");    
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                      
	    		CreateSimpleGraph.addProperty(firstNode, v1, v3 );
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }   
        CreateSimpleGraph.nodeMap.put(s, firstNode);
        return null;
    }



    public Object convertAgent(Object id, Object attrs) {
        String s="" + id  + "";
        URI firstNode = CreateSimpleGraph.createNode();	
        CreateSimpleGraph.addNodeToIndex( "id", s, firstNode);
        CreateSimpleGraph.addProperty(firstNode, "type", "agent"); 
        CreateSimpleGraph.addProperty(firstNode, "id", s); 
        String u="" + attrs + ""; 
        String v=u.replaceAll("\\:","\\_");       
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                      
	    		CreateSimpleGraph.addProperty(firstNode, v1, v3 );
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }   
        CreateSimpleGraph.nodeMap.put(s, firstNode);
        return null;
    }



    public Object convertBundle(Object namespaces, List<Object> records, List<Object> bundles) {
        String s="";
        for (Object o: records) {
            s=s+o+".\n";
        }
        return s;
        }

    public Object convertAttributes(List<Object> attributes) {
        String s="";
        boolean first=true;
        for (Object o: attributes) {
            if (first) {
                first=false;
                s=s+o;
            } else {
                s=s+","+o;
            }
        }
        return s;
    }
    public Object convertId(String id) {
        return ((id==null)? id : id.replace(':','_'));
    }
    public Object convertAttribute(Object name, Object value) {
        return name + "=" + value;
    }
    public Object convertStart(String start) {
        return start;
    }
    public Object convertEnd(String end) {
        return end;
    }
    public Object convertA(Object a) {
        return a;
    }
    public Object convertU(Object a) {
        return a;
    }
    public Object convertG(Object a) {
        return a;
    }
    public Object convertString(String s) {
        return s;
    }

    public Object convertInt(int i) {
        return i;
    }

    public String optionalId(Object id) {
        return ((id==null)? "nil, " : (id + ","));
    }            



    public Object convertUsed(Object id, Object id2,Object id1, Object time, Object aAttrs) {
        String s="" + time + "";
        String t1="" + id  + "";
        String t2="" + id2  + "";
        String t3="" + id1  + "";
        while    (!t1.equals("null"))
        {
        URI firstNode = CreateSimpleGraph.createNode();	
        CreateSimpleGraph.addProperty(firstNode, "type", "used"); 
        CreateSimpleGraph.addProperty(firstNode, "id", t1);
        CreateSimpleGraph.addProperty(firstNode, "activity", t2);
        CreateSimpleGraph.addProperty(firstNode, "entity", t3);
        CreateSimpleGraph.addProperty(firstNode, "time", s);    
        CreateSimpleGraph.nodeMap.put(t1, firstNode);
        break;
        }
        Map<String,String> property=new HashMap<String,String>();		
		property.put("time", s);
                property.put("activity", t2);
                property.put("entity", t3);
                property.put("type", "used");
        String v="" + aAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;
    }       
        try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "used");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }



    public Object convertWasGeneratedBy(Object id, Object id2,Object id1, Object time, Object aAttrs ) {
       String s="" + time + "";
       String t="" + id2 + "";
       String u="" + id1 + "";
       Map<String,String> property=new HashMap<String,String>();		
		property.put("time", s);
                property.put("entity", t);
                property.put("activity", u);
                property.put("type", "wasGeneratedBy");
       String v="" + aAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;
    }        
       try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasGeneratedBy");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property); 
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }



    public Object convertWasStartedBy(Object id, Object id2,Object id1, Object id3, Object time, Object aAttrs) {
       String s="" + time + "";
       String t="" + id2 + "";
       String u="" + id1 + "";
       String w="" + id3 + "";
       Map<String,String> property=new HashMap<String,String>();		
		property.put("time", s);
                property.put("entity", u);
                property.put("activity2", t);
                property.put("activity1", w);
                property.put("type", "wasStartedBy");
       String v="" + aAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;
    }
     try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasStartedBy");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property); 
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }



    public Object convertWasEndedBy(Object id, Object id2,Object id1, Object id3, Object time, Object aAttrs) {
       String s="" + time + "";
       String t="" + id2 + "";
       String u="" + id1 + "";
       String w="" + id3 + "";
       Map<String,String> property=new HashMap<String,String>();		
		property.put("time", s);
                property.put("entity", u);
                property.put("activity2", t);
                property.put("activity1", w);
                property.put("type", "wasEndedBy");
       String v="" + aAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;
    }
     try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasEndedBy");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property); 
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }


    public Object convertWasInformedBy(Object id, Object id2, Object id1, Object aAttrs) {  
      String s="" + id2 + ""; 
      String t="" + id1 + ""; 
      String v="" + aAttrs + "";
      Map<String,String> property=new HashMap<String,String>();
      property.put("activity2", s);
      property.put("activity1",t);
      property.put("type", "wasInformedBy");
      while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;
    }      
      try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasInformedBy");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }



    public Object convertWasInvalidatedBy(Object id, Object id2,Object id1, Object time, Object aAttrs ) {
       String s="" + time + "";
       String t="" + id2 + "";
       String u="" + id1 + "";
       Map<String,String> property=new HashMap<String,String>();
       property.put("entity", t);
       property.put("activity", u);
       property.put("time", s);
       property.put("type", "wasInvalidateBy");
       String v="" + aAttrs + ""; 
       while  (!v.isEmpty())
       {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;
       }
     try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasInvalidateBy");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }



    public Object convertWasAttributedTo(Object id, Object id2,Object id1, Object gAttrs) {
       String s="" + id2 + "";
       String t="" + id1 + "";
       Map<String,String> property=new HashMap<String,String>();		
                property.put("entity", s);
                property.put("agent", t);
                property.put("type", "wasAttributedTo");
       String v="" + gAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;
    }
     try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasAttributedTo");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }


    public Object convertWasDerivedFrom(Object id, Object id2,Object id1, Object pe, Object g2, Object u1, Object aAttrs) {    
     String o="" + id2 + "";
     String p="" + id1 + "";
     String s="" + pe + "";
     String t="" + g2 + "";
     String u="" + u1 + "";
     Map<String,String> property=new HashMap<String,String>();
		property.put("entity2", o);
                property.put("entity1", p);
		property.put("activity", s);
		property.put("generation", t);
                property.put("usage", u);
                property.put("type", "wasDerivedFrom");
     String v="" + aAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }        
     try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasDerivedFrom");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);      
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
 
    }


    public Object convertAlternateOf(Object id2, Object id1) {
     String s="" + id2 + "";
     String t="" + id1 + ""; 
     Map<String,String> property=new HashMap<String,String>();
		property.put("alternate2", s);
                property.put("alternate1", t); 
                property.put("type", "alternateOf");      
      try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "alternateOf");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
 
    }


    public Object convertSpecializationOf(Object id2, Object id1) {
     String s="" + id2 + "";
     String t="" + id1 + ""; 
     Map<String,String> property=new HashMap<String,String>();
		property.put("specificEntity", s);
                property.put("generalEntity", t);  
                property.put("type", "speciallizationOf");
      try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "speciallizationOf");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
 
    }


    public Object convertActedOnBehalfOf(Object id, Object id2,Object id1, Object a, Object aAttrs) {
       String s="" + id2 + "";
       String t="" + id1 + "";
       String u="" + a + "";
       Map<String,String> property=new HashMap<String,String>();		
                property.put("agent2", s);
                property.put("agent1", t);
                property.put("activity", u);
                property.put("type", "actedOnBehalfOf");
       String v="" + aAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;
    }
      try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "actedOnBehalfOf");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }


    public Object convertWasAssociatedWith(Object id, Object id2,Object id1, Object pl, Object aAttrs) {
        String s="" + pl + "";
        String t="" + id2 + "";
        String u="" + id1 + "";
        Map<String,String> property=new HashMap<String,String>();
		property.put("activity", t);
                property.put("agent", u);
		property.put("publicationPolicy", s);
                property.put("type", "wasAssociatedWith");
       String v="" + aAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;
    }        

        try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasAssociatedWith");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property); 
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }



    public Object convertWasRevisionOf(Object id, Object id2,Object id1, Object pe, Object q2, Object q1, Object dAttrs) {
     String s="" + id2 + "";
     String t="" + id1 + "";
     Map<String,String> property=new HashMap<String,String>();
		property.put("entity2", s);
                property.put("entity1", t);
                property.put("type", "wasRevisionOf");
     String v="" + dAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }  
      try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasRevisionOf");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
 
    }



    public Object convertWasQuotedFrom(Object id, Object id2,Object id1, Object pe, Object q2, Object q1, Object dAttrs) {
     String o="" + id2 + "";    
     String s="" + id1 + "";
     Map<String,String> property=new HashMap<String,String>();
		property.put("entity2", o);
                property.put("entity1", s);
                property.put("type", "wasQuotedFrom");
     String v="" + dAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }
       try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasQuotedFrom");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
 
    }



    public Object convertHadOriginalSource(Object id, Object id2,Object id1, Object pe, Object q2, Object q1, Object dAttrs) {
      String s="" + id2 + "";    
      String t="" + id1 + "";
      Map<String,String> property=new HashMap<String,String>();
		property.put("entity2", s);
                property.put("entity1", t);
                property.put("type", "hasOriginalSource");
     String v="" + dAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }
      try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "hasOriginalSource");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
 
    }



    public Object convertTracedTo(Object id, Object id2, Object id1, Object dAttrs) {
      String s="" + id2 + "";    
      String t="" + id1 + "";
      Map<String,String> property=new HashMap<String,String>();
		property.put("entity2", s);
                property.put("entity1", t);
                property.put("type", "tracedTo");
      String v="" + dAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }
       try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "tracedTo");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
 
    }

    public Object convertHadPrimarySource(Object id, Object id2,Object id1, Object pe, Object q2, Object q1, Object dAttrs) {
        //todo
        throw new UnsupportedOperationException();
    }

    public Object convertWasInfluencedBy(Object id, Object id2, Object id1, Object dAttrs) {
      String s="" + id2 + "";    
      String t="" + id1 + "";
      Map<String,String> property=new HashMap<String,String>();
		property.put("influencee", s);
                property.put("influencer", t);
                property.put("type", "wasInfluencedBy");
      String v="" + dAttrs + "";     
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                     
                        property.put(v1, v3);
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }
       try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "wasInfluencedBy");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }

     public Object convertQualifiedName(String qname) {
        return qname;
    }
    public Object convertIRI(String iri) {
        return iri;
    }
    
    public Object convertNamedBundle(Object id, Object nss, List<Object> records){
        return null;
    }

    public Object convertTypedLiteral(String datatype, Object value) {
        return value + "%%" + datatype;
    }

    public Object convertNamespace(Object pre, Object iri) {
       return "prefix " + pre + " " + iri;
    }

   public Object convertDefaultNamespace(Object iri) {
       return  "default " + iri;
   }


   public Object convertNamespaces(List<Object> namespaces) {
        String s="";
        for (Object o: namespaces) {
            s=s+o+"\n";
        }
        return s;
    }


    public Object convertPrefix(String pre) {
        return pre;
    }
   /* Component 5 */

    /*public Object convertInsertion(Object id, Object id2, Object id1, Object map, Object dAttrs) {
        //todo
        throw new UnsupportedOperationException();
    }*/

  

    public Object convertInsertion(Object id, Object id2, Object id1, Object map, Object dAttrs) { 
    Map<String,String> property=new HashMap<String,String>();		
    String s="" + map + "";
    String t="" + id2 + "";     
    String u="" + id1 + "";
    property.put("collection2",t);
    property.put("collection1",u);
    property.put("type", "derivedByInsertionFrom");
    StringBuffer sb = new StringBuffer("");
        while  (!s.isEmpty())
    {
	    String[] array= s.split(",");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		
	    		String v1 = wordList.get(k+1);
                        String v2 = v1.replaceAll("\"|'", "");                     
                        sb.append("("+v2+")");
	    		v1 = null;
	    		   		
	    	}
	    	break;
              
    }
     
   property.put("keyentityset",sb.toString());     
      try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "derivedByInsertionFrom");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);     
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }


    public Object convertEntry(Object o1, Object o2) {
        String s="" + o1 + "";
        String t="" + o2 + "";
        return s+","+t;
    }


    public Object convertKeyEntitySet(List<Object> o) {
    String s="";
        boolean first=true;
        for (Object i: o) {
            if (first) {
                first=false;
                s=s+i;
            } else {
                s=s+","+i;
            }
        }
        return s;
    }  

    /*public Object convertEntry(Object o1, Object o2) {
        //todo
        throw new UnsupportedOperationException();
    }


    public Object convertKeyEntitySet(List<Object> o) {
        //todo
        throw new UnsupportedOperationException();
    }*/


    public Object convertRemoval(Object id, Object id2, Object id1, Object keys, Object dAttrs) {
        //todo
        throw new UnsupportedOperationException();
    }


    public Object convertDerivedByRemovalFrom(Object id, Object id2, Object id1, Object keys, Object dAttrs) {
    Map<String,String> property=new HashMap<String,String>();		
    property.put("type", "derivedByRemovalFrom");
      try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(id2), CreateSimpleGraph.nodeMap.get(id1), "derivedByRemovalFrom");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property); 
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }
  

    public Object convertKeys(List<Object> keys) {
        //todo
        throw new UnsupportedOperationException();
    }

    public Object convertDictionaryMemberOf(Object id, Object id2, Object map, Object complete, Object dAttrs) {
        //todo
        throw new UnsupportedOperationException();
    }
     public Object convertCollectionMemberOf(Object id, Object id2, Object map, Object complete, Object dAttrs) {
        //todo
        throw new UnsupportedOperationException();
    }


   /* Component 6 */


  public Object convertNote(Object id, Object attrs) {       
        String s="" + id  + "";
        URI firstNode = CreateSimpleGraph.createNode();	
        CreateSimpleGraph.addProperty(firstNode, "type", "note"); 
        CreateSimpleGraph.addProperty(firstNode, "id", s);
 
        String u="" + attrs + ""; 
        String v=u.replaceAll("\\:","\\_");    
        while  (!v.isEmpty())
    {
	    String[] array= v.split("=|,");
	    List<String> wordList = Arrays.asList(array); 
	    int i =array.length;
	    int k = 0;    
	    	for(k=0;k<i;k=k+2)
	    	{
	    		String v1 = wordList.get(k);
	    		String v2 = wordList.get(k+1);
                        String v3 = v2.replaceAll("\"|'", "");                      
	    		CreateSimpleGraph.addProperty(firstNode, v1, v3 );
	    		v1 = null;
	    		v2 = null;    		
	    	}
	    	break;

    }   
        CreateSimpleGraph.nodeMap.put(s, firstNode);
        return null;
    }


  public Object convertHasAnnotation(Object something, Object note) {
    Map<String,String> property=new HashMap<String,String>();		
    String s="" + something + "";
    String t="" + note + "";     
    property.put("entity", s);
    property.put("note",t);
    property.put("type", "hasAnnotation");
       try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(something), CreateSimpleGraph.nodeMap.get(note), "hasAnnotation");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property); 
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
 
    }

  public Object convertHasProvenanceIn(Object uid,Object su, Object bu, Object ta, Object se, Object pr, Object dAttrs) {
        //todo
        throw new UnsupportedOperationException();
    }




  public Object convertContextualizationOf(Object su, Object bu, Object ta) {
     String s="" + su + "";
     String t="" + bu + "";
     String u="" + ta + "";
     Map<String,String> property=new HashMap<String,String>();
		property.put("su", s);
                property.put("bu", t);
                property.put("ta", u);
                property.put("type", "ContextualizationOf");
     try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(su), CreateSimpleGraph.nodeMap.get(ta), "ContextualizationOf");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;

    }  

 public Object convertMentionOf(Object su, Object bu, Object ta) {
     String s="" + su + "";
     String t="" + bu + "";
     String u="" + ta + "";
     Map<String,String> property=new HashMap<String,String>();
		property.put("su", s);
                property.put("bu", t);
                property.put("ta", u);
                property.put("type", "mentionOf");
     try {
         URI relationshipUri = CreateSimpleGraph.addRelationship( CreateSimpleGraph.nodeMap.get(su), CreateSimpleGraph.nodeMap.get(ta), "mentionOf");
         CreateSimpleGraph.addMetadataToProperty(relationshipUri,property);
     } catch(URISyntaxException e) {
    	 System.out.println("");
     }       
        return null;
    }



}
