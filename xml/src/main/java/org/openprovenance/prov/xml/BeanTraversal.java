package org.openprovenance.prov.xml;
import java.util.List;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Element;


public class BeanTraversal {
    private BeanConstructor c;

    public BeanTraversal(BeanConstructor c) {
        this.c=c;
    }

    public Object convert(Bundle cont) {
        List lnkRecords=new LinkedList();
        List aRecords=new LinkedList();
        List eRecords=new LinkedList();
        List agRecords=new LinkedList();
        for (Entity e: cont.getRecords().getEntity() ) {
            eRecords.add(convert(e));
        }
        for (Activity a: cont.getRecords().getActivity() ) {
            aRecords.add(convert(a));
        }
        for (Agent ag: cont.getRecords().getAgent() ) {
            agRecords.add(convert(ag));
        }
        for (Object lnk: cont.getRecords().getDependencies().getUsedOrWasGeneratedByOrWasStartedBy() ) {
            Object o=convertRelation(lnk);
            if (o!=null) lnkRecords.add(o);
        }
        return c.convertBundle(cont.getNss(),
                                  aRecords,
                                  eRecords,
                                  agRecords,
                                  lnkRecords);
    }

    public Object convertAttribute(Element a) {
        return c.convertAttribute(a.getNodeName(),
                                  c.convertAttributeValue(a));
    }

    public Object convertAttribute(Object a) {
        if (a instanceof JAXBElement) {
            JAXBElement je=(JAXBElement) a;
            QName q=je.getName();
            Object o=je.getValue();
            String value;
            value=o.toString();
                
            return c.convertAttribute(c.convert(q),
                                      value);
        } if (a instanceof Element) {
            return convertAttribute((Element) a);
        } else {
            return c.convertAttribute(a.toString(), a.toString());
        }
    }
    public List<Object> convertTypeAttributes(HasType e) {
        List attrs=new LinkedList();
        for (Object a: e.getType()) {
            attrs.add(convertTypedLiteral(a));
        }
        return attrs;
    }
    public Object convertLabelAttribute(HasLabel e) {
        Object a=e.getLabel();
        if (a==null) return null;
        return convertTypedLiteral(a);
    }
    public List<Object> convertAttributes(HasExtensibility e) {
        List attrs=new LinkedList();
        for (Object a: e.getAny()) {
            attrs.add(convertAttribute(a));
        }
        return attrs;
    }
    public String quoteWrap(Object b) {
        return "\"" + b + "\"";
    }
    public Object convertTypedLiteral(Object a) {
        if (a instanceof QName) {
            QName q=(QName) a;
            return c.convertTypedLiteral("xsd:QName",quoteWrap(c.convert(q)));
        } if (a instanceof URIWrapper) {
            URIWrapper u=(URIWrapper) a;
            return c.convertTypedLiteral("xsd:anyURI",quoteWrap(u));
        } if (a instanceof Boolean) {
            Boolean b=(Boolean) a;
            return c.convertTypedLiteral("xsd:boolean",quoteWrap(b));
        } if (a instanceof String) {
            String b=(String) a;
            return c.convertTypedLiteral("xsd:string",quoteWrap(b));
        } if (a instanceof Double) {
            Double b=(Double) a;
            return c.convertTypedLiteral("xsd:double",quoteWrap(b));
        } if (a instanceof Integer) {
            Integer b=(Integer) a;
            return c.convertTypedLiteral("xsd:int",quoteWrap(b));
        } else {
            throw new UnsupportedOperationException("Unknown typedLiteral "+a + "(" + a.getClass() +")");
        }
    }

    public Object convert(Entity e) {
        List tAttrs=convertTypeAttributes(e);
        List otherAttrs=convertAttributes(e);
        Object lAttr=convertLabelAttribute(e);


        return c.convertEntity(c.convert(e.getId()),tAttrs,lAttr,otherAttrs);
    }

