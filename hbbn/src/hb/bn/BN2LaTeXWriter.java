package hb.bn;

/*
 @author Henrik Bengtsson, hb@maths.lth.se
 */

import java.awt.Point;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import hb.format.Format;
import hb.format.ArrayConversionParser;
import hb.format.FormatWriter;
import hb.format.Parameters;
import hb.math.graph.Graph;
import hb.math.graph.Vertex;
import hb.math.graph.Edge;
import hb.math.graph.Sepset;
import hb.math.graph.JunctionTree;
import hb.math.graph.Cluster;

class LaTeXWriter extends FilterWriter {
	protected FormatWriter out;

  public LaTeXWriter(Writer out) {
    super(FormatWriter.conform(out));
    this.out = (FormatWriter)super.out;
	}

	/**
   Converts a string into a corresponding string in LaTeX format.
   Example: "my_string" is converted to "my\_string".

   @return a converted String
	 */
	protected String toLaTeXString(String s) {
    StringBuffer sb = new StringBuffer(s);
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (ch == '_')
        sb.insert(i++,'\\');
		}
    return sb.toString();
	}
  
	/**
   Converts a digit into a character according to th rule
   f(digit) = 'A'+digit. Example: "2" is converted to "C".

   @return a converted String
	 */
	protected char digitToLetter(int digit) {
	  return (char)('A'+digit);
  }

	/**
   Converts an integer into a unique LaTeX string.
   Example: "1203" is converted to "BCAD".

   @return a converted String
	 */
	protected String intToLaTeX(int i) {
    String s = String.valueOf(i);
    StringBuffer sbr = new StringBuffer();
    for (int k=0; k<s.length(); k++)
      sbr.append(digitToLetter(s.charAt(k)));
    return sbr.toString();
	}

	/**
   Converts a string containing digits into a string where the digits are
   replaced by letters.
   Example: "foo123" is converted to "fooBCD".

   @return a converted String
	 */
	protected String toLaTeXCommandName(String s) {
    StringBuffer sb = new StringBuffer(s);
    StringBuffer sb2 = new StringBuffer();
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (Character.isLetter(ch))
        sb2.append(ch);
      else if (Character.isDigit(ch)) {
        sb2.append((char)(ch-'0'+'A'));
			}
		}
    return sb2.toString();
	}

	/**
   Removes all underscores ('_') from a string.
   Example: "foo_123" is converted to "foo123".

   @return a converted String
	 */
	protected String toPSTricksName(String s) {
    StringBuffer sb = new StringBuffer(s);
    StringBuffer sb2 = new StringBuffer();
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (ch != '_')
        sb2.append(ch);
		}
    return sb2.toString();
	}

	/**
   Write a LaTeX comment followed by a newline.
	 */
  public void writeComment(String comment) throws java.io.IOException {
    out.write("%% %s\n", new Parameters(comment));
	}

	/**
   Write a LaTeX string. All occurences of "%" is taken care of automatically.
	 */
  public void writeLaTeX(String latexcode) throws java.io.IOException {
    StringBuffer s = new StringBuffer(latexcode);
    int len = s.length();
    int i = 0;
    while (i < len) {
      char ch = s.charAt(i);
      if (ch == '%') {
        s.insert(i++, '%');
        len++;
			}   
      i++;
		}
    out.write("% %s\n", new Parameters(s));
	}

} // class LaTeXWriter


public class BN2LaTeXWriter extends FilterWriter {
  private static Runtime rt = Runtime.getRuntime();
  private static long start, stop;
	protected FormatWriter out;

  public BN2LaTeXWriter(Writer out) {
    super(FormatWriter.conform(out));
    this.out = (FormatWriter)super.out;
	}

