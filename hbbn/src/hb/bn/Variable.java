package hb.bn;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import hb.format.Format;
import hb.format.Parameters;
import hb.format.FormatWriter;
import hb.format.ArrayConversionParser;
import hb.math.graph.Cluster;
import hb.math.graph.Vertex;

public class Variable extends Vertex {
	protected static Random random = new Random();
  protected Cluster familyCluster;
  protected Potential cp;
  protected Likelihood likelihood = null;
  protected Likelihood likelihoodNew = null;
  protected Potential dist;
  protected boolean isLocked = false;
  protected boolean isObserved = false;
  public boolean distValid = false;
  State[] state;
  int nbrOfStates;
  Map stateMap = new HashMap();

  Set condProbs = new HashSet();
  int[] dummy = {};


	public Variable(String name, State[] s) {
    super(name);
    state = s;
    nbrOfStates = state.length;
    for (int i=0; i<nbrOfStates; i++)
      stateMap.put(state[i].name, new Integer(i));
    weight = (double)nbrOfStates;
    dist = new Probability(this);
    likelihood = new Likelihood(this);
    likelihoodNew = new Likelihood(this);
	}

	public Variable(String name, String[] s) {
    super(name);
    state = new State[s.length];
    nbrOfStates = state.length;
    for (int i=0; i<nbrOfStates; i++) {
      state[i] = new State(s[i]);
      stateMap.put(state[i].name, new Integer(i));
    }
    weight = (double)nbrOfStates;
    dist = new Probability(this);
    likelihood = new Likelihood(this);
    likelihoodNew = new Likelihood(this);
	}

	public void lock() {
    isLocked = true;
	}

	public void unlock() {
    isLocked = false;
	}

	public void reset() {
    condProbs.clear();
    stateMap.clear();
    state = new State[0];
    likelihood.clear();
    likelihoodNew.clear();
	}

	public boolean clear() {
		if (isLocked)
      return false;

    likelihoodNew.clear();
    likelihood.clear();
    isObserved = false;
    return true;
	}

	public int size() {
    return nbrOfStates;
	}

	public int getNbrOfStates() {
    return nbrOfStates;
	}

	public State[] getStates() {
    return state;
	}

	public State getState(String name) {
    int idx = ((Integer)stateMap.get(name)).intValue();
    return getState(idx);
	}

	public State getState(int k) {
    return state[k];
	}

	public String getStateName(int k) {
    return state[k].toString();
	}

	public void setPotential(Potential cp) {
			//    if (cp == null) System.err.println("**** ERROR: setPotential(null) ****");
    this.cp = cp;
	}

	public Potential getPotential() {
    return cp;
	}

	/**
   @return false if the observation was the same as previous ones. 
           Otherwise, true is returned.
	 */
	public boolean observe(int state) {
		if (isLocked)
      return false;

    likelihoodNew.fill(0.0);
    likelihoodNew.set(state, 1.0);
    if (!isObserved) {
      isObserved = true;
      return false;
    }
    if (likelihoodNew.equals(likelihood))
      return false;
    return true;
	}

	public boolean observe(String s) {
		if (isLocked)
      return false;

    int idx = ((Integer)stateMap.get(s)).intValue();
    if (idx == -1)
      throw new RuntimeException("ERROR: Can not observe varible '"+name+
          "' since it does not contain a state called '"+s+"'!");
    return observe(idx);
  }

	public boolean isObserved() {
    return isObserved;
	}

	public void setLikelihood(Likelihood likelihood) {
			//		if (!isLocked)
      this.likelihood = likelihood;
	}

	public Likelihood getLikelihood() {
    return likelihood;
	}

	public void clearLikelihood() {
			//		if (!isLocked)
      likelihood.clear();
	}

	public void updateLikelihood() {
			//		if (!isLocked)
      likelihood.assign(likelihoodNew);
	}

	public Potential getProbability() {
    if (familyCluster == null)
      return null;
    Potential pF = (Potential)familyCluster.data;
    if (pF == null)
      return null;
    pF.marginalize(this);
    dist.normalize();  // To handle evidence.
    return dist;
	}

	public int draw() {
    Potential p = getProbability();
    int len = p.value.length;
    double cumsum[] = new double[len];
    cumsum[0] = p.value[0];  cumsum[len-1] = 1;
    for (int i=1; i<len-1; i++)
      cumsum[i] = cumsum[i-1]+p.value[i];
    double rnd = random.nextDouble();
    int state = len-1;
    for (int i=0; i<len-1; i++) {
			if (rnd < cumsum[i]) {
         state = i;
         break;
			}
   	}
    return state;
  }

