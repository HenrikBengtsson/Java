package hb.math;

public class AnalyticalConstant implements AnalyticalTerm, AnalyticalAdder {
  Number value;
  AnalyticalExpression expr;
 
	public AnalyticalConstant(Number value) {
    this.value = value;
    expr = new AnalyticalExpression();
    expr.push(this);
	}

	public AnalyticalConstant add(AnalyticalConstant other) {
		if ((value instanceof Integer || value instanceof Long) &&
		    (other.value instanceof Integer || other.value instanceof Long)) {
      long a = value.longValue();
      long b = other.value.longValue();
      long c = a+b;
      return new AnalyticalConstant(new Long(c));
		} else {
      double a = value.doubleValue();
      double b = other.value.doubleValue();
      double c = a+b;
      return new AnalyticalConstant(new Double(c));
		}
	}

	public int intValue() {
    return value.intValue();
	}

	public double doubleValue() {
    return value.doubleValue();
	}

	public AnalyticalTerm add(AnalyticalAdder other) {
		if (other instanceof AnalyticalConstant)
      return add((AnalyticalConstant)other);
    return null;
	}

	public String toAnalyticalString() {
    return value.toString();
	}

	public String toLaTeXString() {
    return value.toString();
	}

	public String toString() {
    return toAnalyticalString();
	}

	public AnalyticalExpression getAnalyticalExpression() {
    return expr;
	}

	public static void main(String[] args) throws Exception {
    AnalyticalConstant[] c = new AnalyticalConstant[10];
    for (int k=0; k<c.length; k++)
      c[k] = new AnalyticalConstant(new Integer(k));

    AnalyticalExpression expr = new AnalyticalExpression();
    for (int k=0; k<c.length; k++) {
      expr.push(c[k]);
      if (k > 0)
        expr.push(new AdditionOperator());
    }

    System.out.println(expr);

    expr.calculate();

    System.out.println(expr);
	}
} // interface AnalyticalConstant


/* HISTORY:

2000-06-20
  Created.
	
*/
