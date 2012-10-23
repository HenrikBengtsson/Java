package hb;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import hb.format.*;


class TextAreaReader extends Reader implements KeyListener {
  PipedWriter pipeWriter;
  PipedReader pipeReader;
  char[] transfer = new char[256];
  TextArea ta;
  int beforePressPos, afterPressPos;

	public TextAreaReader() throws IOException  {
    this(null);
	}

	public TextAreaReader(TextArea ta) throws IOException {
    pipeWriter = new PipedWriter();
    pipeReader = new PipedReader(pipeWriter);
    connect(ta);
	}
 
	public void connect(TextArea ta) {
    this.ta = ta;
    if (ta != null)
      ta.addKeyListener(this);
	}

  public void finalize() throws Throwable {
    close();
	}

	public void keyPressed(KeyEvent e) {
    beforePressPos = ta.getCaretPosition();
	}

	public void keyReleased(KeyEvent e) {
    afterPressPos = ta.getCaretPosition();
		try {
      int key = e.getKeyCode();
      char ch = e.getKeyChar();
      Format.err.write("KeyEvent: code=%#0x, char='%c' (%d)\n", new Parameters(key).add(ch).add(ch));
      if (ch == e.CHAR_UNDEFINED) {
				if (key == e.VK_LEFT || key == e.VK_RIGHT || key == e.VK_END) {
					// Do nothing!
        } else if (key == e.VK_HOME || key == e.VK_TAB) {
					// Send to terminal!
				} else {
					// "Undo!"
          System.err.println("<non-printable key>");
          ta.setCaretPosition(beforePressPos);
        }
			}
    } catch (IOException ex) {
      System.err.println(ex);
		}
	}

	public void keyTyped(KeyEvent e) {
		try {
      char ch = e.getKeyChar();
      pipeWriter.write(ch);
    } catch (IOException ex) {
      System.err.println(ex);
		}
	}

	public void close() {
	}

  public int read() throws IOException {
    if (!ready()) return -1;
    return super.read();
	}

	public int read(char[] cbuf, int off, int len) throws IOException { 
    len = pipeReader.read(cbuf, off, len);
    return len;
	}

	public boolean ready() throws IOException {
    return pipeReader.ready();
	}
}

class TextAreaWriter extends Writer {
  PipedWriter pipeWriter;
  PipedReader pipeReader;
  char[] transfer = new char[256];
  TextArea ta;

	public TextAreaWriter() throws IOException {
    this(null);
  }

	public TextAreaWriter(TextArea ta) throws IOException {
    connect(ta);
    pipeWriter = new PipedWriter();
    pipeReader = new PipedReader(pipeWriter);
	}
 
	public void connect(TextArea ta) {
    this.ta = ta;
	}

  public void finalize() throws Throwable {
    close();
	}

	public void close() {
	}

	public void flush() throws IOException {
	}

	public void write(char[] cbuf, int off, int len) throws IOException { 
    pipeWriter.write(cbuf, off, len);
    pipeWriter.flush();
    len = pipeReader.read(transfer, 0, transfer.length);
    String s = new String(transfer, 0, len);
    ta.append(s);
	}
}


public class terminal {
  public List commands = new ArrayList();
  String prompt = "hbbn>";
  TextAreaReader in;
  TextAreaWriter out;
  Frame frame;
  boolean echo = false;
  TextArea ta;
  int lineBegin;

	public terminal() throws IOException {
    in = new TextAreaReader();
    out = new TextAreaWriter();
    commands.add("echo");
    commands.add("exit");
    commands.add("hbbn");
    commands.add("hello");
    openGUI();
	}

	public void openGUI() {
    frame = new Frame("hbbn terminal");
    ta = new TextArea(20,80);
    in.connect(ta);
    out.connect(ta);
    frame.add(ta);
    frame.pack();
    frame.show();
	}
  
  public String getPrompt() {
    return prompt;
  }

  public void promptUser() throws IOException {
    ta.setCaretPosition(Integer.MAX_VALUE);
    write(getPrompt());
    lineBegin = ta.getCaretPosition();
  }

