package hb.math;

import hb.format.ArrayConversionParser;
import hb.format.Format;
import hb.format.Parameters;

/**
 Represents a multiway (multidimensional) array with any rank. The elements 
 are of type <tt>double</tt>. 
 You should use this class when you do not know the rank at compile time. 
 Internally, the elements are placed in an one-dimensional array in a well 
 defined order (according to mathematical operator called <i>the 
 vec-operator</i>). It is possible to access the elements directly through 
 this array, but it is recommended to use the methods available.
 */


public class DoubleArray {
  static int zero1 = 0, zero2 = 0;
  // The elements.
  public double[] value; 
  // shape[0] is equal to the size of dimension 1 and so on.
  public  int[] shape;                                
  // base = (1, shape[0], shape[0]*shape[1], ..., shape[0]*...*shape[rank-1])
  
  // The rank of the array is equal to shape.length.
  protected int rank;
  // The number of elements in the array is equal to base[rank].
  protected int[] base;

	/**
   Creates a empty multiway array, i.e. it contains no elements.
	 */
  public DoubleArray() {
    value = new double[0];
    shape = new int[0];
    rank = 0;
    base = new int[0];
  }

	/**
   Creates a multiway array with dimension given by the <tt>shape</tt>-array.
	 */
  public DoubleArray(int[] shape) {
    reshape(shape);
  }

  /**
   Reshape the multiway array. Note that all elements are cleared.
	 */
  public void reshape(int[] shape) {
    // Copy the object shape, since it otherwise can be change by the caller.
    rank = shape.length;
    this.shape = new int[rank];
    for(int i=0; i<rank; i++)
      this.shape[i] = shape[i];
    // Create the bases.
    base = new int[rank+1];
    base[0] = 1;
    for(int i=1; i<base.length; i++)
      base[i] = base[i-1]*shape[i-1];
    // Create the elements in the vec-representation.
    value = new double[base[rank]];
  }

	/**
   Clones the object.
	 */  
  public Object clone() {
    DoubleArray da = new DoubleArray(shape);
    System.arraycopy(value,0, da.value,0, base[rank]);
    return da;
  }
  
	public void assign(DoubleArray array) {
    System.arraycopy(array.value,0, value,0, value.length);
	}

	/**
   Gets the rank of the multiway array. 
   The rank is defined as the number of dimension.
	 */
  public int rank() {
    return rank;
  }
  
	/**
   Gets the shape of the multiway array. 
	 */
  public int[] shape() {
    return shape;
  }
  
	/**
   Gets the number of elements in the multiway array.
   */
  public int size() {
    return base[rank];
  }

	/**
   Gets the number of elements along a given dimension.
	 */
  public int size(int dimension) {
    return shape[dimension];
  }

  public int getIndex(int[] index) {
    if (index.length != rank) {
      throw new RuntimeException(
         "getIndex(int[] index): index has an invalid dimension.");
    }
    int vecIndex = 0;
    for (int i=0; i<rank; i++) {
      vecIndex += index[i]*base[i];  
    }
    return vecIndex;
  }
  
  public int[] getIndex(int index) {
    if (index < 0 || index > base[rank]) {
      throw new RuntimeException(
         "getIndex(int index): index is out of range.");
    }
    int[] maIndex = new int[rank];
    for (int i=0; i<rank; i++) {
      maIndex[i] = (int)((index % base[i+1])/base[i]);
    }
    return maIndex;
  }
  
  public int[] projectIndex(int[] index, int[] axis) {
    int[] res = new int[axis.length];
    for (int i=0; i<res.length; i++) {
      res[i] = index[axis[i]];
    }
    return res;
  }

  public int iteratorStep(int dimension) {
    return base[dimension];
  }

	/**
   Gets the element with the given index, as it is stored internally.
	 */
	public double get(int index) {
    return value[index];
	}

	/**
   Gets the element with the given index, where the index is zero-based.
	 */
	public double get(int[] index) {
    return value[getIndex(index)];
	}

	/**
   Assign a new value to the element with the given index, where the 
   index is zero-based.
	 */
  public double set(int[] index, double value) {
    int idx = getIndex(index);
    double oldValue = this.value[idx];
    this.value[idx] = value;
    return oldValue;
  }
  
	/**
   Assign new values directly to the internal vec-array. 
   Be aware of the order.
	 */
  public void set(double[] value) {
    System.arraycopy(value,0, this.value,0, this.value.length);
  }
  
	/**
   Assign new values taken from a given DoubleArray. 
	 */
  public void set(DoubleArray da) {
    System.arraycopy(da.value,0, value,0, value.length);
  }
  
