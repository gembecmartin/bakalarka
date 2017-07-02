/*************************Autor : Martin Gembec *****************************************
/****************************************************************************************
/**************************** Bakalarska praca ******************************************
/****************************************************************************************
/******** Riadenie a simulácia pohybu automatických vozidiel vo výrobných halách ********
*****************************************************************************************
* Školiteľ : Ing. Štefan Krištofík PhD. *************************************************
*****************************************************************************************
/** Máj 2017 ****************************************************************************
*****************************************************************************************/

package bc;

import bc.controller.Init;
import bc.model.Maps;
import bc.view.main_win;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;


//stanice a vozidla budu cislovane od 1 -> su v ArrayListe
//free a reserved windows budu cislovane od 0 -> su v 2D poli

public class BC{
    public static int max_NO_of_stations = 100;
    public static int max_NO_of_windows = 50;
    public static int max_NO_of_cars = 20;
    public static int stations_loaded;
    public static int Station_size = 6;
    public static int Car_size = 6;

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.net.URISyntaxException
     */
    public static void main(String[] args) throws FileNotFoundException, URISyntaxException, IOException {
        Maps map = new Maps();
        String initial_map = new File(BC.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().toString();   //simulacia je nastavena na svoju "default.txt" mapu
        Init initialized = new Init(initial_map + "\\maps\\default.txt", map);
        
        main_win main = new main_win(map);
        main.setVisible(true);
                
        }    
}
