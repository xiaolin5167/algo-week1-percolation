/**
 * Compilation:  javac Percolation.java
 * Execution:    java Percolation and enter the number of n and of trials
 * @description: Analysis of running time and memory usage
 * @author: Xiaolin LU
 * @create: 2018-10-29 18:45
 **/

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private static final double CONFIDENCE_95 = 1.96;
    private final int trials;
    private final double mean;
    private final double stddev;

    /**
     * Perform trials independent experiments on an n-by-n grid
     *
     * @param n
     * @param trials
     */
    public PercolationStats(int n, int trials) {
        if (n < 1)
            throw new IllegalArgumentException("To construct a grid, n must greater than 0");
        if (trials < 1)
            throw new IllegalArgumentException("This computation experiment times is at least 1");
        this.trials = trials;
        final double[] xt = new double[trials]; // The fraction of open sites in computational experiment t.

        for (int t = 0; t < trials; t++) {
            Percolation percolation = new Percolation(n);
            while (!percolation.percolates()) {
                int row = StdRandom.uniform(n) + 1;
                int col = StdRandom.uniform(n) + 1;
                percolation.open(row, col);
            }

            xt[t] = (double) percolation.numberOfOpenSites() / (double) (n * n);
        }

        mean = StdStats.mean(xt);
        stddev = StdStats.stddev(xt);

    }

    /**
     * Test client
     *
     * @param args
     */
    public static void main(String[] args) {
/*        int n = StdIn.readInt();
        int t = StdIn.readInt();*/
        int n = 5;
        int t = 10;
        PercolationStats ps = new PercolationStats(n, t);
        StdOut.println("% java-algs4 PercolationStats "+ n + " " + t);
        StdOut.printf("%-25s %s %f \n", "means", "=", ps.mean);
        StdOut.printf("%-25s %s %f \n", "stddev", "=", ps.stddev);
        StdOut.printf("%-25s %s%f%s%f%s\n", "95% confidence interval", "= [", ps.confidenceLo(), ", ",
                ps.confidenceHi(), "]");
    }

    /**
     * @return the sample mean of percolation threshold
     */
    public double mean() {
        return mean;
    }

    /**
     * @return the sample standard deviation of percolation threshold
     */
    public double stddev() {
        return stddev;
    }

    /**
     * @return the low  endpoint of 95% confidence interval
     */
    public double confidenceLo() {
        return mean - CONFIDENCE_95 * stddev / Math.sqrt(trials);
    }

    /**
     * @return the high endpoint of 95% confidence interval
     */
    public double confidenceHi() {
        return mean + CONFIDENCE_95 * stddev / Math.sqrt(trials);
    }


}
