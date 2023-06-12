package com.zc.common.core.secret.sm2;

import com.zc.common.core.secret.smutil.Util;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * @author Athena-gongfanfei
 */
public class Sm2KeyVo {

    BigInteger privateKey ;
    ECPoint publicKey ;

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(BigInteger privateKey) {
        this.privateKey = privateKey;
    }

    public ECPoint getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(ECPoint publicKey) {
        this.publicKey = publicKey;
    }

    public String getPubHexInSoft(){
        return Util.byteToHex(publicKey.getEncoded());
    }

    public String getPriHexInSoft(){
        return Util.byteToHex(privateKey.toByteArray());
    }

    @Override
    public String toString() {
        return "SM2KeyVO{" +
                "privateKey=" + privateKey +
                ", publicKey=" + publicKey +
                '}';
    }
}
