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
	 * @return
	 */
	public ArrayList<Points> get90MasterMeters() {
		ArrayList<Points> out = new ArrayList<Points>();
		for( Points tmp :data.min90.values()){
			out.add(tmp);
		}
		return out;
	}

	/**
	 * returns all 180 min. master meters
	 * @return
	 */
	public ArrayList<Points> get180MasterMeters() {
		ArrayList<Points> out = new ArrayList<Points>();
		for( Points tmp :data.min180.values()){
			out.add(tmp);
		}
		return out;
	}
	/**
	 * returns all "Parkstrassen" master meters
	 * @return
	 */
	public ArrayList<Points> getParkMasterMeters() {
		ArrayList<Points> out = new ArrayList<Points>();
		for( Points tmp :data.parkstrassen.values()){
			out.add(tmp);
		}
		return out;
	}
	/**
	 * returns all master meters
	 * @return
	 */
	public ArrayList<Points> getAllMasterMeters() {
		ArrayList<Points> out = new ArrayList<Points>();
		for( Points tmp :data.min90.values()){
			out.add(tmp);
		}
		for( Points tmp :data.min180.values()){
			out.add(tmp);
		}
		for( Points tmp :data.parkstrassen.values()){
			out.add(tmp);
		}
		return out;
	}
	/**
	 * returns the nearest master meter of each category
	 * @param location location of the user
	 * @return 
	 */
	public Points[] getNearestMasterMeters(Points location) {
		Points[] out = new Points[3];
		out[0] = data.min90.get(location.getX(), location.getY());
		out[1] = data.min180.get(location.getX(), location.getY());
		out[2] = data.parkstrassen.get(location.getX(), location.getY());
		return out;
	}
	

}