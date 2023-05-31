package interfaces;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import core.ChangeableObject;
import spring2.BeanRegistry;
import sprites.SpriteManager;
import symulation.Painter;
import animations.Animation;
import sprites.Sprite;

public abstract class AnimatedObject extends JComponent implements ChangeableObject {
	
	private static final long serialVersionUID = 1L;
	private static final int frameDelay=20;
	protected final Sprite sprite;

	protected Animation [] animations;
	protected Animation currentAnimation;
	protected Point position;
	protected int max;
	protected int state;
	protected int spriteWidth;
	protected int spriteHeight;
	protected Dimension size;

	public AnimatedObject (){

		sprite = SpriteManager.getSprite(getClass());
		this.spriteWidth = sprite.getWidth();
		this.spriteHeight = sprite.getHeight();
		BufferedImage b=sprite.getSprite(0, 0);
		size=new Dimension(b.getWidth(), b.getHeight());
		position=new Point();
		initializePosition();


		animations = new Animation [sprite.getNumberOfRows()*sprite.getNumberOfColumns()];

		for (int i=0; i<sprite.getNumberOfRows(); i++ ){
//					System.out.println("i "+i);
				animations[i]=new Animation(sprite.getSpriteFileName(),sprite.getSprite(i), frameDelay);

		}
		max=animations.length;
		currentAnimation=animations[0];

	}

	@Override
	public int getHeight() {
		return spriteHeight;
	}

	public void update() {};
	protected abstract void initializePosition();
	
	public abstract void interrupt(double timePassedSeconds);
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
	

	@Override
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;		
        g2d.drawImage(currentAnimation.getSprite(),position.x, position.y,null);
//        System.out.println("#"+position);
	}

}
