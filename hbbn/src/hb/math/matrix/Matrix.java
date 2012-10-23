package hb.math.matrix;

import hb.format.*;
import hb.lang.*;

import java.io.*;
import java.util.Vector;

/**
 Matrix is a light-weight class for handling matrices. Internally it stores a
 double[][] array on which all operations are performed.
 */
public class Matrix {
  public double[][] value;
  int n, m;

	/**
   Dimensions.
	 */
  public static final int COLUMNWISE=1;
  public static final int ROWWISE=2;

	/**
   Creates a matrix of the given two-dimensional array. Note that the values
   are not copied.
	 */
	public Matrix(double[][] value) {
    this.value = value;
    n = value.length;
    m = (n == 0) ? 0 : value[0].length;
	}

	/**
   Creates a matrix of the given one-dimensional array. Note that the values
   are not copied.
	 */
	public Matrix(double[] value) {
    this.value = new double[1][];
    this.value[0] = value;
    n = 1;
    m = value.length;
	}

	/**
   Creates a unique matrix of the given two-dimensional array. Since the input
   is of type int[][] the elements are copied.
	 */
	public Matrix(int[][] value) {
    n = value.length;
    m = (n == 0) ? 0 : value[0].length;
    this.value = new double[n][m];
    for (int i=0; i<n; i++)
      for (int j=0; j<m; j++)
        this.value[i][j] = (int)value[i][j];
	}

	/**
   Creates a unique copy of a matrix.
	*/
	public Matrix(Matrix A) {
    Matrix clone = (Matrix)A.clone();
    value = clone.value;
    n = clone.n;
    m = clone.m;
	}

	/**
   Creates a new matrix with n rows and m columns.
 	*/
  public Matrix(int n, int m) {
    value = new double[n][m];
		this.n = n; this.m = m;
  }

  /**
   Gets the number of rows in the matrix.
   @return the number of rows.
  */
  public int getRows() {
   return n;
  }

  
  /**
   Gets the number of columns in the matrix.
   @return the number of columns.
  */
  public int getCols() {
   return m;
  }

	/**
   Gets the number of elements in the matrix.
   @return the number of elements.
	 */
	public int size() {
    return n*m;
	}

	/**
   Makes a unique clone of this matrix.
   @return a Matrix instance with unique, i.e. copied, elements.
	 */
	public Object clone() {
    Matrix clone = new Matrix(n,m);
    System.arraycopy(value,0, clone.value,0, n*m);
    return clone;
	}

	/**
   Assign the values in a matrix to this matrix. Note that it is required that
   the both matrices are of the same size.
	 */
	public void assign(double[][] value) {
    if (n*m != value.length*value[0].length)
      throw new RuntimeException("Matrix.assign(): Matrices are of diffent sizes.");
    System.arraycopy(this.value,0, value,0, n*m);
	}

	/**
   Assign the values in a matrix to this matrix. Note that it is required that
   the both matrices are of the same size.
	 */
	public void assign(Matrix A) {
    assign(A.value);
  }

  /**
   Calculates the sum of all elements in the matrix.
   @return the sum of all elements.
  */
  public double sum() {
    double sum = 0;
     for(int i=0; i<n; i++)
      for(int j=0; j<m; j++)
         sum +=  value[i][j];
    return sum;
  }  

