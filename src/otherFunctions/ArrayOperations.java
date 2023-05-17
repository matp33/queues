package otherFunctions;

public class ArrayOperations {
	
	public static double [][] removeZeros (double[][] array, int length){
		 
	        double [][] newArray = new double[length][2];
	        for (int i=0; i<length; i++){	            
                newArray[i][0] = array[i][0];
                newArray[i][1]=array[i][1]; 
	        }
	        return newArray;
	}

}
