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


public class XBN_StaticProperties extends XBN_Element {
  public XBN_ValueElement format;
  public XBN_ValueElement version;
  public XBN_ValueElement creator;

	public XBN_StaticProperties(Node node) {
    super(node);
    NAME = "STATICPROPERTIES";
  }
  
	public boolean parse() {
			//    Format.printf("%s.parse\n", p.add(getClassName()));
    NodeList children = node.getChildNodes();
    if (children != null) {
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equalsIgnoreCase("FORMAT")) {
          format = new XBN_Format(child);
          format.parse();
 				} else if (childName.equalsIgnoreCase("VERSION")) {
          version = new XBN_Version(child);
          version.parse();
 				} else if (childName.equalsIgnoreCase("CREATOR")) {
          creator = new XBN_Creator(child);
          creator.parse();
 				} else if (childName.equals("#text")) {
        } else
          unexpectedNode(child);
			}
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<%s>\n", p.add(indent+1).add('\0').add(NAME)));
    if (format != null)
      s.append(format.toString(indent+2));
    if (version != null)
      s.append(version.toString(indent+2));
    if (creator != null)
      s.append(creator.toString(indent+2));
    s.append(Format.sprintf("%*c</%s>\n", p.add(indent+1).add('\0').add(NAME)));
    return s.toString();
  }
} // XBN_StaticProperties

/* HISTORY:

990611
* Change all class names to lower case.
990610
* Starting to support comments to.
990609
* Created from XBN_ANALYSISNOTEBOOK. XBN_MODEL has to be public.

 */
