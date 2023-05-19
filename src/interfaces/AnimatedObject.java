package interfaces;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JComponent;

import sprites.SpriteManager;
import sprites.SpriteType;
import symulation.Painter;
import animations.Animation;
import sprites.Sprite;

public abstract class AnimatedObject extends JComponent {
	
	private static final long serialVersionUID = 1L;
	private static final int frameDelay=20;
	
	protected Animation [] animations;
	protected Animation currentAnimation;
	protected Point position;
	protected Painter painter;
	protected int max;
	protected int state;
	protected int spriteWidth;
	protected int spriteHeight;
	protected Dimension size;
	private SpriteManager spriteManager;
	
	
	public AnimatedObject (SpriteType spriteType, Painter painter){
		
		try{
			spriteManager = new SpriteManager();
			Sprite sprite = spriteManager.getSprite(spriteType);
			BufferedImage b=sprite.getSprite(0, 0);
			size=new Dimension(b.getWidth(), b.getHeight());
			this.painter=painter;
			position=new Point();
			initializePosition();

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
	public abstract void scheduleMoving();
	
	public BufferedImage getImage(){
		return currentAnimation.getSprite();
	}
	
	public Point getPosition(){
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
		painter.repaint(position.x,position.y,spriteWidth,spriteHeight);
        g2d.drawImage(currentAnimation.getSprite(),position.x, position.y,null);
//        System.out.println("#"+position);
	}

}
