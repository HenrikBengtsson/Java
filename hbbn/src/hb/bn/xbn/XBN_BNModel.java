package hb.bn.xbn;

/*
 Based on the XML Belief Network File Format: XBN Format (see Microsoft)
 This package making use of the IBM XML Parser (see IBM).
 Author: Henrik Bengtsson, hb@maths.lth.se
 Date: June 1999.
 */

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hb.format.Format;
import java.util.List;
import java.util.ArrayList;



public class XBN_BNModel extends XBN_Element {
  public String name;
  public XBN_Variables variables;
  public XBN_Distributions distributions;
  public XBN_Structure structure;
  public XBN_StaticProperties staticproperties;
		//  public XBN_DYNAMICPROPERTIES dynamicproperties;

	public XBN_BNModel(Node node) {
    super(node);
    NAME = "BNMODEL";
    name = getAttributeAsString("NAME");
  }

	public boolean parse() {
 		NodeList children = node.getChildNodes();
 		if (children != null) {
 			int len = children.getLength();
      for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
        if (childName.equalsIgnoreCase("VARIABLES")) {
          variables = new XBN_Variables(child);
          variables.parse();
        } else if (childName.equalsIgnoreCase("DISTRIBUTIONS")) {
          distributions = new XBN_Distributions(child);
          distributions.parse();
        } else if (childName.equalsIgnoreCase("STRUCTURE")) {
          structure = new XBN_Structure(child);
          structure.parse();
        } else if (childName.equalsIgnoreCase("STATICPROPERTIES")) {
          staticproperties = new XBN_StaticProperties(child);
          staticproperties.parse();
        } else if (childName.equalsIgnoreCase("DYNAMICPROPERTIES")) {
          printWarning("This software does not support dynamic properties, "+
                     "i.e. the <DYNAMICPROPERTIES> structure is not parsed.");
        } else if (readTEXT(child)) {}
    	  else 
          unexpectedNode(child);
	  	}
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<BNMODEL", p.add(indent+1).add('\0')));
    if (name != null)
      s.append(" NAME=\"").append(name).append("\"");
    s.append(">\n");
    if (staticproperties != null)
      s.append(staticproperties.toString(indent+2));
		//    if (dynamicproperties != null)
		//      s.append(dynamicproperties.toString(indent+2));
    if (variables != null)
      s.append(variables.toString(indent+2));
    if (structure != null)
      s.append(structure.toString(indent+2));
    if (distributions != null)
      s.append(distributions.toString(indent+2));
    s.append(Format.sprintf("%*c</BNMODEL>\n", p.add(indent+1).add('\0')));
    return s.toString();
  }
} // XBN_BNModel


/* HISTORY:

990611
* Change all class names to lower case.
990610
* Starting to support comments to.
990609
* Created from XBN_ANALYSISNOTEBOOK. XBN_MODEL has to be public.

 */
