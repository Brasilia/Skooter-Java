/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

//FIXME Temporario
import Core.InputHandler;
import Core.Vector2D;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.Math.abs;
import javax.swing.JFrame;

/**
 *
 * @author Rafael
 */
public abstract class MapObject extends GameObject implements Serializable{
   
    
    
    public int posX;
    public int posY;
    public int destX;
    public int destY;
    boolean isMoving;
    protected double speed = 1.5;
    protected MapCollider collider;
    protected MapObjectController controller;
    protected boolean isPushable;
    protected boolean isBreakable;
    protected Direction lookDir;
    protected boolean toRemove;
    
    public MapObject(int x, int y) {
        super((new Vector2D(x, y)).scale(GameMap.TILE_SIZE) );
        this.setSprite("redBlock.png");
        isMoving = false;
        destX = posX = x;
        destY = posY = y;
        controller = new MapObjectController();
        setCollider();
        isPushable = false;
        isBreakable = false;
        //speed = 1;
        lookDir = Direction.DOWN;
        toRemove = false;
        controller.speed = speed;
    }
    
    public boolean move(GameObject.Direction dir){
        if (isMoving || dir == Direction.NEUTRAL) return false;
        //if !isMoving && !emptyTile(posX+dir.x, posY+dir.y), GO ON
        switch (dir){
            case UP:
                if(canMoveTo(posX, posY-1)) destY --;
                lookDir = Direction.UP;
                break;
            case DOWN:
                if(canMoveTo(posX, posY+1)) destY ++;
                lookDir = Direction.DOWN;
                break;
            case LEFT:
                if(canMoveTo(posX-1, posY)) destX --;
                lookDir = Direction.LEFT;
                break;
            case RIGHT:
                if(canMoveTo(posX+1, posY)) destX ++;
                lookDir = Direction.RIGHT;
                break;
        }
        isMoving = posX != destX || posY != destY;
        GameMap.getInstance().moveObjectFromTo(this, posX, posY, destX, destY);
        return isMoving;
    }
    
    public void push(MapObject obj, Direction dir){
        if(!obj.isPushable) return;
        strongPush(obj, dir);
    }
    
    public void strongPush(MapObject obj, Direction dir){
        //Cancela push se o tile destino estiver bloqueado
        switch(dir){
            case UP:
                if(!obj.canMoveTo(obj.posX, obj.posY-1)) return;
                break;
            case DOWN:
                if(!obj.canMoveTo(obj.posX, obj.posY+1)) return;
                break;
            case LEFT:
                if(!obj.canMoveTo(obj.posX-1, obj.posY)) return;
                break;
            case RIGHT:
                if(!obj.canMoveTo(obj.posX+1, obj.posY)) return;
                break;
        }
        obj.controller.makeInput(dir, false);
        if (!obj.isMoving) obj.controller.speed = this.speed;
    }
    
    public void push(int x, int y, Direction dir){
        for (MapObject obj : GameMap.getInstance().objectsAt(x, y)){
            if (obj != this){
                push(obj, dir);
            }
        }
    }
    
    public void push(Direction dir){
        push(posX+dir.getX(), posY+dir.getY(), dir);
    }
    
    public ArrayList<MapObject> objectsOnSameTile(){
        //ArrayList<MapObject> objs = GameMap.getInstance().objectsAt(posX, posY);
        ArrayList<MapObject> objs = new ArrayList<>();
        for(MapObject obj : GameMap.getInstance().objectsAt(destX, destY)){
            if (obj != this){
                try {
                    objs.add((MapObject) obj.clone());
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(MapObject.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return objs;
    }
    
    @Override
    public void firstUpdate(){
        controller.reset(); //necessário para apagar o estado do tick anterior
    }

    @Override
    public void update() {
        //Solicita para mover-se na direção especificada pelo controlador
        double curSpeed = controller.speed;
        move(controller.getDirection());
        //Make move
        if (isMoving){
            transform.translate( new Vector2D((destX - posX) * curSpeed, 0));
            transform.translate( new Vector2D(0, (destY - posY) * curSpeed));
            //Chegou ao destino
            if (abs(posX - transform.getRelativeX()/GameMap.TILE_SIZE) >= 1 
                    || abs(posY - transform.getRelativeY()/GameMap.TILE_SIZE) >= 1){
                posX = destX;
                posY = destY;
                transform.setTo(new Vector2D(posX, posY).scale(GameMap.TILE_SIZE));
                isMoving = false;
                controller.speed = speed;
            }
        }
    }
    
    @Override
    public void lateUpdate(JFrame parent){
        super.lateUpdate(parent);
    }
    
    protected abstract void setCollider(); //escolhe a "camada" do colisor
    
    public MapCollider getCollider(){
        return collider;
    }
    
    public boolean doesCollideWith(MapObject other){
        return this.collider.doesCollideWith(other.collider);
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);
    }
    
    public boolean canMoveTo(int x, int y){
        return GameMap.getInstance().canObjectMoveTo(this, x, y);
    }
    
    @Override
    public boolean mouseContains(){
//        final int SIZE = 32;
//        int mx = InputHandler.getInstance().getMouseX();
//        int my = InputHandler.getInstance().getMouseY();
//        if (mx >= transform.getX() && my >= transform.getY()
//                && mx < SIZE+transform.getX() && my < SIZE+transform.getY()){
//            return true;
//            
//        }
        return false;
    }
}
