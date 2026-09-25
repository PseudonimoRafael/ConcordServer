package security;

import protocol.Packet;

public abstract class AbstractRequest {
    protected Packet pack;
    protected String requestString;

    public Packet makeRequest() {
        this.pack.setContent(this.requestString);
        return this.pack;
    }
}
