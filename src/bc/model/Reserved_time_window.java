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
public class Reserved_time_window {     //trieda, ktora definuje, ako vyzera obsadene okno
    private int start_time;         //pociatocny cas rezervacie
    private int end_time;           //konecny cas rezervacie
    private Car reserved_by;        //ktore vozidlo rezervuje toto okno
    private int node;               //ktory node to je
    private int road;               //a ktora cesta
    
    public Reserved_time_window(int start_time, int end_time, Car car){
        this.start_time = start_time;
        this.end_time = end_time;
        this.reserved_by = car;
    }

    public Reserved_time_window(int start_time, int end_time, Car car, int node, int road){
        this.start_time = start_time;
        this.end_time = end_time;
        this.reserved_by = car;
        this.node = node;
        this.road = road;
    }
    
    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public Car getReserved_by() {
        return reserved_by;
    }

    public void setReserved_by(Car reserved_by) {
        this.reserved_by = reserved_by;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getRoad() {
        return road;
    }

    public void setRoad(int road) {
        this.road = road;
    }

    
    
    
    
}
