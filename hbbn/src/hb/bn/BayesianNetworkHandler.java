package hb.bn;

import hb.bn.xbn.*;
import hb.math.graph.Graph;
import hb.math.graph.JunctionTree;
import hb.math.graph.Cluster;
import hb.format.ArrayConversionParser;
import hb.format.Format;
import hb.format.Parameters;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import hb.util.Arguments;
import hb.util.Debugger;


public class BayesianNetworkHandler {
  protected static Debugger debugger = new Debugger();
  protected static BeliefNetwork bn;
  protected static JunctionTreeInferenceEngine engine;
  protected static Parameters p = new Parameters();
  protected static String xbnFile;

  // Command line options...
  protected static boolean matlabStyle;
  protected static boolean verbose, quiet, latex;
  protected static String latexName = null;

  static {
    Format.out.addConversionParser(
       new ArrayConversionParser(Format.out.getConversionParsers()));
  }

	protected static void setupNetwork(String xbnFile) {
    bn = loadXBN(xbnFile);
    engine = new JunctionTreeInferenceEngine(bn);
	}

	private static BeliefNetwork loadXBN(String filename) {
			//    Format.printf("Loading network '%s'\n", p.add(filename));
    XBNParser parser = new XBNParser();
    XBN_AnalysisNotebook ab = parser.parse(filename);
    BeliefNetwork bn = new BeliefNetwork(ab.bnmodel[0]);
    return bn;
	}

	protected static void saveLaTeXPicture(InferenceEngine engine, String filename, String cmdName, int details, boolean append, String what) {
			//    Format.printf("Saving network '%s'\n", p.add(cmd.filename));
		try {
      BN2LaTeXWriter out = 
        new BN2LaTeXWriter(new FileWriter(filename, append));
      BeliefNetwork bn = engine.getBeliefNetwork();

	  	if (what.equalsIgnoreCase("BN")) {
        if (cmdName == null)
          cmdName = "BN"+bn.name;
        out.write(bn, cmdName, details);
  		} else if (what.equalsIgnoreCase("Moralized")) {
        if (cmdName == null)
          cmdName = "BN"+bn.name+"Moralized";
        Graph bn2 = (Graph)bn.clone();
        bn2.moralize();
        out.write(bn2, cmdName);
  		} else if (what.equalsIgnoreCase("Triangulated")) {
        if (cmdName == null)
          cmdName = "BN"+bn.name+"Triangulated";
        Graph bn2 = (Graph)bn.clone();
        bn2.moralize();
        bn2.triangulate();
        out.write(bn2, cmdName);
  		} else if (what.equalsIgnoreCase("JT")) {
				if (engine instanceof JunctionTreeInferenceEngine) {
          if (cmdName == null)
            cmdName = "BN"+bn.name+"JoinTree";
          out.write(((JunctionTreeInferenceEngine) engine).getJunctionTree(), cmdName);
        } else {
					// There is no junction tree.
				}
			}
		} catch (java.io.IOException ex) {
      System.err.println(ex);
		}
	}

	protected static void display(BeliefNetwork bn, String var) {
    bn.display(var);
	}

	protected static void displayJT(JunctionTreeInferenceEngine engine) {
		JunctionTree jt = engine.getJunctionTree();
    Iterator it = jt.iterator();
  	while (it.hasNext()) {
  		Cluster C = (Cluster)it.next();
  		Potential pot = (Potential)C.data;
  		pot.normalize();
  		Format.printf("%s\n", p.add(pot.toString("%.4f")));
		}
	}

	protected static void assertFiles(String xbnFile) throws IOException {
      File file;
			// STEP 1. Check if the xbn-file exists.
      file = new File(xbnFile);
      if (!file.exists())
        throw new IOException("Cannot find the network file ("+xbnFile+").");

      // STEP 2. Check if the xbn.dtd exists.
      file = new File("xbn.dtd");
      if (!file.exists())
        throw new IOException("Cannot find xbn.dtd.");

      // STEP 3. Check if the xbnscript.dtd exists.
      file = new File("xbnscript.dtd");
      if (!file.exists())
        throw new IOException("Cannot find xbnscript.dtd.");
	}

	public static void initiate(String args[]) {
    Arguments.set(args);
    
    matlabStyle = Arguments.getFlag("-matlab");
    quiet = Arguments.getFlag("-quiet");
    verbose = Arguments.getFlag("-verbose");
    latex = Arguments.getFlag("-latex");
    if (latex)
      latexName = Arguments.getOption("-latex");
    xbnFile = Arguments.getLastOption();

    try {
      assertFiles(xbnFile);
    } catch(IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(10);
		}
  }
} // class MAIN




/* 
HISTORY:

2000-05-23
* Made part of the hb.math.bn package.
* Extracted from SibPairSimulation.
* Added assertFiles(...).
2000-04-10
* Created some examples.
2000-04-07
* Added DrawAndObserve.
990611
* Added constructor for XBN_BNMODEL-parameter.
990602
* Added use of Potentials and DoubleArray. It is the first time I am 
  really getting close to the "true" solution.
990409
* Created.
	
*/
