package org.apptirol.innsbruckmastermeter.model;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

public class Manager {
	protected DataReader data;
	
	public Manager() {
		data = new DataReader();
	}
	public void fillTrees(InputStream is) {
		try {
			data.fillTrees(is);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**
	 * returns all 90 min. master meters
	 * 
	 * @return
	 */
	public ArrayList<Point> get90MasterMeters() {
		ArrayList<Point> out = new ArrayList<Point>(data.min90.values());
		return out;
	}

	/**
	 * returns all 180 min. master meters
	 * 
	 * @return
	 */
	public ArrayList<Point> get180MasterMeters() {
		ArrayList<Point> out = new ArrayList<Point>(data.min180.values());
		return out;
	}

	/**
	 * returns all "Parkstrassen werkstags" master meters
	 * 
	 * @return
	 */
	public ArrayList<Point> getParkWMasterMeters() {
		ArrayList<Point> out = new ArrayList<Point>(data.parkstrassenW.values());
		return out;
	}
	/**
	 * returns all  "Parkstarssen taeglich" master meters
	 * @return
	 */
	public ArrayList<Point> getParkTMasterMeters() {
		ArrayList<Point> out = new ArrayList<Point>(data.parkstrassenT.values());
		return out;
	}

	/**
	 * returns all master meters
	 * 
	 * @return
	 */
	public ArrayList<Point> getAllMasterMeters() {
		ArrayList<Point> out = new ArrayList<Point>();
		out.addAll(get90MasterMeters());
		out.addAll(get180MasterMeters());
		out.addAll(getParkWMasterMeters());
		out.addAll(getParkTMasterMeters());
		return out;
	}

	/**
	 * returns the nearest master meter of each category
	 * 
	 * @param location
	 *            location of the user
	 * @return
	 */
	public Point[] getNearestMasterMeters(Point location) {
		Point[] out = new Point[4];
		out[0] = data.min90.get(location.getX(), location.getY());
		out[1] = data.min180.get(location.getX(), location.getY());
		out[2] = data.parkstrassenW.get(location.getX(), location.getY());
		out[3] = data.parkstrassenT.get(location.getX(), location.getY());
		return out;
	}

	/**
	 * returns the n nearest master meters of a category 
	 * @param location user location
	 * @param n number of master meters wanted
	 * @param category 1 is 90 min., 2 is 180 min.  3 is "Parkstarssen werkstags" and 4 is "Parkstarssen taeglich"
	 * @return
	 */
	public ArrayList<Point> getNearestNMasterMetersOfCategory(Point location,
			int n, int category) {
		ArrayList<Point> out = new ArrayList<Point>();
		if (category == 1) {
			for (int i = 0; i < n; n++) {
				Point tmp = data.min90.get(location.getX(), location.getY());
				out.add(tmp);
				data.min90.remove(tmp.getX(), tmp.getY(), tmp);
			}
			for (Point tmp : out) {
				data.min90.put(tmp.getX(), tmp.getY(), tmp);
			}
		} else if (category == 2) {
			for (int i = 0; i < n; n++) {
				Point tmp = data.min180.get(location.getX(), location.getY());
				out.add(tmp);
				data.min180.remove(tmp.getX(), tmp.getY(), tmp);
			}
			for (Point tmp : out) {
				data.min180.put(tmp.getX(), tmp.getY(), tmp);
			}

		} else if (category == 3) {
			for (int i = 0; i < n; n++) {
				Point tmp = data.parkstrassenW.get(location.getX(),
						location.getY());
				out.add(tmp);
				data.parkstrassenW.remove(tmp.getX(), tmp.getY(), tmp);
			}
			for (Point tmp : out) {
				data.parkstrassenW.put(tmp.getX(), tmp.getY(), tmp);
			}

		} else if  (category == 4) {
			for (int i = 0; i < n; n++) {
				Point tmp = data.parkstrassenT.get(location.getX(),
						location.getY());
				out.add(tmp);
				data.parkstrassenT.remove(tmp.getX(), tmp.getY(), tmp);
			}
			for (Point tmp : out) {
				data.parkstrassenT.put(tmp.getX(), tmp.getY(), tmp);
			}
		}
		return out;
	}

	/**
	 * returns the n nearest master meters of every category
	 * @param location user location
	 * @param n number of master meters wanted
	 * @return
	 */
	public ArrayList<Point> getNearestNMasterMerters(Point location, int n) {
		ArrayList<Point> out = new ArrayList<Point>();
		for (int i = 1; i <= 4; i++) {
			out.addAll(getNearestNMasterMetersOfCategory(location, n, i));
		}
		return out;
	}

}