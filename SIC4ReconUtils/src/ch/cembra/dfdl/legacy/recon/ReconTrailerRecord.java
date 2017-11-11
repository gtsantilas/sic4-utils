package ch.cembra.dfdl.legacy.recon;

import org.apache.commons.lang.StringUtils;

public class ReconTrailerRecord {
	private static final int TOTAL_RECOR_LEN = 133;
	
	private static final int RECON_TRL_TOTAL_TRANS_LEN = 5;
	private static final int RECON_TRL_TOTAL_AMT_LEN  = 17;
	
	public Integer RECON_TRL_TOTAL_TRANS;
	public Long RECON_TRL_TOTAL_AMT;
	
	public String toLegacyString() {
		String s = to_RECON_TRL_TOTAL_TRANS() + to_RECON_TRL_TOTAL_AMT();
		return StringUtils.rightPad( s, TOTAL_RECOR_LEN );
	}
	
	private String to_RECON_TRL_TOTAL_TRANS() {
		return StringUtils.leftPad(String.valueOf(RECON_TRL_TOTAL_TRANS), RECON_TRL_TOTAL_TRANS_LEN, '0' ) ;
	}
	private String to_RECON_TRL_TOTAL_AMT() {
		return StringUtils.leftPad( String.valueOf( RECON_TRL_TOTAL_AMT ), RECON_TRL_TOTAL_AMT_LEN, '0' );
	}
}
