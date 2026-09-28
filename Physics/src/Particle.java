public class Particle extends PhysicsObject {
    //used to find the coefficient of restitution
    private double elasticity;

    public Particle (Vector position, Vector velocity, double mass, double elasticity, Shape s) {
        super(
                position, velocity,
                0, 0,
                mass, s
        );
    }

    //note that non-circular shapes will act strangely as there is no torques simulated on a particle!
    public Particle(Vector position, Vector velocity, double mass, double elasticity, double radius) {
        super(
                position, velocity,
                0, 0,
                mass,
                new Shape.Circle(radius)
        );
        this.elasticity = elasticity;
    }

    public void collide(PhysicsObject other, Vector correction, Vector contactPoint, double deltaTime) {
        super.collide(other, correction,contactPoint, deltaTime);
        double c;
        if (other instanceof Particle) {
            Particle p = (Particle) other;
            //coefficient of restitution
            c = (elasticity + p.elasticity) * 0.5;
        } else {
            c = elasticity;
        }
        //center of mass velocity
        Vector vcm = this.getMomentum().add(other.getMomentum())
                .scale(1 / (getMass() + other.getMass()));
//        Vector normal = getPosition(other);
        Vector normal = correction;
        //initial velocity in center of mass frame parallel to normal
        Vector u1 = getVelocity().getParallelTo(normal);
        Vector u2 = other.getVelocity().getParallelTo(normal);
        //final parallel velocities
        Vector v1 = vcm.getParallelTo(normal).scale(c + 1).subtract(u1.scale(c));
        Vector v2 = vcm.getParallelTo(normal).scale(c + 1).subtract(u2.scale(c));
        //now finding the new total velocity
        v1 = v1.add(getVelocity().getPerpendicularTo(normal));
        v2 = v2.add(other.getVelocity().getPerpendicularTo(normal));
        setVelocity(v1);
        other.setVelocity(v2);
    }
}
