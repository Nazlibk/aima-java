package aima.test.core.bayes.dynamicbayesiannetwork;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.DynamicBayesianNetwork;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.approx.ParticleFiltering;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.util.RandVar;
import aima.core.util.MockRandomizer;
import ie.nuig.ml.bayes.ContinuousNodeImpl;
import ie.nuig.ml.bayes.ProbabilityDistributedFunction;
import ie.nuig.ml.bayes.dynamicbayesiannetwork.DBNUtilities;
import ie.nuig.ml.bayes.dynamicbayesiannetwork.EvidenceReadFromCsv;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nazli on 22/06/17.
 */
public class DynamicBayesianNetworkTest {

    double epsilon = 0.1;
    double a = 0.5;
    MockRandomizer mr;
    DBNUtilities dbnUtilities;
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

        dbnUtilities = new DBNUtilities();
        dbn = dbnUtilities.dbnGenerator();

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
        double result = dbnUtilities.deltaY1(epsilon, y1, y2);
        assertThat(result, is(11.583333333333332));
    }

    @Test
    public void test_deltaY1_must_return_Infifnity(){
        //Equation = (y2 - (Math.pow(y1, 3))/3 + y1)/epsilon)
        double epsilon = 0;
        double y1 = 0;
        double y2 = 0.7;
        double result = dbnUtilities.deltaY1(epsilon, y1, y2);
        assertThat(result, is(Math.abs(Double.POSITIVE_INFINITY)));
    }

    @Test
    public void test_deltaY2_must_calculate_equation(){
        //Equation = a - y1
        double a = 0.5;
        double y1 = 0.3;
        double result = dbnUtilities.deltaY2(a, y1);
        assertThat(result, is(a - y1));
    }

    @Test
    public void test_y1_1_must_calculate_equation(){
        //Equation = y1_1 = (deltay1_0 * h0) + y1_0
        double h0 = 1/120;
        double y1_0 = 1;
        double deltaY1_0 = 120;
        double result = dbnUtilities.y1_1(deltaY1_0, y1_0, h0);
        assertThat(result, is((deltaY1_0 * h0) + y1_0));
    }

    @Test
    public void test_y2_1_must_calculate_equation(){
        //Equation = y2_1 = (deltay2_0 * h0) + y2_0
        double h0 = 1/120;
        double y2_0 = 1;
        double deltaY2_0 = 120;
        double result = dbnUtilities.y1_1(deltaY2_0, y2_0, h0);
        assertThat(result, is((deltaY2_0 * h0) + y2_0));
    }

    @Test
    public void test_y21_and_y11_must_calculate_equation(){
        //Equation = var0 + deltaStep * delta0
        double var0 = 0.6;
        double deltaStep = 0.5;
        double delta0 = 0.7;
        double result = dbnUtilities.Y2_1andY1_1(delta0, var0, deltaStep);
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
        double stepSize = 1.0/164;
        int timeStep = 10;
        //int timeStep = (int)(10/stepSize);
        double[] y1 = new double[timeStep + 1];
        double[] y2 = new double[timeStep + 1];
        double[] sampledY1 = new double[timeStep];
        double[] sampledY2 = new double[timeStep];
        int N = 1000;
        double deltaY1;
        double deltaY2;
        double sum = 0;
        List<Double> time = new ArrayList<>(Arrays.asList(0.4, 1.2, 2.0, 2.7, 3.1, 4.6, 5.2, 6.1, 7.8, 8.2, 9.5));

        y1[0] = 1.0;
        y2[0] = 1.0;
        ParticleFiltering pf = new ParticleFiltering(N, dbn, mr);
        AssignmentProposition[] evidenceY1;
        AssignmentProposition[] evidenceY2;
        AssignmentProposition[][] S;
        int i = 0;
        do{
            deltaY1 = dbnUtilities.deltaY1(epsilon, y1[i], y2[i]);
            deltaY2 = dbnUtilities.deltaY2(a, y1[i]);
            evidenceY1 = new AssignmentProposition[]{new AssignmentProposition(observedy1_t1_RV, y1[i])};
            evidenceY2 = new AssignmentProposition[]{new AssignmentProposition(y2_t1_RV, y2[i])};

            //y1 calculation
            if(i == 0){
                S = pf.particleFiltering(evidenceY1, "First time step");
            }else {
                S = pf.particleFiltering(evidenceY1, sampledY1[i - 1]);
            }
            for(int j = 0; j < S.length; j++){
                sum += (double) S[j][5].getValue();
            }
            sampledY1[i] = sum/S.length;
            sum = 0;

            //y2 calculation
            if(i == 0){
                S = pf.particleFiltering(evidenceY2, "First time step");
            }else {
                S = pf.particleFiltering(evidenceY2, sampledY2[i - 1]);
            }
            for(int j = 0; j < S.length; j++){
                sum += (double) S[j][4].getValue();
            }
            sampledY2[i] = sum/S.length;
            sum = 0;

            i++;
            y1[i] = dbnUtilities.y1_1(deltaY1, y1[i - 1], stepSize);
            y2[i] = dbnUtilities.y2_1(deltaY2, y2[i - 1], stepSize);

        }while (i < timeStep);
        System.out.println("Last sample for y1 is: " + sampledY1[sampledY1.length - 1]);
        System.out.println("Last sample for y2 is: " + sampledY2[sampledY2.length - 1]);
    }

    @Test
    public void test_evidenceReadFromCsv() throws IOException{
        EvidenceReadFromCsv evidenceReadFromCsv = dbnUtilities.readFromCsv();
        System.out.println("Times:" + evidenceReadFromCsv.getTime());
        System.out.println("Values:" + evidenceReadFromCsv.getValues());
    }

    @After
    public void tearDown(){

    }
}
