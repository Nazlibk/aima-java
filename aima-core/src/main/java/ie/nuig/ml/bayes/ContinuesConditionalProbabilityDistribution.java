package ie.nuig.ml.bayes;

import aima.core.probability.ProbabilityDistribution;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.ConditionalProbabilityDistribution;
import aima.core.probability.proposition.AssignmentProposition;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.Set;

/**
 * Created by nazli on 04/07/17.
 */
public class ContinuesConditionalProbabilityDistribution implements ConditionalProbabilityDistribution {

    private RealDistribution realDistribution;

    public ContinuesConditionalProbabilityDistribution(RealDistribution realDistribution) {
        this.realDistribution = realDistribution;
    }

    @Override
    public RandomVariable getOn() {
        return null;
    }

    @Override
    public Set<RandomVariable> getParents() {
        return null;
    }

    @Override
    public Set<RandomVariable> getFor() {
        return null;
    }

    @Override
    public boolean contains(RandomVariable rv) {
        return false;
    }

    @Override
    public double getValue(Object... eventValues) {
        return 0;
    }

    @Override
    public double getValue(AssignmentProposition... eventValues) {
        return 0;
    }

    @Override
    public ProbabilityDistribution getConditioningCase(Object... parentValues) {
        return null;
    }

    @Override
    public ProbabilityDistribution getConditioningCase(AssignmentProposition... parentValues) {
        return null;
    }

    @Override
    public Object getSample(double probabilityChoice, Object... parentValues) {
        return null;
    }

    @Override
    public Object getSample(double probabilityChoice, AssignmentProposition... parentValues) {
        return null;
    }
}
