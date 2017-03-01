
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
    inst ASTEROIDS = 20;
    Asteroid[] ast = new Asteroid[ASTEROIDS]
    //create the bullet array
    int bullets = 10;
    Bullet[] bullet = new Bullet[BULLETS];
    int currentBullet = 0
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
        g2d.setTransform(identity)
        //erase the background
        g2d.setPaint(Color.BLACK)
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
        g2d.setTransformation(identity);
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
                g2d.setTransform(idenity);
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
}
