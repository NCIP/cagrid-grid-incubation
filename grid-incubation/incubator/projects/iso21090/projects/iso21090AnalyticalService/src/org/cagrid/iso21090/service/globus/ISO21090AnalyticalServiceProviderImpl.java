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
package org.cagrid.iso21090.service.globus;

import org.cagrid.iso21090.service.ISO21090AnalyticalServiceImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the ISO21090AnalyticalServiceImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class ISO21090AnalyticalServiceProviderImpl{
	
	ISO21090AnalyticalServiceImpl impl;
	
	public ISO21090AnalyticalServiceProviderImpl() throws RemoteException {
		impl = new ISO21090AnalyticalServiceImpl();
	}
	

    public org.cagrid.iso21090.stubs.SendBlNonNullResponse sendBlNonNull(org.cagrid.iso21090.stubs.SendBlNonNullRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendBlNonNullResponse boxedResult = new org.cagrid.iso21090.stubs.SendBlNonNullResponse();
    boxedResult.setBlNonNull(impl.sendBlNonNull(params.getBlNonNull().getBlNonNull()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendEdTextResponse sendEdText(org.cagrid.iso21090.stubs.SendEdTextRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendEdTextResponse boxedResult = new org.cagrid.iso21090.stubs.SendEdTextResponse();
    boxedResult.setEdText(impl.sendEdText(params.getEdText().getEdText()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendAdResponse sendAd(org.cagrid.iso21090.stubs.SendAdRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendAdResponse boxedResult = new org.cagrid.iso21090.stubs.SendAdResponse();
    boxedResult.setAd(impl.sendAd(params.getAd().getAd()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendBlResponse sendBl(org.cagrid.iso21090.stubs.SendBlRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendBlResponse boxedResult = new org.cagrid.iso21090.stubs.SendBlResponse();
    boxedResult.setBl(impl.sendBl(params.getBl().getBl()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendCdResponse sendCd(org.cagrid.iso21090.stubs.SendCdRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendCdResponse boxedResult = new org.cagrid.iso21090.stubs.SendCdResponse();
    boxedResult.setCd(impl.sendCd(params.getCd().getCd()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendEdResponse sendEd(org.cagrid.iso21090.stubs.SendEdRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendEdResponse boxedResult = new org.cagrid.iso21090.stubs.SendEdResponse();
    boxedResult.setEd(impl.sendEd(params.getEd().getEd()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendEnResponse sendEn(org.cagrid.iso21090.stubs.SendEnRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendEnResponse boxedResult = new org.cagrid.iso21090.stubs.SendEnResponse();
    boxedResult.setEn(impl.sendEn(params.getEn().getEn()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendEnOnResponse sendEnOn(org.cagrid.iso21090.stubs.SendEnOnRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendEnOnResponse boxedResult = new org.cagrid.iso21090.stubs.SendEnOnResponse();
    boxedResult.setEnOn(impl.sendEnOn(params.getEnOn().getEnOn()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendEnPnResponse sendEnPn(org.cagrid.iso21090.stubs.SendEnPnRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendEnPnResponse boxedResult = new org.cagrid.iso21090.stubs.SendEnPnResponse();
    boxedResult.setEnPn(impl.sendEnPn(params.getEnPn().getEnPn()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendIiResponse sendIi(org.cagrid.iso21090.stubs.SendIiRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendIiResponse boxedResult = new org.cagrid.iso21090.stubs.SendIiResponse();
    boxedResult.setIi(impl.sendIi(params.getIi().getIi()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendIvlIntResponse sendIvlInt(org.cagrid.iso21090.stubs.SendIvlIntRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendIvlIntResponse boxedResult = new org.cagrid.iso21090.stubs.SendIvlIntResponse();
    boxedResult.setIvlInt(impl.sendIvlInt(params.getIvlInt().getIvlInt()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendIvlPqResponse sendIvlPq(org.cagrid.iso21090.stubs.SendIvlPqRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendIvlPqResponse boxedResult = new org.cagrid.iso21090.stubs.SendIvlPqResponse();
    boxedResult.setIvlPq(impl.sendIvlPq(params.getIvlPq().getIvlPq()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendIvlRealResponse sendIvlReal(org.cagrid.iso21090.stubs.SendIvlRealRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendIvlRealResponse boxedResult = new org.cagrid.iso21090.stubs.SendIvlRealResponse();
    boxedResult.setIvlReal(impl.sendIvlReal(params.getIvlReal().getIvlReal()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendIvlTsResponse sendIvlTs(org.cagrid.iso21090.stubs.SendIvlTsRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendIvlTsResponse boxedResult = new org.cagrid.iso21090.stubs.SendIvlTsResponse();
    boxedResult.setIvlTs(impl.sendIvlTs(params.getIvlTs().getIvlTs()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendPqResponse sendPq(org.cagrid.iso21090.stubs.SendPqRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendPqResponse boxedResult = new org.cagrid.iso21090.stubs.SendPqResponse();
    boxedResult.setPq(impl.sendPq(params.getPq().getPq()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendRealResponse sendReal(org.cagrid.iso21090.stubs.SendRealRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendRealResponse boxedResult = new org.cagrid.iso21090.stubs.SendRealResponse();
    boxedResult.setReal(impl.sendReal(params.getReal().getReal()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendScResponse sendSc(org.cagrid.iso21090.stubs.SendScRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendScResponse boxedResult = new org.cagrid.iso21090.stubs.SendScResponse();
    boxedResult.setSc(impl.sendSc(params.getSc().getSc()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendStResponse sendSt(org.cagrid.iso21090.stubs.SendStRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendStResponse boxedResult = new org.cagrid.iso21090.stubs.SendStResponse();
    boxedResult.setSt(impl.sendSt(params.getSt().getSt()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendStNtResponse sendStNt(org.cagrid.iso21090.stubs.SendStNtRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendStNtResponse boxedResult = new org.cagrid.iso21090.stubs.SendStNtResponse();
    boxedResult.setStNt(impl.sendStNt(params.getStNt().getStNt()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendTelResponse sendTel(org.cagrid.iso21090.stubs.SendTelRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendTelResponse boxedResult = new org.cagrid.iso21090.stubs.SendTelResponse();
    boxedResult.setTel(impl.sendTel(params.getTel().getTel()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendTelEmailResponse sendTelEmail(org.cagrid.iso21090.stubs.SendTelEmailRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendTelEmailResponse boxedResult = new org.cagrid.iso21090.stubs.SendTelEmailResponse();
    boxedResult.setTelEmail(impl.sendTelEmail(params.getTelEmail().getTelEmail()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendTelPersonResponse sendTelPerson(org.cagrid.iso21090.stubs.SendTelPersonRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendTelPersonResponse boxedResult = new org.cagrid.iso21090.stubs.SendTelPersonResponse();
    boxedResult.setTelPerson(impl.sendTelPerson(params.getTelPerson().getTelPerson()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendTelPhoneResponse sendTelPhone(org.cagrid.iso21090.stubs.SendTelPhoneRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendTelPhoneResponse boxedResult = new org.cagrid.iso21090.stubs.SendTelPhoneResponse();
    boxedResult.setTelPhone(impl.sendTelPhone(params.getTelPhone().getTelPhone()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendTelUrlResponse sendTelUrl(org.cagrid.iso21090.stubs.SendTelUrlRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendTelUrlResponse boxedResult = new org.cagrid.iso21090.stubs.SendTelUrlResponse();
    boxedResult.setTelUrl(impl.sendTelUrl(params.getTelUrl().getTelUrl()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendTsResponse sendTs(org.cagrid.iso21090.stubs.SendTsRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendTsResponse boxedResult = new org.cagrid.iso21090.stubs.SendTsResponse();
    boxedResult.setTs(impl.sendTs(params.getTs().getTs()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendDSetAdResponse sendDSetAd(org.cagrid.iso21090.stubs.SendDSetAdRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendDSetAdResponse boxedResult = new org.cagrid.iso21090.stubs.SendDSetAdResponse();
    boxedResult.setDSetAd(impl.sendDSetAd(params.getDSetAd().getDSetAd()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendDSetCdResponse sendDSetCd(org.cagrid.iso21090.stubs.SendDSetCdRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendDSetCdResponse boxedResult = new org.cagrid.iso21090.stubs.SendDSetCdResponse();
    boxedResult.setDSetCd(impl.sendDSetCd(params.getDSetCd().getDSetCd()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendDSetIIResponse sendDSetII(org.cagrid.iso21090.stubs.SendDSetIIRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendDSetIIResponse boxedResult = new org.cagrid.iso21090.stubs.SendDSetIIResponse();
    boxedResult.setDSetII(impl.sendDSetII(params.getDSetII().getDSetII()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendDSetTelResponse sendDSetTel(org.cagrid.iso21090.stubs.SendDSetTelRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendDSetTelResponse boxedResult = new org.cagrid.iso21090.stubs.SendDSetTelResponse();
    boxedResult.setDSetTel(impl.sendDSetTel(params.getDSetTel().getDSetTel()));
    return boxedResult;
  }

    public org.cagrid.iso21090.stubs.SendIntResponse sendInt(org.cagrid.iso21090.stubs.SendIntRequest params) throws RemoteException {
    org.cagrid.iso21090.stubs.SendIntResponse boxedResult = new org.cagrid.iso21090.stubs.SendIntResponse();
    boxedResult.set_int(impl.sendInt(params.get_int().get_int()));
    return boxedResult;
  }

}
