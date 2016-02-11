package sample.model.Segmentation;


public class SegmentationColection {
    private String id;
    private String segMetod;

    public SegmentationColection(String id, String filterName) {
        this.id = id;
        this.segMetod = filterName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSegMetod() {
        return segMetod;
    }

    public void setSegMetod(String filterName) {
        this.segMetod = filterName;
    }

    @Override
    public String toString() {
        return id + " " + segMetod;
    }
}