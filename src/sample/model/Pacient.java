package sample.model;

import javafx.beans.property.*;

/**
 * Created by oleh on 22.03.2016.
 */
public class Pacient {

    private final StringProperty pacientSurname;
    private final StringProperty pacientName;
    private final StringProperty pacientMiddleName;
    private final StringProperty pacientCartId;


    public Pacient(String pacientSurname, String pacientName, String pacientMiddleName, String pacientCartId ){

        this.pacientSurname = new SimpleStringProperty(pacientSurname);
        this.pacientName = new SimpleStringProperty(pacientName);
        this.pacientMiddleName = new SimpleStringProperty(pacientMiddleName);
        this.pacientCartId = new SimpleStringProperty(pacientCartId);
    }


    public void setPacientSurname(String pacientSurname){
        this.pacientSurname.set(pacientSurname);
    }

    public String getPacientSurname(){
        return pacientSurname.get();
    }

    public StringProperty pacientSurnameProperty() {
        return pacientSurname;
    }

    public void setPacientName(String pacientName){
        this.pacientName.set(pacientName);
    }

    public String getPacientName(){
        return pacientName.get();
    }

    public StringProperty pacientNameProperty() {
        return pacientName;
    }

    public void setPacientMiddleName(String pacientMiddleName){
        this.pacientMiddleName.set(pacientMiddleName);
    }

    public String getPacientMiddleName(){
        return pacientMiddleName.get();
    }

    public StringProperty pacientMiddleNameProperty() {
        return pacientMiddleName;
    }

    public void setPacientCartId(String pacientCartId){
        this.pacientCartId.set(pacientCartId);
    }

    public String getPacientCartId(){
        return pacientCartId.get();
    }

    public StringProperty pacientCartIdProperty() {
        return pacientCartId;
    }
}