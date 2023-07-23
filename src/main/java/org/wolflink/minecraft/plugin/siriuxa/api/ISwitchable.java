package org.wolflink.minecraft.plugin.siriuxa.api;

public interface ISwitchable {

    boolean[] enabled = new boolean[]{false};
    default void setEnabled(boolean value) {
        if(enabled[0] == value) return;
        enabled[0] = value;
        if(enabled[0]) enable();
        else disable();
    }
    void enable();
    void disable();

}
