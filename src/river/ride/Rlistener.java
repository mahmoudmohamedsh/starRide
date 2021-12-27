package river.ride;

import com.sun.opengl.util.j2d.TextRenderer;
import static com.sun.org.apache.xalan.internal.lib.ExsltSets.difference;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import texture.TextureReader;

/**
 *
 * @author Genius
 */
public class Rlistener implements GLEventListener, KeyListener, MouseListener {

    String textureNames[] = {
        "stars.png", "enemy.png", "stars2.png", //2
        "live.png", "bunny2.png", "steady.png", //5
        "right.png", "left.png", "boom1.png", //8
        "boom2.png", "boom3.png", "boom4.png", //11
        "boom5.png", "boom6.png", "boom7.png", //14
        "bullet.png", "background.png", "1player.png", //17
        "2player.png", "high_score.png", "instructions.png",//20
        "exit.png", "easy.png", "normal.png",//23
        "hard.png", "m.png", "um.png", //26
        "back.png", "instructions1.png"//28
    };
    GL gl;
    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    int textures[] = new int[textureNames.length];
    static Sprite player1;
    static Sprite player2;
    static ArrayList<Sprite> backGround = new ArrayList<>();
    static ArrayList<Sprite> enemies = new ArrayList<>();
    static ArrayList<Sprite> liveIcons = new ArrayList<>();
    static ArrayList<Sprite> Bullets = new ArrayList<>();
    static ArrayList<Sprite> Bulletsp2 = new ArrayList<>();
    static int respawnEnemyTime = 30;
    static int timer = 0;
    static boolean pause = false;
    static int lives = 3;
    static boolean gameOver = false;
    static int frame = 0;
    static int crushStartIndex = 8;
    static int crushCurrentIndex = crushStartIndex;
    static int crushEndIndex = 15;
    static int gameType = 0; // 0mainmenu 5oneplayer 6twoplayer
    static int difficalty = 0;
    static int fuel = 100;
    static int highScoreS = 500;
    static int highScoreC = highScoreS;
    static Sprite instructions;
    static ArrayList<Button> buttons = new ArrayList<>();
    static ArrayList<Button> diffButtons = new ArrayList<>();
    static ArrayList<Button> soundBackButtons = new ArrayList<>();
    static SimpleAudioPlayer shootSound = null;
    static SimpleAudioPlayer gameSound = null;
    static SimpleAudioPlayer crashSound = null;
    static SimpleAudioPlayer clickedSound = null;
    TextRenderer renderer = new TextRenderer(new Font("SanasSerif", Font.BOLD, 25));
    TextRenderer renderer1 = new TextRenderer(new Font("SanasSerif", Font.BOLD, 30));
    static boolean muted = false;

    static ArrayList<Button> pauseMenu = new ArrayList<>();

    @Override
    public void init(GLAutoDrawable gld) {

        gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    //This Will Clear The Background Color To Black

        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable Texture Mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);

