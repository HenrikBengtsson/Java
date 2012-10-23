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

class Evidence {
}

class HardEvidence extends Evidence {
}

class SoftEvidence extends Evidence {
}

public class JunctionTreeInferenceEngine extends InferenceEngine implements Simulator {
  protected Debugger debugger = null;
  protected boolean isDebugging = false;
  protected JunctionTree jt = null;
  protected List passMessageListeners = new ArrayList();
  protected boolean hasPassMessageListeners = false;
  public Potential potential;
  private boolean validPotential = false;
  private boolean retractionNeeded = false;
  protected int optimizeFor = SPEED;
  protected int simulationMethod = ONE_PASS;
  public final static int UNCONDITIONED = 0;
  public final static int CONDITIONED   = 1;
  public final static int ONE_PASS      = 2;
  public final static int TWO_PASS      = 3;
  public final static int SPEED  = 1;
  public final static int MEMORY = 2;

  Map vertexClusterMap = new HashMap();

	public JunctionTreeInferenceEngine(BeliefNetwork bn) {
    super(bn);
    reset();
  }

	public void setDebugger(Debugger d) {
    debugger = d;
    isDebugging = (debugger != null);
	}

	public JunctionTree getJunctionTree() {
    return jt;
	}

	protected void reset() {
  	jt = JunctionTree.asJunctionTree(bn);
		//    System.out.println("bn="+bn.toString());
		//    System.out.println("jt="+jt.toString());
    Iterator it = bn.iterator();
    while (it.hasNext()) {
      Variable u = (Variable)it.next();
      Cluster fau = jt.getFamilyCluster(u);
      u.setFamilyCluster(fau);
			//      Format.printf("fa(%s)=%s\n", new Parameters(u).add(fau));
    }
    clearEvidence();
  }

	public void clearObservations() {
    clearEvidence();
	}

	public void enterEvidence(Evidence e) {
	}

/* *******************************************************************************

  super class InferenceEngine

 ******************************************************************************* */
	public void update() {
//    retractionNeeded = true; // Removed 2000-08-08!!! WARNING!!!
    updateFast();
  }

	public void observe(Variable v, int state) {
			//    Format.printf("1. %s.retractionNeeded=%l  ", p.add(v).add(retractionNeeded));
    retractionNeeded |= v.observe(state);
		//    Format.printf("2. %s.retractionNeeded=%l\n", p.add(v).add(retractionNeeded));
	}

	public void observe(Variable v, String statename) {
    retractionNeeded |= v.observe(statename);
	}

	public void observe(String varname, int state) {
    Variable v = bn.getVariable(varname);
    retractionNeeded |= v.observe(state);
	}

	/*
   It is important that this method is fast. The best way to do it is to cache the 
   cliques. Using global propagation etc. this methods take about twice as long time
   as the actual simulation process.
	 */
	public void clearEvidence() {
    boolean updateNeeded = false;
    Iterator it = bn.iterator();
    while (it.hasNext()) {
      Variable v = (Variable)it.next();
      updateNeeded |= v.clear();
		}
    initialize();
    if (updateNeeded)
      updateAfterObservations();
    else
      globalPropagation();
	}


	public void updateFast() {
		if (retractionNeeded) {
			// *** Global Retraction ***
     	/* 1. Reinitialize the join tree potentials. */
      initialize();
	  	/* 2. Incorporate each observation in e2 as usual. */
      updateAfterObservations();
		} else {
 			// *** Global Update ***
      updateAfterObservations();
		}
	}

	protected void updateAfterObservations() {
    Iterator it = bn.iterator();
    while (it.hasNext()) {
      Variable v = (Variable)it.next();
      v.updateLikelihood();
      Cluster fav = v.getFamilyCluster();
	  	//    Format.printf("fa(v)=%s ", p.add(fav));
      Potential pfav = (Potential)fav.data;
  	  //      Format.printf("phi(fa(v))=%s ", p.add(pfav));
			//    	Format.printf("%s\n", p.add(v.toString(10)));
      pfav.multiplyWith(v.getLikelihood());
		}
    globalPropagation();
	}

	public void collectEvidence(Cluster X, Cluster caller) {
			//    Format.printf("CollectionEvidence(%s) called by %s\n", p.add(X).add(caller));
		// STEP 1: Mark X
		X.flag = 1;

    // STEP 2: Call CollectEvidence recursively on X's unmarked neighboring
    //         clusters, if any.
    Collection neX = jt.getNeighbors(X);
    Iterator it = neX.iterator();
    while (it.hasNext()) {
      Cluster Y = (Cluster)it.next();
      if (Y.flag == 0)
        collectEvidence(Y, X);      
		}

    // STEP 3: Pass a message from X to the cluster which invoked
    // CollectEvidence(X).
    if (caller != null) {
      passMessage(X, caller);
    }
	}

