package hb.bn.xbs;

                    
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hb.format.Format;


public class XML_Observe extends XML_Command {
  public String var = null;
  public String statename = null;

	public XML_Observe(Node node) {
    super(node, "OBSERVE");
    Node attr = attributes.getNamedItem("VAR");
    if (attr != null)
      var = attr.getNodeValue();
    attr = attributes.getNamedItem("STATENAME");
    if (attr != null)
      statename = attr.getNodeValue();
  }

	public boolean parse() {
    NodeList children = node.getChildNodes();
    if (children != null) {
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equals("#text")) {
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
    if (var != null)
      s.append(" VAR=\"").append(var).append("\"");
    if (statename != null)
      s.append(" STATENAME=\"").append(var).append(statename);
    s.append("/>\n");
    return s.toString();
  } 
}

/* HISTORY:

2000-04-04
* Extracted and put into a file itself.

990614
* Created.

 */
