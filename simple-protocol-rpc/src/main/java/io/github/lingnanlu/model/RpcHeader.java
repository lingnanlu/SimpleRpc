package io.github.lingnanlu.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by rico on 2017/1/11.
 */
public class RpcHeader implements Serializable{

    private static final byte ST_MASK = (byte) 0x1f;
    private static final byte HB_MASK = (byte) 0x20;
    private static final byte OW_MASK = (byte) 0x40;
    private static final byte RP_MASK = (byte) 0x80;
    public static final short MAGIC = (short) 0xcaf6;
    public static final short HEADER_SIZE = (short) 20;
    public static final byte VERSION = (byte) 1;

    @Getter @Setter private short magic = MAGIC;
    @Getter @Setter private short headerSize = HEADER_SIZE;
    @Getter @Setter private byte version = VERSION;
    @Getter private byte st = (byte) 1;
    @Getter private byte hb = (byte) 0;
    @Getter private byte ow = (byte) 0;
    @Getter private byte rp = (byte) 0;
    @Getter @Setter private byte statusCode = (byte) 0;
    @Getter @Setter private byte reserved = (byte) 0;
    @Getter @Setter private long id = (long) 0;
    @Getter @Setter private int bodySize = (int) 0;


    public void setSt(byte bit_flag) {
        this.st = (byte) (bit_flag & ST_MASK);
    }

    public void setHb(byte bit_flag) {
        this.hb = (byte) (bit_flag & HB_MASK);
    }

    public void setHb() {
        this.hb = HB_MASK;
    }

    public boolean isHb() {
        return hb == HB_MASK;
    }

    public void setOw() {
        this.ow = OW_MASK;
    }

    public void setOw(byte bit_flag) {
        this.ow = (byte) (ow & OW_MASK);
    }

    public boolean isOw() {
        return ow == OW_MASK;
    }

    public void setRp() {
        this.rp = RP_MASK;
    }

    public void setRp(byte bit_flag) {
        this.rp = (byte) (rp & RP_MASK);
    }

    public boolean isRp() {
        return rp == RP_MASK;
    }

}
