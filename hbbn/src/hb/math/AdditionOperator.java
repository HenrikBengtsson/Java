package hb.math;

import java.util.Stack;

public class AdditionOperator extends AnalyticalOperator {
	static {
    instance = new AdditionOperator();
  }

	public void process(Stack stack) {
    Object obj = stack.pop();
  	if (obj instanceof AnalyticalOperator) {
      AnalyticalOperator op = (AnalyticalOperator)obj;
      op.process(stack);
      obj = stack.pop();
		}
    AnalyticalAdder term1 = (AnalyticalAdder)obj;

    obj = stack.pop();
  	if (obj instanceof AnalyticalOperator) {
      AnalyticalOperator op = (AnalyticalOperator)obj;
      op.process(stack);
      obj = stack.pop();
		}
    AnalyticalAdder term2 = (AnalyticalAdder)obj;

		//    System.out.print("adding "+term1+" and "+term2);

    AnalyticalTerm res = term1.add(term2);
    if (res != null) {
				//      System.out.println("result = "+res);
      stack.push(res);
    }
	}

	public String toString() {
    return "+";
	}
}


/*
HISTORY

2000-06-20
* Created!
	
*/
