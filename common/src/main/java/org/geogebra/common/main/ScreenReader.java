package org.geogebra.common.main;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.plugin.EventType;

/**
 * Utility class for reading GeoElement descriptions
 * 
 * @author Judit
 */
public class ScreenReader {

	/**
	 * @param app
	 *            application
	 */
	public static void updateSelection(App app) {
		if (0 < app.getSelectionManager().getSelectedGeos().size()) {
			GeoElement geo0 = app.getSelectionManager().getSelectedGeos().get(0);
			// do not steal focus from input box
			if (geo0.isGeoInputBox()) {
				return;
			}
			readText(geo0, app);
		}
	}

	/**
	 * Reads the selected item of the current drop down.
	 * 
	 * @param app
	 *            application
	 * @param geo
	 *            the current geo
	 */
	public static void dropDownItemSelected(App app, GeoElement geo) {
		String item = ((GeoList) geo).getSelectedElement().getAlgebraDescriptionDefault();
		String readText;
		if ("".equals(item)) {
			readText = app.getLocalization().getMenuDefault("DropDownEmptyItemSelected",
					"Empty item selected. Drop down closed. ");
		} else {
			readText = app.getLocalization().getPlainArray("DropDownItemSelected",
					"Item %0 selected. Drop down closed. ", new String[] { item });
		}
		app.getActiveEuclidianView().readText(readText);
	}

	/**
	 * Reads the item when the selector moved on it.
	 * 
	 * @param app
	 *            application
	 * @param text
	 *            selected item text to read
	 */
	public static void dropDowmSelectorMovedOn(App app, String text) {
		String readText = text;
		if ("".equals(text)) {
			readText = app.getLocalization().getMenuDefault("EmptyItem", "Empty item");
		}
		app.getActiveEuclidianView().readText(readText);
	}

	/**
	 * @param geo0
	 *            selected element
	 * @param app
	 *            application
	 */
	public static void readText(GeoElement geo0, App app) {
		StringBuilder sb = new StringBuilder();
		String readerText = geo0.getAuralText();
		if (readerText != null) {
			sb.append(readerText);
		}

		if (geo0.getScript(EventType.CLICK) != null
				&& geo0.getScript(EventType.CLICK).getText().length() > 0) {
			appendSentence(sb, "PressSpaceToRunScript", "Press space to run script", null, app);
		}
		readText(sb.toString(), app);
	}

	private static void readText(String text, App app) {
		// MOW-137 if selection originated in AV we don't want to move
		// focus to EV
		if (app.getGuiManager() == null || app.getGuiManager().getLayout().getDockManager()
				.getFocusedViewId() == app.getActiveEuclidianView().getViewID()) {
			app.getActiveEuclidianView().readText(text);
		}
	}

	private static void appendSentence(StringBuilder sb, String string, String stringDefault,
			String[] args, App app) {
		sb.append(" ");
		if (app.has(Feature.READ_DROPDOWNS)) {
			if (args != null) {
				sb.append(app.getLocalization().getPlainArray(string, stringDefault, args));
			} else {
				sb.append(app.getLocalization().getMenuDefault(string, stringDefault));
			}
		} else {
			sb.append(app.getLocalization().getMenuDefault(string, stringDefault));
		}
		sb.append(".");
	}

	/**
	 * Reads some instructions at opening the drop down.
	 * 
	 * @param geoList
	 *            drop down
	 */
	public static void readOpenDropDown(GeoList geoList) {
		StringBuilder sb = new StringBuilder();
		sb.append(geoList.getKernel().getApplication().getLocalization().getPlainArray(
				"DropDownOpened", "Drop down %0 opened.",
				new String[] { geoList.getLabel(StringTemplate.defaultTemplate) }));
		sb.append(geoList.getKernel().getApplication().getLocalization().getMenuDefault(
				"PressArrowsToGo", "Press up arrow and down arrow to go to different options."));
		sb.append(geoList.getKernel().getApplication().getLocalization()
				.getMenuDefault("PressEnterToSelect", "Press enter to select."));
		geoList.getKernel().getApplication().getActiveEuclidianView().readText(sb.toString());
	}

	/**
	 * Reads text when space is pressed on geo.
	 * 
	 * @param geo
	 *            GeoElement to handle.
	 */
	public static void handleSpace(GeoElement geo) {
		App app = geo.getKernel().getApplication();
		readText(geo.getAuralTextForSpace(), app);
	}
}
