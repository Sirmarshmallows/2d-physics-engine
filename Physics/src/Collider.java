import java.util.Arrays;

public class Collider {
    private final Shape colliderShape;
    private final PhysicsObject linkedObject;

    private boolean hasCollided = false;
    private Vector correction = null;
    private Vector contactPoint = null;

    public Collider(Shape colliderShape, PhysicsObject linkedObject) {
        this.colliderShape = colliderShape;
        this.linkedObject = linkedObject;

        //triangle test
//        Vector[] output = new Vector[2];
//        System.out.println("Has collided = " + Triangle.collideWithTriangle(
//                new Shape.Triangle(new Vector[]{
//                        new Vector(-3.04, 1.8),
//                        new Vector(7.68, 6.17),
//                        new Vector(5,-2.73)
//                }),
//                new Shape.Triangle(new Vector[]{
//                        new Vector(3.15, 14.2),
//                        new Vector(8, 0),
//                        new Vector(16.8,10.5)
//                }), Vector.zero, output
//        ));
//        System.out.println("Collision Point: " + output[1]);
    }

    public Shape getColliderShape() {
        return colliderShape;
    }

    //do this to calculate new fields of this colliision
    public void collidesWith(Collider other) {
        Vector[] output = new Vector[2];

        Vector d = other.linkedObject.getPosition(this.linkedObject);
        if (colliderShape instanceof Shape.Circle) {
            Shape.Circle c1 = (Shape.Circle) colliderShape;
            Shape otherShape = other.getColliderShape();
            if (otherShape instanceof Shape.Circle) {
                hasCollided = Circle.isCollidingWithCircle(c1, (Shape.Circle) otherShape, d);
                output = Circle.collideWithCircle(c1, (Shape.Circle) otherShape, d);
                correction = output[0];
                contactPoint = output[1];
            } else {
                System.out.println("Circle - Other collision not defined");
            }
        } else if (colliderShape instanceof Shape.Triangle) {
            Shape.Triangle t1 = (Shape.Triangle) colliderShape;
            if (linkedObject.getAngle() != 0) {
                t1 = t1.rotate(Math.toDegrees(linkedObject.getAngle()));
            }
            Shape otherShape = other.colliderShape;
            if (otherShape instanceof Shape.Triangle) {
                Shape.Triangle t2 = (Shape.Triangle) otherShape;
                t2 = t2.rotate(Math.toDegrees(other.linkedObject.getAngle()));
//                System.out.println("#1 this pos " + linkedObject.getPosition() + ", other = " + other.linkedObject.getPosition());
                //TODO fix the weird angle stuff going on with this simulation
                hasCollided = Triangle.collideWithTriangle(
                        t1,
                        t2,
                        d,
                        output
                );
                if (hasCollided) {
                    correction = Triangle.getCorrection();
                    contactPoint = Triangle.getContactPoint().add(linkedObject.getPosition());
                    //System.out.println(correction + " " + contactPoint);
                }

            } else {
                System.out.println("Triangle - Other not defined");
            }
        }

        else {
            System.out.println("Undefined collider");
        }
    }

    //returns the most recent values of all these attributes
    public boolean hasCollided() {
        return hasCollided;
    }
    public Vector getCorrection() {
        return correction;
    }
    public Vector getContactPoint() {
        return contactPoint;
    }

    private static class Circle {

        static boolean isCollidingWithCircle(Shape.Circle c1, Shape.Circle c2, Vector d) {
            return  Math.pow(c1.getRadius() + c2.getRadius(), 2) >= d.getMagSquared();
        }

        static Vector[] collideWithCircle(
                Shape.Circle c1,
                Shape.Circle c2,
                Vector d
        ) {
            double s = 1 - (c1.getRadius() + c2.getRadius()) / d.getMag();
            Vector correction = d.scale(s);

            Vector contactPoint = d.normalize(c1.getRadius());
            return new Vector[]{correction, contactPoint};

        }
    }

