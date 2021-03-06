/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wscobro;

import entities.Books;
import entities.OrderBook;
import entities.Client;
import facades.BooksFacade;
import facades.ClientFacade;
import facades.OrderBookFacade;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author santiagoborobia
 */
@WebService(serviceName = "WSCobro")
public class WSCobro {

    @EJB
    private OrderBookFacade ejbRef; 
    @EJB
    private BooksFacade ejbRef2;
    @EJB
    private ClientFacade ejbRefClient;

    @WebMethod(operationName = "create")
    @Oneway
    public void create(@WebParam(name = "entity") OrderBook entity) {
        ejbRef.create(entity);
    }

    @WebMethod(operationName = "edit")
    @Oneway
    public void edit(@WebParam(name = "entity") OrderBook entity) {
        ejbRef.edit(entity);
    }

    @WebMethod(operationName = "remove")
    @Oneway
    public void remove(@WebParam(name = "entity") OrderBook entity) {
        ejbRef.remove(entity);
    }

    @WebMethod(operationName = "find")
    public OrderBook find(@WebParam(name = "id") Object id) {
        return ejbRef.find(id);
    }

    @WebMethod(operationName = "findAll")
    public List<OrderBook> findAll() {
        return ejbRef.findAll();
    }

    @WebMethod(operationName = "findRange")
    public List<OrderBook> findRange(@WebParam(name = "range") int[] range) {
        return ejbRef.findRange(range);
    }

    @WebMethod(operationName = "count")
    public int count() {
        return ejbRef.count();
    }

    /**
     * Web service operation
     * @param isbn
     * @param idCliente
     * @param cantLibros
     * @return 
     */
    @WebMethod(operationName = "getNewBalance")
    public String getNewBalance(@WebParam(name = "isbn") int isbn, @WebParam(name = "idCliente") int idCliente, @WebParam(name = "cantLibros") int cantLibros) {
        String res = ejbRef.getNewBalance(isbn, cantLibros, idCliente);
        return res;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "updateBalance")
    public int updateBalance(@WebParam(name = "idClte") int idClte, @WebParam(name = "newBalance") String newBalance) {
        int res;
        res = ejbRef.updateBalance(idClte, newBalance);
        return res;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "discountHold")
    public int discountHold(@WebParam(name = "isbn") int isbn, @WebParam(name = "units") int units) {
        return ejbRef.discountHold(isbn, units);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "reStockHold")
    public int reStockHold(@WebParam(name = "isbn") int isbn, @WebParam(name = "units") int units) {
        return ejbRef.reStockHold(isbn, units);
    }
    
    /**
     * Web service operation
     * @param idClient
     * @param Book
     * @param monto
     * @param units
     * @return 
     */
    @WebMethod(operationName = "newOrderBook")
    public int newOrderBook(@WebParam(name = "idClient") int idClient, @WebParam(name = "Book") Books Book, @WebParam(name = "monto") BigDecimal monto, @WebParam(name = "units") int units) {
 
        OrderBook ob = new OrderBook(); //Insert new order in table OrderBook
        Client c = ejbRefClient.findById(idClient);
        ob.setClientid(c);
        ob.setIsbn(Book);
        ob.setFinalcost(monto);
        ob.setUnitsordered(units);
        create(ob);
        System.out.println("A new invoice has been created with value = INV"+ob.getOrderid()
                                            + "\n\t Client "+idClient+" new balance is $"+c.getBalance());
        return ob.getOrderid();
    }
    
    /**
     * Web service operation
     * @param idClt
     * @param isbn
     * @param units
     * @return 
     * @throws java.lang.Exception
     */
    @WebMethod(operationName = "startPayment")
    public int startPayment(@WebParam(name = "idClt") int idClt, @WebParam(name = "isbn") int isbn, @WebParam(name = "units") int units) throws Exception{
        String res;
        int orderId = -1;
        if (Integer.toString(isbn).length() >= 1) { // TODO: Check that the ISBN used is in the 13 digit ISBN format 
            Books bk = ejbRef2.findByIsbn(isbn);
            if(bk.getIsbn()!= -1){
                if(units >= 1){
                    Client clt = ejbRefClient.findById(idClt);
                    if(clt.getClientid()!=-1){
                        String balance = getNewBalance(isbn, idClt, units);
                        double bal = Double.parseDouble(balance);
                        BigDecimal monto = bk.getPrice();  
                        BigDecimal uds = new BigDecimal(units);
                        monto = monto.multiply(uds);
                        
                        if(bal>=0){ //Check if the client has enough credits to proceed with the purchase
                            updateBalance(idClt, balance); //Charges the client
                            discountHold(isbn, units); //Discounts the previously held books
                            orderId = newOrderBook(idClt, bk, monto, units);
                        }
                        else{ // The client hasn't enough credits
                            discountHold(isbn, units); //discounts the previously hold books
                            reStockHold(isbn, units);
                            res = "There are unsufficient credits in the client's account to complete the purchase. The total amount is $"+monto+", but the client has $"+clt.getBalance();
                            throw new Exception(res);
                        }
                    }else{
                        res = "The client with ClientId = "+idClt+" is not registered in the database. Please try with another ClientId";
                        throw new Exception(res);
                    }
                }
                else{
                    res = "Please, enter a number of units bigger than 0";
                    throw new Exception(res);
                }
            } else{
                res = "The booh with ISBN ="+isbn+" is not registered in the database. Please try with another ISBN.";
                throw new Exception(res);
            }
        }else{
            res = "Please enter an ISBN with length of 13 digits.";
            throw new Exception(res);
        }
        return orderId;
    }

    
}
