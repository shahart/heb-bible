package edu.hebbible.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class Dilugim extends Major {

    private boolean found;

    public boolean getFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }
}
