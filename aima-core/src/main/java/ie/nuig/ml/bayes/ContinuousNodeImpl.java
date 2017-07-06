package ie.nuig.ml.bayes;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.ConditionalProbabilityDistribution;
import aima.core.probability.bayes.ContinuousNode;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.impl.AbstractNode;
import aima.core.probability.domain.Domain;

import java.util.Set;

/**
 * Created by nazli on 23/06/17.
 */
public class ContinuousNodeImpl extends AbstractNode implements ContinuousNode {

    private ProbabilityDistributedFunction cpd;
    private ConditionalProbabilityDistribution conditionalProbabilityDistribution;

    public ContinuousNodeImpl(RandomVariable var) {
        super(var);
        cpd = (ProbabilityDistributedFunction)(var.getDomain());
        conditionalProbabilityDistribution = new ContinuesConditionalProbabilityDistribution(cpd.realDistribution);
    }

    public ContinuousNodeImpl(RandomVariable var, Node... parents) {
        super(var, parents);
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
        return conditionalProbabilityDistribution;
    }

    public Domain getDomain(){
        return super.getRandomVariable().getDomain();
    }

}
