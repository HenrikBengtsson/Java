package hb.bn.xbs;

                    
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import dom.DOMParserWrapper;
import hb.format.Format;
import hb.format.Parameters;


/**
 * Requires: The free XML parser by IBM. See JCentral for more information. 
 *
 * @author Henrik Bengtsson, hb@maths.lth.se
 */
public class XMLScriptParser {
  private XML_XBNScript script;

  /** Default parser name. */
  private static final String 
    DEFAULT_PARSER_NAME = "dom.wrappers.DOMParser";

  public XMLScriptParser() {
	}

  public XML_XBNScript parse(String uri) {
    try {
      DOMParserWrapper parser = (DOMParserWrapper)
        Class.forName(DEFAULT_PARSER_NAME).newInstance();
      Document document = parser.parse(uri);
      parse(document);
      return script;
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
    return null;
  }

  protected void parse(Node node) {
    int type = node.getNodeType();
    switch (type) {
      case Node.DOCUMENT_NODE: {
        parse(((Document)node).getDocumentElement());
        break;
      }

      case Node.ELEMENT_NODE: {
        String nodeName = node.getNodeName();
     		if (nodeName.equals("XBNSCRIPT")) {
     		  script = new XML_XBNScript(node);
     			script.parse();
        } else if (nodeName.equals("#text")) {}
        else 
          throw new RuntimeException("PARSING ERROR: Unexpected node <"+
            node.getNodeName()+">.");
        break;
  		}
		}
	}

	public static void main(String args[]) throws java.io.IOException {
    XMLScriptParser parser = new XMLScriptParser();
    XML_XBNScript script = parser.parse(args[0]);
    Format.printf("script:\n%s\n", p.add(script));
	}

  public static Parameters p = new Parameters();
} // class XMLScriptParser


/* HISTORY:

2000-04-04
* Extracted and put into a file itself.

990614
* Created.

 */
