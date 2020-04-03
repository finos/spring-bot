package com.symphony.api.bindings.cxf.example.ioi;

import java.util.UUID;

import quickfix.Message;
import quickfix.field.BeginString;
import quickfix.field.IOIID;
import quickfix.field.IOIShares;
import quickfix.field.IOITransType;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.Text;

public class FixClient {

    public void sendFixMessageFromSymphony(IOI ioi) {
        Message fixMessage = new Message();
        Message.Header header = fixMessage.getHeader();

        header.setField(new BeginString("FIX.4.2"));
        header.setField(new SenderCompID(ioi.getSender()));
        header.setField(new TargetCompID(ioi.getTarget()));
        header.setField(new MsgType("6"));

        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();

        fixMessage.setField(new IOIID(randomUUIDString));
        fixMessage.setField(new IOITransType('N'));
        fixMessage.setField(new Symbol(ioi.getSymbol()));

        fixMessage.setField(new Side(ioi.getAction().equals(Action.B) ? Side.BUY : Side.SELL));

        fixMessage.setField(new IOIShares(ioi.getShares()));
        fixMessage.setField(new Text("Place My Order!"));

        System.out.print(fixMessage.toString());

    }
}
