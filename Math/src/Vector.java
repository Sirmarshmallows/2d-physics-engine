public class Vector {
    protected final double x, y;

    static Vector zero = new Vector(0,0);

    //takes in an x and y component
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //does vector addition
    public Vector add(Vector v) {
        return new Vector(v.x + this.x, v.y + this.y);
    }

    //vector subtraction
    public Vector subtract(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y);
    }

    //scales the vector by a scalar
    public Vector scale(double scalar) {
        return new Vector(x * scalar, y * scalar);
    }

    //rotates by an angle in degrees, will do better calculations for exactly 90 degree multiples
    public Vector rotate(double angle) {
        double theta = Math.toRadians(angle);
        if (angle == 0) {
            return this;
        } else if (angle % 90 == 0) {
            if (angle % 180 == 0) {
                return new Vector(-x, -y);
            } else if (angle % 270 == 0) {
                return new Vector(y, -x);
            } else {
                return new Vector(-y, x);
            }
        }
        return this.scale(Math.cos(theta))
                .add(new Vector(-y, x).scale(Math.sin(theta)));
    }

    //flips the vector 180 degrees
    public Vector invert() {return this.scale(-1);}

    //returns as new vector with the same direction, but a magnitude of the given mag
    public Vector normalize(double mag) {
        return this.scale(mag/getMag());
    }

    //does this just for 1
    public Vector normalize() {
        return normalize(1);
    }

    //dot product of two vectors
    public double dot(Vector v) {
        return x * v.getX() + y * v.getY();
    }

    //dot product to the perpendicular of a vector
    public double pDot(Vector v) {
        return x * v.getY() - y * v.getX();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMag() {return Math.sqrt(getMagSquared());}

    //some calculations require the squared magnitude, and there's less
    //data loss if we don't run it through the sqrt, and then square it again
    public double getMagSquared() {
        return x * x + y * y;
    }

    //returns in radians
    public double getAngle() {
        double angle = Math.atan(y / x);
        //because atan will only return angles in the right quadrants
        if (x < 0) {
            angle += Math.PI;
        }
        return angle;
    }

    //projects this vector onto v
    public Vector projectOn(Vector v) {
        Vector result = v.scale(this.dot(v) / v.getMagSquared());
        return v.scale(this.dot(v) / v.getMagSquared());
    }

    public double scalarProjectOn(Vector v) {
        return this.dot(v) / v.getMag();
    }

    //returns the component of this vector perpendicular to another vector
    public Vector getPerpendicularTo(Vector v) {
        return subtract(getParallelTo(v));
    }

    //this is when direction doesn't matter for the perpendicular
    public Vector createPerpendicular() {
        return new Vector(-y, x);
    }

    //returns the component of this vector parallel to another vector
    public Vector getParallelTo(Vector v) {
        return projectOn(v);
    }

    //creates a vector with no magnitude, or direction
    public static Vector zero(){
        return zero;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean equals(Vector v) {
        return v.getY() == this.getY() && v.getX() == this.getX();
    }

}
