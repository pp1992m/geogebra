package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.CoordSystemAnimation;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererForExport;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Geometry3DGetterManager;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.plugin.Geometry3DGetter;

/**
 * 3D view in the background (no display)
 *
 */
public class EuclidianView3DForExport extends EuclidianView3D {

	final static private double DEFAULT_SCALE = 500;
	/**
	 * default edge length for print
	 */
	final static public double EDGE_FOR_PRINT = 25; // 2.5cm
	final static private double THICKNESS_FOR_PRINT_SURFACES = 1.0 / 2; // 1mm
	final static private double FACTOR_SURFACE_TO_LINE_THICKNESS = 1.5f; // 1.5mm
	final static private float FACTOR_LINE_THICKNESS_TO_POINT_SIZE = 2f; // 2.25mm
	final static private String THICKNESS_GEO_NAME = "STLthickness";

	private double mXmin;
	private double mXmax;
	private double mYmin;
	private double mYmax;
	private double mZmin;
	private double mZmax;
	private boolean boundsSet;
	private boolean needsNewUpdate;
	private boolean useSpecificThickness;
	private float specificThicknessForLines;
	private float specificSizeForPoints;
	private float specificThicknessForSurfaces;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 * @param settings
	 *            settings
	 */
	public EuclidianView3DForExport(EuclidianController3D ec, EuclidianSettings settings) {
		super(ec, settings);
		boundsSet = false;
		((RendererForExport) renderer).setGeometryManager();
	}

	/**
	 * 
	 * @param xmin
	 *            x-coord min
	 * @param xmax
	 *            x-coord max
	 * @param ymin
	 *            y-coord min
	 * @param ymax
	 *            y-coord max
	 * @param zmin
	 *            z-coord min
	 * @param zmax
	 *            z-coord max
	 * @param xyScale
	 *            x:y scale
	 * @param xzScale
	 *            x:z scale
	 * @param xTickDistance
	 *            x axis tick
	 * @param yTickDistance
	 *            y axis tick
	 * @param zTickDistance
	 *            z axis tick
	 */
	public void updateSettings(double xmin, double xmax, double ymin, double ymax, double zmin,
			double zmax, double xyScale, double xzScale, double xTickDistance, double yTickDistance,
			double zTickDistance) {
		this.mXmin = xmin;
		this.mXmax = xmax;
		this.mYmin = ymin;
		this.mYmax = ymax;
		this.mZmin = zmin;
		this.mZmax = zmax;
		boundsSet = true;

		EuclidianSettings3D settings = getSettings();
		settings.updateOrigin(xmin, ymin, zmin);
		double xscale = DEFAULT_SCALE / (xmax - xmin);
		settings.setXscale(xscale);
		settings.setYscale(xscale * xyScale);
		settings.setZscale(xscale * xzScale);
		setNumberingDistance(settings, 0, xTickDistance);
		setNumberingDistance(settings, 1, yTickDistance);
		setNumberingDistance(settings, 2, zTickDistance);
		settingsChanged(settings);

		((RendererForExport) renderer).setXYMinMax(xmin * getXscale(), xmax * getXscale(),
				ymin * getYscale(),
				ymax * getYscale());

		setWaitForUpdate();
	}

	private void setNumberingDistance(EuclidianSettings3D settings, int axis, double distance) {
		if (distance > 0) {
			settings.setAxisNumberingDistance(axis,
					new GeoNumeric(app.getKernel().getConstruction(), distance));
		} else {
			settings.setAutomaticAxesNumberingDistance(true, axis, false);
		}
	}

	@Override
	protected void logInited() {
		// no 3D view really inited
	}

	@Override
	protected Renderer createRenderer() {
		return new RendererForExport(this);
	}

	@Override
	public boolean drawsLabels() {
		return false;
	}

	private void updateScene() {
		needsNewUpdate = true;
		while (needsNewUpdate) {
			needsNewUpdate = false;
			renderer.drawScene();
		}
		boundsSet = false;
	}

