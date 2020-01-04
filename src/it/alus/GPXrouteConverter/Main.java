//============================================================================
// GPXrouteConverter for VfrFlight
// Since       : 16/3/2019
// Author      : Alberto Realis-Luc <alberto.realisluc@gmail.com>
// Web         : https://www.alus.it/airnavigator/gpx.php
// Repository  : https://github.com/alus-it/GPXrouteConverter.git
// Copyright   : (C) 2019-2020 Alberto Realis-Luc
// License     : GNU GPL v3
//
// This source file is part of GPXrouteConverter project
//============================================================================

package it.alus.GPXrouteConverter;

import java.io.File;
import java.util.List;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
	
	public static void main(String[] args) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select VfrFlight or GPX flightplan to convert");
		fc.setMultiSelectionEnabled(true);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setApproveButtonText("Convert");
		fc.setApproveButtonToolTipText("Convert the selected file(s)");
		final FileNameExtensionFilter vfr = new FileNameExtensionFilter("VfrFlight flight plans", "vfr");
		final FileNameExtensionFilter gpx = new FileNameExtensionFilter("GPX flight plans", "gpx");
		fc.addChoosableFileFilter(vfr);
		fc.addChoosableFileFilter(gpx);
		final int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File[] files = fc.getSelectedFiles();
			for (int i=0; i<files.length; i++) {
				String filename = files[i].getPath();
				System.out.println("Opening: " + filename);
				filename = filename.substring(0, filename.lastIndexOf('.')); // remove extension
				List<Waypoint> waypoints = new Vector<Waypoint>();
				if (fc.getFileFilter().equals(vfr)) {
					System.out.println("Selected VfrFlight flight plan, they will be converted to GPX routes.");
					if (VfrFlight.Read(files[i],waypoints)) {
						System.out.println("Read: " + files[i].getName() + " Waypoints: " + waypoints.size());
						filename += ".gpx";
						if (GPX.Write(filename, waypoints)) System.out.println("Succesfully converted to: " + filename);
					}
				} else {
					System.out.println("Selected a GPX file, it will be converted to VfrFlight flight plan.");
					if (GPX.Read(files[i], waypoints)) {
						System.out.println("Read: " + files[i].getName() + " Waypoints: " + waypoints.size());
						filename += ".vfr";
						if (VfrFlight.Write(filename, waypoints)) System.out.println("Succesfully converted to: " + filename);
					}
				}
			}
		}
	}

}
