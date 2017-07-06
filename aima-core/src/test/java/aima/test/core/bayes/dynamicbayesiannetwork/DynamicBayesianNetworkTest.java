package aima.test.core.bayes.dynamicbayesiannetwork;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.DynamicBayesianNetwork;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.bayes.impl.DynamicBayesNet;
import aima.core.probability.util.RandVar;
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
import static org.junit.Assert.assertThat;

/**
 * Created by nazli on 22/06/17.
 */
public class DynamicBayesianNetworkTest {
    DBNGenerator dbnGenerator;
    private DynamicBayesianNetwork dbn;

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

    @Before
    public void setup(){
        dbnGenerator = new DBNGenerator();
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
        //X_1_to_X_1.put(y1_t_RV, deltay1_t_RV);
        X_1_to_X_1.put(epsilon_t_RV, deltay1_t_RV);
        X_1_to_X_1.put(y2_t_RV, deltay1_t_RV);

        Set<RandomVariable> E_1 = new HashSet<RandomVariable>();
        E_1.add(observedy1_t_RV);

        dbn = new DynamicBayesNet(priorNetwork, X_0_to_X_1, X_1_to_X_1, E_1, rootNodes);
    }

    @Test
    public void test_deltaY1_must_calculate_equation(){
        //Equation = (y2 - (Math.pow(y1, 3))/3 + y1)/epsilon)
        double epsilon = 0.07;
        double y1 = 0.5;
        double y2 = 0.7;
        double result = dbnGenerator.deltaY1(epsilon, y1, y2);
        assertThat(result, is(16.547619047619044));
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
        double a = 0;
        double y1 = 0.5;
        double result = dbnGenerator.deltaY2(a, y1);
        assertThat(result, is(a - y1));
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
        assertThat(expected, is(dbn.getNode(a_tm1_RV).getChildren().size()));
        expected = 1;
        assertThat(expected, is(dbn.getNode(a_t_RV).getChildren().size()));
        expected = 3;
        assertThat(expected, is(dbn.getNode(y1_tm1_RV).getChildren().size()));
        expected = 3;
        assertThat(expected, is(dbn.getNode(y1_t_RV).getChildren().size()));
        expected = 2;
        assertThat(expected, is(dbn.getNode(epsilon_tm1_RV).getChildren().size()));
        expected = 1;
        assertThat(expected, is(dbn.getNode(epsilon_t_RV).getChildren().size()));
        expected = 2;
        assertThat(expected, is(dbn.getNode(y2_tm1_RV).getChildren().size()));
        expected = 1;
        assertThat(expected, is(dbn.getNode(y2_t_RV).getChildren().size()));
        expected = 1;
        assertThat(expected, is(dbn.getNode(deltay1_tm1_RV).getChildren().size()));
        expected = 0;
        assertThat(expected, is(dbn.getNode(deltay1_t_RV).getChildren().size()));
        expected = 1;
        assertThat(expected, is(dbn.getNode(deltay2_tm1_RV).getChildren().size()));
        expected = 0;
        assertThat(expected, is(dbn.getNode(deltay2_t_RV).getChildren().size()));
        expected = 0;
        assertThat(expected, is(dbn.getNode(observedy1_t_RV).getChildren().size()));
    }

    @Test
    public void test_DBN_nodes_getparent(){
        int expected = 0;
        assertThat(expected, is(dbn.getNode(a_tm1_RV).getParents().size()));
        expected = 1;
        assertThat(expected, is(dbn.getNode(a_t_RV).getParents().size()));
        expected = 0;
        assertThat(expected, is(dbn.getNode(y1_tm1_RV).getParents().size()));
        expected = 2;
        assertThat(expected, is(dbn.getNode(y1_t_RV).getParents().size()));
        expected = 0;
        assertThat(expected, is(dbn.getNode(epsilon_tm1_RV).getParents().size()));
        expected = 1;
        assertThat(expected, is(dbn.getNode(epsilon_t_RV).getParents().size()));
        expected = 0;
        assertThat(expected, is(dbn.getNode(y2_tm1_RV).getParents().size()));
        expected = 2;
        assertThat(expected, is(dbn.getNode(y2_t_RV).getParents().size()));
        expected = 3;
        assertThat(expected, is(dbn.getNode(deltay1_tm1_RV).getParents().size()));
        expected = 3;
        assertThat(expected, is(dbn.getNode(deltay1_t_RV).getParents().size()));
        expected = 2;
        assertThat(expected, is(dbn.getNode(deltay2_tm1_RV).getParents().size()));
        expected = 2;
        assertThat(expected, is(dbn.getNode(deltay2_t_RV).getParents().size()));
        expected = 1;
        assertThat(expected, is(dbn.getNode(observedy1_t_RV).getParents().size()));
    }

    @After
    public void tearDown(){

    }
}
