package bayes.aima.lisp;

import org.junit.Before;
import org.junit.Test;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import java.util.StringJoiner;

/**
 * Created by nazli on 25/06/17.
 */
public class MathExpressionTest {


    @Before
    public void setUp(){

    }

    @Test
    public void mathExpressionTest(){
        Function function = new Function("f(x,y) = y ^ 2");
        Expression expression = new Expression("f(0.2,3.0)",function);
        System.out.println(expression.calculate());
        function = new Function("f(y1,y2,epsilon) = (y1 + (y1 ^ 3)/3 - y2)/epsilon" );
        double y1 = 0.7;//
        double y2 = 0.4;
        double e = 0.6;
        StringJoiner str = new StringJoiner(",","f(",")");
        str.add(String.valueOf(y1)).add(String.valueOf(y2)).add(String.valueOf(e));
        System.out.println("Expr : "+str.toString());
        expression = new Expression(str.toString(),function);
        System.out.println(expression.calculate());
    }



}
