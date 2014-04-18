import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.LinkedList;

import SQLClient.SQLClient;
import Trace.Trace;
import AX25.AX25Telemetry;
import BitOperations.BitOperations;


public class Test {

	  public static void main(String a[]) throws SQLException {

		  AX25Telemetry bb = new AX25Telemetry();
		  LinkedList<AX25Telemetry> bbbb = new LinkedList<AX25Telemetry>();
		  bbbb.add(bb);
		  SQLClient sql = new SQLClient();
		 
		  sql.ArchieveTelemetry(bbbb);
	  }
}
