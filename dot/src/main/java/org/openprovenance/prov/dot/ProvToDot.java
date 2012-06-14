package org.openprovenance.prov.dot;
import java.util.List;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBElement;
//import org.w3c.dom.Element;

import org.openprovenance.prov.xml.Entity;
import org.openprovenance.prov.xml.Activity;
import org.openprovenance.prov.xml.Relation0;
import org.openprovenance.prov.xml.Relation;
import org.openprovenance.prov.xml.Agent;
import org.openprovenance.prov.xml.Used;
import org.openprovenance.prov.xml.HasType;
import org.openprovenance.prov.xml.WasGeneratedBy;
import org.openprovenance.prov.xml.WasDerivedFrom;
import org.openprovenance.prov.xml.WasAttributedTo;
import org.openprovenance.prov.xml.WasAssociatedWith;
import org.openprovenance.prov.xml.DerivedByInsertionFrom;
import org.openprovenance.prov.xml.ProvFactory;
import org.openprovenance.prov.xml.ProvUtilities;
import org.openprovenance.prov.xml.Identifiable;
import org.openprovenance.prov.xml.ProvDeserialiser;
import org.openprovenance.prov.xml.HasAnnotation;
import org.openprovenance.prov.xml.Note;
import org.openprovenance.prov.xml.Bundle;
import org.openprovenance.prov.xml.HasExtensibility;



import org.openprovenance.prov.dot.ProvPrinterConfiguration;
import org.openprovenance.prov.dot.AgentMapEntry;
import org.openprovenance.prov.dot.AccountColorMapEntry;
import org.openprovenance.prov.dot.EntityMapEntry;
import org.openprovenance.prov.dot.ActivityMapEntry;
import org.openprovenance.prov.dot.RelationStyleMapEntry;
import org.openprovenance.prov.dot.ProvPrinterConfigDeserialiser;

/** Serialisation of  Prov representation to DOT format. */
public class ProvToDot {
    public final static String DEFAULT_CONFIGURATION_FILE="defaultConfig.xml";
    public final static String DEFAULT_CONFIGURATION_FILE_WITH_ROLE="defaultConfigWithRole.xml";
    public final static String USAGE="prov2dot provFile.xml out.dot out.pdf [configuration.xml]";

    ProvUtilities u=new ProvUtilities();
    ProvFactory of=new ProvFactory();

    public String qnameToString(QName qName) {
        return qName.getLocalPart();
    }

    public static void main(String [] args) throws Exception {
        if ((args==null) || (args.length==0) || (args.length>4)) {
            System.out.println(USAGE);
            return;
        }

        String opmFile=args[0];
        String outDot=args[1];
        String outPdf=args[2];
        String configFile=((args.length==4) ? args[3] : null);

        ProvToDot converter=((configFile==null) ? new ProvToDot() : new ProvToDot(configFile));

        converter.convert(opmFile,outDot,outPdf);
    }