	private String toLaTeXString(String s) {
    StringBuffer sb = new StringBuffer(s);
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (ch == '_')
        sb.insert(i++,'\\');
		}
    return sb.toString();
	}

	private String toLaTeXCommandName(String s) {
    StringBuffer sb = new StringBuffer(s);
    StringBuffer sb2 = new StringBuffer();
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (Character.isLetter(ch))
        sb2.append(ch);
      else if (Character.isDigit(ch)) {
        sb2.append((char)(ch-'0'+'A'));
			}
		}
    return sb2.toString();
	}

	private String toPSTricksName(String s) {
    StringBuffer sb = new StringBuffer(s);
    StringBuffer sb2 = new StringBuffer();
    for (int i=0; i<sb.length(); i++) {
      char ch = sb.charAt(i);
      if (ch != '_')
        sb2.append(ch);
		}
    return sb2.toString();
	}

	private String getClusterName(Cluster c) {
    StringBuffer s = new StringBuffer();
    Collection vs = c.getVertices();
    Iterator it = vs.iterator();
    while (it.hasNext()) {
      Vertex u = (Vertex)it.next();
      s.append(toPSTricksName(u.name));
      if (it.hasNext())
        s.append("\\\\");
		}
    return s.toString();
	}

	private void writeVertices(Graph G, int level) throws java.io.IOException {
    // Find the area that the graph covers...
    hb.math.graph.Dimension d = G.graphicalSize();
    double dx = d.width/1e4;
    double dy = d.height/1e4;

    Iterator it = G.iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
      Point lv = v.getPoint().getLocation();
      double x = (lv.x-d.xmin)/dx;
      double y = (lv.y-d.ymin)/dy;
      String name = toPSTricksName(v.name);
      if (v instanceof Cluster) {
        Cluster c = (Cluster)v;
        String cname = getClusterName(c);
        out.write("    \\BN@clique[x=%d,y=%d]{%s}{%s}%%\n", 
          p.add(x).add(y).add(name).add(cname));
      } else if (v instanceof Variable) {
        Variable var = (Variable)v;
        String isObserved = "";
        if (var.isObserved())
          isObserved = ",isObserved=yes";
        if (level == 0) {
          out.write("    \\BN@vertex[x=%d,y=%d%s]{%s}%%\n", 
            p.add(x).add(y).add(isObserved).add(name));
  			} else {
          Potential pv = var.getProbability();
          StringBuffer dist = new StringBuffer();
          State[] state = var.getStates();
          for (int i=0; i<state.length; i++) {
          	double value = pv.value[i];
          	int len = (int)(1000*value);
          	dist.append(state[i].name).append('=').append(len);
          	if (i<state.length-1)
          		dist.append(',');
  			  }				
          out.write("    \\BN@vertexDist[x=%d,y=%d%s,details=%d]{%s}{%s}%%\n", 
          	p.add(x).add(y).add(isObserved).add(level).add(name).add(dist));
				}
	  	} else {
        out.write("    \\BN@vertex[x=%d,y=%d]{%s}%%\n", 
          p.add(x).add(y).add(name));
	  	}
		}
		//    out.write("%%\n");
	}

	private void writeVertices(Graph G) throws java.io.IOException { 
    writeVertices(G,0);
	}

  private void writeEdges(Graph G) throws java.io.IOException {
    Iterator it = G.edgeIterator();
    while (it.hasNext()) {
      Edge e = (Edge)it.next();
      String uname = toPSTricksName(e.u.name);
      String vname = toPSTricksName(e.v.name);
      String dir = "->";
      if (!e.isDirected())
        dir = "-";
      if (G instanceof JunctionTree) {
        Sepset s = (Sepset)e;
        String name = getClusterName((Cluster)s.Sxy);
        out.write("    \\BN@sepset[%s]{%s}{%s}{%s}%%\n",
          p.add(dir).add(uname).add(name).add(vname));
	  	} else {
        out.write("    \\BN@edge[%s]{%s}{%s}%%\n",
          p.add(dir).add(uname).add(vname));
			}
		}
		//    out.write("%%\n");
	}

	private void writeCopyright() throws java.io.IOException {
    out.write("%%\n");
    out.write("%% This file is automatically generated using the\n");
    out.write("%% BN2LaTeXWriter.\n");
    out.write("%% \n");
    out.write("%% Henrik Bengtsson <hb@maths.lth.se>.\n");
    out.write("\n");
	}

	private void writeHelpComment(Graph G) throws java.io.IOException {
    out.write("%% To make use of the graph do:\n");
    out.write("%%  1. \\usepackage{GraphTemplate}\n");
    out.write("%%  2. \\input{BN%s.tex}\n", p.add(G.name));
    out.write("%%  3. \\BN%s\n", p.add(G.name));
    out.write("\n");
	}

	public void write(Graph G) throws java.io.IOException {
    write(G, "BN"+G.name, 0);
	}

	public void write(Graph G, String cmdName) throws java.io.IOException {
    write(G, cmdName, 0);
	}

	public void write(Graph G, String cmdName, int level) 
        throws java.io.IOException {
    int x0 = -2000, x1=12000, y0=-1000, y1=11000;
		if (level > 2) {
      x0 = -4000; x1=14000; y0=-2000; y1=12000;
		}
    writeCopyright();
		//    writeHelpComment(G);
    cmdName = toLaTeXCommandName(cmdName);
    out.write("\\newcommand{\\%s}[1][xunit=0.012mm,yunit=0.015mm]{%%\n", 
         p.add(cmdName));
		//    out.write("  \\BN@Graph[#1]{(%d,%d)(%d,%d)}{%%\n",
		//         p.add(x0).add(y0).add(x1).add(y1));
    out.write("  \\BN@Graph[#1]{%%\n");
    writeVertices(G, level);
    out.write("  }{%%\n");
    writeEdges(G);
    out.write("  }%%\n");
    out.write("}%% %s\n", p.add(cmdName));
    out.write("\n");
    out.write("\n");
	}

  static {
    Format.out.addConversionParser(
       new ArrayConversionParser(Format.out.getConversionParsers()));
  }
  static Parameters p = new Parameters();
} // class BN2LaTeXWriter


/* HISTORY:

2000-07-07
* Added support for java.awt.Point-locations.
2000-05-22
* Updated the write(...) method according to the new \BN@Graph template in LaTeX.

1999-06-11
* Created. Now generates one TeX-file for each network.

*/
