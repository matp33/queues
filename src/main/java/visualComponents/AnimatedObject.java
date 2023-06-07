package visualComponents;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import core.ChangeableObject;
import sprites.SpriteManager;
import animations.Animation;
import sprites.Sprite;

public abstract class AnimatedObject extends JComponent implements ChangeableObject {
	
	private static final long serialVersionUID = 1L;
	private static final int frameDelay= 5;
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


		animations = new Animation [sprite.getNumberOfRows()*sprite.getNumberOfColumns()];

		for (int i=0; i<sprite.getNumberOfRows(); i++ ){
			animations[i]=new Animation(sprite.getSpriteFileName(),sprite.getSprite(i), frameDelay, sprite.isLoop());
		}
		max=animations.length;
		currentAnimation=animations[0];

	}

	public void setPosition(Point position) {
		this.position = position;
	}

	@Override
	public int getHeight() {
		return spriteHeight;
	}

	public void update() {};

	public abstract void interrupt();

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
