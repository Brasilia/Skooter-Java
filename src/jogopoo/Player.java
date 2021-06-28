/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import Core.InputHandler;
import com.sun.glass.events.KeyEvent;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author Rafael
 * A interface KeyListener foi adicionada a esta classe porque o bug
 * da própria KeyListener, em que o evento de release de tecla não é
 * acionado consistentemente, não foi satisfatoriamente contornado 
 * utilizando o gerenciador InputHandler;
 * Em substituição ao getInput, será utilizado o keyPressed, caso este
 * se mostre mais compatível com a resposta a input desejada.
 * TODO - testar dar release nas teclas ao fim dos movimentos
 */
public class Player extends Character /*implements KeyListener*/{
    
    public Player(int x, int y) {
        super(x, y);
        System.out.println("Constructing Player");
        this.setSprite("player"+this.lookDir.toString+".png");
        isPushable = true;
    }
    
    @Override
    public void earlyUpdate(){
        getInput();
        if(!isMoving) push(controller.getDirection());
        if (!isMoving && controller.isAttacking()) attack();
    }
    
    @Override
    public void update(){
        super.update();
    }
    
    @Override
    public void lateUpdate(JFrame parent){
        super.lateUpdate(parent);
        this.setSprite("player"+this.lookDir.toString+".png");
    }
    
    //(player only)
    public void getInput(){        
        ArrayList<Integer> keysRel = new ArrayList<>();
        keysRel.add(KeyEvent.VK_UP);
        keysRel.add(KeyEvent.VK_DOWN);
        keysRel.add(KeyEvent.VK_LEFT);
        keysRel.add(KeyEvent.VK_RIGHT);
        keysRel.add(KeyEvent.VK_W);
        keysRel.add(KeyEvent.VK_S);
        keysRel.add(KeyEvent.VK_A);
        keysRel.add(KeyEvent.VK_D);
        
        //O release das outras keys foi feito para contornar o bug da interface KeyListener, que perde releases
        if (InputHandler.getInstance().isKeyPressed(KeyEvent.VK_UP)
                || InputHandler.getInstance().isKeyPressed(KeyEvent.VK_W)){
            //move(GameObject.Direction.UP);
            controller.makeInput(Direction.UP);
            keysRel.remove((Object)KeyEvent.VK_UP);
            keysRel.remove((Object)KeyEvent.VK_W);
            //InputHandler.getInstance().releaseKeys(keysRel);
            //lookDir = Direction.UP;
        }
        if (InputHandler.getInstance().isKeyPressed(KeyEvent.VK_DOWN)
                || InputHandler.getInstance().isKeyPressed(KeyEvent.VK_S)){
            //move(GameObject.Direction.DOWN);
            controller.makeInput(Direction.DOWN);
            keysRel.remove((Object)KeyEvent.VK_DOWN);
            keysRel.remove((Object)KeyEvent.VK_S);
            //InputHandler.getInstance().releaseKeys(keysRel);
            //if (!isMoving) push(Direction.DOWN);
            //lookDir = Direction.DOWN;
        }
        if (InputHandler.getInstance().isKeyPressed(KeyEvent.VK_LEFT)
                || InputHandler.getInstance().isKeyPressed(KeyEvent.VK_A)){
            //move(GameObject.Direction.LEFT);
            controller.makeInput(Direction.LEFT);
            keysRel.remove((Object)KeyEvent.VK_LEFT);
            keysRel.remove((Object)KeyEvent.VK_A);
            //InputHandler.getInstance().releaseKeys(keysRel);
            //if (!isMoving) push(Direction.LEFT);
            //lookDir = Direction.LEFT;
        }
        if (InputHandler.getInstance().isKeyPressed(KeyEvent.VK_RIGHT)
                || InputHandler.getInstance().isKeyPressed(KeyEvent.VK_D)){
            //System.out.println("RIGHT is pressed!");
            //move(GameObject.Direction.RIGHT);
            controller.makeInput(Direction.RIGHT);
            keysRel.remove((Object)KeyEvent.VK_RIGHT);
            keysRel.remove((Object)KeyEvent.VK_D);
            //InputHandler.getInstance().releaseKeys(keysRel);
            //if (!isMoving) push(Direction.RIGHT);
            //lookDir = Direction.RIGHT;
        }
//        if(InputHandler.getInstance().isTyped(KeyEvent.VK_W))
//            move(GameObject.Direction.UP);
//        if(InputHandler.getInstance().isTyped(KeyEvent.VK_S))
//            move(GameObject.Direction.DOWN);
//        if(InputHandler.getInstance().isTyped(KeyEvent.VK_A))
//            move(GameObject.Direction.LEFT);
//        if(InputHandler.getInstance().isTyped(KeyEvent.VK_D))
//            move(GameObject.Direction.RIGHT);
        if (InputHandler.getInstance().isKeyPressed(KeyEvent.VK_SPACE)){
            controller.makeAttack();
        }
    }
    
    public void attack(){
        if (isMoving) return;
        //System.out.println("Attacking");
        for (MapObject obj : GameMap.getInstance().objectsAt(posX+lookDir.getX(), posY+lookDir.getY())){
            if(obj.isBreakable && !obj.isMoving){
                obj.toRemove = true; //GameMap se encarrega de remover
            }
        }
//        ArrayList<MapObject> objs = GameMap.getInstance().objectsAt(posX+lookDir.getX(), posY+lookDir.getY());
//        GameMap.getInstance().destroyObject(objs.get(objs.size()-1));
        
    }
    

//    @Override
//    public void keyTyped(java.awt.event.KeyEvent e) {
//        //não utilizado
//    }
//
//    @Override
//    public void keyPressed(java.awt.event.KeyEvent e) {
////        System.out.println("keyPressed");
////        switch (e.getKeyCode()) {
////            case KeyEvent.VK_W:
////            case KeyEvent.VK_UP:
////                move(GameObject.Direction.UP);
////                break;
////            case KeyEvent.VK_S:
////            case KeyEvent.VK_DOWN:
////                move(GameObject.Direction.DOWN);
////                break;
////            case KeyEvent.VK_A:
////            case KeyEvent.VK_LEFT:
////                move(GameObject.Direction.LEFT);
////                break;
////            case KeyEvent.VK_D:
////            case KeyEvent.VK_RIGHT:
////                move(GameObject.Direction.RIGHT);
////                break;
////            default:
////                break;
////        }
//    }
//
//    @Override
//    public void keyReleased(java.awt.event.KeyEvent e) {
//        //não utilizado
//    }

    @Override
    protected void setCollider() {
        this.collider = new MapCollider(MapCollider.Layer.PLAYER);
    }
    
}
