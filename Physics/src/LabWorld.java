import java.util.ArrayList;
import com.jogamp.opengl.awt.GLCanvas;

public class LabWorld {
    //gravitational constant
    static double G = 1;

    boolean simulateGravity = false;

    private final PhysRenderer physicsRenderer;

    private final PhysicsLoop loop;

    ArrayList<PhysicsObject> objects = new ArrayList<>();

    private double labTime = 0;

    public LabWorld(int targetFPS, PhysRenderer physicsPhysRenderer) {
        super();
        loop = new PhysicsLoop(targetFPS) {
            public void update(double dt) {
                updateState(dt);
                updateDisplay();
            }
        };
        this.physicsRenderer = physicsPhysRenderer;
    }

    //there is also a method to add swing components, don't let those get confused!
    public void add(PhysicsObject obj) {
        objects.add(obj);
    }

    //test if the two physics objects are colliding, and if so corrects their positioning
    private void collisionTest(PhysicsObject obj1, PhysicsObject obj2) {

    }

    private void updateState(double dt) {
        labTime += dt;
        ArrayList<Integer> collidedObjects = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            PhysicsObject updateObj = objects.get(i);
            //exerting a gravity force
            if (simulateGravity) {
                for (int j = 0; j < objects.size(); j++) {
                    if (i != j) {
                        PhysicsObject other = objects.get(j);
                        //applies a gravitational force to update object
                        applyGravitationalForce(updateObj, other);
                    }
                }
            }
            //Collision Testing
            updateObj.update(dt);
            //arrays to prevent double collisions, which the simulation cannot currently handle
            for (int j = 0; j < objects.size(); j++) {
                if  (i != j) {
                    PhysicsObject other = objects.get(j);
                    Collider c = updateObj.getCollider();
                    //tests for collision, and if a collision has occurred, then tells the physics objects
                    c.collidesWith(other.getCollider());
                    if (c.hasCollided()) {
                        //loop.pause();
                        //TODO why is it not correctly identifying when a collision has happened!??

                        //System.out.println("LABWORLD: collision detected");
                        updateObj.collide(other, c.getCorrection(), c.getContactPoint(), dt);
                        //other.collide(updateObj, c.getCorrection().invert(), c.getContactPoint(), dt);
                        //System.out.println();
                    }
                }
            }
        }
    }

    private void updateDisplay() {
        physicsRenderer.render(objects.toArray(new PhysicsObject[0]));
        physicsRenderer.display();
    }

    //applies a gravitational force to object 1
    private void applyGravitationalForce(PhysicsObject obj1, PhysicsObject obj2) {
        double distance = obj1.getPosition(obj2).getMag();
        double magnitude = G * obj1.getMass() * obj2.getMass() / (distance * distance);
        obj1.applyForce(obj2.getPosition(obj1).normalize(magnitude), obj1.getPosition());
    }

    public void simulateGravity(boolean b) {
        simulateGravity = b;
    }

    public void start() {
        loop.start();
    }
}
