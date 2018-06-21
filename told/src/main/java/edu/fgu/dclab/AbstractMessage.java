package edu.fgu.dclab;

public abstract class AbstractMessage implements Message {
    protected String source = "服務生";

    public String getSource() {
        return this.source;
    }
}
