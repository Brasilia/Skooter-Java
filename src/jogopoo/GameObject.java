/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import Core.ImageHandler;
import Core.InputHandler;
import Core.Vector2D;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;



public class GameObject implements Cloneable, Serializable{ //Classe "Elemento", solicitada no trabalho
    protected transient BufferedImage img;
    protected String imgPath;
    protected transient FileChooserThread fileChooser;
 
    public enum Direction{
        UP(2,"UP"), DOWN(8,"DOWN"), LEFT(4,"LEFT"), RIGHT(6,"RIGHT"), NEUTRAL(10,"NEUTRAL");
        public final int toInt;
        public final String toString;
        private Direction(int value, String s) {
            this.toInt = value;
            this.toString = s;
        }
        static public Direction intToDirection(int i){
            if(Direction.UP.toInt == i) return UP;
            else if (Direction.DOWN.toInt == i) return DOWN;
            else if (Direction.LEFT.toInt == i) return LEFT;
            else if (Direction.RIGHT.toInt == i) return RIGHT;
            else return NEUTRAL;
        }
        public int getX(){
            switch(this){
                case LEFT:
                    return -1;
                case RIGHT:
                    return 1;         
            }
            return 0; //NEUTRAL, UP or DOWN
        }
        public int getY(){
            switch(this){
                case UP:
                    return -1;
                case DOWN:
                    return 1;      
            }
            return 0; //NEUTRAL, LEFT, RIGHT
        }
        
    }
     
    public Vector2D transform;
    
    public GameObject (){
        this.transform = new Vector2D();
    }
    
    public GameObject (Vector2D v2){
        this.transform = new Vector2D(v2);
    }
    
    public GameObject (Vector2D v2, String spriteName){
        this.transform = new Vector2D(v2);
        setSprite(spriteName);
    }
    
    public void firstUpdate(){ //primeiro na sequência de updates
        
    }
    
    public void earlyUpdate(){ //chamado antes do update()
        
    }
    
    public void update(){
    
    }
    
    public void lateUpdate(JFrame parent){ //chamado depois do update
        
    }
    
    public void render(Graphics2D g){
        if (img != null)
            g.drawImage(img, (int)transform.getX(), (int)transform.getY(), null);
        else if (imgPath != null)
            setSprite(imgPath);
    }
    
//    public void setSprite(String imgName){
//        try {
//            this.img = ImageHandler.getInstance().loadImage(imgName);
//        } catch (IOException ex) {
//            Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    public void setSprite(String imgName){
        imgPath = imgName;
        try {
            this.img = ImageHandler.getInstance().loadImage(imgPath);
        } catch (IOException ex) {
            Logger.getLogger(GameObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean mouseContains(){
        return false;
    }
    
}
