package hb.bn;

import java.io.StringWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;
import hb.format.Format;
import hb.format.Parameters;
import hb.io.LaTeXWriter;
import hb.math.graph.Cluster;

//import hb.math.AnalyticalTerm;
//import hb.math.AnalyticalExpression;

/**
 Describes the probability function <i>P(Y|X1,X2,...XN)</i> as a Potential.
 */
public class ConditionalProbability extends Potential {
	public ConditionalProbability(Variable Y, Variable[] X) {
    int len = X.length+1;
    vars = new Variable[len];
    vars[0] = Y;
    System.arraycopy(X,0, vars,1, len-1);
    createDoubleArray();
    double mean = 1.0/Y.size();
    for (int i=0; i<value.length; i++)
      value[i] = mean;
    Y.setPotential(this);
	}

	public ConditionalProbability(Variable Y, Cluster Xs) {
    int len = Xs.size()+1;
    vars = new Variable[len];
    vars[0] = Y;
    System.arraycopy(Xs.toArray(),0, vars,0, len-1);
    createDoubleArray();
    Y.setPotential(this);
	}

	public ConditionalProbability(Variable Y, Variable[] X, double[] value) {
    this(Y,X);
    set(value);
	}

	public ConditionalProbability(Variable Y, Cluster Xs, double[] value) {
    this(Y,Xs);
    set(value);
	}

	public Variable getDependentVariable() {
    return vars[0];
	}

	public Collection getConditioningVariables() {
    Variable[] cvars = new Variable[rank-1];
    System.arraycopy(vars,1, cvars,0, rank-1);
    return Arrays.asList(cvars);
	}

	/**
   Assign a distribution <i>P(Y|<b>X=x</b>) = (y[0], y[1], ... y[N])</i>.
	 */
  public void set(int[] index, double[] value) {
    int[] index2 = new int[index.length+1];
    System.arraycopy(index,0, index2,1, index.length);
    int idx = getIndex(index2);
    for (int k=0; k<value.length; k++)
      this.value[idx++] = value[k];
  }
  
	public String toString(String fmt) {
    Parameters p = new Parameters();
    StringBuffer fmtstr = new StringBuffer("(%s|");
    for (int i=1; i<rank; i++) {
      fmtstr.append("%s");
      if (i < rank-1)
        fmtstr.append(',');
    }
    fmtstr.append(") = ").append(fmt).append('\n');
		//    System.out.println("fmtstr = "+fmtstr);
    StringBuffer res = new StringBuffer("potential P(")
                            .append(vars[0]).append('|');
    for (int i=1; i<rank; i++)
      res.append(vars[i]);
    res.append(")\n");
    for (int i=0; i<base[rank]; i++) {
      int[] maIndex = getIndex(i);
      for (int j=0; j<rank; j++) {
        int state = maIndex[j];
        p.add(vars[j].getStateName(state));
      }
      p.add(value[i]);
      res.append(Format.sprintf(fmtstr.toString(), p));
    }
    return res.toString();
	}

	public String toAnalyticalString() {
    StringBuffer s = new StringBuffer("P(");
    s.append(vars[0]);
    if (rank > 1) {
      s.append("|");
      for (int i=1; i<rank; i++) {
        s.append(vars[i]);
        if (i < rank-1)
        s.append(",");
			}
		}
    s.append(")");
    return s.toString();
	}

	public String toLaTeXString() {
		try {
  		StringWriter s = new StringWriter();
  		LaTeXWriter out = new LaTeXWriter(s);
  		Parameters p = new Parameters();
  		out.write("P(%LaTeX", p.add(vars[0]));
  		if (rank > 1) {
  			out.write("|");
  			for (int i=1; i<rank; i++) {
  				out.write("%LaTeX", p.add(vars[i]));
  				if (i < rank-1)
  				out.write(",");
  			}
  		}
  		out.write(")");
  		return s.toString();
		} catch (IOException ex) {
      ex.printStackTrace();
      System.exit(10);
      return null;
		}
	}

//	public AnalyticalExpression getAnalyticalExpression() {
//		AnalyticalExpression expr = new AnalyticalExpression();
//		Potential nominator = new Potential();
//		Potential denominator = new Potential();
//		expr.push((AnalyticalTerm)nominator);
//		expr.push((AnalyticalTerm)denominator);
//		expr.push((AnalyticalTerm)DivisionOperator.getInstance());
//		return expr;
//	}
} // class ConditionalProbability


/*
HISTORY

990609
* Added set(int[] index, double[] value)
990602
  Created with Potential as a template.
	
*/
