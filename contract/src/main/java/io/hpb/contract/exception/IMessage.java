package io.hpb.contract.exception;

import java.io.Serializable;

public interface IMessage extends Serializable {
     
    public String getCode();
    
    public String getStatus();
     
    public String getMessage();
     
}