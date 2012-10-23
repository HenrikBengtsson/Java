package hb.text;

import hb.format.Format;
import hb.format.Parameters;


public class TextArea {
  public int height, width;
  public int xoffset=0, yoffset=0;
  public char text[][];

	public TextArea() {
    this(24,80);
	}

	public TextArea(int height, int width) {
    this.height = height;
    this.width = width;
    text = new char[height][width];
    clear();
	}

	public TextArea(char text[][]) {
    setText(text);
	}

	public void setOffset(int yoffset, int xoffset) {
    this.xoffset = xoffset;
    this.yoffset = yoffset;
	}

	public Object clone() {
    TextArea ta = new TextArea(text);
    ta.setOffset(yoffset, xoffset);
    return ta;
	}

	public void resize(int newwidth, int newheight) {
    this.height = height;
    this.width = width;
    char newtext[][] = new char[height][width];
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
        char ch = get(i,j);
        if (ch == (char)-1)
          ch = ' ';
        newtext[i][j] = ch;
      }
    }
  }

	public void setText(char text[][]) {
    this.height = text.length;
    this.width = text[0].length;
    this.text = text;
	}

	public void fill(char ch) {
    for(int i=0; i<height; i++) 
      for(int j=0; j<width; j++) 
        text[i][j] = ch;
	}

	public void fillArea(int y0,int x0, int y1,int x1, char ch) {
		if (yoffset != 0) {
      y0+=yoffset; y1+=yoffset;
		}
		if (xoffset != 0) {
      x0+=xoffset; x1+=xoffset;
		}
    int ymin = (int)Math.min(height,y1);
    int xmin = (int)Math.min(width,x1);
    for(int j=y0; j<=ymin; j++) 
      for(int i=x0; i<=xmin; i++) 
        text[i][j] = ch;
	}

	public void fillRow(int row, char ch) {
    if (yoffset != 0) row += yoffset;
    for(int j=0; j<width; j++) 
      text[row][j] = ch;
	}

	public void fillColumn(int col, char ch) {
    if (xoffset != 0) col += xoffset;
    for(int i=0; i<height; i++) 
      text[i][col] = ch;
	}

	public void clear() {
    fill(' ');
	}

	public char get(int row, int col) {
    if (0 <= row && row < height && 0 <= col && col <width)
      return text[row][col];
    else
      return (char)-1;
	}

	public int put(int row, int col, char ch) {
    if (yoffset != 0) row += yoffset;
    if (xoffset != 0) col += xoffset;
    if (0 <= row && row < height && 0 <= col && col <width)
      text[row][col] = ch;
    return row+1;
	}

	public int put(int row, int col, String s) {
    if (yoffset != 0) row += yoffset;
    if (xoffset != 0) col += xoffset;
    int col0=col;
    int len=s.length();
		for (int i=0; i<len; i++) {
      char ch = s.charAt(i);
      if (ch == '\n') {
        row++;
        col=col0;
      } else if (ch == '\r') {
        row++;
        col=col0;
        if (i<len-1 && s.charAt(i+1) == '\n')
          i++;
      } else if (ch == '%') {
				if (i<len-1 && s.charAt(i+1) == '|') {
          i++;
          col0=col;
        } else
          put(row,col++, ch);
      } else {
        put(row,col++, ch);
      }
    }
    return row+1;
	}

	public int put(int row, int col, String fmtstr, Parameters p) {
    return put(row,col, Format.sprintf(fmtstr,p));
  }

	public void clear(int row, int col) {
    if (yoffset != 0) row += yoffset;
    if (xoffset != 0) col += xoffset;
    text[row][col] = ' ';
	}

	public void clearArea(int y0,int x0, int y1,int x1, char ch) {
    fillArea(y0,x0, y1,x1, ch);
	}

	public void clearRow(int row) {
    fillRow(row, ' ');
	}

	public void clearColumn(int col) {
    fillColumn(col, ' ');
	}

	public void drawDiagonal(int y0, int x0, int len, 
                             String direction, char ch) {
    if (yoffset != 0) y0 += yoffset;
    if (xoffset != 0) x0 += xoffset;
    int y1=y0, x1=x0;
    boolean from=(direction.indexOf('>') >= 0);
    Format.printf("from=%l\n", new Parameters(from));
    if (direction.indexOf('/') != -1) {
			if (from) {
        y1=y0-len-1;
        x1=x0+len-1;
      } else {
        y1=y0+len-1;
        x1=x0-len-1; 
			}
    } else if (direction.indexOf('\\') != -1) {
			if (from) {
        y1=y0+len-1;
        x1=x0+len-1; 
      } else {
        y1=y0-len-1;
        x1=x0-len-1; 
			}
    }
    drawLine(y0,x0, y1,x1, ch);
	}

	public void drawLine(int y0, int x0, int y1, int x1, char ch) {
    drawLine(y0,x0, y1,x1, ch, true);
  }

	public int[][] drawLine(int y0, int x0, int y1, int x1, char ch, 
                                                            boolean dense) {
		if (yoffset != 0) {y0 += yoffset; y1 += yoffset;}
    if (xoffset != 0) {x0 += xoffset; x1 += xoffset;}
		int dx = x1-x0; dx = (dx < 0? dx-1:dx+1);
    int dy = y1-y0; dy = (dy < 0? dy-1:dy+1);
    int steps;
    int densesteps = (int)Math.max(Math.abs(dx),Math.abs(dy));
    if (dense || (int)Math.abs(dx) <= 2 || (int)Math.abs(dy) <= 2)
      steps = densesteps;
    else {
      steps = (int)Math.abs(Math.min(Math.abs(dx),Math.abs(dy)));
      if ((int)(densesteps/2.0) > (int)(steps/2.0))
        steps--;
    }
    int pos[][] = new int[steps][2];
		//    Format.printf("(y0,x0)=(%d,%d), (y1,x1)=(%d,%d), dx=%d, dy=%d, steps=%d\n", 
		//     new Parameters(y0).add(x0).add(y1).add(x1).add(dx).add(dy).add(steps));
    for (int k=0; k<steps; k++) {
      int x = (int)(x0+k*dx/steps);
      int y = (int)(y0+k*dy/steps);
      put(y,x,ch);
      pos[k][0] = y;
      pos[k][1] = x;
		}
    return pos;
	}

	public TextArea copyMinimized() {
    int minrow=-1, maxrow=-1, mincol=Integer.MAX_VALUE, maxcol=-1;

    for(int i=0; i<height; i++) {
      for(int j=0; j<width; j++) {
				if (text[i][j] != ' ') {
		  		if (minrow == -1)
            minrow = i;
		  		if (j < mincol)
            mincol = j;
          if (i > maxrow)
            maxrow = i;
		  		if (j > maxcol)
            maxcol = j;
        }
      }
    }
    if (maxrow < 0)
      return new TextArea(0,0);
    return copyArea(minrow,mincol, maxrow,maxcol);
	}

	public TextArea copyArea(int y0, int x0, int y1, int x1) {
    int width  = x1-x0+1;
    int height = y1-y0+1;
    char area[][] = new char[height][width];
    for (int i=0; i<height; i++) {
      for (int j=0; j<width; j++) {
				area[i][j] = text[y0+i][x0+j];
      }
    }
    return new TextArea(area);
  }  

	public void pasteArea(int y0, int x0, TextArea area) {
    for (int i=0; i<area.height; i++) {
      for (int j=0; j<area.width; j++) {
				put(y0+i,x0+j, area.text[i][j]);
      }
    }
  }  

	public String toString() {
    StringBuffer s = new StringBuffer();
    for(int i=0; i<height; i++) {
      s.append(text[i]).append('\n');
    }
    return s.toString();
	}

	public void print() {
    System.out.print(toString());
	}

	private static void test01() throws Exception {
	  Format.printf("test01...\n");
	  Parameters p = new Parameters();

    TextArea tm = new TextArea();
    tm.fill('x');
    tm.clearRow(4);
    tm.clearColumn(24);
    tm.fillColumn( 9,'1');
    tm.fillColumn(19,'2');
    tm.fillColumn(29,'3');
    tm.fillColumn(39,'4');
    tm.fillColumn(49,'5');
    tm.fillColumn(59,'6');
    tm.fillColumn(69,'7');
    tm.fillColumn(79,'8');
    tm.drawLine(11,4, 50,22, '.');
    tm.drawDiagonal(5,20, 15, "\\>", '\\');
    tm.drawDiagonal(6,55, 40, "</", '/');
    Format.printf("%s", p.add(tm));
	}
	
	public static void main(String args[]) throws Exception {
    test01();
	}
} // class TextArea


/*
HISTORY

990316 
  Changed name to hb.text.TextArea
990315
  Created. Why haven't I done this before?!
	
*/
