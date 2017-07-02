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
public class Free_time_window {
    private int start_time;                 //pociatocny cas volneho okna
    private int end_time;                   //konecny cas volneho okna
    private int earliest_arrive;            //kedy sa najskor dostanem do tohto okna...
    private int route;                      //ktorou hranou sa do neho dostanem...
    private int from_window;                //odkial...
    private int from_node;                  //a z ktoreho volneho okna
    
    public Free_time_window(int start_time, int end_time, int earliest_arrive, int route){
        this.start_time = start_time;
        this.end_time = end_time;
        this.earliest_arrive = earliest_arrive;
        this.route = route;
    }
    
    public Free_time_window(int start_time, int end_time){  //free time window
        this.start_time = start_time;
        this.end_time = end_time;
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
  
    public int getEarliest_arrive() {
        return earliest_arrive;
    }

    public void setEarliest_arrive(int earliest_arrive) {
        this.earliest_arrive = earliest_arrive;
    }

    public int getRoute() {
        return route;
    }

    public void setRoute(int route) {
        this.route = route;
    }

    public int getFrom_window() {
        return from_window;
    }

    public void setFrom_window(int from_window) {
        this.from_window = from_window;
    }

    public int getFrom_node() {
        return from_node;
    }

    public void setFrom_node(int from_node) {
        this.from_node = from_node;
    }
    
    
}
