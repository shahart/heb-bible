package edu.hebbible.service;

import edu.hebbible.model.Pasuk;

import java.util.List;

public interface Svc {

    List<Pasuk> psukim(String name, boolean containsName, boolean withDups);

    int repoSize();

    int recordPsukimUsage(String userId);

    int dilugim(String name, int skipMin, int skipMan);

}
