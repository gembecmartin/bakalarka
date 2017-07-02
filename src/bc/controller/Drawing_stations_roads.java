/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.controller;

import static bc.BC.Station_size;
import static bc.BC.max_NO_of_cars;
import static bc.BC.max_NO_of_stations;
import bc.model.Car;
import bc.model.Maps;
import bc.model.Road;
import bc.model.Station;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Martin
 */
public final class Drawing_stations_roads extends JPanel{
    private ArrayList<Station> stations;
    private ArrayList<Car> cars; 
    private Road[][] roads;  
    private final int display_size;
    JLabel labs_v[];
    JLabel labs_s[];
    
    public Drawing_stations_roads(Maps map){
        this.setLayout(null);                       //layout noveho panelu bude null -> pre kreslenie do panelu
        this.display_size = Station_size*3;         //velkost zobrazenych objektov bude trojnasobna, nez ich skutocny rozmer v pixeloch -> inak by boli velmi male
        initFields(map);                            //inicializujem polia
        render();                                   //a vykreslim pociatocny panel
        addLabels();                                //pridam labely vsetkym prvkom v paneli
        
        Thread t = new Thread(() -> {               //nasledne v osobitnom vlakne vykreslujem dookola vzdy nove okno, ked updatujem
            while(true){
                this.repaint();                     //prekreslenie
                updateLabels();                     //update labelov na nove pozicie na paneli
            }
        });
        t.start();
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;                             //budem pracovat s 2D grafikou
        g2.setColor(Color.BLACK);                                   //najprv vykreslim hrany

        for(int i = 1; i < max_NO_of_stations; i++){
            for(int j = 1; j < max_NO_of_stations; j++){
                               
                if(roads[i][j].getWeight() > 0){                    //ak je to validna hrana (jej vaha je nenulova)
                    int from_dir = roads[i][j].getFrom_dir();       //zistim si, z ktorej svetovej strany ide a kam
                    int to_dir = roads[i][j].getTo_dir();
                    
                    int fromX = 0;
                    int fromY = 0;
                    int toX = 0;
                    int toY = 0;
                    
                    switch(from_dir){
                        case 1:
                            fromY = -display_size;
                            break;
                        case 2:
                            fromX =2 -display_size;
                            break;
                        case 3:
                            fromY = display_size;
                            break;
                        case 4:
                            fromX = display_size;
                            break;
                    }
                    
                    switch(to_dir){
                        case 1:
                            toY = -display_size;
                            break;
                        case 2:
                            toX = -display_size;
                            break;
                        case 3:
                            toY = display_size;
                            break;
                        case 4:
                            toX = display_size;                              //zlozite vypocty pre vykreslenie....zistim si smer a o tolko posuniem vykreslenie
                            break;                                          //hranu vykreslim na 3x
                    }
                    g2.drawLine(stations.get(i-1).getPolohaX() + (display_size/2), stations.get(i-1).getPolohaY() + (display_size/2), stations.get(i-1).getPolohaX() + (display_size/2) + fromX, stations.get(i-1).getPolohaY() + (display_size/2) + fromY);    
                    g2.drawLine(stations.get(i-1).getPolohaX() + (display_size/2) + fromX, stations.get(i-1).getPolohaY() + (display_size/2) + fromY, stations.get(j-1).getPolohaX() + (display_size/2) + toX, stations.get(j-1).getPolohaY() + (display_size/2) + toY);
                    g2.drawLine(stations.get(j-1).getPolohaX() + (display_size/2), stations.get(j-1).getPolohaY() + (display_size/2), stations.get(j-1).getPolohaX() + (display_size/2) + toX, stations.get(j-1).getPolohaY() + (display_size/2) + toY);
                
                    JLabel lab = new JLabel(roads[i][j].getID() + " | " + roads[i][j].getWeight());             //nasledne nastavim label hrane...zalezi, ako je hrana orientovana....podla toho umiestnim label...
                    lab.setFont(new Font(lab.getFont().getName(), Font.PLAIN , 11));                            //...aby sa nekrizoval napis s hranou
                    if(stations.get(i-1).getPolohaX() == stations.get(j-1).getPolohaX())
                        lab.setBounds((stations.get(i-1).getPolohaX() + stations.get(j-1).getPolohaX())/2 + 15, (stations.get(i-1).getPolohaY() + stations.get(j-1).getPolohaY())/2, 40, 20);
                    else if(stations.get(i-1).getPolohaY() == stations.get(j-1).getPolohaY())
                        lab.setBounds((stations.get(i-1).getPolohaX() + stations.get(j-1).getPolohaX())/2, (stations.get(i-1).getPolohaY() + stations.get(j-1).getPolohaY())/2 + 10 , 40, 20);
                    else
                        lab.setBounds((stations.get(i-1).getPolohaX() + stations.get(j-1).getPolohaX())/2, (stations.get(i-1).getPolohaY() + stations.get(j-1).getPolohaY())/2, 40, 20);
                    
                    this.add(lab);
                }
            }
        }
        
        
         //vykreslim uzly modrou farbou -> polohu mam z textakov
         for(int i = 0; i < stations.size(); i++){
            Rectangle rc = new Rectangle(stations.get(i).getPolohaX(),stations.get(i).getPolohaY(),display_size, display_size);
            g2.setColor(Color.BLUE);
            g2.fill(rc);
        }
         
         
        //a vykreslim aj polohu vozidiel -> vypocitanu nizsie
         for(int i = 0; i < cars.size(); i++){
            g2.setColor(cars.get(i).getFarba());
            if(cars.get(i).getX() != -1 && cars.get(i).getY() != -1)
                g2.fillOval(cars.get(i).getX(),cars.get(i).getY(), display_size, display_size);
        }
    }
    
    public void addLabels(){    //umiesni napisy vozidiel a stanic 
               
        for(int i = 0; i < stations.size(); i++){
            labs_s[i] = new JLabel(stations.get(i).getNazov());
            this.add(labs_s[i]);
            labs_s[i].setBounds(stations.get(i).getPolohaX() + 20, stations.get(i).getPolohaY() - 15, 200, 20);
        }
        
        for(int i = 0; i < cars.size(); i++){
            labs_v[i] = new JLabel(cars.get(i).getMeno());
            this.add(labs_v[i]);
            if(cars.get(i).getX() != -1 && cars.get(i).getY() != -1)
            labs_v[i].setBounds(cars.get(i).getX()+ 5, cars.get(i).getY() + 20, 200, 20);
        }
    }
    
    public void updateLabels(){     //po vykresleni labelov ich tato funkcia updatuje a dava im nove polohy podla ich vozidiel

        for(int i = 0; i < cars.size(); i++){
            
            if(cars.get(i).getX() != -1 && cars.get(i).getY() != -1){              
                labs_v[i].setText(cars.get(i).getMeno());
                labs_v[i].setVisible(true);
                labs_v[i].setBounds(cars.get(i).getX() + 5, cars.get(i).getY() + 20, 200, 20);
            }
            else
                labs_v[i].setVisible(false);
                
        }
    }

    public void initFields(Maps map){           //inicializacna funkcia
        stations = map.getStations();           //beriem si polia z triedy Maps
        cars = map.getCars();
        roads = map.getRoads();
        
        labs_s = new JLabel[stations.size()];       //inicializujem polia pre napisy
        labs_v = new JLabel[max_NO_of_cars];
        for(int i = 0; i < max_NO_of_cars; i++){
            labs_v[i] = new JLabel();
            this.add(labs_v[i]);
        }
    }
    
    public void render(){
        repaint();
    }
}
