package hb.bn.xbs;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hb.format.Format;


public class XML_Echo extends XML_Command {
  public String value = null;

	public XML_Echo(Node node) {
    super(node, "ECHO");
  }

	public boolean parse() {
    NodeList children = node.getChildNodes();
    if (children != null) {
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equals("#text")) {
          value = child.getNodeValue();
 				} else if (childName.equals("#comment")) {
        } else
          unexpectedNode(child);
			}
		}
    if (value == null)
      value = "";
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<%s>", p.add(indent+1).add('\0').add(NAME)));
    if (value != null)
      s.append(value);
    s.append(Format.sprintf("</%s>\n", p.add(NAME)));
    return s.toString();
  } 
}


/* HISTORY:

2000-04-04
* Extracted and put into a file itself.

990614
* Created.

 */
