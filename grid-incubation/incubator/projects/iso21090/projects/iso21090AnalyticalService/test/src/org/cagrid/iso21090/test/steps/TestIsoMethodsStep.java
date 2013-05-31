/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.iso21090.test.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.iso21090.client.ISO21090AnalyticalServiceClient;
import org.iso._21090.IVLINT;
import org.iso._21090.IVLTS;
import org.iso._21090.NullFlavor;
import org.iso._21090.UpdateMode;

import gov.nih.nci.cagrid.testing.system.haste.Step;

public class TestIsoMethodsStep extends Step {

	private EndpointReferenceType epr;
	
	public TestIsoMethodsStep(EndpointReferenceType serviceEPR) {
		this.epr = serviceEPR;
	}
	
	@Override
	public void runStep() throws Throwable {
		ISO21090AnalyticalServiceClient client = new ISO21090AnalyticalServiceClient( this.epr );

		org.iso._21090.ObjectFactory f = new org.iso._21090.ObjectFactory();

		System.out.println("**************sendIvlInt****************************");
		IVLINT ivlInt = f.createIVLINT();
		ivlInt.setFlavorId("Testing flavor Id: FlavorID_IVLINT");
		ivlInt.setNullFlavor(NullFlavor.TRC);
		ivlInt.setControlActExtension("IVLINT_controlActExtension");
		ivlInt.setHigh(f.createINT());
		ivlInt.setHighClosed(true);
		ivlInt.setLow(f.createINT());
		ivlInt.setLowClosed(false);
		ivlInt.setWidth(f.createReal());

		IVLINT returnIVLINT = client.sendIvlInt(ivlInt);
		System.out.println("*** " + returnIVLINT.getControlActExtension() + " ******");

		System.out.println("****************sendAd**************************");
		org.iso._21090.Ad sendAd = f.createAd();
		sendAd.setControlActExtension("123");
		sendAd.setNullFlavor(NullFlavor.DER);
		IVLTS ivlts = f.createIVLTS();
		ivlts.setControlActRoot("456");
		sendAd.setUseablePeriod(ivlts);
		org.iso._21090.Ad outAd = client.sendAd(sendAd);
		System.out.println("ControlActExtension["+outAd.getControlActExtension()+"]");
		System.out.println("NullFlavor["+outAd.getNullFlavor().name()+"]");
		System.out.println("UseablePeriod.ControlActRoot["+outAd.getUseablePeriod().getControlActRoot()+"]");

		System.out.println("****************sendBl**************************");
		org.iso._21090.BL sendBl = f.createBL();
		sendBl.setControlActExtension("123");
		sendBl.setNullFlavor(NullFlavor.ASKU);
		sendBl.setUpdateMode(UpdateMode.A);
		org.iso._21090.BL outBl = client.sendBl(sendBl);
		System.out.println("ControlActExtension["+outBl.getControlActExtension()+"]");
		System.out.println("NullFlavor["+outBl.getNullFlavor().name()+"]");
		System.out.println("UpdateMode["+outBl.getUpdateMode().name()+"]");

		System.out.println("**************sendCd****************************");
		org.iso._21090.CD sendCd = f.createCD();
		sendCd.setControlActExtension("123");
		client.sendCd(sendCd);

		System.out.println("**************sendEd****************************");
		org.iso._21090.ED sendEd = f.createED();
		sendEd.setControlActExtension("123");
		client.sendEd(sendEd);

		System.out.println("**************sendEn****************************");
		org.iso._21090.EN sendEn = f.createEN();
		sendEn.setControlActExtension("123");
		client.sendEn(sendEn);

		System.out.println("****************sendEnOn**************************");
		org.iso._21090.EnOn sendEnOn = f.createEnOn();
		sendEnOn.setControlActExtension("123");
		client.sendEnOn(sendEnOn);

		System.out.println("****************sendEnPn**************************");
		org.iso._21090.EnPn sendEnPn = f.createEnPn();
		sendEnPn.setControlActExtension("123");
		client.sendEnPn(sendEnPn);

		System.out.println("*****************sendIi*************************");
		org.iso._21090.Ii sendIi = f.createIi();
		sendIi.setControlActExtension("123");
		client.sendIi(sendIi);

		System.out.println("*****************sendIvlInt*************************");
		org.iso._21090.IVLINT sendIvlInt = f.createIVLINT();
		sendIvlInt.setControlActExtension("123");
		client.sendIvlInt(sendIvlInt);

		System.out.println("******************sendIvlPq************************");
		org.iso._21090.IVLPQ sendIvlPq = f.createIVLPQ();
		sendIvlPq.setControlActExtension("123");
		client.sendIvlPq(sendIvlPq);

		System.out.println("******************sendIvlReal************************");
		org.iso._21090.IVLREAL sendIvlReal = f.createIVLREAL();
		sendIvlReal.setControlActExtension("123");
		client.sendIvlReal(sendIvlReal);

		System.out.println("*******************sendIvlTs***********************");
		org.iso._21090.IVLTS sendIvlTs = f.createIVLTS();
		sendIvlTs.setControlActExtension("123");
		client.sendIvlTs(sendIvlTs);

		System.out.println("********************sendPq**********************");
		org.iso._21090.PQ sendPq = f.createPQ();
		sendPq.setControlActExtension("123");
		client.sendPq(sendPq);

		System.out.println("********************sendReal**********************");
		org.iso._21090.Real sendReal = f.createReal();
		sendReal.setControlActExtension("123");
		client.sendReal(sendReal);

		System.out.println("********************sendSc**********************");
		org.iso._21090.SC sendSc = f.createSC();
		sendSc.setControlActExtension("123");
		client.sendSc(sendSc);

		System.out.println("********************sendSt**********************");
		org.iso._21090.ST sendSt = f.createST();
		sendSt.setControlActExtension("123");
		client.sendSt(sendSt);

		System.out.println("*******************sendStNt***********************");
		org.iso._21090.StNt sendStNt = f.createStNt(); 
		sendStNt.setControlActExtension("123");
		client.sendStNt(sendStNt);

		System.out.println("*******************sendTel***********************");
		org.iso._21090.TEL sendTel = f.createTEL(); 
		sendTel.setControlActExtension("123");
		client.sendTel(sendTel);

		System.out.println("*****************sendTelEmail*************************");
		org.iso._21090.TelEmail sendTelEmail = f.createTelEmail();
		sendTelEmail.setControlActExtension("123");
		client.sendTelEmail(sendTelEmail);

		System.out.println("******************sendTelPerson************************");
		org.iso._21090.TELPerson sendTelPerson = f.createTELPerson(); 
		sendTelPerson.setControlActExtension("123");
		client.sendTelPerson(sendTelPerson);

		System.out.println("*******************sendTelPhone***********************");
		org.iso._21090.TelPhone sendTelPhone = f.createTelPhone();  
		sendTelPhone.setControlActExtension("123");
		client.sendTelPhone(sendTelPhone);

		System.out.println("********************sendTelUrl**********************");
		org.iso._21090.TelUrl sendTelUrl = f.createTelUrl();  
		sendTelUrl.setControlActExtension("123");
		client.sendTelUrl(sendTelUrl);

		System.out.println("********************sendTs**********************");
		org.iso._21090.TS sendTs = f.createTS();  
		sendTs.setControlActExtension("123");
		client.sendTs(sendTs);

		System.out.println("*******************sendDSetAd**********************");
		org.iso._21090.DSetAd sendDSetAd = f.createDSetAd(); 
		sendDSetAd.setControlActExtension("123");
		client.sendDSetAd(sendDSetAd);

		System.out.println("********************sendDSetCd**********************");
		org.iso._21090.DSetCd sendDSetCd = f.createDSetCd(); 
		sendDSetCd.setControlActExtension("123");
		client.sendDSetCd(sendDSetCd);

		System.out.println("********************sendDSetII**********************");
		org.iso._21090.DSetII sendDSetII = f.createDSetII();  
		sendDSetII.setControlActExtension("123");
		client.sendDSetII(sendDSetII);

		System.out.println("********************sendDSetTel**********************");
		org.iso._21090.DSetTel sendDSetTel = f.createDSetTel();  
		sendDSetTel.setControlActExtension("123");
		client.sendDSetTel(sendDSetTel);

		System.out.println("*******************sendInt***********************");
		org.iso._21090.INT sendInt = f.createINT(); 
		sendInt.setControlActExtension("123");
		client.sendInt(sendInt);

	}

}
