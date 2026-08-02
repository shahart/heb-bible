package edu.hebbible.management;

import edu.hebbible.repository.Repo;
import org.junit.jupiter.api.Test;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PsukimJmxTest {

    @Test
    void exposesTheNumberOfPsukimAsAReadOnlyJmxAttribute() throws Exception {
        Repo repo = new Repo() {
            @Override
            public int getTotalVerses() {
                return 23_204;
            }
        };
        PsukimJmx psukimJmx = new PsukimJmx(repo);
        MBeanServer server = MBeanServerFactory.createMBeanServer();
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setServer(server);
        exporter.setEnsureUniqueRuntimeObjectNames(false);

        try {
            ObjectName objectName = exporter.registerManagedResource(psukimJmx);

            assertEquals(new ObjectName("edu.hebbible:type=Psukim"), objectName);
            assertEquals(23_204, server.getAttribute(objectName, "PsukimCount"));
            MBeanAttributeInfo attribute = server.getMBeanInfo(objectName).getAttributes()[0];
            assertEquals("PsukimCount", attribute.getName());
            assertFalse(attribute.isWritable());
        } finally {
            exporter.destroy();
            MBeanServerFactory.releaseMBeanServer(server);
        }
    }
}
