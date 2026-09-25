package security;

import handler.ClientHandler;
import protocol.Packet;
import protocol.PacketType;

import java.util.Base64;

public class DHFirstRequest extends AbstractRequest {
  DHAbstraction DH_key;

  public DHFirstRequest(DHAbstraction DH_key) throws Exception{
    pack = new Packet(PacketType.DH_SER_INIT);
    this.DH_key = DH_key;
    requestString = Base64.getEncoder().encodeToString(
            DH_key.senndIntermediaryKey()
    );
  }
}
