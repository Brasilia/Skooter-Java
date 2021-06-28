/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import java.io.Serializable;

/**
 *
 * @author Rafael
 */
public class MapCollider implements Serializable{
    public enum Layer {
        //Player, monster, block, arrow, pickup
        /*
        -   0   1   0   0
        0   1   1   1   1
        1   1   1   0   1
        0   1   0   -   ?
        0   1   1   ?   -
        */
        PLAYER(0),
        MONSTER(1),
        BLOCK(2),
        ARROW(3),
        PICKUP(4);
        public final int id;
        private Layer(int value) { this.id = value; }
        public int toInt() { return id; }
    }
    private final boolean collisionMap[][] =
    {   {true, false, true, false, false},
        {false, true, true, true, true},
        {true, true, true, false, true},
        {false, true, false, true, true},
        {false, true, true, true, true}
    };
    
    private final Layer layer;
    
    public MapCollider(Layer l){
        layer = l;
    }
    
    public boolean doesCollideWith(MapCollider otherCollider){
        int l1 = this.layer.toInt();
        int l2 = otherCollider.layer.toInt();
        return collisionMap[l1][l2];
    }
    
    
}
