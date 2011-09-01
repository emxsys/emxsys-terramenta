package gov.nasa.worldwindx.sunlight;

import com.sun.opengl.util.BufferUtil;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.terrain.*;
import gov.nasa.worldwind.util.Logging;

import javax.media.opengl.GL;
import java.awt.*;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @author heidtmare
 */
public class DayNightRectangularTessellator extends RectangularTessellator {

    private Vec4 lightDirection;
    private Material material = new Material(Color.WHITE);
    private Color lightColor = Color.WHITE;
    private Color ambientColor = new Color(.1f, .1f, .1f);

    protected static class NormalRenderInfo extends RectangularTessellator.RenderInfo {

        protected final DoubleBuffer normals;

        protected NormalRenderInfo(DrawContext dc, int density, FloatBuffer vertices, Vec4 refCenter, DoubleBuffer normals) {
            super(dc, density, vertices, refCenter);
            this.normals = normals;
        }

        public DoubleBuffer getNormals() {
            return normals;
        }

        public DoubleBuffer setNormals() {
            return normals;
        }
    }

    protected static class NormalRectTile extends RectangularTessellator.RectTile {

        public NormalRectTile(RectangularTessellator arg0, Extent arg1, int arg2, int arg3, Sector arg4, double arg5) {
            super(arg0, arg1, arg2, arg3, arg4, arg5);
        }

        public void setRI(RenderInfo ri) {
            this.ri = ri;
        }
    }

    /**
     * 
     * @return
     */
    public Vec4 getLightDirection() {
        return this.lightDirection;
    }

    /**
     * 
     * @param direction
     */
    public void setLightDirection(Vec4 direction) {
        this.lightDirection = direction;
    }

    /**
     * 
     * @return
     */
    public Color getLightColor() {
        return this.lightColor;
    }

    /**
     * 
     * @param color
     */
    public void setLightColor(Color color) {
        if (color == null) {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.lightColor = color;
    }

    /**
     * 
     * @return
     */
    public Color getAmbientColor() {
        return this.ambientColor;
    }

    /**
     * 
     * @param color
     */
    public void setAmbientColor(Color color) {
        if (color == null) {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        this.ambientColor = color;
    }

    /**
     * 
     * @param dc
     */
    @Override
    public void beginRendering(DrawContext dc) {
        // Tiles don't push their reference center, they set it, so push the reference center once here so it can be
        // restored later, in endRendering.
        dc.getView().pushReferenceCenter(dc, Vec4.ZERO);

        if (!dc.isPickingMode() && this.lightDirection != null) {
            GL gl = dc.getGL();
            gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_CURRENT_BIT | GL.GL_LIGHTING_BIT);

            this.material.apply(gl, GL.GL_FRONT);
            gl.glDisable(GL.GL_COLOR_MATERIAL);

            float[] lightPosition = {(float) -lightDirection.x, (float) -lightDirection.y, (float) -lightDirection.z, 0.0f};
            float[] lightDiffuse = new float[4];
            float[] lightAmbient = new float[4];
            lightColor.getRGBComponents(lightDiffuse);
            ambientColor.getRGBComponents(lightAmbient);

            gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPosition, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse, 0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient, 0);

            gl.glDisable(GL.GL_LIGHT0);
            gl.glEnable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHTING);
        }

