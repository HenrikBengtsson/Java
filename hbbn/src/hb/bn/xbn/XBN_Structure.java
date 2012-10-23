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


public class XBN_Structure extends XBN_Element {
  public XBN_Arc[] arc;

	public XBN_Structure(Node node) {
    super(node, "STRUCTURE");
  }
  
	public boolean parse() {
			//    Format.printf("%s.parse\n", p.add(getClassName()));
    NodeList children = node.getChildNodes();
    if (children != null) {
      List arcs = new ArrayList();
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equalsIgnoreCase("ARC")) {
          XBN_Element arc = new XBN_Arc(child);
          arc.parse();
          arcs.add(arc);
 				} else if (childName.equals("#text")) {
 				} else if (childName.equals("#comment")) {
        } else
          unexpectedNode(child);
			}
      arc = new XBN_Arc[arcs.size()];
      for (int i=0; i<arc.length; i++)
        arc[i] = (XBN_Arc)arcs.get(i);
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<STRUCTURE>\n", p.add(indent+1).add('\0')));
    if (arc != null) {
			for (int i=0; i<arc.length; i++)
        s.append(arc[i].toString(indent+2));
    }
    s.append(Format.sprintf("%*c</STRUCTURE>\n", p.add(indent+1).add('\0')));
    return s.toString();
  }
} // XBN_Structure


/* HISTORY:

990611
* Change all class names to lower case.
990610
* Starting to support comments to.
990609
* Created from XBN_ANALYSISNOTEBOOK. XBN_MODEL has to be public.

 */
