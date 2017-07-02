/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.controller;

import bc.BC;
import static bc.BC.Station_size;
import static bc.BC.max_NO_of_stations;
import bc.model.Car;
import bc.model.Maps;
import bc.model.Road;
import bc.model.Station;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Martin
 */
public class win_control {
    private double time;
    private boolean pressed_play;
    
    public win_control(){
        this.time = 0;
    }
    
    public DefaultComboBoxModel fill_vehicle_list(Maps map){        //naplnenie comboboxov vozidiel
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        ArrayList<Car> cars = map.getCars();

        for(int i = 0; i < cars.size(); i++){
            model.addElement(Integer.toString(cars.get(i).getID()) + " " + cars.get(i).getMeno() );
        }

        return model;
    }
    
    public DefaultComboBoxModel fill_station_list(Maps map){        //naplnenie comboboxov stanic
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        ArrayList<Station> stations = map.getStations();
        
         for(int i = 0; i < stations.size(); i++){
            model.addElement(Integer.toString(stations.get(i).getID()) + " " + stations.get(i).getNazov() );
        }
        
        return model;
    }
    
    public void fill_plan_info(JTextArea area, Car c, ArrayList<Station> st){       //tu vyplnujem vypis planu vozidla
            String text = "";
            text = text + "  Štart\t  Koniec\t  Uzol\t  Cesta\n";
            for(int i = 0; i < c.getPlan().size(); i++){
                text = text + "  " + Integer.toString(c.getPlan().get(i).getStart_time()) + "\t  " + Integer.toString(c.getPlan().get(i).getEnd_time()) + "\t  " + st.get(c.getPlan().get(i).getNode()-1).getNazov() + "\t  " + Integer.toString(c.getPlan().get(i).getRoad()) + "\n";
                area.setText(text);
            }
            
            
    }

    public Car selected_vehicle(Maps m, JComboBox box, JLabel lab){     //funkcia vytvori z comboboxoveho vyberu vybrane vozidlo
        int i;
        String hodnoty = (String)box.getSelectedItem();
        String[] split;
        
        split = hodnoty.split("\\s+"); 
        
        for(i = 0; i < m.getCars().size(); i++){        //prechadzam zoznamom vozidiel a hladam to moje vozidlo
           if(m.getCars().get(i).getID() == Integer.parseInt(split[0]) && m.getCars().get(i).getMeno().equals(split[1])){
               lab.setText(Integer.toString(m.getCars().get(i).getLast_node().getID()) + " " + m.getCars().get(i).getLast_node().getName());
               break;
           }
       }
        
        return m.getCars().get(i);
    }
    
    public int create_new_car(Maps map, ButtonGroup speed, JTextField waittime, JTextField name, JComboBox station, JPanel p, JTextField starttime, JCheckBox blokujuci, JCheckBox asap, JTextField farba){
        String meno, stat = null; 
        String split[];
        int rychlost = 0, wt = 0, st = 0;
        boolean blok = false, asap1 = false;
        Traffic_control cntrl = new Traffic_control(map);
        
        Station selected;
        
        if((meno = name.getText()).equals("")){                                         //v tejto funkcii je zistenie udajov z volby pre vytvorenie noveho vozidla
            JOptionPane.showMessageDialog(p,"Zadajte meno vozidla.");
            return 0;
        }
        
        if(speed.getSelection().getActionCommand() != null){                    
            rychlost = Integer.valueOf(speed.getSelection().getActionCommand());
        }
        else{
            JOptionPane.showMessageDialog(p,"Vyberte rýchlosť vozidla.");
            return 0;
        }
        
          
        stat = (String)station.getSelectedItem();
        split = stat.split("\\s+");
        
        if((selected = map.getStations().get(Integer.valueOf(split[0])-1)) == null){
            JOptionPane.showMessageDialog(p,"Stanica nebola načítaná.");
            return 0;
        }
       
        if(!starttime.getText().equals(""))
                st = Integer.valueOf(starttime.getText());
            
        else{
                JOptionPane.showMessageDialog(p,"Zadajte počiatočný čas.");
                return 0;
        }
        asap1 = asap.isSelected();
        
        if(blokujuci.isSelected()){                                             //ak blokujem, nepotrebujem starttime a ASAP
             blok = true;
        }
        else{                                                                   //ak neblokujem, budem potrebovat aj nacitany starttime a ci je to ASAP
            if(!waittime.getText().equals("")){
                wt = Integer.valueOf(waittime.getText());
            }
            else{
                JOptionPane.showMessageDialog(p,"Zadajte dobu čakania.");
                return 0;
            }
        }
        
       
        
        int succ = cntrl.create_vehicle(map, meno, selected, rychlost, wt, st, blok, asap1, p, farba.getBackground());
        return succ;
    }
    
