package ch.cembra.pf.dd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
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
		Map<String, VPlusDDLegacyRecord> outLegacyRecs = parseLegacyFile(args[0]);
		
		XMLInputFactory xif = XMLInputFactory.newFactory();
		StreamSource xml = new StreamSource( new File( args[1] ));
		XMLEventReader  xsr = xif.createXMLEventReader(xml);
		
        JAXBContext jc = JAXBContext.newInstance("ch.cembra.pain008");
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        int iCount = 0;
        BigDecimal totalAmount = new BigDecimal(0);
        String accountNbr = null;

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
	                VPlusDDLegacyRecord ddrec = outLegacyRecs.get( accNo + refNo );
	                if ( ddrec == null ) {
	                	System.err.println("MISSING RECORD ==> " + accNo + "::" + refNo );
	                	return;
	                }
	                BigDecimal trnAmount = txn.getInstdAmt().getValue().multiply(new BigDecimal(100));

	                if ( ! ddrec.AMZDB_PYMT_AMT.equals( trnAmount ) ) {
	                	System.err.println("amount is not the same for ==> " + accNo + "::" + refNo );
	                	return;	                	
	                }
	                iCount++;
	                continue;
	            }
            xsr.nextEvent();
        }
        xsr.close();
        System.out.println( "finished...!");
	}
	private static Map<String, VPlusDDLegacyRecord> parseLegacyFile(String fileName) throws Exception {
		Map<String, VPlusDDLegacyRecord> outlegacyRecs = new Hashtable();
		BufferedReader fr = new BufferedReader(new FileReader(new File(fileName)));
		VPlusDDLegacyRecord ddRec = new VPlusDDLegacyRecord();
		ddRec.init("src/AMZDBRL.xsd", "AMZDBINDELIVERYDETAIL", "AMZDBINDELIVERYDETAIL", null);
		
		String line = null;
		
		fr.readLine();
		while ( true ) {
			line = fr.readLine();
			if ( line == null ) break;
			ddRec.parse(line.getBytes(),  line.getBytes().length);
			outlegacyRecs.put(ddRec.AMZDB_DR_Y_ACCT+ddRec.AMZDB_REF_NBR, ddRec);
		}
		return outlegacyRecs;
		
	}
}
