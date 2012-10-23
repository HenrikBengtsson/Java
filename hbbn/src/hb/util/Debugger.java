package hb.util;

import hb.format.FormatOutputStream;
import hb.format.Format;
import hb.format.FormatString;
import hb.format.Parameters;
import java.util.Stack;

public class Debugger {
  private static Parameters p = new Parameters();
  private static Runtime rt = Runtime.getRuntime();
  private long start = 0L, stop = 0L;
  private boolean isOn = true;
  private Stack stack = new Stack();
  private FormatOutputStream out = new FormatOutputStream(System.err);
  private int indentLevel = 0;
  private int indentStep = 2;

	public Debugger() {
    this(true);
	}

	public Debugger(boolean on) {
    set(on);
	}

	public void set(boolean on) {
    isOn = on;
  }

	public void on() {
    set(true);
	}

	public void off() {
    set(false);
	}

	public boolean isOn() {
    return isOn;
	}

	public synchronized void write(String fmtstr, Parameters p) {
    write(Format.sprintf(fmtstr, p));
  }

	public synchronized void write(FormatString fmtstr, Parameters p) {
    write(Format.sprintf(fmtstr, p));
  }

	public synchronized void write(String msg) {
		if (isOn) {
	  	try {
        out.write("%*c%s\n", p.add(indentLevel*indentStep).add('\0').add(msg));
      } catch (hb.format.ParseException ex) {
				// Should never occur, but...
        System.err.println(ex);
        System.exit(99);
      } catch (java.io.IOException ex2) {
				// Might occur,...
        System.err.println(ex2);
        System.exit(99);
			}
    }
  }

	public void push(String fmtstr, Parameters p) {
    push(Format.sprintf(fmtstr, p));
  }

	public void push(String msg) {
		if (isOn) {
      stack.push(msg);
      write(msg);
      indentLevel++;
		}
	}

	public void pop() {
		if (isOn) {
      String msg = (String)stack.pop();
      if (msg == null)
        write("                     *** Debugger.pop(): pop() without push().");
      else {
        indentLevel--;
        write(msg+" finished!");
      }
		}
	}

 	public void startTimer() {
    stop = 0L;
    start = System.currentTimeMillis();
    write("Timer started at %.2fs", p.add(start/1e3));
	}

	public void stopTimer() {
    long ms = System.currentTimeMillis();
    if (start == 0L)
      throw new RuntimeException("Timer.stopTimer(): The timer was never started!");
    stop = ms;
    write("Timer stopped at %.2fs after %.2fs", p.add(stop/1e3).add((stop-start)/1e3));
  }

	public long getTimer() {
    long length;
    if (stop > start)
      length = stop-start;
    else {
      length = System.currentTimeMillis()-start;
      if (start == 0L)
        throw new RuntimeException("Timer.stopTimer(): The timer was never started!");
    }
    return length;
	}

	public void printTimer() {
    Format.printf("Timer: %.2fs\n", p.add((getTimer())/1000.0));
	}

	public long getFreeMemory() {
    return rt.freeMemory();
	}

	public void printMemory() {
    Format.printf("Free memory: %d kb\n", p.add(getFreeMemory()/1e3));
	}
} // class Debugger


/* 
HISTORY:

2000-05-23
* Created from SibPairSimulation.
	
*/
