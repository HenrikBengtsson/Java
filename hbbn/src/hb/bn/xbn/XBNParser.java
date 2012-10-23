package hb.bn.xbn;

/**
 * A parser for the XML Belief Network Format (XBN).
 * 
 * Requires: The free XML parser by IBM. See JCentral for more information. 
 *
 * @author Henrik Bengtsson, hb@maths.lth.se
 */

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import dom.DOMParserWrapper;


public class XBNParser {
  private XBN_AnalysisNotebook analysisnotebook;

  /** Default parser name. */
  private static final String 
    DEFAULT_PARSER_NAME = "dom.wrappers.DOMParser";

  public XBNParser() {
	}

  public XBN_AnalysisNotebook parse(String uri) {
    try {
      DOMParserWrapper parser = (DOMParserWrapper)
        Class.forName(DEFAULT_PARSER_NAME).newInstance();
      Document document = parser.parse(uri);
      parse(document);
      return analysisnotebook;
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
     		if (nodeName.equals("ANALYSISNOTEBOOK")) {
     		  analysisnotebook = new XBN_AnalysisNotebook(node);
     			analysisnotebook.parse();
        } else if (nodeName.equals("#text")) {}
        else 
          throw new RuntimeException("PARSING ERROR: Unexpected node <"+
            node.getNodeName()+">.");
        break;
  		}
		}
	}
} // class XBNParser


/* HISTORY:

990612
* Removed the main()-method.
990608
* Now parsing all XBN-elements.
990607
* Created with template from com.DOMWriter.java (XML Parser by IBM)

 */
