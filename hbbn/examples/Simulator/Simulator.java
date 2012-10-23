import hb.bn.*;
import hb.bn.event.*;
import hb.bn.xbn.*;
import hb.math.DoubleArray;
import hb.math.graph.*;
import hb.format.*;
import hb.text.*;
import java.util.*;
import java.io.*;
import hb.util.*;
import hb.math.matrix.Matrix;
import hb.bn.JunctionTreeSimulator;
import hb.bn.BayesianNetworkSimulator;


public class Simulator extends BayesianNetworkHandler implements PassMessageListener {
	private	FormatString fmtString = null;
  private Variable[] g, x;
  private FormatWriter matlabOut = null;
  private Writer datWriter = null;
  private int nbrOfMessage, nbrOfSampleMessage;
  protected hb.bn.Simulator simulator = null;

	public void simulate(int nbrOfFamilies) throws IOException {
    nbrOfMessage = nbrOfSampleMessage = 0;

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
    Collector sim = new SimulationWriter(datWriter, "%d ", bn.size());
    engine.setDebugger(debugger);
    simulator.setDebugger(debugger);
    debugger.startTimer();
    debugger.push("Simulate %d instances of the BN with %d variables.", 
      p.add(nbrOfFamilies).add(bn.size()));

    simulator.simulate(nbrOfFamilies, sim);
    debugger.write("Number of message passed: %d, i.e. %.2f messages/sample.", 
      p.add(nbrOfMessage).add(((double)nbrOfMessage)/nbrOfFamilies));
    debugger.write("Number of message passed: %d, i.e. %.2f messages/sample.", 
      p.add(nbrOfSampleMessage).add(((double)nbrOfSampleMessage)/nbrOfFamilies));
    debugger.pop();
    debugger.stopTimer();

		if (latex) {
      debugger.write("LaTeX: append to "+latexName+".tex");
			saveLaTeXPicture(engine, latexName+".tex", latexName+"SickAll", 3, true, "BN");
 			latex=false;
    }

    debugger.pop();
	} // simulate()


  public void projection(PassMessageEvent e) {}
  public void absorption(PassMessageEvent e) {}

  public void messagePassed(PassMessageEvent e) {
    if (e instanceof PassSampleMessageEvent) {
      nbrOfSampleMessage++;
    } else if (e instanceof PassMessageEvent) {
      nbrOfMessage++;
    }
  }

	public static void main(String args[]) throws IOException {
    initiate(args);
    
    int nbrOfFamilies = Arguments.getOptionAsInt("-n", 30);
    debugger.set(Arguments.getFlag("-debug"));

    String datName = Arguments.getOption("-f", "simulation.dat");
    boolean append = !Arguments.getFlag("+append");
    String method = Arguments.getOption("-method", "one-pass");
    String optimizeFor = Arguments.getOption("-optimize", "speed");

    Format.printf("Simulate using method '%s' and optimizing for '%s'\n", 
      new Parameters(method).add(optimizeFor));
    Simulator sim = new Simulator();
    sim.datWriter = new FileWriter(datName, append);

    sim.setupNetwork(xbnFile);


    if (method.equals("one-pass")) {
      sim.simulator = new JunctionTreeSimulator(engine,JunctionTreeSimulator.ONE_PASS);
    } else if (method.equals("two-pass")) {
      sim.simulator = new JunctionTreeSimulator(engine,JunctionTreeSimulator.TWO_PASS);
    } else if (method.equals("conditioned")) {
      sim.simulator = new BayesianNetworkSimulator(engine,BayesianNetworkSimulator.CONDITIONED);
    } else if (method.equals("unconditioned")) {
      sim.simulator = new BayesianNetworkSimulator(engine,BayesianNetworkSimulator.UNCONDITIONED);
    } else
      Format.printf("Unknown simulation method ('%s')\n", new Parameters(method));
    if (optimizeFor.equals("speed"))
      engine.setOptimizationFor(engine.SPEED);
    else
      engine.setOptimizationFor(engine.MEMORY);
    engine.addPassMessageListener(sim);
    sim.simulate(nbrOfFamilies);

    debugger.printTimer();
	} // main()


} // class MAIN




/*
expand -2 SibPairSimulation.java | a2ps | psnup -2up | lpr 

HISTORY

2000-07-05
* Added support for appending values to dat-file.
2000-05-23
* Created.
	
*/
