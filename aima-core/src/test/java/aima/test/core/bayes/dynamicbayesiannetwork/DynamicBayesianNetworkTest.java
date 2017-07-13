package aima.test.core.bayes.dynamicbayesiannetwork;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.DynamicBayesianNetwork;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.approx.ParticleFiltering;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.bayes.impl.DynamicBayesNet;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.util.RandVar;
import aima.core.util.MockRandomizer;
import ie.nuig.ml.bayes.ContinuousNodeImpl;
import ie.nuig.ml.bayes.ProbabilityDistributedFunction;
import ie.nuig.ml.bayes.dynamicbayesiannetwork.DBNGenerator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by nazli on 22/06/17.
 */
public class DynamicBayesianNetworkTest {

    MockRandomizer mr;
    DBNGenerator dbnGenerator;
    private DynamicBayesianNetwork dbn;

    RealDistribution g = new NormalDistribution();

    final RandVar a_t0_RV = new RandVar("A_t0_RV", new ProbabilityDistributedFunction(g));
    final RandVar a_t1_RV = new RandVar("A_t1_RV", new ProbabilityDistributedFunction(g));
    final RandVar epsilon_t0_RV = new RandVar("Epsilon_t0_RV", new ProbabilityDistributedFunction(g));
    final RandVar epsilon_t1_RV = new RandVar("Epsilon_t1_RV", new ProbabilityDistributedFunction(g));
    final RandVar y1_t0_RV = new RandVar("Y1_t0_RV", new ProbabilityDistributedFunction(g));
    final RandVar y1_t1_RV = new RandVar("Y1_t1_RV", new ProbabilityDistributedFunction(g));
    final RandVar y2_t0_RV = new RandVar("Y2_t0_RV", new ProbabilityDistributedFunction(g));
    final RandVar y2_t1_RV = new RandVar("Y2_t1_RV", new ProbabilityDistributedFunction(g));
    final RandVar deltay1_t1_RV = new RandVar("Deltay1_t1_RV", new ProbabilityDistributedFunction(g));
    final RandVar deltay1_t0_RV = new RandVar("Deltay1_t0_RV", new ProbabilityDistributedFunction(g));
    final RandVar deltay2_t1_RV = new RandVar("Deltay2_t1_RV", new ProbabilityDistributedFunction(g));
    final RandVar deltay2_t0_RV = new RandVar("Deltay2_t0_RV", new ProbabilityDistributedFunction(g));
    final RandVar observedy1_t1_RV = new RandVar("Observedy1_t1_RV", new ProbabilityDistributedFunction(g));

    @Before
    public void setup(){

        dbnGenerator = new DBNGenerator();
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

        //randomizer
        int N = 10;
        int timeStep = 2;
        int numberOfNoes = dbn.getX_1().size() + dbn.getX_0().size() + dbn.getX_11().size();
        double[] randomNumbers = new double[numberOfNoes * N * timeStep];
        for(int i = 0; i < randomNumbers.length; i++){
            randomNumbers[i] = Math.random();
        }
        mr = new MockRandomizer(randomNumbers);
    }

    @Test
    public void test_deltaY1_must_calculate_equation(){
        //Equation = (y2 - (Math.pow(y1, 3))/3 + y1)/epsilon)
        double epsilon = 0.1;
        double y1 = 0.5;
        double y2 = 0.7;
        double result = dbnGenerator.deltaY1(epsilon, y1, y2);
        assertThat(result, is(11.583333333333332));
    }

    @Test
    public void test_deltaY1_must_return_Infifnity(){
        //Equation = (y2 - (Math.pow(y1, 3))/3 + y1)/epsilon)
        double epsilon = 0;
        double y1 = 0;
        double y2 = 0.7;
        double result = dbnGenerator.deltaY1(epsilon, y1, y2);
        assertThat(result, is(Math.abs(Double.POSITIVE_INFINITY)));
    }

    @Test
    public void test_deltaY2_must_calculate_equation(){
        //Equation = a - y1
        double a = 0.5;
        double y1 = 0.3;
        double result = dbnGenerator.deltaY2(a, y1);
        assertThat(result, is(a - y1));
    }

    @Test
    public void test_y1_1_must_calculate_equation(){
        //Equation = y1_1 = (deltay1_0 * h0) + y1_0
        double h0 = 1/120;
        double y1_0 = 1;
        double deltaY1_0 = 120;
        double result = dbnGenerator.y1_1(deltaY1_0, y1_0, h0);
        assertThat(result, is((deltaY1_0 * h0) + y1_0));
    }

    @Test
    public void test_y2_1_must_calculate_equation(){
        //Equation = y2_1 = (deltay2_0 * h0) + y2_0
        double h0 = 1/120;
        double y2_0 = 1;
        double deltaY2_0 = 120;
        double result = dbnGenerator.y1_1(deltaY2_0, y2_0, h0);
        assertThat(result, is((deltaY2_0 * h0) + y2_0));
    }

    @Test
    public void test_y21_and_y11_must_calculate_equation(){
        //Equation = var0 + deltaStep * delta0
        double var0 = 0.6;
        double deltaStep = 0.5;
        double delta0 = 0.7;
        double result = dbnGenerator.Y2_1andY1_1(delta0, var0, deltaStep);
        assertThat(result, is(var0 + deltaStep * delta0));
    }

