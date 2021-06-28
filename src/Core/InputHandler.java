/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author retirado de http://abrindoojogo.com.br
 *          modificado por Rafael Miranda
 */
public class InputHandler implements KeyListener, MouseListener {
    //Key estates
    static protected int KEY_RELEASED = 0;
    static protected int KEY_JUST_PRESSED = 1;
    static protected int KEY_PRESSED = 2;
    //Instância para Singleton
    static private InputHandler instance;
    //Para obter e manter o cache
    private final HashMap<Integer, Integer> keyCache;
    private final ArrayList<Integer> pressedKeys;
    private final ArrayList<Integer> releasedKeys;
    //private final ArrayList<Integer> typedKeys;
    
    //private int tickCounter;
    
    private int mouseX;
    private int mouseY;
    private boolean mouseClicked = false;
    private int mouseClickedTicks = 0;
    
    private InputHandler(){
        //this.tickCounter = 0;
        keyCache = new HashMap<>();
        pressedKeys = new ArrayList<>();
        releasedKeys = new ArrayList<>();
        //typedKeys = new ArrayList<>();
    }
    
    static public InputHandler getInstance(){
        if (instance == null){
            instance = new InputHandler();
        }
        return instance;
    }
    
    public boolean isPressed(int keyId){
        
        //System.out.print(keyCache.containsKey(keyId));
        //if (keyCache.containsKey(keyId)) System.out.println(!keyCache.get(keyId).equals(KEY_RELEASED));
        //System.out.println();
        return keyCache.containsKey(keyId)
                && !keyCache.get(keyId).equals(KEY_RELEASED);
    }
    
    public boolean isJustPressed(int keyId){
        return keyCache.containsKey(keyId)
                && keyCache.get(keyId).equals(KEY_JUST_PRESSED);
    }
    
    public boolean isReleased(int keyId){
        return !keyCache.containsKey(keyId)
                || keyCache.get(keyId).equals(KEY_RELEASED);
    }
    
//    public boolean isTyped(int keyId){
//        return typedKeys.contains(keyId);
//    }
    
    public void update(){
        for (Integer keyCode : keyCache.keySet()){
            if (isJustPressed(keyCode)){
                keyCache.put(keyCode, KEY_PRESSED);
            }
        }
        for (Integer keyCode : releasedKeys){
            keyCache.put(keyCode, KEY_RELEASED);
        }
        for (Integer keyCode : pressedKeys){
            if (isReleased(keyCode)){
                keyCache.put(keyCode, KEY_JUST_PRESSED);
            }else {
                keyCache.put(keyCode, KEY_PRESSED);
            }
        }
        //System.out.println("pressedKeys.size() = " + pressedKeys.size());
        pressedKeys.clear();
        releasedKeys.clear();
        //typedKeys.clear();
//        tickCounter++;
//        if (tickCounter > 20){
//            tickCounter = 0;
//            pressedKeys.clear();
//        }
        mouseClickedTicks++;
        if(mouseClickedTicks > 1)
            mouseClicked = false;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        //typedKeys.add(e.getKeyCode());
        //System.out.println(e.getKeyCode() + " typed");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        //System.out.println("Key PRESSED");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        releasedKeys.add(e.getKeyCode());
        //System.out.println("Key RELEASED");
    }
    
    //Fix to the keyReleased bug not being called:
    //modificado
    public void releaseKey(int keyId){
        releasedKeys.add(keyId);
    }
    
    public void releaseKeys(ArrayList<Integer> keyIds){
        for (Integer keyId : keyIds) {
            releaseKey(keyId);
        }
    }
    
    public boolean isKeyPressed(int keyId){
        //System.out.println("pressedKyes.size() = " + pressedKeys.size());
        //return pressedKeys.contains(keyId);
        return isPressed(keyId);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3){
            System.out.println("Richt Click");
            mouseX = e.getX();
            mouseY = e.getY();
            mouseClickedTicks = 0;
            mouseClicked = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        if(e.getButton() == MouseEvent.BUTTON1){
//            mouseClicked = false;
//        }    
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    public int getMouseX(){
        return mouseX;
    }
    
    public int getMouseY(){
        return mouseY;
    }
    
    public boolean mouseClicked(){
        return mouseClicked;
    }
}
