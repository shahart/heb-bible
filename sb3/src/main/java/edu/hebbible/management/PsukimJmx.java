package edu.hebbible.management;

import edu.hebbible.repository.Repo;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(
        objectName = "edu.hebbible:type=Psukim",
        description = "Hebrew Bible psukim statistics"
)
public class PsukimJmx {

    private final Repo repo;

    public PsukimJmx(Repo repo) {
        this.repo = repo;
    }

    @ManagedAttribute(description = "Total number of psukim")
    public int getPsukimCount() {
        return repo.getTotalVerses();
    }
}
