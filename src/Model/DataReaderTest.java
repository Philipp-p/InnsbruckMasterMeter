package Model;

import static org.junit.Assert.*;

import org.junit.Test;

public class DataReaderTest {

	@Test
	public void testFillTrees() {
		Manager test = new Manager();
		assertEquals(test.getAllMasterMeters().size(), 381);
	}

}