	public void distributeEvidence(Cluster X) {
			//    Format.printf("DistributeEvidence(%s)\n", p.add(X));
		// STEP 1: Mark X
		X.flag = 1;

    // STEP 2: Pass a message from X to each of its unmarked neighboring
    //         clusters, if any.
    Collection neX = jt.getNeighbors(X);
    Iterator it = neX.iterator();
    while (it.hasNext()) {
      Cluster Y = (Cluster)it.next();
      if (Y.flag == 0)
        passMessage(X, Y);
		}

    // STEP 3: Call DistributeEvidence recursively on X's unmarked 
    // neighboring cluster, if any.
    it = neX.iterator();
    while (it.hasNext()) {
      Cluster Y = (Cluster)it.next();
      if (Y.flag == 0)
        distributeEvidence(Y);
		}
	}

	protected void passMessage(Cluster X, Cluster Y) {
			//    Format.printf("Passing message from %s to %s", p.add(X).add(Y));
		Sepset R = (Sepset)jt.getEdge(X,Y); // The sepset between X and Y.

    Potential pX = (Potential)X.data;
    Potential pR = (Potential)R.data;
    Potential pY = (Potential)Y.data;

		//    Format.printf("Potentials: (pX,pR,pY) %s,%s,%s\n", p.add(pX).add(pR).add(pY));

    Potential pRold = (Potential)pR.clone();
    pR.project(pX);
    // Now tell all listeners about the projection...
    if (hasPassMessageListeners) {
      PassMessageEvent e = new PassMessageEvent(X, R, Y);
      processProjection(e);
  	}
    pY.absorb(pR, pRold);
    // Now tell all listeners about the absorption...
    if (hasPassMessageListeners) {
      PassMessageEvent e = new PassMessageEvent(X, R, Y);
      processAbsorption(e);
  	}

    // update the sepset and clique.
    R.data = pR;
    Y.data = pY;

    // Now tell all listeners about the passed message...
    if (hasPassMessageListeners) {
      PassMessageEvent e = new PassMessageEvent(X, R, Y);
      processMessagePassed(e);
  	}
	}

	protected void initialize() {
    Potential potential;
    Iterator it;

    // STEP 1
    // Initialize all clusters
    it = jt.iterator();
    while (it.hasNext()) {
      Cluster C = (Cluster)it.next();
      potential = (Potential)C.data;
      if (potential == null) {
        potential = new Potential(C);
        C.data = potential;
      }
      potential.fill(1.0);
		}

    // Initialize all sepsets
    it = jt.edgeIterator();
    while (it.hasNext()) {
      Sepset Sxy = (Sepset)it.next();
      potential = (Potential)Sxy.data;
      if (potential == null) {
        potential = new Potential(Sxy);
        Sxy.data = potential;
      }
      potential.fill(1.0);
			//      Sxy.name = Sxy.name+"*";
		}

    // STEP 2
    // For each variable V:
    //  (a) multiply its potential to the potential of its family cluster. 
    //  (b) set each likelihood element Lambda_V(v) to 1.
    it = bn.iterator();
    while (it.hasNext()) {
      Variable v = (Variable)it.next();
			//      Format.printf("v=%s ", p.add(v));
      Cluster fav = v.getFamilyCluster();
			//      Format.printf("fa(v)=%s ", p.add(fav));
      Potential pfav = (Potential)fav.data;
			//      Format.printf("phi(fa(v))=%s ", p.add(pfav));
      pfav.multiplyWith(v.getPotential());
      v.clearLikelihood();
		}
    retractionNeeded = false;
  }

	protected void globalPropagation(Cluster X) {
    Iterator it;
		// STEP 1: Choose an arbitrary cluster X.
    // Already done.
   
		// STEP 2: Unmark all clusters. Call CollectEvidence(X).
    it = jt.iterator();
    while (it.hasNext()) {
      Cluster Y = (Cluster)it.next();
      Y.flag = 0;
		}
    collectEvidence(X, null);

		// STEP 3: Unmark all clusters. Call DistributeEvidence(X).
    it = jt.iterator();
    while (it.hasNext()) {
      Cluster Y = (Cluster)it.next();
      Y.flag = 0;
		}
    distributeEvidence(X);
	}

