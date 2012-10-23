import hb.bn.*;
import hb.bn.xbn.*;
import hb.math.DoubleArray;
import hb.math.graph.*;
import hb.format.*;
import hb.text.*;
import java.util.*;
import java.io.*;
import hb.util.*;
import hb.math.matrix.Matrix;


public class sim extends BayesianNetworkHandler {
	private	static FormatString fmtString = null;
  private static Variable[] g, x;
  private static FormatWriter matlabOut = null;

  private static void printHeader() {
		if (!quiet) {
   		if (!matlabStyle) {
   			Format.printf("Fam. M  F  O1 O2\n");
   			Format.printf("----------------\n");
   			fmtString = Format.out.compileFormatString("%4d %2s %2s %2s %2s\n");
   		} else
   			fmtString = Format.out.compileFormatString("%4d %2d %2d %2d %2d\n");
   	}
  }
 
	protected static void writeSimulation(int i, int[] value) {
    if (!matlabStyle)
      Format.printf("%5d. ", p.add(i));

    for (int j=0; j<value.length; j++)
      Format.printf("%1d ", p.add(value[j]));
    Format.printf("\n");
	}

	protected static void writeDat(FormatWriter out, int i, int[] value) {
		try {
      out.write("%5d. ", p.add(i));

      for (int j=0; j<value.length; j++)
        out.write("%1d ", p.add(value[j]));
      out.write("\n");
		} catch(ParseException ex2) {
  		// Should never occur.
      System.err.println(ex2.getMessage());
      System.exit(99);
		} catch(IOException ex) {
      System.err.println(ex.getMessage());
      System.exit(10);
		}
	}

	protected static void loadXBN(String xbnFile) {
    setupNetwork(xbnFile);

    // Get the variables.
    g = new Variable[14];         // Genotypes
    x = new Variable[g.length];   // Phenotypes
    for (int i=0; i<g.length; i++)
      g[i] = bn.getVariable(String.valueOf(i+1));
    for (int i=0; i<x.length; i++)
      x[i] = bn.getVariable(String.valueOf(i+1)+"P");
	}


	public static void simulate(int nbrOfFamilies) throws IOException {
    debugger.push("Simulate...");

    engine.update();
       
    if (latex) {
      debugger.write("LaTeX: save "+latexName+".tex");
  		saveLaTeXPicture(engine, latexName+".tex", latexName+"", 3, false, "BN");
      debugger.write("LaTeX: append to "+latexName+".tex");
  		saveLaTeXPicture(engine, latexName+".tex", latexName+"JT", 3, true, "JT");
    }  

  	if (verbose) {
      debugger.write("Display all variables and their distributions.");
  	  display(bn, "*");
  	}
  
		//    printHeader();
    SimulationList sim = new SimulationList(bn);
    debugger.startTimer();
    debugger.push("Simulate %d families.", p.add(nbrOfFamilies));
    engine.simulate(nbrOfFamilies, sim);
    debugger.pop();
    debugger.stopTimer();

    if (!quiet) {
      // Plot simulations...
      debugger.push("Get all variables.");
			Variable[] variable = new Variable[g.length+x.length];
      for (int i=0; i<g.length; i++) {
        variable[i] = g[i];
        variable[i+g.length] = x[i];
        debugger.write("Variable %i: %s", p.add(i).add(variable[i]));
      }
      debugger.pop();
      int[] varMap = bn.indicesOf(variable);
      debugger.push("varMap:");
      for (int i=0; i<varMap.length; i++)
        debugger.write("varMap[%d]=%d", p.add(i).add(varMap[i]));
      debugger.pop();

      debugger.write("Reshuffle the data");
      sim.reshuffle(varMap);

      debugger.write("Get all samples in form of a matrix");
      Matrix m = new Matrix(sim.get());

      if (matlabOut != null) {
        debugger.write("Write matrix to the dat-file");
        m.write(matlabOut, "%d ");
			}

      if (verbose) {
        int n=m.getRows();
        double[][] margDist = new double[variable.length][];
        for (int j=0; j<margDist.length; j++) {
          int smax = variable[j].size();
          margDist[j] = new double[smax];
          for (int s=0; s<smax; s++) {
            for (int i=0; i<n; i++)
              if (m.value[i][j] == s) margDist[j][s]++;
            margDist[j][s] = margDist[j][s]/n;
				  }
				}

        debugger.write("Write marginal distributions to standard output.");
        for (int j=0; j<margDist.length; j++) {
          Format.printf("%2d. (%12s) ", p.add(j).add(variable[j].name));
          Potential dist = variable[j].getProbability();
          for (int s=0; s<margDist[j].length; s++) {
            double exact = dist.get(s);
            double estimate = margDist[j][s];
            double diff = estimate-exact;
            Format.printf("%.3f (=%.3f%+.3f)  ", 
              p.add(estimate).add(exact).add(diff));
          }
          Format.printf("\n");
				}
			}

  		if (latex) {
        debugger.write("LaTeX: append to "+latexName+".tex");
  			saveLaTeXPicture(engine, latexName+".tex", latexName+"SickAll", 3, true, "BN");
  			latex=false;
  		}
    }
    debugger.pop();
	} // simulate()

	public static void main(String args[]) throws IOException {
    initiate(args);
    
    int nbrOfFamilies = Arguments.getOptionAsInt("-n", 30);
    int what = Arguments.getOptionAsInt("-run", 01);
    debugger.set(Arguments.getFlag("-debug"));

    String matlabName = Arguments.getOption("-matlab");
    if (matlabName != null)
      matlabOut = new FormatWriter(new FileWriter(matlabName));

    loadXBN(xbnFile);
    if (what == 00)
      simulate(nbrOfFamilies);
    else
      simulate(nbrOfFamilies);

    if (!matlabStyle)
      debugger.printTimer();
	} // main()


} // class MAIN




/*
expand -2 SibPairSimulation.java | a2ps | psnup -2up | lpr 

HISTORY

2000-05-23
* Created.
	
*/
