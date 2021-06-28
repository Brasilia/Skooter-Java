/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

/**
 *
 * @author Rafael
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import jogopoo.AutoSaver;


public abstract class Game implements WindowListener {
    //Window
    protected final JFrame mainWindow;
    private boolean active;
    private BufferStrategy bufferStrategy;
    //FPS
    private GameSpeedTracker speedTracker;
    private int expectedTPS;
    private double expectedNanosPerTick;
    private int maxFrameSkip;
    
    public Game(){
        System.out.println("Game constructed.");
        mainWindow = new JFrame("Game Core");
        mainWindow.setSize(640 ,480);
        mainWindow.addWindowListener(this);
        mainWindow.addKeyListener(InputHandler.getInstance());
        mainWindow.addMouseListener(InputHandler.getInstance());
        active = false;       
    }
    
    private void terminate(){
        active = false;
        System.out.println("Terminating...");
    }
    
    public boolean isActive(){
        return active;
    }
    
    public void run(){
        active = true;
        load();
        //Inicializa tps counter
        speedTracker = new GameSpeedTracker();
        speedTracker.start();
        expectedTPS = 60;
        expectedNanosPerTick = GameSpeedTracker.NANOS_IN_ONE_SECOND/expectedTPS;
        maxFrameSkip = 10; // número de frames que podem ser pulados, para manter a taxa de tps constante
        long nanoTimeAtNextTick = System.nanoTime();
        int skippedFrames = 0; // conta o número de frames já puladas
        //Game Loop
        while (active){
            speedTracker.update();
            if (System.nanoTime() > nanoTimeAtNextTick){ // roda próximo tick
                nanoTimeAtNextTick = System.nanoTime() + (long)expectedNanosPerTick;
                speedTracker.countTick();
                //Input
                InputHandler.getInstance().update();
                //Update
                update();
                //Render
                if (System.nanoTime() < nanoTimeAtNextTick || skippedFrames > maxFrameSkip){ //ainda está no tick atual ou já pulou frames demais
                    render();
                    skippedFrames = 0;
                }
                else {
                    skippedFrames ++;
                }
                if (System.nanoTime() < nanoTimeAtNextTick){ //Ainda sobrou algum tempo
                    try {
                        Thread.sleep( (nanoTimeAtNextTick - System.nanoTime())/1000000 );
                    }
                    catch (InterruptedException ex){
                        //TODO
                        System.out.println("InterruptedException");
                    }
                }
            }
        }
        unload();
    }
  
    private void load(){
        mainWindow.setUndecorated(false); // False para exibir moldura da janela
        mainWindow.setIgnoreRepaint(true); // For page flipping to work properly
        mainWindow.setLocation(100, 100);
        mainWindow.setVisible(true);
        mainWindow.createBufferStrategy(2); // double buffer
        bufferStrategy = mainWindow.getBufferStrategy();
        onLoad();
    }
    
    private void unload(){
        onUnload();
        bufferStrategy.dispose();
        mainWindow.dispose();
    }
    
    private void update(){
        onUpdate();
    }
    
    private void render(){
        //Prepara a tela
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, mainWindow.getWidth(), mainWindow.getHeight()); // Preenche toda a tela com a cor especificada para g
        //Faz o render específico do jogo
        onRender(g); 
        //Desenha o tps counter
        g.setColor(Color.black);
        g.fillRect(0, 30, 50, 16);
        g.setColor(Color.white);
        g.setFont(new Font ("",Font.BOLD, 12));
        g.drawString(speedTracker.getTPS() + " tps", 11, 42);
        //Encerra
        g.dispose();
        bufferStrategy.show(); // Flip: mostra o backbuffer, colocando-o como frontbuffer
    }
    
    public void refreshRender(){
        render();
    }
    
    abstract protected void onLoad();
    abstract protected void onUnload();
    abstract protected void onUpdate();
    abstract protected void onRender(Graphics2D g);
    
    
    protected int getWindowHeight(){
        return mainWindow.getHeight();
    }
    
    protected int getWindowWidth(){
        return mainWindow.getWidth();       
    }
    
    
    //Window Listener Implementation:
    @Override
    public void windowOpened(WindowEvent e) {
        
    }

    @Override
    public void windowClosing(WindowEvent e) {
        terminate();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        
    }

    @Override
    public void windowIconified(WindowEvent e) {
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        
    }

    @Override
    public void windowActivated(WindowEvent e) {
        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
    
}
