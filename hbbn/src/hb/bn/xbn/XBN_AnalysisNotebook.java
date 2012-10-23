package hb.bn.xbn;
                    
/*
 Based on the XML Belief Network File Format: XBN Format (see Microsoft)
 This package making use of the IBM XML Parser (see IBM).
 Author: Henrik Bengtsson, hb@maths.lth.se
 Date: June 1999.
 */


import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hb.format.Format;


public class XBN_AnalysisNotebook extends XBN_Element {
  public String name;
  public String root;
  public String filename;
  public XBN_BNModel[] bnmodel;

	public XBN_AnalysisNotebook(Node node) {
    super(node);
    NAME = "AnalysisNotebook";
    name     = getAttributeAsString("NAME");
    root     = getAttributeAsString("ROOT");
    filename = getAttributeAsString("FILENAME");
  }

	public boolean parse() {
 		NodeList children = node.getChildNodes();
 		if (children != null) {
      List bnmodels = new ArrayList();
 			int len = children.getLength();
      for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
        if (childName.equalsIgnoreCase("BNMODEL")) {
          XBN_BNModel bnmodel = new XBN_BNModel(child);
          bnmodel.parse();
          bnmodels.add(bnmodel);
        } else if (readTEXT(child)) {}
    	  else 
          unexpectedNode(child);
	  	}
      bnmodel = new XBN_BNModel[bnmodels.size()];
      for (int i=0; i<bnmodel.length; i++)
        bnmodel[i] = (XBN_BNModel)bnmodels.get(i);
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<%s", p.add(indent+1).add('\0').add(NAME)));
    if (name != null)
      s.append(" NAME=\"").append(name).append("\"");
    if (root != null)
      s.append(" ROOT=\"").append(root).append("\"");
    if (filename != null)
      s.append(" FILENAME=\"").append(filename).append("\"");
    s.append(">\n");
    if (bnmodel != null)
      for (int i=0; i<bnmodel.length; i++)
        s.append(bnmodel[i].toString(indent+2));
    s.append(Format.sprintf("%*c</%s>\n", p.add(indent+1).add('\0').add(NAME)));
    return s.toString();
  }
} // XBN_AnalysisNotebook



/* HISTORY:

990611
* Changed all class names to lower case.
990608
* Now parsing all XBN-elements.
990607
* Created with template from com.DOMWriter.java (XML Parser by IBM)

 */
