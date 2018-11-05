package renderer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import renderer.Scene.Polygon;

public class Renderer extends GUI {
	
	protected Scene scene = null;
	private Vector3D lightSource;
	private Color color;
	private List<Polygon> polygons = new ArrayList<>();
	
	
	@Override
	protected void onLoad(File file) {
		
	
		BufferedReader data;
		try {
			data = new BufferedReader(new FileReader(file));
			String title = data.readLine();
			String[] titleLine = title.split(" ");
			float x = Float.parseFloat(titleLine[0]);
			float y = Float.parseFloat(titleLine[1]);
			float z = Float.parseFloat(titleLine[2]);
			this.lightSource = new Vector3D(x, y, z).unitVector();
			String line = null;
			
			while ((line = data.readLine()) != null) {
				
				String[] values = line.split(" ");
				
				Vector3D v1 = new Vector3D(Float.parseFloat(values[0]),Float.parseFloat(values[1]),Float.parseFloat(values[2]));
				Vector3D v2 = new Vector3D(Float.parseFloat(values[3]),Float.parseFloat(values[4]),Float.parseFloat(values[5]));
				Vector3D v3 = new Vector3D(Float.parseFloat(values[6]),Float.parseFloat(values[7]),Float.parseFloat(values[8]));
				
				this.color = new Color(Integer.parseInt(values[9]), Integer.parseInt(values[10]), Integer.parseInt(values[11]));
			
				polygons.add(new Polygon(v1, v2, v3, color));
			}
			
			data.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.scene = new Scene(polygons, lightSource);
	}
	
	
	@Override
	protected void onKeyPress(KeyEvent ev) {
		
        if(ev.getKeyCode() == KeyEvent.VK_LEFT || ev.getKeyCode() == KeyEvent.VK_A){
            scene = Pipeline.rotateScene(scene, 0,(float) (-0.1*Math.PI));
            
        }else if(ev.getKeyCode() == KeyEvent.VK_RIGHT || ev.getKeyCode() == KeyEvent.VK_D){
            scene = Pipeline.rotateScene(scene, 0,(float) (0.1*Math.PI));
        
        }else if(ev.getKeyCode() == KeyEvent.VK_UP|| ev.getKeyCode() == KeyEvent.VK_W){
            scene = Pipeline.rotateScene(scene, (float) (0.1*Math.PI), 0);
        
        }else if(ev.getKeyCode() == KeyEvent.VK_DOWN || ev.getKeyCode() == KeyEvent.VK_S){
            scene = Pipeline.rotateScene(scene, (float) (-0.1*Math.PI), 0);
        }
	}
	

	@Override
	protected BufferedImage render() {
		
		if (this.scene == null) return null;
		
		
		this.scene = Pipeline.translateScene(scene);
		this.scene = Pipeline.scaleScene(scene);
		
		Color[][] zBuffer = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
		float[][] zDepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];
		
		EdgeList edges;

		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				zBuffer[x][y] = Color.WHITE;
				zDepth[x][y] = Float.POSITIVE_INFINITY;
			}
		}
		
		for (Polygon polygon : this.scene.getPolygons()) {
			
			if (!Pipeline.isHidden(polygon)) {
				
				Color color = Pipeline.getShading(polygon, scene.getLight(), this.color, new Color(getAmbientLight()[0], getAmbientLight()[1], getAmbientLight()[2]));
				
				edges = Pipeline.computeEdgeList(polygon);

				Pipeline.computeZBuffer(zBuffer, zDepth, edges, color);
			}
		}
		
		return convertBitmapToImage(zBuffer);
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}
}