    public Object convert(Activity e) {
        List tAttrs=convertTypeAttributes(e);
        List otherAttrs=convertAttributes(e);
        Object lAttr=convertLabelAttribute(e);

        return c.convertActivity(c.convert(e.getId()),tAttrs,lAttr,otherAttrs,e.getStartTime(), e.getEndTime());
    }

    public Object convert(Agent e) {
        List tAttrs=convertTypeAttributes(e);
        List otherAttrs=convertAttributes(e);
        Object lAttr=convertLabelAttribute(e);

        return c.convertAgent(c.convert(e.getId()),tAttrs,lAttr,otherAttrs);
    }

    public Object convertRelation(Object o) {
        // no visitors, so ...
        if (o instanceof WasAssociatedWith) {
            return convert((WasAssociatedWith) o);
        } else if (o instanceof Used) {
            return convert((Used) o);
        } else if (o instanceof WasDerivedFrom) {
            return convert((WasDerivedFrom) o);
        } else if (o instanceof HasAnnotation) {
            return convert((HasAnnotation) o);
        } else if (o instanceof WasInformedBy) {
            return convert((WasInformedBy) o);
        } else if (o instanceof AlternateOf) {
            return convert((AlternateOf) o);
        } else if (o instanceof SpecializationOf) {
            return convert((SpecializationOf) o);
        } else if (o instanceof WasGeneratedBy) {
            return convert((WasGeneratedBy) o);
        } else {
            throw new UnsupportedOperationException("Unknown relation type "+o);
        }
    }

    public Object convert(WasAssociatedWith o) {
        List tAttrs=convertTypeAttributes((HasType)o);
        List otherAttrs=convertAttributes((HasExtensibility)o);
        return c.convertWasAssociatedWith(c.convert(o.getId()),
                                          tAttrs,
                                          otherAttrs,
                                          c.convert(o.getActivity().getRef()),
                                          (o.getAgent()==null)? null: c.convert(o.getAgent().getRef()),
                                          (o.getPlan()==null)? null: c.convert(o.getPlan().getRef()));
    }

    public Object convert(Used o) {
        List tAttrs=convertTypeAttributes((HasType)o);
        List otherAttrs=convertAttributes((HasExtensibility)o);
        return c.convertUsed(c.convert(o.getId()),
                             tAttrs,
                             otherAttrs,
                             c.convert(o.getActivity().getRef()),
                             c.convert(o.getEntity().getRef()));
    }

    public Object convert(WasDerivedFrom o) {
        List tAttrs=convertTypeAttributes((HasType)o);
        List otherAttrs=convertAttributes((HasExtensibility)o);
        return c.convertWasDerivedFrom(c.convert(o.getId()),tAttrs,otherAttrs,
                                       c.convert(o.getGeneratedEntity().getRef()),
                                       c.convert(o.getUsedEntity().getRef()));
    }

    public Object convert(HasAnnotation o) {
        List tAttrs=null;
        List otherAttrs=convertAttributes((HasExtensibility)o);
        return c.convertHasAnnotation(c.convert(o.getId()),tAttrs,otherAttrs);
    }

    public Object convert(WasInformedBy o) {
        List tAttrs=convertTypeAttributes((HasType)o);
        List otherAttrs=convertAttributes((HasExtensibility)o);
        return c.convertWasInformedBy(c.convert(o.getId()),tAttrs,otherAttrs);
    }

    public Object convert(AlternateOf o) {

        return c.convertAlternateOf(c.convert(o.getEntity2().getRef()),
                                    c.convert(o.getEntity1().getRef()));
    }

    public Object convert(SpecializationOf o) {
        return c.convertSpecializationOf(c.convert(o.getSpecializedEntity().getRef()),
                                         c.convert(o.getGeneralEntity().getRef()));
    }

    public Object convert(WasGeneratedBy o) {
        List tAttrs=convertTypeAttributes((HasType)o);
        List otherAttrs=convertAttributes((HasExtensibility)o);

        return c.convertWasGeneratedBy(c.convert(o.getId()),tAttrs,otherAttrs,
                                       c.convert(o.getEntity().getRef()),
                                       c.convert(o.getActivity().getRef()));
    }
}
