package hb.bn;


public class State {
  public String name, 
                label;
  
	public State(String name) {
    this(name, name);
	}

	public State(String name, String label) {
    this.name = name;
    this.label = label;
  }

	public boolean equals(Object o) {
    if (o == null)
      return false;
    State s = (State)o;
    return (label.equals(s.label) || name.equals(s.name));
	}

	public String toString() {
    return label;
  }
} // class State



/*
HISTORY

990406
  Created.
	
*/
