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


public class HBBN {
  private static Runtime rt = Runtime.getRuntime();
  private static long start, stop;


	public static void startTimer() {
    start = System.currentTimeMillis();
	}

	public static void stopTimer() {
    stop = System.currentTimeMillis();
	}

	public static void printTimer() {
    Format.printf("Timer: %.2fs\n", p.add((stop-start)/1000.0));
	}

	public static void printMemory() {
    Format.printf("Free memory: %d kb\n", p.add(rt.freeMemory()/1e3));
	}

	private static void executeLoadCmd(XML_Load cmd) {
			//    Format.printf("Loading network '%s'\n", p.add(cmd.filename));
    XBNParser parser = new XBNParser();
    XBN_AnalysisNotebook ab = parser.parse(cmd.filename);
    BN = new BeliefNetwork(ab.bnmodel[0]);
    engine = new JunctionTreeInferenceEngine(BN);
    FileWriter out = null;
    try {
      out = new FileWriter("expression.tex");
		} catch (IOException ex) {
      ex.printStackTrace();
		}
    FormulaProducer prod = new FormulaProducer(out);
		//    engine.addPassMessageListener(prod);
	}

	private static void executeSaveCmd(XML_Save cmd) {
			//    Format.printf("Saving network '%s'\n", p.add(cmd.filename));
    XML_Parameter[] parameter = cmd.parameter;
    String cmdName = null;
    int level = 1;
    boolean append = false;
  	if (parameter != null) {
			for (int i=0; i<parameter.length; i++) {
  			if (parameter[i].name.equalsIgnoreCase("CommandName")) {
          cmdName = parameter[i].value;
        } else if (parameter[i].name.equalsIgnoreCase("Append")) {
          if (parameter[i].value.equalsIgnoreCase("yes"))
            append = true;
        } else if (parameter[i].name.equalsIgnoreCase("Details")) {
          level = Integer.valueOf(parameter[i].value).intValue();
				}
			}
		}
		try {
      BN2LaTeXWriter out = 
        new BN2LaTeXWriter(new FileWriter(cmd.filename, append));
	  	if (cmd.what.equalsIgnoreCase("BN")) {
        if (cmdName == null)
          cmdName = "BN"+BN.name;
        out.write(BN, cmdName, level);
  		} else if (cmd.what.equalsIgnoreCase("Moralized")) {
        if (cmdName == null)
          cmdName = "BN"+BN.name+"Moralized";
        Graph BN2 = (Graph)BN.clone();
        BN2.moralize();
        out.write(BN2, cmdName);
  		} else if (cmd.what.equalsIgnoreCase("Triangulated")) {
        if (cmdName == null)
          cmdName = "BN"+BN.name+"Triangulated";
        Graph BN2 = (Graph)BN.clone();
        BN2.moralize();
        BN2.triangulate();
        out.write(BN2, cmdName);
  		} else if (cmd.what.equalsIgnoreCase("JoinTree")) {
        if (cmdName == null)
          cmdName = "BN"+BN.name+"JoinTree";
        out.write(engine.getJunctionTree(), cmdName);
			}
		} catch (java.io.IOException ex) {
      System.err.println(ex);
		}
	}

	private static void executeResetCmd(XML_Reset cmd) {
			//    Format.printf("Reset network\n");
  	engine.clearEvidence();
	}

	private static void executeUpdateCmd(XML_Update cmd) {
			//    Format.printf("Update network\n");
  	engine.update();
	}

	private static void executePropertyCmd(XML_Property cmd) {
			//    Format.printf("Set property\n");
	}

	private static void executeEchoCmd(XML_Echo cmd) {
    System.out.println(cmd.value);
	}

	private static void executeDisplayCmd(XML_Display cmd) {
			//    Format.printf("Displaying variable(s) '%s'\n", p.add(cmd.var));
    Variable v;

    String var = cmd.var;
    if (var.equals("*")) {
      Iterator it = BN.iterator();
      while (it.hasNext()) {
        v = (Variable)it.next();
        Format.printf("%s\n", p.add(v.toString(10)));
			}
		} else {
      v = (Variable)BN.getVertex(var);
      if (v == null)
        throw new RuntimeException("ERROR: Cannot display variable '"+var+
                               "' since it does not exist!");
      Format.printf("%s\n", p.add(v.toString(10)));
		}
	}

	private static void executeObserveCmd(XML_Observe cmd) {
			//    Format.printf("Observing variable '%s' = %s\n",
			//      p.add(cmd.var).add(cmd.statename));

    String var = cmd.var;
    Variable v = (Variable)BN.getVertex(var);
    if (v == null)
      throw new RuntimeException("ERROR: Cannot observe variable '"+var+
                                 "' since it does not exist!");
    String statename = cmd.statename;
    engine.observe(v,statename);
	}

	private static void executeDrawAndObserveCmd(XML_DrawAndObserve cmd) {
    String var = cmd.var;
    Variable v = (Variable)BN.getVertex(var);
    if (v == null)
      throw new RuntimeException("ERROR: Cannot draw from variable '"+var+
                                 "' since it does not exist!");

    int drawnState = v.draw();
    State state = v.getState(drawnState);
		Format.printf("Drawing variable '%s' : %s\n", p.add(cmd.var).add(state));
    engine.observe(v, drawnState);
	}

	public static void main(String args[]) throws java.io.IOException {
		//     test01();
		//     test02();
			//		loadXBN(args[0]);
    XMLScriptParser parser = new XMLScriptParser();
    XML_XBNScript script = parser.parse(args[0]);
		//    Format.printf("%s\n", p.add(script));

    XML_Command[] command = script.command;
    for (int i=0; i<command.length; i++) {
      XML_Command cmd = command[i];
			if (cmd instanceof XML_Display) {
        executeDisplayCmd((XML_Display)cmd);
			} else if (cmd instanceof XML_DrawAndObserve) {
        executeDrawAndObserveCmd((XML_DrawAndObserve)cmd);
			} else if (cmd instanceof XML_Load) {
        executeLoadCmd((XML_Load)cmd);
			} else if (cmd instanceof XML_Save) {
        executeSaveCmd((XML_Save)cmd);
			} else if (cmd instanceof XML_Reset) {
        executeResetCmd((XML_Reset)cmd);
			} else if (cmd instanceof XML_Update) {
        executeUpdateCmd((XML_Update)cmd);
			} else if (cmd instanceof XML_Observe) {
        executeObserveCmd((XML_Observe)cmd);
			} else if (cmd instanceof XML_Property) {
        executePropertyCmd((XML_Property)cmd);
			} else if (cmd instanceof XML_Echo) {
        executeEchoCmd((XML_Echo)cmd);
			}
		}
	} // main()


  static {
    Format.out.addConversionParser(
       new ArrayConversionParser(Format.out.getConversionParsers()));
  }
  static Parameters p = new Parameters();
  static int[] i1d = new int[1];
  static int[] i2d = new int[2];
  static int[] i3d = new int[3];
  static Variable a, b, c, d, e, f, g, h;
  static BeliefNetwork BN = new BeliefNetwork();
  static JunctionTreeInferenceEngine engine = new JunctionTreeInferenceEngine(BN);
} // class HBBN




/*
HISTORY

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
