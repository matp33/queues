

package sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Sprite {

    private final BufferedImage spriteFile;
    private final int SPRITE_DIMENSION_HORIZONTAL;
    private final int SPRITE_DIMENSION_VERTICAL;
    private final int numberOfRows;
    private final int numberOfColumns;
    /**
     * 
     * @param width A single sprite width
     * @param height A single sprite height
     * @param spriteFileName Name of the file, which contains sprites, including extension. It should
     * be put into src directory.
     * @throws IOException If the file is not found
     */
    public Sprite (int width,int height, String spriteFileName)
                                                            throws IOException{
    	
    	System.out.println(spriteFileName +" wi "+width +" he "+height);
        spriteFile=ImageIO.read(new File("./src/images"+spriteFileName));
        numberOfColumns=spriteFile.getWidth()/width;
        numberOfRows=spriteFile.getHeight()/height;
        SPRITE_DIMENSION_HORIZONTAL=width;
        SPRITE_DIMENSION_VERTICAL=height;
    }   
    
    public int getNumberOfRows(){
    	return numberOfRows;
    }
    
    public int getNumberOfColumns(){
    	return numberOfColumns;
    }

    public BufferedImage getSprite(int rowNumber, int columnNumber) {

        return spriteFile.getSubimage(columnNumber*SPRITE_DIMENSION_HORIZONTAL, rowNumber*SPRITE_DIMENSION_VERTICAL,
                SPRITE_DIMENSION_HORIZONTAL, SPRITE_DIMENSION_VERTICAL);

    }
    
    public BufferedImage [] getSprite(int rowNumber) {

        BufferedImage [] sprites = new BufferedImage [spriteFile.getWidth()/SPRITE_DIMENSION_HORIZONTAL];
        int currentColumnNumber=0;
            while (currentColumnNumber<getNumberOfColumns()){
                sprites[currentColumnNumber]=spriteFile.getSubimage(currentColumnNumber*
                		SPRITE_DIMENSION_HORIZONTAL, rowNumber*SPRITE_DIMENSION_VERTICAL,
                		SPRITE_DIMENSION_HORIZONTAL, SPRITE_DIMENSION_VERTICAL);
                currentColumnNumber++;
            }
        return sprites;

    }



}
