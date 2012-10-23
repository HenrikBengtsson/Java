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


public class XBN_Distributions extends XBN_Element {
  public XBN_Dist[] dist;

	public XBN_Distributions(Node node) {
    super(node, "DISTRIBUTIONS");
  }
  
	public boolean parse() {
			//    Format.printf("%s.parse\n", p.add(getClassName()));
    NodeList children = node.getChildNodes();
    if (children != null) {
      List dists = new ArrayList();
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equalsIgnoreCase("DIST")) {
          XBN_Dist dist = new XBN_Dist(child);
          dist.parse();
          dists.add(dist);
 				} else if (childName.equals("#text")) {
 				} else if (childName.equals("#comment")) {
        } else
          unexpectedNode(child);
			}
      dist = new XBN_Dist[dists.size()];
      for (int i=0; i<dist.length; i++)
        dist[i] = (XBN_Dist)dists.get(i);
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<DISTRIBUTIONS>\n", p.add(indent+1).add('\0')));
    if (dist != null) {
			for (int i=0; i<dist.length; i++)
        s.append(dist[i].toString(indent+2));
    }
    s.append(Format.sprintf("%*c</DISTRIBUTIONS>\n", p.add(indent+1).add('\0')));
    return s.toString();
  }
} // XBN_Distributions


/* HISTORY:

990611
* Change all class names to lower case.
990610
* Starting to support comments to.
990609
* Created from XBN_ANALYSISNOTEBOOK. XBN_MODEL has to be public.

 */
