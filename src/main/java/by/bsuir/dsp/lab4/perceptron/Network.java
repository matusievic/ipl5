package by.bsuir.dsp.lab4.perceptron;

import by.bsuir.dsp.lab4.array.ArrayService;

import java.util.Arrays;
import java.util.Random;

import static by.bsuir.dsp.lab4.array.ArrayService.toBipolar;
import static java.lang.Math.exp;

public class Network {
    private int n;
    private int m;

    private double[] entrance;
    private double[] hidden;
    private double[] output;

    private double[][] center;
    private double[] sigma;
    private double[][] ho;

    public Network(int n, int m) {
        this.n = n;
        this.m = m;

        this.entrance = new double[n];
        this.hidden = new double[m];
        this.output = new double[m];

        this.center = new double[m][n];
        this.sigma = new double[m];

        this.ho = new double[m][m];

        init();
    }

    private void init() {
        Random r = new Random();
        for (int j = 0; j < m; j++) {
            for (int k = 0; k < m; k++) {
                ho[j][k] = r.nextDouble() * (r.nextBoolean() ? 1 : -1);
            }
        }
    }

    public void learn(int[][] bitmap, double[] result, double a) {
        int[] input = toBipolar(bitmap);
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 1.0) {
                double[] center = this.center[i];
                for (int j = 0; j < center.length; j++) {
                    center[j] = (center[j] + input[j]) / 2;
                }
                final int ind = i;
                sigma[i] = ArrayService.minDistance(this.center[i], Arrays.stream(this.center)
                                                                          .filter(arr -> !Arrays.equals(arr, this.center[ind]))
                                                                          .toArray(double[][]::new)) / 2;
            }
        }

        for (int j = 0; j < m; j++) {
            for (int k = 0; k < m; k++) {
                double dk = result[k] - output[k];
                ho[j][k] += a * dk * hidden[j];
            }
        }
    }

    private void supply(int[] input) {
        for (int i = 0; i < n; i++) {
            entrance[i] = input[i];
        }
        calculate();
    }

    private void calculate() {
        for (int i = 0; i < m; i++) {
            double distance = ArrayService.distance(entrance, center[i]);
            hidden[i] = Math.exp(-distance / sigma[i]);
        }

        for (int k = 0; k < m; k++) {
            output[k] = 0;
            for (int j = 0; j < m; j++) {
                output[k] += ho[j][k] * hidden[k];
            }
            output[k] = activation(output[k]);
        }
    }

    private double activation(double value) {
        return (1.0 / (1.0 + exp(-value)));
    }

    public int recognize(int[][] bitmap) {
        int[] input = toBipolar(bitmap);
        supply(input);
        int max = 0;
        for (int i = 1; i < m; i++) {
            if (output[i] > output[max]) {
                max = i;
            }
        }
        return max;
    }

    public double[] getOutput() {
        return output;
    }
}
