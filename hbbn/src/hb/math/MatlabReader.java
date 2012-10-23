package hb.math;

import hb.format.Format;
import hb.format.Parameters;
import hb.io.BasicFormatReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class MatlabReader extends BasicFormatReader {
	public MatlabReader(Reader in) {
    super(in);
  }
   
	public double[][] readMatrix() throws IOException {
   // Try to figure out the number of columns...
   readWhitespace();
   mark();
   int nbrOfColumns = 0;
   String wspc = null;
   do { 
     readNonWhitespace();
     nbrOfColumns++;
     wspc = readWhitespace();
   } while(wspc.indexOf('\n') == -1);
   reset();

   Format.printf("Found %d columns\n", new Parameters(nbrOfColumns));

   List list = new ArrayList();
   boolean ready = false;
   while(!ready) {
     double[] value = new double[nbrOfColumns];
     for (int i=0; i<nbrOfColumns; i++) {
			 try {
         value[i] = readNextScientific();
				 //  	       Format.printf("Read %f\n", new Parameters(value[i]));
       } catch(hb.format.ParseException ex) {
				 if (isAtEndOfFile()) {
           ready = true;
           break;
         }
         throw ex;
			 }
		 }
     if (!ready)
       list.add(value);
   }
   int size = list.size();

   Format.printf("Read %d lines\n", new Parameters(size));

   double[][] value = new double[size][nbrOfColumns];
   int pos = 0;
   Iterator it = list.iterator();
   while (it.hasNext()) {
     value[pos++] = (double[])it.next();
	 }
   return value;
	}

	public static void main(String args[]) throws Exception {
		FileReader in0 = new FileReader(args[0]);
    MatlabReader in = new MatlabReader(in0);
    double[][] value = in.readMatrix();
    for(int i=0; i<value.length; i++) {
      for(int j=0; j<value[i].length; j++) {
        Format.printf("%4d ", new Parameters(value[i][j]));
			}
      Format.printf("\n");
		}
	}
} // class MatlabReader


/* HISTORY

2000-05-02
* Created!

 */
