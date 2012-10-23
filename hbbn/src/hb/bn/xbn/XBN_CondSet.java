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


public class XBN_CondSet extends XBN_Element {
  public XBN_CondElem[] condelem;

	public XBN_CondSet(Node node) {
    super(node, "CONDSET");
  }

	public boolean parse() {
			//    Format.printf("%s.parse\n", p.add(getClassName()));
    NodeList children = node.getChildNodes();
    if (children != null) {
      List condelems = new ArrayList();
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equalsIgnoreCase("CONDELEM")) {
          XBN_CondElem condelem = new XBN_CondElem(child);
          condelem.parse();
          condelems.add(condelem);
 				} else if (childName.equals("#text")) {
        } else
          unexpectedNode(child);
			}
      condelem = new XBN_CondElem[condelems.size()];
      for (int i=0; i<condelem.length; i++)
        condelem[i] = (XBN_CondElem)condelems.get(i);
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<CONDSET>\n", p.add(indent+1).add('\0')));
    if (condelem != null)
      for (int i=0; i<condelem.length; i++)
        s.append(condelem[i].toString(indent+2));
    s.append(Format.sprintf("%*c</CONDSET>\n", p.add(indent+1).add('\0')));
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
