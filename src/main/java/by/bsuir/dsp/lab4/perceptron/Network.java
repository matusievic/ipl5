package by.bsuir.dsp.lab4.perceptron;

import by.bsuir.dsp.lab4.array.ArrayService;

import java.util.Arrays;
import java.util.Random;

import static by.bsuir.dsp.lab4.array.ArrayService.toBipolar;

public class Network {
    private int n;
    private int h;

    private double[] entrance;
    private double[] hidden;
    private double[] output;

    private double[][] center;
    private double[] sigma;
    private double[][] ho;

    public Network(int n, int h) {
        this.n = n;
        this.h = h;

        this.entrance = new double[n];
        this.hidden = new double[h];
        this.output = new double[h];

        this.center = new double[h][n];
        this.sigma = new double[h];

        this.ho = new double[h][h];

        init();
    }

    private void init() {
        Random r = new Random();
        for (int j = 0; j < h; j++) {
            for (int k = 0; k < h; k++) {
                ho[j][k] = r.nextDouble() * (r.nextBoolean() ? 1 : -1);
            }
        }
    }

    public int learn(int[][] bitmap, double[] result, double a, double maxError) {
        int[] input = toBipolar(bitmap);
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 1.0) {
                double[] center = this.center[i];
                for (int j = 0; j < center.length; j++) {
                    center[j] = input[j];
                }
            }
        }

        for (int i = 0; i < h; i++) {
            final int ind = i;
            double[][] others = Arrays.stream(this.center)
                                      .filter(arr -> !Arrays.equals(arr, this.center[ind]))
                                      .toArray(double[][]::new);
            sigma[i] = ArrayService.minDistance(this.center[i], others) / 2;
        }

        int iterCount = 0;
        double error;
        do {
            error = 0;
            this.supply(input);
            for (int k = 0; k < h; k++) {
                double dk = result[k] - output[k];
                if (Math.abs(dk) > maxError) {
                    error = Math.abs(dk);
                }
                for (int j = 0; j < h; j++) {
                    ho[j][k] += a * dk * hidden[j];
                }
            }
            iterCount++;
        } while (error > maxError);
        return iterCount;
    }

    private void supply(int[] input) {
        for (int i = 0; i < n; i++) {
            entrance[i] = input[i];
        }
        calculate();
    }

    private void calculate() {
        for (int j = 0; j < h; j++) {
            double distance = ArrayService.distance(entrance, center[j]);
            hidden[j] = Math.exp(-distance / Math.pow(sigma[j], 2));
        }

        for (int k = 0; k < h; k++) {
            output[k] = 0;
            for (int j = 0; j < h; j++) {
                output[k] += ho[j][k] * hidden[j];
            }
        }
    }

    public int recognize(int[][] bitmap) {
        int[] input = toBipolar(bitmap);
        this.supply(input);
        int max = 0;
        for (int i = 1; i < h; i++) {
            if (output[i] > output[max]) {
                max = i;
            }
        }
        return max;
    }

    public double[] getHidden() {
        return hidden;
    }

    public double[] getOutput() {
        return output;
    }

    public double[][] getHO() {
        return ho;
    }
}
