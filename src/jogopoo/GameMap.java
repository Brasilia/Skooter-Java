/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import Core.ImageHandler;
import Core.InputHandler;
import Core.Vector2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JFrame;

/**
 *
 * @author Rafael
 */
public class GameMap extends GameObject implements Serializable{
    
    enum State{
        STARTING, RUNNING, WON, GAME_OVER;
    }
    
    int tickCounter = 0;
    State state;
    
    private static GameMap instance; 
    public static final int TILE_SIZE = 32;
    final int WIDTH = 11;
    final int HEIGHT = 11;
    
    int score = 0;
    int top = 0;
    int lives = 3;
    int sheet = 1;
    
    static final int STARTING_SHEET = 1;
    static final int STARTING_LIVES = 3;
    
    final static int SCORE1 = 100;
    final static int SCORE2 = 150;
    final static int SCORE3 = 200;
    final static int SCORE4 = 250;
    
    private final ArrayList<MapObject> map[][];
    
    private final ArrayList<GameObject> gameObjects;
    
    Player player;
    ArrayList<Monster> monsters;
    ArrayList<PickupItem> items;
   
    private GameMap(Vector2D v2, GameMap lastMap) {
        super(v2);
        if (lastMap == null){
            sheet = STARTING_SHEET;
            lives = STARTING_LIVES;
            score = 0;
        } else {
            sheet = lastMap.sheet;
            lives = lastMap.lives;
            score = lastMap.score;
            top = lastMap.top;
        }
        //instance = getInstance();
        //Inicializa o mapa de objetos
        map = new ArrayList[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++){
            for (int j = 0; j < WIDTH; j++){
                map[i][j] = new ArrayList<>();
            }
        }
        gameObjects = new ArrayList<>();
        monsters = new ArrayList<>();
        items = new ArrayList<>();
        state = State.STARTING;
        initialize(sheet); 
    }
    
    static public GameMap getInstance(){
        if (instance == null){
            instance = new GameMap( new Vector2D(), null);
        }
        return instance;
    } 
    