	/**
   Assign a specific value to all elements in this multiway array.
	 */
	public void fill(double value) {
    for(int i=0; i<this.value.length; i++)
      this.value[i] = value;
	}

	/**
   Zeros all elements.
	 */
	public void clear() {
    fill(0.0);
	}

	/**
   Returns the sum (a scalar) of all elements in the multiway array.
	 */
  public double sum() {
    double sum = 0.0;
    for (int i=0; i<base[rank]; i++)
      sum += value[i];
    return sum;
  }
  
  /**
   Sum the elements along the given dimensions and puts the result in a 
   new DoubleArray with rank equal to current rank minus the number of 
   dimensions that summed over.
   */
  public DoubleArray sum(int[] dimension) {
    int[] newShape = new int[rank-dimension.length];
    int[] axis = new int[newShape.length];
    for (int i=0,j=0; i<newShape.length; i++,j++) {
      for (int k=0; k<dimension.length; k++)
        if (j == dimension[k])
          j++;
      newShape[i] = shape[j];
      axis[i] = j;
    }
    DoubleArray sum = new DoubleArray(newShape);
    for (int i=0; i<base[rank]; i++) {
      int[] idx = getIndex(i);
      int[] newIdx = projectIndex(idx, axis);
      int newi = sum.getIndex(newIdx);
      sum.value[newi] += value[i];
    }
    return sum;
  }
  
	/**
   Multiplies the two multiway arrays <i>X<i> and <i>Y</i> using the mapping of
   dimensions given by the two integer arrays <i>XZmap[]</i> and 
   <i>YZmap[]</i>. The result is returned in a multiway array <i>Z</i>, where
   dim(Z) = dim(X) union dim(Y).
   @return a DoubleArray.
	 */
	public static DoubleArray multiply(DoubleArray X, DoubleArray Y, 
                                                    int[] XZmap, int[] YZmap) {
    int Zrank = 0;
    for (int i=0; i<XZmap.length; i++) {
      if (XZmap[i] > Zrank)
        Zrank = XZmap[i];
    }
    for (int i=0; i<YZmap.length; i++) {
      if (YZmap[i] > Zrank)
        Zrank = YZmap[i];
    }
    int[] Zshape = new int[Zrank+1];
		//    Format.printf("Z.rank = %d\n", new Parameters(Zshape.length));
    for (int i=0; i<XZmap.length; i++)
      Zshape[XZmap[i]] = X.size(i);
    for (int i=0; i<YZmap.length; i++)
      Zshape[YZmap[i]] = Y.size(i);
		//    Format.printf("Z.shape[] = %(%dx)\n", new Parameters(Zshape));
    DoubleArray Z = new DoubleArray(Zshape);
    int[] Xidx = new int[X.rank()];
    int[] Yidx = new int[Y.rank()];
    for (int i=0; i<Z.value.length; i++) {
      int[] Zidx = Z.getIndex(i);
      for (int j=0; j<Xidx.length; j++)
        Xidx[j] = Zidx[XZmap[j]];
      for (int j=0; j<Yidx.length; j++)
        Yidx[j] = Zidx[YZmap[j]];
      Z.value[i] = X.get(Xidx)*Y.get(Yidx);
			//      Format.printf("Z(%(%d,)) = X(%(%d,))*Y(%(%d,)) = %.3f*%.3f = %.3f\n", new Parameters(Zidx).add(Xidx).add(Yidx).add(X.get(Xidx)).add(Y.get(Yidx)).add(Z.value[i]));
		}
    return Z;
	}

	public void multiplyWith(DoubleArray Y, int[] YXmap) {
    int[] Yidx = new int[Y.rank()];
    for (int i=0; i<value.length; i++) {
      int[] Xidx = getIndex(i);
      for (int j=0; j<Yidx.length; j++)
        Yidx[j] = Xidx[YXmap[j]];
      value[i] *= Y.get(Yidx);
		}
	}
		/*
	public void multiplyWith_OLDVERSION(DoubleArray Y, int[] YXmap) {
    int[] XXmap = new int[rank];
    for (int i=1; i<rank; i++)
      XXmap[i] = i;

    DoubleArray X = DoubleArray.multiply(this,Y, XXmap, YXmap);
    assign(X);
  }
		*/

	/**
   Multiplies the two multiway arrays <i>X<i> and <i>Y</i> elementwise, i.e.
   they have to have same shape.
   @return a DoubleArray.
	 */
	public static DoubleArray multiplyElementwise(DoubleArray X, DoubleArray Y) {
    DoubleArray Z = (DoubleArray)X.clone();
    for (int i=0; i<Z.value.length; i++)
      Z.value[i] *= Y.value[i];
    return Z;
	}