	public List matchCommands(String startsWith) {
    List matches = new ArrayList();
    Iterator it = commands.iterator();
    while(it.hasNext()) {
      String command = (String)it.next();
      if (command.startsWith(startsWith))
        matches.add(command);
		}
    return matches;
	}

	public void beep() {
    System.err.println('\u0007');
	}

	public StringBuffer handleTab() {
    StringBuffer add = new StringBuffer();
    String s = getLine();
    List matches = matchCommands(s);
    int nbrOfMatches = matches.size();
    if (nbrOfMatches == 1) {
      String command = (String)matches.get(0);
      String rest = command.substring(s.length());
      add.append(rest).append(' ');
  	} else if (nbrOfMatches > 1) {
      beep();
  	} else if (nbrOfMatches == 0) {
      add.append(' ');
		}

    String replacer = add.toString();
    int pos = ta.getCaretPosition()-1;
    ta.replaceRange(replacer, pos, pos+1);//replacer.length());
    return add;
	}

	public String readWord(FormatReader in) throws IOException {
    StringBuffer s = new StringBuffer();
    char ch;
    do {
      ch = (char)in.read();
    } while (Character.isWhitespace(ch));

    boolean ready = false;
    while(!ready) {
      if (ch == (char)-1 || Character.isWhitespace(ch))
        ready = true;
      else {
        s.append(ch);
			}
      if (!ready)
        ch = (char)in.read();
		}
    return s.toString();
	}

	public String[] readArgs(FormatReader in) throws IOException {
    List argsList = new ArrayList();
    boolean ready = false;
    while(!ready) {
      String arg = in.readNextQuotation('"');
      if (arg == null)
        arg = in.readNextWord();
      if (arg.length() == 0) {
        ready = true;
      } else if (arg.equals(";")) {
        ready = true;
      } else {
        argsList.add(arg);
			}
		}
    String[] args = new String[argsList.size()];
    for (int i=0; i<args.length; i++)
      args[i] = (String)argsList.get(i);
    return args;
	}

	public int echo(String args[]) throws IOException {
		for (int i=0; i<args.length; i++) {
      write(args[i]);
      write(' ');
		}
    write('\n');
    return 0;
	}

	public String getLine() {
    return ta.getText().substring(lineBegin).trim();
	}

	public boolean parseLine() throws IOException {
    String s = getLine();
    FormatReader in = new FormatReader(new StringReader(s));
    String command = in.readNextWord();
    String args[] = readArgs(in);
    if (command.length()!=0) {
			if (command.equals("exit")) {
        writeln("Bye...");
        return false;
      } else if (command.equals("echo")) {
        echo(args);
      } else if (command.equals("hello")) {
        writeln("Hello world!");
      } else {
        writeln("Processing: '"+command+"'");
			}
    }
    return true;
	}

  public boolean ready() throws IOException {
    return in.ready();
  }

	public char read() throws IOException {
    return (char)in.read();
	}

	public void write(char ch) throws IOException {
    if (out != null)
      out.write(ch); 
	}

	public void write(String s) throws IOException {
    if (out != null)
      out.write(s); 
	}

	public void writeln(String s) throws IOException {
		if (out != null) {
      out.write(s); 
      out.write('\n'); 
		}
	}

  public void echo(char ch) throws IOException {
    if (echo && out != null)
      out.write(ch);
  }

  public void echo(String s) throws IOException {
    if (echo && out != null)
      out.write(s);
  }

	public void close() {
    in.close();
    out.close();
    frame.dispose();
	}

	public static void main(String args[]) throws Exception {
    terminal term = new terminal();

    boolean ready = false;
    while (!ready) {
      term.promptUser();
      while (!term.ready());
      boolean newline = false;
      while (!newline) {
        while(!term.ready());
        char ch = term.read();
        if (ch == '\n') {
          newline = true;
        } else if (ch == '\t') {
          StringBuffer add = term.handleTab();
					//          term.echo(add.toString());
        } else if (ch == (char)-1) {
          System.err.println("ERROR: Read -1 from StdIn!");
        } else if (ch == '\0') {
          System.err.println("ERROR: Read NIL from StdIn!");
				}
      }
      ready = !term.parseLine();
		}

    term.close();
	}
} // class terminal


/* HISTORY:

2000-05-26
* Created!

*/
