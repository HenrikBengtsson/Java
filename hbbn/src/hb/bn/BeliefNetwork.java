package hb.bn;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import hb.bn.xbn.*;
import hb.math.DoubleArray;
import hb.math.graph.Graph;
import hb.math.graph.Edge;
import hb.math.graph.Cluster;
import hb.math.graph.Sepset;
import hb.format.Format;
import hb.format.ArrayConversionParser;
import hb.format.FormatWriter;
import hb.format.Parameters;
import hb.text.TextArea;


public class BeliefNetwork extends Graph {
  public Potential potential;
  private boolean validPotential = false;
  private boolean retractionNeeded = false;

	public BeliefNetwork() {
	}

	public BeliefNetwork(XBN_BNModel bnmodel) {
    name = bnmodel.name;

    // build variables
    XBN_Var[] var = bnmodel.variables.var;
    for (int i=0; i<var.length; i++) {
      Variable v = new Variable(var[i].name, var[i].getStatenameAsStrings());
      v.description = var[i].description.value;
      Point pv = v.getPoint();
      pv.setLocation(var[i].xpos, var[i].ypos);
      add(v);
		}

    // build edges
    XBN_Arc[] arc = bnmodel.structure.arc;
    for (int i=0; i<arc.length; i++) {
      addEdge(arc[i].parent, arc[i].child, Edge.DIRECTED);
		}

    // build distributions
    XBN_Dist[] dist = bnmodel.distributions.dist;
    for (int i=0; i<dist.length; i++) {
      Variable Y = (Variable)getVertex(dist[i].privatE.name);
			//      Format.printf("DIST: Y=%s\n", p.add(Y));
      Potential potential = null;
      XBN_CondSet condset = dist[i].condset;
      if (condset != null) {
        XBN_CondElem[] condelem = condset.condelem;
        if (condelem != null) {
          Variable[] X = new Variable[condelem.length];
    			for (int j=0; j<X.length; j++) {
            X[j] = (Variable)getVertex(condelem[j].name);
						//            Format.printf("  X[%d]=%s\n", p.add(j).add(X[j]));
			  	}
          potential = new ConditionalProbability(Y,X);
				}
			} else {
        potential = new Probability(Y);
			}

      XBN_DPIs dpis = dist[i].dpis;
      if (dpis != null) {
        XBN_DPI[] dpi = dpis.dpi;
        if (dpi != null) {
					for (int j=0; j<dpi.length; j++) {
            double sum = 0.0;
      			double value[] = new double[Y.size()]; // number of states.
            int[] indexes = null;
      			int idx = 0;
      			StringTokenizer st = new StringTokenizer(dpi[j].value);
      			while(st.hasMoreTokens()) {
              String token = st.nextToken();
      				value[idx] = Double.valueOf(token).doubleValue();
							/*
              // Detta hjälper ibland
              if (value[idx] == 0.0)
								value[idx] = 1e-300; // Double.MIN_VALUE;
							*/
              sum += value[idx];
              idx++;
						}
      			if (idx != value.length) {
      				throw new RuntimeException(
                  "XBN FORMAT ERROR: A distribution has an incorrect "+
      						"number of states ("+idx+"). "+value.length+
      						" states were expected ("+dpi[j]+")."
              );
		  			}
            if (Math.abs(sum-1.0) > 1e-5) {
              Format.printf("Sum of distribution is not equal to one. "+
                            "It is %f. (%s)", p.add(sum).add(dpi[j]));
						}
  					// Get the indexes, if any...
  					if (dpi[j].indexes != null) {
							if ( !(potential instanceof ConditionalProbability) ) {
  							throw new RuntimeException(
  									"XBN FORMAT ERROR:  Unexpected attribute INDEXES "+
  									"in "+dpi[j]+"."
  							);
							}
              indexes = new int[potential.rank()-1];
        			idx = 0;
  						st = new StringTokenizer(dpi[j].indexes);
  						while(st.hasMoreTokens()) {
  							String token = st.nextToken();
  							indexes[idx++] = Integer.valueOf(token).intValue();
  						}
  						if (idx != indexes.length) {
  							throw new RuntimeException(
  									"XBN FORMAT ERROR: A distribution has an incorrect "+
  									"number of indexes ("+idx+"). "+indexes.length+
  									" indexes were expected ("+dpi[j]+")."
  							);
  						}
  					}
            if (indexes != null) {
              ConditionalProbability cp = (ConditionalProbability)potential;
              cp.set(indexes, value);
						/*
              Format.printf("P(%s|%(%s,))=P(%s|%(%d,)) = [%(%.2f,)]\n", 
                p.add(Y).add(cp.getConditioningVariables().toArray())
                 .add(Y).add(indexes).add(value));
						*/
            } else {
              potential.set(value);
							/*
              Format.printf("P(%s) = [%(%.2f,)]\n", p.add(Y).add(value));
							*/
						}
					} // for(dpi...)
				} // if (dpi...)
			} // if (dpis...)
		}
	} // constructor with XBN_MODEL parameter.
 

