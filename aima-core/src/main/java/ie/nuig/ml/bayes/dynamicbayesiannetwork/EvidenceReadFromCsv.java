package ie.nuig.ml.bayes.dynamicbayesiannetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nazli on 13/07/17.
 */
public class EvidenceReadFromCsv {
    List<Double> time = new ArrayList<>();
    List<Double> values =  new ArrayList<>();

    public void setTime(List<Double> time) {
        this.time = time;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }

    public List<Double> getTime() {

        return time;
    }

    public List<Double> getValues() {
        return values;
    }

    public EvidenceReadFromCsv(List<Double> time, List<Double> values){
        this.time = time;
        this.values = values;

    }
}
