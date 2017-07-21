package ie.nuig.ml.bayes;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.ConditionalProbabilityDistribution;
import aima.core.probability.bayes.ContinuousNode;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.impl.AbstractNode;
import aima.core.probability.domain.Domain;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by nazli on 23/06/17.
 */
public class ContinuousNodeImpl extends AbstractNode implements ContinuousNode {

    private ProbabilityDistributedFunction cpd = null;
    private ContinuesConditionalProbabilityDistribution conditionalProbabilityDistribution;

    public ContinuousNodeImpl(RandomVariable var) {
        super(var);
        cpd = (ProbabilityDistributedFunction)(var.getDomain());
        conditionalProbabilityDistribution = new ContinuesConditionalProbabilityDistribution(cpd.realDistribution);
    }

    public ContinuousNodeImpl(RandomVariable var, Node... parents) {
        super(var, parents);
        cpd = (ProbabilityDistributedFunction)(var.getDomain());
        RandomVariable[] conditionedOn = new RandomVariable[getParents().size()];
        int i = 0;
        for (Node p : getParents()) {
            conditionedOn[i++] = p.getRandomVariable();
        }
        conditionalProbabilityDistribution = new ContinuesConditionalProbabilityDistribution(cpd.realDistribution, var, conditionedOn);
    }

    @Override
    public RandomVariable getRandomVariable() {
        return super.getRandomVariable();
    }

    @Override
    public boolean isRoot() {
        return 0 == getParents().size();
    }

    @Override
    public Set<Node> getMarkovBlanket() {
        throw new UnsupportedOperationException("not implemented yet <getMarkovBlancket>");
    }

    @Override
    public ConditionalProbabilityDistribution getCPD() {
        return returnCPD();
        //return conditionalProbabilityDistribution;
    }

    public Domain getDomain(){
        return super.getRandomVariable().getDomain();
    }

    public void setValue(double value){
        if(this.cpd.realDistribution instanceof ConstantRealDistribution){
            this.cpd.realDistribution = new ConstantRealDistribution(value);
        }
    }

    public ContinuesConditionalProbabilityDistribution returnCPD() {
        if (cpd.getRealDistribution() instanceof NormalDistribution) {
            List<ContinuousNodeImpl> parents = new ArrayList<>();
            for (Node p : getParents()) {
                parents.add((ContinuousNodeImpl) p);
            }
            double std = ((NormalDistribution)this.conditionalProbabilityDistribution.getRealDistribution()).getStandardDeviation();
            double mean = ((NormalDistribution)this.conditionalProbabilityDistribution.getRealDistribution()).getMean();
            if (parents.size() != 0) {
                std = 0;
                mean = 0;
                for (ContinuousNodeImpl p : parents) {
                    if (p.getDomain() instanceof ConstantRealDistribution) {
                        mean = ((ConstantRealDistribution) p.getDomain()).sample();
                        std = this.conditionalProbabilityDistribution.getOffset();
                    } else {
                        if (p.cpd.getRealDistribution() instanceof NormalDistribution) {
                            std += p.conditionalProbabilityDistribution.getOffset();
                            mean += ((NormalDistribution)p.conditionalProbabilityDistribution.getRealDistribution()).getMean()
                            * p.conditionalProbabilityDistribution.getCoefficient() + p.conditionalProbabilityDistribution.getOffset();
                        } else if (p.cpd.getRealDistribution() instanceof ConstantRealDistribution) {
                            //offset += p.cpd.getRealDistribution().sample();
                            mean += p.cpd.getRealDistribution().sample();
                        }
                    }
                    std = std / parents.size();
                    mean = mean / parents.size();
                }
            }
            RealDistribution realDistribution = new NormalDistribution(mean, std);
            return new ContinuesConditionalProbabilityDistribution(realDistribution);
        } else if (cpd.getRealDistribution() instanceof ConstantRealDistribution) {
            RealDistribution realDistribution = new ConstantRealDistribution(cpd.sample());
            return new ContinuesConditionalProbabilityDistribution(realDistribution);
        }else{
            throw new UnsupportedOperationException("Not implemented yet (" + cpd.getRealDistribution() + ")");
        }

    }
}