    public int create_new_schedule(Maps map, JComboBox dest, JComboBox car_sel, JTextField waittime, JCheckBox blokujuci, JPanel p){
        String meno, stat = null; 
        String split[];
        int wt = 0, st = 0;
        boolean blok = false;
        
        Station selected_s;
        Car selected_c = null;
        
        Traffic_control cntrl = new Traffic_control(map);                       //pre pristup do triedy s metodami pre modifikacie systemu
          
        if(dest.getItemCount() > 0){                                            //ak su nejake uzly na vyber
            stat = (String)dest.getSelectedItem();                              //ziskam si z vybraneho itemu...
            split = stat.split("\\s+");                                         //
        
            if((selected_s = map.getStations().get(Integer.valueOf(split[0])-1)) == null)   //ak vybrana stanica neexistuje -> toto mam pre istotu, v pripade chyby 
                JOptionPane.showMessageDialog(p,"Stanica nebola načítaná.");                  //tak mi to hodi informacne okno
        }
        else                                                                    
            return 0;                                                           //ak nie su uzly na vyber, vratim sa -> niet podla coho hladat
        
        if(car_sel.getItemCount() > 0){                                         //ak su nejake vozidla v zozname
            stat = (String)car_sel.getSelectedItem();                       
            split = stat.split("\\s+") ;
            
             for(int i = 0; i < map.getCars().size(); i++){
                if(map.getCars().get(i).getID() == Integer.valueOf(split[0]) && map.getCars().get(i).getMeno().equals(split[1])){
                    selected_c = map.getCars().get(i);
                }
            }
            
            if(selected_c == null)                                              //znova ak neexistuje vybrana stanica
                JOptionPane.showMessageDialog(p,"Stanica nebola načítaná.");      //tak dostanem info okno
        }
        else                                                                    //ak nie
            return 0;                                                           //nemam co spracovat

        if(blokujuci.isSelected()){                                             //ak je nastaveny boolean pre blokovanie
            blok = true;
        }
            
        else{                                                                   //ak nie je zakliknuty...
            if(!waittime.getText().equals("")){                                 //ak nie je prazdny chlievik pre waittime
                wt = Integer.valueOf(waittime.getText());                       //ziskam si waittime -> nemam osetrenie oba pre inty
            }
            else{                                                                //ak nie je zadany cas
                JOptionPane.showMessageDialog(p,"Zadajte dobu čakania.");         //informujem .... ale default cas je hore nastaveny ako 0
                return 0;
            }
        }
        
        return cntrl.create_schedule(map, selected_s, selected_c, wt, blok, p); //idem do metody pre vypocet a vytvorenie scenara
     }
    