  /**
   Calculates the sum of all elements column-wise or row-wise.
   @return a vector containing the sum of all elements along one dimension.
  */
  public double[] sum(int dimension) {
    double[] sum;
    if (dimension == ROWWISE) {
      sum = new double[n];
      for(int i=0; i<n; i++)
        for(int j=0; j<m; j++)
          sum[i] +=  value[i][j];
    } else if (dimension == COLUMNWISE) {
      sum = new double[m];
      for(int i=0; i<n; i++)
        for(int j=0; j<m; j++)
          sum[j] +=  value[i][j];
    } else {
      throw new RuntimeException("Matrix.sum(int): Unknown dimension ("
            +dimension+").");
		}
    return sum;
  }

// ========================== I/O methods ==============================
	public void write(Writer out, String format)
       throws hb.format.ParseException, java.io.IOException {
    FormatWriter fout = FormatWriter.conform(out);
    
    // 1. Create a format string for a row.
    StringBuffer fmtstr = new StringBuffer();
    for (int j=0; j<m; j++)
      fmtstr.append(format);
    fmtstr.append('\n');
    // 2. Pre-compile the format string.
    FormatString fmt = fout.compileFormatString(fmtstr.toString());

    // 3. Write the elements of the matrix.
    Parameters p = new Parameters();
    for (int i=0; i<n; i++) {
			for (int j=0; j<m; j++)
        p.add(value[i][j]);
      fout.write(fmt, p);
		}
 	} // write
  
	public void write(Writer out)
       throws hb.format.ParseException, java.io.IOException {
    write(out, "%e ");
 	} // write


// =========================== Constructors =============================
  /**
  * Constructs a new matrix from the string argument.
  * The string argument should have the following format:
  *
  *  1. the string must start with a '[' and end with a ']'.
  *  2. each row must be delimited by a semicolon (;) or a newline (CR or LF).
  *  3. in every row each value must be divided by a comman (,), a blank
  *     space ( ) or with a tab (TAB).
  *  4. each row must be of equal number of values.
  *
  *  Examples:
  *   
  *   1. [1,2,3; 4,5,6; 7,8,9]
  *   2. [1 2 3; 4 5 6; 7 8 9]
  *   
  */
  public Matrix(String s) {
   parseStringAndConstruct(s);
  }

  
  protected void parseStringAndConstruct(String s) {
    char TAB = (char) 9;
    char LF  = (char)10;
    char CR  = (char)13;

    // data will be assembled into these vectors
    // and then transferred into the array element[][].
    Vector rows = new Vector();
    Vector cols = new Vector();

    StringVariable cells = new StringVariable();
    try {
      Format.sscanf(s, "[%[^]]]", new Parameters(cells));
    } catch (Exception ex) {
    }
    s=cells.toString()+";";
    try {
      Format.printf("s=(%s)", new Parameters(s));
    } catch (Exception ex) {
    }
    
    int rowCounter = 0;
    int colCounter = 0;
    String valuestr = new String(); // will hold each element during parsing
    for (int pos=0; pos<s.length(); pos++) {
    /*  Delimiter syntax:
      columns separated by tabs, commas, or spaces
      rows separated by newline or semicolon
      short rows filled with zeros
    */
    char ch = s.charAt(pos);
    // debug: System.out.println(sChar + " " + (int) sChar + "\r\n");

    // check for a delimiter...
    if ( (ch==' ') || (ch==',') || (ch==TAB)|| (ch==';') || (ch==CR ) ||
        (ch==LF ) ) {
      // See if the string in sData represents a number...
      try {
       // append column element as string
       cols.addElement(new Double(valuestr));
      } 
      catch (java.lang.NumberFormatException e) {
       // non-numeric stuff...
       Double dummy = new Double(0);
      }

      valuestr = new String(); // wipe out contents of string

      if ( !cols.isEmpty() && ((ch==';') || (ch==CR) || (ch==LF)) ) {
       rows.addElement(cols); // append row (i.e., vector of column elements)
       rowCounter++;
       valuestr = new String();   // wipe out contents of string
       colCounter = cols.size();
       cols = new Vector();     // wipe out the column vector
       /* an interesting Java note: use new Vector() method to
          force the contents of this vector to be explicitly copied
          into the row vector. The removeAllElements method will not
          work in this situation (try it!).
       */
      }
    }

    // build up data...
    else {
      if ((Character.isDigit(ch)) || (ch=='.') || (ch=='-')) {
       // allow only digit and decimal point characters
       valuestr += ch; // append to string
      }
    }
    }

    // Create matrix

    if (rowCounter > 0) {
	  	n = rowCounter; m = ((Vector)rows.elementAt(0)).size();
      value = new double[n][m];

      for (int i=0; i<getRows(); i++) {
        Vector values = (Vector)rows.elementAt(i);
        for (int j=0; j<values.size(); j++) {
         value[i][j] = ((Double)values.elementAt(j)).doubleValue();
        }
      }
    } else
      value = new double[n=0][m=0];
  } // Matrix(String) 
  
// ========================== Misc. methods ==============================
  
