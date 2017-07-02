/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.model;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Martin
 */
public class Car {
    private int ID;                                                             //parametre vozdidla
    private int rychlost;                                                       //rychlost .... jedna z  moznosti
    private Last_station last_node;                                             //posledny uzol vozidla
    private String meno;                                                        //nazov vozidla
    private ArrayList<Reserved_time_window> plan;                               //plan vozidla -> ako z textaka
    private int x;                                                              //aktualna poloha x
    private int y;                                                              //a y
    private Color farba;
    
    public Car(int ID){
        this.ID = ID;
    }
    
    public Car(int ID,int rychlost, String meno, Color farba){
        this.ID = ID;
        this.rychlost = rychlost;
        this.meno = meno;
        this.farba = farba;
    }
    
    
    //getters and setters  
   public void setID(int ID){
       ID = this.ID;
   }
   
   public void setRychlost(int rychlost){
       this.rychlost = rychlost;
   }

   public int getID(){
       return ID;
   }
   
   public int getRychlost(){
       return rychlost;
   }

    public Last_station getLast_node() {
        return last_node;
    }

    public void setLast_node(Last_station last_node) {
        this.last_node = last_node;
    }

    public String getMeno() {
        return meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<Reserved_time_window> getPlan() {
        return plan;
    }

    public void setPlan(ArrayList<Reserved_time_window> plan) {
        this.plan = plan;
    }  

    public Color getFarba() {
        return farba;
    }

    public void setFarba(Color farba) {
        this.farba = farba;
    }
    
    
   
}
