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
public class Arrow extends MapObject{
    
    public Arrow(int x, int y, Direction dir) {
        super(x, y);
        lookDir = dir;
        switch (dir){
            case UP:
                setSprite("arrowUp.png");
                break;
            case DOWN:
                setSprite("arrowDown.png");
                break;
            case LEFT:
                setSprite("arrowLeft.png");
                break;
            case RIGHT:
                setSprite("arrowRight.png");
                break;
        }
        this.speed = 3;
    }

    @Override
    protected void setCollider() {
        this.collider = new MapCollider(MapCollider.Layer.ARROW);
    }
    
    @Override
    public void earlyUpdate(){
        super.earlyUpdate();
        for (MapObject obj : objectsOnSameTile()){
            strongPush(obj, this.lookDir);
        }
    }
    
}