        dc.getGL().glPushClientAttrib(GL.GL_CLIENT_VERTEX_ARRAY_BIT);
        dc.getGL().glEnableClientState(GL.GL_VERTEX_ARRAY);
    }

    @Override
    protected long render(DrawContext dc, RectTile rt, int i) {
        GL gl = dc.getGL();
        gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
        //gl.glNormalPointer(GL.GL_DOUBLE, 0, rt.getRi().getNormals().rewind());
        gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
        gl.glPopClientAttrib();
        return super.render(dc, rt, i);
    }

    /**
     * 
     * @param dc
     */
    @Override
    public void endRendering(DrawContext dc) {
        dc.getGL().glPopClientAttrib();

        if (!dc.isPickingMode() && this.lightDirection != null) {
            GL gl = dc.getGL();
            gl.glDisable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHT0);
            gl.glDisable(GL.GL_LIGHTING);
            gl.glPopAttrib();
        }

        dc.getView().popReferenceCenter(dc);
    }

    public boolean buildVerts(DrawContext dc, RectTile tile, boolean makeSkirts) {
        int density = tile.getDensity();
        int side = density + 3;
        int numVertices = side * side;
        java.nio.FloatBuffer verts = BufferUtil.newFloatBuffer(numVertices * 3);
        ArrayList<LatLon> latlons = this.computeLocations(tile);
        double[] elevations = new double[latlons.size()];
        dc.getGlobe().getElevations(tile.getSector(), latlons, tile.getResolution(), elevations);

        Globe globe = dc.getGlobe();

        Angle dLat = tile.getSector().getDeltaLat().divide(density);
        Angle latMin = tile.getSector().getMinLatitude();
        Angle latMax = tile.getSector().getMaxLatitude();

        Angle dLon = tile.getSector().getDeltaLon().divide(density);
        Angle lonMin = tile.getSector().getMinLongitude();
        Angle lonMax = tile.getSector().getMaxLongitude();

        Angle lat, lon;
        int iv = 0;
        double elevation, verticalExaggeration = dc.getVerticalExaggeration();
        Vec4 p;

        LatLon centroid = tile.getSector().getCentroid();
        Vec4 refCenter = globe.computePointFromPosition(centroid.getLatitude(), centroid.getLongitude(), 0d);

        int ie = 0;
        Iterator<LatLon> latLonIter = latlons.iterator();

        // Compute verts without skirts
        for (int j = 0; j < side; j++) {
            for (int i = 0; i < side; i++) {
                LatLon latlon = latLonIter.next();
                elevation = verticalExaggeration * elevations[ie++];
                p = globe.computePointFromPosition(latlon.getLatitude(), latlon.getLongitude(), elevation);
                verts.put(iv++, (float) (p.x - refCenter.x)).put(iv++, (float) (p.y - refCenter.y)).put(iv++, (float) (p.z - refCenter.z));
            }
        }

        // Compute indices and normals
        java.nio.IntBuffer indices = getIndices(density);
        java.nio.DoubleBuffer norms = getNormals(density, verts, indices, refCenter);

        // Fold down the sides as skirts
        double exaggeratedMinElevation = makeSkirts ? Math.abs(globe.getMinElevation() * verticalExaggeration) : 0;
        lat = latMin;
        for (int j = 0; j < side; j++) {
            //min longitude side
            ie = j * side + 1;
            elevation = verticalExaggeration * elevations[ie];
            elevation -= exaggeratedMinElevation >= 0 ? exaggeratedMinElevation : -exaggeratedMinElevation;
            p = globe.computePointFromPosition(lat, lonMin, elevation);
            iv = (j * side) * 3;
            verts.put(iv++, (float) (p.x - refCenter.x)).put(iv++, (float) (p.y - refCenter.y)).put(iv, (float) (p.z - refCenter.z));

            //max longitude side
            ie += side - 2;
            elevation = verticalExaggeration * elevations[ie];
            elevation -= exaggeratedMinElevation >= 0 ? exaggeratedMinElevation : -exaggeratedMinElevation;
            p = globe.computePointFromPosition(lat, lonMax, elevation);
            iv = ((j + 1) * side - 1) * 3;
            verts.put(iv++, (float) (p.x - refCenter.x)).put(iv++, (float) (p.y - refCenter.y)).put(iv, (float) (p.z - refCenter.z));

            if (j > density) {
                lat = latMax;
            } else if (j != 0) {
                lat = lat.add(dLat);
            }
        }

        lon = lonMin;
        for (int i = 0; i < side; i++) {
            //min latitude side
            ie = i + side;
            elevation = verticalExaggeration * elevations[ie];
            elevation -= exaggeratedMinElevation >= 0 ? exaggeratedMinElevation : -exaggeratedMinElevation;
            p = globe.computePointFromPosition(latMin, lon, elevation);
            iv = i * 3;
            verts.put(iv++, (float) (p.x - refCenter.x)).put(iv++, (float) (p.y - refCenter.y)).put(iv, (float) (p.z - refCenter.z));

            //max latitude side
            ie += (side - 2) * side;
            elevation = verticalExaggeration * elevations[ie];
            elevation -= exaggeratedMinElevation >= 0 ? exaggeratedMinElevation : -exaggeratedMinElevation;
            p = globe.computePointFromPosition(latMax, lon, elevation);
            iv = (side * (side - 1) + i) * 3;
            verts.put(iv++, (float) (p.x - refCenter.x)).put(iv++, (float) (p.y - refCenter.y)).put(iv, (float) (p.z - refCenter.z));

            if (i > density) {
                lon = lonMax;
            } else if (i != 0) {
                lon = lon.add(dLon);
            }
        }
        //tile.getRi().setRI(new NormalRenderInfo(dc, density, verts, refCenter, norms));
        return true;

    }

    protected static IntBuffer getIndices(int density) {
        if (density < 1) {
            density = 1;
        }

        // return a pre-computed buffer if possible.
        java.nio.IntBuffer buffer = indexLists.get(density);
        if (buffer != null) {
            return buffer;
        }

        int sideSize = density + 2;

        int indexCount = 2 * sideSize * sideSize + 4 * sideSize - 2;
        buffer = BufferUtil.newIntBuffer(indexCount);
        int k = 0;
        for (int i = 0; i < sideSize; i++) {
            buffer.put(k);
            if (i > 0) {
                buffer.put(++k);
                buffer.put(k);
            }

            if (i % 2 == 0) // even
            {
                buffer.put(++k);
                for (int j = 0; j < sideSize; j++) {
                    k += sideSize;
                    buffer.put(k);
                    buffer.put(++k);
                }
            } else // odd
            {
                buffer.put(--k);
                for (int j = 0; j < sideSize; j++) {
                    k -= sideSize;
                    buffer.put(k);
                    buffer.put(--k);
                }
            }
        }

        indexLists.put(density, buffer);

        return buffer;
    }

    protected static java.nio.DoubleBuffer getNormals(int density, FloatBuffer vertices, java.nio.IntBuffer indices, Vec4 referenceCenter) {
        int side = density + 3;
        int numVertices = side * side;
        int numFaces = indices.limit() - 2;
        double centerX = referenceCenter.x;
        double centerY = referenceCenter.y;
        double centerZ = referenceCenter.z;
        // Create normal buffer
        java.nio.DoubleBuffer normals = BufferUtil.newDoubleBuffer(numVertices * 3);
        int[] counts = new int[numVertices];
        Vec4[] norms = new Vec4[numVertices];
        for (int i = 0; i < numVertices; i++) {
            norms[i] = new Vec4(0d);
        }

        for (int i = 0; i < numFaces; i++) {
            //get vertex indices
            int index0 = indices.get(i);
            int index1 = indices.get(i + 1);
            int index2 = indices.get(i + 2);

            //get verts involved in current face
            Vec4 v0 = new Vec4(vertices.get(index0 * 3) + centerX, vertices.get(index0 * 3 + 1)
                    + centerY, vertices.get(index0 * 3 + 2) + centerZ);

            Vec4 v1 = new Vec4(vertices.get(index1 * 3) + centerX, vertices.get(index1 * 3 + 1)
                    + centerY, vertices.get(index1 * 3 + 2) + centerZ);

            Vec4 v2 = new Vec4(vertices.get(index2 * 3) + centerX, vertices.get(index2 * 3 + 1)
                    + centerY, vertices.get(index2 * 3 + 2) + centerZ);

            // get triangle edge vectors and plane normal
            Vec4 e1 = v1.subtract3(v0), e2;
            if (i % 2 == 0) {
                e2 = v2.subtract3(v0);
            } else {
                e2 = v0.subtract3(v2);
            }
            Vec4 N = e1.cross3(e2).normalize3(); // if N is 0, the triangle is degenerate

            if (N.getLength3() > 0) {
                // Store the face's normal for each of the vertices that make up the face.
                norms[index0] = norms[index0].add3(N);
                norms[index1] = norms[index1].add3(N);
                norms[index2] = norms[index2].add3(N);

                //increment vertex normal counts
                counts[index0]++;
                counts[index1]++;
                counts[index2]++;
            }
        }

        // Now loop through each vertex, and average out all the normals stored.
        for (int i = 0; i < numVertices; i++) {
            if (counts[i] > 0) {
                norms[i] = norms[i].divide3(counts[i]).normalize3();
            }
            int index = i * 3;
            normals.put(index++, norms[i].x).put(index++, norms[i].y).put(
                    index, norms[i].z);
        }

        return normals;
    }
}
