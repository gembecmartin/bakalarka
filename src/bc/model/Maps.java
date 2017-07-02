/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.model;

import static bc.BC.max_NO_of_stations;
import static bc.BC.max_NO_of_windows;
import java.util.ArrayList;
import java.io.FileNotFoundException;
/**
 *
 * @author Martin
 */
public final class Maps { //trieda pre graf tovarne...taktiez tu budu suradnice pre vykreslenie objektov na main obrazovke
   
   private ArrayList<Station> stations;                                                                                          //zoznam stanic
   private ArrayList<Car> cars;                                                                                                  //zoznam vozidiel
   private Free_time_window[][] free_windows = new Free_time_window[max_NO_of_stations+1][max_NO_of_windows+1];                  //zoznam volnych okien
   private Reserved_time_window[][] reserved_windows = new Reserved_time_window[max_NO_of_stations+1][max_NO_of_windows+1];      //zoznam rezervovanych okien
   private Road[][] roads = new Road[max_NO_of_stations][max_NO_of_stations];                                                    //matica hran
   private int NO_reserved[];                                                                                                    //budem si pamatat pre kazdy uzol pocet rezervovnych okien v nom
   
    public Maps() throws FileNotFoundException{  //default mapa
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }

    public Free_time_window[][] getFree_windows() {
        return free_windows;
    }

    public void setFree_windows(Free_time_window[][] free_windows) {
        this.free_windows = free_windows;
    }

    public Reserved_time_window[][] getReserved_windows() {
        return reserved_windows;
    }

    public void setReserved_windows(Reserved_time_window[][] reserved_windows) {
        this.reserved_windows = reserved_windows;
    }

    public Road[][] getRoads() {
        return roads;
    }

    public void setRoads(Road[][] roads) {
        this.roads = roads;
    }
    
    
    
    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public int[] getNO_reserved() {
        return NO_reserved;
    }

    public void setNO_reserved(int[] NO_reserved) {
        this.NO_reserved = NO_reserved;
    }
    
    
   
}
