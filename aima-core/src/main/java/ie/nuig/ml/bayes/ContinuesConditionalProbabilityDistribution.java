package ie.nuig.ml.bayes;

import aima.core.probability.ProbabilityDistribution;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.ConditionalProbabilityDistribution;
import aima.core.probability.proposition.AssignmentProposition;
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
    private List<Double> offset = new ArrayList<>();
    private List<Double> mean = new ArrayList<>();

    public ContinuesConditionalProbabilityDistribution(RealDistribution realDistribution) {
        this.realDistribution = realDistribution;
        if(realDistribution instanceof NormalDistribution){
            NormalDistribution normalDistribution = (NormalDistribution) realDistribution;
            offset.add(normalDistribution.getStandardDeviation());
            mean.add(normalDistribution.getMean());
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
            NormalDistribution normalDistribution = (NormalDistribution) realDistribution;
            offset.add(normalDistribution.getStandardDeviation());
            mean.add(normalDistribution.getMean());
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
        RealDistribution realDistribution = new NormalDistribution(this.mean.get(0), this.offset.get(0));
        return realDistribution.sample();
    }

    @Override
    public Object getSample(double probabilityChoice, AssignmentProposition... parentValues) {
        return this.realDistribution.sample();
    }


    public List<Double> getOffset() {
        return offset;
    }

    public List<Double> getMean() {
        return mean;
    }
}
