package rpc.service;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.TreeMap;

@XmlRootElement
public class JAXBMap<T, K> {
    TreeMap<T, K> map;

    public TreeMap<T, K> getMap() {
        return map;
    }

    public void setMap(TreeMap<T, K> map) {
        this.map = map;
    }

    public JAXBMap(TreeMap<T, K> map) {
        super();
        this.map = map;
    }

    public JAXBMap() {
        super();
    }
}