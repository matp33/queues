

package otherFunctions;

public class ExpressionAnalyzer {

    public static boolean analyze(String expression){

        try {
            Double.parseDouble(expression);            
            return true;
        }
        catch(NullPointerException nll){
            nll.printStackTrace();
            return false;
        }
        catch (NumberFormatException nfe){
            nfe.printStackTrace();
            return false;
        }

    }

}
