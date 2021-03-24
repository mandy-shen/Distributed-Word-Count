package rpc.service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface WordCount {

    @WebMethod
    JAXBMap<String, Integer> count(byte[] bytes);

    @WebMethod
    String output(String server, String path);
}