    //todo make triangle-triangle collision work, and eventually expand it to circle-triangle and beyond!!!!
    private static class Triangle {
        private static Vector correction, contactPoint;

        static boolean collideWithTriangle(Shape.Triangle tri1, Shape.Triangle tri2, Vector d, Vector[] output) {
            Vector[] normals = new Vector[6];
            //moves the other triangle to it's relative position
            tri2 = tri2.translate(d);
            //putting all normals in one array
            for (int i = 0; i < 3; i++) {normals[i] = tri1.getNormals()[i];}
            for (int i = 3; i < 6; i++) {normals[i] = tri2.getNormals()[i - 3];}

            //testing for collision by projecting the shape onto the normal
            double[] corrections = new double[normals.length];
            Vector[] contactPoints = new Vector[6];
            for (int i = 0; i < normals.length; i++) {
                Vector n = normals[i];
                double[] projected1 = tri1.project(n);
                int[] minmax1 = minmaxIndex(projected1);
                double[] projected2 = tri2.project(n);
                int[] minmax2 = minmaxIndex(projected2);
                //finding distances[(-111.56188759328478, -38.43811240671522), (-11.56188759328478, -38.43811240671522), (-61.56188759328478, 11.56188759328478)]
                double[] distances = {
                        projected2[minmax2[1]] - projected1[minmax1[0]],
                        projected2[minmax2[0]] - projected1[minmax1[1]]
                };

                //testing for a normal where there isn't collision
                if ((projected1[minmax1[1]] <= projected2[minmax2[0]] || projected1[minmax1[0]] >= projected2[minmax2[1]])) {
                    return false;
                }

                int minCorrectionIndex = 0;
                if (Math.abs(distances[minCorrectionIndex]) > Math.abs(distances[1])) {
                    minCorrectionIndex = 1;
                }

                boolean contactOnTri1 = i >= normals.length / 2;

                int contactPointNum;
                if (contactOnTri1) {
                    if (minCorrectionIndex == 0) {
                        contactPointNum = minmax1[0];
                    } else {
                        contactPointNum = minmax1[1];
                    }
                    contactPoints[i] = tri1.getPoint(contactPointNum);
                } else {
                    if (minCorrectionIndex == 0) {
                        contactPointNum = minmax2[1];
                    } else {
                        contactPointNum = minmax2[0];
                    }
                    contactPoints[i] = tri2.getPoint(contactPointNum);
                }

                corrections[i] = distances[minCorrectionIndex];
                if (corrections[i] == 0) {
                    System.out.println("Triangle 1: " + tri1);
                    System.out.println("Triangle 2: " + tri2);
                }
            }

            //finding minimum correction
            int minIndex = 0;
            for (int i = 1; i < corrections.length; i++) {
                if ( Math.abs(corrections[minIndex]) > Math.abs(corrections[i])) {
                    minIndex = i;
                }
            }
//            System.out.println("Minimum normal: " + normals[minIndex]);
//            System.out.println("Correction: " + corrections[minIndex]);
            correction = normals[minIndex].normalize(corrections[minIndex]);
//            if (correction.equals(new Vector(0,0))) {
//                System.out.println("Correction is undefined");
//                System.out.println(Arrays.toString(corrections));
//            }
            //note that contact points are relative to this colliders linked object!
            contactPoint = contactPoints[minIndex].add(correction);
            return true;
        }

        //helper function as I seem to be doing this alot for triangles
        private static int[] minmaxIndex(double[] array) {
            int min = 0;
            int max = 0;

            for (int i = 0; i < array.length; i++) {
                double a = array[i];
                if (array[min] > a) {
                    min = i;
                }
                if (array[max] < a) {
                    max = i;
                }
            }

            return  new int[] {min,max};
        }

        public static Vector getCorrection() {
            return correction;
        }

        public static Vector getContactPoint() {
            return contactPoint;
        }
    }

}
