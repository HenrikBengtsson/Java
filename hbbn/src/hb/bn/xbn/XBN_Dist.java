package hb.bn.xbn;

/*
 Based on the XML Belief Network File Format: XBN Format (see Microsoft)
 This package making use of the IBM XML Parser (see IBM).
 Author: Henrik Bengtsson, hb@maths.lth.se
 Date: June 1999.
 */

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hb.format.Format;
import java.util.List;
import java.util.ArrayList;


public class XBN_Dist extends XBN_Element {
  public String      type;
  public XBN_CondSet condset;
  public XBN_DPIs    dpis;
  public XBN_Private privatE;
  public XBN_Shared  shared;

	public XBN_Dist(Node node) {
    super(node, "DIST");
    Node attr = attributes.getNamedItem("TYPE");
    if (attr != null) {
      type = attr.getNodeValue();
    }
  }
  
	public boolean parse() {
    String typeStr = getAttributeAsString("TYPE");
    if (!typeStr.equals("discrete") && !typeStr.equals("ci"))
      throw new RuntimeException(
        "Sorry, but this version only accept 'discrete' and 'ci' "+
        "distributions. The specified network contains a distribution "+
        "of type '"+typeStr+"'.");

    NodeList children = node.getChildNodes();
    if (children != null) {
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equals("CONDSET")) {
          condset = new XBN_CondSet(child);
          condset.parse();
 				} else if (childName.equals("DPIS")) {
          dpis = new XBN_DPIs(child);
          dpis.parse();
 				} else if (childName.equals("PRIVATE")) {
          privatE = new XBN_Private(child);
          privatE.parse();
 				} else if (childName.equals("SHARED")) {
          shared = new XBN_Shared(child);
          shared.parse();
 				} else if (childName.equals("REFERENCE")) {
          printWarning("This software does not support references, "+
                       "i.e. the <REFERENCE> structure is not parsed.");
 				} else if (readTEXT(child)) {}
        else
          unexpectedNode(child);
			}
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<DIST TYPE=\"%s\">\n", 
      p.add(indent+1).add('\0').add(type)));
    if (condset != null)
      s.append(condset.toString(indent+2));
    if (privatE != null)
      s.append(privatE.toString(indent+2));
    if (dpis != null)
      s.append(dpis.toString(indent+2));
    s.append(Format.sprintf("%*c</DIST>\n", p.add(indent+1).add('\0')));
    return s.toString();
  }
}


/* HISTORY:

990611
* Change all class names to lower case.
990610
* Starting to support comments to.
990609
* Created from XBN_ANALYSISNOTEBOOK. XBN_MODEL has to be public.

 */
