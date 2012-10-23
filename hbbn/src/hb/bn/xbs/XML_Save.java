package hb.bn.xbs;

                    
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hb.format.Format;


public class XML_Save extends XML_Command {
  public String filename = "";
  public String what = "";
  public String format = "";
  public XML_Parameter[] parameter = null;

	public XML_Save(Node node) {
    super(node, "LOAD");
    Node attr = attributes.getNamedItem("FILENAME");
    if (attr != null)
      filename = attr.getNodeValue();
    attr = attributes.getNamedItem("WHAT");
    if (attr != null)
      what = attr.getNodeValue();
    attr = attributes.getNamedItem("FORMAT");
    if (attr != null)
      format = attr.getNodeValue();
  }

	public boolean parse() {
    NodeList children = node.getChildNodes();
    if (children != null) {
      List parameters = new ArrayList();
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
 				if (childName.equalsIgnoreCase("PARAMETER")) {
          XML_Parameter parameter = new XML_Parameter(child);
          parameter.parse();
          parameters.add(parameter);
 				} else if (childName.equals("#text")) {
 				} else if (childName.equals("#comment")) {
        } else
          unexpectedNode(child);
			}
      parameter = new XML_Parameter[parameters.size()];
      for (int i=0; i<parameter.length; i++)
        parameter[i] = (XML_Parameter)parameters.get(i);
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<%s", p.add(indent+1).add('\0').add(NAME)));
    if (filename != null)
      s.append(" FILENAME=\"").append(filename).append("\"");
    if (filename != null)
      s.append(" WHAT=\"").append(what).append("\"");
    if (filename != null)
      s.append(" FORMAT=\"").append(format).append("\"");
    s.append(">\n");
    if (parameter != null) {
      for (int i=0; i<parameter.length; i++)
        s.append(parameter[i]);
  	}
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
