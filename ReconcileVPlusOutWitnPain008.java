package ch.cembra.pf.dd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.stream.StreamSource;

import ch.cembra.pain008.DirectDebitTransactionInformation9CHPain008;

import com.ibm.xtq.xslt.runtime.Hashtable;

public class ReconcileVPlusOutWitnPain008 {
	public static void main( String [] args ) throws Exception {
		
		if ( args.length  != 2 ) {
			System.out.println("Args : LegacyFileName pain.008FileName");
			return;
		}
		Map<String, BigInteger> outLegacyRecs = parseLegacyFile(args[0]);
		
		XMLInputFactory xif = XMLInputFactory.newFactory();
		StreamSource xml = new StreamSource( new File( args[1] ));
		XMLEventReader  xsr = xif.createXMLEventReader(xml);
		
        JAXBContext jc = JAXBContext.newInstance("ch.cembra.pain008");
        Unmarshaller unmarshaller = jc.createUnmarshaller();

		while(xsr.hasNext()) {
            if(xsr.peek().isStartElement() && xsr.peek().asStartElement().getName().getLocalPart().equals("GrpHdr")) {
            	System.out.println("header found, nothing to do so far...");
            }
            if(xsr.peek().isStartElement() )
            	if ( xsr.peek().asStartElement().getName().getLocalPart().equals("DrctDbtTxInf")) {
	                JAXBElement<DirectDebitTransactionInformation9CHPain008> element = unmarshaller.unmarshal(xsr, DirectDebitTransactionInformation9CHPain008.class);
	                DirectDebitTransactionInformation9CHPain008 txn = element.getValue();
	                String accNo = txn.getDbtrAcct().getId().getOthr().getId();
	                String refNo = txn.getPmtId().getEndToEndId();
	                BigInteger legacyAmt = outLegacyRecs.get( accNo + refNo );
	                if ( legacyAmt == null ) {
	                	System.err.println("MISSING RECORD ==> " + accNo + "::" + refNo );
	                	return;
	                }
	                BigInteger trnAmount = txn.getInstdAmt().getValue().multiply(new BigDecimal(100)).toBigInteger();

	                if ( ! legacyAmt.equals( trnAmount ) ) {
	                	System.err.println("amount is not the same for ==> " + accNo + "::" + refNo );
	                	return;	                	
	                }
	                outLegacyRecs.remove(accNo + refNo);
	                continue;
	            }
            xsr.nextEvent();
        }
        xsr.close();
        if ( ! outLegacyRecs.keySet().isEmpty() ) {
        	System.err.println("LEGACY RECORDS STILL REMAIN IN THE MAP");
        	return;
        }
        System.out.println( "finished...no mismatched records found!");
	}
	private static Map<String, BigInteger> parseLegacyFile(String fileName) throws Exception {
		Map<String, BigInteger> outlegacyRecs = new Hashtable();
		BufferedReader fs = new BufferedReader( new FileReader( fileName ));
		VPlusDDLegacyRecord ddRec = new VPlusDDLegacyRecord();
		ddRec.init("src/AMZDBRL.xsd", "AMZDBINDELIVERYDETAIL", "AMZDBINDELIVERYDETAIL", null);
		
		fs.readLine();
		while ( true ) {
			String line  = fs.readLine();
			if ( line == null ) break;
			line += "\r";
			
			ddRec.parse( line.getBytes(), line.length() );
			if ( ddRec.AMZDB_DR_Y_ACCT == null ) continue;
			if ( ddRec.isError() ) continue;
			if ( outlegacyRecs.get(ddRec.AMZDB_DR_Y_ACCT+ddRec.AMZDB_REF_NBR) != null )  {
				System.out.println("key already exists.......");
			}
			System.out.println( ddRec.AMZDB_DR_Y_ACCT );
			outlegacyRecs.put(ddRec.AMZDB_DR_Y_ACCT+ddRec.AMZDB_REF_NBR, ddRec.AMZDB_PYMT_AMT);
			ddRec.resetMe();
		}
		return outlegacyRecs;
		
	}
}

