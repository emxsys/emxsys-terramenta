/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.utilities;

import com.terramenta.globe.interfaces.Clusterable;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chris.heidt
 */
public class ClusterUtilities {

    /**
     *
     * @param beans
     * @param vectors
     * @return
     */
    public static ClusterTreeNode cluster(List<Clusterable> clstrbls) {
        int clusterableCount = clstrbls.size();

        int[] dMin = new int[clusterableCount];
        double[] cSize = new double[clusterableCount];
        List<List<ClusterTreeNode>> clusters = new ArrayList<List<ClusterTreeNode>>(clusterableCount);
        Matrix distMatrix = new Matrix(clusterableCount, clusterableCount);

        // Initialize distance matrix and vector of closest clusters
        for (int i = 0; i < clusterableCount; i++) {
            dMin[i] = 0;
            for (int j = 0; j < clusterableCount; j++) {
                if (i == j) {
                    distMatrix.set(i, j, Double.POSITIVE_INFINITY);
                } else {
                    Angle angulerDistance = Position.greatCircleDistance(LatLon.ZERO, LatLon.ZERO);
                    distMatrix.set(i, j, angulerDistance.radians);
                }

                if (distMatrix.get(i, dMin[i]) > distMatrix.get(i, j)) {
                    dMin[i] = j;
                }
            }
        }

        // create leaves of the tree
        for (int i = 0; i < clusterableCount; i++) {
            ArrayList<ClusterTreeNode> cluster = new ArrayList<ClusterTreeNode>();
            Clusterable clstrbl = clstrbls.get(i);
            cluster.add(new ClusterTreeNode(clstrbl, null, null, 0, clstrbl.getPosition()));
            clusters.add(cluster);
            cSize[i] = 1;
        }

        // Main loop
        ClusterTreeNode root = null;
        ClusterTreeNode c1Cluster;
        ClusterTreeNode c2Cluster;
        ClusterTreeNode newCluster;
        for (int p = 0; p < clusterableCount - 1; p++) {

            // find the closest pair of clusters
            int c1 = 0;
            for (int i = 0; i < clusterableCount; i++) {
                if (distMatrix.get(i, dMin[i]) < distMatrix.get(c1, dMin[c1])) {
                    c1 = i;
                }
            }
            int c2 = dMin[c1];

            // create node to store cluster info 
            c1Cluster = clusters.get(c1).get(0);
            c2Cluster = clusters.get(c2).get(0);
            //double[] newCentroid = calculateCentroid(c1Cluster.size, c1Cluster.centroid, c2Cluster.size, c2Cluster.centroid);

            Sector boundingSector = Sector.boundingSector(c1Cluster.bean.getPosition(), c1Cluster.bean.getPosition());
            LatLon centroid = boundingSector.getCentroid();
            newCluster = new ClusterTreeNode(null, c1Cluster, c2Cluster, distMatrix.get(c1, c2), centroid);

            clusters.get(c1).add(0, newCluster);
            cSize[c1] += cSize[c2];

            // overwrite row c1 with respect to the linkage type
            for (int j = 0; j < clusterableCount; j++) {
                //SINGLE_LINKAGE
//                if (distMatrix.get(c1,j) > distMatrix.get(c2,j)) {
//                    distMatrix.get[j][c1] = distMatrix[c1][j] = distMatrix[c2][j];
//                }
                //COMPLETE_LINKAGE
                if (distMatrix.get(c1, j) < distMatrix.get(c2, j)) {
                    distMatrix.set(c1, j, distMatrix.get(c2, j));
                    distMatrix.set(j, c1, distMatrix.get(c2, j));
                }
                //AVERAGE_LINKAGE
//              var avg = (cSize[c1] * distMatrix[c1][j] + cSize[c2] * distMatrix[c2][j]) / (cSize[c1] + cSize[j]) 
//              distMatrix[j][c1] = distMatrix[c1][j] = avg;

            }
            distMatrix.set(c1, c1, Double.POSITIVE_INFINITY);

            // infinity ­out old row c2 and column c2
            for (int i = 0; i < clusterableCount; i++) {
                distMatrix.set(c2, i, Double.POSITIVE_INFINITY);
                distMatrix.set(i, c2, Double.POSITIVE_INFINITY);
            }

            // update dmin and replace ones that previous pointed to c2 to point to c1
            for (int j = 0; j < clusterableCount; j++) {
                if (dMin[j] == c2) {
                    dMin[j] = c1;
                }
                if (distMatrix.get(c1, j) < distMatrix.get(c1, dMin[c1])) {
                    dMin[c1] = j;
                }
            }

            // keep track of the last added cluster
            root = newCluster;
        }

        return root;
    }

    public static class ClusterTreeNode {

        private final Clusterable bean;
        private final ClusterTreeNode left;
        private final ClusterTreeNode right;
        private final double dist;
        private final int size;
        private final int depth;
        private final LatLon centroid;

        public ClusterTreeNode(Clusterable clstrbl, ClusterTreeNode left, ClusterTreeNode right, double dist, LatLon centroid) {
            this.bean = clstrbl;
            this.left = left;
            this.right = right;
            this.dist = dist;
            this.centroid = centroid;
            if (left == null && right == null) {
                this.size = 1;
                this.depth = 0;
            } else {
                this.size = left.size + right.size;
                this.depth = 1 + Math.max(left.depth, right.depth);
            }
        }

        public Clusterable getBean() {
            return bean;
        }

        public ClusterTreeNode getLeft() {
            return left;
        }

        public ClusterTreeNode getRight() {
            return right;
        }

        public double getDist() {
            return dist;
        }

        public int getSize() {
            return size;
        }

        public int getDepth() {
            return depth;
        }

        public LatLon getCentroid() {
            return centroid;
        }
    }
}
