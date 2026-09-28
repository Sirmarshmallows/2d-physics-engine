import java.util.Arrays;

public abstract class Shape {

    public abstract Shape translate(Vector v);
    public abstract Shape rotate(double theta);
    public abstract Vector getCenterOfMass();
    public abstract double getArea();

    public static class Circle extends Shape {
        private double radius;
        private Vector center;
        public Circle(double radius, Vector center){
            this.radius = Math.abs(radius);
            this.center = center;
        }

        //just puts the circle in the center :)
        public Circle(double radius){
            this(radius, Vector.zero());
        }

        public double getRadius() {
            return radius;
        }

        public Vector getCenter() {
            return center;
        }

        public Circle rotate(double theta) {
            //returns the same circle
            return new Circle(radius, center);
        }

        @Override
        public Vector getCenterOfMass() {
            return center;
        }

        public double getArea() {
            return Math.PI * radius * radius;
        }

        @Override
        public Circle translate(Vector v) {
            return new Circle(radius, center.add(v));
        }
    }

    public static class Triangle extends Shape {
        private Vector[] points;
        private Vector centroid;
        //note that normals are not unit vectors!
        private Vector[] normals;
        private Vector[] sides;


        public Triangle(Vector[] points) {
            if (points.length != 3){
                throw new IllegalArgumentException("Triangles must have 3 points");
            }
            else{
                this.points = points;
                Vector[] sides = new Vector[3];
                for (int i = 0; i < 3; i++){
                    sides[i] = points[i].subtract(points[(i + 1) % 3]);
                }
                this.sides = sides;
                Vector[] normals = new Vector[3];
                for (int i = 0; i < 3; i++) {
                    //creating perpendicular vectors
                    normals[i] = new Vector(sides[i].getY(), -1 * sides[i].getX());
                }
                this.normals = normals;
                //calculating centroid
                centroid = Vector.zero();
                for (Vector p : points) {
                    centroid = centroid.add(p);
                }
                centroid = centroid.scale(1.0 / 3.0);
            }
        }

        public Triangle translate(Vector v) {
            Vector[] newPoints = new Vector[3];
            for (int i = 0; i < 3; i++) {
                newPoints[i] = points[i].add(v);
            }
            return new Triangle(newPoints);
        }

        //rotates about the origin, add a center of rotation later
        public Triangle rotate(double angle) {
            Vector[] newPoints = new Vector[3];
            for (int i = 0; i < 3; i++) {
                newPoints[i] = points[i].rotate(angle);
            }
            return new Triangle(newPoints);
        }

        //projects the shape onto a vector, only returns the boundaries of the resulting line
        public double[] project(Vector v) {
            double[] values = new double[3];
            for (int i = 0; i < 3; i++) {
                values[i] = points[i].scalarProjectOn(v);
            }
            return values;
        }

        public Vector[] getPoints() {return points;}

        public Vector getPoint(int i) {
            return points[i];
        }
        //returns the midpoint on the indexed side
        public Vector getMidPoint(int i) {
            return points[i].add(points[(i+1)%3]).scale(0.5);
        }

        public Vector[] getNormals() {return normals;}
        public Vector getNormal(int i) {return normals[i];}

        public Vector[] getSides() {return sides;}
        public Vector getSide(int i) {return sides[i];}
        //returns the side index opposite to a point index
        public int getOppositeSideIndexOfPoint(int i) {
            return (i + 1) % 3;
        }

        public Vector getCenterOfMass() {
            return centroid;
        }

        public double getArea() {
            return Math.abs(getSide(0).pDot(getSide(2))) * 0.5;
        }

        public String toString(){
            return Arrays.toString(points);
        }
    }
}
