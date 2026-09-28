import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //creating the frame that contains the visuals
        final JFrame frame = new JFrame("Physics Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2000, 1000);

        //PhyRenderer deals with drawing to the screen
        PhysRenderer.init();
        PhysRenderer physicsPhysRenderer = new PhysRenderer();
        LabWorld myLab = new LabWorld(100, physicsPhysRenderer);
        frame.add(physicsPhysRenderer);

        //experiment that runs
        collisionTest(myLab);

        frame.setVisible(true);
        myLab.start();
    }

    //experiments
    public static void collisionTest(LabWorld lab) {
        int numParticles = 100;
        for (int i = 0; i < numParticles; i++) {
            Shape.Triangle tri = new Shape.Triangle( new Vector[]{
                    new Vector(Math.random() * 50 - 25, Math.random() * 50 - 25),
                    new Vector(Math.random() * 50 - 25, Math.random() * 50 - 25),
                    new Vector(Math.random() * 50 - 25, Math.random() * 50 - 25)
            });
//            Shape.Triangle tri = new Shape.Triangle( new Vector[]{
//                    new Vector(-25, 0),
//                    new Vector(25, 0),
//                    new Vector(0, 25)
//            });
            double density = 0.1;
            double elasticity = 1;
            double mass = tri.getArea() * density;
            RigidBody p = new RigidBody (
                    new Vector[]{
                            new Vector(
                                    Math.random() * 900 + 50,
                                    Math.random() * 600 + 50
                            ),
                            new Vector(
                                    Math.random() * 100 - 50,
                                    Math.random() * 100 - 50
                            )
                    },
                    new double[] {Math.random() * Math.PI * 0.5, Math.random() * 2.0 - 1.0},
                    mass, elasticity,
                    tri
            );
            lab.add(p);
        }
    }

    public static void singleCollision(LabWorld lab) {
        Shape.Triangle tri = new Shape.Triangle( new Vector[]{
                new Vector(-25, 0),
                new Vector(25, 0),
                new Vector(0, 25)
        });
        RigidBody p1 = new RigidBody (
                new Vector[]{
                        new Vector(100, 100),
                        new Vector(10, 0)
                },
                new double[] {0, 2},
                1, 1.0,
                tri
        );
        RigidBody p2 = new RigidBody (
            new Vector[]{
                new Vector(400, 120),
                new Vector(-20, 0)
            },
            new double[] {0, 1},
            1, 1.0,
            tri
        );
        lab.add(p1);
        lab.add(p2);
    }
}
