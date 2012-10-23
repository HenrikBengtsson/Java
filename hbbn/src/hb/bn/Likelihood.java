package hb.bn;


import hb.format.Format;
import hb.format.Parameters;


/**
 Describes the likelihood function <i>Lambda(X)</i> of a variable.
 */
public class Likelihood extends Potential {

	public Likelihood(Variable Y) {
    vars = new Variable[] {Y};
    createDoubleArray();
    fill(1.0);
	}

	public Likelihood(Variable Y, double[] value) {
    this(Y);
    set(value);
	}

	public void clear() {
    fill(1.0);
	}

	public boolean equals(Likelihood l) {
    if (size() != l.size())
      return false;
    for (int i=0; i<value.length; i++) {
      if (Math.abs(value[i]-l.value[i]) > 1e-8)
        return false;
		}
    return true; 
	}

	public double get(int state) {
    return this.value[state];
	}

	public double set(int state, double value) {
    double oldValue = this.value[state];
    this.value[state] = value;
    return oldValue;
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
} // class Likelihood


/*
HISTORY

990614
  Created from Probility class.
	
*/
