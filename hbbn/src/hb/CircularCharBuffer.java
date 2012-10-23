package hb;

import java.io.*;
import java.util.*;
import hb.format.*;
import hb.util.*;

public class CircularCharBuffer {
  Debugger debug = new Debugger(false); 
  char[] buffer;
  int readPos, writePos, size;
  
	public CircularCharBuffer() {
    this(8);
	}

	public CircularCharBuffer(int size) {
    buffer = new char[size];
    readPos = writePos = size = 0;
	}

	public char get(int index) {
    return buffer[index];
	}

	public void set(int index, char ch) {
    buffer[index] = ch;
	}

	public int length() {
    return buffer.length;
	}

	public int size() {
    return size;
	}

	public int free() {
    return buffer.length-size;
	}

	public boolean isEmpty() {
    return (size == 0);
	}

	public boolean isFull() {
    return (buffer.length-size == 0);
	}

	public boolean hasNext() {
    return (size > 0);
	}

	public char next() {
    return get();
	}

	public char get() {
    if (isEmpty()) return (char)-1;
    char ch = buffer[readPos];
    if (++readPos >= buffer.length) readPos=0;
    size--;
    return ch;
	}

	public void put(char ch) {
    if (isFull()) throw new BufferException("Buffer is full.");
    buffer[writePos] = ch;
    if (++writePos >= buffer.length) writePos=0;
    size++;
    debug.write("copying '%c to [%d]\n", new Parameters(ch).add(writePos));
	}

	public int put(char[] cbuf, int off, int len) {
    if (isFull()) throw new BufferException("Buffer is full.");
    int free = buffer.length-size;
    len = (len > free ? free : len);
    int left = buffer.length-writePos;
    int len1 = (len > left ? left : len);
    int len2 = len-len1;

    // Now copy the first chunck...
		debug.write("copying [%d..%d] to [%d..%d]\n", new Parameters(off).add(off+len1-1).add(writePos).add(writePos+len1-1));
    System.arraycopy(cbuf, off, buffer, writePos, len1);
    // And if necessary, the second chunk too...
    if (len2 > 0) {
  		debug.write("copying [%d..%d] to [%d..%d]\n", new Parameters(off+len1).add(off+len-1).add(0).add(len2-1));
      System.arraycopy(cbuf, off+len1, buffer, 0, len2);
      writePos = len2;
    } else {
      writePos += len;
		}
    size += len;
    return len;
	}

	public int get(char[] cbuf, int off, int len) {
    if (isEmpty()) return 0;
    len = (len > size ? size : len);
    int left = buffer.length-readPos;
    int len1 = (len > left ? left : len);
    int len2 = len-len1;

    // Now copy the first chunck...
		debug.write("copying [%d..%d] from [%d..%d]\n", new Parameters(off).add(off+len1-1).add(readPos).add(readPos+len1-1));
    System.arraycopy(buffer, readPos, cbuf, off, len1);
    // And if necessary, the second chunk too...
    if (len2 > 0) {
 		debug.write("copying [%d..%d] from [%d..%d]\n", new Parameters(off+len1).add(off+len-1).add(0).add(len2-1));
      System.arraycopy(buffer, 0, cbuf, off+len1, len2);
      readPos = len2;
    } else {
      readPos += len;
		}
    size -= len;
    return len;
	}

	public static void main(String args[]) throws Exception {
    CircularCharBuffer bfr = new CircularCharBuffer();
    char[] msg = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'};
    char[] cbfr = new char[64];

    bfr.put(msg, 0, 5);

    int len = bfr.get(cbfr,0,cbfr.length);
    for (int i=0; i<len; i++)
      System.out.println(cbfr[i]);

    bfr.put(msg, 0, 5);

    len = bfr.get(cbfr,0,cbfr.length);
    for (int i=0; i<len; i++)
      System.out.println(cbfr[i]);
	}
} // class terminal


/* HISTORY:

2000-05-26
* Created!

*/
