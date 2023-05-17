package interfaces;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JComponent;

import symulation.Painter;
import animations.Animation;
import animations.Sprite;

public abstract class AnimatedObject extends JComponent {
	
	private static final long serialVersionUID = 1L;
	private static final int frameDelay=20;
	
	protected Animation [] animations;
	protected Animation currentAnimation;
	protected Dimension position;
	protected Painter painter;
	protected int max;
	protected int state;
	protected int spriteWidth;
	protected int spriteHeight;
	protected Dimension size;
	
	
	public AnimatedObject (Sprite sprite, Painter painter){
		
		try{
			BufferedImage b=sprite.getSprite(0, 0);
			size=new Dimension(b.getWidth(), b.getHeight());
			this.painter=painter;
			position=new Dimension();
			initializePosition();
//			this.spriteWidth=spriteWidth;
//			this.spriteHeight=spriteHeight;
			
			animations = new Animation [sprite.getNumberOfRows()*sprite.getNumberOfColumns()];
			
			for (int i=0; i<sprite.getNumberOfRows(); i++ ){
//					System.out.println("i "+i);
					animations[i]=new Animation(sprite.getSprite(i), frameDelay);
				
			}
			max=animations.length;
			currentAnimation=animations[0];
		}
		catch (IOException exception){
			exception.printStackTrace(); // TODO save in log file
		}
		catch (Exception exception){
			exception.printStackTrace();
		}
		
	}
		
	
	
	protected abstract void initializePosition();
	
	public abstract void interrupt();
	public abstract void resume();
	
	public BufferedImage getImage(){
		return currentAnimation.getSprite();
	}
	
	public Dimension getPosition(){
		return position;
	}
	
	public Dimension getSize(){
		return size;
	}
	
	public void startDrawingMe(){
		painter.addObject(this); 
	}
	
	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;		
		painter.repaint(position.width,position.height,spriteWidth,spriteHeight);
        g2d.drawImage(currentAnimation.getSprite(),position.width, position.height,null); 
//        System.out.println("#"+position);
	}

}
