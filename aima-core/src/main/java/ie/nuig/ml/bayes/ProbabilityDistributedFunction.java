package ie.nuig.ml.bayes;

import aima.core.probability.domain.AbstractContinuousDomain;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Created by nazli on 23/06/17.
 */
public class ProbabilityDistributedFunction extends AbstractContinuousDomain {


    RealDistribution realDistribution;

    public ProbabilityDistributedFunction(RealDistribution realDistribution) {
        this.realDistribution = realDistribution;

    }

    public RealDistribution getRealDistribution() {
        return realDistribution;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }
}
