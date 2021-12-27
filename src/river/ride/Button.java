package river.ride;

import javax.media.opengl.GL;


public class Button {
    GL gl;
    private int x;
    private int y;
    private float scaleX;
    private float scaleY;
    private int texture;
    private String name = "";

    public Button(GL gl, int x, int y, int texture, float scaleX, float scaleY,String name) {
        this.gl = gl;
        this.x = x;
        this.y = y;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.texture = texture;
        this.name = name;
    }

    
            
    public void drawButton(){
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);	// Turn Blending On

        gl.glPushMatrix();
        gl.glTranslated(x / (consts.maxWidth / 2.0) - 1, y / (consts.maxHeight / 2.0) - 1, 0);
        gl.glScaled(scaleX, scaleY, 1);
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
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

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public int getTexture() {
        return texture;
    }
    
    public boolean  isClicked(int xc ,int yc){
        //setSize(600,770);
        //25% posx
        
        //ypos = 770- 90%
        //int xb = 600-25/100.0;
        if((xc < (this.x + scaleX*50) && xc >=(this.x - scaleX*50)) ){
            if((100 - yc < ( this.y + scaleY*50) && 100 - yc >=(this.y - scaleY*50)))
                return true;
    }
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    
}
