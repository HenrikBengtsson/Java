package hb.bn.xbs;

                    
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hb.format.Format;



public class XML_Parameter extends XML_Command {
  public String name = "";
  public String value = "";

	public XML_Parameter(Node node) {
    super(node, "PARAMETER");
    Node attr = attributes.getNamedItem("NAME");
    if (attr != null)
      name = attr.getNodeValue();
  }

	public boolean parse() {
    NodeList children = node.getChildNodes();
    value = node.getNodeValue();
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
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<%s", p.add(indent+1).add('\0').add(NAME)));
    if (name != null)
      s.append(" NAME=\"").append(name).append("\"");
    s.append(">");
    if (value != null)
      s.append(value);
    s.append(Format.sprintf("%*c<%s>\n", p.add(indent+1).add('\0').add(NAME)));
    return s.toString();
  } 
}


/* HISTORY:

2000-04-04
* Extracted and put into a file itself.

990614
* Created.

 */
