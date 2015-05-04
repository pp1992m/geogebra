package org.geogebra.web.web.gui.view.dataCollection.Settings;

import java.util.ArrayList;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.view.dataCollection.DataCollectionView;
import org.geogebra.web.web.main.AppWapplication;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

public abstract class SensorSetting extends FlowPanel implements SetLabels {

	private String captionString;
	/** button to set sensor on and off */
	ToggleButton sensorOnOff;
	private Label captionLabel;
	/** button to collapse/expand settings for this sensor */
	private ToggleButton collapse;

	/**
	 * panel with the name of the sensor data and the the listbox with the
	 * depending geoElement
	 */
	FlowPanel dataValues;
	/** the listBoxes */
	private ArrayList<GeoListBox> listBoxes = new ArrayList<GeoListBox>();

	private AppW app;
	private DataCollectionView view;

	/**
	 * 
	 * @param app
	 *            {@link AppW}
	 * @param dataView
	 *            {@link DataCollectionView}
	 * @param captionString
	 *            String
	 */
	public SensorSetting(AppW app, DataCollectionView dataView,
			String captionString) {
		this.captionString = captionString;
		this.app = app;
		this.view = dataView;
		createGUI();
	}

	private void createGUI() {
		this.dataValues = new FlowPanel();
		this.dataValues.addStyleName("panelIndent");

		addCaption();
		addContent();

		this.add(dataValues);
		this.setVisible(false);
	}

	protected abstract void addContent();

	/**
	 * panel with a "sensor-on-off" button and a label with the name of the
	 * sensor. if sensor is turned off, the settings for this sensor disappear
	 * and the connections between sensor values and geoElements are
	 * restored/set back.
	 */
	private void addCaption() {
		FlowPanel caption = new FlowPanel();
		caption.addStyleName("panelTitle");

		this.captionLabel = new Label(captionString);

		collapse = new ToggleButton(
				new Image(GuiResources.INSTANCE.collapse()), new Image(
						GuiResources.INSTANCE.expand()));
		collapse.addStyleName("collapse");
		collapse.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				dataValues.setVisible(!collapse.isDown());
			}
		});

		sensorOnOff = new ToggleButton(
				new Image(AppResources.INSTANCE.hidden()), new Image(
						AppResources.INSTANCE.shown()));
		sensorOnOff.addStyleName("sensorOnOffButton");
		sensorOnOff.setDown(true);
		sensorOnOff.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				for (GeoListBox listbox : listBoxes) {
					if (listbox.getSelection() != null) {
						if (sensorOnOff.isDown()) {
							((AppWapplication) app).getDataCollection()
									.registerGeo(listbox.getType().toString(),
											listbox.getSelection());
						} else {
							((AppWapplication) app).getDataCollection()
									.removeRegisteredGeo(listbox.getType());
						}
					}
				}
			}
		});

		caption.add(sensorOnOff);
		caption.add(this.captionLabel);
		caption.add(collapse);
		this.add(caption);
	}

	/**
	 * @param rowCaption
	 *            caption
	 * @param type
	 *            {@link Types}
	 */
	protected void addRow(String rowCaption, Types type) {
		FlowPanel container = new FlowPanel();
		container.addStyleName("rowContainer");
		container.add(new Label(rowCaption));
		container.add(new Label(app.getPlain("LinkedObject")));

		GeoListBox listBox = new GeoListBox(type, this);
		listBox.addChangeHandler(this.view);

		this.listBoxes.add(listBox);

		container.add(listBox);
		dataValues.add(container);
	}

	/**
	 * updates the entries for all {@link GeoListBox listBoxes}
	 * 
	 * @param availableObjects
	 *            {@link ArrayList}
	 * @param usedObjects
	 *            {@link ArrayList}
	 */
	public void updateAllBoxes(ArrayList<GeoElement> availableObjects,
			ArrayList<GeoElement> usedObjects) {
		for (GeoListBox box : this.listBoxes) {
			updateBox(box, availableObjects, usedObjects);
		}
	}

	/**
	 * updates all {@link GeoListBox listBoxes} except the given one.
	 * 
	 * @param listBox
	 *            {@link GeoListBox}
	 * @param availableObjects
	 *            {@link ArrayList}
	 * @param usedObjects
	 *            {@link ArrayList}
	 */
	public void updateOtherBoxes(GeoListBox listBox,
			ArrayList<GeoElement> availableObjects,
			ArrayList<GeoElement> usedObjects) {
		for (GeoListBox box : this.listBoxes) {
			if (box != listBox) {
				updateBox(box, availableObjects, usedObjects);
			}
		}
	}

	/**
	 * updates the entries of the given {@link GeoListBox}
	 * 
	 * @param box
	 * @param availableObjects
	 * @param usedObjects
	 *            {@link ArrayList}
	 */
	private void updateBox(GeoListBox box,
			ArrayList<GeoElement> availableObjects,
			ArrayList<GeoElement> usedObjects) {
		GeoElement selectedElem = box.getSelection();
		box.clear();
		if (selectedElem != null && usedObjects.contains(selectedElem)) {
			box.addItem(selectedElem);
			box.setSelection(selectedElem);
			box.setSelectedIndex(box.getFirstFreeGeoListBoxIndex());
		} else {
			box.setSelection(null);
		}
		for (GeoElement elem : availableObjects) {
			box.addItem(elem);
		}
	}

	/**
	 *
	 * @return {@code true} if sensor is turned on
	 */
	public boolean isOn() {
		return this.sensorOnOff.isDown();
	}

	/**
	 * @return list of all {@link GeoListBox geoListBoxes} of this sensor
	 */
	public ArrayList<GeoListBox> getListBoxes() {
		return this.listBoxes;
	}

	@Override
	public void setLabels() {

	}
}

