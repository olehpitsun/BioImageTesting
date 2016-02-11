package sample.util;

/**
 * Created by oleh on 11.02.16.
 */
public class PreProcessingParam {

    public String researchName;
    public String researchPath;
    public String contrast;
    public String bright;
    public String dilate;
    public String erode;

    public void setResearchName(String resName){
        this.researchName = resName;
    }

    public String getResearchName(){
        return this.researchName;
    }

    public void setResearchPath(String resPath){
        this.researchPath = resPath;
    }

    public String getResearchPath(){
        return this.researchPath;
    }

    public void setContrast(String contrast){
        this.contrast = contrast;
    }

    public String getContrast(){
        return this.contrast;
    }

    public void setBright(String bright){
        this.bright = bright;
    }

    public String getBright(){
        return this.bright;
    }

    public void setDilate(String dilate){
        this.dilate = dilate;
    }

    public String getDilate(){
        return this.dilate;
    }

    public void setErode(String erode){
        this.erode = erode;
    }

    public String getErode(){
        return this.erode;
    }
}
