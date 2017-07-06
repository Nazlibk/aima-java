package ie.nuig.ml.bayes.particlefilter;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.DynamicBayesianNetwork;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.bayes.impl.DynamicBayesNet;
import aima.core.probability.util.RandVar;
import ie.nuig.ml.bayes.ContinuousNodeImpl;
import ie.nuig.ml.bayes.ProbabilityDistributedFunction;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.*;

/**
 * Created by nazli on 09/06/17.
 */
public class ParticleFilterUtils {

    //TODO: These are global variables for some methods in this class
    int nodesPerSlice;
    double[] ps = {};
    double[] ws = new double[1000];//

    //TODO: dbn is a global value for some methods in this class
    public DynamicBayesianNetwork dbnGenerator(){
        DynamicBayesianNetwork dbn;

        RealDistribution g = new NormalDistribution();

        final RandVar a_tm1_RV = new RandVar("A_tm1_RV", new ProbabilityDistributedFunction(g));
        final RandVar a_t_RV = new RandVar("A_t_RV", new ProbabilityDistributedFunction(g));
        final RandVar epsilon_tm1_RV = new RandVar("Epsilon_tm1_RV", new ProbabilityDistributedFunction(g));
        final RandVar epsilon_t_RV = new RandVar("Epsilon_t_RV", new ProbabilityDistributedFunction(g));
        final RandVar y1_tm1_RV = new RandVar("Y1_tm1_RV", new ProbabilityDistributedFunction(g));
        final RandVar y1_t_RV = new RandVar("Y1_t_RV", new ProbabilityDistributedFunction(g));
        final RandVar y2_tm1_RV = new RandVar("Y2_tm1_RV", new ProbabilityDistributedFunction(g));
        final RandVar y2_t_RV = new RandVar("Y2_t_RV", new ProbabilityDistributedFunction(g));
        final RandVar deltay1_t_RV = new RandVar("Deltay1_t_RV", new ProbabilityDistributedFunction(g));
        final RandVar deltay1_tm1_RV = new RandVar("Deltay1_tm1_RV", new ProbabilityDistributedFunction(g));
        final RandVar deltay2_t_RV = new RandVar("Deltay2_t_RV", new ProbabilityDistributedFunction(g));
        final RandVar deltay2_tm1_RV = new RandVar("Deltay2_tm1_RV", new ProbabilityDistributedFunction(g));
        final RandVar observedy1_t_RV = new RandVar("Observedy1_t_RV", new ProbabilityDistributedFunction(g));

            Node a_tm1 = new ContinuousNodeImpl(a_tm1_RV);
            Node y1_tm1 = new ContinuousNodeImpl(y1_tm1_RV);
            Node epsilon_tm1 = new ContinuousNodeImpl(epsilon_tm1_RV);
            Node y2_tm1 = new ContinuousNodeImpl(y2_tm1_RV);
            Node deltay1_tm1 = new ContinuousNodeImpl(deltay1_tm1_RV, y1_tm1, epsilon_tm1, y2_tm1);
            Node deltay2_tm1 = new ContinuousNodeImpl(deltay2_tm1_RV, a_tm1, y1_tm1);
            Node a_t = new ContinuousNodeImpl(a_t_RV, a_tm1);
            Node y1_t = new ContinuousNodeImpl(y1_t_RV, y1_tm1, deltay1_tm1);
            Node epsilon_t = new ContinuousNodeImpl(epsilon_t_RV, epsilon_tm1);
            Node y2_t = new ContinuousNodeImpl(y2_t_RV, y2_tm1, deltay2_tm1);
            Node deltay1_t = new ContinuousNodeImpl(deltay1_t_RV, y1_t, epsilon_t, y2_t);
            Node deltay2_t = new ContinuousNodeImpl(deltay2_t_RV, a_t, y1_t);
            Node observedy1_t = new ContinuousNodeImpl(observedy1_t_RV, y1_t);

            List<Node> rootNodesList = new ArrayList<>();
            rootNodesList.add(a_tm1);
            rootNodesList.add(y1_tm1);
            rootNodesList.add(epsilon_tm1);
            rootNodesList.add(y2_tm1);
            Node[] rootNodes = rootNodesList.toArray(new Node[]{});

            BayesianNetwork priorNetwork = new BayesNet(rootNodes);

            Map<RandomVariable, RandomVariable> X_0_to_X_1 = new HashMap<RandomVariable, RandomVariable>();
            X_0_to_X_1.put(a_tm1_RV, a_t_RV);
            X_0_to_X_1.put(epsilon_tm1_RV, epsilon_t_RV);
            X_0_to_X_1.put(y1_tm1_RV, y1_t_RV);
            X_0_to_X_1.put(y2_tm1_RV, y2_t_RV);
            X_0_to_X_1.put(deltay1_tm1_RV, y1_t_RV);
            X_0_to_X_1.put(deltay2_tm1_RV, y2_t_RV);

            Map<RandomVariable, RandomVariable> X_1_to_X_1 = new HashMap<RandomVariable, RandomVariable>();
            X_1_to_X_1.put(a_t_RV, deltay2_t_RV);
            X_1_to_X_1.put(y1_t_RV, deltay2_t_RV);
            X_1_to_X_1.put(epsilon_t_RV, deltay1_t_RV);
            X_1_to_X_1.put(y2_t_RV, deltay1_t_RV);

            Set<RandomVariable> E_1 = new HashSet<RandomVariable>();
            E_1.add(observedy1_t_RV);

            dbn = new DynamicBayesNet(priorNetwork, X_0_to_X_1, X_1_to_X_1, E_1, rootNodes);
            return  dbn;
    }

