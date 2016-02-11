package sample.tools;

/**
 * Created by oleh on 11.02.16.
 */
public class ValidateOperations {

    public static boolean isInt(String inputValue){
        Integer s;
        try {
            s = Integer.parseInt(inputValue);
            return true;
        } catch (NumberFormatException e) {

            return false;
        }
    }

    public static String filterAndSegValidate(String param){
        if(param.length()==0 || ValidateOperations.isInt(param) == false ){
            param ="3";
        }else{
            param=param;
        }
        return param;
    }
}
