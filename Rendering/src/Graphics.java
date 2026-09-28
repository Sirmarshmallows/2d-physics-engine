import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import java.awt.*;

public class Graphics {
    GL2 gl;

    public Graphics(GL gl) {
        this.gl = gl.getGL2();
    }

    private void genRect(int x, int y, int width, int height, Color c, int type){
        gl.glColor3f(c.getRed(), c.getGreen(), c.getBlue());
        gl.glTranslatef(x,y,0);

        gl.glBegin(type);
            gl.glVertex2f(0,0);
            gl.glVertex2f(width,0);
            gl.glVertex2f(width,height);
            gl.glVertex2f(0,height);
        gl.glEnd();

        gl.glLoadIdentity();
    }

    public void fillRect(int x, int y, int width, int height, Color c) {
        genRect(x,y,width,height,c,GL2.GL_QUADS);
    }

    public void drawRect(int x, int y, int width, int height, Color c) {
        genRect(x,y,width,height,c,GL2.GL_LINE_LOOP);
    }

    private void regularPolygon(float radius, int sides, int type) {
        gl.glBegin(type);
        double deltaAngle = 2 * Math.PI / sides;
        for (int i = 0; i < sides; i++){
            double Angle = deltaAngle * i;
            gl.glVertex2f(
                    radius * (float) Math.cos(Angle),
                    radius * (float) Math.sin(Angle)
            );
        }
        gl.glEnd();
        gl.glLoadIdentity();
    }

    private void drawTriangle (Shape.Triangle t, int type) {
        gl.glBegin(type);
        for (int i = 0; i < 3; i++) {
            Vector point = t.getPoint(i);
            gl.glVertex2f(
                    (float) point.getX(),
                    (float) point.getY()
            );
        }
        gl.glEnd();
        gl.glLoadIdentity();
    }

    private void draw(float x, float y, float angle, Shape s, Color c, int type) {
        //initial translations will be the same for all shapes
        gl.glColor3f(c.getRed(), c.getGreen(), c.getBlue());
        gl.glTranslatef(x,y,0);
        gl.glRotatef(angle, 0, 0, 1);
        if (s instanceof Shape.Circle) {
            Shape.Circle circle = (Shape.Circle) s;
            regularPolygon((float) circle.getRadius(),(int) circle.getRadius() * 10, type);
        } else if (s instanceof Shape.Triangle) {
            Shape.Triangle tri =  (Shape.Triangle) s;
            drawTriangle(tri, type);
        }
    }

    public void drawShape(float x, float y, float angle, Shape s, Color c) {
        draw(x,y,angle,s,c,GL2.GL_LINE_LOOP);
    }

    public void fillShape(float x, float y, float angle, Shape s, Color c) {
        draw(x,y,angle,s,c,GL2.GL_POLYGON);
    }
}
