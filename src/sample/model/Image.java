package sample.model;

import sample.util.LocalDateAdapter;
import javafx.beans.property.*;
import org.opencv.core.Mat;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

public class Image {

    private static Mat image;

    static {
        image = new Mat();
    }

    public static Mat getImageMat(){
        return image;
    }

    public static Mat setImageMat(Mat newImage){
        image = newImage;
        return image;
    }


}