/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Core;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author retirado de http://abrindoojogo.com.br
 */
public class ImageHandler {
    static private ImageHandler instance;
	private HashMap<String, BufferedImage> images;

	private ImageHandler() {
		images = new HashMap<String, BufferedImage>();
	}

	static public ImageHandler getInstance() {
		if (instance == null) {
			instance = new ImageHandler();
		}
		return instance;
	}

	public BufferedImage loadImage(String fileName) throws IOException {
		URL url = getClass().getResource("/" + fileName);
		if (url == null) {
			throw new RuntimeException("A imagem /" + fileName
					+ " não foi encontrada.");
		} else {
			String path = url.getPath();
			if (images.containsKey(path)) {
				return images.get(path);
			} else {
				BufferedImage img = ImageIO.read(url);
				images.put(path, img);
				return img;
			}
		}
	}

	public BufferedImage loadImage(String fileName, int x, int y, int w, int h)
			throws IOException {
		BufferedImage sheet = loadImage(fileName);
		BufferedImage img = sheet.getSubimage(x, y, w, h);
		return img;
	}

	public BufferedImage flipImage(BufferedImage image, boolean flipHorizontal, boolean flipVertical) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(w, h, BufferedImage.BITMASK);
		if (flipHorizontal) {
			if (flipVertical) {
				img.getGraphics().drawImage(image, w, h, -w, -h, null);
			} else {
				img.getGraphics().drawImage(image, w, 0, -w, h, null);
			}
		} else if (flipVertical) {
			img.getGraphics().drawImage(image, 0, h, w, -h, null);
		} else {
			img.getGraphics().drawImage(image, 0, 0, w, h, null);
		}
		return img;
	}
        
//        //Adicionado:
//        public BufferedImage rotateImage90Clockwise(BufferedImage image, int times) {
//		int w = image.getWidth();
//		int h = image.getHeight();
//		BufferedImage img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(w, h, BufferedImage.BITMASK);
//		for(int i = 0; i < times; i++){
//                    img.createGraphics().rotate(Math.toRadians(90), w/2, h/2);
//                    img.getGraphics().drawImage(image, 0, 0, w, h, null);
//                    
//                }
//                if(times <= 0){
//                    img.getGraphics().drawImage(image, 0, 0, w, h, null);
//                }
//		return img;
//	}

	public SpriteAnimation loadSpriteAnimation(String fileName, int qtdFrames) throws
			IOException {
		BufferedImage sheet = loadImage(fileName);
		if (sheet.getWidth() % qtdFrames != 0) {
			throw new RuntimeException("A imagem /" + fileName
					+ " não possui " + qtdFrames + " sprites de mesmo tamanho.");
		} else {
			SpriteAnimation anim = new SpriteAnimation();
			int w = sheet.getWidth() / qtdFrames;
			int h = sheet.getHeight();
			for (int i = 0; i < qtdFrames; i++) {
				anim.addImage(sheet.getSubimage(i * w, 0, w, h));
			}
			return anim;

		}
	}
}
