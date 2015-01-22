package Model;

import java.io.IOException;
import java.util.ArrayList;

public class Manager {
	protected DataReader data = new DataReader();

	public Manager() {
		super();
		try {
			data.fillTrees();
		} catch (IOException e) {
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
		ArrayList<Point> out = new ArrayList<Point>();
		for (Point tmp : data.min180.values()) {
			out.add(tmp);
		}
		return out;
	}

	/**
	 * returns all "Parkstrassen" master meters
	 * 
	 * @return
	 */
	public ArrayList<Point> getParkMasterMeters() {
		ArrayList<Point> out = new ArrayList<Point>();
		for (Point tmp : data.parkstrassen.values()) {
			out.add(tmp);
		}
		return out;
	}

	/**
	 * returns all master meters
	 * 
	 * @return
	 */
	public ArrayList<Point> getAllMasterMeters() {
		ArrayList<Point> out = new ArrayList<Point>();
		for (Point tmp : data.min90.values()) {
			out.add(tmp);
		}
		for (Point tmp : data.min180.values()) {
			out.add(tmp);
		}
		for (Point tmp : data.parkstrassen.values()) {
			out.add(tmp);
		}
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
		Point[] out = new Point[3];
		out[0] = data.min90.get(location.getX(), location.getY());
		out[1] = data.min180.get(location.getX(), location.getY());
		out[2] = data.parkstrassen.get(location.getX(), location.getY());
		return out;
	}

	/**
	 * returns the n nearest master meters of a category 
	 * @param location user location
	 * @param n number of master meters wanted
	 * @param category 1 is 90 min., 2 is 180 min. and 3 is "parkstarssen"
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
				Point tmp = data.parkstrassen.get(location.getX(),
						location.getY());
				out.add(tmp);
				data.parkstrassen.remove(tmp.getX(), tmp.getY(), tmp);
			}
			for (Point tmp : out) {
				data.parkstrassen.put(tmp.getX(), tmp.getY(), tmp);
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
		for (int i = 1; i <= 3; i++) {
			out.addAll(getNearestNMasterMetersOfCategory(location, n, i));
		}
		return out;
	}

}