  /**
   Check if the matrix contains a specific value.

   @return true if it contains the value, otherwise false.
  */
	public boolean contains(double d) {
    for (int j=n; --j>0;)
      for (int k=m; --k>0;)
        if (value[j][k] == d) return true;
    return false;
	}

  /**
  * Method comment: Returns a copy of a row in the matrix.
  */
  public Matrix getRow(int r) {
    Matrix res = new Matrix(1, m);
    for(int j=0; j < m; j++){
      res.value[0][j] = value[r][j];
    }
   
    return res;
  }

  
  /**
  * Method comment: Returns a copy of a column in the matrix.
  */
  public Matrix getCol(int c) {
    Matrix res = new Matrix(n, 1);
    for(int i=0; i < n; i++){
      res.value[i][0] = value[i][c];
    }
   
    return res;
  }

  
  /**
  * Method comment: Returns a copy of a subpart of the matrix.
  */
  public Matrix getSubMatrix(int a, int b) {
   Matrix res = new Matrix(getRows()-1,getRows()-1);
   int resi = 0;
    for(int i=0; i < getRows(); i++){
    if (i != a) {
      int resj = 0;
      for(int j=0; j < getCols(); j++){
       if (j != b) {
        res.value[resi][resj] = value[i][j];
         resj++;
       }
      }
      resi++;
    }
    }

   return res;
  }

  public Matrix getSubMatrix(int row, int col, int height, int width) {
   Matrix res = new Matrix(height, width);
    for(int i=0; i < res.getRows(); i++){
    for(int j=0; j < res.getCols(); j++){
      double tmp = value[i+row][j+m];
      res.value[i][j] = tmp;
    }
    }

   return res;
  }

  public double getMinValue() {
   double res = Double.MAX_VALUE;

   for(int i=0; i < getRows(); i++){
    for(int j=0; j < getCols(); j++){
      if (value[i][j] < res)
       res = value[i][j];
    }
    }

   return res;   
  }
  
  public double getMaxValue() {
   double res = Double.MIN_VALUE;

   for(int i=0; i < getRows(); i++){
    for(int j=0; j < getCols(); j++){
      if (value[i][j] > res)
       res = value[i][j];
    }
    }

   return res;   
  }


  /**
  * Method comment: Returns the sum of all values in matrix.
  */
  public double getSum() {
   double sum = 0;
   
    for(int i=0; i<getRows(); i++)
    for(int j=0; j<getCols(); j++)
      sum +=  value[i][j];

   return sum;
  }  
// ================= Actions performed on the matrix ====================
  
  /**
  * Method comment: Set new values of a row in the matrix.
  */
  public void setRow(int r, Matrix V) {
    for(int j=0; j < getCols(); j++){
      value[r][j] = V.value[0][j];
    }
  }


  /**
   * Method comment: Set new values of a column in the matrix.
  */
  public void setColumn(int c, Matrix V) {
    for(int i=0; i < getRows(); i++){
      value[i][c] = V.value[i][0];
    }
  }

  
  /**
  * Method comment: Set all values in the matrix according to the
  * value argument.
  */
  public void fill(double value) {
   for(int i=0; i<getRows(); i++)
    for(int j=0; j<getCols(); j++)
      this.value[i][j] = value;
  }

  
  /**
  * Method comment: Swap two rows in the matrix.
  */
  public void swapRows(int a, int b) {
    for(int j=0; j < getCols(); j++){
      double tmp  = value[a][j];
      value[a][j] = value[b][j];
      value[b][j] = tmp;
    }
  }

  
  /**
  * Method comment: Swap two columns in the matrix.
  */
  public void swapColumns(int a, int b) {
    for(int i=0; i < getRows(); i++){
      double tmp  = value[i][a];
      value[i][a] = value[i][b];
      value[i][b] = tmp;
    }
  }


