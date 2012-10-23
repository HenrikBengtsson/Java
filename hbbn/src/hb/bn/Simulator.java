package hb.bn;

import hb.util.Debugger;

/**
 
 */

public interface Simulator {
	abstract public Collector simulate(int n, Collector c);
	abstract public Collector simulate(int n);
	abstract public void simulate();
	abstract public int[] draw();
	abstract public int draw(Variable v);
	abstract public int drawAndObserve(Variable v);
	abstract public int draw(String var);
	abstract public int drawAndObserve(String var);
	abstract public void setDebugger(Debugger d);
} // class Simulator

/*
HISTORY

2000-08-08
* Added setDebugger(Debugger).
2000-05-02
* Created from InferenceEngine.
	
*/