    public ProvToDot() {
        InputStream is=this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIGURATION_FILE);
        init(is);
    }
    public ProvToDot(boolean withRoleFlag) {
        InputStream is;
        if (withRoleFlag) {
            is=this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIGURATION_FILE_WITH_ROLE);
        } else {
            is=this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIGURATION_FILE);
        }
        init(is);
    }

    public ProvToDot(String configurationFile) {
        this();
        init(configurationFile);
    }

    public ProvPrinterConfigDeserialiser getDeserialiser() {
        return ProvPrinterConfigDeserialiser.getThreadProvPrinterConfigDeserialiser();
    }
    
    public void init(String configurationFile) {
        ProvPrinterConfigDeserialiser printerDeserial=getDeserialiser();
        try {
            ProvPrinterConfiguration opc=printerDeserial.deserialiseProvPrinterConfiguration(new File(configurationFile));
            init(opc);
        } catch (JAXBException je) {
            je.printStackTrace();
        }
    }

    public void init(InputStream is) {
        ProvPrinterConfigDeserialiser printerDeserial=getDeserialiser();
        try {
            ProvPrinterConfiguration opc=printerDeserial.deserialiseProvPrinterConfiguration(is);
            init(opc);
        } catch (JAXBException je) {
            je.printStackTrace();
        }
    }

    public void init(ProvPrinterConfiguration configuration) {
        if (configuration==null) return;

        if (configuration.getRelations()!=null) {
            if (configuration.getRelations().getDefault()!=null) {
                defaultRelationStyle=configuration.getRelations().getDefault();
            }
            
            for (RelationStyleMapEntry edge: configuration.getRelations().getRelation()) {
                edgeStyleMap.put(edge.getType(),edge);
            }
        }

        if (configuration.getActivities()!=null) {
            if (configuration.getActivities().isDisplayValue()!=null) {
                this.displayActivityValue=configuration.getActivities().isDisplayValue();
            }
            if (configuration.getActivities().isColoredAsAccount()!=null) {
                this.displayActivityColor=configuration.getActivities().isColoredAsAccount();
            }
            for (ActivityMapEntry process: configuration.getActivities().getActivity()) {
                processNameMap.put(process.getValue(),process.getDisplay());
            }
        }

        if (configuration.getEntities()!=null) {
            if (configuration.getEntities().isDisplayValue()!=null) {
                this.displayEntityValue=configuration.getEntities().isDisplayValue();
            }
            if (configuration.getEntities().isColoredAsAccount()!=null) {
                this.displayEntityColor=configuration.getEntities().isColoredAsAccount();
            }
            for (EntityMapEntry artifact: configuration.getEntities().getEntity()) {
                artifactNameMap.put(artifact.getValue(),artifact.getDisplay());
            }
        }

        if (configuration.getAgents()!=null) {
            if (configuration.getAgents().isDisplayValue()!=null) {
                this.displayAgentValue=configuration.getAgents().isDisplayValue();
            }
            if (configuration.getAgents().isColoredAsAccount()!=null) {
                this.displayAgentColor=configuration.getAgents().isColoredAsAccount();
            }
            for (AgentMapEntry agent: configuration.getAgents().getAgent()) {
                agentNameMap.put(agent.getValue(),agent.getDisplay());
            }
        }

        if (configuration.getAccounts()!=null) {
            if (configuration.getAccounts().getDefaultAccount()!=null) {
                this.defaultAccountLabel=configuration.getAccounts().getDefaultAccount();
            }
            if (configuration.getAccounts().getDefaultColor()!=null) {
                this.defaultAccountColor=configuration.getAccounts().getDefaultColor();
            }
            for (AccountColorMapEntry account: configuration.getAccounts().getAccount()) {
                accountColourMap.put(account.getName(),account.getColor());
            }
        }

        if (configuration.getGraphName()!=null) {
            this.name=configuration.getGraphName();
        }

    }

    public void convert(String opmFile, String dotFile, String pdfFile)
        throws java.io.FileNotFoundException, java.io.IOException, JAXBException {
        convert (ProvDeserialiser.getThreadProvDeserialiser().deserialiseBundle(new File(opmFile)),dotFile,pdfFile);
    }

    public void convert(Bundle graph, String dotFile, String pdfFile)
        throws java.io.FileNotFoundException, java.io.IOException {
        convert(graph,new File(dotFile));
        Runtime runtime = Runtime.getRuntime();
        java.lang.Process proc = runtime.exec("dot -o " + pdfFile + " -Tpdf " + dotFile);
    }

    public void convert(Bundle graph, File file) throws java.io.FileNotFoundException{
        OutputStream os=new FileOutputStream(file);
        convert(graph, new PrintStream(os));
    }

    public void convert(Bundle graph, PrintStream out) {
        List<Relation0> edges=u.getRelations(graph);

        prelude(out);

        if (graph.getRecords().getActivity()!=null) {
            for (Activity p: graph.getRecords().getActivity()) {
                emitActivity(p,out);
            }
        }

        if (graph.getRecords().getEntity()!=null) {
            for (Entity p: graph.getRecords().getEntity()) {
                emitEntity(p,out);
            }
        }

        if (graph.getRecords().getAgent()!=null) {
            for (Agent p: graph.getRecords().getAgent()) {
                emitAgent(p,out);
            }
        }

        for (Relation0 e: edges) {
            if (!(e instanceof HasAnnotation)) 
            emitDependency(e,out);
        }
        

        postlude(out);
       
    }

    boolean collapseAnnotations=true;

    static int embeddedAnnotationCounter=0;
    public void emitAnnotations(HasExtensibility node, PrintStream out) {

    }



    //////////////////////////////////////////////////////////////////////
    ///
    ///                              ELEMENTS
    ///
    //////////////////////////////////////////////////////////////////////

    public void emitActivity(Activity p, PrintStream out) {
        HashMap<String,String> properties=new HashMap();

        emitElement(p.getId(),
                 addActivityShape(p,addActivityLabel(p, addActivityColor(p,properties))),
                 out);

                emitAnnotations("", p,out);
    }

    public void emitEntity(Entity a, PrintStream out) {
        HashMap<String,String> properties=new HashMap();

        emitElement(a.getId(),
                 addEntityShape(a,addEntityLabel(a, addEntityColor(a,properties))),
                 out);

        emitAnnotations("", a,out);
    }

    public void emitAgent(Agent ag, PrintStream out) {
        HashMap<String,String> properties=new HashMap();

        emitElement(ag.getId(),
                 addAgentShape(ag,addAgentLabel(ag, addAgentColor(ag,properties))),
                 out);

        emitAnnotations("", ag,out);

    }

    public void emitAnnotations(String id, HasExtensibility ann, PrintStream out) {

        if ((ann.getAny()==null) || (ann.getAny().isEmpty())
            &&
            (((HasType)ann).getType().isEmpty())) return;

        HashMap<String,String> properties=new HashMap();
        QName newId=annotationId(((Identifiable)ann).getId(),id);
        emitElement(newId,
                    addAnnotationShape(ann,addAnnotationColor(ann,addAnnotationLabel(ann,properties))),
                    out);
        HashMap<String,String> linkProperties=new HashMap();
        emitRelation(qnameToString(newId),
                     qnameToString(((Identifiable)ann).getId()),
                     addAnnotationLinkProperties(ann,linkProperties),out,true);
    }



    int annotationCount=0;
    public QName annotationId(QName id,String node) {
        if (true || id==null) {
            return new QName("http://foo/ignore","ann" + node + (annotationCount++));
        } else {
            return id;
        }
    }

    public HashMap<String,String> addAnnotationLinkProperties(HasExtensibility ann, HashMap<String,String> properties) {
        properties.put("arrowhead","none");
        properties.put("style","dashed");
        properties.put("color","gray");
        return properties;
    }
    
    public HashMap<String,String> addActivityShape(Activity p, HashMap<String,String> properties) {
        properties.put("shape","polygon");
        properties.put("sides","4");
        return properties;
    }


    public HashMap<String,String> addBlankNodeShape(HashMap<String,String> properties) {
        properties.put("shape","point");
        properties.put("label","");
        return properties;
    }

    public HashMap<String,String> addActivityLabel(Activity p, HashMap<String,String> properties) {
        properties.put("label",processLabel(p));
        return properties;
    }

    public HashMap<String,String> addActivityColor(Activity p, HashMap<String,String> properties) {
        if (displayActivityColor) {
            properties.put("color",processColor(p));
            properties.put("fontcolor",processColor(p));
        }
        addColors(p,properties);
        return properties;
    }

    public  HashMap<String,String> addColors(HasExtensibility e, HashMap<String,String> properties) {
        Hashtable<String,List<Object>> table=e.attributesWithNamespace("http://openprovenance.org/Toolbox/dot#");

        List<Object> o=table.get("fillcolor");
        if (o!=null && !o.isEmpty()) {
            properties.put("fillcolor", o.get(0).toString());
            properties.put("style", "filled");
        }
        o=table.get("color");
        if (o!=null && !o.isEmpty()) {
            properties.put("color", o.get(0).toString());
        }
        o=table.get("url");
        if (o!=null && !o.isEmpty()) {
            properties.put("URL", o.get(0).toString());
        }
        return properties;
    }



    public HashMap<String,String> addEntityShape(Entity p, HashMap<String,String> properties) {
        // default is good for entity
        List<Object> types=p.getType();
        for (Object type: types) {
            if (type instanceof QName) {
                QName name=(QName) type;
                if (("Dictionary".equals(name.getLocalPart()))
                    ||
                    ("EmptyDictionary".equals(name.getLocalPart()))) {
                    properties.put("shape","folder");
                }
            }
        }
        return properties;
    }

    public HashMap<String,String> addEntityColor(Entity a, HashMap<String,String> properties) {
        if (displayEntityColor) {
            properties.put("color",entityColor(a));
            properties.put("fontcolor",entityColor(a));
        }
        addColors(a,properties);
        return properties;
    }

    public HashMap<String,String> addEntityLabel(Entity p, HashMap<String,String> properties) {
        properties.put("label",entityLabel(p));
        return properties;
    }

    public HashMap<String,String> addAgentShape(Agent p, HashMap<String,String> properties) {
        properties.put("shape","house");
        //properties.put("sides","8");
        return properties;
    }

    public HashMap<String,String> addAgentLabel(Agent p, HashMap<String,String> properties) {
        properties.put("label",agentLabel(p));
        return properties;
    }

    public HashMap<String,String> addAgentColor(Agent a, HashMap<String,String> properties) {
        if (displayAgentColor) {
            properties.put("color",agentColor(a));
            properties.put("fontcolor",agentColor(a));
        }
        addColors(a,properties);
        return properties;
    }

    public HashMap<String,String> addAnnotationShape(HasExtensibility ann, HashMap<String,String> properties) {
        properties.put("shape","note");
        return properties;
    }
    public HashMap<String,String> addAnnotationLabel(HasExtensibility ann, HashMap<String,String> properties) {
        String label="";
        label=label+"<<TABLE cellpadding=\"0\" border=\"0\">\n";
        for (Object type: ((HasType)ann).getType()) {
            label=label+"	<TR>\n";
            label=label+"	    <TD align=\"left\">" + "type" + ":</TD>\n";
            label=label+"	    <TD align=\"left\">" + convertValue(type) + "</TD>\n";
            label=label+"	</TR>\n";
        }
        for (Object prop: ann.getAny()) {

            if (prop instanceof JAXBElement) {
                JAXBElement je=(JAXBElement) prop;
                QName attribute=je.getName();
                if ("fillcolor".equals(attribute.getLocalPart())) {
                    // no need to display this attribute
                    break;
                }
            } 

            label=label+"	<TR>\n";
            label=label+"	    <TD align=\"left\">" + convertProperty(prop) + ":</TD>\n";
            label=label+"	    <TD align=\"left\">" + convertValue(prop) + "</TD>\n";
            label=label+"	</TR>\n";
        }
        label=label+"    </TABLE>>\n";
        properties.put("label",label);
        properties.put("fontsize","10");
        return properties;
    }

   public String convertValue(Object v) {
       if (v instanceof QName) {
           QName name=(QName) v;
           return name.getLocalPart();
       }
       String label=getPropertyValueFromAny(v);
       System.out.println("Foudn value " + label);
       int i=label.lastIndexOf("#");
       int j=label.lastIndexOf("/");
       return label.substring(Math.max(i,j)+1, label.length());
   }

    public String qnameToUri(QName qname) {
        return qname.getNamespaceURI() + qname.getLocalPart();
    }

    public String convertProperty(Object oLabel) {
        String label=getPropertyFromAny(oLabel);
        int i=label.lastIndexOf("#");
        int j=label.lastIndexOf("/");
        return label.substring(Math.max(i,j)+1, label.length());
    }

    public String getPropertyFromAny (Object o) {
        if (o instanceof JAXBElement) {
            return qnameToUri(((JAXBElement)o).getName());
        } else if (o instanceof org.w3c.dom.Element) {
            return ((org.w3c.dom.Element)o).getTagName();
        } else {
            return o.toString();
        }
    }

    public String getPropertyValueFromAny (Object o) {
        if (o instanceof JAXBElement) {
            Object val=((JAXBElement)o).getValue();
            if (val instanceof QName) {
                QName q=(QName)val;
                return q.getNamespaceURI() + q.getLocalPart();
            } else {
                return "" +  val;
            }
        } else if (o instanceof org.w3c.dom.Element) {
            return ((org.w3c.dom.Element)o).getFirstChild().getNodeValue();
        } else {
            return o.toString();
        }
    }


    public HashMap<String,String> addAnnotationColor(HasExtensibility ann, HashMap<String,String> properties) {
        if (displayAnnotationColor) {
            properties.put("color",annotationColor(ann));
            properties.put("fontcolor","black");
            //properties.put("style","filled");
        }
        return properties;
    }


    boolean displayActivityValue=false;
    boolean displayActivityColor=false;
    boolean displayEntityValue=false;
    boolean displayEntityColor=false;
    boolean displayAgentColor=false;
    boolean displayAgentValue=false;
    boolean displayAnnotationColor=true;

    public String processLabel(Activity p) {
        if (displayActivityValue) {
            return convertActivityName(""+of.getLabel(p));
        } else {
            return qnameToString(p.getId());
        }
    }
    public String processColor(Activity p) {
        // Note, I should compute effective account membership
        List<String> colors=new LinkedList();
        // for (AccountRef acc: p.getAccount()) {
        //     String accountLabel=((Account)acc.getRef()).getId();
        //     String colour=convertAccount(accountLabel);
        //     colors.add(colour);
        // }

        return selectColor(colors);
    }

    // returns the first non transparent color
    public String selectColor(List<String> colors) {
        String tr="transparent";
        for (String c: colors) {
            if (!(c.equals(tr))) return c;
        }
        return tr;
    }
        
    public String entityLabel(Entity p) {
        if (displayEntityValue) {
            return convertEntityName(""+of.getLabel(p));
        } else {
            System.out.println(" ----> " + qnameToString(p.getId()) + "    " + of.getLabel(p));
            return qnameToString(p.getId());
        }
    }
    public String entityColor(Entity p) {
        // Note, I should compute effective account membership
        List<String> colors=new LinkedList();
        // for (AccountRef acc: p.getAccount()) {
        //     String accountLabel=((Account)acc.getRef()).getId();
        //     String colour=convertAccount(accountLabel);
        //     colors.add(colour);
        // }
        return selectColor(colors);
    }
    public String agentColor(Agent p) {
        // Note, I should compute effective account membership
        List<String> colors=new LinkedList();
        // for (AccountRef acc: p.getAccount()) {
        //     String accountLabel=((Account)acc.getRef()).getId();
        //     String colour=convertAccount(accountLabel);
        //     colors.add(colour);
        // }
        return selectColor(colors);
    }


    public String annotationColor(HasExtensibility ann) {
        List<String> colors=new LinkedList();
        colors.add("gray");
        return selectColor(colors);
    }

    public String agentLabel(Agent p) {
        if (displayAgentValue) {
            return convertAgentName(""+of.getLabel(p));
        } else {
            return qnameToString(p.getId());
        }
    }

    HashMap<String,String> processNameMap=new HashMap<String,String>();
    public String convertActivityName(String process) {
        String name=processNameMap.get(process);
        if (name!=null) return name;
        return process;
    }
    HashMap<String,String> artifactNameMap=new HashMap<String,String>();
    public String convertEntityName(String artifact) {
        String name=artifactNameMap.get(artifact);
        if (name!=null) return name;
        return artifact;
    }
    HashMap<String,String> agentNameMap=new HashMap<String,String>();
    public String convertAgentName(String agent) {
        String name=agentNameMap.get(agent);
        if (name!=null) return name;
        return agent;
    }


    int bncounter=0;

    //////////////////////////////////////////////////////////////////////
    ///
    ///                              EDGES
    ///
    //////////////////////////////////////////////////////////////////////

    public void emitDependency(Relation0 e, PrintStream out) {
        HashMap<String,String> properties=new HashMap();

        // List<AccountRef> accounts=e.getAccount();
        // if (accounts.isEmpty()) {
        //     accounts=new LinkedList();
        //     accounts.add(of.newAccountRef(of.newAccount(defaultAccountLabel)));
        // }
            
        // for (AccountRef acc: accounts) {
        //     String accountLabel=((Account)acc.getRef()).getId();
        //     addRelationAttributes(accountLabel,e,properties);
        //        }
        List<QName> others=u.getOtherCauses(e);
        if (others !=null) { // n-ary case
            String bnid="bn" + (bncounter++);

	    
            emitBlankNode(dotify(bnid), addBlankNodeShape(properties), out);

            HashMap<String,String> properties2=new HashMap();
            properties2.put("arrowhead","none");

            HashMap<String,String> properties3=new HashMap();


            emitRelation( qnameToString(u.getEffect(e)),
                          bnid,
                          properties2,
                          out,
                          true);

            relationName(e, properties3);

            if (e instanceof DerivedByInsertionFrom) {
                properties3.put("arrowhead","onormal");
            }
            
            emitRelation( bnid,
                          qnameToString(u.getCause(e)),
                          properties3,
                          out,
                          true);

            HashMap<String,String> properties4=new HashMap();

            for (QName other: others) {
                emitRelation( bnid,
                              qnameToString(other),
                              properties4,
                              out,
                              true);
            }

        } else { // binary case
            relationName(e, properties);
            if (e instanceof Relation) {
                addColors((Relation)e,properties);
            }

            emitRelation( qnameToString(u.getEffect(e)),
                          qnameToString(u.getCause(e)),
                          properties,
                          out,
                          true);

        }
    }

    void relationName(Relation0 e, HashMap<String,String> properties) {
	String l=getShortLabelForRelation(e);
	if (l!=null) {
	    properties.put("taillabel",l);
	    properties.put("labelangle", "60.0");
	    properties.put("labeldistance", "1.5");
	    properties.put("rotation", "20");
	    properties.put("labelfontsize", "8");
	}
    }

    String getLabelForRelation(Relation0 e) {
	if (e instanceof Used) return "used";
	if (e instanceof WasGeneratedBy) return "wasGeneratedBy";
	if (e instanceof WasDerivedFrom) return "wasDerivedFrom";
	if (e instanceof WasAssociatedWith) return "wasAssociatedWith";
	if (e instanceof WasAttributedTo) return "wasAttributedTo";
	return null;
    }
    String getShortLabelForRelation(Relation0 e) {
	if (e instanceof Used) return "used";
	if (e instanceof WasGeneratedBy) return "wgb";
	if (e instanceof WasDerivedFrom) return "wdf";
	if (e instanceof WasAssociatedWith) return "waw";
	if (e instanceof WasAttributedTo) return "wat";
	return null;
    }
    

    public HashMap<String,String> addRelationAttributes(String accountLabel,
                                                        Relation0 e,
                                                        HashMap<String,String> properties) {
        String colour=convertAccount(accountLabel);
        properties.put("color",colour);
        properties.put("fontcolor",colour);
        properties.put("style",getRelationStyle(e));
        addRelationLabel(e,properties);
        return properties;
    }


    /* Displays type if any, role otherwise. */
    public void addRelationLabel(Relation0 e0, HashMap<String,String> properties) {
        String label=null;
        if (!(e0 instanceof Relation)) return;
        Relation e=(Relation)e0;
        List<Object> type=of.getType(e);
        if ((type!=null) || (!type.isEmpty())) {
            label=type.get(0).toString();
        } else if (getRelationPrintRole(e)) {
            String role=of.getRole(e);
            if (role!=null) {
                label=displayRole(role);
                properties.put("fontsize","8");
            }
        }
        if (label!=null) {
            properties.put("label",convertRelationLabel(label));
            if (properties.get("fontsize")==null) {
                properties.put("fontsize","10");
            }
        }
    }

    public String displayRole(String role) {
        return "(" + role + ")";
    }

    public String convertRelationLabel(String label) {
        return label.substring(label.indexOf("#")+1, label.length());
    }


    HashMap<String,String> accountColourMap=new HashMap<String,String>();
    public String convertAccount(String account) {
        String colour=accountColourMap.get(account);
        if (colour!=null) return colour;
        return defaultAccountColor;
    }

    String defaultRelationStyle;
    HashMap<String,RelationStyleMapEntry> edgeStyleMap=new HashMap<String,RelationStyleMapEntry>();
    
    public String getRelationStyle(Relation0 edge) {
        String name=edge.getClass().getName();
        RelationStyleMapEntry style=edgeStyleMap.get(name);
        if (style!=null) return style.getStyle();
        return defaultRelationStyle;
    }

    public boolean getRelationPrintRole(Relation0 edge) {
        String name=edge.getClass().getName();
        RelationStyleMapEntry style=edgeStyleMap.get(name);
        if (style!=null) {
            Boolean flag=style.isPrintRole();
            if (flag==null) return false;
            return flag;
        } else {
            return false;
        }
    }

    
    //////////////////////////////////////////////////////////////////////
    ///
    ///                              DOT FORMAT GENERATION
    ///
    //////////////////////////////////////////////////////////////////////
    
    
    String name;
    String defaultAccountLabel;
    String defaultAccountColor;

    /* make name compatible with dot notation*/
    
    public String dotify(String name) {
        return "n" + name.replace('-','_').replace('.','_').replace('/','_');
    }

    public void emitElement(QName name, HashMap<String,String> properties, PrintStream out) {
        StringBuffer sb=new StringBuffer();
        sb.append(""+dotify(qnameToString(name)));
        emitProperties(sb,properties);
        out.println(sb.toString());
    }


    public void emitBlankNode(String bnid, HashMap<String,String> properties, PrintStream out) {
        StringBuffer sb=new StringBuffer();
	sb.append(bnid);
        emitProperties(sb,properties);
        out.println(sb.toString());
    }


    public void emitRelation(String src, String dest, HashMap<String,String> properties, PrintStream out, boolean directional) {
        StringBuffer sb=new StringBuffer();
        sb.append(dotify(src));
        if (directional) {
            sb.append(" -> ");
        } else {
            sb.append(" -- ");
        }
        sb.append(dotify(dest));
        emitProperties(sb,properties);
        out.println(sb.toString());
    }

    public void emitProperties(StringBuffer sb,HashMap<String,String> properties) {
        sb.append(" [");
        boolean first=true;
        for (String key: properties.keySet()) {
            if (first) {
                first=false;
            } else {
                sb.append(",");
            }
            String value=properties.get(key);
            sb.append(key);
            if (value.startsWith("<")) {
                sb.append("=");
                sb.append(value);
            } else {
                sb.append("=\"");
                sb.append(value);
                sb.append("\"");
            }


        }
        sb.append("]");
    }

    void prelude(PrintStream out) {
        out.println("digraph " + name + " { rankdir=\"BT\"; ");
    }

    void postlude(PrintStream out) {
        out.println("}");
        out.close();
    }


    


    
}


