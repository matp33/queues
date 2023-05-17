package interfaces;

import animations.Sprite;
import symulation.Painter;

public abstract class AnimatedAndObservable extends AnimatedObject implements Observable{

	private static final long serialVersionUID = 6113197386703740928L;

	public AnimatedAndObservable (Sprite sprite, Painter painter){
		super(sprite, painter);
	}
}
