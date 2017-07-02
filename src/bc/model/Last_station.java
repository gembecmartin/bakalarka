/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.model;

/**
 *
 * @author Martin
 */
public class Last_station {
    private int ID;                         //posledny uzol...podobne, ako trieda Station
    private int from;
    private int to;
    private String name;
    private int rw_ID;
    
    public Last_station(int ID, int start_time, int end_time, String name, int rw){         //konstruktor pri pridavani poslednej stanice pri vytvarani auta
        this.ID = ID;
        this.from = start_time;
        this.to = end_time;
        this.name = name;
        this.rw_ID = rw;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRw_ID() {
        return rw_ID;
    }

    public void setRw_ID(int rw_ID) {
        this.rw_ID = rw_ID;
    }
    
    
       
}
