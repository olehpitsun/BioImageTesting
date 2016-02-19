package sample.util;

import javax.swing.text.StyledEditorKit;

/**
 * Created by oleh on 12.02.16.
 */
public class Estimate {

    public static Double firstHistAverageValue;
    public static Double secondHistAverageValue;
    public static Double histDifference;
    public static Boolean result;

    public static void setFirstHistAverageValue(Double firstHistAverVal){
        firstHistAverageValue = firstHistAverVal;
    }

    public static  Double getFirstHistAverageValue(){
        return  firstHistAverageValue;
    }

    public static void setSecondHistAverageValue(Double secondHistAverVal){
        secondHistAverageValue = secondHistAverVal;
        //return ;
    }

    public static Double getSecondHistAverageValue(){
        return secondHistAverageValue;
    }

    public static Boolean checkHistogramValues(){

        histDifference = firstHistAverageValue - secondHistAverageValue;

        if(histDifference >= 0){
            result = true;
        }else{
            result = false;
        }
        return result;
    }
}
