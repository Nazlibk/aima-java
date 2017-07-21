package aima.test.core.bayes.dynamicbayesiannetwork;

import aima.core.probability.bayes.DynamicBayesianNetwork;
import aima.core.probability.bayes.approx.ParticleFiltering;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.util.RandVar;
import aima.core.util.MockRandomizer;
import ie.nuig.ml.bayes.ContinuousNodeImpl;
import ie.nuig.ml.bayes.ProbabilityDistributedFunction;
import ie.nuig.ml.bayes.dynamicbayesiannetwork.DBNUtilities;
import ie.nuig.ml.bayes.dynamicbayesiannetwork.CsvEvidenceReader;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by nazli on 22/06/17.
 */
public class DynamicBayesianNetworkTest {

    double epsilon = 0.1;
    double a = 0.5;
    int N = 10;
    double stepSize = 1.0/32.0;
    int timeStep = (int)(10/stepSize);
    MockRandomizer mr;
    DBNUtilities dbnUtilities = new DBNUtilities();
    private DynamicBayesianNetwork dbn;

    RealDistribution g = new NormalDistribution();


    final RandVar a_t0_RV = new RandVar("A_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(a)));
    final RandVar a_t1_RV = new RandVar("A_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(a)));
    final RandVar epsilon_t0_RV = new RandVar("Epsilon_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(epsilon)));
    final RandVar epsilon_t1_RV = new RandVar("Epsilon_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(epsilon)));
    final RandVar y1_t0_RV = new RandVar("Y1_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(1.0)));
    final RandVar y1_t1_RV = new RandVar("Y1_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(dbnUtilities.y((5.0/3.0)/epsilon, 1, stepSize))));
    final RandVar y2_t0_RV = new RandVar("Y2_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(1.0)));
    final RandVar y2_t1_RV = new RandVar("Y2_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(dbnUtilities.y(0.5, 1, stepSize))));
    final RandVar deltay1_t1_RV = new RandVar("Deltay1_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(dbnUtilities.deltaY1(epsilon, 1.0, 1.0))));
    final RandVar deltay1_t0_RV = new RandVar("Deltay1_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(dbnUtilities.deltaY1(epsilon, 1.0, 1.0))));
    final RandVar deltay2_t1_RV = new RandVar("Deltay2_t1_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(dbnUtilities.deltaY2(a, 1.0))));
    final RandVar deltay2_t0_RV = new RandVar("Deltay2_t0_RV", new ProbabilityDistributedFunction(new ConstantRealDistribution(dbnUtilities.deltaY2(a, 1.0))));
    final RandVar observedy1_t1_RV = new RandVar("Observedy1_t1_RV", new ProbabilityDistributedFunction(new NormalDistribution()));

    final Random rn = new Random(432445454);
    @Before
    public void setup(){

        dbn = dbnUtilities.dbnGenerator();

        //randomizer

        int numberOfNoes = dbn.getX_1().size() + dbn.getX_0().size() + dbn.getX_11().size();
        double[] randomNumbers = new double[numberOfNoes * N * timeStep];
        final RealDistribution uniform = new UniformRealDistribution(0, 1);
        for(int i = 0; i < randomNumbers.length; i++){
            randomNumbers[i] = uniform.sample();
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
        //Equation = y1 = (deltay1_0 * h0) + y1_0
        double h0 = 1/120;
        double y1_0 = 1;
        double deltaY1_0 = 120;
        double result = dbnUtilities.y(deltaY1_0, y1_0, h0);
        assertThat(result, is((deltaY1_0 * h0) + y1_0));
    }

    @Test
    public void test_y2_1_must_calculate_equation(){
        //Equation = y2 = (deltay2_0 * h0) + y2_0
        double h0 = 1/120;
        double y2_0 = 1;
        double deltaY2_0 = 120;
        double result = dbnUtilities.y(deltaY2_0, y2_0, h0);
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
    public void test_vanderpol_dbn() throws IOException{

        double[] y1 = new double[timeStep + 1];
        double[] y2 = new double[timeStep + 1];
        double[] sampledY1 = new double[timeStep + 1];
        double[] sampledY2 = new double[timeStep + 1];
        double deltaY1;
        double deltaY2;
        double sum = 0;

        y1[0] = 1.0;
        y2[0] = 1.0;
        sampledY1[0] = y1[0];
        sampledY2[0] = y2[0];
        ParticleFiltering pf = new ParticleFiltering(N, dbn, mr);
        AssignmentProposition[] evidenceY1;
        AssignmentProposition[] evidenceY2;
        AssignmentProposition[][] S;
        int i = 0;
        do{
            deltaY1 = dbnUtilities.deltaY1(epsilon, y1[i], y2[i]);
            ((ContinuousNodeImpl)dbn.getNode(deltay1_t1_RV)).setValue(deltaY1);
            deltaY2 = dbnUtilities.deltaY2(a, y1[i]);
            ((ContinuousNodeImpl)dbn.getNode(deltay2_t1_RV)).setValue(deltaY1);
            evidenceY1 = new AssignmentProposition[]{new AssignmentProposition(observedy1_t1_RV, y1[i])};
            evidenceY2 = new AssignmentProposition[]{new AssignmentProposition(y2_t1_RV, y2[i])};

            //y1 calculation
            S = pf.particleFiltering(evidenceY1, sampledY1[i]);
            for(int j = 0; j < S.length; j++){
                sum += (double) S[j][5].getValue();
            }
            sampledY1[i + 1] = sum/S.length;
            sum = 0;

            //y2 calculation
//            S = pf.particleFiltering(evidenceY2, sampledY2[i - 1]);
//            for(int j = 0; j < S.length; j++){
//                sum += (double) S[j][4].getValue();
//            }
//            sampledY2[i] = sum/S.length;
//            sum = 0;

            i++;
            y1[i] = dbnUtilities.y(deltaY1, y1[i - 1], stepSize);
            ((ContinuousNodeImpl)dbn.getNode(y1_t1_RV)).setValue(y1[i]);
            ((ContinuousNodeImpl)dbn.getNode(y1_t0_RV)).setValue(y1[i - 1]);
            ((ContinuousNodeImpl)dbn.getNode(observedy1_t1_RV)).getDomain();
            y2[i] = dbnUtilities.y(deltaY2, y2[i - 1], stepSize);
            ((ContinuousNodeImpl)dbn.getNode(y2_t1_RV)).setValue(y2[i]);
            ((ContinuousNodeImpl)dbn.getNode(y2_t0_RV)).setValue(y2[i - 1]);

        }while (i < timeStep);
        System.out.println("Last sample for y1 is: " + sampledY1[sampledY1.length - 1]);
        System.out.println("Last sample for y2 is: " + sampledY2[sampledY2.length - 1]);

        String outputFilename = "y1Output.csv";
        Path path = Paths.get(outputFilename);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (int j = 0; j < sampledY1.length; j++) {
                String out = j + "," + sampledY1[j] + "\n";
                writer.write(out);
            }
        }

        outputFilename = "y1.csv";
        path = Paths.get(outputFilename);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (int j = 0; j < y1.length; j++) {
                String out = j + "," + y1[j] + "\n";
                writer.write(out);
            }
        }
    }

    @Test
    public void test_evidenceReadFromCsv() throws IOException{
        CsvEvidenceReader csvEvidenceReader = dbnUtilities.readFromCsv();
        //System.out.println("Times:" + csvEvidenceReader.getTime());
        //System.out.println("Values:" + csvEvidenceReader.getValues());
    }

    @After
    public void tearDown(){

    }
}
