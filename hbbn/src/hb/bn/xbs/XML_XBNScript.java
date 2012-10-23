package hb.bn.xbs;


import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import hb.format.Format;


public class XML_XBNScript extends XML_Element {
  public XML_Command[] command;

	public XML_XBNScript(Node node) {
    super(node, "XBNSCRIPT");
  }

	public boolean parse() {
    NodeList children = node.getChildNodes();
    if (children != null) {
      List commands = new ArrayList();
		  int len = children.getLength();
 			for (int i = 0; i < len; i++) {
 				Node child = children.item(i);
        String childName = child.getNodeName();
        XML_Command command = null;
 				if (childName.equalsIgnoreCase("OBSERVE")) {
          command = new XML_Observe(child);
 				} else if (childName.equalsIgnoreCase("DRAW-AND-OBSERVE")) {
          command = new XML_DrawAndObserve(child);
 				} else if (childName.equalsIgnoreCase("DISPLAY")) {
          command = new XML_Display(child);
 				} else if (childName.equalsIgnoreCase("PROPERTY")) {
          command = new XML_Property(child);
 				} else if (childName.equalsIgnoreCase("RESET")) {
          command = new XML_Reset(child);
 				} else if (childName.equalsIgnoreCase("UPDATE")) {
          command = new XML_Update(child);
 				} else if (childName.equalsIgnoreCase("ECHO")) {
          command = new XML_Echo(child);
 				} else if (childName.equalsIgnoreCase("LOAD")) {
          command = new XML_Load(child);
 				} else if (childName.equalsIgnoreCase("SAVE")) {
          command = new XML_Save(child);
 				} else if (childName.equals("#text")) {
 				} else if (childName.equals("#comment")) {
        } else
          unexpectedNode(child);
        if (command != null) {
          command.parse();
          commands.add(command);
				}
			}
      command = new XML_Command[commands.size()];
      for (int i=0; i<command.length; i++)
        command[i] = (XML_Command)commands.get(i);
		}
    return true;
	} // parse

	public String toString(int indent) {  
    StringBuffer s = new StringBuffer();
    s.append(Format.sprintf("%*c<%s>\n", p.add(indent+1).add('\0').add(NAME)));
    if (command != null) {
			for (int i=0; i<command.length; i++)
        s.append(command[i].toString(indent+2));
    }
    s.append(Format.sprintf("%*c</%s>\n", p.add(indent+1).add('\0').add(NAME)));
    return s.toString();
  }  
}

/* HISTORY:

2000-04-07
* Added DrawAndObserve.
2000-04-04
* Extracted and put into a file itself.

990614
* Created.

 */