	/**
	 * 
	 * @param format
	 *            3D format
	 * @return 3D export
	 */
	public StringBuilder export3D(Format format) {
		settingsChanged(getSettings());
		useSpecificThickness = false;
		updateScene();
		if (format.needsClosedObjects()) {
			if (updateObjectsBounds(true, true)) {
				useSpecificThickness = true;
				double d = boundsMax.getX() - boundsMin.getX();
				for (int i = 2; i <= 3; i++) {
					double val = (boundsMax.get(i) - boundsMin.get(i))
							* getScale(i - 1) / getXscale();
					if (val > d) {
						d = val;
					}
				}
				double thickness = THICKNESS_FOR_PRINT_SURFACES;
				GeoElement thicknessGeo = getKernel()
						.lookupLabel(THICKNESS_GEO_NAME);
				if (thicknessGeo != null && thicknessGeo.isNumberValue()) {
					thickness = ((NumberValue) thicknessGeo).getDouble() / 2;
				}
				specificThicknessForSurfaces = (float) ((d / EDGE_FOR_PRINT)
						* thickness * getXscale());
				specificThicknessForLines = (float) ((d / EDGE_FOR_PRINT)
						* thickness * FACTOR_SURFACE_TO_LINE_THICKNESS
						* getXscale());
				specificSizeForPoints = specificThicknessForLines
						* FACTOR_LINE_THICKNESS_TO_POINT_SIZE;
				specificThicknessForLines /= PlotterBrush.LINE3D_THICKNESS;
				specificSizeForPoints /= DrawPoint3D.DRAW_POINT_FACTOR;
				format.setScale(EDGE_FOR_PRINT / d);
				reset();
				updateScene();
			} else {
				format.setScale(10); // default value: 1unit = 10mm
			}
		}
		ExportToPrinter3D exportToPrinter = new ExportToPrinter3D(this,
				renderer.getGeometryManager());
		return exportToPrinter.export(format);
	}

	@Override
	public void waitForNewRepaint() {
		needsNewUpdate = true;
	}

	@Override
	protected double[][] updateClippingCubeMinMax() {
		if (boundsSet) {
			return clippingCubeDrawable.updateMinMax(mXmin, mXmax, mYmin, mYmax, mZmin, mZmax);
		}
		return clippingCubeDrawable.updateMinMax();
	}

	/**
	 * 
	 * @param getter
	 *            geometry getter
	 */
	public void export3D(Geometry3DGetter getter) {
		useSpecificThickness = false;
		updateScene();
		Geometry3DGetterManager m = new Geometry3DGetterManager(this, getter);
		for (int i = 0; i < 3; i++) {
			getAxisDrawable(i).export(m, false);
		}
		getRenderer().drawable3DLists.export(m);
	}

	@Override
	public void repaint() {
		// no need
	}

	@Override
	public void setToolTipText(String plainTooltip) {
		// no need
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void requestFocus() {
		// no need
	}

	@Override
	public boolean isShowing() {
		return false;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
	protected void createPanel() {
		// no need
	}

	@Override
	protected void setTransparentCursor() {
		// no need
	}

	@Override
	protected boolean getShiftDown() {
		return false;
	}

	@Override
	protected void setDefault2DCursor() {
		// no need
	}

	@Override
	public GGraphics2D getTempGraphics2D(GFont fontForGraphics) {
		return null;
	}

	@Override
	public GFont getFont() {
		return null;
	}

	@Override
	protected void setHeight(int h) {
		// no need
	}

	@Override
	protected void setWidth(int h) {
		// no need
	}

	@Override
	protected void setStyleBarMode(int mode) {
		// no need
	}

	@Override
	protected void updateSizeKeepDrawables() {
		// no need
	}

	@Override
	public boolean requestFocusInWindow() {
		return false;
	}

	@Override
	public void setPreferredSize(GDimension preferredSize) {
		// no need
	}

	@Override
	protected CoordSystemAnimation newZoomer() {
		return null;
	}

	@Override
	public void add(GBox box) {
		// no need
	}

	@Override
	public void remove(GBox box) {
		// no need
	}

	@Override
	protected EuclidianStyleBar newEuclidianStyleBar() {
		return null;
	}

	@Override
	protected void addDynamicStylebarToEV(EuclidianStyleBar dynamicStylebar) {
		// no need
	}

	@Override
	protected EuclidianStyleBar newDynamicStyleBar() {
		return null;
	}

	@Override
	public void readText(String text) {
		// no need
	}

	@Override
	public float getThicknessForLine(int thickness) {
		if (useSpecificThickness) {
			return thickness == 0 ? 0f : specificThicknessForLines;
		}
		return super.getThicknessForLine(thickness);
	}

	@Override
	public float getThicknessForSurface() {
		return specificThicknessForSurfaces;
	}

	@Override
	public float getSizeForPoint(int size) {
		if (useSpecificThickness) {
			return size == 0 ? 0f : specificSizeForPoints;
		}
		return super.getSizeForPoint(size);
	}

	@Override
	public float getTicksThicknessFactor() {
		return 1.5f;
	}

	@Override
	public float getTicksMinorThicknessFactor() {
		return 1.25f;
	}

	@Override
	public float getTicksDeltaFactor() {
		return 0.25f;
	}

}