	public Variable getVariable(String varname) {
    return (Variable)getVertex(varname);
	}

	public Potential getPotential(Cluster c) {
    return (Potential)c.data;
	}

	public Potential getPotential(Sepset c) {
    return (Potential)c.data;
	}


	/**
   Gets a formatted String describing the vertex.
   @return a formatted String describing the vertex.
	 */
	public String toString(Cluster c, int verboseLevel) {
    Parameters p = new Parameters();
    TextArea ta = new TextArea();
    int row=0;

		try {
  		switch (verboseLevel) {
  			case 0 : 
          return Format.sprintf("%s", new Parameters(c.name));
  			case 10: 
          row = ta.put(row,0,"cluster "+c.name+" {");
          row = ta.put(row,0,"  potential = %|"+c.data);
          row = ta.put(row,0,"}");
          ta = ta.copyMinimized();
          return ta.toString();
  			case 20: 
          Point lc = c.getPoint().getLocation();
          row = ta.put(row,0,"cluster "+c.name+" {");
          row = ta.put(row,0,"  potential = %|"+c.data);
          row = ta.put(row,0,"  (x,y)     = (%.3f,%.3f)",p.add(lc.x).add(lc.y));
          row = ta.put(row,0,"}");
          ta = ta.copyMinimized();
          return ta.toString();
  		}
 		} catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
		}
		return c.name;
  }

	/**
   Gets a formatted String describing the vertex.
   @return a formatted String describing the vertex.
	 */
	public String toString(Sepset S, int verboseLevel) {
    Parameters p = new Parameters();
    TextArea ta = new TextArea();
    int row=0;

		try {
  		switch (verboseLevel) {
  			case 0 : return Format.sprintf("%s", new Parameters(S.name));
  			case 10:
          row = ta.put(row,0,"sepset "+S.name+" {");
          row = ta.put(row,0,"  potential = %|"+getPotential(S));
          row = ta.put(row,0,"}");
          ta = ta.copyMinimized();
          return ta.toString();
  			case 99:
          row = ta.put(row,0,"sepset "+S.name+" {");
          row = ta.put(row,0,"  potential = %|"+getPotential(S));
          row = ta.put(row,0,"  (X,Y) = ("+S.X+","+S.Y+")");
          row = ta.put(row,0,"  mass = "+S.mass);
          row = ta.put(row,0,"  cost = "+S.cost);
          row = ta.put(row,0,"}");
          ta = ta.copyMinimized();
          return ta.toString();
  		}
 		} catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
		}
		return S.name;
  }

	public static void display(DoubleArray X, String label) {
    Parameters p = new Parameters();
    for(int i=0; i<X.size(); i++) {
      int[] ii = X.getIndex(i);
      Format.printf("%s(%(%d,)) = %.3f\n", 
        p.add(label).add(ii).add(X.value[i]));
  	}
    Format.printf("\n");
	}

 
	public void display(String var) {
    if (var.equals("*")) {
      Iterator it = iterator();
      while (it.hasNext()) {
        Variable v = (Variable)it.next();
        System.out.println(v.toString(10));
			}
		} else {
      Variable v = (Variable)getVertex(var);
      if (v == null)
        throw new RuntimeException("ERROR: Cannot display variable '"+var+
                               "' since it does not exist!");
      System.out.println(v.toString(10));
		}
	}

	public void printAll() {
    // Print the distribution of all variables in the DAG
    Iterator it = iterator();
    while (it.hasNext()) {
      Variable u = (Variable)it.next();
      Potential Pu = u.getProbability();
      Format.printf("P(%s)=(%(%s,))=%s, sum=%.3f\n", 
        p.add(u).add(u.state).add(Pu).add(Pu.sum()));
		}
	}

	public void addCondition(ConditionalProbability cp) {
    Variable u = cp.getDependentVariable();
    Collection cvars = cp.getConditioningVariables();
    Iterator it = cvars.iterator();
    while(it.hasNext()) {
      Variable v = (Variable)it.next();
      Edge e = new Edge(v,u, Edge.DIRECTED);
      addEdge(e);
		}
	}

	public void addConditions(ConditionalProbability[] cp) {
    for (int i=0; i<cp.length; i++)
      addCondition(cp[i]);
  }

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
} // class Cluster


/*
HISTORY

2000-07-07
* Added support for java.awt.Point-locations.
2000-05-02
* Added the unique mapping Z->V where Z is the integer set and V is the variable
  set. This to speed up the access of variables and to simplify the return type of
  simulations. The mapping is done in the Graph class; actually this mapping already
  exists since we use a List to handle the references to the variables.
2000-04-26
* Moved all parts related to the Junction Tree Algorithm into the 
  JunctionTreeInferenceEngine a'la Kevin Murphy @ cs.berkeley.edu.
2000-04-07
* Added getVariable, observe(String, ...).
990611
* Added constructor for XBN_BNMODEL-parameter.
990602
* Added use of Potentials and DoubleArray. It is the first time I am 
  really getting close to the "true" solution.
990409
* Created.
	
*/
