package ie.nuig.ml.bayes.particlefilter;

import ie.nuig.ml.bayes.particlefilter.ParticleFilterUtils;
import ie.nuig.ml.bayes.particlefilter.ToleranceCheckResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


/**
 * Created by nazli on 09/06/17.
 */
public class ParticleFilterUtilsTest {

    ParticleFilterUtils particleFilterUtils;

    @Before
    public void setUp(){
        particleFilterUtils = new ParticleFilterUtils();
    }

    @Test
    public void test_check_tolerance_must_return_true(){
        //First result form check tolerance.txt
        Double[] particles = {1.0, 1.0, 0.0, 0.08128274289407607, 1.0, -1.0, 20.504556903328915, 21.504556903328915, 0.0, 0.0, 0.0889858238585264, 21.504556903328915, -21.50455690332891, -37010.32189183375};
        Integer[] nodes = {6, 5};//TODO: Investigate the order in this array
        double tolerance = 0.01;
        double stepSize = 1.0;
        ToleranceCheckResult result = particleFilterUtils.checkTolerance(Arrays.asList(particles), Arrays.asList(nodes), tolerance, stepSize);
        System.out.println(result.getNewStepSize());
        System.out.println(result.isToleranceExceeded());
        assertThat(result.isToleranceExceeded(),is(true));
        assertThat(result.getNewStepSize(),is(6.614176880813091E-4));//Lisp result: 6.614176631678055d-4
        System.out.println("Lisp and java difference: " + Math.abs(result.getNewStepSize() - 6.614176631678055E-4));
    }

    @Test
    public void test_check_tolerance_must_return_false(){
        //Second result form check tolerance.txt
        Double[] particles = {1.0, 1.0, 0.0, 0.08128274289407607, 1.0, -1.0, 20.504556903328915, 1.0135620761112911, 0.9993385823368321, 0.0, 0.08177774227747617, 1.0135620761112911, -1.0135620761112911, 20.370096327095478};
        Integer[] nodes = {6, 5};
        double tolerance = 0.01;
        double stepSize = 6.614176631678055E-4;
        ToleranceCheckResult result = particleFilterUtils.checkTolerance(Arrays.asList(particles), Arrays.asList(nodes), tolerance, stepSize);
        System.out.println(result.getNewStepSize());
        System.out.println(result.isToleranceExceeded());
        assertThat(result.isToleranceExceeded(),is(false));
        assertThat(result.getNewStepSize(),is(9.921264947517082E-4));//Lisp result: 9.921264947517082d-4
        System.out.println("Lisp and java difference: " + Math.abs(result.getNewStepSize() - 9.921264947517082E-4));
    }

    @Test
    public void test_GetNextT_must_return_sum_of_arguments(){
        double currentT = 2;
        double summaryInterval = 6;
        double result = particleFilterUtils.GetNextT(currentT, summaryInterval);
        assertThat(result, is(currentT + summaryInterval));
    }

    @Test
    public void test_setSliceEvidence_must_return_array_of_zero(){
        //regardless of currentT and finishTime values, setSliceEvidence return array of zeros (because of
        // some ifs in the code)
        int currentT = 0;
        int finishTime = 1;
        double[] result = particleFilterUtils.setSliceEvidence(currentT, finishTime);
        for(double r: result) {
            assertThat(r, is(0.0));
        }
    }

    @After
    public void tearDown(){

    }
}
