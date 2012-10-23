package hb.bn;

import hb.util.Debugger;

/**
 
 */

abstract public class InferenceEngine {
  protected BeliefNetwork bn = null;
  protected boolean updateNeeded = false;

	public InferenceEngine(BeliefNetwork bn) {
    this.bn = bn;
	}

	public BeliefNetwork getBeliefNetwork() {
    return bn;
	}

	abstract public void update();

	abstract public void observe(Variable v, int state);
	abstract public void observe(Variable v, String statename);
	abstract public void observe(String varname, int state);

	abstract public void clearEvidence();

	abstract public void setDebugger(Debugger d);
} // class InferenceEngine

/*
HISTORY

2000-05-02
* Extracted the methods for simulation into the Simulator interface.
2000-04-26
* Conforming HBBN to be more look alike to Kevin Murphy's BNT. The reason is
  to make BN packages to be more conform. My package is much smaller right now
  so I will adjust my API towards his.
* Created.
	
*/
