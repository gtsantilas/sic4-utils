package ch.cembra.dfdl.legacy.recon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HostReportToReconFile {
	private static ReconDetailRecord reconDtl = null;
	
	private static final int TXN_ID_INDEX = 4;
	private static final int MICRO_FLM_INDEX = 13;
	private static final int AMOUNT_INDEX = 8;
	private static final int ACCOUNT_INDEX = 10;
	private static final int POST_DATE_INDEX = 2;
	
	public static void main( String [] args ) throws Exception {
		if ( args.length == 0 ) {
			System.out.println("No Input File. No recon File. Bye !!!");
			return;
		}

		reconDtl = new ReconDetailRecord();
		
		ReconTrailerRecord reconTrl = new ReconTrailerRecord();

		reconTrl.RECON_TRL_TOTAL_AMT = new Long(0);
		reconTrl.RECON_TRL_TOTAL_TRANS = new Integer(0);
		
		String s = buildHeaderRecord();
		BufferedReader r = new BufferedReader(new FileReader( new File(args[0])));
		BufferedWriter w = new BufferedWriter(new FileWriter(new File( "ESR_RECON_FILE_" + buildDateAsString() + ".TXT")));
		
		w.write(s + System.lineSeparator() );
		String line = null;
		int lineCount = 0;
		while( true ) {
			line = r.readLine();
			if ( line == null ) break;
			if ( line.length() == 1 ) continue;
			lineCount++;
			if ( lineCount == 1 ) continue; // ignore first line
			String [] lineColumns = line.split(";");
			if ( lineColumns.length != 15 ) {
				System.err.println("Invalid file.abort......");
				break;
			}
			buildDetailRecord( lineColumns );
			w.write( reconDtl.toLegacyString() + System.lineSeparator());
			reconTrl.RECON_TRL_TOTAL_AMT += reconDtl.RECON_DTL_TRN_AMT.toBigInteger().longValue() * 100;  
			reconTrl.RECON_TRL_TOTAL_TRANS++;
			System.out.println("transform line : " + reconTrl.RECON_TRL_TOTAL_TRANS );
		}
		w.write( reconTrl.toLegacyString() + System.lineSeparator() );
		r.close();
		w.close();
		System.out.println("reconciliation file complete.");
	}
	
	private static void buildDetailRecord( String [] data ) throws Exception {
		reconDtl.RECON_DTL_EFF_DATE = data[POST_DATE_INDEX].replace("\"", "" );
		reconDtl.RECON_DTL_RECONBCR = "090002";
		reconDtl.RECON_DTL_REFNBR = data[TXN_ID_INDEX].replace("\"", "" ) + data[MICRO_FLM_INDEX].replace("\"", "" ); // original txn id
		reconDtl.RECON_DTL_TRN_AMT = new BigDecimal ( (data[AMOUNT_INDEX].replace("\"", "" ) ) );
		reconDtl.RECON_DTL_REJ_SRC = "PRE";
		reconDtl.RECON_DTL_REJ_RSN = "ACC NOT FOUND";
		reconDtl.RECON_DTL_ACCT = data[ACCOUNT_INDEX].replace("\"", "" );
		reconDtl.RECON_DTL_PYMT_SRC = "E";
		reconDtl.RECON_DTL_FLAG = "C";
		reconDtl.RECON_DTL_SUS_ACCNT = "DUMMY_ACCNT";
	}
	private static String buildHeaderRecord () throws Exception {
		SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMdd");
		String sdate = dtf.format(Calendar.getInstance().getTime());
		ReconHeaderRecord reconHdr = new ReconHeaderRecord();
		reconHdr.RECON_HDR_PROC_DATE = Integer.parseInt(sdate);
		return reconHdr.toLegacyString();
	}
	private static String buildDateAsString( ) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy_MM_dd");
		return f.format( Calendar.getInstance().getTime());
	}
}
