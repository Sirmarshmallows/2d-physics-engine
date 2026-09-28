public class RigidBody extends PhysicsObject {
    final private double elasticity;

    public RigidBody(
            Vector[] posDerivatives,
            double[] angleDerivatives,
            double mass,
            double elasticity,
            Shape s
    ) {
        super(
            posDerivatives[0],
            posDerivatives[1],
            angleDerivatives[0],
            angleDerivatives[1],
            mass,
            s
        );
        this.elasticity = elasticity;
    }

    @Override
    public void collide(PhysicsObject other, Vector collisionNormal, Vector contactPoint, double deltaTime) {
        super.collide(other, collisionNormal, contactPoint, deltaTime);

        if (other instanceof RigidBody) {
            Vector n = collisionNormal.normalize();
            //initial variables for collision
            double va, wa, ma, Ia, ra;
            double vb, wb, mb, Ib, rb;
            //a measure of how bouncy the materials are together, property
            //of a pair of objects
            double restitution = ( elasticity + ((RigidBody) other).elasticity ) * 0.5;

            va = getVelocity().scalarProjectOn(n);
            vb = other.getVelocity().scalarProjectOn(n);

            wa = getAngularVelocity();
            wb = other.getAngularVelocity();

            ma = getMass();
            mb = other.getMass();

            Ia = getMomentInertia();
            Ib = other.getMomentInertia();

            ra = contactPoint.subtract(getPosition()).createPerpendicular().scalarProjectOn(n);
            rb = contactPoint.subtract(other.getPosition()).createPerpendicular().scalarProjectOn(n);


            //find equation on desmos for more information under Rotation Collision 2
            double J = -(1 + restitution) * (va + ra * wa - vb - rb * wb) * (ma * mb * Ia * Ib)
                    / ( Ia * Ib * (ma + mb) + ma * mb * (Ib * ra*ra + Ia * rb*rb) );


            //System.out.println("Before energy: " + (getKineticEnergy() + other.getKineticEnergy()));
            //System.out.println("J = " + J);

            Vector impulse = n.scale(J);

            setVelocity(getVelocity().add(impulse.scale(1.0 / getMass())));
            other.setVelocity(other.getVelocity().add(impulse.scale(-1.0 / other.getMass())));
            //angular impulse
            Vector r = contactPoint.subtract(getPosition());
            double H = r.pDot(impulse);
            this.setAngularVelocity(this.getAngularVelocity() + H / (getMomentInertia()));

            r = contactPoint.subtract(other.getPosition());
            H = r.pDot(impulse.invert());
            other.setAngularVelocity(other.getAngularVelocity() + H / other.getMomentInertia());
        } else {
            System.out.println("RigidBody can only collide with other rigid bodies at the moment!");
        }
    }

}
