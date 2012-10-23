package hb.math.graph;

public class Dimension {
  public double xmin, ymin, xmax, ymax;
  public double width, height;

	public Dimension() {
    xmin = ymin = xmax = ymax = 0.0;
    width  = 0.0;
    height = 0.0;
	}

	public Dimension(double xmin, double ymin, double xmax, double ymax) {
    this.xmin = xmin;
    this.ymin = ymin;
    this.xmax = xmax;
    this.ymax = ymax;
    this.width  = xmax-xmin;
    this.height = ymax-ymin;
	}

	public Dimension(double width, double height) {
    xmin = ymin = 0;
    xmax = xmin+width;
    ymax = ymin+height;
    this.width  = xmax-xmin;
    this.height = ymax-ymin;
	}
} // class Dimension


/* HISTORY:

990610
* Created.

 */
