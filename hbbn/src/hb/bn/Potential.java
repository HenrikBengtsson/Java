package hb.bn;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.TreeSet;
import hb.format.Format;
import hb.format.ConversionParser;
import hb.format.ArrayConversionParser;
import hb.format.Parameters;
import hb.math.DoubleArray;

public class Potential extends DoubleArray  {
	protected static Random random = new Random();
  protected Variable[] vars;
  protected DoubleArray store = null;

	public Potential() {
	}

	public Potential(Variable[] vars) {
    this.vars = new Variable[vars.length];
    System.arraycopy(vars,0, this.vars,0, vars.length);
    createDoubleArray();
	}

	public Potential(Collection c) {
    vars = new Variable[c.size()];
    System.arraycopy(c.toArray(),0, vars,0, vars.length);
    createDoubleArray();
	}

	public Object clone() {
    Potential p = new Potential(vars);
    System.arraycopy(value,0, p.value,0, base[rank]);
    return p;
	}

	public void store() {
    if (store == null)
      store = (DoubleArray)super.clone();
    else
      store.set(this);
	}

	public void compareStore() {
    if (store == null)
      return;
    for (int i=0; i<base[rank]; i++) {
      if (store.value[i] != value[i])
        Format.printf("Store differs in position %d\n", new Parameters(i));
		}
	}

	public void restore() {
    if (store == null)
      return;
    assign(store);
	}

	protected void createDoubleArray() {
    int[] shape = new int[vars.length];
    for (int i=0; i<vars.length; i++)
      shape[i] = vars[i].size();
    reshape(shape);
    initialize();
	}

	public int indexOf(Variable v) {
   for (int i=0; i<vars.length; i++) {
     if (v == vars[i])
       return i;
		}
	  return -1;
	}

	public Variable getVariable(int dimension) {
    return vars[dimension];
	}

	/**
   Gets the dimension index of a variable. If there is no such variable
   -1 is returned.
	 */
	public int getDimension(Variable var) {
    for (int i=0; i<vars.length; i++)
			if (vars[i] == var)
        return i;
    return -1;
	}

	/**
   Set all elements equal to one.
	 */
	public void initialize() {
    fill(1.0);
	}

	public int[] getMap(Potential Y) {
    int[] YXmap = new int[Y.rank()];
    for (int i=0; i<YXmap.length; i++) {
      Variable u = Y.getVariable(i);
      YXmap[i] = getDimension(u);
    }
    return YXmap;
	}

	/**
   Gets the a map of all the variables that are common to this potential and the given one.
	 */
	public int[] getMapInOther(Potential Y) {
    //               A C B
    // (x1,x2,x3) = (2,1,4)
    //
    // MAP: (m1,m2) = (3,2)
    //
    //               B C
    // (y1,y2)    = (4,1)
    int[] map = new int[rank];
    int dim = 0;

    int Yrank = Y.rank();
    boolean isInY = false;
    for (int i=0; i<rank; i++) {
      Variable u = getVariable(i);
      map[i] = Y.indexOf(u);
		}
    return map;
	}

	/**
   Gets the a map of all the variables that this potential belongs to except those variables
   found in the given potential.
   Note: It is required that this potential to have a higher rank than the passed potential.
	 */
	public int[] getComplementMap(Potential Y) {
    // get the compliment of the map.
    int Yrank = Y.rank();
    if (rank <= Yrank)
      throw new RuntimeException("ERROR: X.getComplementMap(Y): X.rank < Y.rank");
    int[] CYXmap = new int[rank-Yrank];
    int dim = 0;
    for (int i=0; i<rank; i++) {
      Variable u = getVariable(i);
      // Check if this variable is "in" Y...
      boolean isInY = false;
      for (int j=0; j<Yrank; j++) {
        isInY = (u == Y.getVariable(j));
        if (isInY) break;
			}
      if (!isInY)
        CYXmap[dim++] = i;
		}
    return CYXmap;
	}

	public int[] getComplementMap(Variable v) {
    // get the compliment of the map.
    int[] CYXmap = new int[rank-1];
    int dim = 0;
    for (int i=0; i<rank; i++) {
      Variable u = getVariable(i);
      if (v != u)
        CYXmap[dim++] = i;
		}
    return CYXmap;
	}

