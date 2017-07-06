package ie.nuig.ml.bayes.particlefilter;

/**
 * Created by nazli on 09/06/17.
 */
class ToleranceCheckResult {

    boolean toleranceExceeded;
    double newStepSize;

    public ToleranceCheckResult(boolean toleranceExceeded, double newStepSize) {
        this.toleranceExceeded = toleranceExceeded;
        this.newStepSize = newStepSize;
    }

    public boolean isToleranceExceeded() {
        return toleranceExceeded;
    }

    public void setToleranceExceeded(boolean toleranceExceeded) {
        this.toleranceExceeded = toleranceExceeded;
    }

    public double getNewStepSize() {
        return newStepSize;
    }

    public void setNewStepSize(double newStepSize) {
        this.newStepSize = newStepSize;
    }
}
