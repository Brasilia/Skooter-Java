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
public class BlockArrow extends Block{
    
    public BlockArrow(int x, int y, boolean isBreakable, Direction arrowDir) {
        super(x, y, isBreakable, true);
        this.lookDir = arrowDir;
        pushableSprite.setSprite(ARROW_BLOCK+lookDir.toString+".png");
        controller.makeWeak(true);
        speed = 3;
    }
    
    @Override
    public void earlyUpdate(){
        super.earlyUpdate();
        controller.makeInput(lookDir);
    }
    
    @Override
    public void update(){
        super.update();
        pushableSprite.setSprite(ARROW_BLOCK+lookDir.toString+".png");
        //System.out.println(controller.getDirection());
    }
    
    @Override
    public boolean move(Direction dir){
        Direction originalDir = lookDir;
        boolean r = super.move(dir);
        lookDir = originalDir;
        return r;
    }
    
}