    @Test
    public void test_DBN_nodes_getchildren(){
        int expected = 2;
        assertThat(dbn.getNode(a_t0_RV).getChildren().size(), is(expected));
        expected = 1;
        assertThat(dbn.getNode(a_t1_RV).getChildren().size(), is(expected));
        expected = 3;
        assertThat(dbn.getNode(y1_t0_RV).getChildren().size(), is(expected));
        expected = 3;
        assertThat(dbn.getNode(y1_t1_RV).getChildren().size(), is(expected));
        expected = 2;
        assertThat(dbn.getNode(epsilon_t0_RV).getChildren().size(), is(expected));
        expected = 1;
        assertThat(dbn.getNode(epsilon_t1_RV).getChildren().size(), is(expected));
        expected = 2;
        assertThat(dbn.getNode(y2_t0_RV).getChildren().size(), is(expected));
        expected = 1;
        assertThat(dbn.getNode(y2_t1_RV).getChildren().size(), is(expected));
        expected = 1;
        assertThat(dbn.getNode(deltay1_t0_RV).getChildren().size(), is(expected));
        expected = 0;
        assertThat(dbn.getNode(deltay1_t1_RV).getChildren().size(), is(expected));
        expected = 1;
        assertThat(dbn.getNode(deltay2_t0_RV).getChildren().size(), is(expected));
        expected = 0;
        assertThat(dbn.getNode(deltay2_t1_RV).getChildren().size(), is(expected));
        expected = 0;
        assertThat(dbn.getNode(observedy1_t1_RV).getChildren().size(), is(expected));
    }

    @Test
    public void test_DBN_nodes_getparent(){
        int expected = 0;
        assertThat(dbn.getNode(a_t0_RV).getParents().size(), is(expected));
        expected = 1;
        assertThat(dbn.getNode(a_t1_RV).getParents().size(), is(expected));
        expected = 0;
        assertThat(dbn.getNode(y1_t0_RV).getParents().size(), is(expected));
        expected = 2;
        assertThat(dbn.getNode(y1_t1_RV).getParents().size(), is(expected));
        expected = 0;
        assertThat(dbn.getNode(epsilon_t0_RV).getParents().size(), is(expected));
        expected = 1;
        assertThat(dbn.getNode(epsilon_t1_RV).getParents().size(), is(expected));
        expected = 0;
        assertThat(dbn.getNode(y2_t0_RV).getParents().size(), is(expected));
        expected = 2;
        assertThat(dbn.getNode(y2_t1_RV).getParents().size(), is(expected));
        expected = 3;
        assertThat(dbn.getNode(deltay1_t0_RV).getParents().size(), is(expected));
        expected = 3;
        assertThat(dbn.getNode(deltay1_t1_RV).getParents().size(), is(expected));
        expected = 2;
        assertThat(dbn.getNode(deltay2_t0_RV).getParents().size(), is(expected));
        expected = 2;
        assertThat(dbn.getNode(deltay2_t1_RV).getParents().size(), is(expected));
        expected = 1;
        assertThat(dbn.getNode(observedy1_t1_RV).getParents().size(), is(expected));
    }

    @Test
    public void test_vanderpol_dbn(){
        int timeStep = 2;
        double stepSize = 1/120;
        double[] y1 = new double[timeStep + 1];
        double[] y2 = new double[timeStep + 1];
        double[] sampledY1 = new double[timeStep];
        double[] sampledY2 = new double[timeStep];
        int N = 10;
        double epsilon = 0.1;
        double a = 0.5;
        double deltaY1;
        double deltaY2;
        double sum = 0;

        y1[0] = 1.0;
        y2[0] = 1.0;
        ParticleFiltering pf = new ParticleFiltering(N, dbn, mr);
        AssignmentProposition[] evidenceY1;
        int i = 0;
        do{
            deltaY1 = dbnGenerator.deltaY1(epsilon, y1[i], y2[i]);
            deltaY2 = dbnGenerator.deltaY2(a, y1[i]);
            evidenceY1 = new AssignmentProposition[]{new AssignmentProposition(observedy1_t1_RV, y1[i])};
            System.out.println("Sample set " + (i + 1) + ":");
            AssignmentProposition[][] S;
            if(i == 0){
                S = pf.particleFiltering(evidenceY1, "First time step");
            }else {
                S = pf.particleFiltering(evidenceY1, sampledY1[i - 1]);
            }
            for (int j = 0; j < N; j++) {
                System.out.println("Sample " + (j + 1) + " = " + S[j][5]);
            }
            for(int j = 0; j < S.length; j++){
                sum += (double) S[j][5].getValue();
            }
            sampledY1[i] = sum/S.length;
            System.out.println("Final value obtained from samples in run number "
                    + (i + 1) + " is: " + sampledY1[i]);
            i++;
            y1[i] = dbnGenerator.y1_1(deltaY1, y1[i - 1], stepSize);
            y2[i] = dbnGenerator.y2_1(deltaY2, y2[i - 1], stepSize);

        }while (i < timeStep);
    }

    @After
    public void tearDown(){

    }
}
