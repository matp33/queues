package otherFunctions;

public class Pair <Obj1, Obj2> {

	Obj1 obj1;
	Obj2 obj2;
	
	public Pair(Obj1 obj1, Obj2 obj2){
		this.obj1=obj1;
		this.obj2=obj2;
	}
	
	public Obj1 getObject1(){
		return obj1;
	}
	
	public Obj2 getObject2(){
		return obj2;
	}

}
