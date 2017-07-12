package ie.nuig.ml.bayes;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.ConditionalProbabilityDistribution;
import aima.core.probability.bayes.ContinuousNode;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.impl.AbstractNode;
import aima.core.probability.domain.Domain;
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

    public ContinuesConditionalProbabilityDistribution returnCPD(){
        //if(cpd.getRealDistribution().equals(new NormalDistribution())){
            List<ContinuousNodeImpl> parents = new ArrayList<>();
            for(Node p: getParents()){
                parents.add((ContinuousNodeImpl)p);
            }
            double offset = this.conditionalProbabilityDistribution.getOffset().get(0);
            double mean = this.conditionalProbabilityDistribution.getMean().get(0);;
            if(parents.size() != 0){
                offset = 0;
                mean = 0;
                for(ContinuousNodeImpl p:parents){
                    offset += p.conditionalProbabilityDistribution.getOffset().get(0);
                    mean += p.conditionalProbabilityDistribution.getMean().get(0);
                }
                offset = offset/parents.size();
                mean = mean/parents.size();
            }
            RealDistribution realDistribution = new NormalDistribution(mean, offset);
        //}
        return new ContinuesConditionalProbabilityDistribution(realDistribution);
    }
}