	protected void globalPropagation() {
    Iterator it = jt.iterator();
    if (it.hasNext()) {
  		// STEP 1: Choose an arbitrary cluster X.
      Cluster X = (Cluster)it.next();
      // Make a global propagation.
      globalPropagation(X);
    }
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
  
  		clearEvidence();
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
       		clearEvidence();
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
    observe(v, drawnState);
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
  Makes a neighboring clique (and their separator set) consistent with the given
  instantiated (e.g. sampled) clique (see [HBBN00]). This method gives the same
  result as the more general passMessage(Cluster, Cluster) method, but is more
  efficient.
  
  @param X - the instantiated Cluster 
  @param Y - the unconsistent Cluster which potential will be updated
  @param sampleIndex - the index of the sampled value in clique X specified as
                       an integer array
  */
	protected void passInstantiatedMessage(Cluster X, Cluster Y, int[] sampleIndex) {
		Sepset R = (Sepset)jt.getEdge(X,Y); // The sepset between X and Y.
		//    Format.printf("Passing message from %s to %s via %s\n", new Parameters(X).add(Y).add(R));

    Potential pX = (Potential)X.data;
    Potential pR = (Potential)R.data;
    Potential pY = (Potential)Y.data;

		//    Format.printf("pot(X)=%s\n", new Parameters(pX));

		//    Format.printf("Potentials: (pX,pR,pY) %s,%s,%s\n", p.add(pX).add(pR).add(pY));

    // STEP 1. Project the (single) drawn value onto the sepset.
    int[] map = pR.getMapInOther(pX);
    int[] projectIndex = new int[map.length];
    for (int i=0; i<map.length; i++) {
				//      Format.printf("map[%d]=%d\n", new Parameters(i).add(map[i]));
      projectIndex[i] = sampleIndex[map[i]];
    }
    double normalizationConst = 1/pR.get(projectIndex);
		pR.clear();
    pR.set(projectIndex, 1.0);
    // Now tell all listeners about the projection...
    if (hasPassMessageListeners) {
      PassMessageEvent e = new PassSampleMessageEvent(X, R, Y);
      processProjection(e);
  	}

    // STEP 2. Absorb the (single) value in the sepset into the other clique.
    // X -> R -> Y
    int[] RYmap = pY.getMap(pR);
    int pYsize = pY.value.length;
    int pRrank = pR.rank();
    int[] Ridx = new int[pRrank];
    for (int i=0; i<pYsize; i++) {
      int[] Yidx = pY.getIndex(i);
      for (int j=0; j<pRrank; j++) {
        Ridx[j] = Yidx[RYmap[j]];
        if (Ridx[j] != projectIndex[j]) {
					// Not the sampled index.
          pY.value[i] = 0.0;
          break;
				}
			}
      pY.value[i] *= normalizationConst;
		}
    // Now tell all listeners about the absorption...
    if (hasPassMessageListeners) {
      PassMessageEvent e = new PassSampleMessageEvent(X, R, Y);
      processAbsorption(e);
  	}

    // Now tell all listeners about the passed sampled message...
    if (hasPassMessageListeners) {
      PassMessageEvent e = new PassSampleMessageEvent(X, R, Y);
      processMessagePassed(e);
  	}

		//    pY.normalize();

		//    Format.printf("pot(Y)=%s\n", new Parameters(pY));
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
        passInstantiatedMessage(X, Y, sampleIndex);
		}

    // STEP 4: Call sampleAndDistribute recursively on X's unmarked 
    // neighboring clusters, if any.
    it = neX.iterator();
    while (it.hasNext()) {
      Cluster Y = (Cluster)it.next();
      if (Y.flag == 0)
        sampleAndDistribute(Y);
		}
    retractionNeeded = false;
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
    globalPropagation(X);

    retractionNeeded = false;
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

} // class JunctionTreeInferenceEngine


/*
HISTORY

2000-08-08
* Renamed passSampleMessage() to passInstantiateMessage().
* Added some javadoc comments.
2000-07-05
* There was a bug in  storing and restoring the potentials in the simulate()-
  method. It resulted in one variable was sampled the same value all the time.
	The code said for (int k=N;--k>0;) instead of for (int k=N;--k>=0);!!!.
2000-06-30
* Catches java.lang.OutOfMemoryError exceptions in simulate(int n). Reports how
  many successful simulations there were made before out of memory.  

2000-06-29
* Updated the simulation(int n) method so it does not use clearEvidence after each
  turn. Using clearEvidence requires basically a global update which is twice as
  expensive as the sample procedure itself. Now it caches all the potentials in
  the beginning at restores them after each sample.
* Modified passSampleMessage().
2000-06-20
* Added support for PassMessageEvents.
2000-05-23
* Rearranged the methods into different sections according to super class and 
  interfaces.
* Updated simulate() so it does not draw from already visited cliques. This was
  a miss by me, but the result was never incorrect... I think!
2000-05-02
* Updated passSampleMessage() to just send the single value instead of a regular 
  project & absorb message pass.
2000-04-27
* Yesterday night I came up with the idea of making a one-pass simulation 
  passing. See sampleAndDistribute(). - Much faster!

2000-04-26
* Conforming HBBN to be more look alike to Kevin Murphy's BNT. The reason is
  to make BN packages to be more conform. My package is much smaller right now
  so I will adjust my API towards his.
* Created.
	
*/