  /**
  * Method comment: Add a (row-) matrix to a given row.
  */
  public void addRow(int n, Matrix V) {
    for(int j=0; j < getCols(); j++){
      value[n][j] += V.value[0][j];
    }
  }

  
  /**
  * Method comment: Substract a (row-) matrix to a given row.
  */
  public  void subRow(int n, Matrix V) {
    for(int j=0; j < getCols(); j++){
      value[n][j] -= V.value[0][j];
    }
  }

  
  /**
  * Method comment: Add a (column-) matrix to a given row.
  */
  public void addColumn(int m, Matrix V) {
    for(int i=0; i < getRows(); i++){
      value[i][m] += V.value[i][0];
    }
  }

  
  /**
  * Method comment: Substract a (column-) matrix to a given row.
  */
  public void subColumn(int m, Matrix V) {
    for(int i=0; i < getRows(); i++){
      value[i][m] -= V.value[i][0];
    }
  }

  
  /**
  * Method comment: Remove a row in matrix.
  */
  public void removeRow(int r) {
   Matrix res = new Matrix(getRows()-1, getCols());

   int iold = 0;
    for(int i=0; i<res.getRows(); i++){
    if (i != r) {
      for(int j=0; j<res.getCols(); j++) {
       double newvalue = value[iold][j];
       res.value[i][j] = newvalue;
      }
    } else
      iold++;
    
    iold++;
    }

   n = res.getRows();
   m = res.getCols();
   value = new double[n][m];
   assign(res);
  }

// ========================= toString methods ============================

  /**
  * Method comment: Return a string representation of the matrix.
  * The format is a single C-style format of float type that tells
  * the preferable width and precision of every value. The newline
  * argument desides if the rows will be divided by semicolons (;)
  * or by newline character ("\n").
  *
  * Example: format '%5.3f' newline = false
  *
  *   [1.000 2.000 3.000; 4.000 5.000 6.000; 7.000 8.000 9.000]
  *
  * Example: format '%5.3f' newline = true
  *
  *   [1.000 2.000 3.000
  *    4.000 5.000 6.000
  *   7.000 8.000 9.000]
  *
  */
  public String toString(String format, boolean newline) {
    FormatString fmt = Format.out.compileFormatString(format);

    String res = "[";
    for(int i=0; i<n; i++) {
      for(int j=0; j<m; j++) {
        try {
          res += Format.sprintf(fmt, new Parameters(value[i][j]));
        } catch (Exception ex) {
         return "ERROR @ Matrix.toString(String, boolean): "+ex;
        }
        if (j < getCols()-1)
        res += " ";
      }
      if (i < getRows()-1) {
        if (newline)
          res += "\n ";
        else
          res += "; ";
      }
    }
    res += "]";

    return res;
  }

 
  /**
  * Method comment: Returns the same string as toString(format, false) do.
  */
  public String toString(String format) {
   return toString(format, false);
  }
  

  /**
  * Method comment: Returns the same string as toString("%.3f", false) do. 
  */
  public String toString() {
   return toString(false);
  }

  
  /**
  * Method comment: Returns the same string as toString("%.3f", newline) do. 
  */
  public String toString(boolean newline) {
   return toString("%.3f", newline);
  }

  
// ============================ Operations ==============================
 
 protected int getNumberOfZerosInRow(int row) {
   int res = 0;
   for(int j=0; j < getCols(); j++) {
    if (value[row][j] == 0)
      res++;
   }

   return res;
  }
  
