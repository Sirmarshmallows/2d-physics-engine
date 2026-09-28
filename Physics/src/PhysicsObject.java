import java.util.ArrayList;

//defines a basic object that obeys newtonian physics
public abstract class PhysicsObject extends KinematicsPoint {

    static int objectCount = 0;

    private int objectNumber;

    private ArrayList<AppliedForce> appliedForces = new ArrayList<>();
    private Vector netForce;
    private double mass;

    //moment of inertia is about the axis going
    //through the center of mass
    //that is perpendicular to the screen
    private double momentInertia;

    //for collisions with other systems
    private final Collider collider;

    //how the object is represented graphically, and used for physics collision
    Shape physShape;

    public PhysicsObject(
            Vector position,
            Vector velocity,
            double angularPosition,
            double angularVelocity,
            double mass,
            Shape graphicsShape
    ) {
        super(position, velocity, angularPosition, angularVelocity);
        this.mass = mass;
        //translating the shape so that (0,0) is the center of mass
        this.physShape = graphicsShape.translate(graphicsShape.getCenterOfMass().invert());
        collider = new Collider(this.physShape, this);
        objectCount++;
        this.objectNumber = objectCount;
        netForce = Vector.zero();
        this.momentInertia = calculateMomentInertia(graphicsShape);
    }

    protected void update(double deltaTime) {
        //calculating net force and net torque on the object
        Vector netForce = Vector.zero();
        double netTorque = 0;
        for (AppliedForce f : appliedForces) {
            netForce = netForce.add(f.force);
            netTorque += f.getTorque();
        }
        //translational motion
        setAcceleration(netForce.scale(1 / mass));
        setAngularAcceleration(netTorque / momentInertia);
        //resetting all forces on the object
        appliedForces = new ArrayList<>();

        updatePosition(deltaTime);
    }

    //applies a force for the duration of one frame
    private void applyForce(AppliedForce force) {
        appliedForces.add(force);
    }

    public void applyForce(Vector force, Vector position) {
        applyForce(new AppliedForce(force, position));
    }

    public void applyForce(Vector force) {
        applyForce(force, getPosition());
    }

    //it is guaranteed that this other physics object is currently colliding
    public void collide(PhysicsObject other, Vector correction, Vector contactPoint, double deltaTime) {
        //the object with greater velocity has the corrected position
        if (getVelocity().getMagSquared() > other.getVelocity().getMagSquared()) {
            correctPosition(correction);
        }
        //exerting a normal force if net force is pointing towards the other object
        if (netForce.dot(correction) < 0) {
            Vector n = netForce.projectOn(correction).invert();
            applyForce(n);
        }
    }

    //reaction depends on physics model, collision axis is not normalized at the moment
    //is guaranteed to be away from the other object
//    abstract void collisionReaction(PhysicsObject other, Vector collisionNormal, Vector contactPoint, double deltaTime);

    public Shape getShape() {
        return physShape;
    }

    public double getMass() {
        return mass;
    }

    public Vector getMomentum(KinematicsPoint referencePoint) {
        return getVelocity(referencePoint).scale(mass);
    }

    public Vector getMomentum() {
        return getMomentum(KinematicsPoint.origin);
    }

    public double getKineticEnergy() {
        return 0.5 * mass * getVelocity().getMagSquared() + 0.5 * momentInertia * Math.pow(getAngularVelocity(), 2);
    }

    //returns moment of inertia about any position
    public double getMomentInertia(Vector position) {
        return momentInertia + mass * position.subtract( getPosition() ).getMagSquared();
    }

    //returns it from center of mass
    public double getMomentInertia() {
        return momentInertia;
    }

    //TODO this is just for editing, get rid of this in final program if all else is functioning


    public Collider getCollider() {
        return collider;
    }

    //defines the force, location of an applied force, and the object it's applied to
    private class AppliedForce {
        final Vector force, position;

        public AppliedForce(Vector force, Vector location) {
            this.force = force;
            this.position = location;
        }

        //returns the torque about the center of mass of an object
        public double getTorque() {
            //distance to center of mass
            Vector radius = this.position.subtract(PhysicsObject.this.getPosition());
            //essentially the cross product
            return radius.pDot(force);
        }
    }

    private double calculateMomentInertia(Shape s) {
        if (s instanceof Shape.Circle) {
            double r = ((Shape.Circle) s).getRadius();
            return 0.5 * getMass() * r * r;
        } else if (s instanceof Shape.Triangle) {
            Shape.Triangle t = (Shape.Triangle) s;
            //sets which point is the top, shouldn't matter for finding Icm
            int i = 0;
            Vector midpoint = t.getMidPoint(t.getOppositeSideIndexOfPoint(i));
            Vector heightMedian = t.getPoint(i).subtract(midpoint);
            Vector bottom = t.getSide(t.getOppositeSideIndexOfPoint(i));
            double I0 = (heightMedian.getMagSquared() + bottom.getMagSquared() * 0.25) * mass / 6;
            //System.out.println("mass: " + mass + ", moment of inertia " + I0);
            return I0 - mass * t.getCenterOfMass().subtract(midpoint).getMagSquared();
        } else {
            throw new IllegalArgumentException("PHYSICS OBJECT: I don't have a formula to calculate moment of inertia for this shape!");
        }
    }

    public String toString() {
        return "Physics Object #" + objectNumber;
    }

}