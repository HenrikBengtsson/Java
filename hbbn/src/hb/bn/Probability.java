package hb.bn;

import hb.format.Format;
import hb.format.Parameters;



/**
 Describes the probability function <i>P(Y|X1,X2,...XN)</i> as a Potential.
 */
public class Probability extends Potential {

	public Probability(Variable Y) {
    vars = new Variable[] {Y};
    createDoubleArray();
    double mean = 1.0/Y.size();
    for (int i=0; i<value.length; i++)
      value[i] = mean;
    Y.setPotential(this);
	}

	public Probability(Variable Y, double[] value) {
    this(Y);
    set(value);
	}

	public String toString(String fmt) {
		/*
    StringWriter sout = new StringWriter();
    FormatWriter out = new FormatWriter(sout);
    out.addConversionParser(
		new ArrayConversionParser(out.getConversionParsers()));
    */
    return Format.sprintf("(%(%.3f,))", p.add(value));
	}

  static Parameters p = new Parameters();
} // class Probability


/*
HISTORY

990602
  Created with Potential as a template.
	
*/
