package ch.cembra.dfdl.legacy.recon;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

public class ReconDetailRecord {
	private static final int RECON_DTL_ACCT_LEN = 19;
	private static final int RECON_DTL_EFF_DATE_LEN = 8;
	private static final int RECON_DTL_REJ_SRC_LEN = 4;
	private static final int RECON_DTL_REFNBR_LEN = 23;
	private static final int RECON_DTL_SUS_ACCNT_LEN = 13;
	private static final int RECON_DTL_TRN_AMT_LEN = 17;
	private static final int RECON_DTL_REJ_RSN_LEN = 40;
	
	public String RECON_DTL_ACCT;
	public String RECON_DTL_EFF_DATE;
	public String RECON_DTL_FLAG;
	public String RECON_DTL_PYMT_SRC;
	public String RECON_DTL_REJ_SRC;
	public String RECON_DTL_REFNBR;
	public String RECON_DTL_RECONBCR;
	public String RECON_DTL_SUS_ACCNT;
	public BigDecimal RECON_DTL_TRN_AMT;
	public String RECON_DTL_REJ_RSN;
	
	public String toLegacyString() {
		String s = to_RECON_DTL_ACCT() + to_RECON_DTL_EFF_DATE() + RECON_DTL_FLAG + RECON_DTL_PYMT_SRC + to_RECON_DTL_REJ_SRC() + 
				   to_RECON_DTL_REFNBR() + RECON_DTL_RECONBCR + to_RECON_DTL_SUS_ACCNT() + to_RECON_DTL_TRN_AMT() + to_RECON_DTL_REJ_RSN();
		return s;
	}
	private String to_RECON_DTL_ACCT() {
		return StringUtils.rightPad(RECON_DTL_ACCT, RECON_DTL_ACCT_LEN);
	}
	private String to_RECON_DTL_EFF_DATE() {
		return StringUtils.rightPad(RECON_DTL_EFF_DATE, RECON_DTL_EFF_DATE_LEN);
	}
	private String to_RECON_DTL_REJ_SRC() {
		return StringUtils.rightPad(RECON_DTL_REJ_SRC, RECON_DTL_REJ_SRC_LEN);
	}
	private String to_RECON_DTL_REFNBR() {
		return StringUtils.rightPad(RECON_DTL_REFNBR, RECON_DTL_REFNBR_LEN );
	}
	private String to_RECON_DTL_SUS_ACCNT () {
		return StringUtils.rightPad(RECON_DTL_SUS_ACCNT, RECON_DTL_SUS_ACCNT_LEN );
	}
	private String to_RECON_DTL_TRN_AMT() {
		return StringUtils.leftPad( String.valueOf( RECON_DTL_TRN_AMT.multiply(new BigDecimal(100)).longValue()), RECON_DTL_TRN_AMT_LEN, '0' );
	}
	private String to_RECON_DTL_REJ_RSN() {
		return StringUtils.rightPad(RECON_DTL_REJ_RSN, RECON_DTL_REJ_RSN_LEN );
	}
}
