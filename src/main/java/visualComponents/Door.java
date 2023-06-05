package visualComponents;


import interfaces.AnimatedObject;

import java.util.TimerTask;

public class Door extends AnimatedObject {
	
	private static final long serialVersionUID = 1L;

	private boolean requestOpening;
	private boolean finishedOpening;

	private boolean isOpened;

	private TimerTask currentAnimationTask;
	
	public Door(){
		super();
			
		requestOpening=false;
		finishedOpening = true;

	}

	public void doOpening(){
		requestOpening = true;
		finishedOpening = false;

	}

	public void stopOpening(){
		if (currentAnimationTask != null){
			currentAnimationTask.cancel();
		}
		currentAnimation.stop();
	}
	
	public void interrupt(){
		if (requestOpening){
			stopOpening();
		}		
	}

    public boolean canClientEnter(){
		if (currentAnimation.getCurrentFrame()>=2){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public void update(double currentTimeSeconds) {
		if (requestOpening  && !finishedOpening){
			currentAnimation.normalDirection();
			currentAnimation.start();
			requestOpening = false;
		}
		if (currentAnimation.isFinalFrame() && !requestOpening){
			currentAnimation.reverseDirection();
			currentAnimation.start();
			finishedOpening = true;
		}
		currentAnimation.updateFrame();
//				//TODO state machine pattern


		if (canClientEnter() ){
			isOpened=true;
		}
		else{
			isOpened = false;
		}

	}

	public boolean isOpened() {
		return isOpened;
	}
}
