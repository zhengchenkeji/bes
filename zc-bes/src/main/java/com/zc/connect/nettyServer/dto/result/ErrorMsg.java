package com.zc.connect.nettyServer.dto.result;

public class ErrorMsg<T> extends JsonMsg
{
    private T error;

    public T getError()
    {
        return error;
    }

    public void setError(T error)
    {
        this.error = error;
    }

}
