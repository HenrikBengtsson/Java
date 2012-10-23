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


public class XBN_Var extends XBN_Element {
  public String name;
  public String type;
  public int xpos, ypos;
  public XBN_Description description;
  public XBN_StateName[] statename;

	public XBN_Var(Node node) {
    super(node, "VAR");
    name = getAttributeAsString("NAME");
    type = getAttributeAsString("TYPE");
    if (!type.equals("discrete"))
       throw new RuntimeException(
        "Sorry, but this version only accept discrete "+
        "variables. The specified network contains a variable "+
        "of type '"+type+"'.");
    xpos = getAttributeAsInt("XPOS");
    ypos = getAttributeAsInt("YPOS");
  }

	public String[] getStatenameAsStrings() {
    String[] sname = new String[statename.length];
    for (int i=0; i<sname.length; i++)
      sname[i] = statename[i].value;
    return sname;
	}

	public boolean parse() {
			//    Format.printf("%s.parse\n", p.add(getClassName()));
    NodeList children = node.getChildNodes();
    if (children != null) {
  		int len = children.getLength();
	  	List stateNames = new ArrayList();
		  for (int i = 0; i < len; i++) {
			  Node child = children.item(i);
 				String childName;
 				int childType = child.getNodeType();
				if (childType == child.ELEMENT_NODE) {
					childName = child.getNodeName();
					if (childName.equals("DESCRIPTION")) {
						description = new XBN_Description(child);
            description.parse();
					} else if (childName.equals("STATENAME")) {
						XBN_Element statename = new XBN_StateName(child);
            statename.parse();
            stateNames.add(statename);
					} else if (childName.equals("PROPERTY")) {
            printWarning("This software does not support properties of "+
                       "variables, i.e. the <PROPERTY> structure is not "+
                       "parsed.");
          } else if (readTEXT(child)) {}
          else 
            unexpectedNode(child);
        } else if (readTEXT(child)) {}
				else
          unexpectedNode(child);
			} // for
			int nbrOfStates = stateNames.size();
			statename = new XBN_StateName[nbrOfStates];
			for (int i=0; i<statename.length; i++)
				statename[i] = (XBN_StateName)stateNames.get(i);
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<VAR", p.add(indent+1).add('\0')));
    if (name != null)
      s.append(" NAME=\"").append(name).append("\"");
    if (type != null)
      s.append(" TYPE=\"").append(type).append("\"");
    if (xpos != Integer.MIN_VALUE)
      s.append(" XPOS=\"").append(xpos).append("\"");
    if (ypos != Integer.MIN_VALUE)
      s.append(" YPOS=\"").append(ypos).append("\"");
    s.append(">\n");
    if (description != null)
      s.append(description.toString(indent+2));
    if (statename != null) {
			for (int i=0; i<statename.length; i++)
        s.append(statename[i].toString(indent+2));
    }
    s.append(Format.sprintf("%*c</VAR>\n", p.add(indent+1).add('\0')));
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
