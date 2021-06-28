/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogopoo;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Rafael
 */
public class FileChooserThread extends Thread {
    
    JFrame parent;
    static GameObject gameObject;
    
    public FileChooserThread(JFrame parent){
        this.parent = parent;
        this.gameObject = null;
    }
    
    @Override
    public void run(){
        JFileChooser chooser = new JFileChooser("./GraphicObjects");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "GOB Files", "gob");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           System.out.println("You chose to open this file: " +
                chooser.getSelectedFile().getName());
           //Block newBlock
           gameObject = (Block)CriadorDeObjetosGraficos.loadObject(chooser.getSelectedFile().getName());
        }
    }
}
