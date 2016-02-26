package sample.util;

/**
 * Created by oleh on 12.02.16.
 */
public class Estimate {

    public static Double firstHistAverageValue;
    public static Double secondHistAverageValue;
    public static Double histDifference;
    public static Boolean result;

    public static Long redAverage;
    public static Long greenAverage;
    public static Long blueAverage;
    public static float brightVal;

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

    public static void setRedAverage(Long redAverage1){
        redAverage = redAverage1;
    }

    public static Long getRedAverage(){
        return redAverage;
    }

    public static void setGreenAverage(Long greenAverage1){
        greenAverage = greenAverage1;
    }

    public static Long getGreenAverage(){
        return greenAverage;
    }

    public static void setBlueAverage(Long blueAverage1){
        blueAverage=blueAverage1;
    }

    public static Long getBlueAverage(){
        return blueAverage;
    }

    public static void setBrightVal(float brightVal1){
        brightVal = brightVal1;
    }

    public static float getBrightVal(){
        return brightVal;
    }
}
