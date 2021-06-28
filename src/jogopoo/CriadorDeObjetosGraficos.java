/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */
public class CriadorDeObjetosGraficos {
    
    public static void main(String[] args){
        System.out.println("NOVA MAIN - Criador de Objetos Gráficos");
        MapObject mobj;
        mobj = new Block(0, 0, false, false); //Vermelho
        saveObject(mobj, "vermelho");
        mobj = new Block(0, 0, true, false); //Verde
        saveObject(mobj, "verde");
        mobj = new Block(0, 0, false, true); //Vermelho móvel
        saveObject(mobj, "vermelho_movel");
        mobj = new Block(0, 0, true, true); //Verde móvel
        saveObject(mobj, "verde_movel");     
    }
    
    private static void saveObject(GameObject gameObj, String filename){
        filename = "GraphicObjects/" + filename;
        String ext = ".gob";
        try {
            FileOutputStream f_out = new FileOutputStream(filename + ext);
            try (ObjectOutputStream obj_out = new ObjectOutputStream(f_out)) {
                obj_out.writeObject(gameObj);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GameMap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static GameObject loadObject(String filename){
        FileInputStream f_in = null;
        try {
            f_in = new FileInputStream("GraphicObjects/"+filename);
            ObjectInputStream obj_in = new ObjectInputStream(f_in);
            GameObject fObj = (GameObject) obj_in.readObject();
            //getInstance();
            return fObj;
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
        return null;
    }
}
