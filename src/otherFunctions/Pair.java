package otherFunctions;

public class Pair <Obj1, Obj2> {
	
	Object obj1;
	Object obj2;
	
	public Pair(Object obj1, Object obj2){
		this.obj1=obj1;
		this.obj2=obj2;
	}
	
	public Object getObject1(){
		return obj1;
	}
	
	public Object getObject2(){
		return obj2;
	}

}