  protected int findRowWithMostZeros() {
   int res = 0, max = 0;
   for(int i=0; i < getRows(); i++) {
    int tmp = getNumberOfZerosInRow(i);
    if (tmp > max) {
      max = tmp;
      res = i;
    }
   }

   return res;
  }


  /**
  * Method comment: Returns determinant of the matrix.
  */
  public double determinant() {
   if (getRows() == 2) {
    return value[0][0]*value[1][1]-value[0][1]*value[1][0];
   }
   
   int row = findRowWithMostZeros();
   int sign = (int)Math.pow(-1,row);

   double sum = 0;
   for(int j=0; j < getCols(); j++) {
    // Don't do calculation if you don't need to ;) ...
    if (value[row][j] != 0) {
      Matrix sub = getSubMatrix(row,j);
      /*
      Format.printf("x = x + %d * det|%s|\n",
       new Parameters(sign*value[row][j]).add(sub.toString("%d")));
       */
      double tmp = (double)sign*value[row][j]*sub.determinant();
      /*
      Format.printf("x = x + %d * det|%s| = %d\n",
       new Parameters(sign*value[row][j]).add(sub.toString("%d")).add(tmp));
       */
      sum += tmp;
    }
    
    sign = -sign;
   }

   return sum;
  }
  
    
  /**
  * Method comment: Returns a copy of the tranposition of the matrix.
  */
  public Matrix transpose() {
    Matrix res = new Matrix(getCols(),getRows());
    for(int i=0; i < getRows(); i++){
      for(int j=0; j < getCols(); j++){
        res.value[j][i] = value[i][j];
      }
    }
   
    return res;
  }


  /**
  * Method comment: Round every value with precision. 
  */
  public void round(int prec) {
   for(int i=0; i<getRows(); i++) {
     for(int j=0; j<getCols(); j++) {
      long pow = (int)Math.pow(10,prec);
      long tmp = Math.round(pow*value[i][j]);
      value[i][j]= (double)tmp/pow;
    }
   }
  }


  /**
  * Method comment: Check if the matrix is invertable or not.
  */
  public boolean isInvertable() {
   return (determinant() != 0);
  }


  private boolean isRowsLinearDependent(Matrix A, Matrix B) {
    double a = A.value[0][0]/B.value[0][0];
    for(int k=1; k<A.getCols(); k++) {
     double b = A.value[0][k]/B.value[0][k];
     if (b != a)
       return false;
    }

    return true;
  }
  
  /**
  * Method comment: Return number of rows that are linear independent
  * in the matrix.
  */
  public int numberOfRowsLinearIndependent() {
   int res = 0;
   
   for(int i=0; i<getRows(); i++) {
    // For every row...
    Matrix rowi = getRow(i);

    int tmp=i+1;
    for(int j=i+1; j<getRows(); j++) {
      // ...check if it is linear independent to rest of the rows.
      Matrix rowj = getRow(j);
      if (!isRowsLinearDependent(rowi, rowj))
       tmp++;
    }
    if (tmp == getRows())
      res++;
    //     Format.printf("i=%2d, res = %d\n", new Parameters(i).add(res));
   }
   return res;
  }


  /**
   * Sort matrix in increasing rows using one key column.
  */
  public Matrix sortByColumn(int col) {
   Matrix res = new Matrix(this);

   for(int i=0; i<res.getRows(); i++) {
    for(int k=i+1; k<res.getRows(); k++) {
      if (res.value[i][col] > res.value[k][col]) {
       res.swapRows(i,k);
      }
    }
   }
   
   return res;
  }


  public Matrix abs() {
   Matrix res = new Matrix(this);
   for(int i=0; i<res.getRows(); i++) {
    for(int j=0; j<res.getCols(); j++) {
      res.value[i][j] = Math.abs(res.value[i][j]);
    }
   }

   return res;
  }
} // Matrix


/* HISTORY

2000-05-23
* Started to make Matrix light-weight.

Spring 1997
* Created during Cryptography course at UCSB.

*/
