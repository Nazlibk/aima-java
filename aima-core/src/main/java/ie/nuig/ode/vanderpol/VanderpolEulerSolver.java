package ie.nuig.ode.vanderpol;

/**
 * Created by nazli on 05/07/17.
 */
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VanderpolEulerSolver {


    private static final String outputFilename = "output.csv";
    private static final double step = 0.0001;
    private static final int iteration = 100_000;

    public static void main(String... args) throws IOException {

        Path path = Paths.get(outputFilename);


        EulerIntegrator integrator = new EulerIntegrator(step);

        double y0[] = new double[]{1.0,1.0};
        Vanderpol ode = new Vanderpol();
        ode.setInitialCondition(y0);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            double t = 0.0;
            double y[] = new double[y0.length];
            for (int i = 0; i < iteration; i++) {
                integrator.integrate(ode, 0.0, y0, step, y);
                //ode.setEpsilon(....);
                //ode.setA(....);
                y0[0] = y[0];
                y0[1] = y[1];
                String out = t + "," + y[0] + "," + y[1] + "\n";
                writer.write(out);
                System.out.print(out);
                t += step;
            }
        }
    }


}
