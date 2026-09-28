//defines an object with position, velocity, acceleration, and their angular versions
public class KinematicsPoint {
    private Vector position, velocity, acceleration;

    //positive denotes counter-clockwise rotation
    //all should be in terms of radians
    private double angle, angularVelocity, angularAcceleration;

    //defines the origin point at (0,0) with no motion
    static KinematicsPoint origin = new KinematicsPoint();

    public KinematicsPoint(
            Vector position, Vector velocity, Vector acceleration,
            double angle, double angularVelocity, double angularAcceleration
    ) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.angle = angle;
        this.angularVelocity = angularVelocity;
        this.angularAcceleration = angularAcceleration;
    }

    public KinematicsPoint(
            Vector position, Vector velocity,
            double angle, double angularVelocity
    ){
        this(position, velocity, Vector.zero(), angle, angularVelocity, 0);
    }

    public KinematicsPoint(
            Vector position, Vector velocity, Vector acceleration
    ) {
        this(position, velocity, acceleration, 0, 0, 0);
    }

    //inertial reference frame
    public KinematicsPoint(
        Vector position, Vector velocity
    ){
        this(
            position, velocity, Vector.zero()
        );
    }

    //creates a motionless point at the origin
    public KinematicsPoint() {
        this(Vector.zero(), Vector.zero());
    }

    //gives the position of the kinematics relative to some other position
    public Vector getPosition(KinematicsPoint referencePoint) {
        return position.subtract(referencePoint.position);
    }

    public Vector getPosition() {
        return getPosition(origin);
    }

    //gives velocity relative to another kinematics point
    public Vector getVelocity(KinematicsPoint referencePoint) {
        return velocity.subtract(referencePoint.velocity);
    }

    public Vector getVelocity() {
        return getVelocity(origin);
    }

    public Vector getAcceleration() {
        return acceleration;
    }

    public Vector getAcceleration(KinematicsPoint referencePoint) {
        return acceleration.subtract(referencePoint.acceleration);
    }

    protected void setAcceleration(Vector acceleration) {
        this.acceleration = acceleration;
    }

    //updates the point over a period of time
    protected void updatePosition(double deltaTime) {
        velocity = velocity.add(acceleration.scale(deltaTime));
        position = position.add(velocity.scale(deltaTime));

        angularVelocity += angularAcceleration * deltaTime;
        angle += angularVelocity * deltaTime;
    }

    //corrects position for collision purposes
    protected void correctPosition(Vector correction) {
        position = position.add(correction);
    }

    //TODO make it so this isn't needed any more
    protected void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public double getAngle() {
        return angle;
    }

    //returns the angle from the distance vector
    //between this and reference point
    public double getAngle(Vector reference) {
        return angle - reference.getAngle();
    }

    protected void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public double getAngularVelocity(KinematicsPoint referencePoint) {
        Vector r = getPosition(referencePoint);
        return velocity.pDot(r)/r.getMagSquared();
    }

    public double getAngularAcceleration() {
        return angularAcceleration;
    }

    public void setAngularAcceleration(double angularAcceleration) {
        this.angularAcceleration = angularAcceleration;
    }

    public double getAngularAcceleration(KinematicsPoint referencePoint) {
        Vector r = getPosition(referencePoint);
        Vector r_perp = new Vector(-r.getY(),r.getX());
        Vector v = getVelocity(referencePoint);
        Vector a = getAcceleration(referencePoint);
        double rsquare = r.getMagSquared();
        //If this actually becomes an important calculation, will need to double check math on this
        return (Math.sqrt(rsquare) * a.scalarProjectOn(r_perp) - v.scalarProjectOn(r_perp)*v.scalarProjectOn(r)) / rsquare;
    }

}
