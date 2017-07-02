/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.controller;

import static bc.BC.max_NO_of_stations;
import static bc.BC.max_NO_of_windows;
import static bc.BC.stations_loaded;
import bc.model.Car;
import bc.model.Free_time_window;
import bc.model.Last_station;
import bc.model.Maps;
import bc.model.Reserved_time_window;
import bc.model.Road;
import bc.model.Station;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin
 */
public class Init {
        BufferedReader br;
        FileReader fr;
    
    public Init(String subor, Maps map) throws FileNotFoundException, IOException{
        init_fields(subor, map);
        loadMap(map);
        loadRoads(map);
        loadCars(map);
    }
    
    public void loadMap(Maps map) throws FileNotFoundException{                 //funkcia, ktorou sa nahravaju mapy z textakov
        ArrayList<Station> stat;        
        stat = new ArrayList<>();
        
        try {
        String hodnoty;                                                         //do premennej hodnoty budem nacitavat riadky z textaka
        int stanica_na_zapis = 1;                                               //counter pre stanice
        
        //---------------------------------------- zaciatok nacitavania
        hodnoty = br.readLine();                                                //nacitam riadok z textaka
        int x,y;                                                                //x, y budu sluzit pre nacitane suradnice
        String nazov;   
        String[] split;                                                         //split je pole stringov...tu rozdelim riadok na jednotlive hodnoty
        split = hodnoty.split("\\s+");                                          //delim podla medzery
        nazov = split[0];                                                       //nazov stanice bude prvy string
        x = Integer.parseInt(split[1]);                                         //nasleduje konverzia suradnic
        y = Integer.parseInt(split[2]);
        
        Station st = new Station(nazov,stanica_na_zapis,x,y);                   //vytvorim novu stanicu
        
        stat.add(st);                                                           //a pridam do zoznamu stanic
        
        while(!"".equals(hodnoty = br.readLine())){                             //toto robim, az kym nie som na prazdnom riadku
            stanica_na_zapis++;
            split = hodnoty.split("\\s+");
            nazov = split[0];
            x = Integer.parseInt(split[1]);
            y = Integer.parseInt(split[2]);
            st = new Station(nazov,stanica_na_zapis,x,y);      
            stat.add(st);
        }
        } catch (IOException ex) {
            Logger.getLogger(Maps.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        stations_loaded = stat.size();                          
        map.setStations(stat);                                                  //nakoniec ukladam nahrany zoznam stanic
    }

//----------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------
    //    NACITAVANIE VOZIDIEL
//----------------------------------------------------------------------------------------------    
    public void loadRoads(Maps map){                                            //dalej pokracujem nahranim ciest
        
        Road[][] road_matrix = map.getRoads();                                  //toto bude dvojrozmerne pole, nie Array List

        try {
        String hodnoty;
        int cesta_No = 1;
        
        int weight, from, from_dir, to, to_dir;
        String[] split;
        //---------------------------------------- zaciatok nacitavania
        hodnoty = br.readLine();                                                //znova nacitam riadok
        
        split = hodnoty.split("\\s+");                                          //rozdelim na hodnoty podla medzery
        weight = Integer.parseInt(split[0]);                                    //prva hodnota je vaha hrany
        from = Integer.parseInt(split[1]);                                      //nasleduje pociatocny uzol...
        from_dir = Integer.parseInt(split[2]);                                  //a smer z pociatocneho uzla
        to = Integer.parseInt(split[3]);                                        //nakoniec nahram koncovy uzol...
        to_dir = Integer.parseInt(split[4]);                                    //a smer z koncoveho uzla
        
        Road rd;
            rd = new Road(cesta_No,weight, from_dir, to_dir);                   //vsetko to nabijem do novej cesty
            road_matrix[from][to] = rd;                                         //a ulozim
            
            rd = new Road(cesta_No,weight, to_dir, from_dir);
            road_matrix[to][from] = rd;                                         //ukladam aj naopak...su predsa bidirectional :)
        
        while(!"".equals(hodnoty = br.readLine())){                             //opakujem proces, kym nenajdem prazdny riadok
            cesta_No++;                                                         //counter pre cesty
            split = hodnoty.split("\\s+");
            weight = Integer.parseInt(split[0]);
            from = Integer.parseInt(split[1]);
            from_dir = Integer.parseInt(split[2]);
            to = Integer.parseInt(split[3]);
            to_dir = Integer.parseInt(split[4]);
        
            rd = new Road(cesta_No,weight, from_dir, to_dir);
            road_matrix[from][to] = rd;
            
            rd = new Road(cesta_No,weight, to_dir, from_dir);
            road_matrix[to][from] = rd;
        }
        
         } catch (IOException ex) {
            Logger.getLogger(Maps.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        map.setRoads(road_matrix);                                              ///ulozim vysledne 2D pole
    }   
//----------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------
   // LOADING CARS
//----------------------------------------------------------------------------------------------
    private void loadCars(Maps map) throws IOException{                                 
        Free_time_window free[][] = map.getFree_windows();                      //matrix volnych okien - vytiahnem si z Maps
        Reserved_time_window reserved[][] = map.getReserved_windows();          //matrix obsadenych okien - vytiahnem si ich z Maps
        ArrayList<Car> cars = map.getCars();                                    //zoznam aut -> vytiahnute z Maps
        ArrayList<Station> stations = map.getStations();
        Last_station last_station = null;                                            //premenna typu station pre poslednu stanicu nahranu z textaka -> urci sa z nej aktualny cas
        
        System.out.println("\nLoading cars.");
        int temp[] = new int[stations_loaded+1];                                
        for(int i = 0; i<stations_loaded+1; i++){
            temp[i] = 0;
        }
        
        try {
        String hodnoty, name;
        int car_No = 0;                                                         //auta budu pridavane podla IDcka -> indexu
        int speed, startt, endt, station_name, road;
        int station;
        String[] split;                                                         //nahrane hodnoty rozoberiem a rozdelim premennym
        ArrayList<Reserved_time_window> rtw = new ArrayList<>();
        
        Color farba;
        int r = 255, g = 0, b = 0;
        
        if(!"".equals(hodnoty = br.readLine()) && hodnoty != null){             //nahranie hodnoty
        System.out.println(hodnoty);
        
        split = hodnoty.split("\\s+");                                          //split nahraneho stringu    
        speed = Integer.parseInt(split[0]);                                     //prva hodnota bude rychlost
        name = split[1];                                                        //nasleduje meno vozidla
        r = Integer.parseInt(split[2]);                                         //a farba v RGB
        g = Integer.parseInt(split[3]); 
        b = Integer.parseInt(split[4]); 
        farba = new Color(r,g,b);
        
        Car cr = new Car(car_No, speed, name, farba);                           //vytvorim si novy objekt pre vozidlo
        cr.setX(-1);
        cr.setY(-1);
        while(!"".equals(hodnoty = br.readLine()) && hodnoty != null){          //pokracujem az do konca suboru
            System.out.println(hodnoty);    
            if("#".equals(hodnoty)){                                            //vozidla su v textaku oddelene mriezkou
                cr.setLast_node(last_station);                                  //posledna nacitana stanica bude pridelena ku vozidlu
                cars.add(cr);                                                   //vozidlo ide do zoznamu vozidiel
                cr.setPlan(rtw);                                                //ulozim si trasu vozidla
                hodnoty = br.readLine();                                        //nacitam dalsi riadok...
                rtw = new ArrayList<>();
                split = hodnoty.split("\\s+");                                  //a rozoberiem nahrany string
                speed = Integer.parseInt(split[0]);
                name = split[1]; 
                
                car_No++;
                
                r = Integer.parseInt(split[2]); 
                g = Integer.parseInt(split[3]); 
                b = Integer.parseInt(split[4]); 
                farba = new Color(r,g,b);
                
                cr = new Car(car_No, speed, name, farba);                                    //vytvorim novy objekt vozidla
                cr.setX(-1);
                cr.setY(-1);
            }
            
            else{
                split = hodnoty.split("\\s+");                                  //ak to nie je mriezka, nacitavam plan vozidla a ulozim si nacitane hodnoty
                startt = Integer.parseInt(split[0]);                            
                endt = Integer.parseInt(split[1]);
                station_name = Integer.parseInt(split[2]);
                road = Integer.parseInt(split[3]);
                
                
                station = -1;
                for(int i= 0;i< map.getStations().size(); i++){                 //ziskanu stanicu z planu vozidla otestujem 
                    if(map.getStations().get(i).getID() == station_name){       //prechadzam vsetky nahrane stanice, a ak najdem zhodu...
                        station = i+1;                                          //ulozim si ju
                        break;
                    }
                }
                if(station == -1){                                              //ak takato stanica v zozname stanic nie je...    
                    System.out.println("Zadana neexistuhuca stanica");          //informujem pouzivatela 
                    return;                                                     //a koncim nacitavanie
                }
                
                
                
                last_station = new Last_station(station_name, startt, endt, stations.get(station_name-1).getNazov(), temp[station]);         //nacitana stanica moze byt posledna, preto si ukaladam nahrane hodnoty do objektu poslednej stanice

                reserved[station][temp[station]].setStart_time(startt);                                 //do prveho prazdneho rezervovaneho okna nahram udaje
                reserved[station][temp[station]].setEnd_time(endt);
                reserved[station][temp[station]].setReserved_by(cr);

                rtw.add(new Reserved_time_window(startt, endt, cr, station, road));                      //idem si vytvorit plan, presne ako je v textaku
                
                temp[station]++;
                
                 if(temp[station] > 1)
                    reserved = sort(temp, station, reserved);                   //sortujem
            }  
        }
        cr.setLast_node(last_station);
        cars.add(cr);
        cr.setPlan(rtw); 
        }
        } catch (IOException ex) {
            Logger.getLogger(Maps.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        map.setFree_windows(free);                          //ulozim si ziskane zoznamy a polia
        map.setReserved_windows(reserved);
        map.setNO_reserved(temp);
    }
    
    
    
    //inicializacia poli pre nasledne nacitanie zo suboru
    private void init_fields(String subor, Maps map) throws FileNotFoundException{      //inicializacia vsetkych zoznamov -> hlavne tych z triedy Maps
        map.setCars(new ArrayList<>());
        map.setStations(new ArrayList<>());
        Free_time_window free[][] = map.getFree_windows();
        Reserved_time_window reserved[][] = map.getReserved_windows();
         Road[][] road_matrix = map.getRoads();
         ArrayList<Car>cars;
        
        fr = new FileReader(subor);                     //nabijem do File Readera moj subor
        br = new BufferedReader(fr);                    //a budem ho citat
         
         cars = new ArrayList<>();
         
         for(int i= 0;i< max_NO_of_stations; i++){
            for(int j = 0;j< max_NO_of_stations; j++){
              if(i==j){  
                road_matrix[i][j] = new Road(0,-1,-1,-1);
              }
              else{
                road_matrix[i][j] = new Road(-1,-1,-1,-1);
              }
            }
         }
        
        for(int i= 0;i< max_NO_of_stations; i++){
            for(int j = 0;j< max_NO_of_windows; j++){
                free[i][j] = new Free_time_window(-1,-1,-1, -1);
                
                reserved[i][j] = new Reserved_time_window(-1,-1, new Car(-1));
            }
        }
        
        map.setFree_windows(free);
        map.setReserved_windows(reserved);
        map.setRoads(road_matrix);
        map.setCars(cars);
    }
    
    
    //bubblesort pre usporiadanie nacitanych obsadenych okien
    private Reserved_time_window[][] sort(int[] temp, int station, Reserved_time_window[][] rtw){   //klasicky bubblesort obsadenych okien
        Reserved_time_window temp1;
        
        for (int i=0; i<temp[station]-1; i++){
            for (int j=temp[station]-1; j>i; j--){
		if (rtw[station][j-1].getStart_time() > rtw[station][j].getStart_time()){
                    temp1 = rtw[station][j-1];
                    rtw[station][j-1] = rtw[station][j];
                    rtw[station][j] = temp1;
                }
            }
	}
    return rtw;
    }
  
    
}
