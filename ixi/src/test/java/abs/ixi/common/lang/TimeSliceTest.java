package abs.ixi.common.lang;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import abs.ixi.server.common.TimeSlice;

/**
 * JUnit test case for {@link TimeSlice} class
 * 
 * @author Yogi
 *
 */
public class TimeSliceTest {
	private TimeSlice<String> timeSlice;

	@Before
	public void setUp() {
		this.timeSlice = new TimeSlice<>();
	}

	@Test
	public void testOperations() {
		this.timeSlice.addOrIncrement("foo");
		this.timeSlice.addOrIncrement("foo");

		assertEquals(true, this.timeSlice.contains("foo"));
		assertEquals(1, this.timeSlice.size());
		assertEquals(0, this.timeSlice.count("bar"));
		assertEquals(2, this.timeSlice.count("foo"));

		this.timeSlice.remove("foo");
		assertEquals(0, this.timeSlice.count("foo"));
		assertEquals(false, this.timeSlice.contains("foo"));
		assertEquals(0, this.timeSlice.size());

	}
}
