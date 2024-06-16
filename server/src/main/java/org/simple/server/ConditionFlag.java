package org.simple.server;

import lombok.Data;

@Data
public class ConditionFlag {
    public static final String STARTED = "started";
    public static final String STOPPED = "stopped";
    private final String state;

    public ConditionFlag() {
        this.state = STARTED;
    }

    public ConditionFlag(String s) {
        this.state = s;
    }
}