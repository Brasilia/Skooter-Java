/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import Core.InputHandler;
import java.awt.Graphics2D;

/**
 *
 * @author Rafael
 */
public class Block extends MapObject{
    static final String PUSHABLE_BLOCK = "diamond.png";
    static final String BREAKABLE_BLOCK = "greenBlock.png";
    static final String UNBREAKABLE_BLOCK = "redBlock.png";
    static final String ARROW_BLOCK = "blockArrow";
    public GameObject pushableSprite;
    
    
    public Block(int x, int y, boolean isBreakable, boolean isPushable) {
        super(x, y);
        //System.out.println("Constructing Block");
        this.isBreakable = isBreakable;
        this.isPushable = isPushable;
        setPushableSprite();
        if (isBreakable){
            this.setSprite(BREAKABLE_BLOCK);
        } else{
            this.setSprite(UNBREAKABLE_BLOCK);
        }
    }
    
    public void setPushableSprite(){
        pushableSprite = null;
        if (isPushable){
            pushableSprite = new GameObject();
            pushableSprite.setSprite(PUSHABLE_BLOCK);
            pushableSprite.transform.setParent(this.transform);
        }
    }
    
    @Override
    public void render(Graphics2D g){
        super.render(g);
        if (pushableSprite != null){
            pushableSprite.render(g);
        }
    }

    @Override
    protected void setCollider() {
        this.collider = new MapCollider(MapCollider.Layer.BLOCK);
    }
    
    @Override
    public boolean mouseContains(){
        final int SIZE = 32;
        int mx = InputHandler.getInstance().getMouseX();
        int my = InputHandler.getInstance().getMouseY();
        return mx >= transform.getX() && my >= transform.getY()
                && mx < SIZE+transform.getX() && my < SIZE+transform.getY();
    }
}
