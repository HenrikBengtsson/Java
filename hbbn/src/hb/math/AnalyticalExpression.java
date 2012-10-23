package hb.math;

import java.io.StringWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;
import java.util.Iterator;
import hb.format.Format;
import hb.format.Parameters;
import hb.io.LaTeXWriter;
import hb.math.graph.Cluster;


public class AnalyticalExpression implements AnalyticalTerm {
  Stack stack = new Stack();

	public static void printStack(Stack stack) {
    StringBuffer s = new StringBuffer();
    Iterator it = stack.iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      if (obj instanceof AnalyticalTerm) {
        s.append(obj);
			} else if (obj instanceof AnalyticalOperator) {
        s.append(obj);
			} else {
        throw new RuntimeException("Unknown object on expression stack: "+obj);
			}
      s.append(" ");
		}
    System.out.println(s);
	}
		


	public void calculate() {
    Object obj;
    while ((obj = stack.pop()) != null) {
			if (obj instanceof AnalyticalOperator) {
        AnalyticalOperator op = (AnalyticalOperator)obj;
        op.process(stack);
			} else {
        stack.push(obj);
        break;
			}
		}
	}

	public String toAnalyticalString() {
    StringBuffer s = new StringBuffer();
    Iterator it = stack.iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      if (obj instanceof AnalyticalTerm) {
        s.append(obj);
			} else if (obj instanceof AnalyticalOperator) {
        s.append(obj);
			} else {
        throw new RuntimeException("Unknown object on expression stack: "+obj);
			}
      s.append(" ");
		}
    return s.toString();
	}

	public String toLaTeXString() {
    return toAnalyticalString();
	}

	public AnalyticalExpression getAnalyticalExpression() {
    return this;
	}

	public void push(AnalyticalTerm term) {
    stack.push(term);
	}

	public void push(AnalyticalOperator op) {
    stack.push(op);
	}

	public String toString() {
    return toAnalyticalString();
	}
} // class AnalyticalExpression


/*
HISTORY

2000-06-20
* Created!
	
*/
