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
package org.cagrid.iso21090.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class ISO21090AnalyticalServiceImpl extends ISO21090AnalyticalServiceImplBase {

	
	public ISO21090AnalyticalServiceImpl() throws RemoteException {
		super();
	}
	

  public org.iso._21090.BlNonNull sendBlNonNull(org.iso._21090.BlNonNull blNonNull) throws RemoteException {
	  System.out.println(blNonNull.getControlActExtension());
	    return blNonNull;
  }

  public org.iso._21090.EdText sendEdText(org.iso._21090.EdText edText) throws RemoteException {
	  System.out.println(edText.getControlActExtension());
	    return edText;
  }

  public org.iso._21090.Ad sendAd(org.iso._21090.Ad ad) throws RemoteException {
	  System.out.println(ad.getControlActExtension());
	    return ad;
  }

  public org.iso._21090.BL sendBl(org.iso._21090.BL bl) throws RemoteException {
	  System.out.println(bl.getControlActExtension());
	    return bl;
  }

  public org.iso._21090.CD sendCd(org.iso._21090.CD cd) throws RemoteException {
	  System.out.println(cd.getControlActExtension());
	    return cd;
  }

  public org.iso._21090.ED sendEd(org.iso._21090.ED ed) throws RemoteException {
	  System.out.println(ed.getControlActExtension());
	    return ed;
  }

  public org.iso._21090.EN sendEn(org.iso._21090.EN en) throws RemoteException {
	  System.out.println(en.getControlActExtension());
	    return en;
  }

  public org.iso._21090.EnOn sendEnOn(org.iso._21090.EnOn enOn) throws RemoteException {
	  System.out.println(enOn.getControlActExtension());
	    return enOn;
  }

  public org.iso._21090.EnPn sendEnPn(org.iso._21090.EnPn enPn) throws RemoteException {
	  System.out.println(enPn.getControlActExtension());
	    return enPn;
  }

  public org.iso._21090.Ii sendIi(org.iso._21090.Ii ii) throws RemoteException {
	  System.out.println(ii.getControlActExtension());
	    return ii;
  }

  public org.iso._21090.IVLINT sendIvlInt(org.iso._21090.IVLINT ivlInt) throws RemoteException {
	  System.out.println(ivlInt.getControlActExtension());
	    return ivlInt;
  }

  public org.iso._21090.IVLPQ sendIvlPq(org.iso._21090.IVLPQ ivlPq) throws RemoteException {
	  System.out.println(ivlPq.getControlActExtension());
	    return ivlPq;
  }

  public org.iso._21090.IVLREAL sendIvlReal(org.iso._21090.IVLREAL ivlReal) throws RemoteException {
	  System.out.println(ivlReal.getControlActExtension());
	    return ivlReal;
  }

  public org.iso._21090.IVLTS sendIvlTs(org.iso._21090.IVLTS ivlTs) throws RemoteException {
    System.out.println(ivlTs.getControlActExtension());
    return ivlTs;
  }

  public org.iso._21090.PQ sendPq(org.iso._21090.PQ pq) throws RemoteException {
	  System.out.println(pq.getControlActExtension());
	    return pq;
  }

  public org.iso._21090.Real sendReal(org.iso._21090.Real real) throws RemoteException {
	  System.out.println(real.getControlActExtension());
	    return real;
  }

  public org.iso._21090.SC sendSc(org.iso._21090.SC sc) throws RemoteException {
	  System.out.println(sc.getControlActExtension());
	    return sc;
  }

  public org.iso._21090.ST sendSt(org.iso._21090.ST st) throws RemoteException {
	  System.out.println(st.getControlActExtension());
	    return st;
  }

  public org.iso._21090.StNt sendStNt(org.iso._21090.StNt stNt) throws RemoteException {
	  System.out.println(stNt.getControlActExtension());
	    return stNt;
  }

  public org.iso._21090.TEL sendTel(org.iso._21090.TEL tel) throws RemoteException {
	  System.out.println(tel.getControlActExtension());
	    return tel;
  }

  public org.iso._21090.TelEmail sendTelEmail(org.iso._21090.TelEmail telEmail) throws RemoteException {
	  System.out.println(telEmail.getControlActExtension());
	    return telEmail;
  }

  public org.iso._21090.TELPerson sendTelPerson(org.iso._21090.TELPerson telPerson) throws RemoteException {
	  System.out.println(telPerson.getControlActExtension());
	    return telPerson;
  }

  public org.iso._21090.TelPhone sendTelPhone(org.iso._21090.TelPhone telPhone) throws RemoteException {
	  System.out.println(telPhone.getControlActExtension());
	    return telPhone;
  }

  public org.iso._21090.TelUrl sendTelUrl(org.iso._21090.TelUrl telUrl) throws RemoteException {
	  System.out.println(telUrl.getControlActExtension());
	    return telUrl;
  }

  public org.iso._21090.TS sendTs(org.iso._21090.TS ts) throws RemoteException {
	  System.out.println(ts.getControlActExtension());
	    return ts;
  }

  public org.iso._21090.DSetAd sendDSetAd(org.iso._21090.DSetAd dSetAd) throws RemoteException {
	  System.out.println(dSetAd.getControlActExtension());
	    return dSetAd;
  }

  public org.iso._21090.DSetCd sendDSetCd(org.iso._21090.DSetCd dSetCd) throws RemoteException {
	  System.out.println(dSetCd.getControlActExtension());
	    return dSetCd;
  }

  public org.iso._21090.DSetII sendDSetII(org.iso._21090.DSetII dSetII) throws RemoteException {
	  System.out.println(dSetII.getControlActExtension());
	    return dSetII;
  }

  public org.iso._21090.DSetTel sendDSetTel(org.iso._21090.DSetTel dSetTel) throws RemoteException {
	  System.out.println(dSetTel.getControlActExtension());
	    return dSetTel;
  }

  public org.iso._21090.INT sendInt(org.iso._21090.INT _int) throws RemoteException {
	  System.out.println(_int.getControlActExtension());
	    return _int;
  }

}

