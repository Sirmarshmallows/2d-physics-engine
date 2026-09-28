import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import java.awt.*;


public class PhysRenderer extends GLCanvas{
    static GLCapabilities capabilities;
    PhysicsObject[] renderObjects;

    public PhysRenderer() {
        super(capabilities);
        addGLEventListener(new EventListener());
    }

    //must be run before creating any renderers
    public static void init(){
        GLProfile.initSingleton();
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        capabilities = new GLCapabilities(profile);
    }

    //gets a list of shapes to render, and sets them to be rendered whenever the display method is called
    public void render(PhysicsObject[] physicsObject) {
        renderObjects = physicsObject;
    }

    private class EventListener implements GLEventListener {

        @Override
        public void init(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {

        }

        @Override
        public void display(GLAutoDrawable drawable) {

            GL2 gl = drawable.getGL().getGL2();
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

            gl.glLoadIdentity();
            Graphics g = new Graphics(gl);

            for (PhysicsObject obj : renderObjects) {
                try {
                    float x = (float) obj.getPosition().getX();
                    float y = (float) obj.getPosition().getY();
                    float angle = (float) Math.toDegrees(obj.getAngle());
                    g.fillShape(x, y, angle,obj.getShape(), Color.WHITE);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(obj.getPosition());
                }
            }
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            GL2 gl = drawable.getGL().getGL2();
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();

            //makes each pixel on the screen one unit
            gl.glOrtho(x, width / 2f, y, height/2f, -1, 1);
            gl.glMatrixMode(GL2.GL_MODELVIEW);
        }
    }

}
