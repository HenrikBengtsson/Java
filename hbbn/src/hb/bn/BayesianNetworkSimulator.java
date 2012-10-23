package hb.bn;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import hb.bn.event.PassMessageListener;
import hb.bn.event.PassMessageEvent;
import hb.bn.event.PassSampleMessageEvent;
import hb.format.Format;
import hb.format.Parameters;
import hb.format.FormatString;
import hb.math.graph.JunctionTree;
import hb.math.graph.Cluster;
import hb.math.graph.Vertex;
import hb.math.graph.Sepset;
import hb.util.Debugger;

/**
 
 */
public class BayesianNetworkSimulator implements Simulator {
  protected InferenceEngine engine;
  protected BeliefNetwork bn = null;

  protected List passMessageListeners = new ArrayList();
  protected boolean hasPassMessageListeners = false;

  protected Debugger debugger = null;
  protected boolean isDebugging = false;

  protected int optimizeFor = SPEED;
  protected int simulationMethod = CONDITIONED;
  public final static int UNCONDITIONED = 0;
  public final static int CONDITIONED   = 1;
  public final static int SPEED  = 1;
  public final static int MEMORY = 2;


	public BayesianNetworkSimulator(InferenceEngine engine) {
    this(engine, CONDITIONED);
  }

	public BayesianNetworkSimulator(InferenceEngine engine, int simulationMethod) {
    this.simulationMethod = simulationMethod;
    this.engine = engine;
    bn = engine.getBeliefNetwork();
    if (bn == null)
      throw new RuntimeException(getClass().getName()+": Unknown Bayesian network.");
  }

	public void setDebugger(Debugger d) {
    debugger = d;
    isDebugging = (debugger != null);
	}

/* *******************************************************************************

  interface Simulator

 ******************************************************************************* */

  public void setSimulationMethod(int method) { 
    simulationMethod = method;
  }

  public int getSimulationMethod() {
    return simulationMethod;
  }

  public void setOptimizationFor(int optimizeFor) {
    this.optimizeFor = optimizeFor;
  }

  public int getOptimizationFor() {
    return optimizeFor;
  }


  /**
  Simulates a given number of instances using the cached one-pass method 
  (see [HBBN00]).
  
  @param n - the number of samples to be performed
  @return the sampled instances contained in a SimulationList.
  */
	public Collector simulate(int n) {
    Collector list = new SimulationList(bn, n);
    simulate(n, list);
    return list;
	}

  /**
  Simulates a given number of instances using the cached one-pass method 
  (see [HBBN00]).
  
  @param n - the number of samples to be performed
  @param c - the Collector for storing the samples
  @return the sampled instances as a Collector
  */
	public Collector simulate(int n, Collector c) {
    int count = 0;
		try {
  		Object[] os = bn.toArray();
  		int len = os.length;
  		Variable[] vs = new Variable[len];
  		System.arraycopy(os,0, vs,0, len);
  
      long lastTime = 0;
      FormatString debugFmtstr = null;
      if (isDebugging) {
        lastTime = debugger.getTimer();
        debugFmtstr = Format.out.compileFormatString("Simulation #%d finished after %.2fs. Current speed is %.2f simulations/s");
			}
      Parameters p = new Parameters();
  		for (count=n; --count>=0;) {
     		engine.clearEvidence();
//        engine.update();
   			int[] value = new int[len];
        draw(vs, value);
  			c.add(value);
        if (isDebugging && count % 10 == 0 && count != 0) {
          int idx = n-count;
          long time = debugger.getTimer();
          long diffTime = time-lastTime;
          double performance = 10000.0/diffTime;
          debugger.write(debugFmtstr, p.add(idx).add(time/1000.0).add(performance));
          lastTime = time;
				}
  		}
      if (isDebugging) {
        long time = debugger.getTimer();
        long diffTime = time;
        double performance = 1000.0*n/diffTime;
        debugger.write("Simulation #%d finished after %.2fs. Average speed was %.2f simulations/s", 
          p.add(n).add(time/1000.0).add(performance));
        lastTime = time;
			}
      return c;
    } catch (java.lang.OutOfMemoryError memex) {
      System.err.println("HBBN: Out of memory while doing simulations!");
      System.err.println((n-count+1)+" simulation(s) out of "+n+" was finished.");
      memex.printStackTrace();
      return c;
		}
	}

  /**
  Simulates one instance using the one-pass method (see [HBBN00]).
  */
	public void simulate() {
		// STEP 1: Unmark all clusters.
    Iterator it = bn.iterator();
    while (it.hasNext()) {
      Variable v = (Variable)it.next();
      drawAndObserve(v);
      engine.update();
		}
	}

	public int[] draw() {
    Object[] os = bn.toArray();
    int len = os.length;
    int[] value = new int[len];
    Variable[] vs = new Variable[len];
    System.arraycopy(os,0, vs,0, len);
    return draw(vs, value);
  }

  /**
  Samples all variables in the Bayesian network and stores the result in the
  given integer array.

  @param vs - the array of Variables to be drawn
  @param value - the integer array to store the sampled values
  @return the integer array containing the sampled values
  */
	public int[] draw(Variable[] vs, int[] value) {
    for (int i=0; i<value.length; i++) {
      value[i] = drawAndObserve(vs[i]);
      engine.update();
		}
    return value;
  }

	public int draw(Variable v) {
    return v.draw();
	}

	public int draw(String var) {
    Variable v = bn.getVariable(var);
    if (v == null)
      throw new RuntimeException("ERROR: Cannot draw from variable '"+var+
                                 "' since it does not exist!");
    return v.draw();
	}

	public int drawAndObserve(Variable v) {
    int drawnState = v.draw();
    engine.observe(v, drawnState);
    return drawnState;
	}

	public int drawAndObserve(String var) {
    Variable v = bn.getVariable(var);
    if (v == null)
      throw new RuntimeException("ERROR: Cannot draw from variable '"+var+
                                 "' since it does not exist!");
    return v.drawAndObserve();
	}


/* *******************************************************************************

  Debug methods, i.e. methods for asserting the correctness of the algorithms.

 ******************************************************************************* */


} // class BayesianNetworkSimulator


/*
HISTORY

2000-08-08
* Created from JunctionTreeSimulator.
	
*/
