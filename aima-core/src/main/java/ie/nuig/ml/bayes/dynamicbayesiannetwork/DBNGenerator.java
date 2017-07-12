package ie.nuig.ml.bayes.dynamicbayesiannetwork;

/**
 * Created by nazli on 22/06/17.
 */
public class DBNGenerator {


    public double deltaY1(double epsilon, double y1, double y2){

        return (y2 - (Math.pow(y1, 3))/3 + y1)/epsilon;
    }

    public double deltaY2(double a, double y1){
        return a - y1;
    }

    public double y1_1(double deltaY1_0, double y1_0, double stepsize){ return (deltaY1_0 * stepsize) + y1_0;}

    public double y2_1(double deltaY2_0, double y2_0, double stepsize){ return (deltaY2_0 * stepsize) + y2_0;}

    public double Y2_1andY1_1(double delta0, double var0, double deltaStep){
        return var0 + deltaStep * delta0;
    }

}
