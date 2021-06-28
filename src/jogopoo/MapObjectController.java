/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class MapObjectController implements Serializable{
    MapObject.Direction direction;
    ArrayList<MapObject.Direction> highPriorityDir; //armazena input de maior prioridade
    //ArrayList<MapObject.Direction> lowPriorityDir; //armazena input de menor prioridade
    MapObject.Direction lowPriorityDir;
    boolean attackButton;
    boolean isWeak;
    double speed;
    
    public MapObjectController(){
        direction = MapObject.Direction.NEUTRAL;
        highPriorityDir = new ArrayList<>();
        lowPriorityDir = MapObject.Direction.NEUTRAL;
        attackButton = false;
        isWeak = false;
        speed = 0;
    }
    
    public void reset(){
        direction = GameObject.Direction.NEUTRAL;
        lowPriorityDir = MapObject.Direction.NEUTRAL;
        highPriorityDir.clear();
        attackButton = false;
    }
    
    public void makeInput(MapObject.Direction dir, boolean highPriority){
        if (highPriority){
            if(!highPriorityDir.contains(dir)){
            highPriorityDir.add(dir);
        }
        } else{
            lowPriorityDir = dir;
        }
    }
    
    public void makeInput(MapObject.Direction dir){
        makeInput(dir, true);
    }
    
    public MapObject.Direction getDirection(){
        //Se possui mais de 1 input, ignora todos
        if(highPriorityDir.size() > 1) 
            highPriorityDir.clear();
        //Se não há um comando de alta prioridade
        if(highPriorityDir.isEmpty())
            return lowPriorityDir;
        else{ //Há um comando de alta prioridade
            if(highPriorityDir.get(0).toInt+lowPriorityDir.toInt == GameObject.Direction.NEUTRAL.toInt &&!isWeak) //comandos em direções opostas
                return GameObject.Direction.NEUTRAL;
            else if(lowPriorityDir != GameObject.Direction.NEUTRAL) 
                return lowPriorityDir;
            else 
                return highPriorityDir.get(0);
        }
    }
    
    public boolean isPassive(){
        return highPriorityDir.isEmpty();
    }
    
    public void makeAttack(){
        attackButton = true;
    }
    
    public boolean isAttacking(){
        return attackButton && lowPriorityDir == GameObject.Direction.NEUTRAL;
    }
    
    public void makeWeak(boolean b){
        isWeak = b;
    }
    
}
