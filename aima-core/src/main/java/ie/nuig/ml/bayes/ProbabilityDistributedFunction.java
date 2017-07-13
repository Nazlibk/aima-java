package ie.nuig.ml.bayes;

import aima.core.probability.domain.AbstractContinuousDomain;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Created by nazli on 23/06/17.
 */
public class ProbabilityDistributedFunction extends AbstractContinuousDomain {


    RealDistribution realDistribution;
    double value = Double.MAX_VALUE;

    public ProbabilityDistributedFunction(RealDistribution realDistribution) {
        this.realDistribution = realDistribution;
    }

    public ProbabilityDistributedFunction(double value) {
        this.value = value;
    }

    public RealDistribution getRealDistribution() {
        return realDistribution;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    public double getValue() {
        return value;
    }

    public double sample(){
        if(value == Double.MAX_VALUE){
            return realDistribution.sample();
        }else{
            return getValue();
        }
    }
}
