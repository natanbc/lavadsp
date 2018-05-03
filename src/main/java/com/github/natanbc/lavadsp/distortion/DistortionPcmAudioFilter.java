package com.github.natanbc.lavadsp.distortion;

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.github.natanbc.lavadsp.natives.DistortionConverter;
import com.github.natanbc.lavadsp.natives.DistortionNativeLibLoader;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

import java.util.concurrent.atomic.AtomicInteger;

public class DistortionPcmAudioFilter extends ConverterPcmAudioFilter<DistortionConverter> {
    public static final int SIN = DistortionConverter.SIN;
    public static final int COS = DistortionConverter.COS;
    public static final int TAN = DistortionConverter.TAN;
    
    private volatile double sinOffset = 0;
    private volatile double sinScale = 1;
    private volatile double cosOffset = 0;
    private volatile double cosScale = 1;
    private volatile double tanOffset = 0;
    private volatile double tanScale = 1;
    private volatile double offset = 0;
    private volatile double scale = 1;
    private final AtomicInteger enabled;

    public DistortionPcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount) {
        super(new DistortionConverter(), downstream, channelCount);
        this.enabled = new AtomicInteger(DistortionNativeLibLoader.allFunctions());
    }

    public void enableFunctions(int functions) {
        getConverter().enableFunctions(functions);
        enabled.updateAndGet(v->v | (functions & DistortionNativeLibLoader.allFunctions()));
    }

    public void disableFunctions(int functions) {
        getConverter().disableFunctions(functions);
        enabled.updateAndGet(v->v & ~functions);
    }

    public boolean isEnabled(int function) {
        return (enabled.get() & (function & DistortionNativeLibLoader.allFunctions())) != 0;
    }

    public double getSinOffset() {
        return sinOffset;
    }

    public void setSinOffset(double sinOffset) {
        getConverter().setSinOffset(sinOffset);
        this.sinOffset = sinOffset;
    }

    public double getSinScale() {
        return sinScale;
    }

    public void setSinScale(double sinScale) {
        getConverter().setSinScale(sinScale);
        this.sinScale = sinScale;
    }

    public double getCosOffset() {
        return cosOffset;
    }

    public void setCosOffset(double cosOffset) {
        getConverter().setCosOffset(cosOffset);
        this.cosOffset = cosOffset;
    }

    public double getCosScale() {
        return cosScale;
    }

    public void setCosScale(double cosScale) {
        getConverter().setCosScale(cosScale);
        this.cosScale = cosScale;
    }

    public double getTanOffset() {
        return tanOffset;
    }

    public void setTanOffset(double tanOffset) {
        getConverter().setTanOffset(tanOffset);
        this.tanOffset = tanOffset;
    }

    public double getTanScale() {
        return tanScale;
    }

    public void setTanScale(double tanScale) {
        getConverter().setTanScale(tanScale);
        this.tanScale = tanScale;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        getConverter().setOffset(offset);
        this.offset = offset;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        getConverter().setScale(scale);
        this.scale = scale;
    }
}
