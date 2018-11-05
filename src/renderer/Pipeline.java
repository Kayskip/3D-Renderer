package renderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import renderer.Scene.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * 
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Polygon poly) {
		Vector3D[] v = poly.getVertices();
		Vector3D normal = v[1].minus(v[0]).crossProduct(v[2].minus(v[1]));
		return (normal.z > 0);
	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 * 
	 * @param lightDirection
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColor
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	
	/* I had help with this method after calculating the cos */
	public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {
		int r = 255;
		int g = 255; 
		int b = 255;
		
		float red, green, blue;
		Vector3D e1 = poly.getVertices()[1].minus(poly.getVertices()[0]);
		Vector3D e2 = poly.getVertices()[2].minus(poly.getVertices()[1]);
		
		Vector3D normal = e1.crossProduct(e2).unitVector();
		float cos = normal.cosTheta(lightDirection);

		if (cos < 0) lightColor = new Color(0, 0, 0);
		
		red = ((
				((1/(float)255) * ambientLight.getRed()) + 
				(1/(float)255) * lightColor.getRed() * cos) * 
				(1/(float)255) * poly.getReflectance().getRed());
		
		
		if (red * 255 < 255) r = (int) (red * 255); 
		
		green = ((
				((1/(float)255) * ambientLight.getGreen()) +
				(1/(float)255) * lightColor.getGreen() * cos) * 
				(1/(float)255) * poly.getReflectance().getGreen());
		
		
		if (green * 255 < 255) g = (int) (green * 255); 
		
		blue = ((
				((1/(float)255) * ambientLight.getBlue()) +
				(1/(float)255) * lightColor.getBlue() * cos) * 
				(1/(float)255) * poly.getReflectance().getBlue());
		
		
		if (blue * 255 < 255) b = (int) (blue * 255); 
				
		return new Color(r, g, b);
	}

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
	 * 
	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {
		
		List<Polygon> rotation = new ArrayList<Polygon>(); 
		Vector3D rotatedLight = Transform.newYRotation(yRot).multiply(Transform.newXRotation(xRot).multiply(scene.getLight()));
		
		Vector3D[] vertices;
		for (Polygon p : scene.getPolygons()) {
		    vertices = new Vector3D[4];
			int i = 1;
			
			for (Vector3D point : p.getVertices()) {
				
				point = Transform.newYRotation(yRot).multiply(Transform.newXRotation(xRot).multiply(point));
				
				vertices[i] = point;
				
				i++;				
			}
			Color color = p.getReflectance();
			rotation.add(new Polygon(vertices[1], vertices[2], vertices[3], color));
		}
		return new Scene(rotation, rotatedLight);
	}

	/**
	 * This should translate the scene by the appropriate amount.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene translateScene(Scene scene) {
		// TODO fill this in.
		return scene;
	}

	/**
	 * This should scale the scene.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene scaleScene(Scene scene) {
		// TODO fill this in.
		return scene;
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		
		float x;
		int y;
		float z;
		float slopeX;
		float slopeZ;

		int startY = (int) Math.min(poly.vertices[1].y, Math.min(poly.vertices[0].y, poly.vertices[2].y));
		int endY = (int) Math.max(poly.vertices[1].y, Math.max(poly.vertices[0].y, poly.vertices[2].y));
		
		EdgeList edgeList = new EdgeList(startY,endY);
	
		Vector3D[][] edges = new Vector3D[][]{
			new Vector3D[]{poly.vertices[0],poly.vertices[1]},
			new Vector3D[]{poly.vertices[1],poly.vertices[2]},
			new Vector3D[]{poly.vertices[2],poly.vertices[0]}
		};
		

		for (Vector3D[] edge : edges) {
			
			slopeX = (edge[1].x - edge[0].x) / (float) (edge[1].y-edge[0].y);
			slopeZ = (edge[1].z - edge[0].z) / (float) (edge[1].y-edge[0].y);
			
			y = (int) (edge[0].y);
			
			x = edge[0].x;
			
			z = edge[0].z;
			
			if (edge[0].y < edge[1].y) {
				while (y <= (int) (edge[1].y)) {
					edgeList.setLeftX(y, x);
					edgeList.setLeftZ(y,z);
					x = x + slopeX;
					z = z + slopeZ;
					y++;
				}
			} 
			
			else {
				while (y >= (int)(edge[1].y)) {
					edgeList.setRightX(y,x);
					edgeList.setRightZ(y, z);
					x = x - slopeX;
					z = z - slopeZ;
					y--;
				}
			}
		}	

		return edgeList;
	}
	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 * 
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 * 
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {
		
		int y = polyEdgeList.getStartY();
		
		while ( y < polyEdgeList.getEndY()) {
			
			float slope = (polyEdgeList.getRightZ(y) - polyEdgeList.getLeftZ(y)) / (polyEdgeList.getRightX(y) - polyEdgeList.getLeftZ(y));
			
			float z = polyEdgeList.getLeftZ(y);
			
			int x = Math.round(polyEdgeList.getLeftX(y));
			
			while (x <= Math.round(polyEdgeList.getRightX(y)) - 1) {
				if (y >= 0 && x >= 0 && y < GUI.CANVAS_HEIGHT && x < GUI.CANVAS_WIDTH && z < zdepth[x][y]) {
					zbuffer[x][y] = polyColor;
					zdepth[x][y] = z;
				}
				z = z + slope;
				x++;
			}
			y++;
		}
	}
}
// code for comp261 assignments