    public int delete_car(Maps map, JComboBox car_sel, JPanel p){       //spracovanie udajov z okna pre mazanie vozidla
        String split[];
        String stat;
        Car selected = null;
        
         Traffic_control cntrl = new Traffic_control(map);
        
        if(car_sel.getItemCount() > 0){                 //zistim si, ci bolo nieco vybrane z listu vozidiel
            stat = (String)car_sel.getSelectedItem();
            split = stat.split("\\s+");                 //a rozoberiem si vybrane vozidlo
            
            for(int i = 0; i < map.getCars().size(); i++){                      //ak sedi vozidlo
                if(map.getCars().get(i).getID() == Integer.valueOf(split[0]) && map.getCars().get(i).getMeno().equals(split[1])){
                    selected = map.getCars().get(i);
                }
            }
            if(selected == null){                                               //ak nesedi stanica - uzol
                JOptionPane.showMessageDialog(p,"Stanica nebola načítaná.");
                return -1;
            }
        }
        else
            return 0;
        
        return cntrl.delete_vehicle(map, selected);
    }
    
    public int save_map(Maps map, JButton button) throws URISyntaxException, IOException{       //zapis do suboru
       int ret;
       JFileChooser fc = new JFileChooser();                                                    //spustim File Cooser
       fc.setCurrentDirectory(new File(BC.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile());        //nastavim si ho na directory s BC.jar suborom
       
       ret = fc.showSaveDialog(button);                                                 //zobrazim File Chooser
       if(ret == JFileChooser.APPROVE_OPTION){                                          //ak je to potvrdenie
           FileWriter writer = new FileWriter(fc.getSelectedFile() + ".txt");           //zapisem do suboru s priponou .txt
           for(int i = 0; i < map.getStations().size(); i++){
               writer.write(map.getStations().get(i).getNazov() + " " + map.getStations().get(i).getPolohaX() + " " + map.getStations().get(i).getPolohaY() + "\n");        //kazdu stanicu
           }
           
           writer.write("\n");
           
           Road rd[][] = map.getRoads();                                            //zapisem hranu, ale....
           for(int i = 1; i < max_NO_of_stations * max_NO_of_stations; i++){        //hladam podla IDcka, lebo inak to robi bordel
               int found = 0;
               for(int j = 0; j < rd.length; j++){                                  //prechadzam maticu hran
                   for(int k = 0; k < rd.length; k++){
                   
                        if(rd[j][k].getWeight() > 0 && rd[j][k].getID() == i){      //ak sedi index a je to hrana
                        writer.write(rd[j][k].getWeight() + " " + j + " " + rd[j][k].getFrom_dir() + " " + k + " " + rd[j][k].getTo_dir() + "\n");      //zapisem
                        found = 1;
                        break;
                        }
                    }
                   if(found == 1)
                       break;
               }
               if(found == 0)       //hladam, len kym nieco najdem...teda ak je to hrana 12 pri pocte 1 hran, nenajde nic a ja koncim cyklus
                   break;
           }
           
           writer.write("\n");
           
           
           for(int i = 0; i < map.getCars().size(); i++){               //nakoniec zapisem vozidla s planmi
               writer.write(map.getCars().get(i).getRychlost() + " " + map.getCars().get(i).getMeno() + " " + map.getCars().get(i).getFarba().getRed() + " " + map.getCars().get(i).getFarba().getGreen() + " " + map.getCars().get(i).getFarba().getBlue() + "\n");
               for(int j = 0; j < map.getCars().get(i).getPlan().size(); j++){
                   writer.write(map.getCars().get(i).getPlan().get(j).getStart_time() + " " + map.getCars().get(i).getPlan().get(j).getEnd_time() + " " + map.getCars().get(i).getPlan().get(j).getNode() + " " + map.getCars().get(i).getPlan().get(j).getRoad() + "\n");
               }
               if(i < map.getCars().size() -1)
               writer.write("#\n");                     //kazde vozidlo je oddelene #
           }
           
           writer.write("\n");
           writer.close();
       }
       else if(ret == JFileChooser.ERROR || ret == JFileChooser.CANCEL_OPTION)
           return -1;
       
       return 1;
    }
    
    public void updateCoords(Maps m){
        ArrayList<Station> stations = m.getStations();
        ArrayList<Car> cars = m.getCars(); 
        Road[][] roads = m.getRoads();
        int to_node_dir, from_node_dir = 0;
        Station in;
        
        for(int i = 0; i < cars.size(); i++){                                   //pre kazde vozidlo
            for(int j = 0; j < cars.get(i).getPlan().size(); j++){              //prejdem jeho cely plan
                if(cars.get(i).getPlan().get(j).getStart_time() <= this.time && (cars.get(i).getPlan().get(j).getEnd_time() >= this.time ||  j == cars.get(i).getPlan().size()-1)){         //ak je vozdilo v uzle
                    if(j != 0){                                                                                                                     //a ak to nie je pociatocny
                        if( j != cars.get(i).getPlan().size()-1){                                                                                   //ak to nie je koncovy uzol
                            in = stations.get(cars.get(i).getPlan().get(j).getNode()-1);                                                            //zistim si uzol v ktorom budem pocitat polohu vozidla
                            to_node_dir = roads[in.getID()][cars.get(i).getPlan().get(j-1).getNode()].getFrom_dir();                                //zistim, z ktoreho smeru prislo
                            from_node_dir = roads[in.getID()][cars.get(i).getPlan().get(j+1).getNode()].getFrom_dir();                              //a ktorym smerom ma namieren dalej
                            
                            if( (double)this.time < (double)cars.get(i).getPlan().get(j).getStart_time() + ((double)Station_size/(double)cars.get(i).getRychlost())){
                                if( cars.get(i).getPlan().get(j-1).getNode() == in.getID()){
                                    
                                        for(int k = 1; k < max_NO_of_stations; k++){                                           
                                                if(roads[in.getID()][k].getID() == cars.get(i).getPlan().get(j-1).getRoad()){
                                                    to_node_dir = roads[in.getID()][k].getFrom_dir();
                                                    break;
                                                }
                                        }
                                }
                                
                                double time_part = ((double)this.time - (double)cars.get(i).getPlan().get(j).getStart_time()) / ((double)cars.get(i).getPlan().get(j).getStart_time() + ((double)Station_size/(double)cars.get(i).getRychlost()) - (double)cars.get(i).getPlan().get(j).getStart_time());
                                cars.set(i, getCoords_node(1,to_node_dir, cars.get(i), in, time_part));            //vypocet suradnic cez linearnu interpolaciu
                                break;
                            }
                            else if((double)this.time > (double)cars.get(i).getPlan().get(j).getEnd_time() - ((double)Station_size/(double)cars.get(i).getRychlost())){
                                if( cars.get(i).getPlan().get(j+1).getNode() == in.getID()){
                                        for(int k = 1; k < max_NO_of_stations; k++){                                           
                                                if(roads[in.getID()][k].getID() == cars.get(i).getPlan().get(j).getRoad()){
                                                    from_node_dir = roads[in.getID()][k].getFrom_dir();
                                                    break;
                                                }
                                        }
                                }
                                    double time_part = 1.0 - ((double)this.time - (double)cars.get(i).getPlan().get(j).getStart_time()) / ((double)cars.get(i).getPlan().get(j).getEnd_time() - ((double)cars.get(i).getPlan().get(j).getStart_time()));
                                    cars.set(i, getCoords_node(1,from_node_dir, cars.get(i), in, time_part));            //vypocet suradnic cez linearnu interpolaciu 
                                    break;
        
                            }
                                        
                            else{                                                                       //je v strede bodu
                                cars.get(i).setX(in.getPolohaX());                                  
                                cars.get(i).setY(in.getPolohaY());                                      //nemusim pocitat polohu
                            }
                            break;
                        }
                        else{                                                                                                                       //ak to je koncovy uzol
                            in = stations.get(cars.get(i).getPlan().get(j).getNode()-1);                                                            //zistim si uzol v ktorom budem pocitat polohu vozidla
                            to_node_dir = roads[cars.get(i).getPlan().get(j).getNode()][cars.get(i).getPlan().get(j-1).getNode()].getFrom_dir();    //zistim, z ktoreho smeru prislo
                            
                            if( this.time <= (double)cars.get(i).getPlan().get(j).getStart_time() + ((double)Station_size/(double)cars.get(i).getRychlost())){
                                double time_part = ((double)this.time - (double)cars.get(i).getPlan().get(j).getStart_time()) / ((double)cars.get(i).getPlan().get(j).getStart_time() + ((double)Station_size/(double)cars.get(i).getRychlost()) - (double)cars.get(i).getPlan().get(j).getStart_time());
                                cars.set(i, getCoords_node(1,to_node_dir, cars.get(i), in, time_part));            //vypocet suradnic cez linearnu interpolaciu
                                break;
                            }
                            else{  
                                if(cars.get(i).getPlan().get(j).getEnd_time() == -1){                                //zistim, ci je blokujuci alebo nie
                                        cars.get(i).setX(in.getPolohaX());                                           //obsadi do nekonecna    
                                        cars.get(i).setY(in.getPolohaY()); 
                                        break;
                                }
                                else if(this.time > cars.get(i).getPlan().get(j).getEnd_time()){
                                        cars.get(i).setX(-1);                                                        //po konecnom case sa odstrani z mapy                               
                                        cars.get(i).setY(-1);   
                                        break;
                                }
                            }
                            
                        }
                    }
                    else if(j == 0 && cars.get(i).getPlan().size() != 1 && (double)cars.get(i).getPlan().get(0).getStart_time() <= time){                   //je prvy, ale nie je zaroven aj posledny
                        in = stations.get(cars.get(i).getPlan().get(j).getNode()-1);
                        from_node_dir = roads[in.getID()][cars.get(i).getPlan().get(j+1).getNode()].getFrom_dir();                                          //a ktorym smerom ma namieren dalej
                        
                        if((double)cars.get(i).getPlan().get(0).getStart_time() == -1){
                            cars.get(i).setX(in.getPolohaX());                                                                                              //a uz v nom iba cakam
                                cars.get(i).setY(in.getPolohaY());                                                                                          //nemusim pocitat polohu
                                break;
                        }
                        
                        double time_part = 1.0 - ((double)this.time - (double)cars.get(i).getPlan().get(j).getStart_time()) / ((double)cars.get(i).getPlan().get(j).getEnd_time() - ((double)cars.get(i).getPlan().get(j).getStart_time()));
                        cars.set(i, getCoords_node(1,from_node_dir, cars.get(i), in, time_part));            //vypocet suradnic cez linearnu interpolaciu 
                        break;
                    }
                    else if(j == 0 && cars.get(i).getPlan().size() == 1 && (double)cars.get(i).getPlan().get(0).getStart_time() <= time){              //ak je prvy aj posledny, len caka v uzle
                        in = stations.get(cars.get(i).getPlan().get(j).getNode()-1);            //zistim si, ktory je to node
                            if(this.time < cars.get(i).getPlan().get(j).getEnd_time() || cars.get(i).getPlan().get(0).getEnd_time() == -1){
                                cars.get(i).setX(in.getPolohaX());                                                                                      //a uz v nom iba cakam
                                cars.get(i).setY(in.getPolohaY());                                                                                      //nemusim pocitat polohu
                                break;
                            }
                            else{
                                cars.get(i).setX(-1);                                                                                                   //potom vozidlo zmizne
                                cars.get(i).setY(-1);
                                break;
                            }
                    
                            
                    
                }
                /*else if(cars.get(i).getPlan().get(j).getStart_time() >= time && cars.get(i).getPlan().get(j-1).getEnd_time() == -1 && cars.get(i).getPlan().get(j-1).getNode() == in.getID()){
                            double time_part = 1.0 - ((double)this.time - (double)cars.get(i).getPlan().get(j).getStart_time()) / ((double)cars.get(i).getPlan().get(j).getEnd_time() - ((double)cars.get(i).getPlan().get(j).getStart_time()));
                            from_node_dir = roads[in.getID()][cars.get(i).getPlan().get(j+1).getNode()].getFrom_dir();  //a ktorym smerom ma namieren dalej
                            
                            
                            cars.set(i, getCoords_node(1,from_node_dir, cars.get(i), in, time_part));            //vypocet suradnic cez linearnu interpolaciu 
                }*/
                }
            //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                else{                                                                                                                                   //ak nie je v uzle
                    if(j > 0){                                                                                                                          //ak to nie je pred zaciatkom existencie vozidla
                        if(cars.get(i).getPlan().get(j).getStart_time() >= this.time && cars.get(i).getPlan().get(j-1).getEnd_time() <= this.time){     //a ak je cas simulacie medzi oknami
                                Station to = stations.get(cars.get(i).getPlan().get(j).getNode()-1);                                                    //zistim si, z ktoreho uzla vyrazim
                                Station from = stations.get(cars.get(i).getPlan().get(j-1).getNode()-1);                                                //a do ktoreho sa dostavam
                                int to_dir = roads[to.getID()][from.getID()].getFrom_dir();                                                             //zistim, z ktoreho smeru prislo
                                int from_dir = roads[from.getID()][to.getID()].getFrom_dir();                                                           //a ktorym smerom ma namieren dalej
                                double time_part = ((double)this.time - (double)cars.get(i).getPlan().get(j-1).getEnd_time()) / ((double)cars.get(i).getPlan().get(j).getStart_time() - (double)cars.get(i).getPlan().get(j-1).getEnd_time());
                                
                                if( to.getID() == from.getID()){                                                //ak su dva plany v ramci jedneho uzla -> uhybanie sa na drahu alebo zmiznutie vozidla
                                    if( cars.get(i).getPlan().get(j-1).getRoad() == -1){                    //ak je to zmiznutie vozidla...
                                        cars.get(i).setX(-1);                                                   //nebudem vozidlo zobrazovat
                                        cars.get(i).setX(-1);
                                        break;
                                    }
                                        for(int k = 1; k < max_NO_of_stations; k++){                            //inak normalne pocitam cestu na                                 
                                                if(roads[from.getID()][k].getID() == cars.get(i).getPlan().get(j-1).getRoad()){
                                                    from_dir = roads[from.getID()][k].getFrom_dir();
                                                    break;
                                                }
                                        }
                                }
                                cars.set(i, getCoords_road(1,from_dir, to_dir, cars.get(i), from, to, time_part));            //vypocet suradnic cez linearnu interpolaciu
                                break;
                        }
                    }
                    else if(this.time < cars.get(i).getPlan().get(0).getStart_time()){
                        cars.get(i).setX(-1);                                                   //inak nebudem vozidlo zobrazovat
                        cars.get(i).setX(-1);
                        break;
                    }
                    
                    
                }
            }
        }
        
        m.setCars(cars);
    }
    
