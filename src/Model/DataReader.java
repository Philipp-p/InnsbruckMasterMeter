package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import android.os.StrictMode;
import quadTree.QuadTree;

public class DataReader {
	protected QuadTree<Point> min90 = new QuadTree<Point>(11, 47, 12, 48);
	protected QuadTree<Point> min180 = new QuadTree<Point>(11, 47, 12, 48);
	protected QuadTree<Point> parkstrassenW = new QuadTree<Point>(11, 47, 12,
			48);
	protected QuadTree<Point> parkstrassenT = new QuadTree<Point>(11, 47, 12, 48);

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
		Double x;
		Double y;
		int i = 0;
		for (String content = buff.readLine(); content != null; content = buff
				.readLine(), i++) {
			String[] tmp = content.split(";");
			if (i != 0) {
				x = nf.parse(tmp[6]).doubleValue();
				y = nf.parse(tmp[7]).doubleValue();
				if (tmp[3].equals("90 min."))
					min90.put(x, y, new Point(x, y));
				else if (tmp[3].equals("180 min."))
					min180.put(x, y, new Point(x, y));
				else if (tmp[3].equals("Parkstraße werktags"))
					parkstrassenW.put(x, y, new Point(x, y));
				else if (tmp[3].equals("Parkstraße täglich"))
					parkstrassenT.put(x, y, new Point(x, y));
			}
		}
	}

	private static BufferedReader loadFileFromWeb() {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
			        new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			}
		try {
			URL url12 = new URL(
					"https://www.innsbruck.gv.at/data.cfm?vpath=diverse/ogd/gis/parkscheinautomatencsv");
			URLConnection urlConn = url12.openConnection();
			InputStreamReader inStream = new InputStreamReader(
					urlConn.getInputStream());
			BufferedReader buff = new BufferedReader(inStream);
			return buff;
		} catch (Exception e) {
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
