package utilities;

public class MoveDirection {
	
	private int horizontalDirection;
	private int verticalDirection;
	
	public MoveDirection(int verticalDirection, int horizontalDirection){
		this.verticalDirection=verticalDirection;
		this.horizontalDirection=horizontalDirection;
	}
	
	public int getHorizontalDirection(){
		return horizontalDirection;
	}
	
	public int getVerticalDirection(){
		return verticalDirection;
	}
	
	
	

}
