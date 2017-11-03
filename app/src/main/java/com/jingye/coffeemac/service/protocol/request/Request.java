package com.jingye.coffeemac.service.protocol.request;

import com.jingye.coffeemac.service.protocol.LinkFrame;
import com.jingye.coffeemac.service.protocol.pack.AutoPackHelper;
import com.jingye.coffeemac.service.protocol.pack.Pack;

public abstract class Request extends AutoPackHelper{

    protected String uid;
    protected LinkFrame header = null;

    public Request(String uid){
        this.uid = uid;
    }

    public LinkFrame getLinkFrame(){
    	if (header == null) {
    		header = new LinkFrame(getServiceId(),getCommandId(),uid);
    	}
        return header;
    }
    
    public Pack packRequest() {
    	return pack();
    }

    public abstract short getServiceId();

    public abstract short getCommandId();

}
