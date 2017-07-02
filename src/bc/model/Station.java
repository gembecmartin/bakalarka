package bc.model;

public class Station {
    private int ID;         //ID stanice
    private String nazov;   //nazov stanice
    private int polohaX;    //poloha stanice -> pre vypocet pohybu vozidla na mape
    private int polohaY;    
    
    public Station(String nazov,int ID, int polohaX, int polohaY){       //konstruktor Stanice pri vytvarani siete
       this.nazov = nazov;
       this.ID = ID;
       this.polohaX = polohaX;
       this.polohaY = polohaY;
    }
    
    public int getPolohaX(){
        return polohaX;
    }
    
    public int getPolohaY(){
        return polohaY;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }
    
    
}
