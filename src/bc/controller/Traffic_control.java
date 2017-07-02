

// pre kazdu stanicu na mape si vyhradime zoznamy volnych a obsadenych casovych okien
package bc.controller;

import static bc.BC.Station_size;
import static bc.BC.max_NO_of_stations;
import static bc.BC.max_NO_of_windows;
import bc.model.Car;
import bc.model.Free_time_window;
import bc.model.Last_station;
import bc.model.Maps;
import bc.model.Reachability;
import bc.model.Reserved_time_window;
import bc.model.Road;
import bc.model.Station;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Traffic_control{ //trieda pre vypocet trasy...tu bude dijsktra :)
    private Maps m;

    private int processing_tau;         //premenna, pre spracovavane tau v teste dostupnosti
    private int result_tau;             //vysledne tau z testu dostupnosti
    private int result_lambda;          //vysledna lambda z testu dostupnosti
    private int alpha;                  //hodnota cesty z bodu
    
    ArrayList<Reachability> reachable = new ArrayList<>();          //zoznam dosianutelnych volnych okien
    
   private ArrayList<Station> stations; 
   private ArrayList<Car> cars;
   private Free_time_window[][] free_windows;
   private Reserved_time_window[][] reserved_windows;
   private Road[][] roads;  
    
    private boolean F[][] = new boolean[max_NO_of_stations][max_NO_of_windows];     //validne volne okna
    private boolean T[][] = new boolean[max_NO_of_stations][max_NO_of_windows];     //navstivene volne okna
    private boolean U[][] = new boolean[max_NO_of_stations][max_NO_of_windows];     //napriamo dosiahnutelne z T
    private boolean X[][] = new boolean[max_NO_of_stations][max_NO_of_windows];     //pomocne pole F - T
    
   public Traffic_control(Maps map){
       this.m = map;
   } 
    
    public int findShortestWay(Last_station startSt, int finalSt, int waittime, Car car, boolean block){
        int last_poradie = 0, t, min_tau, min_lambda = 0, min_l, k = 0, r = 0;
        int i, j, p, q;
        int counter = 0;
        
        init(m);    //inicializujem polia a zoznamy
        
        for(p = 0; p < max_NO_of_windows; p++){                                                                         //posledne okno v cielovom uzle
            if(free_windows[finalSt][p].getStart_time() != -1 && free_windows[finalSt][p].getEnd_time() == -1)
                last_poradie = p;                                                                   
        }
             
        for(i = 1; i < stations.size() +1 ;i++){
            for(j = 0; j < max_NO_of_windows; j++){
                if(free_windows[i][j].getStart_time() != -1){       //ak nezacinaju na -1
                    F[i][j] = true;                                 //su to validne volne okna
                }
            }
        }
        
        T[startSt.getID()][0] = true;
        
        while(true){
        for(i = 1; i < stations.size() +1; i++){                                //pociatocna inicializacia v kazdom behu hladania
            for(j = 0; j < max_NO_of_windows; j++){
                if(F[i][j] == true)                     //tie, ktore su validne volne okna
                    X[i][j] = true;                     //z nich mozme hladat
                if(T[i][j] == true)                     //tie, ktore su uz navstivene 
                    X[i][j] = false;                    //nemozme z nich hladat cestu
            }    
        }
        
            for(j = 1; j < stations.size() +1 ;j++){
                for(q = 0; q < max_NO_of_windows; q++){
                    if(X[j][q] == true){                                                                    //z tych pridanych okien, co sme uz predtym vyhladali nemozme viest hladanie, iba mozu byt hladanie
                       t = 0;
                    
                  for(i = 1; i < stations.size() +1 ;i++){                                                  //prechadzam vsetky uzly...
                       for(p = 0; p < max_NO_of_windows; p++){                                              //a ich volne okna
                           if(T[i][p] == true){                                                             //iba tie, co sme nasli uz predtym mozu byt hladane
                                reachability_test(j,q,i,p, car.getRychlost(), startSt.getID());             //zistim, ci su okna dosiahnutelne
                                counter++;
                                Reachability rb = new Reachability(j,q,i,p, result_tau, result_lambda);     //vytvorim si zaznam o vyhladavani
                                reachable.add(rb);                                                          //pridam vysledok vyhladavania do zoznamu
                                t++;
                            }
                        }
                 }
                
                 min_tau = -1;
                
                  for(i = 1; i < stations.size() + 1; i++){
                       for(p = 0; p < max_NO_of_windows; p++){
                           if(T[i][p] == true){
                             for(int l = 0; l< t; l++){
                                 if(reachable.get(l).getFrom_node() == j && reachable.get(l).getFrom_window() == q && 
                                   (reachable.get(l).getTau() < min_tau || min_tau == -1) && reachable.get(l).getTau() > -1){
                                            min_tau = reachable.get(l).getTau();
                                            k = reachable.get(l).getTo_node();
                                            r = reachable.get(l).getTo_window();
                                            min_lambda = reachable.get(l).getLambda();
                                     
                                     
                                 } 
                              }
                            }
                        }
                  }
                
                  for(int l = 0; l < t; l++){
                        if(reachable.get(l).getFrom_node() == j && reachable.get(l).getFrom_window() == q && reachable.get(l).getTo_node() == k && reachable.get(l).getTo_window() == r && reachable.get(l).getTau() > -1){
                           
                                free_windows[j][q].setEarliest_arrive(min_tau);
                                free_windows[j][q].setFrom_node(reachable.get(l).getTo_node());
                                free_windows[j][q].setFrom_window(reachable.get(l).getTo_window());
                                free_windows[j][q].setRoute(min_lambda);
                           
                        }
                 }
                
                  if(free_windows[j][q].getEarliest_arrive() != -1){
                      U[j][q] = true;
                    }
                 }
                 reachable = new ArrayList<>();         //premazem zoznam dosiahnutelnych okien
                }
    
            }
        
         min_l = -1;
        
         for(j = 1; j < stations.size() +1 ; j++){
                for(q = 0; q < max_NO_of_windows; q++){
                   if(U[j][q] == true && (free_windows[j][q].getEarliest_arrive() < min_l || min_l == -1)){     //hladam minimalnu lambdu z volnych okien 
                       min_l = free_windows[j][q].getEarliest_arrive();                                         //ak je taka lambda
                       i = j;                                                   //zapamatam si uzol
                       p = q;                                                   //a ktore volne okno
                   }
              }
         }
        
         if(min_l == -1){       //ak nie je cesta. iba vyradim posledny uzol...lebo to je ten nas navyse 
 
            car.setLast_node(new Last_station(car.getPlan().get(car.getPlan().size()-1).getNode(), car.getPlan().get(car.getPlan().size()-1).getStart_time(), car.getPlan().get(car.getPlan().size()-1).getEnd_time(), stations.get(car.getPlan().get(car.getPlan().size()-1).getNode() -1).getNazov(), -1));

            m.setFree_windows(free_windows);
            m.setReserved_windows(reserved_windows);
            
            return 0;
         }
        
         T[i][p] = true;                        //je navstivene
         U[i][p] = false;                       
        
         boolean najdene = false;
          for(i = 0; i <= last_poradie; i++){   //prejdem vsetky okna 
              if(T[finalSt][i] == true){        //ak je v T mnozine nejake okno z nasho hladaneho uzla nastavene na true...
                  
                  //ta dlha podmienka za && je pre pripad, ze sa mi do cieloveho okna vozdilo nezmesti kvoli waittime-u
                  if(finalSt != startSt.getID() || i != 0){
                    if(block && free_windows[finalSt][i+1].getStart_time() == -1){
                        last_poradie = i;           //zapamatam si, ktory free window to je
                        najdene = true;             //a nastavim boolean pre hladanie na true
                    }
                    else if(!block && (free_windows[finalSt][i].getEnd_time() > free_windows[finalSt][i].getEarliest_arrive() + (Station_size/car.getRychlost()) + waittime || free_windows[finalSt][i].getEnd_time() == -1)){
                        last_poradie = i;           //zapamatam si, ktory free window to je
                        najdene = true;             //a nastavim boolean pre hladanie na true
                    }
                  }
                }
            }
          if(najdene)       //ak je to vyhovujuce hladanie -> idem na vytvorenie rezervovanych okien
              break;
        }
        
        //vyrobim reserved z najdeneho planu
        int node = finalSt;                         //nastavim si node na hladany
        int window = last_poradie;                  //a window na najdene okno
        int route;
        
        ArrayList<Reserved_time_window> to_add = new ArrayList<>();
        car.setLast_node(new Last_station(finalSt, free_windows[node][window].getEarliest_arrive(), free_windows[node][window].getEarliest_arrive() + waittime + (Station_size/car.getRychlost()), stations.get(finalSt-1).getNazov(), last_poradie));        //vozidlu nastavim novy last station
        int[] res = m.getNO_reserved();             //nacitam si pocty rezervovanych okien pre stanice
        
        reserved_windows[node][res[node]].setStart_time(free_windows[node][window].getEarliest_arrive());            //vytvaram rezervovane okna z volnych vyplnenych okien -> verzia s tym istym oknom

        if(block)    
            reserved_windows[node][res[node]].setEnd_time(-1);      //ak je to cielova stanica -> pridam aj waittime
        else
            reserved_windows[node][res[node]].setEnd_time(free_windows[node][window].getEarliest_arrive() + waittime);      //ak je to cielova stanica -> pridam aj waittime
        
        reserved_windows[node][res[node]].setReserved_by(car);                                                              //a nastavim tiez, ktore vozidlo rezervuje okno
        reserved_windows[node][res[node]].setRoad(-1);
        reserved_windows[node][res[node]].setNode(node);
        
        to_add.add(reserved_windows[node][res[node]]);
        reserved_windows = sort_reserved(res, node, reserved_windows);                                                      //zosortujem....
        res[node]++;                                                                                                        //teraz mam v bode o 1 reserved window viac
        
        route = free_windows[node][window].getRoute();
        window = free_windows[node][last_poradie].getFrom_window();
        node = free_windows[node][last_poradie].getFrom_node();                                                             //a idem na node a okno, z ktoreho som sa do povodneho okna dostal dostal 
        
        while(node != startSt.getID() || window != 0){             //kym nenajdem zaciatok          
            reserved_windows[node][res[node]].setStart_time(free_windows[node][window].getEarliest_arrive());            //vytvaram rezervovane okna z volnych vyplnenych okien -> prechod oknom
            reserved_windows[node][res[node]].setEnd_time(free_windows[node][window].getEarliest_arrive() + (2*Station_size/car.getRychlost()));          //ak nie, vozidlo iba prechadza -> cize len ti/2
            reserved_windows[node][res[node]].setReserved_by(car);                                                              //a nastavim tiez, ktore vozidlo rezervuje okno
            reserved_windows[node][res[node]].setRoad(route);
            reserved_windows[node][res[node]].setNode(node);
            
            to_add.add(new Reserved_time_window(free_windows[node][window].getEarliest_arrive(), free_windows[node][window].getEarliest_arrive() + (2*Station_size/car.getRychlost()), car, node, route));
            int temp_w;
            route = free_windows[node][window].getRoute();                                                               //vytiahnem si cestu, po ktorej som sa sem dostal
            temp_w = free_windows[node][window].getFrom_window();                                                        //nahram si cislo okna, z ktoreho som sem prisiel do pomocnej premennej
            
            reserved_windows = sort_reserved(res, node, reserved_windows);                                                      //zosortujem....
            res[node]++;
            
            node = free_windows[node][window].getFrom_node();                                                             //a idem na node, z ktoreho som sa do povodneho okna dostal dostal
            
            window = temp_w;
        }
        
        reserved_windows[startSt.getID()][res[startSt.getID()]].setStart_time(free_windows[startSt.getID()][0].getStart_time());        //ak je to zaciatocne okno, len vyvorim nazad rezervovane okno 
        reserved_windows[startSt.getID()][res[startSt.getID()]].setEnd_time(free_windows[startSt.getID()][0].getEnd_time());
        reserved_windows[startSt.getID()][res[startSt.getID()]].setReserved_by(car);
        reserved_windows[startSt.getID()][res[startSt.getID()]].setRoad(route);
        reserved_windows[startSt.getID()][res[startSt.getID()]].setNode(node);
        
         to_add.add(reserved_windows[node][res[startSt.getID()]]);
        
        reserved_windows = sort_reserved(res, startSt.getID(), reserved_windows);           //a znova zosortujem
        res[startSt.getID()]++;
        
        for(int x = to_add.size() - 1; x >= 0; x--){                             //idem pridavat do vozidla dalsiu trasu pre plan
            car.getPlan().add(car.getPlan().size(), to_add.get(x));             //idem od konca a pridavam na koniec planu vozidla
        }
        
        m.setFree_windows(free_windows);                                    //este pre istotu ukladam
        m.setReserved_windows(reserved_windows);
        m.setNO_reserved(res);
        
        System.out.println(counter);
        return 1;   //vratim uspesnu operaciu
    }
    
    
    //==================================================================================================s
     private void reachability_test(int to_node, int to_window, int from_node, int from_window, int speed, int startSt){
         int temp; 
         int relevant[] = new int[max_NO_of_stations];
         
         if(from_node != to_node){                                              //ak nehladame dostupnost v ramci rovnakeho uzla
               if(roads[from_node][to_node].getWeight() == -1){                 //ak sa neviem dostat z jedneho bodu do druheho
                   result_tau = -1;
                   result_lambda = -1;
                   return;
               }
               else if(free_windows[from_node][from_window].getEarliest_arrive() == -1){      //ak nepoznam najskorsi cas prichodu
                   result_tau = -1;
                   result_lambda = -1;
                   return;
               }
               
               if(from_node == startSt && from_window == 0){
                    alpha = free_windows[from_node][from_window].getEarliest_arrive() +  (Station_size/speed) + roads[from_node][to_node].getWeight();       //urcim si, kolko bude trvat cesta po tej lajne do bodu
               }
               else{
                   alpha = free_windows[from_node][from_window].getEarliest_arrive() +  (2*Station_size/speed) + roads[from_node][to_node].getWeight();
               }
               if(free_windows[to_node][to_window].getStart_time() > alpha)             //ak sa volne okno zacina neskor ako pride vozidlo -> niekto tam este je :/
                   processing_tau = free_windows[to_node][to_window].getStart_time();   //nastavim si cas prichodu ako start time volneho okna
               else     
                   processing_tau = alpha;                                              //inak viem rovno vstupit do stanice a teda cas prichodu bude ten mnou vypocitany
               
               if(free_windows[to_node][to_window].getEnd_time() != -1){                    //ak okno, v ktorom som nie je nekonecne
                   if(processing_tau > free_windows[to_node][to_window].getEnd_time() - (2*Station_size/speed)){    //a ak sa tam vozidlo nezmesti
                       result_tau = -1;                                                         //nevhodne okno
                       result_lambda = -1;
                       return;
                   }
               }
               
               for(int x = 0; x < max_NO_of_windows; x++){               //catch up conflict
                   if((reserved_windows[from_node][x].getStart_time() >= free_windows[from_node][from_window].getEnd_time()) && free_windows[from_node][from_window].getEnd_time() != -1){
                   Car v = reserved_windows[from_node][x].getReserved_by();                         //ziskam si, ktore auto obsadzuje okno v pociatocnom node
                   
                   if(v.getID() != -1){                                                             
                      for(int y = 0; y < max_NO_of_windows; y++){                                                                   
                          if(reserved_windows[to_node][y].getEnd_time() <= free_windows[to_node][to_window].getStart_time()){       //beriem iba obsadene okna, ktore su pred zacatim mojho volneho okna
                                                                                                                                    //tu je uprava oproti staremu algoritmu, pretoze sa nezaujimam o obsadene okna, ktore su za mojim volnym oknom
                            Car u = reserved_windows[to_node][y].getReserved_by();                                                  //vyberiem si vozidlo z toho obsadeneho okna...
                            if(v.getID() == u.getID() || roads[to_node][from_node].getID() == reserved_windows[to_node][y].getRoad()){          //ak su to rovnake vozidla, alebo ak sa vozidlo uhyba na rovnaku cestu v tom case                                                                      //a ak sa vybrane vozidla zhoduju, neexistuje tadial cesta daleje
                                 result_tau = -1;
                                  result_lambda = -1;
                                  return;
                            }
                          }
                      } 
                   }
                   }
               }
               
               for(int x = 0; x < max_NO_of_windows; x++){               //heads on conflict
                    if(reserved_windows[to_node][x].getStart_time() >= free_windows[to_node][to_window].getEnd_time() && free_windows[to_node][to_window].getEnd_time() != -1){
                   Car v = reserved_windows[to_node][x].getReserved_by();
                   
                   if(v.getID() != -1){
                      for(int y = 0; y < to_window; y++){
                           if(reserved_windows[from_node][y].getStart_time() <= free_windows[from_node][from_window].getStart_time()){
                          Car u = reserved_windows[from_node][y].getReserved_by();
                          if(v.getID() == u.getID() || roads[to_node][from_node].getID() == reserved_windows[from_node][y].getRoad()){
                              result_tau = -1;
                              result_lambda = -1;
                              return;
                          }
                            }
                      } 
                   }
                    }
               }
               
               result_tau = processing_tau;                                     //vysledne tau bude to vypocitane alebo start free okna
               result_lambda = roads[from_node][to_node].getID();               //a lambda bude ID cesty, po ktorej sa pojde
           }
           
           else{                                                                //ak su to rovnake uzly
               if(free_windows[from_node][from_window].getEarliest_arrive() == -1){           //zistujem, ci je zadane Lko
                   result_tau = -1;
                   result_lambda = -1;
                   return;
               }
               
               if(from_window > to_window){                                     //a ci nahodou nechcem ist do predchadzajuceho uzla -> do minuosti neviem ist
                    result_tau = -1;
                    result_lambda = -1;
                    return;
               }
               
               temp = 0;
               int temp1;
               int B[] = new int[4];                                            //all bidirectional lanes incident to node
               int C[] = new int[4];                                            //kopia Bcka
               
               for(int x = 1; x < stations.size(); x++){                        
                   if(roads[from_node][x].getID() > 0){                         //zistujem, kolko je ciest zo source bodu a ake su ich IDcka
                       B[temp] = roads[from_node][x].getID();                   //prihadzujem ich do B pola
                       temp++;
                   }
               }
               
               if(temp == 0){                                                   //ak nie su ziadne cesty
                   result_tau = -1;
                   result_lambda = -1;
                   return;
               }
               
               processing_tau = free_windows[to_node][to_window].getStart_time();
               
               for(int x = 0; x < max_NO_of_windows; x++){   
                   if(reserved_windows[from_node][x].getStart_time() >= free_windows[from_node][from_window].getEnd_time() && free_windows[from_node][from_window].getEnd_time() != -1
                      && reserved_windows[from_node][x].getEnd_time() <= free_windows[to_node][to_window].getStart_time() && reserved_windows[from_node][x].getEnd_time() != -1){
                       //cize opis tohto ifka -> ak sa reserved window x nachadza mezdi dvoma free windowmi a nie su to nejake weird okna...
                       
                            int m = 0;
                            Car v = reserved_windows[from_node][x].getReserved_by();                   //ziskam si vozidlo obsadzujuce tento bod
                            
                           
                                for(int l = 0; l < v.getPlan().size(); l++){                                                                                                         //prechadzam plan toho vozidla
                                        if(v.getPlan().get(l).getStart_time() >= free_windows[from_node][from_window].getEnd_time() &&                                               //zistim si, ci je to planik medzi volnymi oknami
                                                v.getPlan().get(l).getEnd_time() <= free_windows[to_node][to_window].getStart_time() && v.getPlan().get(l).getNode() == from_node){
                                            if(v.getPlan().get(l).getReserved_by() == v){                                                                                           //a ak je to nase vozidlo
                                                if(l != 0){
                                                    relevant[m] = v.getPlan().get(l-1).getRoad();        //vyradim hranu ktorou sa do uzla dostalo
                                                    relevant[m+1] = v.getPlan().get(l).getRoad();        //a ktorou hranou pojde dalej
                                                    m += 2;
                                                }
                                                else{
                                                    relevant[m] = v.getPlan().get(l).getRoad();         //ak je to pociatocny planik -> iba ktorou odide
                                                    m++;
                                                    }
                                            }
                                        }
                                }
                            
                            for(int k = 0; k < m; k++){                         //vyradim tie hrany, ktore som oznacil ako relevantne
                                for(int l = 0; l < temp; l++){
                                    if(B[l] == relevant[k]){
                                        B[l] = -1;                              //nastavim ich ako -1
                                    }
                                }
                            }
                            
                            for(int k = 0; k < temp; k++){
                                C[k] = B[k];                                    //prekopirujem do pomocneho pola
                            }
                            
                            
                            temp1 = temp;
                            temp = 0;
                            
                            for(int k = 0; k < temp1; k++){                     
                                if(C[k] != -1){                                 //a popresuvam na zaciatok
                                    B[temp] = C[k];
                                    temp++;
                                }
                            }
                   }
               }
               
                   if(temp == 0){                                               //ak nie je ziadna vhodna hrana na uhyb, neda sa najst cesta
                        result_tau = -1;
                        result_lambda = -1;
                        return;
                   }
                   
                   result_tau = processing_tau;                                 //ak nejaka hrana zostala
                   result_lambda = B[0];                                        //beriem tu prvu v poli hran
               
         }
        
    }
     
     public int create_vehicle(Maps map, String meno, Station sel, int rychlost, int waittime, int starttime, boolean blocked, boolean asap, JPanel p, Color farba){
         Reserved_time_window rw[][] = map.getReserved_windows();
         Reserved_time_window r;
         ArrayList<Car> c = map.getCars();
         int[] res = map.getNO_reserved();
         boolean created = false;
         
         for(int i = 0; i < max_NO_of_windows; i++){                                                        //prechadzam obsadene okna a hladam medzeru pre umiestnenie noveho vozidla

            if(rw[sel.getID()][i].getStart_time() == -1 && rw[sel.getID()][i].getEnd_time() == -1){
               if(i == 0){                                                                                      //ak je celkom prazdny node
                    Car car = new Car(map.getCars().size(), rychlost, meno, farba);                                    //vytvorim si nove vozidlo                                                                                 //a pridam ho do zozonamu vozidiel

                    if(blocked){
                         r = new Reserved_time_window(starttime, -1, car);           //vytvorim nove rezervovane okno -> blokujuce, takze endtime bude -1
                         r.setNode(sel.getID());
                         r.setRoad(-1);
                          
                         Last_station ls = new Last_station(sel.getID(), starttime, -1, sel.getNazov(), i);        //vytvorim si poslednu stanicu -> pre pocitanie tnow
                        car.setLast_node(ls);  
                    }

                    else{
                         r = new Reserved_time_window(starttime, starttime + waittime, car);        //vytvorim nove rezervovane okno -> neblokujuce, takze bude endtime zaciatok + cakanie
                         r.setNode(sel.getID());
                        r.setRoad(-1);
                        
                        Last_station ls = new Last_station(sel.getID(), starttime, starttime + waittime, sel.getNazov(), i);        //vytvorim si poslednu stanicu -> pre pocitanie tnow
                        car.setLast_node(ls);  
                    }
                    ArrayList<Reserved_time_window> plan = new ArrayList<>();                   //vytvorim si novy plan pre vozidlo

                    plan.add(r);                                                                //prvy prvok tohto planu bude pociatocne rezervovane okno
                    car.setPlan(plan);                                                          //pridam plan vozidlu
                    car.setX(-1);                                                               //suradnice su zatial -1 -> budu sa pocitat neskor
                    car.setY(-1);  
                    c.add(car);                                                                 //pridam vozidlo zoznamu vozidiel

                    rw[sel.getID()][res[sel.getID()]] = r;                                    //dam ho na koniec rezervovanych okien
                    sort_reserved(res, sel.getID(), rw);                                        //zosortujem, nech su okna pekne usporiadane
                    res[sel.getID()]++;                                                         //pre danu stanicu pribudne rezervovane okno
                    created = true;
                    break;   
                
               }
               else if(rw[sel.getID()][i-1].getEnd_time() != -1 && starttime >= rw[sel.getID()][i-1].getEnd_time()){ 
                    Car car = new Car(map.getCars().size(), rychlost, meno, farba);                                    //vytvorim si nove vozidlo                                                                                 //a pridam ho do zozonamu vozidiel

                    if(blocked){
                        r = new Reserved_time_window(starttime, -1, car);           //vytvorim nove rezervovane okno -> blokujuce, takze endtime bude -1
                        r.setNode(sel.getID());
                        r.setRoad(-1);
                        
                        Last_station ls = new Last_station(sel.getID(), starttime, -1, sel.getNazov(), i);        //vytvorim si poslednu stanicu -> pre pocitanie tnow
                        car.setLast_node(ls);                                                                                       //nastavim vozidlu poslednu stanicu
                    }

                    else{
                        r = new Reserved_time_window(starttime, starttime + waittime, car);        //vytvorim nove rezervovane okno -> neblokujuce, takze bude endtime zaciatok + cakanie
                        r.setNode(sel.getID());
                        r.setRoad(-1);
                        
                        Last_station ls = new Last_station(sel.getID(), starttime, starttime + waittime, sel.getNazov(), i);        //vytvorim si poslednu stanicu -> pre pocitanie tnow
                        car.setLast_node(ls);                                                                                       //nastavim vozidlu poslednu stanicu
                    }
                    ArrayList<Reserved_time_window> plan = new ArrayList<>();                   //vytvorim si novy plan pre vozidlo

                    plan.add(r);                                                                //prvy prvok tohto planu bude pociatocne rezervovane okno
                    car.setPlan(plan);                                                          //pridam plan vozidlu
                    car.setX(-1);                                                               //suradnice su zatial -1 -> budu sa pocitat neskor
                    car.setY(-1);  
                    c.add(car);                                                                 //pridam vozidlo zoznamu vozidiel

                    rw[sel.getID()][res[sel.getID()]] = r;                                    //dam ho na koniec rezervovanych okien
                    sort_reserved(res, sel.getID(), rw);                                        //zosortujem, nech su okna pekne usporiadane
                    res[sel.getID()]++;                                                         //pre danu stanicu pribudne rezervovane okno
                    created = true;
                    break;   
                }
            }
             
             if(rw[sel.getID()][i].getStart_time() >= starttime + waittime){                                                        //ak je este nejaky window za...
                if(i == 0){                                                                                                         //ak je to prve rezervovane okno 
                    
                        if(!blocked){                                                                                                 //ak je blokujuci, nie je miesto pre vozidlo
                            Car car = new Car(map.getCars().size(), rychlost, meno, farba);                                                    //vytvorim si nove vozidlo
                            Last_station ls = new Last_station(sel.getID(), starttime, starttime + waittime, sel.getNazov(), i);        //vytvorim si poslednu stanicu -> pre pocitanie tnow
                            car.setLast_node(ls);                                                                                       //nastavim vozidlu poslednu stanicu

                            ArrayList<Reserved_time_window> plan = new ArrayList<>();                                                   //tiez si vytvorim plan auta
                            r = new Reserved_time_window(starttime, starttime+ waittime, car);                                          //a ako prvy prvok bude pociatocne rezervovane okno
                            r.setNode(sel.getID());
                            r.setRoad(-1);
                            plan.add(r);                                                                                                //pridam toto okno do planu

                            car.setPlan(plan);                                                                                          //nastavim autu plan
                            car.setX(-1);                                                                                               //suradnice nastavene na -1
                            car.setY(-1);
                            c.add(car);                                                                                                 //a pridam auto do zoznamu aut

                            rw[sel.getID()][res[sel.getID()]] = r;                                                                    //dam okno na koniec rezervovanych okien
                            sort_reserved(res, sel.getID(), rw);                                                                        //zosortujem, nech su okna pekne usporiadane
                            res[sel.getID()]++;                                                                                         //pre danu stanicu pribudne rezervovane okno
                            created = true;
                            break;
                        }
                }
                
                else if(rw[sel.getID()][i-1].getEnd_time() <= starttime){         //ak nie je okno prve a koniec predchadzajuceho okna je vacsi alebo rovny, ako cas, odkedy chcem zacat hladat miesto pre vozidlo 
                    if(!blocked){                                                   //ak je blokovanie zvolene, nie je miesto, pretoze uz mam jeden uzol na konci
                        Car car = new Car(map.getCars().size(), rychlost, meno, farba);
                        Last_station ls = new Last_station(sel.getID(), rw[sel.getID()][i-1].getEnd_time(), rw[sel.getID()][i-1].getEnd_time() + waittime, sel.getNazov(), i);
                        car.setLast_node(ls);
                        ArrayList<Reserved_time_window> plan = new ArrayList<>();                                                                    //tiez si vytvorim plan auta
                        r = new Reserved_time_window(rw[sel.getID()][i-1].getEnd_time(), rw[sel.getID()][i-1].getEnd_time() + waittime, car);        //vytvorim nove rezervovane okno -> neblokujuce, takze bude endtime zaciatok + cakanie
                        r.setNode(sel.getID());
                        r.setRoad(-1);
                        plan.add(r);                                                                                                                 //pridam toto okno do planu
                        
                        car.setPlan(plan);                                                                                                           //nastavim autu plan
                        car.setX(-1);                                                                                                                //suradnice nastavene na -1
                        car.setY(-1);
                        c.add(car);            
                        
                        rw[sel.getID()][res[sel.getID()]] = r;                                                        //dam ho na koniec rezervovanych okien
                        sort_reserved(res, sel.getID(), rw);                                                                          //zosortujem, nech su okna pekne usporiadane
                        res[sel.getID()]++;                                                                                  //pre danu stanicu pribudne rezervovane okno
                        created = true;
                        break;
                    }
                } 
            }
             if(rw[sel.getID()][i].getEnd_time() > starttime && asap)
                 starttime = rw[sel.getID()][i].getEnd_time();
         }
         
         map.setCars(c);
         map.setReserved_windows(rw);
         map.setNO_reserved(res);
         
         if(created)
            return 1;
         else 
            return -1;
     }
     
     public int create_schedule(Maps map, Station dest, Car car_sel, int waittime, boolean block, JPanel p){
            if(car_sel.getLast_node().getTo() == -1)      //ak je posledny uzol blokovaci -> nie je miesto, ani netreba pocitat
                return -1;
         
            if(createFreeWindows(map, car_sel, dest) == -1)     //ak je vysledok vytvarania volnych okien -1 -> tiez sa nenaslo miesto (nema sa ako pohnut z bodu)
                return -1;
            return findShortestWay(car_sel.getLast_node(), dest.getID(), waittime, car_sel, block);     //inak idem hladat    
            
     }
     
     public int createFreeWindows(Maps map, Car car, Station dest){
        Free_time_window free[][] = map.getFree_windows();
        Reserved_time_window reserved[][] = map.getReserved_windows();
        int tnow, temp_tnow;
        int counter[] = new int[map.getStations().size()+1];
        int[] res = map.getNO_reserved();                                       //nacitam si pocty rezervovanych okien pre stanice
        Reserved_time_window out;
        
        for(int i = 0; i < max_NO_of_stations; i++){
            for(int j = 0; j < max_NO_of_windows; j++){
                free[i][j] = new Free_time_window(-1,-1,-1,-1);
            }
        }
        
        if(car.getLast_node().getTo() == -1){       //zistim si tnow z posledneho uzla vozidla
            tnow = car.getLast_node().getFrom();
        }
        else tnow = car.getLast_node().getTo();
        
        //teraz budem vytvarat obsadene okno vozidlu...robim to preto, lebo vozidlo musi mat nejaky cas na odchod....hladam prve miesto, kde by to slo
        
         for(int i = 0; i < max_NO_of_windows; i++){                                                                        
            if(reserved[car.getLast_node().getID()][i].getEnd_time() <= tnow &&                                             //takze idem hladat dvojicu rezervovanych okien, medzi ktore viem vsunut odchod vozidla z uzla
              (reserved[car.getLast_node().getID()][i+1].getStart_time() >= tnow + Station_size / car.getRychlost()) 
              || reserved[car.getLast_node().getID()][i+1].getStart_time() == -1){                                          //ak take miesto najdem
                    
                    out = new Reserved_time_window(tnow, tnow + Station_size / car.getRychlost(), car, car.getLast_node().getID(), -1);  //vytvorim si na tom mieste nove rezervovane okno
                    reserved[car.getLast_node().getID()][res[car.getLast_node().getID()]] = out;                                         //a prihodim do matice
                    sort_reserved(res, car.getLast_node().getID(), reserved);                                                           //presortujem
                    res[car.getLast_node().getID()]++;                                                                                          

                    Last_station new_ls = new Last_station(car.getLast_node().getID(), tnow, tnow+Station_size / car.getRychlost(),         //takisto upravim aj posledny uzol pre vozidlo
                                                           car.getLast_node().getName(), res[car.getLast_node().getID()]);
                    car.setLast_node(new_ls);

                    tnow = car.getLast_node().getFrom();                        //tnow nastavim od konca noveho rezervovaneho okna....od toho casu vyrobim volne okna
                    break;
            }
            else if(reserved[car.getLast_node().getID()][i].getEnd_time() == -1){       //ak je to koncove obsadene okno
                if(reserved[car.getLast_node().getID()][i].getStart_time() == -1)       //ak je mimo 
                    return -1;                                                          //nenasiel som miesto
                else{                                                                           
                    out = new Reserved_time_window(tnow, tnow + Station_size / car.getRychlost(), car, car.getLast_node().getID(), -1);     //opakujem postup ako vyssie
                    reserved[car.getLast_node().getID()][res[car.getLast_node().getID()]] = out;
                    sort_reserved(res, car.getLast_node().getID(), reserved);
                    res[car.getLast_node().getID()]++;

                    Last_station new_ls = new Last_station(car.getLast_node().getID(), tnow, tnow + Station_size / car.getRychlost(), 
                                                           car.getLast_node().getName(), res[car.getLast_node().getID()]);
                    car.setLast_node(new_ls);

                    tnow = car.getLast_node().getFrom();
                    break;
                }
            }
            
            if(tnow < reserved[car.getLast_node().getID()][i+1].getEnd_time())      //toto je posun, ak nenajdem medzi oknami miesto, posuvam sa dalej -> nastavim si tnow
                tnow = reserved[car.getLast_node().getID()][i+1].getEnd_time();
        }
        //----------------------------------------------------------------------------------------------------
        
        for(int i = 1; i <= map.getStations().size(); i++){                          //prejdem kazdu stanicu
            counter[i] = 0;
            temp_tnow = tnow;
            for(int j = 0; j <max_NO_of_windows; j++){                              //a v stanici kazde okno
                if(reserved[i][j].getStart_time() > temp_tnow){                          //ak sa pohybujeme v ramci aktualneho casu vozidla
                        if(j == 0){                                                 //overime si, ci je to prvy reserved window
                            free[i][counter[i]].setStart_time(0);                            //ak ano, starttime free windowu je 0
                            free[i][counter[i]].setEnd_time(reserved[i][j].getStart_time()); //a endtime je zaciatkom reserved windowu
                            counter[i]++;
                        }
                        
                        else{
                            free[i][counter[i]].setStart_time(reserved[i][j-1].getEnd_time());   //ak nie je prvy node ale niektory z nasledujucich, starttime je koniec predchadzajuceho reserved
                            free[i][counter[i]].setEnd_time(reserved[i][j].getStart_time());     //a koniec je zaciatkom overovaneho reserved windowu
                            counter[i]++;
                        }
                        
                        if(reserved[i][j].getEnd_time() != -1 && reserved[i][j].getEnd_time() > 0){ //pre pripad, ze za reserved uz nic nie je...nekonci v nekonecne
                            free[i][counter[i]].setStart_time(reserved[i][j].getEnd_time());               //vyrobim si jeden free window ako ukoncovac...ak nasleduje dalsi free window, bude nahradene
                            if(reserved[i][j+1].getStart_time() != -1){                             //ak to nie je window mimo zoznamu (su oznacene -1, -1)
                                free[i][counter[i]].setEnd_time(reserved[i][j+1].getStart_time());       //do free window nahram ako koniec zaciatocnu hodnotu z reserved windowu 
                            }
                            else{
                                free[i][counter[i]].setEnd_time(-1);                                       //inak aj endtime bude kvazy prazdny window
                            }
                        }
                    
                }
                else if(reserved[i][j].getStart_time() == temp_tnow){                                     //ak reserved window zacina presne v aktualnom case vozidla
                    if(reserved[i][j].getStart_time() != -1){                                   //zistim si, ci to nie je iba prazdny windwow
                        if(reserved[i][j].getEnd_time() != -1 && reserved[i][j].getEnd_time() > 0){ //a ci to nie je navzdy blokujuci -> v takom pripade nema zmysel vytvarat free window
                            free[i][counter[i]].setStart_time(reserved[i][j].getEnd_time());                 //nastavim starttime free windowu na endtime reserved windowu
                            if(reserved[i][j+1].getStart_time() != -1)
                                free[i][counter[i]].setEnd_time(reserved[i][j+1].getStart_time());
                            else
                                free[i][counter[i]].setEnd_time(-1);
                        }
                    }
                }
                    
                else if(reserved[i][j].getStart_time() == -1){                              //ak narazim na uzol s koncovym casom -1, zistujem...
                    if(reserved[i][0].getStart_time() == -1){                               //ak je jedno velke ciste okno
                        free[i][0].setStart_time(temp_tnow);                                //nastavim starttime od tnow
                        free[i][0].setEnd_time(-1);                                         //az do nekonecna
                    }
                    else{
                        if(reserved[i][j-1].getEnd_time() != -1){                                       //ak to je nejaky uzol mimo zaciatku, zistim, ci je predchadzajuci konecny alebo nekonecny
                            free[i][counter[i]].setStart_time(reserved[i][j-1].getEnd_time());         //nastavim starttime od tnow
                            free[i][counter[i]].setEnd_time(-1); 
                            counter[i]++;
                        }
                    }
                }
                    
                if(reserved[i][j].getEnd_time() > temp_tnow)                    //tnow posuvam, ak to pouzivatel zaskrtol....prve volne miesto
                        temp_tnow = reserved[i][j].getEnd_time();               //musim ho nastavit na koniec prehladavaneho rezervovaneho okna, ak je mensie, ako ta hodnota
                }
            }
        
        //nastavim si L pre pociatocne okno
        int source_st = car.getLast_node().getID();                                                      //nacitam si ID poslednej stanice
        free[source_st][counter[source_st]].setStart_time(car.getLast_node().getFrom());                //a idem menit reserved window zdrojovej stanice na free 
        free[source_st][counter[source_st]].setEnd_time(car.getLast_node().getTo());
        free[source_st][counter[source_st]].setEarliest_arrive(tnow);
        free = sort_free(counter, source_st, free);                                                     //pridam na koniec a zosortujem
        
        int found = -1;
        for(int i = 0; i < max_NO_of_windows; i++){                                                     //prejdem cele pole reserved windowsov a hladam podla last station zdrojove okno
            if(reserved[source_st][i].getStart_time() == car.getLast_node().getFrom() && reserved[source_st][i].getEnd_time() == car.getLast_node().getTo()){   //ak ho najdem
                reserved[source_st][i].setStart_time(-1);                                               //vynulujem udaje
                reserved[source_st][i].setEnd_time(-1);
                reserved[source_st][i].setReserved_by(null);
                found = i;
                break;
            }
        }
        
        int rw_reserved[] = map.getNO_reserved();                                                       //nacitam si pocet nahranych reserved windows z textaka     
        for(int i = found; i < rw_reserved[source_st]; i++){                                                //posuniem vsetky reserved windows, prve je totiz prazdne
            reserved[source_st][found] = reserved[source_st][found+1];
        }
        rw_reserved[source_st]--;
        
        
        map.setNO_reserved(rw_reserved);
        map.setFree_windows(free);
        map.setReserved_windows(reserved);
        
        return 1;
    }
     
     public int delete_vehicle(Maps map, Car car){                                  //funkcia pre mazanie vozidla
         Reserved_time_window reserved[][] = map.getReserved_windows();
         ArrayList<Car> cars = map.getCars();
         
         for(int i = 0; i < max_NO_of_stations; i++){
             for(int j = 0; j < max_NO_of_windows; j++){
                 if(reserved[i][j].getReserved_by().getID() == car.getID()){                    //vyhladam obsadene okna vozidlom, ktore som si vybral
                     reserved[i][j] = new Reserved_time_window(-1,-1, new Car(-1), -1, -1);     //ak ich najdem, nastavim ich parametre na -1
                     
                     for(int k = j+1; k < max_NO_of_windows; k++){                              //a posuniem vsetky okna za nimi
                         reserved[i][k-1] = reserved[i][k];
                     }
                 }
             }
         }
         
         for(int i = 0; i < cars.size(); i++){                                                  //nakoniec odstranim vozidlo zo zoznamu
             if(cars.get(i).getID() == car.getID() && cars.get(i).getMeno().equals(car.getMeno())){
                 cars.remove(i);
             
                 //for(int j = i; j< m.getCars().size(); j++)
                     //cars.set(j, cars.get(j+1));
             }
         }
         
         map.setCars(cars);
         map.setReserved_windows(reserved);
         return 1;
     }
     
    public Reserved_time_window[][] sort_reserved(int[] temp, int station, Reserved_time_window[][] rtw){   //funkcia pre bubblesort obsadenych okien
        Reserved_time_window temp1;
        
        for (int i=0; i<temp[station]; i++){
            for (int j=temp[station]; j>i; j--){
		if (rtw[station][j-1].getStart_time() > rtw[station][j].getStart_time()){
                    temp1 = rtw[station][j-1];
                    rtw[station][j-1] = rtw[station][j];
                    rtw[station][j] = temp1;
                }
            }
	}
    return rtw;
    }
     
    public Free_time_window[][] sort_free(int[] temp, int station, Free_time_window[][] ftw){       //funkcia pre bubblesort volnych okien
        Free_time_window temp1;
        
        for (int i=0; i<temp[station]; i++){
            for (int j=temp[station]; j>i; j--){
		if (ftw[station][j-1].getStart_time() > ftw[station][j].getStart_time()){
                    temp1 = ftw[station][j-1];
                    ftw[station][j-1] = ftw[station][j];
                    ftw[station][j] = temp1;
                }
            }
	}
    return ftw;
    }

    private void init(Maps map){                                            //este pred hladanim cesty inicializujem vsetky polia a zoznamy
        System.out.println("Loading external marices and arrays\n");
        
        processing_tau = 0;
        result_tau = 0;
        result_lambda = 0;
        alpha = 0;
        
        this.stations = map.getStations();
        this.cars = map.getCars();
        this.free_windows = map.getFree_windows();
        this.reserved_windows = map.getReserved_windows();
        this.roads = map.getRoads();
        
        System.out.println("\nSetting alg variables and matrices");
        for(int i= 0; i< max_NO_of_windows; i++){
            for(int j = 0;j< max_NO_of_windows; j++){       //vsetky matice nastavim na false
                this.F[i][j] = false;
                this.T[i][j] = false;
                this.U[i][j] = false;
                this.X[i][j] = false;
            }
        }

         for(int i = 0; i < stations.size() +1 ;i++){        //inicializujem polia 
            for(int j = 0; j < max_NO_of_windows; j++){
                F[i][j]=false;
                T[i][j]=false;
                U[i][j]=false;
                X[i][j]=false;
            }
        }
        
      
    }
    
}