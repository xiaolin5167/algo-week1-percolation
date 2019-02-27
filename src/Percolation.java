/**
 * Compilation:  javac Percolation.java
 * Local Test:
 * *1.Execution:    java Percolation ../test/input.txt
 * *2.Execution:    active fetchLocalFile() and execute java Percolation
 * Dependencies: Percolation.java
 * @program: Percolation
 * @description: A program to estimate the value of the percolation threshold via Monte Carlo simulation.
 * @author: Xiaolin LU
 * @create: 2018-10-25 18:03
 * @Notice: the row and column indices are integers between 1 and n, where (1, 1) is the upper-left site
 **/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class Percolation {

    private static final boolean BLOCKED = false;
    private static final boolean OPEN = true;

    private final int n; // number of row/column of grid
    private final WeightedQuickUnionUF vtTopUF; // UF with virtual top site
    private final WeightedQuickUnionUF vtBottomUF; // UF with virtual bottom site

    private boolean[][] grid;
    private int openSiteNumber = 0;
    private boolean isPercolate = false;

    /**
     * Create n-by-n grid, with all sites blocked
     * @param n number of the sites in the grid
     */
    public Percolation(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("Number of sites of grid must be greater than 0");

        this.n = n;
        vtTopUF = new WeightedQuickUnionUF(n * n + 1);
        vtBottomUF = new WeightedQuickUnionUF(n * n + 1);
        grid = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = BLOCKED;
            }
        }
    }

    /**
     * Return the index of UF correspond to the row and column from the grid given
     * @param row
     * @param col
     * @return the index{@code index}n
     */
    private int indexUF(int row, int col) {
        int index = n * (row - 1) + col - 1;
        return index;
    }

    private void validate(int row, int col) {
        if (row < 1 || row > n) {
            throw new IllegalArgumentException("the number of row : " + row + " is not between 1 and " + n);
        }

        if (col < 1 || col > n) {
            throw new IllegalArgumentException("the number of column : " + col + " is not between 1 and " + n);
        }
    }

    /**
     * open site (row, col) if it is not open already
     * @param row row of the site
     * @param col column of the site
     */
    public void open(int row, int col) {

        validate(row, col);

        if (isOpen(row, col))
            return;

        grid[row - 1][col - 1] = OPEN;
        openSiteNumber++;
        unionNeighboring(row, col);
        /* threshold p* about 0.592746 for large square lattices, if the open site less than n,
        it can skip to assign the value of isPercolate */
        if (!isPercolate && openSiteNumber >= n) {
            if (vtTopUF.connected(indexUF(row, col), n * n)
                    && vtBottomUF.connected(indexUF(row, col), n * n)) isPercolate = true;
        }

    }

    private void unionNeighboring(int row, int col) {
        // The grid with 1 site is always percolate if it is opened.
        if (1 == n) {
            isPercolate = true;
            vtTopUF.union(0, 1);
            vtBottomUF.union(0, 1);
        }
        int index = this.indexUF(row, col);
        boolean hasLeft = col == 1 ? false : true;
        boolean hasRight = col == n ? false : true;
        boolean hasUp = row == 1 ? false : true;
        boolean hasDown = row == n ? false : true;

        // connecting it to an open site of neighboring (left)
        if (hasLeft && isOpen(row, col - 1) && !vtTopUF.connected(index, index - 1)) {
            vtTopUF.union(index, index - 1);
            vtBottomUF.union(index, index - 1);
        }

        // connecting it to an open site of neighboring (right)
        if (hasRight && isOpen(row, col + 1) && !vtTopUF.connected(index, index + 1)) {
            vtTopUF.union(index, index + 1);
            vtBottomUF.union(index, index + 1);
        }
        // connecting it to an open site of neighboring (up)
        if (hasUp && isOpen(row - 1, col)) {
            int upIndex = this.indexUF(row - 1, col);
            if (!vtTopUF.connected(index, upIndex)) {
                vtTopUF.union(index, upIndex);
                vtBottomUF.union(index, upIndex);
            }
        }

        // connection of the top site with the virtual top site
        if (!hasUp)
            if (!vtTopUF.connected(index, n * n))
                vtTopUF.union(index, n * n);

        // connecting it to an open site of neighboring (down)
        if (hasDown && isOpen(row + 1, col)) {
            int downIndex = this.indexUF(row + 1, col);
            if (!vtTopUF.connected(index, downIndex)) {
                vtTopUF.union(index, downIndex);
                vtBottomUF.union(index, downIndex);
            }
        }

        // connection of the bottom site with the virtual bottom site
        if (!hasDown)
            if (!vtBottomUF.connected(index, n * n))
                vtBottomUF.union(index, n * n);

    }

    /**
     * Verify if a given site (row, col) is open.
     * @param row
     * @param col
     * @return {@code true} if the site is open;
     *         {@code false} otherwise
     */
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return grid[row - 1][col - 1];
    }

    /**
     * Verify if a given site (row, col) is full.
     * @param row
     * @param col
     * @return {@code true} if the site is full;
     *         {@code false} otherwise
     */
    public boolean isFull(int row, int col) {
        validate(row, col);
        if (vtTopUF.connected(indexUF(row, col), n * n))
            return true;
        return false;
    }

    /**
     *
     * @return the number of open sites
     */
    public int numberOfOpenSites() {
        return openSiteNumber;
    }

    /**
     * check if the system is percolate
     */
    public boolean percolates() {
        return isPercolate;
    }

    private void printInfo(int row, int col) {
        StdOut.println("open grid[" + row + "][" + col + "]");
        StdOut.println("grid [" + row + "][" + col + "] isOpen: " + isOpen(row, col)
                + "; isFull: " + isFull(row, col) + "; isPercolation: " + isPercolate);
    }

    /**
     * Unit tests the methods in this class
     * test client (optional)
     * @param args
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        String s = null;
        int n;

        while (in.hasNextLine()) {
            s = in.readLine();
            if (s != null && !s.trim().equals(""))
                break;
        }

        s = s.trim();
        n = Integer.parseInt(s);
        Percolation percolation = new Percolation(n);

        while (in.hasNextLine()) {
            s = in.readLine();
            if (s != null && !s.trim().equals("")) {
                s = s.trim();
                String[] a = s.split("\\s+");
                if (a.length != 2)
                    break;
                int row = Integer.parseInt(a[0]);
                int col = Integer.parseInt(a[1]);
                percolation.open(row, col);
                percolation.printInfo(row, col);
            }
        }
    }
}
