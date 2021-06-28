/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

/**
 *
 * @author Rafael
 */
public class PickupItem extends MapObject{
    
    private int score;

    public PickupItem(int x, int y, int score, String spriteName) {
        super(x, y);
        setSprite(spriteName);
        this.score = score;
    }

    @Override
    protected void setCollider() {
        this.collider = new MapCollider(MapCollider.Layer.PICKUP);
    }
    
    public int getScore(){
        return score;
    }
}
