package Model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import quadTree.Points;
import quadTree.QuadTree;

public class DataReader {
	QuadTree<Points> min90 = new QuadTree<Points>(11, 47, 12, 48);
	QuadTree<Points> min180 = new QuadTree<Points>(11, 47, 12, 48);
	QuadTree<Points> parkstrassen = new QuadTree<Points>(11, 47, 12, 48);

	public static void demoRead(BufferedReader buff) throws IOException {
		for (String content = buff.readLine(); content != null; content = buff
				.readLine()) {
			String[] tmp = content.split(";");
			System.out.println(tmp[3] + " " + tmp[6] + " " + tmp[7]);
		}
	}

	public void fillTrees(BufferedReader buff) throws IOException {
		Double x;
		Double y;
		int i = 0;
		for (String content = buff.readLine() ; content != null; content = buff.readLine(), i++) {
			String[] tmp = content.split(";");
			if (i !=0) {
				x = Double.parseDouble(tmp[6].replace(",","."));
				y = Double.parseDouble(tmp[7].replace(",","."));
				if (tmp[3].equals("90 min."))
					min90.put(x, y, new Points(x,y));
				else if (tmp[3].equals("180 min."))
					min180.put(x, y, new Points(x,y));
				else if (tmp[3].equals("Parkstraße werktags"))
					parkstrassen.put(x, y, new Points(x,y));
			}
		}
	}

	public static BufferedReader loadFileFromWeb() {
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

	public static void main(String[] args) {

		BufferedReader buff = loadFileFromWeb();
		try {
			demoRead(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buff = loadFileFromWeb();
		DataReader test = new DataReader();
		try {
			test.fillTrees(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
