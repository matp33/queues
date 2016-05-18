

package events;

import symulation.Painter;
import visualComponents.Queue;

public class QueueObservation implements Runnable {

private Painter okno;
private int nrKolejki;

    public QueueObservation (Painter okno, int nrKolejki){
        this.okno=okno;
        this.nrKolejki=nrKolejki-1;
    }

    @Override
    public void run(){
//        Kolejka k=okno.symulacja.kolejki[nrKolejki];
//        System.out.println("w kolejce: "+k.klienci.size());
//        synchronized(okno.symulacja.lock){
//            while (k.klienci.get(k.klienci.size()-1).czyWRuchu==true){
//                try{
//                   okno.symulacja.lock.wait();
//                }
//                catch (InterruptedException ex){
//                    
//                }
//            }
//        }
//        okno.symulacja.przesunKolejke(nrKolejki+1, k.klienci.size());

    }

}
