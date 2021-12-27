package river.ride;

import javax.media.opengl.GL;
import java.lang.Math;

public class Sprite {

    GL gl;
    private int x;
    private int y;
    private int texture;
    private float xScale;
    private float yScale;
    private int score = 0;
    private int moveDirection;

    public Sprite(GL gl, int x, int y, int texture, float xScale, float yScale, int moveDirection) {
        this.gl = gl;
        this.x = x;
        this.y = y;
        this.xScale = xScale;
        this.yScale = yScale;
        this.texture = texture;
        this.moveDirection = moveDirection;
    }

    public void DrawSprite() {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);	// Turn Blending On

        gl.glPushMatrix();
        gl.glTranslated(x / (consts.maxWidth / 2.0) - 1, y / (consts.maxHeight / 2.0) - 1, 0);
        gl.glScaled(xScale, yScale, 1);
        //System.out.println(x +" " + y);
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL.GL_BLEND);
    }

    public boolean colideWith(Sprite sprite) {
        float lowerY = sprite.getY() - sprite.getyScale() * consts.maxHeight / 2;
        float UpperY = sprite.getY() + sprite.getyScale() * consts.maxHeight / 2;
        float leftX = sprite.getX() - sprite.getxScale() * consts.maxWidth / 2;
        float rightX = sprite.getX() + sprite.getxScale() * consts.maxWidth / 2;
        float plowerY = getY() - getyScale() * consts.maxHeight / 2;
        float pUpperY = getY() + getyScale() * consts.maxHeight / 2;
        float pleftX = getX() - getxScale() * consts.maxWidth / 2;
        float prightX = getX() + getxScale() * consts.maxWidth / 2;

        if (pleftX < rightX && leftX < prightX && plowerY < UpperY && lowerY < pUpperY) {
            return true;
        }
        return false;
    }

    public void scale(float xScale, float yScale) {

    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTexture() {
        return texture;
    }

    public void setxScale(float xScale) {
        this.xScale = xScale;
    }

    public void setyScale(float yScale) {
        this.yScale = yScale;
    }

    public float getxScale() {
        return xScale;
    }

    public float getyScale() {
        return yScale;
    }

    public void setMoveDirection(int moveDirection) {
        this.moveDirection = moveDirection;
    }

    public int getMoveDirection() {
        return moveDirection;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    
}
