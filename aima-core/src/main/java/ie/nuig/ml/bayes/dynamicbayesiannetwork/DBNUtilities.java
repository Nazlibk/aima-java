package ie.nuig.ml.bayes.dynamicbayesiannetwork;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.DynamicBayesianNetwork;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.bayes.impl.DynamicBayesNet;
import aima.core.probability.util.RandVar;
import ie.nuig.ml.bayes.ContinuousNodeImpl;
import ie.nuig.ml.bayes.ProbabilityDistributedFunction;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by nazli on 22/06/17.
 */
public class DBNUtilities {

    public DynamicBayesianNetwork dbnGenerator(){

        DynamicBayesianNetwork dbn;
        double epsilon = 0.1;
        double a = 0.5;
        double stepSize = 1.0/164.0;

        final RandVar a_t0_RV = new RandVar("A_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(a)));
        final RandVar a_t1_RV = new RandVar("A_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(a)));
        final RandVar epsilon_t0_RV = new RandVar("Epsilon_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(epsilon)));
        final RandVar epsilon_t1_RV = new RandVar("Epsilon_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(epsilon)));
        final RandVar y1_t0_RV = new RandVar("Y1_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(1.0)));
        final RandVar y1_t1_RV = new RandVar("Y1_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(y((5.0/3.0)/epsilon, 1, stepSize))));
        final RandVar y2_t0_RV = new RandVar("Y2_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(1.0)));
        final RandVar y2_t1_RV = new RandVar("Y2_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(y(0.5, 1, stepSize))));
        final RandVar deltay1_t1_RV = new RandVar("Deltay1_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(deltaY1(epsilon, 1.0, 1.0))));
        final RandVar deltay1_t0_RV = new RandVar("Deltay1_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(deltaY1(epsilon, 1.0, 1.0))));
        final RandVar deltay2_t1_RV = new RandVar("Deltay2_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(deltaY2(a, 1.0))));
        final RandVar deltay2_t0_RV = new RandVar("Deltay2_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(deltaY2(a, 1.0))));
        final RandVar observedy1_t1_RV = new RandVar("Observedy1_t1_RV", new ProbabilityDistributedFunction(new NormalDistribution()));

        //prior nodes
        Node a_tm1 = new ContinuousNodeImpl(a_t0_RV);
        Node y1_tm1 = new ContinuousNodeImpl(y1_t0_RV);
        Node epsilon_tm1 = new ContinuousNodeImpl(epsilon_t0_RV);
        Node y2_tm1 = new ContinuousNodeImpl(y2_t0_RV);
        Node deltay1_tm1 = new ContinuousNodeImpl(deltay1_t0_RV, y1_tm1, epsilon_tm1, y2_tm1);
        Node deltay2_tm1 = new ContinuousNodeImpl(deltay2_t0_RV, a_tm1, y1_tm1);
        //main nodes
        Node a_t0 = new ContinuousNodeImpl(a_t0_RV);
        Node y1_t0 = new ContinuousNodeImpl(y1_t0_RV);
        Node epsilon_t0 = new ContinuousNodeImpl(epsilon_t0_RV);
        Node y2_t0 = new ContinuousNodeImpl(y2_t0_RV);
        Node deltay1_t0 = new ContinuousNodeImpl(deltay1_t0_RV, y1_t0, epsilon_t0, y2_t0);
        Node deltay2_t0 = new ContinuousNodeImpl(deltay2_t0_RV, a_t0, y1_t0);
        Node a_t = new ContinuousNodeImpl(a_t1_RV, a_t0);
        Node y1_t = new ContinuousNodeImpl(y1_t1_RV, y1_t0, deltay1_t0);
        Node epsilon_t = new ContinuousNodeImpl(epsilon_t1_RV, epsilon_t0);
        Node y2_t = new ContinuousNodeImpl(y2_t1_RV, y2_t0, deltay2_t0);
        Node deltay1_t = new ContinuousNodeImpl(deltay1_t1_RV, y1_t, epsilon_t, y2_t);
        Node deltay2_t = new ContinuousNodeImpl(deltay2_t1_RV, a_t, y1_t);
        Node observedy1_t = new ContinuousNodeImpl(observedy1_t1_RV, y1_t);

        List<Node> priorNodesList = new ArrayList<>();
        priorNodesList.add(a_tm1);
        priorNodesList.add(y1_tm1);
        priorNodesList.add(epsilon_tm1);
        priorNodesList.add(y2_tm1);
        Node[] priorNodes = priorNodesList.toArray(new Node[]{});
        BayesianNetwork priorNetwork = new BayesNet(priorNodes);

        List<Node> rootNodesList = new ArrayList<>();
        rootNodesList.add(a_t0);
        rootNodesList.add(y1_t0);
        rootNodesList.add(epsilon_t0);
        rootNodesList.add(y2_t0);
        Node[] rootNodes = rootNodesList.toArray(new Node[]{});

        Map<RandomVariable, RandomVariable> X_0_to_X_1 = new HashMap<RandomVariable, RandomVariable>();
        X_0_to_X_1.put(a_t0_RV, a_t1_RV);
        X_0_to_X_1.put(epsilon_t0_RV, epsilon_t1_RV);
        X_0_to_X_1.put(y1_t0_RV, y1_t1_RV);
        X_0_to_X_1.put(y2_t0_RV, y2_t1_RV);
        X_0_to_X_1.put(deltay1_t0_RV, y1_t1_RV);
        X_0_to_X_1.put(deltay2_t0_RV, y2_t1_RV);

        Map<RandomVariable, RandomVariable> X_1_to_X_1 = new HashMap<RandomVariable, RandomVariable>();
        X_1_to_X_1.put(a_t1_RV, deltay2_t1_RV);
        X_1_to_X_1.put(y1_t1_RV, deltay2_t1_RV);
        X_1_to_X_1.put(epsilon_t1_RV, deltay1_t1_RV);
        X_1_to_X_1.put(y2_t1_RV, deltay1_t1_RV);

        Set<RandomVariable> E_1 = new HashSet<RandomVariable>();
        E_1.add(observedy1_t1_RV);

        dbn = new DynamicBayesNet(priorNetwork, X_0_to_X_1, X_1_to_X_1, E_1, rootNodes);
        return  dbn;
    }

    private static final String fileName = "evidence.csv";

    public CsvEvidenceReader readFromCsv() throws IOException{

        List<String> s = new ArrayList<>();
        String tmp;
        Path path = Paths.get(fileName);
        try(BufferedReader reader = Files.newBufferedReader(path)){
            List<Double> time = new ArrayList<>();
            List<Double> values =  new ArrayList<>();
            List<String> oneRow = new ArrayList<>();
            CsvEvidenceReader csvEvidenceReader;
            while ((tmp = reader.readLine()) != null) {
                s.add(tmp);
            }
            for(String element:s){
                tmp = element.substring(1, element.length() - 1);
                oneRow = Arrays.asList(tmp.split(" "));
                time.add(Double.valueOf(oneRow.get(0)));
                values.add(Double.valueOf(oneRow.get(1)));
            }
            csvEvidenceReader = new CsvEvidenceReader(time, values);
            return csvEvidenceReader;
        }
    }

    public double deltaY1(double epsilon, double y1, double y2){

        return (y2 - (Math.pow(y1, 3))/3 + y1)/epsilon;
    }

    public double deltaY2(double a, double y1){
        return a - y1;
    }

    public double y(double deltaY, double y, double stepsize){ return (deltaY * stepsize) + y;}

    public double Y2_1andY1_1(double delta0, double var0, double deltaStep){
        return var0 + deltaStep * delta0;
    }

}