    public Car getCoords_node(int mode, int dir, Car c, Station node, double time){     //pohyb vo vnutri uzla...vypocet polohy vozidla
        int moveX = 0;
        int moveY = 0;
        
                    switch(dir){
                        case 1:
                            moveY = - Station_size*3;
                            break;
                        case 2:
                            moveX =2 -Station_size*3;
                            break;
                        case 3:
                            moveY = Station_size*3;
                            break;
                        case 4:
                            moveX = Station_size*3;
                            break;
                    }
                    
            if(mode == 1){
                c.setX((int) Math.round(lin_int(node.getPolohaX() + moveX, node.getPolohaX(), time)));
                c.setY((int) Math.round(lin_int(node.getPolohaY() + moveY, node.getPolohaY(), time)));
            }
            
            return c;
    }
    
    public Car getCoords_road(int mode, int dir_from, int dir_to, Car c, Station from, Station to, double time){
        int fromX = 0;
        int fromY = 0;
        int toX = 0;
        int toY = 0;
        
                    switch(dir_from){                       //podla smeru opustenia uzla zistim posun
                        case 1:
                            fromY = - Station_size*3;
                            break;
                        case 2:
                            fromX =2 -Station_size*3;
                            break;
                        case 3:
                            fromY = Station_size*3;
                            break;
                        case 4:
                            fromX = Station_size*3;
                            break;
                    }
                    
                    switch(dir_to){
                        case 1:
                            toY = - Station_size*3;
                            break;
                        case 2:
                            toX =2 -Station_size*3;
                            break;
                        case 3:
                            toY = Station_size*3;
                            break;
                        case 4:
                            toX = Station_size*3;
                            break;
                    }
                    
            if(from.getID() == to.getID()){         //ak je to posun v ramci jedneho uzla...
                c.setX(from.getPolohaX() + fromX);
                c.setY(from.getPolohaY() + fromY);
            }
            else{                                   //ak medzi dvoma uzlami...
                c.setX((int) Math.round(lin_int(from.getPolohaX() + fromX, to.getPolohaX() + toX, time)));      //ziskam zaokruhlene suradnice z linearnej interpolacie
                c.setY((int) Math.round(lin_int(from.getPolohaY() + fromY, to.getPolohaY() + toY, time)));
            }
            
            return c;
    }
    