    public ToleranceCheckResult checkTolerance(List<Double> particle, List<Integer> nodes, double tolerance, double stepSize) {
        final double safetyFactor = 0.9;
        final double M1 = 1.5;
        final double M2 = 0.9;
        double d2y;
        double max = 0;
        double tempMax;
        double newStepsize;
        nodesPerSlice = 7;

        for (int i = 0; i < nodes.size() - 1; i++) {
            tempMax = particle.get(nodes.get(i)) - particle.get(nodes.get((i)) + nodesPerSlice);
            if (Math.abs(tempMax) > max) {
                max = Math.abs(tempMax);
            }
        }
        d2y = max / stepSize;
        System.out.println("d2y = " + d2y);
        if ((Math.pow(stepSize, 2) * d2y / 2) < tolerance) {
            newStepsize = Math.min(safetyFactor * Math.sqrt(2 * tolerance / d2y), stepSize * M1);
            return new ToleranceCheckResult(false, newStepsize);
        } else {
            newStepsize = Math.min(safetyFactor * Math.sqrt(2 * tolerance / d2y), stepSize * M2);
            return new ToleranceCheckResult(true, newStepsize);
        }
    }

    public double GetNextT(double currentT, double summaryInterval){
        return currentT + summaryInterval;
    }

    public void weightParticles(List<Node> weightNodes, List<Double> sliceEvidence, DynamicBayesianNetwork dbn){
        //TODO: I am not sure about the type of sliceEvidence

        double nodeValue = 0;//TODO: depends on below TODOs
        int dbnSize = dbn.getE_1().size() + dbn.getX_0().size() + dbn.getX_1().size();

        for(int i = 0; i < ws.length; i++){
            ws[i] = 0.001;
        }

        for(int i = 0; i < ps.length; i++) {
            ws[i] = 1.0;
            if (!weightNodes.isEmpty()) {
                //TODO: VanDer Pol example never come to this part so I haven't implemented this part
                for (int j = 0; j < weightNodes.size(); j++) {
                    //nodeValue = sliceEvidence.get(weightNodes.get(j).index) - dbnSize;//TODO: find what is bnode-index (index)
                    if (nodeValue != 0) {
                        //ws[i] *= weightNodes.get(j).
                    }
                }
            } else {
                for (int j = 0; j < dbn.getX_1().size(); j++) {
                    //There is another if here that makes VanDer Pol example to never come here
                    //TODO: find what is bnode-index and implement this part
                }
            }
        }
        //TODO: There is no junit for this function
    }

    public double[] setSliceEvidence(double currentT, double finishTime){
        double largestT = 0;
        nodesPerSlice = 6;
        double[] lSliceEvidence = new double[nodesPerSlice]; //TODO: I am not sure about the type of lSliceEvidence
        List<Integer[][]> continuousEvidence = new ArrayList<>(); //TODO: I am not sure about the type of continuousEvidence  TODO: This is a global variable
        List<Integer[][]> instantEvidence = new ArrayList<>(); //TODO: I am not sure about the type of instantEvidence  TODO: This is a global variable

        for(Integer[][] nodeEvidence: continuousEvidence){//TODO: I haven't check this part because VanDer Pol example never come to this part
            for(int i = 0; i < nodeEvidence.length; i++){
                if((nodeEvidence[i][1] <= currentT) && (largestT <= nodeEvidence[i][1])){
                    lSliceEvidence[nodeEvidence[i][0]] = nodeEvidence[i][2];
                    largestT = nodeEvidence[i][1];
                }
            }
            largestT = 0;
        }
        if(currentT < finishTime){
            for(Integer[][] nodeEvidence: instantEvidence){//TODO: I haven't check this part because VanDer Pol example never come to this part
                for(int i = 0; i < nodeEvidence.length; i++){
                    if(nodeEvidence[i][1] == currentT){
                        lSliceEvidence[nodeEvidence[i][0]] = nodeEvidence[i][2];
                    }
                }
            }
        }
        return lSliceEvidence;
    }

    public List<Double> apfStep(List<Double> particle, double stepSize, List<Double> sliceEvidence, double definedStep){
        nodesPerSlice = 6;
        DynamicBayesianNetwork dbn = dbnGenerator();
        Set<RandomVariable> nodes = dbn.getX_1();
        nodes.add((RandomVariable) dbn.getX_11());
        int firstParticleIndex = nodesPerSlice;
        double scaleFactor = Math.sqrt(definedStep/stepSize);
        double deltaStep = stepSize;//In the code deltaStep is never used!!!

        int i = firstParticleIndex;
        for(RandomVariable rv:nodes){
            for(double se: sliceEvidence){
                if(se == 0){
                    particle.set(i, 5.0);
                }else{
                    particle.set(i, se);
                }
            }
            i++;
        }
        return particle;
    }
}
