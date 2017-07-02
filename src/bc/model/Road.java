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
public class Road {
    int ID;         //ID cesty
    int weight;     //vaha cesty
    int from_dir;   //i index
    int to_dir;     //j index
    
    public Road(int ID, int weight, int from_dir, int to_dir){
        this.ID = ID;
        this.weight = weight;
        this.from_dir = from_dir;
        this.to_dir = to_dir;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }  

    public int getFrom_dir() {
        return from_dir;
    }

    public void setFrom_dir(int from_dir) {
        this.from_dir = from_dir;
    }

    public int getTo_dir() {
        return to_dir;
    }

    public void setTo_dir(int to_dir) {
        this.to_dir = to_dir;
    }
    
    
}
