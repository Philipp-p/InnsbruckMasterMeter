package org.apptirol.innsbruckmastermeter.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apptirol.innsbruckmastermeter.model.quadTree.QuadTree;

import android.os.StrictMode;

public class DataReader {
	protected QuadTree<Point> min90;
	protected QuadTree<Point> min180;
	protected QuadTree<Point> parkstrassenW;
	protected QuadTree<Point> parkstrassenT;

	/**
	 * fills the three quad trees of the DataReader, one for each category of
	 * master meters, with the data from www.innsbruck.gv.at
	 * 
	 * @throws IOException
	 * @throws ParseException 
	 */
	public void fillTrees(InputStream is) throws IOException, ParseException {
		BufferedReader buff = loadFileFromWeb();
		if(buff == null)
			buff = new BufferedReader(new InputStreamReader(is));
		NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
		ArrayList<Point> p90 = new ArrayList<Point>();
		ArrayList<Point> p180 = new ArrayList<Point>();
		ArrayList<Point> pW = new ArrayList<Point>();
		ArrayList<Point> pT = new ArrayList<Point>();
		Point minCoord = new Point(180.0, 90.0);
		Point maxCoord = new Point(-180.0, -90.0);
		// first line contains identifiers for columns
		buff.readLine();
		for (String content = buff.readLine(); content != null; content = buff.readLine()) {
			String[] tmp = content.split(";");
			if (tmp.length == 8) {
				double x = nf.parse(tmp[6]).doubleValue();
				double y = nf.parse(tmp[7]).doubleValue();
				if(x > maxCoord.getX())
					maxCoord.setX(x);
				if(y > maxCoord.getY())
					maxCoord.setY(y);
				if(x < minCoord.getX())
					minCoord.setX(x);
				if(y < minCoord.getY())
					minCoord.setY(y);
				Point pos = new Point(x,y);
				if (tmp[3].equals("90 min."))
					p90.add(pos);
				else if (tmp[3].equals("180 min."))
					p180.add(pos);
				else if (tmp[3].equals("Parkstraße werktags"))
					pW.add(pos);
				else if (tmp[3].equals("Parkstraße täglich"))
					pT.add(pos);
			}
		}
		min90 = new QuadTree<Point>(minCoord.getX(), minCoord.getY(), maxCoord.getX(), maxCoord.getY());
		min180 = new QuadTree<Point>(minCoord.getX(), minCoord.getY(), maxCoord.getX(), maxCoord.getY());
		parkstrassenT = new QuadTree<Point>(minCoord.getX(), minCoord.getY(), maxCoord.getX(), maxCoord.getY());
		parkstrassenW = new QuadTree<Point>(minCoord.getX(), minCoord.getY(), maxCoord.getX(), maxCoord.getY());
		for(Point p : p90)
			min90.put(p.getX(), p.getY(), p);
		for(Point p : p180)
			min180.put(p.getX(), p.getY(), p);
		for(Point p : pW)
			parkstrassenW.put(p.getX(), p.getY(), p);
		for(Point p : pT)
			parkstrassenT.put(p.getX(), p.getY(), p);
	}

	private static BufferedReader loadFileFromWeb() {
		BufferedReader buff = null;
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Callable<BufferedReader> callable = new Callable<BufferedReader>() {
			@Override
			public BufferedReader call() {
				try {
					URL url12 = new URL(
							"https://www.innsbruck.gv.at/data.cfm?vpath=diverse/ogd/gis/parkscheinautomatencsv");
					URLConnection urlConn = url12.openConnection();
					InputStreamReader inStream = new InputStreamReader(
							urlConn.getInputStream());
					return new BufferedReader(inStream);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		Future<BufferedReader> future = executor.submit(callable);
		// future.get() returns 2
		executor.shutdown();
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void demoRead(BufferedReader buff) throws IOException {
		for (String content = buff.readLine(); content != null; content = buff
				.readLine()) {
			String[] tmp = content.split(";");
			System.out.println(tmp[3] + " " + tmp[6] + " " + tmp[7]);
		}
	}
}
