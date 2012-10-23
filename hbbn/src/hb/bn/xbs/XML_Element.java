package hb.bn.xbs;

/*
 Based on the XML Belief Network File Format: XBN Format (see Microsoft)
 This package making use of the IBM XML Parser (see IBM).
 Author: Henrik Bengtsson, hb@maths.lth.se
 Date: June 1999.
 */
  
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import hb.format.Parameters;

public abstract class XML_Element {
  protected Parameters p = new Parameters();
  public String NAME;
  public Node node;
  public NamedNodeMap attributes;

	public XML_Element(Node node, String NAME) {
    this.node = node;
    attributes = node.getAttributes();
    this.NAME = NAME;
  }

	public XML_Element(Node node) {
    this(node, null);
  }

	public String getClassName() {
    String cname = getClass().getName();
    int pos = cname.lastIndexOf('.');
    return cname.substring(pos+1);
	}

  public Node getAttribute(String attrName) {
    Node attr = attributes.getNamedItem(attrName);
    if (attr == null)
      throw new RuntimeException("ERROR: Expected an attribute called '"+
        attrName+"'.");
    return attr;
	}

  public int getAttributeAsInt(String attrName) {
    Node attr = attributes.getNamedItem(attrName);
    if (attr == null)
      return Integer.MIN_VALUE;
    return Integer.valueOf(attr.getNodeValue()).intValue();
	}

  public String getAttributeAsString(String attrName) {
    Node attr = attributes.getNamedItem(attrName);
    if (attr == null)
      return null;
    return attr.getNodeValue();
  }

	public boolean isNode(Node node, String name) {
    return name.equals(node.getNodeName());
	}

  public void unexpectedNode(Node node) {
    throw new RuntimeException("PARSING ERROR: Unexpected node <"+
         node.getNodeName()+"> in <"+this.node.getNodeName()+">.");
	}

  public boolean readTEXT(Node node) {
    if (!"#text".equals(node.getNodeName()))
      return false;
    return true;
  }

  abstract public boolean parse();

  public void printWarning(String msg) {
    System.err.println("Warning: "+msg);
	}

	abstract public String toString(int indent);

	public String toString() {
    return toString(0);
  }
}


/* HISTORY:

2000-06-14
* Made all import explicit.
990614
* Created from hb.xml.xbn.XBN_Element.

 */
