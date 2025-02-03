package edu.hebbible.service;

import edu.hebbible.model.Pasuk;

import java.util.List;

public interface Svc {

    List<Pasuk> psukim(String name, boolean containsName);

    int repoSize();

    void logMoreFeatures(String name, String extra, String tableName);

}
