import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.awt.*;

public class EventListener implements GLEventListener {

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
//
//        GL2 gl = drawable.getGL().getGL2();
//        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
//
//        gl.glLoadIdentity();
//        Graphics g = new Graphics(gl);
//        //TODO this vector(0,0) should not be 0,0, big changes to the code that are probably going
//        //to get really annoying, so honestly maybe just delete this part and restart?
////        Shape shape = new Shape.Circle(100, new Vector(0,0));
////        g.fillShape(50, 50, 0, shape, Color.RED);
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
