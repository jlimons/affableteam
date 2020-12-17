/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import entities.DeliveryCompany;
import facades.DeliveryCompanyFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author jaimelimonsamperio
 */
@WebService(serviceName = "WsDeliveryCompany")
@Stateless()
public class WsDeliveryCompany {

    @EJB
    private DeliveryCompanyFacade ejbRef;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    @WebMethod(operationName = "create")
    @Oneway
    public void create(@WebParam(name = "entity") DeliveryCompany entity) {
        ejbRef.create(entity);
    }

    @WebMethod(operationName = "edit")
    @Oneway
    public void edit(@WebParam(name = "entity") DeliveryCompany entity) {
        ejbRef.edit(entity);
    }

    @WebMethod(operationName = "remove")
    @Oneway
    public void remove(@WebParam(name = "entity") DeliveryCompany entity) {
        ejbRef.remove(entity);
    }

    @WebMethod(operationName = "find")
    public DeliveryCompany find(@WebParam(name = "id") Object id) {
        return ejbRef.find(id);
    }

    @WebMethod(operationName = "findAll")
    public List<DeliveryCompany> findAll() {
        return ejbRef.findAll();
    }

    @WebMethod(operationName = "findRange")
    public List<DeliveryCompany> findRange(@WebParam(name = "range") int[] range) {
        return ejbRef.findRange(range);
    }

    @WebMethod(operationName = "count")
    public int count() {
        return ejbRef.count();
    }
    
    @WebMethod(operationName = "findByIsbn")
    public DeliveryCompany findByDeliveryId(@WebParam(name = "deliveryId") int deliveryId) {
        return ejbRef.findById(deliveryId);
    }
    
}