	public static Potential multiply(Potential X, Potential Y) {
    Collection vars = new TreeSet(Arrays.asList(X.vars));
    vars.addAll(Arrays.asList(Y.vars));
    Potential Z = new Potential(vars);
    Z.multiplyWith(X);
    Z.multiplyWith(Y);
    return Z;
  }

	public void multiplyWith(Potential Y) {
    int[] YXmap = getMap(Y);
    multiplyWith(Y, YXmap);
  }

	public void divideWith(Potential Y) {
    int[] YXmap = getMap(Y);
    divideWith(Y, YXmap);
  }

	public void marginalize(Variable u) {
    int[] CRXmap = getComplementMap(u);
    u.dist.project(this);
    u.distValid = true;
	}

	public void normalize() {
    double sum = sum();
    for (int i=0; i<value.length; i++)
      value[i] /= sum;
	}

	public void project(Potential X) {
    int[] CRXmap = X.getComplementMap(this);
		//    Format.printf("CRXmap = (%(%d,))\n", p.add(CRXmap));
    assign(X.sum(CRXmap));
		//    normalize();  // Not needed!
    normalize();  // NEEDED!
  }

	public void absorb(Potential R, Potential Rold) {
    int[] RYmap = getMap(R);
    DoubleArray Rquota = DoubleArray.divideElementwise(R, Rold);
		//    System.out.println(Rquota);
    multiplyWith(Rquota, RYmap);
    normalize();  // NEEDED!
	}

	/**
   Draws one value from the potential, which is required to be normalized first.
   @ return the one-dimensial index of the drawn value.
  */
	public int drawIndex() {
    double rnd = random.nextDouble();
    int idx = 0;
		double cumsum[] = new double[value.length];
    cumsum[0] = value[0];
    if (value[0] < rnd) {
      for (int i=1; i<cumsum.length; i++) {
        cumsum[i] = cumsum[i-1]+value[i];
        if (cumsum[i] > rnd) {
          idx = i;
          break;
        }
      }
    }
		//    Format.printf("drawn index=%i : rnd=%.3f, cumsum[index]=%.3f\n", 
		//        new Parameters(idx).add(rnd).add(cumsum[idx]));
    return idx;
	}

	public int[] draw() {
    int idx = drawIndex();
    return getIndex(idx);
	}

	public int[] drawAndObserve() {
    int idx = drawIndex();
    observe(idx);
    return getIndex(idx);
	}

	public void observe(int index) {
    clear(); value[index] = 1.0;
	}

	public void observe(int index[]) {
    int idx = getIndex(index);
    clear(); value[idx] = 1.0;
	}

	public String toString(String fmt) {
    Parameters p = new Parameters();
    StringBuffer fmtstr = new StringBuffer("(");
    for (int i=0; i<rank; i++) {
      fmtstr.append("%s");
      if (i < rank-1)
        fmtstr.append(',');
    }
    fmtstr.append(") = ").append(fmt).append('\n');
		//    System.out.println("fmtstr = "+fmtstr);
    StringBuffer res = new StringBuffer("potential ");
    for (int i=0; i<rank; i++)
      res.append(vars[i]);
    res.append("\n");
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

	public static void main(String args[]) {
		String[] s01 = {"0", "1"};
		String[] s012 = {"0", "1", "2"};
    Variable a = new Variable("A", s012);
    Variable b = new Variable("B", s01);
    Variable c = new Variable("C", s012);
 
    pX = new Potential(new Variable[] {a,b,c});
    for (int i=0; i<pX.size(); i++)
      pX.value[i] = i+1.0;
    pY = new Potential(new Variable[] {a,c});
    for (int i=0; i<pY.size(); i++)
      pY.value[i] = i+1.0;
    System.out.println(pX);
    System.out.println(pY);
    pX.divideWith(pY);
    System.out.println(pX);
    pX.multiplyWith(pY);
    System.out.println(pX);
	}

	static {
    ConversionParser[] cp = Format.out.getConversionParsers();
    Format.out.addConversionParser(new ArrayConversionParser(cp));
	}
  static Parameters p = new Parameters();
  static Potential pX,pY,pZ;
} // class Potential


/*
HISTORY

2000-04-26
* Added drawAndObserve().

990611
* After almost two days I found out that one have to normalize after an
  absorption/project is performed. See "Implementation Aspects of Various 
  Propagation Algorithms in Hugin", Frank Jensen, 1994, Dept. of 
  Mathematics and Computer Science, AAlborg, Denmark..
990602
* multiplyWith() and divideWith() works.
* Created.
	
*/
