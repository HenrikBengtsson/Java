package hb.math;

import java.util.Stack;

public abstract class AnalyticalOperator {
  protected static AnalyticalOperator instance = null;

	public static AnalyticalOperator getInstance() {
    return instance;
	}

	public void process(Stack stack) {
	}

	abstract public String toString();
} // interface AnalyticalOperator;


/*
HISTORY

2000-06-20
* Created!
	
*/
