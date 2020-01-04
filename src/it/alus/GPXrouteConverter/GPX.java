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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GPX {
	
	public static Boolean Read(final File inputFile, List<Waypoint> waypoints) {
		try {
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
			doc.getDocumentElement().normalize();
			if (doc.getDocumentElement().getNodeName() != "gpx") {
				JOptionPane.showMessageDialog(null, "Expected \"gpx\" as root element but not found it.","ERROR",JOptionPane.ERROR_MESSAGE);
				return false;
			}
			NodeList nList = doc.getElementsByTagName("rtept");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					double lat = Double.parseDouble(eElement.getAttribute("lat"));
					double lon = Double.parseDouble(eElement.getAttribute("lon"));
					double alt = Double.parseDouble(eElement.getElementsByTagName("ele").item(0).getTextContent());
					String name = eElement.getElementsByTagName("name").item(0).getTextContent();
					waypoints.add(new Waypoint(lat,lon,alt,name));
				}
			}
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Exception caught while reading GPX file.","ERROR",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return false;
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
		OutputStreamWriter gpx = null;
		Boolean retVal = false;
		try {
			gpx = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
			gpx.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
					"<gpx\n" +
					"version=\"1.1\"\n" +
					" creator=\"VfrFlight GPX Converter - http://www.alus.it\"\n" +
					" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
					" xmlns=\"http://www.topografix.com/GPX/1/1\"\n" +
					" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n" +
					"<metadata>\n");
			final Date time = Calendar.getInstance().getTime();
			SimpleDateFormat zuluFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			zuluFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			gpx.write("<time>" + zuluFormat.format(time) + "</time>\n");
			Waypoint w = waypoints.get(0);
			double minLat = w.getLat();
			double maxLat = minLat;
			double minLon = w.getLon();
			double maxLon = minLon;
			for (int i = 1; i < waypoints.size(); i++) {
				w = waypoints.get(i);
				if (minLat > w.getLat()) minLat = w.getLat();
				if (maxLat < w.getLat()) maxLat = w.getLat();
				if (minLon > w.getLon()) minLon = w.getLon();
				if (maxLon < w.getLon()) maxLon = w.getLon();
			}
			gpx.write("<bounds minlat=\"" + minLat + "\" minlon=\"" + minLon + "\" maxlat=\"" + maxLat + "\" maxlon=\"" + maxLon + "\"/>\n" +
					"</metadata>\n" +
					"<rte>\n" +
					" <name>" + file.getName() + "</name>\n" +
					" <desc></desc>\n");
			for (int i = 0; i < waypoints.size(); i++) {
				w = waypoints.get(i);
				gpx.write("<rtept lat=\"" + w.getLat() + "\" lon=\"" + w.getLon() + "\">\n");
				gpx.write(" <ele>" + w.getRoundedAltMt() + "</ele>\n");
				gpx.write(" <name>" + w.getName() + "</name>\n");
				gpx.write(" <cmt></cmt>\n" + 
						" <desc></desc>\n" + 
						" <sym>" + ((i==0 || i == waypoints.size()-1) ? "Airport" : "Waypoint") + "</sym>\n" + 
						"</rtept>\n");
			}
			gpx.write("</rte>\n" + 
					"<extensions>\n" + 
					"</extensions>\n" + 
					"</gpx>\n");
			
			retVal = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Exception caught while writing GPX file.","ERROR",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} finally {
			try {
				gpx.flush();
				gpx.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Exception caught while finalizing GPX file.","ERROR",JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		return retVal;
	}

}
