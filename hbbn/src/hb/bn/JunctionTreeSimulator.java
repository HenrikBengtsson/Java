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
public class JunctionTreeSimulator implements Simulator {
  protected JunctionTreeInferenceEngine engine;
  protected JunctionTree jt = null; // For direct access!
  protected BeliefNetwork bn = null;

  protected List passMessageListeners = new ArrayList();
  protected boolean hasPassMessageListeners = false;

  protected Debugger debugger = null;
  protected boolean isDebugging = false;

  protected int optimizeFor = SPEED;
  protected int simulationMethod = ONE_PASS;
  public final static int ONE_PASS      = 2;
  public final static int TWO_PASS      = 3;
  public final static int SPEED  = 1;
  public final static int MEMORY = 2;


	public JunctionTreeSimulator(InferenceEngine engine) {
    this((JunctionTreeInferenceEngine)engine);
  }

	public JunctionTreeSimulator(JunctionTreeInferenceEngine engine) {
    this(engine, ONE_PASS);
  }

	public JunctionTreeSimulator(JunctionTreeInferenceEngine engine, int simulationMethod) {
    this.simulationMethod = simulationMethod;
    this.engine = engine;
    jt = engine.getJunctionTree();
    if (jt == null)
      throw new RuntimeException(getClass().getName()+": Unknown junction tree.");
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

	protected Potential[] getAllPotentials() {
    int size = 2*jt.size()-1;
    Potential[] potential = new Potential[size];

    Iterator it = jt.iterator();
    while (it.hasNext()) {
      Cluster X = (Cluster)it.next();
      X.flag = 0;
		}

    int pos = 0;
    it = jt.iterator();
    while (it.hasNext()) {
      Cluster X = (Cluster)it.next();
      // STEP 1: If cluster is unvisited, ...
      if (X.flag == 0) {
        // STEP 2c: ...do a draw'n'distribute propagation.
        pos = getAllPotentials(X, potential, pos);
      }
    }

    return potential;
  }

	protected int getAllPotentials(Cluster X, Potential[] potential, int pos) {
		X.flag = 1;

    Potential pX = (Potential)X.data;
    potential[pos++] = pX;

    // STEP 2: Get all potentials of neighbors, if any.
    Collection neX = jt.getNeighbors(X);
    Iterator it = neX.iterator();
    while (it.hasNext()) {
      Cluster Y = (Cluster)it.next();
			// STEP 2a: If Y is unvisited get "its" potentials...
      if (Y.flag == 0) {
  			//  Get the potential of the sepset...
  			Sepset R = (Sepset)jt.getEdge(X,Y); // The sepset between X and Y.
  			Potential pR = (Potential)R.data;
  			potential[pos++] = pR;
        pos = getAllPotentials(Y, potential, pos);
			}
		}
    return pos;
	}


  public void setSimulationMethod(int method) { 
    simulationMethod = method;
    if (simulationMethod == ONE_PASS)
      Format.printf("Simulation method: ONE_PASS\n");
    else if (simulationMethod == TWO_PASS)
      Format.printf("Simulation method: TWO_PASS\n");
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
  
  		engine.clearEvidence();
   		Potential[] pX = null;
    	int nbrOfPotentials = 0;
      if (optimizeFor == SPEED) {
        // This will require twice as much memory, but will run about three
        // times faster. The call to store() in a potential will allocate the
        // extra memory needed.
    		pX = getAllPotentials();
    		nbrOfPotentials = pX.length;
  	  	for (int k=nbrOfPotentials; --k>=0;)
  		  	pX[k].store();
      } else if (optimizeFor == MEMORY) {
        // Do nothing; a global update will be performed after each sample-turn.
      }
  
      long lastTime = 0;
      FormatString debugFmtstr = null;
      if (isDebugging) {
        lastTime = debugger.getTimer();
        debugFmtstr = Format.out.compileFormatString("Simulation #%d finished after %.2fs. Current speed is %.2f simulations/s");
			}
      Parameters p = new Parameters();
  		for (count=n; --count>=0;) {
   			int[] value = new int[len];
        draw(vs, value);
  			c.add(value);
        if (isDebugging && count % 100 == 0 && count != 0) {
          int idx = n-count;
          long time = debugger.getTimer();
          long diffTime = time-lastTime;
          double performance = 100000.0/diffTime;
          debugger.write(debugFmtstr, p.add(idx).add(time/1000.0).add(performance));
          lastTime = time;
				}
        if (optimizeFor == SPEED) {
    			for (int k=nbrOfPotentials; --k>=0;)
  	  			pX[k].restore();
        } else if (optimizeFor == MEMORY) {
       		engine.clearEvidence();
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
    Iterator it = jt.iterator();
    while (it.hasNext()) {
      Cluster X = (Cluster)it.next();
      X.flag = 0;
		}

		// STEP 2: For all cliques, draw one instance and then update...
    it = jt.iterator();
    if (it.hasNext()) {
  		// STEP 2a: Choose an arbitrary cluster X.
      Cluster X = (Cluster)it.next();
      // STEP 2b: If cluster is unvisited, ...
      if (X.flag == 0) {
        // STEP 2c: ...do a draw'n'distribute propagation.
        Potential pX = (Potential)X.data;
		  	pX.normalize();
        if (simulationMethod == ONE_PASS)
          sampleAndDistribute(X);
        else if (simulationMethod == TWO_PASS)
          sampleAndUpdate(X);
      }
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
    simulate();
    for (int i=0; i<value.length; i++) {
      value[i] = vs[i].draw();
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


  /**
  Samples from the joint probability function of a given a clique and 
  recursively updates (makes consistent) and samples unvisited neighbors
  using the one-pass method [HBBN00].
  
  @param X - the clique to sample from
  */
	protected void sampleAndDistribute(Cluster X) {
		// STEP 1: Mark X
		X.flag = 1;

    // STEP 2: Draw a value
    Potential pX = (Potential)X.data;
    int[] sampleIndex = pX.drawAndObserve();
		//    Format.printf("pot(X)=%s\n", new Parameters(pX));

    // STEP 3: Pass a message from X to each of its unmarked neighboring
    //         clusters, if any.
    Collection neX = jt.getNeighbors(X);
    Iterator it = neX.iterator();
    while (it.hasNext()) {
      Cluster Y = (Cluster)it.next();
			//      Format.printf("pot(Y)=%s\n", new Parameters(Y.data));
      if (Y.flag == 0)
        engine.passInstantiatedMessage(X, Y, sampleIndex);
		}

    // STEP 4: Call sampleAndDistribute recursively on X's unmarked 
    // neighboring clusters, if any.
    it = neX.iterator();
    while (it.hasNext()) {
      Cluster Y = (Cluster)it.next();
      if (Y.flag == 0)
        sampleAndDistribute(Y);
		}
	}

  /**
  Samples from the joint probability function of a given a clique and 
  updates (makes consistent) the whole junction tree using the (inefficient)
  two-pass method [HBBN00].
  
  @param X - the clique to sample from
  */
	protected void sampleAndUpdate(Cluster X) {
		// STEP 1: Mark X
		X.flag = 1;

    // STEP 2: Draw a value
    Potential pX = (Potential)X.data;
    int[] sampleIndex = pX.drawAndObserve();
		//    Format.printf("pot(X)=%s\n", new Parameters(pX));

    // STEP 3: Now perform a global propagation update
    engine.globalPropagation(X);

	}


/* *******************************************************************************

  Methods for PassMessageListener and PassMessageEvent.

 ******************************************************************************* */
	public void addPassMessageListener(PassMessageListener l) {
		if (!passMessageListeners.contains(l)) {
      passMessageListeners.add(l);
      hasPassMessageListeners = true;
		}
	}

	public void removePassMessageListener(PassMessageListener l) {
		if (passMessageListeners.contains(l)) {
      passMessageListeners.remove(l);	
      hasPassMessageListeners = (passMessageListeners.size() > 0);
		}
	}

	protected void processProjection(PassMessageEvent e) {
    Iterator it = passMessageListeners.iterator();
    while (it.hasNext()) {
      PassMessageListener l = (PassMessageListener)it.next();
      l.projection(e);
		}
	}

	protected void processAbsorption(PassMessageEvent e) {
    Iterator it = passMessageListeners.iterator();
    while (it.hasNext()) {
      PassMessageListener l = (PassMessageListener)it.next();
      l.absorption(e);
		}
	}

	protected void processMessagePassed(PassMessageEvent e) {
    Iterator it = passMessageListeners.iterator();
    while (it.hasNext()) {
      PassMessageListener l = (PassMessageListener)it.next();
      l.messagePassed(e);
		}
	}



/* *******************************************************************************

  Debug methods, i.e. methods for asserting the correctness of the algorithms.

 ******************************************************************************* */

	/**
   Checks if initialization satisfies equation (2) on page 9 in
   "Inference in Belief Networks: A Procedural Guide" by Huang and Darwiche.
   */
	public void assertEquation2() {
    Runtime rt = Runtime.getRuntime();
		//    Format.printf("assert Eq 2. free memory: %d kb, ",
		//      p.add(rt.freeMemory()/1e3));
		// Create a cluster containing all variables in all clusters, i.e.
		// all variables in the graph.
    Potential pot = null;
    Collection Ss = jt.getEdges();
    Iterator it = jt.iterator();
    while (it.hasNext()) {
      Cluster X = (Cluster)it.next();
			//      Format.printf("X=%s\n", p.add(X));
      Potential pX = (Potential)X.data;
      Collection XE = jt.getEdges((Vertex)X);
      Iterator itE = XE.iterator();
      while (itE.hasNext()) {
        Sepset Sxy = (Sepset)itE.next();
        if (Ss.contains(Sxy)) {
						//          Format.printf("Sxy=%s\n", p.add(Sxy));
          Potential pSxy = (Potential)Sxy.data;
          pX.divideWith(pSxy);
					//          Format.printf("pX/pSxy=%s\n", p.add(pX));
          itE.remove();
          Cluster Y = (Cluster)Sxy.otherVertex(X);
          Sepset Syx = (Sepset)jt.getEdge(Y,X);
          Ss.remove(Sxy);
          break;
				}
			}
      if (pot == null)
        pot = pX;
      else
        pot = Potential.multiply(pot,pX);
			//      Format.printf("pot=%s\n", p.add(pot));
    } // while

    double sum = pot.sum();
    Format.printf("sum(P(U))=%.3f (should be equal to 1)\n", new Parameters(sum));
  }

} // class JunctionTreeSimulator


/*
HISTORY

2000-08-08
* Created from former JunctionTreeInferenceEngine which also was a Simulator.
	
*/
