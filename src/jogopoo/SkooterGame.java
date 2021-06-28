/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import Core.Game;
import Core.InputHandler;
import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Rafael
 */
public class SkooterGame extends Game{
    
    public long saveNanoTime;
    public long saveSecondsInterval = 30;
    AutoSaver autosaver;
    private boolean serverIsWaiting;
            
    public SkooterGame(){
        System.out.println("SkooterGame constructed.");
        saveNanoTime = System.nanoTime() + saveSecondsInterval*1000000000;
        serverIsWaiting = false;
    }

    @Override
    protected void onLoad() {
        autosaver = new AutoSaver(this);
        autosaver.start();
    }

    @Override
    protected void onUnload() {

    }

    @Override
    protected void onUpdate() {
        handleInput();
        handleAI();
        GameMap.getInstance().firstUpdate();
        GameMap.getInstance().earlyUpdate();
        GameMap.getInstance().update();
        GameMap.getInstance().lateUpdate(mainWindow);
    }

    @Override
    protected void onRender(Graphics2D g) {
        //Desenha opções de autosave
        g.setColor(Color.white);
        g.setFont(new Font ("",Font.BOLD, 12));
        g.drawString("Autosave:  O (-)  " + saveSecondsInterval + "s  (+) P", 31, 440);
        g.drawString("Save: K", 31, 452);
        g.drawString("Load: L", 31, 464);
        //Desenha instruções de Server-Client
        g.drawString("Server: V", 331, 452);
        g.drawString("Client: C", 331, 464);
        //Mensagem de espera do server
        if(serverIsWaiting){
            g.drawString("Servidor aguardando por conexão...", 128, 52);
        }
        //Render do jogo
        GameMap.getInstance().render(g);
    }
    
    private void handleInput(){
        if (InputHandler.getInstance().isJustPressed(KeyEvent.VK_L)){
            GameMap.getInstance().load("save");
            saveNanoTime = System.nanoTime() + saveSecondsInterval*1000000000;
        }
        if (InputHandler.getInstance().isJustPressed(KeyEvent.VK_K)){
            GameMap.getInstance().save("save");
            saveNanoTime = System.nanoTime() + saveSecondsInterval*1000000000;
        }
        if (InputHandler.getInstance().isJustPressed(KeyEvent.VK_P)){
            saveSecondsInterval += 30;
            if (saveSecondsInterval > 600)
                saveSecondsInterval = 600;
        }
        if (InputHandler.getInstance().isJustPressed(KeyEvent.VK_O)){
            saveSecondsInterval -= 30;
            if (saveSecondsInterval < 30)
                saveSecondsInterval = 30;
        }
        //Server x Client
        //Client
        if (InputHandler.getInstance().isJustPressed(KeyEvent.VK_C)){
            Socket clientSocket = null;
            ObjectInputStream obj_in = null;
            try {
                clientSocket = new Socket("localhost", 8008);
                obj_in = new ObjectInputStream(clientSocket.getInputStream());
                GameMap inMap = (GameMap)obj_in.readObject();
                GameMap.getInstance().setMap(inMap);
            } catch (IOException ex) {
                Logger.getLogger(SkooterGame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SkooterGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Server
        if (InputHandler.getInstance().isJustPressed(KeyEvent.VK_V)){
            ServerSocket ouvido = null;
            try {
                ouvido = new ServerSocket(8008);
                System.out.println("Server waiting for client connection...");
                serverIsWaiting = true;
                refreshRender();
                Socket serverSocket = ouvido.accept();
                serverIsWaiting = false;
                //Carrega e envia arquivo de save
                FileInputStream f_in = null;
                try {
                    f_in = new FileInputStream("save" + ".data.gz");
                    GZIPInputStream z_in = new GZIPInputStream(f_in);
                    ObjectInputStream obj_in = new ObjectInputStream(z_in);
                    GameMap fMap = (GameMap) obj_in.readObject();
                    ObjectOutputStream obj_out = new ObjectOutputStream(serverSocket.getOutputStream());
                    obj_out.writeObject(fMap);
                    obj_out.flush();
                    System.out.println("Game state transfered from Server");
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (f_in != null) f_in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }    
            } catch (IOException ex) {
                Logger.getLogger(SkooterGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void handleAI(){
        
    }
    
}
