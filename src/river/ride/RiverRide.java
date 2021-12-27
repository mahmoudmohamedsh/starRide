package river.ride;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.swing.JFrame;


public class RiverRide extends JFrame{
    static Animator animator;
    public static void main(String[] args) {
        // TODO code application logic here
        new RiverRide();
    }
    
    public RiverRide() {
        GLCanvas glcanvas;
        Rlistener listener = new Rlistener();
        glcanvas = new GLCanvas();
        glcanvas.addGLEventListener(listener);
        glcanvas.addKeyListener(listener);
        glcanvas.addMouseListener(listener);
        getContentPane().add(glcanvas, BorderLayout.CENTER);
        animator = new FPSAnimator(30);
        animator.add(glcanvas);
        animator.start();

        setTitle("Anim Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,770);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        setFocusable(true);
        glcanvas.requestFocus();
    }
    
    public void exit(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