    public double lin_int(int fromCoord, int toCoord, double time){       //procedura linearnej interpolacie...
        double new_coord;
        
        new_coord = fromCoord + time * (toCoord - fromCoord);       //z povodnej polohy, cielovej polohy a casu vypocita polohu v case
        
        return new_coord;
    }
    
    //---------------------------------------
    //manipulacia s casom
    public void play(Maps map, ButtonGroup g, JLabel lab){              //stlacenie play a pause
        
        
        Thread t = new Thread(() -> {
            double timer = 0.0;
            while(true){
                timer += 0.1;
                if(timer % 1 == 0){
                    inc_time(map, g);
                    lab.setText(Double.toString(this.time));
                    timer = 0;
                    try {
                        Thread.sleep(750);                      //skok kazdu 0.75 sekundu
                    } catch (InterruptedException ex) {
                        Logger.getLogger(win_control.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(pressed_play == false){
                        break;
                    }
                }
            }
        });
        t.start();
    
        
     
    }
    
    public void inc_time(Maps m, ButtonGroup g){        //prirata cas podla buttonu
        double jump = 1;
        jump = get_selected_btn(g);
        
        this.time += jump;
        updateCoords(m);
    }
    
    public void dec_time(Maps m, ButtonGroup g){        //odrata cas podla buttonu
        double jump = 1;
        for (Enumeration<AbstractButton> buttons = g.getElements(); buttons.hasMoreElements();) {
            AbstractButton btn = buttons.nextElement();

            if (btn.isSelected()) {
                jump = Double.parseDouble(btn.getText());
                break;
            }
        }
        if(this.time - jump > 0)
            this.time -= jump;
        else
            this.time = 0;
        updateCoords(m);;
    }
    
    public double get_selected_btn(ButtonGroup g){      //ziska vybrany button z buttongroupu
        double jump = 1;
        for (Enumeration<AbstractButton> buttons = g.getElements(); buttons.hasMoreElements();) {
            AbstractButton btn = buttons.nextElement();

            if (btn.isSelected()) {
                jump = Double.parseDouble(btn.getText());
                break;
            }
        }
        return jump;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setPressed_play(boolean pressed_play) {
        this.pressed_play = pressed_play;
    }

    public boolean isPressed_play() {
        return pressed_play;
    }
    
    
}