	/**
   Multiplies the two multiway arrays <i>X<i> and <i>Y</i> using the mapping of
   dimensions given by the two integer arrays <i>XZmap[]</i> and 
   <i>YZmap[]</i>. The result is returned in a multiway array <i>Z</i>, where
   dim(Z) = dim(X) and dim(X) >= dim(Y).
   @return a DoubleArray.
	 */
	public void divideWith(DoubleArray Y, int[] YXmap) {
    int[] Yidx = new int[Y.rank()];
    for (int i=0; i<value.length; i++) {
      int[] Xidx = getIndex(i);
      for (int j=0; j<Yidx.length; j++)
        Yidx[j] = Xidx[YXmap[j]];
      double Yvalue = Y.get(Yidx);
      if (Yvalue == 0.0) {
        value[i] = 0.0;
				//        Format.printf("*** O/O-DIVISION: TYPE 1 (#%d|#%d) ***\n", p.add(zero1).add(zero2));
        zero1++;
      } else if (Math.abs(Yvalue) < 1e-20) {
					//        Format.printf("*** O/O-DIVISION: TYPE 2 (#%d|#%d) ***\n", p.add(zero1).add(zero2));
        value[i] = 0.0;
        zero2++;
      } else
        value[i] /= Yvalue;
		}
	}

	/**
   Divide the two multiway arrays <i>X<i> and <i>Y</i> elementwise, i.e.
   they have to have same shape. The definition 0/0=0 is used.
   @return a DoubleArray.
	 */
	public static DoubleArray divideElementwise(DoubleArray X, DoubleArray Y) {
    DoubleArray Z = (DoubleArray)X.clone();
    for (int i=0; i<Z.value.length; i++) {
      double Yvalue = Y.value[i];
      if (Yvalue == 0.0) {
        Z.value[i] = 0.0;
        zero1++;
				//        Format.printf("*** O/O-DIVISION: TYPE 1 (#%d|#%d) ***\n", p.add(zero1).add(zero2));
      } else if (Math.abs(Yvalue) < 1e-20) {
        zero2++;
				//        Format.printf("*** O/O-DIVISION: TYPE 2 (#%d|#%d) ***\n", p.add(zero1).add(zero2));
        Z.value[i] = 0.0;
      } else
        Z.value[i] /= Yvalue;
		}
    return Z;
	}

  public String toString(String fmt) {
    Parameters p = new Parameters();
    StringBuffer fmtstr = new StringBuffer("(");
    for (int i=0; i<rank; i++) {
      fmtstr.append("%d");
      if (i < rank-1)
        fmtstr.append(',');
    }
    fmtstr.append(") = ").append(fmt).append('\n');
    //    System.out.println("fmtstr = "+fmtstr);
    StringBuffer res = new StringBuffer();
    for (int i=0; i<base[rank]; i++) {
      int[] maIndex = getIndex(i);
      for (int j=0; j<rank; j++)
        p.add(maIndex[j]);
      p.add(value[i]);
      res.append(Format.sprintf(fmtstr.toString(), p));
    }
    return res.toString();
  }

  public String toString() {
    return toString("%.3f");
  }

	public static void main(String args[]) {
    X = new DoubleArray(new int[] {3,2,3});
    Y = new DoubleArray(new int[] {3,3});

    for(int i=0; i<X.size(); i++) {
      int[] ii = X.getIndex(i);
      int   iX = X.getIndex(ii);
      X.value[i] = (double)i+1;
  	}

    for(int i=0; i<Y.size(); i++) {
      int[] ii = Y.getIndex(i);
      int   iY = Y.getIndex(ii);
      Y.value[i] = (double)i+1;
  	}

    Z = (DoubleArray)X.clone();
    Z.multiplyWith(Y, new int[] {2,0});
    for(int i=0; i<Z.size(); i++) {
      int[] ii = Z.getIndex(i);
      Format.printf("Z(%(%d,))) = %7.3f\n", p.add(ii).add(Z.value[i]));
  	}
    Z.divideWith(Y, new int[] {2,0});
    for(int i=0; i<Z.size(); i++) {
      int[] ii = Z.getIndex(i);
      Format.printf("Z(%(%d,))) = %7.3f\n", p.add(ii).add(Z.value[i]));
  	}
  }

	static {
    Format.out.addConversionParser(
       new ArrayConversionParser(Format.out.getConversionParsers()));
	}
  static Parameters p = new Parameters();
  static DoubleArray X,Y,Z;
}


/* HISTORY:
2000-06-29
* Added set(DoubleArray da).
2000-05-23
* Added get(int).
990602
* divideWith() is tested and seems to be OK.
990601
* Added multiply() and it seems to work.
990526
* Added some javadoc comments.
990524
* Created from design. See Master's thesis.

*/
