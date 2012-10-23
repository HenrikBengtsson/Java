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


public class XBN_Shared extends XBN_Element {
  public String name;
  public String states;

	public XBN_Shared(Node node) {
    super(node, "SHARED");
    Node attr = attributes.getNamedItem("NAME");
    if (attr != null) {
      name = attr.getNodeValue();
    }
    attr = attributes.getNamedItem("STATES");
    if (attr != null) {
      states = attr.getNodeValue();
    }
  }
  
	public boolean parse() {
			//    Format.printf("%s.parse\n", p.add(getClassName()));
    NodeList children = node.getChildNodes();
    if (children != null) {
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
        if (childName.equals("#text")) {
        } else
          unexpectedNode(child);
			}
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<SHARED", p.add(indent+1).add('\0')));
    if (name != null)
      s.append(" NAME=\"").append(name).append("\"");
    if (states != null)
      s.append(" STATES=\"").append(name).append("\"");
    s.append("/>\n");
    return s.toString();
  }
} // XBN_Shared


/* HISTORY:

990611
* Change all class names to lower case.
990610
* Starting to support comments to.
990609
* Created from XBN_ANALYSISNOTEBOOK. XBN_MODEL has to be public.

 */
