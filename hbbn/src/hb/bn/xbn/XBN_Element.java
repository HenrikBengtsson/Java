package hb.bn.xbn;

/*
 Based on the XML Belief Network File Format: XBN Format (see Microsoft)
 This package making use of the IBM XML Parser (see IBM).
 Author: Henrik Bengtsson, hb@maths.lth.se
 Date: June 1999.
 */

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import hb.format.Parameters;


public abstract class XBN_Element {
  String NAME;
  Node node;
  NamedNodeMap attributes;

	public XBN_Element(Node node, String NAME) {
    this.node = node;
    attributes = node.getAttributes();
    this.NAME = NAME;
  }

	public XBN_Element(Node node) {
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

  Parameters p = new Parameters();
}


/* HISTORY:

990611
* Change all class names to lower case.
990610
* Starting to support comments to.
990609
* Created from XBN_ANALYSISNOTEBOOK. XBN_MODEL has to be public.

 */
