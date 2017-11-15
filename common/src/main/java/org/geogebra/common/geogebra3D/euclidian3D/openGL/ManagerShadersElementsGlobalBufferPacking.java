package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;

/**
 * manager packing geometries
 *
 */
public class ManagerShadersElementsGlobalBufferPacking extends ManagerShadersElementsGlobalBuffer {

	private GLBufferManager bufferManager;
	private boolean isPacking;
	private GColor currentColor;

	private class GeometriesSetElementsGlobalBufferPacking extends GeometriesSetElementsGlobalBuffer {

		private static final long serialVersionUID = 1L;
		private GColor color;

		/**
		 * constructor
		 * 
		 * @param color
		 */
		public GeometriesSetElementsGlobalBufferPacking(GColor color) {
			this.color = color;
		}

		@Override
		protected Geometry newGeometry(Type type) {
			return new GeometryElementsGlobalBufferPacking(type, index, color);
		}

		@Override
		public void bindGeometry(int size, TypeElement type) {
			bufferManager.setIndices(size);
		}

		/**
		 * geometry handler for buffer packing
		 *
		 */
		public class GeometryElementsGlobalBufferPacking extends Geometry {

			private int index;
			private GColor color;

			public GeometryElementsGlobalBufferPacking(Type type, int index, GColor color) {
				super(type);
				this.index = index;
				this.color = color;
			}

			protected void setBuffers() {
				// no internal buffer needed here
			}

			public void setType(Type type) {
				this.type = type;
			}

			public void setVertices(ArrayList<Double> array, int length) {
				// Log.debug("v length = " + length);
				bufferManager.setCurrentIndex(index);
				bufferManager.setVertexBuffer(array, length);
			}

			public void setNormals(ArrayList<Double> array, int length) {
				// Log.debug("n length = " + length);
				bufferManager.setNormalBuffer(array, length);
			}

			public void setTextures(ArrayList<Double> array, int length) {
				// Log.debug("t length = " + length);
			}

			public void setColors(ArrayList<Double> array, int length) {
				// Log.debug("c length = " + length);
			}

			public void setColorsEmpty() {
				bufferManager.setColorBuffer(color);
			}

		}

	}

	/**
	 * constructor
	 * 
	 * @param renderer
	 *            renderer
	 * @param view3d
	 *            3D view
	 */
	public ManagerShadersElementsGlobalBufferPacking(Renderer renderer,
			EuclidianView3D view3d) {
		super(renderer, view3d);
		bufferManager = new GLBufferManager();
		isPacking = false;
	}

	@Override
	protected GeometriesSet newGeometriesSet() {
		if (isPacking) {
			return new GeometriesSetElementsGlobalBufferPacking(currentColor);
		}
		return super.newGeometriesSet();
	}

	/**
	 * draw curves
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawCurves(Renderer renderer) {
		if (bufferManager.getLength() > 0) {
			bufferManager.draw((RendererShadersInterface) renderer);
		}
	}

	/**
	 * set flag that manager is actually packing (temporary code)
	 * 
	 * @param isPacking
	 */
	public void setIsPacking(boolean isPacking) {
		this.isPacking = isPacking;
	}

	/**
	 * set the current color in use
	 * 
	 * @param color
	 *            color
	 */
	public void setCurrentColor(GColor color) {
		this.currentColor = color;
	}

}