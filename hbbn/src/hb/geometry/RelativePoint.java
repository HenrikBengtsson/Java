package hb.geometry;

import java.awt.Point;

public class RelativePoint extends Point {
  Object object;
  Point positioner;
  boolean flag;

	public RelativePoint() {
	}

	public RelativePoint(int x, int y) {
    super(x,y);
	}

	public RelativePoint(Point p) {
    super(p);
	}

	public RelativePoint(Point p, int x, int y) {
    super(x,y);
    positioner = p;
	}

	public void setObject(Object object) {
    this.object = object;
	}

	public Object getObject() {
    return object;
	}

	public Point getPositioner() {
    return positioner;
	}

	public void setPositioner(Point p) {
    positioner = p;
	}

	public double getX() {
		if (positioner != null) {
      Point p0 = positioner.getLocation();
      return x+p0.x;
		} else {
      return x;
		}
	}

	public double getY() {
		if (positioner != null) {
      Point p0 = positioner.getLocation();
      return y+p0.y;
		} else {
      return y;
		}
	}

	public Point getLocation() {
		if (positioner != null) {
  		if (flag) {
        throw new RuntimeException(
          getClass().getName()+".getLocation(): Detected circular reference.");
  		} else
        flag = true;
      Point p0 = positioner.getLocation();
      flag = false;
      return new Point(x+p0.x,y+p0.y);
		} else {
      return this;
		}
	}

	public void fixLocation() {
		if (positioner != null) {
      Point p0 = positioner.getLocation();
      x += p0.x;
      y += p0.y;
      positioner = null;
		}
	}

	public void setLocation(Point p, int x, int y) {
    setPositioner(p);
    super.setLocation(x,y);
	}

	public Point getAnchor() {
		if (positioner != null) {
      if (positioner instanceof RelativePoint)
        return ((RelativePoint)positioner).getAnchor();
      else
        return positioner;
		} else
      return this;
	}

	public String toString() {
		if (positioner != null) {
      Point p0 = getLocation();
      return getClass().getName()+"[X="+p0.x+",Y="+p0.y+"]";
		} else
      return super.toString();
	}

	public static void main(String[] args) throws Exception {
    Point[] p = new Point[10];
    RelativePoint[] rp = new RelativePoint[10];
    p[0] = new Point(0,0);
    p[1] = new Point(10,10);
    rp[0] = new RelativePoint(p[0], 5,1);
    rp[1] = new RelativePoint(rp[0], -10,-2);

    for (int k=0; k<p.length; k++) {
      if (p[k] != null)
        System.out.println(p[k]);
		}

    for (int k=0; k<rp.length; k++) {
      if (rp[k] != null)
        System.out.println(rp[k]);
		}

    rp[0].fixLocation();

    for (int k=0; k<rp.length; k++) {
      if (rp[k] != null)
        System.out.println(rp[k]);
		}

	}
} // class RelativePoint


/* HISTORY:

2000-07-10
* Added an optional reference to the object that is located at the point.
2000-07-07
* Created.

 */
