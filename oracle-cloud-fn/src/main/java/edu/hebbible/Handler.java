package edu.hebbible;

import java.util.List;

import edu.hebbible.model.Pasuk;
import edu.hebbible.service.Svc;
import edu.hebbible.service.impl.ServiceImpl;

public class Handler {

    Svc svc = new ServiceImpl();

    public String handleRequest(Input input) {
        String name = ServiceImpl.engTx
                (input.name);
        List<Pasuk> result = svc.psukim(name, false /* input.containsName */);
        return "Total Psukim: " + result.size(); //  + name + svc.repoSize() /* 23_203 */ + svc.firstPasuk();
    }

    public static class Input {
        public String name;
    }

}