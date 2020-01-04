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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.swing.JOptionPane;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class VfrFlight {
	private static final double oneMin = 1.0/60;
	private static final double oneSec = 1.0/3600;
	private static int deg=0, min=0;
	private static double sec=0;
	private static char hem='X';
	
	public static Boolean Read(final File inputFile, List<Waypoint> waypoints) {
		try {
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
			doc.getDocumentElement().normalize();
			if (doc.getDocumentElement().getNodeName() != "route") {
				JOptionPane.showMessageDialog(null, "Expected \"route\" as root element but not found it.","ERROR",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (!doc.getDocumentElement().getAttribute("version").equals("1.0")) {
				JOptionPane.showMessageDialog(null, "Expected \"route\" tag with version 1.0 but not found it.","ERROR",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			NodeList nList = doc.getElementsByTagName("altitude");
			if (nList.getLength() != 1) {
				JOptionPane.showMessageDialog(null, "Tag \"altitude\" not found or repeated.","ERROR",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			final String unit = nList.item(0).getTextContent();
			Boolean unitIsFt = true;
			if (unit.equals("ft")) unitIsFt = true;
			else if (unit.equals("m")) unitIsFt = false;
			else {
				JOptionPane.showMessageDialog(null, "Altitude unit not valid.","ERROR",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			nList = doc.getElementsByTagName("gpsplace");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String name = eElement.getAttribute("name");
					double alt = Double.parseDouble(eElement.getAttribute("alt"));
					if (unitIsFt) alt = alt * 0.3048;
					double lat = calcDegrees((Element)eElement.getElementsByTagName("latitude").item(0), true);
					if (lat == -181 ) return false;
					double lon = calcDegrees((Element)eElement.getElementsByTagName("longitude").item(0), false);
					if (lon == -181 ) return false;
					waypoints.add(new Waypoint(lat,lon,alt,name));
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Exception caught while reading VfrFlight file.","ERROR",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Boolean Write(final String filename, final List<Waypoint> waypoints) {
		if (waypoints.size() < 2) {
			JOptionPane.showMessageDialog(null, "Found less than 2 waypoints.","ERROR",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		final File file = new File(filename);
		if(file.exists() && !file.isDirectory() && JOptionPane.showConfirmDialog(null,
				"The destination file: " + file.getName() + " already exists.\n" + 
				"Do you want to overwrite it?",
				"Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) return false;
		OutputStreamWriter vfr = null;
		Boolean retVal = true;
		final DecimalFormat df = new DecimalFormat(".#");
		try {
			vfr = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
			vfr.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<route version=\"1.0\">\n" + 
					"    <mapradials/>\n" + 
					"    <landmarks/>\n" + 
					"    <vors/>\n" + 
					"    <points>\n");
			for (int i = 0; i < waypoints.size(); i++) {
				final Waypoint w = waypoints.get(i);
				vfr.write("        <gpsplace name=\"" + w.getName() + "\" alt=\"" + df.format(round(w.getAltFt(),1)) +"\">\n");
				if (!calcDegMinSec(w.getLat(), true)) {
					retVal = false;
					JOptionPane.showMessageDialog(null, "Invalid waypoint latitude.","ERROR",JOptionPane.ERROR_MESSAGE);
					break;
				}
				vfr.write("            <latitude deg=\"" + deg + "\" min=\"" + min + "\" sec=\"" + df.format(sec) + "\" hem=\"" + hem +"\"/>\n");
				if (!calcDegMinSec(w.getLon(), false)) {
					JOptionPane.showMessageDialog(null, "Invalid waypoint longitude.","ERROR",JOptionPane.ERROR_MESSAGE);
					retVal = false;
					break;
				}
				vfr.write("            <longitude deg=\"" + deg + "\" min=\"" + min + "\" sec=\"" + df.format(sec) + "\" hem=\"" + hem +"\"/>\n");
				vfr.write("        </gpsplace>\n");
			}
			if (retVal) vfr.write("    </points>\n" + 
					"    <magneticdeclination xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" + 
					"    <cruisespeed xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" + 
					"    <climbingspeed xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" + 
					"    <descendingspeed xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" + 
					"    <cruisealt xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" + 
					"    <fuel>\n" + 
					"        <qmax xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" + 
					"        <qm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" + 
					"        <burnrate xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" + 
					"        <navFuelReserveMin xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" + 
					"    </fuel>\n" + 
					"    <wind>\n" + 
					"        <direction>0.0</direction>\n" + 
					"        <speed>0.0</speed>\n" + 
					"        <windsock difflat=\"0.0\" difflon=\"0.0\"/>\n" + 
					"    </wind>\n" + 
					"    <units>\n" + 
					"        <distance>km</distance>\n" + 
					"        <speed>km/h</speed>\n" + 
					"        <windSpeed>kt</windSpeed>\n" + 
					"        <altitude>ft</altitude>\n" + 
					"        <fuel>l</fuel>\n" + 
					"    </units>\n" + 
					"    <alternates/>\n" + 
					"</route>\n");
		} catch (Exception e) {
			retVal = false;
			JOptionPane.showMessageDialog(null, "Exception caught while writing VfrFlight file.","ERROR",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} finally {
			try {
				vfr.flush();
				vfr.close();
			} catch (IOException e) {
				retVal = false;
				JOptionPane.showMessageDialog(null, "Exception caught while finalizing VfrFlight file.","ERROR",JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		return retVal;
	}
	
	private static double calcDegrees(final Element coord, final Boolean isLat) {
		int deg = Integer.parseInt(coord.getAttribute("deg"));
		int min = Integer.parseInt(coord.getAttribute("min"));
		double sec = Double.parseDouble(coord.getAttribute("sec"));
		String hem = coord.getAttribute("hem");
		Boolean positive = hem.equals(isLat?"N":"E");
		if(!positive && !hem.equals(isLat?"S":"W")) return -181;
		double degrees = (double)deg + (double)min * oneMin + sec * oneSec;
		if (!positive) degrees = -degrees;
		return degrees;
	}
	
	private static Boolean calcDegMinSec(final double degrees, final Boolean isLat) {
		if (isLat) {
			if (degrees > 90 || degrees < -90) return false;
			hem = degrees >= 0 ? 'N':'S';
		} else {
			if (degrees > 180 || degrees < -180) return false;
			hem =  degrees >= 0 ? 'E':'W';
		}
		double decimal = Math.abs(degrees);
		deg = (int) Math.floor(decimal);
		decimal = (decimal-deg)/oneMin;
		min = (int) Math.floor(decimal);
		if (min == 60) {
			deg++;
			min = 0;
		}
		sec = (decimal-min)/oneMin;
		if (sec == 60) {
			min++;
			sec = 0;
		}
		sec = round (sec,1);
		if (min == 60) {
			deg++;
			min = 0;
		}
		return true;
	}
	
	private static double round(final double value, final int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}
	
}