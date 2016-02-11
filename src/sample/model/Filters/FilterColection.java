package sample.model.Filters;


public class FilterColection {
    private String id;
    private String filterName;

    public FilterColection(String id, String filterName) {
        this.id = id;
        this.filterName = filterName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfilterName() {
        return filterName;
    }

    public void setfilterName(String filterName) {
        this.filterName = filterName;
    }

    @Override
    public String toString() {
        return id + " " + filterName;
    }
}