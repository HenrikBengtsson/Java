package hb.bn;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SimulationList extends ArrayList implements Collector {
  private BeliefNetwork bn;
  protected int[] map = null;
  private int size;

	public SimulationList(BeliefNetwork bn) {
    this.bn = bn;
    this.size = bn.size();
	}

	public SimulationList(BeliefNetwork bn, int size) {
    super(size);
    this.bn = bn;
    this.size = bn.size();
	}

	/**
    Gets the simulated values as a matrix.

    @return an two-dimensional int-array.
	 */
	public int[][] get() {
    int[][] matrix = new int[size()][];
    for (int i=0; i<matrix.length; i++)
      matrix[i] = (int[])get(i);
    return matrix;
	}

	public void add(int[] value) {
		if (map != null) {
      int[] mediator = new int[size];
      for (int i=size; --i>0;)
        mediator[i] = value[map[i]];
      value = mediator;
		}
    super.add(value);
	}

	public void setMap(int[] map) {
    this.map = map;
	}

	/**
    Reshuffles the values in all samples according to a shuffle map.
	 */
	public void reshuffle(int[] shuffleMap) {
    int m = shuffleMap.length;
    int[] mediator = new int[m];

    Iterator it = iterator();
    while (it.hasNext()) {
      int[] value = (int[])it.next();
      for (int i=0; i<m; i++)
        mediator[i] = value[shuffleMap[i]];
      System.arraycopy(mediator,0, value,0, m);
    }
	}

	public int[] getSimulation(int index) {
    return (int[])get(index);
	}

	public BeliefNetwork getBeliefNetwork() {
    return bn;
	}


	public int[] getSimulation(int index, int[] variableMap) {
    int[] value = getSimulation(index);

    // STEP 2: Create a subarray.
    int[] subValue = new int[variableMap.length];
    for (int i=0; i<subValue.length; i++)
      subValue[i] = value[variableMap[i]];

    return subValue;
	}

	public int[] getSimulation(int index, Variable[] variable) {
		int[]	map = bn.indicesOf(variable);
    return getSimulation(index, map);
	}


}

/*
HISTORY

2000-05-02
* Created.
	
*/
