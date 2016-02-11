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
}
