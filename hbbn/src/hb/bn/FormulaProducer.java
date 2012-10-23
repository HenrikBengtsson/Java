package hb.bn;

import java.io.IOException;
import java.io.FileWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import hb.bn.event.PassMessageEvent;
import hb.bn.event.PassMessageListener;
import hb.bn.xbn.*;
import hb.bn.xbs.*;
import hb.format.Format;
import hb.format.ArrayConversionParser;
import hb.format.FormatWriter;
import hb.format.Parameters;
import hb.io.LaTeXWriter;
import hb.math.graph.Graph;
import hb.math.graph.Vertex;
import hb.math.graph.JunctionTree;

import java.util.Collection;
import hb.math.graph.Cluster;
import hb.math.graph.Sepset;

public class FormulaProducer implements PassMessageListener {
  protected LaTeXWriter out;

	public FormulaProducer() {
    this(new OutputStreamWriter(System.out));
	}

	public FormulaProducer(Writer out) {
    if (out instanceof LaTeXWriter)
      this.out = (LaTeXWriter)out;
    else
      this.out = new LaTeXWriter(out);
    writeHeader();
  }

	protected void writeCommentLine() {
    try {
      String commentLine = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";
      out.write("%s\n", new Parameters(commentLine));
    } catch (IOException ex) {
		}
	}

	protected void writeHeader() {
    try {
      writeCommentLine();
      out.write("%% Do not modify this file. It is automatically generated and \n");
      out.write("%% it might be overwritten again.                             \n");
      out.write("%% Generator: FormulaProducer.                                \n");
      writeCommentLine();
      out.write("\n");
      out.write("\\newcommand{\\cliqueA}{}\n");
      out.write("\\newcommand{\\cliqueB}{}\n");
      out.write("\\newcommand{\\sepsetAB}{}\n");
      out.write("\\newcommand{\\marginalizers}{}\n");
      out.write("\n");
    } catch (IOException ex) {
		}
	}
 
	protected String getLateXCluster(Collection c) {
    StringBuffer sbr = new StringBuffer();
    if (c.size() > 1) 
      sbr.append("\\mathbf{X}_{\\{");
    else
      sbr.append("X_{");
    Iterator it = c.iterator();
    while (it.hasNext()) {
      Vertex v = (Vertex)it.next();
      String vname = v.toString();
      sbr.append(vname);
      if (it.hasNext())
        sbr.append(',');
		}
    if (c.size() > 1) 
      sbr.append("\\}}");
    else
      sbr.append("}");
    return sbr.toString();
	}

  protected void writeEvent(PassMessageEvent e) {
    String A   = getLateXCluster(e.getFrom());
    String B   = getLateXCluster(e.getTo());
    String Sab = getLateXCluster(e.getSepset());
    Cluster m = (Cluster)e.getFrom().clone();
    m.removeAll(e.getSepset());
    String M   = getLateXCluster(m);
    try {
      out.write("\\renewcommand{\\cliqueA}{%s}\n", new Parameters(A));
      out.write("\\renewcommand{\\cliqueB}{%s}\n", new Parameters(B));
      out.write("\\renewcommand{\\setsetAB}{%s}\n", new Parameters(Sab));
      out.write("\\renewcommand{\\marginalizers}{%s}\n", new Parameters(M));
    } catch (IOException ex) {
		}
	}

	public void projection(PassMessageEvent e) {
    try {
      writeCommentLine();
      out.write("%% Projection: \n");
      out.write("\n");
      writeEvent(e);
      out.write("\\phi_{\\sepsetAB}^{old} = ");
      out.write("\\phi_{\\sepsetAB}");
      out.write("\n");
      out.write("\\phi_{\\sepsetAB} = \\sum_{\\marginalizers}\\phi_{\\cliqueA}");
      out.write("\n");
      String from = getLateXCluster(e.getFrom());
      String via  = getLateXCluster(e.getSepset());
      out.write("% projection: %s on %s\n",
        new Parameters(from).add(via));
    } catch (IOException ex) {
		}
	}

	public void absorption(PassMessageEvent e) {
    try {
      writeCommentLine();
      out.write("%% Absorption: \n");
      out.write("\n");
      writeEvent(e);
      String to   = getLateXCluster(e.getTo());
      String via  = getLateXCluster(e.getSepset());
      out.write("% absorption: %s from %s\n",
        new Parameters(to).add(via));
    } catch (IOException ex) {
		}
	}

	public void messagePassed(PassMessageEvent e) {
    try {
      writeCommentLine();
      out.write("%% Message passed: \n");
      out.write("\n");
      writeEvent(e);
      String from = getLateXCluster(e.getFrom());
      String to   = getLateXCluster(e.getTo());
      String via  = getLateXCluster(e.getSepset());
      out.write("% message passed: from %s to %s via %s\n",
        new Parameters(from).add(to).add(via));
    } catch (IOException ex) {
		}
	}
} // class FormulaProducer

/* HISTORY:

2000-06-20
* Created.
	
*/