        for (int i = 0; i < textureNames.length; i++) {
            try {
                texture[i] = TextureReader.readTexture("Assets" + "//" + textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

//                mipmapsFromPNG(gl, new GLU(), texture[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format,
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Imagedata
                );
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        backGround.add(new Sprite(gl, 50, 50, textures[0], 1f, 1f, 0));
        backGround.add(new Sprite(gl, 50, 150, textures[0], 1f, 1f, 0));
        liveIcons.add(new Sprite(gl, 6, 91, textures[3], 0.05f, 0.08f, 0));
        liveIcons.add(new Sprite(gl, 12, 91, textures[3], 0.05f, 0.08f, 0));
        liveIcons.add(new Sprite(gl, 18, 91, textures[3], 0.05f, 0.08f, 0));
        player1 = new Sprite(gl, 60, 15, textures[5], 0.18f, 0.16f, 0);
        player2 = new Sprite(gl, 40, 15, textures[5], 0.18f, 0.16f, 0);
        buttons.add(new Button(gl, 25, 90, textures[17], 0.4f, 0.2f, "singlePlayer"));
        buttons.add(new Button(gl, 25, 70, textures[18], 0.4f, 0.2f, "multiPlayer"));
        buttons.add(new Button(gl, 25, 50, textures[19], 0.4f, 0.2f, "highScore"));
        buttons.add(new Button(gl, 25, 30, textures[20], 0.4f, 0.2f, "instructions"));
        buttons.add(new Button(gl, 25, 10, textures[21], 0.4f, 0.2f, "exit"));
        diffButtons.add(new Button(gl, 25, 70, textures[22], 0.4f, 0.2f, "Easy"));
        diffButtons.add(new Button(gl, 25, 50, textures[23], 0.4f, 0.2f, "Normal"));
        diffButtons.add(new Button(gl, 25, 30, textures[24], 0.4f, 0.2f, "Hard"));
        soundBackButtons.add(new Button(gl, 93, 93, textures[25], 0.14f, 0.14f, "muted"));
        soundBackButtons.add(new Button(gl, 93, 93, textures[26], 0.14f, 0.14f, "unMuted"));
        soundBackButtons.add(new Button(gl, 75, 93, textures[27], 0.14f, 0.14f, "back"));
        instructions = new Sprite(gl, 25, 50, textures[28], 0.4f, 0.8f, 0);
//        pauseMenu.add(new Button(gl, 75, 50, textures[23], 0.4f, 0.2f, "pausemenu restart"));
        pauseMenu.add(new Button(gl, 50, 50, textures[27], 0.4f, 0.4f, "pausemenu exit"));
        try {
            gameSound = new SimpleAudioPlayer(true, "Audios\\game.wav");
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    boolean check = true;

    @Override
    public void display(GLAutoDrawable gld) {
        if (frame == 30) {
            frame = 0;
        }
        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);       //Clear The Screen And The Depth Buffer
        gl.glLoadIdentity();
        Button b = soundBackButtons.get(2);
        Button m = soundBackButtons.get(0);
        Button um = soundBackButtons.get(1);
        if (gameType == 5) {
            //show elements
            for (int i = 0; i < Rlistener.backGround.size(); i++) {
                respawnSprite(Rlistener.backGround.get(i));
            }
            for (int i = 0; i < Rlistener.enemies.size(); i++) {
                respawnSprite(Rlistener.enemies.get(i));
            }
            for (int i = 0; i < Rlistener.liveIcons.size(); i++) {
                respawnSprite(Rlistener.liveIcons.get(i));
            }
            if (!gameOver) {
                if (!pause) {
                    for (int i = 0; i < Rlistener.Bullets.size(); i++) {
                        respawnSprite(Bullets.get(i));
                    }
                    respawnSprite(player1);

                    backGroundLoop();
                    try {
                        hit();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    bulletsMove();
                    if (timer >= respawnEnemyTime) {
                        timer = 0;
                        createEnemy(gl);
                    }
                    timer++;

                    if (!enemies.isEmpty()) {
                        enemiesFall(false);
                    }
                    player1 = handleKeyPress(player1, 1);
                    updateScore(gld);
                    updateFuel();
                }
            } else {
                //if gameover
                // [x] 1. render player score
                // [x] 2. cruch animation
                // [x] 3. show menu currently only back button 
                // [-] 4. check high score once 
                renderer1.beginRendering(gld.getWidth(), gld.getHeight());
                renderer1.draw("High Scores : " + player1.getScore(), 200, 600);
                renderer1.endRendering();

                if (crushCurrentIndex < crushEndIndex && gameOver) {
                    gameOver(crushCurrentIndex);
                    crushCurrentIndex++;
                    respawnSprite(player1);

                }
//                
                if (check) {
                    checkscore();
                    check = !check;
                }
                showFuel();
                showMenubutttons(gl);
            }
        } else if (gameType == 6) {
            //show elements
            for (int i = 0; i < Rlistener.backGround.size(); i++) {
                respawnSprite(Rlistener.backGround.get(i));
            }
            for (int i = 0; i < Rlistener.enemies.size(); i++) {
                respawnSprite(Rlistener.enemies.get(i));
            }
            for (int i = 0; i < Rlistener.liveIcons.size(); i++) {
                respawnSprite(Rlistener.liveIcons.get(i));
            }
            if (!gameOver) {
                if (!pause) {
                    for (int i = 0; i < Rlistener.Bullets.size(); i++) {
                        respawnSprite(Bullets.get(i));
                    }
                    for (int i = 0; i < Bulletsp2.size(); i++) {
                        respawnSprite(Bulletsp2.get(i));
                    }
                    respawnSprite(player1);
                    respawnSprite(player2);
                    backGroundLoop();
                    try {
                        hit();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    bulletsMove();
                    bulletsMovep2();

                    if (timer >= respawnEnemyTime) {
                        timer = 0;
                        createEnemy(gl);
                    }
                    timer++;

                    if (!enemies.isEmpty()) {
                        enemiesFall(false);
                    }
                    player1 = handleKeyPress(player1, 1);
                    player2 = handleKeyPress(player2, 2);
                    updateScore(gld);
                    updateFuel();
                }
            } else {
                //if gameover
                // [x] 1. render player score
                // [x] 2. cruch animation
                // [x] 3. show menu currently only back button 
                // [-] 4. check high score once 
                renderer1.beginRendering(gld.getWidth(), gld.getHeight());
                renderer1.draw("High Scores : " + player1.getScore(), 200, 600);
                renderer1.endRendering();

                if (crushCurrentIndex < crushEndIndex && gameOver) {
                    gameOver(crushCurrentIndex);
                    crushCurrentIndex++;
                    respawnSprite(player1);

                }
//                
                showFuel();
                showMenubutttons(gl);
            }
        } else if (gameType == 0) {
            DrawBackground(gl);
            for (int i = 0; i < buttons.size(); i++) {
                respawnSprite(buttons.get(i));
            }
            if (muted) {
                m.drawButton();
            } else {
                um.drawButton();
            }
        } else if (gameType == 1 || gameType == 2) {//single player1
            DrawBackground(gl);
            for (int i = 0; i < diffButtons.size(); i++) {
                respawnSprite(diffButtons.get(i));
            }
            b.drawButton();
            if (muted) {
                m.drawButton();
            } else {
                um.drawButton();
            }
        } else if (gameType == 3) {
            DrawBackground(gl);
            instructions.DrawSprite();
            b.drawButton();
            if (muted) {
                m.drawButton();
            } else {
                um.drawButton();
            }
        } else if (gameType == 4) {//high score
            DrawBackground(gl);
            String[] highScores = ReadFile.readfile();
            renderer1.beginRendering(gld.getWidth(), gld.getHeight());
            renderer1.draw("High Scores : ", 50, highScoreC);
            for (String score : highScores) {
                if (score != null) {
                    highScoreC -= 50;
                    renderer1.draw(score, 50, highScoreC);
                }
            }
            renderer1.endRendering();
            highScoreC = highScoreS;
            soundBackButtons.get(2).drawButton();
            if (muted) {
                m.drawButton();
            } else {
                um.drawButton();
            }

        }
        frame++;
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
    }

    @Override
    public void displayChanged(GLAutoDrawable glad, boolean bln, boolean bln1) {
    }

    public Sprite handleKeyPress(Sprite sprite, int playernum) {
        if (playernum == 1) {
            if (isKeyPressed(KeyEvent.VK_LEFT)) {
                if (sprite.getX() > sprite.getxScale() * consts.maxWidth / 2) {
                    sprite.setX(sprite.getX() - 2);
                    player1.setTexture(textures[7]);
                }
            }
            if (isKeyPressed(KeyEvent.VK_RIGHT)) {
                if (sprite.getX() < consts.maxWidth - sprite.getxScale() * consts.maxWidth / 2) {
                    sprite.setX(sprite.getX() + 2);
                    player1.setTexture(textures[6]);
                }
            }
            /*if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (sprite.getX() < consts.maxWidth-sprite.) {
                x++;
            }
        }*/
            if (isKeyPressed(KeyEvent.VK_DOWN)) {

            }
            if (isKeyPressed(KeyEvent.VK_SPACE)) {
                firep1();
            }
            if (isKeyPressed(KeyEvent.VK_UP)) {
                backGroundLoop();
                enemiesFall(true);
            }
        } else {
            if (isKeyPressed(KeyEvent.VK_A)) {
                if (sprite.getX() > sprite.getxScale() * consts.maxWidth / 2) {
                    sprite.setX(sprite.getX() - 2);
                    player2.setTexture(textures[7]);
                }
            }
            if (isKeyPressed(KeyEvent.VK_D)) {
                if (sprite.getX() < consts.maxWidth - sprite.getxScale() * consts.maxWidth / 2) {
                    sprite.setX(sprite.getX() + 2);
                    player2.setTexture(textures[6]);
                }
            }
            if (isKeyPressed(KeyEvent.VK_CONTROL)) {
                firep2();
            }
        }
        return sprite;

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public BitSet keyBits = new BitSet(256);

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keyBits.set(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keyBits.clear(keyCode);
        if (!pause) {
            if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_A) {
                player2.setTexture(textures[5]);
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
                player1.setTexture(textures[5]);
            }

        }
    }

    public boolean isKeyPressed(final int keyCode) {
        return keyBits.get(keyCode);
    }

    private void respawnSprite(Sprite Sprite) {
        Sprite.DrawSprite();
    }

    private void respawnSprite(Button button) {
        button.drawButton();
    }

    private void createEnemy(GL gl) {
        int x = (int) (Math.random() * 91) + 6;
        int y = 105;
        Sprite enemy = new Sprite(gl, x, y, textures[1], 0.09f, 0.09f, (int) (Math.random() * 2));
        Rlistener.enemies.add(enemy);

    }

    private void enemiesFall(boolean up) { // move enemy handle enemy colide with player
        if (!enemies.isEmpty() && !liveIcons.isEmpty()) {
            for (int i = 0; i < enemies.size(); i++) {
                Sprite enemy = enemies.get(i);
                if (difficalty == 1 || difficalty == 2 || difficalty == 3) {
                    //level 1
                    enemy.setY(enemy.getY() - 1);
                    //sharpedite
                    if (i < 4) {
                        if (player1.colideWith(enemy)) {
                            // lose 1 live
                            enemies.remove(enemy);
                            loseOneLive(1);
                        }
                        if (gameType == 6 && player2.colideWith(enemy)) {

                            // lose 1 live
                            enemies.remove(enemy);
                            loseOneLive(2);

                        }

                        //sharp
                        if (enemy.getY() < -20) {
                            Rlistener.enemies.remove(enemy);
                        }
                    }
                    /////////////////////////////////////////////level 1
                    //level 2
                    if (difficalty == 2 || difficalty == 3) {
                        //check and change direction
                        if (enemy.getX() > 94 && Rlistener.enemies.get(i).getMoveDirection() == 1) {
                            Rlistener.enemies.get(i).setMoveDirection(0);
                        } else if (enemy.getX() < 6 && Rlistener.enemies.get(i).getMoveDirection() == 0) {
                            Rlistener.enemies.get(i).setMoveDirection(1);
                        }

                        if (enemy.getY() < 95 && !enemies.isEmpty()) {
                            if (!up) {
                                //move left and right

                                if (Rlistener.enemies.get(i).getMoveDirection() == 0) {
                                    Rlistener.enemies.get(i).setX(Rlistener.enemies.get(i).getX() - 2);
                                } else {
                                    Rlistener.enemies.get(i).setX(Rlistener.enemies.get(i).getX() + 2);
                                }

                            } else {
                                if (Rlistener.enemies.get(i).getMoveDirection() == 0) {
                                    Rlistener.enemies.get(i).setX(Rlistener.enemies.get(i).getX() - 1);
                                } else {
                                    Rlistener.enemies.get(i).setX(Rlistener.enemies.get(i).getX() + 1);
                                }
                            }
                        }
                    }
                    /////////////////////////////////level 2
                    if (difficalty == 3) {
                        //level 3
                        if (enemy.getY() < 95 && !enemies.isEmpty()) {
                            if (player1.getX() > enemies.get(i).getX()) {
                                //move left and right
                                if (player1.getX() - enemies.get(i).getX() >= 15) {
                                    enemies.get(i).setMoveDirection(1);
                                }
                            } else {
                                if (player1.getX() - enemies.get(i).getX() <= -15) {
                                    enemies.get(i).setMoveDirection(0);
                                }
                            }
                        }
                    }
                    ////////////////////////////////level 3
                }
            }
        }
    }

    private void backGroundLoop() {
        if (!backGround.isEmpty()) {
            for (int i = 0; i < backGround.size(); i++) {
                Sprite background = backGround.get(i);
                background.setY(background.getY() - 1);
                if (background.getY() <= -50) {
                    Rlistener.backGround.get(i).setY(150);
                }
            }
        }
    }

//    private ArrayList<Sprite> bulletMove(ArrayList<Sprite> bullets) {
//        for (Sprite bullet : bullets) {
//            bullet.setY(bullet.getY() + 1);
//            respawnSprite(bullet);
//        }
//        return bullets;
//    }
//private ArrayList<Sprite> bulletMovep2(ArrayList<Sprite> bullets) {
//        for (Sprite bullet : Bulletsp2) {
//            bullet.setY(bullet.getY() + 1);
//            respawnSprite(bullet);
//        }
//        return bullets;
//    }
    private void firep1() {

        if (Bullets.size() <= 10) {

            Sprite bullet = new Sprite(player1.gl, player1.getX(),
                    player1.getY() + 1,
                    textures[15], 0.05f, 0.05f, 3);
            Bullets.add(bullet);
            if (!muted) {
                try {
                    shootSound = new SimpleAudioPlayer(false, "Audios\\shoot.wav");
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                }
                shootSound.play();
            }
        }
    }

    private void firep2() {
        System.out.println(Bulletsp2.size());
        if (Bulletsp2.size() <= 10) {

            Sprite bullet = new Sprite(gl, player2.getX(),
                    player2.getY() + 1,
                    textures[15], 0.05f, 0.05f, 3);
            Bulletsp2.add(bullet);
            if (!muted) {
                try {
                    shootSound = new SimpleAudioPlayer(false, "Audios\\shoot.wav");
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                }
                shootSound.play();
            }
        }
    }

    private void bulletsMove() {
        Sprite bullet1 = null;
        for (Sprite bullet : Bullets) {
            bullet.setY(bullet.getY() + 1);
            if (bullet.getY() > 102) {
                bullet1 = bullet;
            }
        }
        Bullets.remove(bullet1);
    }

    private void bulletsMovep2() {
        Sprite bullet1 = null;
        for (Sprite bullet : Bulletsp2) {
            bullet.setY(bullet.getY() + 1);
            if (bullet.getY() > 102) {
                bullet1 = bullet;
            }
        }
        Bulletsp2.remove(bullet1);
    }

    private void hit() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Sprite enemy1 = null;
        Sprite bullet1 = null;

        Sprite enemy2 = null;
        Sprite bullet2 = null;
        for (Sprite enemy : enemies) {
            for (Sprite bullet : Bullets) {
                if (bullet.colideWith(enemy)) {
                    enemy1 = enemy;
                    bullet1 = bullet;
                }
            }
            for (Sprite bullet : Bulletsp2) {
                if (bullet.colideWith(enemy)) {
                    enemy2 = enemy;
                    bullet2 = bullet;
                }
            }
        }
        if (enemy1 != null) {
            enemies.remove(enemy1);
            Bullets.remove(bullet1);
        }
        if (enemy2 != null) {
            enemies.remove(enemy2);
            Bulletsp2.remove(bullet2);
        }
        if (!muted) {
            shootSound = new SimpleAudioPlayer(false, "Audios\\shoot.wav");
        }

    }

    private void gameOver(int index) {

        crush(index);

    }

    private void crush(int i) {
        player1.setTexture(textures[i]);

    }

//    private void restart() {
//        backGround = new ArrayList<>();
//        liveIcons = new ArrayList<>();
//        enemies = new ArrayList<>();
//        backGround.add(new Sprite(player1.gl, 50, 50, textures[0], 1f, 1f, 0));
//        backGround.add(new Sprite(player1.gl, 50, 150, textures[0], 1f, 1f, 0));
//        liveIcons.add(new Sprite(player1.gl, 6, 91, textures[6], 0.05f, 0.08f, 0));
//        liveIcons.add(new Sprite(player1.gl, 12, 91, textures[6], 0.05f, 0.08f, 0));
//        liveIcons.add(new Sprite(player1.gl, 18, 91, textures[6], 0.05f, 0.08f, 0));
//        player1 = new Sprite(player1.gl, 10, 10, textures[5], 0.18f, 0.16f, 0);
//        lives = 3;
//        gameOver = false;
//        crushCurrentIndex = crushStartIndex;
//        timer = 0;
//        frame = 0;
//        pause = false;
//        fuel = 100;
//    }
    private ArrayList<Sprite> destroy(Sprite sprite, ArrayList<Sprite> array) {
        array.remove(sprite);
        return array;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = (int) (e.getX() / 6.0);
        int y = (int) (e.getY() / 770.0 * 100);
        Button b = soundBackButtons.get(2);
        Button m = soundBackButtons.get(0);
        Button um = soundBackButtons.get(1);
        if (gameType == 0) {
            for (Button button : buttons) {
                if (button.isClicked(x, y)) {
                    switch (button.getName()) {
                        case "singlePlayer":
                            gameType = 1;
                            break;
                        case "multiPlayer":
                            gameType = 2;
                            break;
                        case "instructions":
                            gameType = 3;
                            break;
                        case "highScore":
                            gameType = 4;
                            break;
                        case "exit": {
                            try {
                                clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                            } catch (UnsupportedAudioFileException ex) {
                                Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (LineUnavailableException ex) {
                                Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            clickedSound.play();
                            System.exit(0);
                        }
                        break;

                    }
                    if (!muted) {
                        try {
                            clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                            clickedSound.play();
                        } catch (UnsupportedAudioFileException ex) {
                            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (LineUnavailableException ex) {
                            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                if (muted) {
                    m.drawButton();
                    if (m.isClicked(x, y)) {
                        mute(false);

                    }
                } else {
                    um.drawButton();
                    if (m.isClicked(x, y)) {
                        mute(true);
                    }
                }
            }
        } else if (gameType == 1) {
            for (Button button : diffButtons) {
                if (button.isClicked(x, y)) {
                    switch (button.getName()) {
                        case "Easy":
                            difficalty = 1;
                            gameType = 5;
                            break;
                        case "Normal":
                            difficalty = 2;
                            gameType = 5;
                            break;
                        case "Hard":
                            difficalty = 3;
                            gameType = 5;
                            break;
                    }
                    if (!muted) {
                        try {
                            clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                            clickedSound.play();
                        } catch (UnsupportedAudioFileException ex) {
                            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (LineUnavailableException ex) {
                            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            b.drawButton();
            if (muted) {
                m.drawButton();
                if (m.isClicked(x, y)) {
                    mute(false);
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                um.drawButton();
                if (m.isClicked(x, y)) {
                    mute(true);
                }
            }

            if (b.isClicked(x, y)) {
                gameType = 0;
                if (!muted) {
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        } else if (gameType == 2) {// two player diff screen handler "changed by gaber"

            for (Button button : diffButtons) {
                if (button.isClicked(x, y)) {
                    switch (button.getName()) {
                        case "Easy":
                            difficalty = 1;
                            gameType = 6;
                            break;
                        case "Normal":
                            difficalty = 2;
                            gameType = 6;
                            break;
                        case "Hard":
                            difficalty = 3;
                            gameType = 6;
                            break;
                    }
                    if (!muted) {
                        try {
                            clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                            clickedSound.play();
                        } catch (UnsupportedAudioFileException ex) {
                            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (LineUnavailableException ex) {
                            Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            b.drawButton();
            if (muted) {
                m.drawButton();
                if (m.isClicked(x, y)) {
                    mute(false);
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                um.drawButton();
                if (m.isClicked(x, y)) {
                    mute(true);
                }
            }

            if (b.isClicked(x, y)) {
                gameType = 0;
                if (!muted) {
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else if (gameType == 3) {
            b.drawButton();
            if (b.isClicked(x, y)) {
                gameType = 0;
                if (!muted) {
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (muted) {
                m.drawButton();
                if (m.isClicked(x, y)) {
                    mute(false);
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                um.drawButton();
                if (m.isClicked(x, y)) {
                    mute(true);
                }
            }
        } else if (gameType == 4) {
            b.drawButton();
            if (muted) {
                m.drawButton();
                if (m.isClicked(x, y)) {
                    mute(false);
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                um.drawButton();
                if (m.isClicked(x, y)) {
                    mute(true);
                }
            }
            if (b.isClicked(x, y)) {
                gameType = 0;
                if (!muted) {
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        } else if (gameType == 3) {
            b.drawButton();

            if (b.isClicked(x, y)) {
                gameType = 0;
                if (!muted) {
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (muted) {
                m.drawButton();
                if (m.isClicked(x, y)) {
                    mute(false);
                    try {
                        clickedSound = new SimpleAudioPlayer(false, "Audios\\button.wav");
                        clickedSound.play();
                    } catch (UnsupportedAudioFileException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                um.drawButton();
                if (m.isClicked(x, y)) {
                    mute(true);
                }
            }
        }
        if ((gameType == 5 && gameOver) || (gameType == 6 && gameOver)) {
            // handle the pause menu button when gameover
            // restart the in game probrties and back to main menu with game type = 0
            for (Button a : pauseMenu) {
                if (a.isClicked(x, y)) {
                    enemies = new ArrayList<>();
                    liveIcons = new ArrayList<>();
                    Bullets = new ArrayList<>();
                    Bulletsp2 = new ArrayList<>();

                    timer = 0;
                    pause = false;
                    lives = 3;
                    gameOver = false;
                    frame = 0;
                    crushCurrentIndex = crushStartIndex;

                    gameType = 0;
                    difficalty = 0;

                    liveIcons.add(new Sprite(gl, 6, 91, textures[3], 0.05f, 0.08f, 0));
                    liveIcons.add(new Sprite(gl, 12, 91, textures[3], 0.05f, 0.08f, 0));
                    liveIcons.add(new Sprite(gl, 18, 91, textures[3], 0.05f, 0.08f, 0));
                    player1 = new Sprite(gl, 50, 15, textures[5], 0.18f, 0.16f, 0);
                    player2 = new Sprite(gl, 40, 15, textures[5], 0.18f, 0.16f, 0);
                }
            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void showScore(GLAutoDrawable gld) {
        renderer.beginRendering(gld.getWidth(), gld.getHeight());
        renderer.draw("score : " + player1.getScore(), 250, 700);
        renderer.endRendering();
    }

    private void updateScore(GLAutoDrawable gld) {
        /*TextRenderer renderer = new TextRenderer(new Font("SanasSerif", Font.BOLD, 20));
        player1.setScore(player1.getScore() + 1);
        renderer.draw("score : " + player1.getScore(), 50, 50);
        renderer.endRendering();*/

        player1.setScore(player1.getScore() + 1);
        showScore(gld);

    }

    public void DrawBackground(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[16]);	// Turn Blending On

        gl.glPushMatrix();
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

    private void updateFuel() {
        fuel--;
    }

    private void showFuel() {

    }

    private void loseOneLive(int playernum) {
        pause = true;
        liveIcons.remove(liveIcons.size() - 1);
        if (!liveIcons.isEmpty()) {
            if (playernum == 1) {
                player1.setX(50);
            } else {
                player2.setX(50);
            }
            Bullets = new ArrayList<>();
            frame = 0;
            lives--;
            fuel = 100;
            resume();
        } else {
            gameOver = true;
            if (!muted) {
                try {
                    crashSound = new SimpleAudioPlayer(false, "Audios\\crash.wav");
                    crashSound.play();
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void resume() {
        int i = 1000;
        while (--i != 0) {
            pause = true;
        }
        pause = false;
    }

    private void lose() {

    }

    private void win() {

    }

    private void mute(Boolean b) {
        muted = b;
        if (b) {
            if (gameSound != null) {
                try {
                    gameSound.stop();
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } else {
            try {
                gameSound = new SimpleAudioPlayer(true, "Audios\\game.wav");
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
            } catch (LineUnavailableException ex) {
                Logger.getLogger(Rlistener.class.getName()).log(Level.SEVERE, null, ex);
            }
            gameSound.play();
        }

    }

    private void showMenubutttons(GL gl) {
        for (Button b : pauseMenu) {
            b.drawButton();

        }
    }

    private void checkscore() {
        String hscores[] = ReadFile.readfile();
        for (int i = 0; i < hscores.length; i++) {
            if (player1.getScore() > Integer.parseInt(hscores[i].split("-")[1])) {
                ReadFile.writefile("playername", player1.getScore());
                break;
            }
        }
    }
}
