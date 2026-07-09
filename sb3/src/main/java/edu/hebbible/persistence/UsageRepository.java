package edu.hebbible.persistence;

public interface UsageRepository {

    int recordInvocation(String userId, String endpoint);
}
