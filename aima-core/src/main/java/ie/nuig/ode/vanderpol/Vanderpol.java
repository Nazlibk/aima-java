package ie.nuig.ode.vanderpol;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by nazli on 05/07/17.
 */
class Vanderpol implements FirstOrderDifferentialEquations {

    private int dimention;

    private AtomicReference<Double> epsilon = new AtomicReference<>(0.1);
    private AtomicReference<Double> a = new AtomicReference<>(0.5);

    public void setEpsilon(double epsilon){
        this.epsilon.set(epsilon);
    }

    public void setA(double a){
        this.a.set(a);
    }

    public void setInitialCondition(double[] y0) {
        dimention = y0.length;
    }

    @Override
    public int getDimension() {
        return dimention;
    }

    @Override
    public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
        yDot[0] = (1d / epsilon.get()) * (y[1] - Math.pow(y[0], 3) / 3d + y[0]);
        yDot[1] = a.get() - y[0];
    }
}
