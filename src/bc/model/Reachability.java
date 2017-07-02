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
public class Reachability {         //trieda, ktora sluzi na zapamatanie si dosiahnutelnosti dvoch volnych okien
   private int from_node;               //z ktoreho uzla idem...
   private int from_window;             //a ktore okno to je
   private int to_node;                 //ktory uzol navstivim
   private int to_window;               //a ktore to bude okno
   private int t;                       //tau
   private int l;                       //lambda
   
   public Reachability(int from_node,int from_window, int to_node, int to_window, int t, int l){
       this.from_node = from_node;
       this.from_window = from_window;
       this.to_node = to_node;
       this.to_window = to_window;
       this.t = t;
       this.l = l;
   }

    public int getFrom_node() {
        return from_node;
    }

    public void setFrom_node(int from_node) {
        this.from_node = from_node;
    }

    public int getFrom_window() {
        return from_window;
    }

    public void setFrom_window(int from_window) {
        this.from_window = from_window;
    }

    public int getTo_node() {
        return to_node;
    }

    public void setTo_node(int to_node) {
        this.to_node = to_node;
    }

    public int getTo_window() {
        return to_window;
    }

    public void setTo_window(int to_window) {
        this.to_window = to_window;
    }

    public int getTau() {
        return t;
    }

    public void setTau(int tau) {
        this.t = tau;
    }

    public int getLambda() {
        return l;
    }

    public void setLambda(int lambda) {
        this.l = lambda;
    }
   
   
}
