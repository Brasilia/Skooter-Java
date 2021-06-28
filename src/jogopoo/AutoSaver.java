/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */
public class AutoSaver extends Thread {
    
    private SkooterGame game;
    
    AutoSaver(SkooterGame game){
        this.game = game;
    }
    
    @Override
    public void run(){
        System.out.println("Autosave Thread In");
        while(game.isActive()){
            if (game.saveNanoTime <= System.nanoTime()){
                System.out.println("Autosaving...");
                game.saveNanoTime = System.nanoTime() + game.saveSecondsInterval*1000000000;
                GameMap.getInstance().save("save");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AutoSaver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Autosave Thread Out");
    }
}
