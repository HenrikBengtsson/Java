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


public class XBN_Variables extends XBN_Element {
  public XBN_Var[] var;

	public XBN_Variables(Node node) {
    super(node, "VARIABLES");
  }
  
	public boolean parse() {
			//    Format.printf("%s.parse\n", p.add(getClassName()));
    NodeList children = node.getChildNodes();
    if (children != null) {
      List vars = new ArrayList();
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equalsIgnoreCase("VAR")) {
          XBN_Element var = new XBN_Var(child);
          var.parse();
          vars.add(var);
 				} else if (childName.equals("#text")) {
 				} else if (childName.equals("#comment")) {
        } else
          unexpectedNode(child);
			}
      var = new XBN_Var[vars.size()];
      for (int i=0; i<var.length; i++)
        var[i] = (XBN_Var)vars.get(i);
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<VARIABLES>\n", p.add(indent+1).add('\0')));
    if (var != null) {
			for (int i=0; i<var.length; i++)
        s.append(var[i].toString(indent+2));
    }
    s.append(Format.sprintf("%*c</VARIABLES>\n", p.add(indent+1).add('\0')));
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
