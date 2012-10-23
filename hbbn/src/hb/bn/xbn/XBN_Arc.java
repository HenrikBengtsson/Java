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

public class XBN_Arc extends XBN_Element {
  public String parent, child;

	public XBN_Arc(Node node) {
    super(node);
    parent = getAttributeAsString("PARENT");
    child  = getAttributeAsString("CHILD");
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
    s.append(Format.sprintf("%*c<ARC", p.add(indent+1).add('\0')));
    if (parent != null)
      s.append(" PARENT=\"").append(parent).append("\"");
    if (child != null)
      s.append(" CHILD=\"").append(child).append("\"");
    s.append("/>\n");
    return s.toString();
  }
} // XBN_Arc


/* HISTORY:

990611
* Change all class names to lower case.
990610
* Starting to support comments to.
990609
* Created from XBN_ANALYSISNOTEBOOK. XBN_MODEL has to be public.

 */
