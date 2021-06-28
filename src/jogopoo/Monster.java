/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author Rafael
 */
public class Monster extends Character{
    //ArrayList<Integer>() route;
    
    public Monster(int x, int y) {
        super(x, y);
        this.setSprite("monster"+this.lookDir.toString+".png");
        speed = 0.8; 
    }
    
    @Override
    public void update(){
        if (!isMoving) getDecision();
        super.update();
    }
    
    @Override
    public void lateUpdate(JFrame parent){
        super.lateUpdate(parent);
        this.setSprite("monster"+this.lookDir.toString+".png");
    }
    
    public void getDecision(){ //similar ao getInput() do Player
        Random rnd = new Random();
        int randomDir = rnd.nextInt(8);
        switch (randomDir){
            case 0:
                if(canMoveTo(posX, posY-1))
                    move(GameObject.Direction.UP);
                break;
            case 1:
                if(canMoveTo(posX, posY+1))
                    move(GameObject.Direction.DOWN);
                break;
            case 2:
                if(canMoveTo(posX-1, posY))
                    move(GameObject.Direction.LEFT);
                break;
            case 3:
                if(canMoveTo(posX+1, posY))
                    move(GameObject.Direction.RIGHT);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                move(this.lookDir);
                break;
        }
    }

    @Override
    protected void setCollider() {
        this.collider = new MapCollider(MapCollider.Layer.MONSTER);
    }
    
}