	public int drawAndObserve() {
    int drawnState = draw();
    observe(drawnState);
    return drawnState;
	}

	public void setFamilyCluster(Cluster X) {
    familyCluster = X;
	}

	public Cluster getFamilyCluster() {
    return familyCluster;
  }

	public void set(String s, double pr) {
    int idx = ((Integer)stateMap.get(s)).intValue();
  }

	public void set(String[] s, double[] pr) {
    for (int k=0; k<s.length; k++)
      set(s[k],pr[k]);
  }

	public void set(int[] s, double[] pr) {
  }

	public String toString() {
	  return name;
	}
	
	private static String arrayToString(String fmtstr, double[] i) {
    String s = "";
    fmtstr = "%s "+fmtstr;
    Parameters p = new Parameters();
		for (int k=0; k<i.length; k++)
      s = Format.sprintf(fmtstr, p.add(s).add(i[k]));
    return s;
	}

	/**
   Gets a formatted String describing the variable.
   @return a formatted String describing the variable.
	 */
	public String toString(int verboseLevel) {
    StringWriter sout = new StringWriter();
    FormatWriter out = new FormatWriter(sout);
    out.addConversionParser(
       new ArrayConversionParser(out.getConversionParsers()));

    StringBuffer s;
		try {
  		switch (verboseLevel) {
  			case 0 : 
          return Format.sprintf("%s", new Parameters(name));
  			case 10: 
          s = new StringBuffer(Format.sprintf(
  					 "variable %s {\n"+
             "  distribution: %s\n"+
             "  likelihood  : %s\n"+
  					 "}\n",
  					 new Parameters(name)
                 .add(getProbability())
                 .add(likelihood)
  				 ));
  				 return s.toString();
  			case 12: 
          s = new StringBuffer(Format.sprintf(
  					 "variable %s {\n"+
             "  distribution: %s\n"+
             "  likelihood  : %s\n"+
  					 "  (x,y)       = (%.3f,%.3f)\n"+
  					 "}\n",
  					 new Parameters(name)
                 .add(dist)
                 .add(likelihood)
  							 .add(x).add(y)
  				 ));
  				 return s.toString();
  			case 99: 
          try {
            out.write("%(%s,)", new Parameters(state));
          } catch(java.io.IOException ex) {}
          s = new StringBuffer(Format.sprintf(
  					 "variable %s {\n"+
						 "  description = %s\n"+
             "  states        (%s)\n"+
             "  distribution: %s\n"+
  					 "  (x,y)       = (%.3f,%.3f)\n"+
  					 "  distance    = %i\n"+
  					 "  parent      = %s\n"+
  					 "  color       = %s\n"+
  					 "  timestamps  = %i/%i\n"+
  					 "}\n",
  					 new Parameters(name)
                 .add(description)
                 .add(sout)
                 .add(getProbability())
  							 .add(x).add(y)
  							 .add(distance)
  							 .add(parent==null?"<none>":parent.name)
  							 .add(getColorString())
  							 .add(timestamp_discovered)
  							 .add(timestamp_finished)
  				 ));
  				 return s.toString();
  		}
 		} catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
		}
		return name;
 	}

	public static void main(String args[]) {
    String[] s123 = {"1", "2", "3"};
    String[] sOnOff = {"on", "off"};
    Variable a = new Variable("A", sOnOff);
    a.set("on",0.5); a.set("off",0.5);
    Variable b = new Variable("B", sOnOff);
    b.set("on",0.5); b.set("off",0.5);
    Variable c = new Variable("C", sOnOff);
    c.set("on",0.5); c.set("off",0.5);
    Variable d = new Variable("D", sOnOff);
    d.set("on",0.5); d.set("off",0.5);
    Format.printf("%s\n", p.add(a.toString(10)));
    Format.printf("%s\n", p.add(b.toString(10)));
    Format.printf("%s\n", p.add(c.toString(10)));
    Format.printf("%s\n", p.add(d.toString(10)));

    Cluster ab = new Cluster(); ab.add(a); ab.add(b);
    Format.printf("%s\n", p.add(ab.toString(10)));

    a.observe("on");
    Format.printf("%s\n", p.add(a.toString(10)));
    a.observe("off");
    Format.printf("%s\n", p.add(a.toString(10)));
	}

  static Parameters p = new Parameters();
} // class Variable




/*
HISTORY

2000-04-26
* Added lock() and unlock().

2000-04-07
* Added the function draw() and drawAndObserve().

990602
* Added size() (=#states)

990406
* Created.
	
*/