    //Salva o objeto GameMap para arquivo .data
    public synchronized void save(String filename){
        System.out.println("Saving...");
        try {
            FileOutputStream f_out = new FileOutputStream(filename + ".data.gz");
            GZIPOutputStream zip_out = new GZIPOutputStream(f_out);
            try (ObjectOutputStream obj_out = new ObjectOutputStream(zip_out)) {
                obj_out.writeObject(this);
                obj_out.flush();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Game Saved!");
    }
    
    //Chamar pelo construtor, para carregar mapa
    public synchronized void load(String filename){
        System.out.println("Loading...");
        FileInputStream f_in = null;
        try {
            f_in = new FileInputStream(filename + ".data.gz");
            GZIPInputStream z_in = new GZIPInputStream(f_in);
            ObjectInputStream obj_in = new ObjectInputStream(z_in);
            GameMap fMap = (GameMap) obj_in.readObject();
            //getInstance();
            instance = fMap;
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
        //return null; //retorna null se não tiver retornado map anteriormente
        System.out.println("Game Loaded.");
    }
    
    public void setMap(GameMap otherMap){
        instance = otherMap;
    }
    
    private boolean stateMachineAllow(int counter){
        if (state == State.STARTING){
            if (tickCounter > counter/2){
                tickCounter = 0;
                state = State.RUNNING;  
            }
            return false;
        }
        if (state == State.GAME_OVER){
            //Muda imagem para GAME OVER
            if (tickCounter > counter*2){
                //fecha programa ou algo assim
                resetStats();
                GameMap.instance = new GameMap(transform, GameMap.getInstance());
            }
            return false;
        }
        if (state == State.WON){
            if (tickCounter > counter*3){
                //fecha programa ou algo assim
                resetStats();
                GameMap.instance = new GameMap(transform, GameMap.getInstance());
            }
            return false;
        }
        return true;
    }
    
    private void resetStats(){
        sheet = STARTING_SHEET;
        score = 0;
        lives = STARTING_LIVES;
    }
    
    @Override
    public void firstUpdate(){
        tickCounter++;
        if(!stateMachineAllow(180)) return;
        
        //Se lista de itens vazia, ganhou fase
        if(items.isEmpty()){
            sheet++;
            instance = new GameMap(transform, GameMap.getInstance());
        }
        if(lives == 0){
            //Perde o jogo
            state = State.GAME_OVER;
        }
        
        for (GameObject gameObject : gameObjects) {
            gameObject.firstUpdate();
        }
    }
    
    @Override
    public void earlyUpdate(){
        if(!stateMachineAllow(180)) return;
        player.earlyUpdate();
        for (GameObject gameObject : gameObjects) {
            if(gameObject != player) gameObject.earlyUpdate();
        }
    }
    
    @Override
    public void update() {
        if(!stateMachineAllow(180)) return;
        player.update();
        for (GameObject gameObject : gameObjects) {
            if(gameObject != player) gameObject.update();
        }
        checkPlayerCollisions();
    }
    
    @Override
    public void lateUpdate(JFrame parent){
        if(!stateMachineAllow(180)) return;
        player.lateUpdate(parent);
        for (GameObject gameObject : gameObjects) {
            if(gameObject != player) gameObject.lateUpdate(parent);
        }
        doRemoveObjects();
        for (GameObject gameObject : gameObjects) {
            if (InputHandler.getInstance().mouseClicked()){
                if (gameObject.mouseContains()){
                    System.out.println("Mouse clicked on " + gameObject.toString());
                    fileChooser = new FileChooserThread(parent);
                    fileChooser.start();
                    //O uso da thread como deveria foi abandonado por uma opção visual melhor,
                    // por isso o join vem em seguida; a classe foi mantida caso se deseje reverter
                    try {
                        fileChooser.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(FileChooserThread.gameObject != null){
                        //Faz a troca do objeto-block pelo escolhido
                        ((Block)gameObject).isBreakable = 
                                ((Block)FileChooserThread.gameObject).isBreakable;
                        ((Block)gameObject).isPushable = 
                                ((Block)FileChooserThread.gameObject).isPushable;
                        ((Block)gameObject).setSprite(FileChooserThread.gameObject.imgPath);
                        ((Block)gameObject).setPushableSprite();
                    }
                }
            }
        }
//        if (!FileChooserThread.running && FileChooserThread.gameObject != null){
//            
//            gameObject = null;
//        }
        
    }

    @Override
    public void render(Graphics2D g) {
        renderGUI(g);
        if(state == State.WON){
            g.setFont(new Font ("",Font.BOLD, 18));
            g.setColor(new Color(222, 222, 222));
            g.drawString("Jogo produzido por", 50, 200);
            g.setFont(new Font ("",Font.BOLD, 24));
            g.setColor(new Color(252, 252, 252));
            g.drawString("Rafael Miranda Lopes", 120-1, 250-1);
            g.setColor(new Color(252, 252, 202));
            g.drawString("Rafael Miranda Lopes", 120, 250);
            return;
        }
        
        try {
            //render map
            BufferedImage bGround = ImageHandler.getInstance().loadImage("tilebase.png");
            int XOff, YOff;
            XOff = 30;
            YOff = 72;
            for (int i = 0; i < 11; i++){
                for (int j = 0; j < 11; j++){
                    g.drawImage(bGround, i*32+XOff, j*32+YOff, null);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
        }
              
        for (GameObject gameObject : gameObjects) {
            gameObject.render(g);
        }

        if(state == State.GAME_OVER){
            g.setFont(new Font ("",Font.BOLD, 54));
            g.setColor(new Color(0, 0, 0));
            g.drawString("GAME OVER", 50, 270);
            g.setColor(new Color(77, 77, 77));
            g.drawString("GAME OVER", 50-4, 270-4);
        }
    }
    
    protected void renderGUI(Graphics2D g){
        final int START_X = 450;
        final int START_Y = 90;
        int dY = 0;
        String sScore = String.format("%06d", score);
        String sTop = String.format("%06d", top);
        String sLives = String.format("%02d", lives);
        String sSheet = String.format("%02d", sheet);
        g.setColor(new Color(222, 222, 222));
        g.setFont(new Font ("",Font.BOLD, 20));
        //Score
        g.drawString("-SCORE-", START_X, START_Y+dY); dY+=22;
        g.drawString(sScore, START_X+25, START_Y+dY); dY+=30;
        //Top
        g.drawString("-   TOP  -", START_X, START_Y+dY); dY+=22;
        g.drawString(sTop, START_X+25, START_Y+dY); dY+=30;
        //Lives
        g.drawString("- LIVES -", START_X, START_Y+dY); dY+=22;
        g.drawString(sLives, START_X+70, START_Y+dY); dY+=30;
        //Sheet
        g.drawString("-SHEET-", START_X, START_Y+dY); dY+=22;
        g.drawString(sSheet, START_X+70, START_Y+dY); dY+=58;
        //Score multipliers and Item Images
        ArrayList<GameObject> pickupItemSprites = new ArrayList<>();
        g.drawString(SCORE1+"", START_X, START_Y+dY); 
        pickupItemSprites.add(new GameObject(new Vector2D(START_X+70, START_Y+dY-26), "orange.png")); dY+=32;
        g.drawString(SCORE2+"", START_X, START_Y+dY); 
        pickupItemSprites.add(new GameObject(new Vector2D(START_X+70, START_Y+dY-26), "red.png")); dY+=32;
        g.drawString(SCORE3+"", START_X, START_Y+dY); 
        pickupItemSprites.add(new GameObject(new Vector2D(START_X+70, START_Y+dY-26), "green.png")); dY+=32;
        g.drawString(SCORE4+"", START_X, START_Y+dY); 
        pickupItemSprites.add(new GameObject(new Vector2D(START_X+70, START_Y+dY-26), "purple.png")); dY+=32;
        for(GameObject sprite : pickupItemSprites){
            sprite.render(g);
        }
   
    }
    
    private void addObject(MapObject obj){
        map[obj.posY][obj.posX].add(obj);
        gameObjects.add(obj);
        obj.transform.setParent(this.transform);
    }
    
    public void addMonster(Monster m){
        monsters.add(m);
        addObject(m);
    }
    
    public void addPickupItem(PickupItem item){
        items.add(item);
        addObject(item);
    }
    
    public void addPlayer(Player p){
        player = p;
        addObject(p);
    }
    
    public void moveObjectFromTo(MapObject obj, int x1, int y1, int x2, int y2){
        map[y2][x2].add(obj);
        map[y1][x1].remove(obj);
    }
    
    public boolean canObjectMoveTo(MapObject obj, int x, int y){
        boolean r;
        //Limites do mapa
        if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
            return false;
        //Verifica colisão contra objetos
        r = canObjectMoveToPos(obj, x, y);
        for (MapObject mapObj : map[y][x]) {
            if(obj.doesCollideWith(mapObj))
                r = false;
        }
        return r; 
    }
    
    //Verificação de colisão de objetos saindo de uma posição
    public boolean canObjectMoveToPos(MapObject obj, int x, int y){
        boolean r = true;
        if(!outOfBounds(x, y+1) && y-1 != obj.posY)
            for (MapObject mapObj : map[y+1][x]) {
                if(obj.doesCollideWith(mapObj) && mapObj.posY == y)
                    r = false;
            }
        if(!outOfBounds(x, y-1) && y+1 != obj.posY)
            for (MapObject mapObj : map[y-1][x]) {
                if(obj.doesCollideWith(mapObj) && mapObj.posY == y)
                    r = false;
            }
        if(!outOfBounds(x+1, y) && x-1 != obj.posX)
            for (MapObject mapObj : map[y][x+1]) {
                if(obj.doesCollideWith(mapObj) && mapObj.posX == x)
                    r = false;
            }
        if(!outOfBounds(x-1, y) && x+1 != obj.posX)
            for (MapObject mapObj : map[y][x-1]) {
                if(obj.doesCollideWith(mapObj) && mapObj.posX == x)
                    r = false;
            }
        return r;
    }
    
    private boolean isEmpty(int x, int y){
        return map[y][x].isEmpty();
    }
    
    public ArrayList<MapObject> objectsAt(int x, int y){
        if(outOfBounds(x, y)) return new ArrayList<>();
        return map[y][x];
    }
    
    public boolean outOfBounds(int x, int y){
        return x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT;
    }
    
    public void doRemoveObjects(){
        //Varre os tiles
        for (int i = 0; i < HEIGHT; i++){
            for (int j = 0; j < WIDTH; j++){
                //Varre a lista de objetos no tile
                for (int k = 0; k < map[i][j].size(); k++){
                    MapObject obj = map[i][j].get(k);
                    if(obj.toRemove){
                        //System.out.println("Destroying object " + obj);
                        map[i][j].remove(obj);
                        gameObjects.remove(obj);
                    }
                }
            }
        }
    }
    
    //Verifica colisão com monstros e itens, a fim de verificar o fluxo de jogo
    public void checkPlayerCollisions(){
        for(Monster m : monsters){
            if(player.transform.distanceEuclidian(m.transform) <= 0.5*TILE_SIZE){
                //Perde vida
                lives--;
                //Recomeça fase
                if(lives > 0) instance = new GameMap(transform, GameMap.getInstance());
                
            }
        }
        for(int i = 0; i < items.size(); i++){
            if(player.transform.distanceEuclidian(items.get(i).transform) <= 0.5*TILE_SIZE){
                //Remove
                items.get(i).toRemove = true;
                //Dá pontos
                score += items.get(i).getScore() * (5-items.size());
                if (top < score) top = score;
                //Remove da lista
                items.remove(items.get(i));  
            }
        }
    }
    
    
    //Experimental - passar para arquivo, posteriormente:
    private void initialize(int level){
        
        //Player player;
        this.transform.setTo( new Vector2D (30, 72) );
        
        switch (level){
            
            case 1:
                player = new Player(4, 4);
                //Arrows
                //Monsters
                addMonster(new Monster(9, 0));
                addMonster(new Monster(0, 2));
                addMonster(new Monster(1, 10));
                addMonster(new Monster(9, 10));
                //Pickups
                addPickupItem(new PickupItem(0, 0, SCORE4, "purple.png"));
                addPickupItem(new PickupItem(10, 0, SCORE2, "red.png"));
                addPickupItem(new PickupItem(0, 10, SCORE1, "orange.png"));
                addPickupItem(new PickupItem(10, 10, SCORE3, "green.png"));
                //Blocks
                for (int i = 0; i < 11; i++){
                    for (int j = 0; j < 11; j++){
                        if ( (i*j)%2 == 1 ){
                            MapObject block = new Block(i, j, false, false);
                            addObject(block);
                        }
                    }
                }
                addObject(new Block(1, 0, true, true));
                addObject(new Block(5, 0, true, true));
                addObject(new Block(2, 1, true, true));
                addObject(new Block(8, 1, true, true));
                addObject(new Block(10, 1, true, true));
                addObject(new Block(1, 2, true, true));
                addObject(new Block(5, 2, true, true));
                addObject(new Block(0, 3, true, true));
                addObject(new Block(8, 3, true, true));
                addObject(new Block(1, 4, true, true));
                addObject(new Block(9, 4, true, true));
                addObject(new Block(2, 5, true, true));
                addObject(new Block(6, 5, true, true));
                addObject(new Block(5, 6, true, true));
                addObject(new Block(7, 6, true, true));
                addObject(new Block(8, 7, true, true));
                addObject(new Block(10, 7, true, true));
                addObject(new Block(3, 8, true, true));
                addObject(new Block(9, 8, true, true));
                addObject(new Block(0, 9, true, true));
                addObject(new Block(2, 9, true, true));
                addObject(new Block(6, 9, true, true));
                addObject(new Block(8, 9, true, true));
                addObject(new Block(7, 10, true, true));
                //Player
                addPlayer(player);
                break;
            
            case 2:
                //Arrows
                addObject(new Arrow(3, 0, Direction.RIGHT));
                addObject(new Arrow(7, 0, Direction.RIGHT));
                addObject(new Arrow(3, 2, Direction.RIGHT));
                addObject(new Arrow(4, 2, Direction.DOWN));
                addObject(new Arrow(5, 2, Direction.LEFT));
                addObject(new Arrow(6, 2, Direction.LEFT));
                addObject(new Arrow(7, 2, Direction.LEFT));
                addObject(new Arrow(0, 3, Direction.UP));
                addObject(new Arrow(2, 3, Direction.DOWN));
                addObject(new Arrow(4, 3, Direction.DOWN));
                addObject(new Arrow(6, 3, Direction.UP));
                addObject(new Arrow(8, 3, Direction.UP));
                addObject(new Arrow(10, 3, Direction.DOWN));
                addObject(new Arrow(0, 4, Direction.UP));
                addObject(new Arrow(3, 4, Direction.RIGHT));
                addObject(new Arrow(7, 4, Direction.RIGHT));
                addObject(new Arrow(8, 4, Direction.UP));
                addObject(new Arrow(9, 4, Direction.LEFT));
                addObject(new Arrow(10, 4, Direction.LEFT));
                addObject(new Arrow(0, 5, Direction.UP));
                addObject(new Arrow(0, 6, Direction.UP));
                addObject(new Arrow(3, 6, Direction.RIGHT));
                addObject(new Arrow(7, 6, Direction.LEFT));
                addObject(new Arrow(0, 7, Direction.UP)); //UP
                addObject(new Arrow(2, 7, Direction.DOWN));
                addObject(new Arrow(4, 7, Direction.DOWN));
                addObject(new Arrow(6, 7, Direction.UP));
                addObject(new Arrow(8, 7, Direction.UP));
                addObject(new Arrow(10, 7, Direction.UP));
                addObject(new Arrow(3, 8, Direction.RIGHT));
                addObject(new Arrow(7, 8, Direction.RIGHT));
                addObject(new Arrow(3, 10, Direction.LEFT));
                addObject(new Arrow(7, 10, Direction.RIGHT));
                //Player
                player = new Player(5,5);
                //Monsters
                addMonster(new Monster(1, 1));
                addMonster(new Monster(1, 9));
                addMonster(new Monster(9, 1));
                addMonster(new Monster(9, 9));
                //Pickup items
                addPickupItem(new PickupItem(1, 5,SCORE2,"red.png"));
                addPickupItem(new PickupItem(5, 1,SCORE4,"purple.png"));
                addPickupItem(new PickupItem(9, 5,SCORE1,"orange.png"));
                addPickupItem(new PickupItem(5, 9,SCORE3,"green.png"));
                //Blocks
                for (int i = 0; i < 11; i++){
                    for (int j = 0; j < 11; j++){
                        if ( (i*j)%2 == 1 ){
                            if (isEmpty(j, i) && i*j != 25 ){
                                MapObject block = new Block(j, i, false, false);
                                addObject(block);
                            }   
                        }
                    }
                }
                addPlayer(player);
                //save("level2");
                //load("level2");
                break;
                
            case 3:
                //Arrows
                //Monsters
                addMonster(new Monster(5, 0));
                addMonster(new Monster(5, 2));
                addMonster(new Monster(5, 8));
                addMonster(new Monster(5, 10));
                //Pickups
                addPickupItem(new PickupItem(0, 5, SCORE4, "purple.png"));
                addPickupItem(new PickupItem(2, 5, SCORE1, "orange.png"));
                addPickupItem(new PickupItem(8, 5, SCORE2, "red.png"));
                addPickupItem(new PickupItem(10, 5, SCORE3, "green.png"));
                //Blocks
                for(int mX = 1; mX < 10; mX++){ //Externos
                    //Adiciona na horizontal
                    addObject(new Block(mX, 1, false, true));
                    addObject(new Block(mX, 9, false, true));
                    //Adiciona na vertical
                    if (mX != 1 && mX != 9){
                        addObject(new Block(1, mX, false, true));
                        addObject(new Block(9, mX, false, true));
                    }
                }
                for(int mX = 3; mX < 8; mX++){ //Internos
                    //Adiciona na horizontal
                    addObject(new Block(mX, 3, false, true));
                    addObject(new Block(mX, 7, false, true));
                    //Adiciona na vertical
                    if (mX != 3 && mX != 7){
                        addObject(new Block(3, mX, false, true));
                        addObject(new Block(7, mX, false, true));
                    }
                }
                //Player
                addPlayer(new Player(5, 5));
                break;
            case 4:
                //Arrows
                //Monsters
                addMonster(new Monster(5, 0));
                addMonster(new Monster(0, 5));
                addMonster(new Monster(5, 10));
                addMonster(new Monster(10, 5));
                //Pickups
                addPickupItem(new PickupItem(0, 0, SCORE4, "purple.png"));
                addPickupItem(new PickupItem(0, 10, SCORE1, "orange.png"));
                addPickupItem(new PickupItem(10, 0, SCORE2, "red.png"));
                addPickupItem(new PickupItem(10, 10, SCORE3, "green.png"));
                //Blocks
                for(int j = 1; j < 10; j++){ //Blocos verdes
                    for (int i = 1; i < 10; i++){
                        if (i%2 == j%2)
                            addObject(new Block(i, j, true, false));
                    }
                }
                addObject(new Block(3, 0, false, false));
                addObject(new Block(7, 0, false, false));
                addObject(new Block(0, 1, false, false));
                addObject(new Block(8, 1, false, false));
                addObject(new Block(5, 2, false, false));
                addObject(new Block(2, 3, false, false));
                addObject(new Block(10, 3, false, false));
                addObject(new Block(2, 5, false, false));
                addObject(new Block(8, 5, false, false));
                addObject(new Block(3, 6, false, false));
                addObject(new Block(0, 7, false, false));
                addObject(new Block(1, 8, false, false));
                addObject(new Block(7, 8, false, false));
                addObject(new Block(10, 8, false, false));
                addObject(new Block(1, 10, false, false));
                //Player
                addPlayer(new Player(5, 4));
                break;
            case 5:
                //Arrows
                addObject(new Arrow(7, 0, Direction.DOWN));
                addObject(new Arrow(6, 1, Direction.DOWN));
                addObject(new Arrow(5, 3, Direction.DOWN));
                addObject(new Arrow(6, 3, Direction.LEFT));
                addObject(new Arrow(10, 3, Direction.LEFT));
                addObject(new Arrow(3, 4, Direction.RIGHT));
                addObject(new Arrow(4, 4, Direction.DOWN));
                addObject(new Arrow(5, 4, Direction.RIGHT));
                addObject(new Arrow(6, 4, Direction.UP));
                addObject(new Arrow(9, 4, Direction.UP));
                addObject(new Arrow(3, 5, Direction.UP));
                addObject(new Arrow(4, 5, Direction.LEFT));
                addObject(new Arrow(6, 5, Direction.RIGHT));
                addObject(new Arrow(7, 5, Direction.DOWN));
                addObject(new Arrow(1, 6, Direction.DOWN));
                addObject(new Arrow(4, 6, Direction.DOWN));
                addObject(new Arrow(5, 6, Direction.LEFT));
                addObject(new Arrow(6, 6, Direction.UP));
                addObject(new Arrow(7, 6, Direction.LEFT));
                addObject(new Arrow(0, 7, Direction.DOWN));
                addObject(new Arrow(4, 7, Direction.RIGHT));
                addObject(new Arrow(5, 7, Direction.UP));
                addObject(new Arrow(4, 9, Direction.LEFT));
                addObject(new Arrow(3, 10, Direction.LEFT));
                //Monsters
                addMonster(new Monster(0, 0));
                addMonster(new Monster(0, 1));
                addMonster(new Monster(1, 0));
                addMonster(new Monster(1, 1));
                //Pickups
                addPickupItem(new PickupItem(6, 0, SCORE4, "purple.png"));
                addPickupItem(new PickupItem(10, 4, SCORE3, "green.png"));
                addPickupItem(new PickupItem(0, 6, SCORE2, "red.png"));
                addPickupItem(new PickupItem(4, 10, SCORE1, "orange.png"));
                //Blocks
                    //Red
                for (int i = 0; i < 3; i++){
                    addObject(new Block(i, 5, false, false));
                    addObject(new Block(10-i, 5, false, false));
                    addObject(new Block(5, i, false, false));
                    addObject(new Block(5, 10-i, false, false));
                }
                addObject(new Block(3, 3, false, false));
                addObject(new Block(5, 5, false, false));
                addObject(new Block(7, 7, false, false));
                addObject(new Block(3, 7, false, false));
                addObject(new Block(7, 3, false, false));
                    //Pushable
                addObject(new Block(9, 9, false, true));
                addObject(new Block(9, 7, false, true));
                addObject(new Block(7, 9, false, true));
                    //Green
                addObject(new Block(6, 3, true, false));
                addObject(new Block(3, 4, true, false));
                addObject(new Block(6, 4, true, false));
                addObject(new Block(4, 5, true, false));
                addObject(new Block(7, 5, true, false));
                addObject(new Block(4, 6, true, false));
                addObject(new Block(6, 6, true, false));
                addObject(new Block(4, 7, true, false));
                //Player
                addPlayer(new Player(8, 2));
                break;
            case 6:
                //Arrows
                for (int i = 0; i < 9; i++)
                    addObject(new Arrow(0, i, Direction.DOWN));
                for (int i = 4; i < 10; i++){
                    addObject(new Arrow(i, 3, Direction.RIGHT));
                    addObject(new Arrow(i, 9, Direction.RIGHT));
                }
                for (int i = 4; i < 9; i++){
                    addObject(new Arrow(3, i, Direction.DOWN));
                    addObject(new Arrow(10, i, Direction.LEFT));
                }
                //Monsters
                addMonster(new Monster(6, 4));
                addMonster(new Monster(7, 5));
                addMonster(new Monster(8, 6));
                addMonster(new Monster(8, 10));
                //Pickups
                addPickupItem(new PickupItem(4, 6, SCORE4, "purple.png"));
                addPickupItem(new PickupItem(5, 7, SCORE3, "green.png"));
                addPickupItem(new PickupItem(6, 8, SCORE2, "red.png"));
                addPickupItem(new PickupItem(4, 8, SCORE1, "orange.png"));
                //Blocks
                addObject(new Block(4, 5, false, true));
                addObject(new Block(5, 6, false, true));
                addObject(new Block(6, 7, false, false));
                addObject(new Block(7, 8, false, true));
                addObject(new Block(4, 7, false, true));
                addObject(new Block(5, 8, false, true));
                for (int i = 1; i < 11; i++){
                    addObject(new BlockArrow(i, 1, true, Direction.RIGHT));
                }
                //Player
                addPlayer(new Player(2, 6));
                break;
            case 7:
                state = State.WON;
                break;
        }
        

    }
    
}
