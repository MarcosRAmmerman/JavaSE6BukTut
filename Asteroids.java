
/**
 * Write a description of class Asteroids here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;

public class Asteroids extends Applet implements Runnable, KeyListener
{
    //the main thread becomes gameloop
    Thread gameloop;
    // use this as a double buffer
    BufferedImage backbuffer;
    //the main drawing object for the back buffer
    Graphics2D g2d;
    //toggle for drawing bounding boxes
    boolean showBounds = false;
    //create the asteroid array
    int ASTEROIDS = 20;
    Asteroid[] ast = new Asteroid[ASTEROIDS];
    //create the bullet array
    int BULLETS = 10;
    Bullet[] bullet = new Bullet[BULLETS];
    int currentBullet = 0;
    //the player's ship
    Ship ship = new Ship();
    //create the identity transform(0,0)
    AffineTransform identity = new AffineTransform();
    //creae a random number generator
    Random rand = new Random();
    
    //applet init event
    public void init()
    {
        //create the back buffer for smooth graphics
        backbuffer = new BufferedImage(640,480,BufferedImage.TYPE_INT_RGB);
        g2d = backbuffer.createGraphics();
        //set up the ship
        ship.setX(320);
        ship.setY(240);
        //set up the bullets
        for(int n = 0; n<BULLETS; n++)
        {
            bullet[n] = new Bullet();
        }
        //create the asteroids
        for (int n=0; n<ASTEROIDS; n++)
        {
            ast[n] = new Asteroid();
            ast[n].setRotationVelocity(rand.nextInt(3)+1);
            ast[n].setX((double)rand.nextInt(600)+20);
            ast[n].setY((double)rand.nextInt(440)+20);
            ast[n].setMoveAngle(rand.nextInt(360));
            double ang = ast[n].getMoveAngle()-90;
            ast[n].setVelX(calcAngleMoveX(ang));
            ast[n].setVelY(calcAngleMoveY(ang));
        }
        //starts the user input listener
        addKeyListener(this);
    }
    public void update(Graphics g)
    {
        //start of transforms at identity
        g2d.setTransform(identity);
        //erase the background
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0,0,getSize().width,getSize().height);
        //print some status information
        g2d.setColor(Color.WHITE);
        g2d.drawString("Ship: " + Math.round(ship.getX())+","+ Math.round(ship.getX()),5,10);
        g2d.drawString("Move Angle: " + Math.round(ship.getMoveAngle())+90,5,25);
        g2d.drawString("Face Angle: " + Math.round(ship.getFaceAngle()),5,40);
        //draw the game graphics
        drawShip();
        drawBullets();
        drawAsteroids();
        //repaint the applet window
        paint(g);
        }
    //drawShip called by applet update event
    public void drawShip()
    {
        g2d.setTransform(identity);
        g2d.translate(ship.getX(),ship.getY());
        g2d.rotate(Math.toRadians(ship.getFaceAngle()));
        g2d.setColor(Color.ORANGE);
        g2d.fill(ship.getShape());
    }
    //drawBullets called by applet update event
    public void drawBullets()
    {
        for(int n = 0;n<BULLETS; n++)
        {
            if(bullet[n].isAlive())
            {
                //draw the bullet
                g2d.setTransform(identity);
                g2d.translate(bullet[n].getX(),bullet[n].getY());
                g2d.setColor(Color.MAGENTA);
                g2d.draw(bullet[n].getShape());
            }
        }
    }
    //drawAsteroids called by applet update event
    public void drawAsteroids()
    {
        //iterate through the asteroids array
        for(int n = 0; n<ASTEROIDS;n++)
        {
            if(ast[n].isAlive())
            {
                //draw the Asteroid
                g2d.setTransform(identity);
                g2d.translate(ast[n].getX(),ast[n].getY());
                g2d.rotate(Math.toRadians(ast[n].getMoveAngle()));
                g2d.setColor(Color.DARK_GRAY);
                g2d.fill(ast[n].getShape());
            }
        }
    }
    //applet window repaint event- -draw the back buffer
    public void paint(Graphics g)
    {
        g.drawImage(backbuffer,0,0,this);
    }
    //thread start even - start the game loop running
    public void start()
    {
        //create the gameloop thread for real-time updates
        gameloop = new Thread(this);
        gameloop.start();
    }
    //thread run event (game loop)
    public void run()
    {
        //aquire the current thread
        Thread t = Thread.currentThread();
        //keep going as long as the thread is alive
        while(t==gameloop)
        {
            try
            {
                //update the gameloop
                gameUpdate();
                //target framerate is 50fps
                Thread.sleep(20);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            repaint();
            }
    }
    public void stop()
    {
            //kill the gameloop thread
            gameloop = null;
    }
    //move and animate the objects inthe game
    private void gameUpdate()
    {
        updateShip();
        updateBullets();
        updateAsteroids();
        checkCollisions();
    }
    //Update the ship's position based on veolcity
    public void updateShip()
    {
        ship.incX(ship.getVelX());
        //wrap around left/right
        if(ship.getX() < -10)
        {
            ship.setX(getSize().width+10);
        }
        else if(ship.getX() > getSize().width + 10)
        {
            ship.setX(-10);
        }
        //update ships Y position
        ship.incY(ship.getVelY());
        //wrap around top/bottom
        if(ship.getY()< -10)
        {
            ship.setY(getSize().height+10);
        }
        else if(ship.getY()>getSize().height+10)
        {
            ship.setY(-10);
        }
    }
    public void updateBullets()
    {
        for(int n = 0; n<BULLETS; n++)
        {
            if(bullet[n].isAlive())
            {
                bullet[n].incX(bullet[n].getVelX());
                if(bullet[n].getX()<0||bullet[n].getX()>getSize().width)
                {
                    bullet[n].setAlive(false);
                }
                bullet[n].incY(bullet[n].getVelY());
                if(bullet[n].getY()<0||bullet[n].getY()>getSize().height)
                {
                    bullet[n].setAlive(false);
                }
            }
        }
    }
    public void updateAsteroids()
    {
        for(int n = 0; n < ASTEROIDS; n++)
        {
            if(ast[n].isAlive())
            {
                ast[n].incX(ast[n].getVelX());
                if(ast[n].getX()<-20)
                {
                    ast[n].setX(getSize().width+20);
                }
                else if(ast[n].getX()>getSize().width+20)
                {
                    ast[n].setX(-20);
                }
                ast[n].incY(ast[n].getVelY());
                if(ast[n].getY()<-20)
                {
                    ast[n].setY(getSize().height+20);
                }
                else if(ast[n].getY()>getSize().height+20)
                {
                    ast[n].setY(-20);
                }
                ast[n].incMoveAngle(ast[n].getRotationVelocity());
                if(ast[n].getMoveAngle()<0)
                {
                    ast[n].setMoveAngle(360-ast[n].getRotationVelocity());
                }
                else if(ast[n].getMoveAngle()>360)
                {
                    ast[n].setMoveAngle(ast[n].getRotationVelocity());
                }
            }
        }
    }
    public void checkCollisions()
    {
        for(int m=0; m<ASTEROIDS;m++)
        {
            if(ast[m].isAlive())
            {
                for(int n = 0; n<BULLETS;n++)
                {
                    if(bullet[n].isAlive())
                    {
                        if(ast[m].getBounds().contains(bullet[n].getX(),bullet[n].getY()))
                        {
                            bullet[n].setAlive(false);
                            ast[m].setAlive(false);
                            continue;
                        }
                    }
                }
            }
            if(ast[m].getBounds().intersects(ship.getBounds()))
            {
                ast[m].setAlive(false);
                ship.setX(320);
                ship.setY(240);
                ship.setFaceAngle(0);
                ship.setVelX(0);
                ship.setVelY(0);
                continue;
            }
        }
    }
    public void keyReleased(KeyEvent k){}
    public void keyTyped(KeyEvent k){}
    public void keyPressed(KeyEvent k)
    {
        int keyCode = k.getKeyCode();
        switch(keyCode)
        {
            case KeyEvent.VK_LEFT:
                ship.incFaceAngle(-5);
                if(ship.getFaceAngle()<0)
                {
                    ship.setFaceAngle(355);
                }
                break;
            case KeyEvent.VK_RIGHT:
                ship.incFaceAngle(5);
                if(ship.getFaceAngle()>360)
                {
                    ship.setFaceAngle(5);
                }
                break;
            case KeyEvent.VK_UP:
                ship.setMoveAngle(ship.getFaceAngle()-90);
                ship.incVelX(calcAngleMoveX(ship.getMoveAngle())*0.1);
                ship.incVelY(calcAngleMoveY(ship.getMoveAngle())*0.1);
                break;
            case KeyEvent.VK_CONTROL:
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                currentBullet++;
                if(currentBullet>BULLETS-1)
                {
                    currentBullet=0;
                    bullet[currentBullet].setAlive(true);
                }
                bullet[currentBullet].setX(ship.getX());
                bullet[currentBullet].setY(ship.getY());
                bullet[currentBullet].setMoveAngle(ship.getFaceAngle()-90);
                double angle = bullet[currentBullet].getMoveAngle();
                double svx = ship.getVelX();
                double svy = ship.getVelY();
                bullet[currentBullet].setVelX(svx+calcAngleMoveX(angle*2));
                bullet[currentBullet].setVelY(svy+calcAngleMoveY(angle*2));
                break;
        }
    }
    public double calcAngleMoveX(double angle)
    {
        return (double)(Math.cos(angle*Math.PI/180));
    }
    public double calcAngleMoveY(double angle)
    {
        return (double)(Math.sin(angle*Math.PI/180));
    }
}
