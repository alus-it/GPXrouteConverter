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

public class Waypoint {
	private double lat;
	private double lon;
	private double alt;
	private String name;
	
	public Waypoint(double latitude, double longitude, double altitude, String waypointName) {
		lat = latitude;
		lon = longitude;
		alt = altitude;
		name = waypointName;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public double getAltMt() {
		return alt;
	}
	
	public int getRoundedAltMt() {
		return (int) Math.round(alt);
	}
	
	public double getAltFt() {
		return alt/0.3048;
	}

	public String getName() {
		return name;
	}
}
