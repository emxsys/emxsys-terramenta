/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.globe.utilities;

/**
 *
 * @author chris.heidt
 */
public class Matrix {

    private final int M;             // number of rows
    private final int N;             // number of columns
    private final double[][] data;   // M-by-N array

    /**
     * create M-by-N matrix of 0's
     *
     * @param M
     * @param N
     */
    public Matrix(int M, int N) {
        this.M = M;
        this.N = N;
        data = new double[M][N];
    }

    /**
     * create matrix based on 2d array
     *
     * @param data
     */
    public Matrix(double[][] data) {
        M = data.length;
        N = data[0].length;
        this.data = new double[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                this.data[i][j] = data[i][j];
            }
        }
    }

    /**
     * copy constructor
     *
     * @param A
     */
    public Matrix(Matrix A) {
        this(A.data);
    }

    // create and return a random M-by-N matrix with values between 0 and 1
    /**
     *
     * @param M
     * @param N
     * @return
     */
    public static Matrix random(int M, int N) {
        Matrix A = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                A.data[i][j] = Math.random();
            }
        }
        return A;
    }

    // create and return the N-by-N identity matrix
    /**
     *
     * @param N
     * @return
     */
    public static Matrix identity(int N) {
        Matrix I = new Matrix(N, N);
        for (int i = 0; i < N; i++) {
            I.data[i][i] = 1;
        }
        return I;
    }

    /**
     *
     * @param M
     * @param N
     * @return
     */
    public double get(int M, int N) {
        return data[M][N];
    }

    /**
     *
     * @param M
     * @param N
     * @param value
     */
    public void set(int M, int N, double value) {
        data[M][N] = value;
    }

    /**
     * create and return the transpose of the invoking matrix
     *
     * @return
     */
    public Matrix transpose() {
        Matrix A = new Matrix(N, M);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                A.data[j][i] = this.data[i][j];
            }
        }
        return A;
    }

    /**
     *
     * @param B
     * @return C = A + B
     */
    public Matrix plus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                C.data[i][j] = A.data[i][j] + B.data[i][j];
            }
        }
        return C;
    }

    /**
     *
     * @param B
     * @return C = A - B
     */
    public Matrix minus(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                C.data[i][j] = A.data[i][j] - B.data[i][j];
            }
        }
        return C;
    }

    /**
     * does A = B exactly?
     *
     * @param B
     * @return
     */
    public boolean eq(Matrix B) {
        Matrix A = this;
        if (B.M != A.M || B.N != A.N) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (A.data[i][j] != B.data[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param B
     * @return C = A * B
     */
    public Matrix times(Matrix B) {
        Matrix A = this;
        if (A.N != B.M) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        Matrix C = new Matrix(A.M, B.N);
        for (int i = 0; i < C.M; i++) {
            for (int j = 0; j < C.N; j++) {
                for (int k = 0; k < A.N; k++) {
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
                }
            }
        }
        return C;
    }

    /**
     *
     * @param rhs
     * @return x = A^-1 b, assuming A is square and has full rank
     */
    public Matrix solve(Matrix rhs) {
        if (M != N || rhs.M != N || rhs.N != 1) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }

        // create copies of the data
        Matrix A = new Matrix(this);
        Matrix b = new Matrix(rhs);

        // Gaussian elimination with partial pivoting
        for (int i = 0; i < N; i++) {

            // find pivot row and swap
            int max = i;
            for (int j = i + 1; j < N; j++) {
                if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i])) {
                    max = j;
                }
            }
            A.swap(i, max);
            b.swap(i, max);

            // singular
            if (A.data[i][i] == 0.0) {
                throw new RuntimeException("Matrix is singular.");
            }

            // pivot within b
            for (int j = i + 1; j < N; j++) {
                b.data[j][0] -= b.data[i][0] * A.data[j][i] / A.data[i][i];
            }

            // pivot within A
            for (int j = i + 1; j < N; j++) {
                double m = A.data[j][i] / A.data[i][i];
                for (int k = i + 1; k < N; k++) {
                    A.data[j][k] -= A.data[i][k] * m;
                }
                A.data[j][i] = 0.0;
            }
        }

        // back substitution
        Matrix x = new Matrix(N, 1);
        for (int j = N - 1; j >= 0; j--) {
            double t = 0.0;
            for (int k = j + 1; k < N; k++) {
                t += A.data[j][k] * x.data[k][0];
            }
            x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
        }
        return x;

    }

    /**
     * swap rows i and j
     *
     * @param i
     * @param j
     */
    public void swap(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                sb.append(data[i][j]);
            }
            sb.append(Character.LINE_SEPARATOR);
        }
        return sb.toString();
    }
}
