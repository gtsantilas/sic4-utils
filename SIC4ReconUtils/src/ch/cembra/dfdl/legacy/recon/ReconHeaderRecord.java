package ch.cembra.dfdl.legacy.recon;

import org.apache.commons.lang.StringUtils;

public class ReconHeaderRecord {
	private static final int TOTAL_RECOR_LEN = 133;
	private static final int RECON_HDR_PROC_DATE_LEN = 8;
	public Integer RECON_HDR_PROC_DATE;
	
	public String toLegacyString() {
		String s = String.valueOf(RECON_HDR_PROC_DATE);
		
		return StringUtils.rightPad(s, TOTAL_RECOR_LEN );
	}

}
