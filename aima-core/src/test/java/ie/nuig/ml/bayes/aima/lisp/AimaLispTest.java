package ie.nuig.ml.bayes.aima.lisp;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.armedbear.lisp.Interpreter;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by nazli on 24/06/17.
 */
public class AimaLispTest {

    final String rootPath = AimaLispTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    Interpreter interpreter;

    @Before
    public void setUp(){
        interpreter = Interpreter.createInstance();
        System.out.println(rootPath);
    }

    @Test
    public void test_load_lisp_aima() {
        String s = "(load \"file:" + rootPath + "aima/defpackage.lisp\")";
        interpreter.eval(s);
        s = "(in-package :aima)";
        interpreter.eval(s);
        s = "(load \"file:" + rootPath + "aima/probability/domains/edit-nets.lisp\")";
        interpreter.eval(s);
        s = "(load \"file:" + rootPath + "aima/aima.lisp\")";
        interpreter.eval(s);
        s = "(aima-load-binary 'all)";
        interpreter.eval(s);
        s = "(load \"file:" + rootPath + "aima/Functions.lisp\")";
        interpreter.eval(s);
        s = "(load \"file:" + rootPath + "aima/probability/algorithms/dbn-adaptive-inference.lisp\")";
        interpreter.eval(s);
        s = "(load \"file:" + rootPath + "aima/DBNCInterface/run-inference.lisp\")";
        interpreter.eval(s);
        //s = "(load \"file:" + rootPath + "aima/defpackage.lisp\")";
        //interpreter.eval(s);
        s = "(in-package :aima)";
        interpreter.eval(s);
        //s = "(load \"file:" + rootPath + "aima/probability/domains/edit-nets.lisp\")";
        //interpreter.eval(s);
        s = "(aima-load-binary 'all)";
        interpreter.eval(s);
        s = "(load \"file:" + rootPath + "aima/probability/algorithms/dbn-adaptive-inference.lisp\")";
        interpreter.eval(s);

        NormalDistribution normalDistribution = new NormalDistribution(0.07, 0.01);
        for(int i = 0; i < 100;i++)
            System.out.println(normalDistribution.sample());
/*
        s = "(random-from-normal 0.07 0.01)";
        LispObject result = interpreter.eval(s);
        System.out.println("  >>>>  "+((DoubleFloat) result).value);
        System.out.println(result.getClass().getName());
*/

       //s = "(initialize-Phi-table 10 100)";
        //LispObject eval = interpreter.eval(s);
        //System.out.println(eval);
        //TrapezoidIntegrator trapezoidIntegrator = new TrapezoidIntegrator(100, 500);
        //trapezoidIntegrator.
    }
}
