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


public class XBN_DPIs extends XBN_Element {
  public XBN_DPI[] dpi;

	public XBN_DPIs(Node node) {
    super(node, "DPIS");
  }
  
	public boolean parse() {
			//    Format.printf("%s.parse\n", p.add(getClassName()));
    NodeList children = node.getChildNodes();
    if (children != null) {
      List dpis = new ArrayList();
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equalsIgnoreCase("DPI")) {
          XBN_DPI dpi = new XBN_DPI(child);
          dpi.parse();
          dpis.add(dpi);
 				} else if (childName.equals("#text")) {
        } else
          unexpectedNode(child);
			}
      dpi = new XBN_DPI[dpis.size()];
      for (int i=0; i<dpi.length; i++)
        dpi[i] = (XBN_DPI)dpis.get(i);
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<DPIS>\n", p.add(indent+1).add('\0')));
    if (dpi != null)
      for (int i=0; i<dpi.length; i++)
        s.append(dpi[i].toString(indent+2));
    s.append(Format.sprintf("%*c</DPIS>\n", p.add(indent+1).add('\0')));
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
