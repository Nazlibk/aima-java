package ie.nuig.ml.bayes;

import aima.core.probability.ProbabilityDistribution;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.ConditionalProbabilityDistribution;
import aima.core.probability.proposition.AssignmentProposition;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nazli on 04/07/17.
 */
public class ContinuesConditionalProbabilityDistribution implements ConditionalProbabilityDistribution {

    private RealDistribution realDistribution;
    private RandomVariable on = null;
    private LinkedHashSet<RandomVariable> discreteParents = new LinkedHashSet<RandomVariable>();
    private LinkedHashSet<RandomVariable> continuousParents = new LinkedHashSet<RandomVariable>();
    private double offset = 0.0;
    private double std;
    private double coefficient = 1.0;
    private double mean;

    public ContinuesConditionalProbabilityDistribution(RealDistribution realDistribution) {
        this.realDistribution = realDistribution;
        if(realDistribution instanceof NormalDistribution){
            std = ((NormalDistribution) realDistribution).getStandardDeviation();
            mean = ((NormalDistribution) realDistribution).getMean();
            offset = this.getOffset();
            coefficient = this.getCoefficient();
        }
    }


    public ContinuesConditionalProbabilityDistribution(RealDistribution realDistribution,
                                                       RandomVariable on,
                                                       RandomVariable... conditionedOn) {
        this.on = on;
        if (null == conditionedOn) {
            conditionedOn = new RandomVariable[0];
        }
        this.realDistribution = realDistribution;
        for (RandomVariable rv :conditionedOn) {
            if(rv.getDomain().isFinite()){
                discreteParents.add(rv);
            }else if(rv.getDomain().isInfinite()){
                continuousParents.add(rv);
            }
        }
        if(realDistribution instanceof NormalDistribution){
            std = ((NormalDistribution) realDistribution).getStandardDeviation();
            mean = ((NormalDistribution) realDistribution).getMean();
            offset = this.getOffset();
            coefficient = this.getCoefficient();
        }

    }

    @Override
    public RandomVariable getOn() {
        return this.on;
    }

    @Override
    public Set<RandomVariable> getParents() {
        LinkedHashSet<RandomVariable> parents = new LinkedHashSet<RandomVariable>();
        parents.addAll(discreteParents);
        parents.addAll(continuousParents);
        return parents;
    }

    @Override
    public Set<RandomVariable> getFor() {
        throw new UnsupportedOperationException("not implemented yet <getFor>");
    }

    @Override
    public boolean contains(RandomVariable rv) {
        throw new UnsupportedOperationException("not implemented yet <contains>");
    }

    @Override
    public double getValue(Object... eventValues) {
        throw new UnsupportedOperationException("not implemented yet <getValue>");
    }

    @Override
    public double getValue(AssignmentProposition... eventValues) {
        throw new UnsupportedOperationException("not implemented yet <getValue>");
    }

    @Override
    public ProbabilityDistribution getConditioningCase(Object... parentValues) {
        throw new UnsupportedOperationException("not implemented yet <getConditioningCase>");
    }

    @Override
    public ProbabilityDistribution getConditioningCase(AssignmentProposition... parentValues) {
        throw new UnsupportedOperationException("not implemented yet <getConditioningCase>");
    }

    @Override
    public Object getSample(double probabilityChoice, Object... parentValues) {
        if(this.realDistribution instanceof NormalDistribution){
            RealDistribution realDistribution = new NormalDistribution(this.mean, this.offset);
            return realDistribution.sample();
        }
        else if(this.realDistribution instanceof ConstantRealDistribution){
            return realDistribution.sample();
        } else{
            throw new UnsupportedOperationException("Not implemented yet (" + realDistribution + ")");
        }
            //throw new UnsupportedOperationException("Not implemented yet (" + realDistribution + ")");
    }

    @Override
    public Object getSample(double probabilityChoice, AssignmentProposition... parentValues) {
        return this.realDistribution.sample();
    }


    public double getOffset() {
        return offset;
    }

    public double getCoefficient() { return coefficient;}

    public RealDistribution getRealDistribution() { return realDistribution;}
}
