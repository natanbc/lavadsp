package com.github.natanbc.lavadsp.distortion;

import com.github.natanbc.lavadsp.Converter;

public class DistortionConverter implements Converter {
    public static final int SIN = 1;
    public static final int COS = 2;
    public static final int TAN = 4;
    public static final int ALL_FUNCTIONS = SIN | COS | TAN;
    
    private int enabled = ALL_FUNCTIONS;
    private float sinOffset = 0;
    private float sinScale = 1;
    private float cosOffset = 0;
    private float cosScale = 1;
    private float tanOffset = 0;
    private float tanScale = 1;
    private float offset = 0;
    private float scale = 1;
    
    public void setSinOffset(float sinOffset) {
        this.sinOffset = sinOffset;
    }
    
    public void setSinScale(float sinScale) {
        this.sinScale = sinScale;
    }
    
    public void setCosOffset(float cosOffset) {
        this.cosOffset = cosOffset;
    }
    
    public void setCosScale(float cosScale) {
        this.cosScale = cosScale;
    }
    
    public void setTanOffset(float tanOffset) {
        this.tanOffset = tanOffset;
    }
    
    public void setTanScale(float tanScale) {
        this.tanScale = tanScale;
    }
    
    public void setOffset(float offset) {
        this.offset = offset;
    }
    
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    public void enable(int functions) {
        enabled |= functions;
    }
    
    public void disable(int functions) {
        enabled &= ~functions;
    }
    
    @Override
    public void process(float[] input, int inputOffset, float[] output, int outputOffset, int samples) {
        boolean useSin = (enabled & SIN) != 0;
        boolean useCos = (enabled & COS) != 0;
        boolean useTan = (enabled & TAN) != 0;
        for(int i = 0; i < samples; i++) {
            float sample = input[inputOffset + i];
            float sampleSin = sinOffset + (float)Math.sin(sample * sinScale);
            float sampleCos = cosOffset + (float)Math.cos(sample * cosScale);
            float sampleTan = tanOffset + (float)Math.tan(sample * tanScale);
            float result = offset + scale * (useSin ? sampleSin : 1) * (useCos ? sampleCos : 1) * (useTan ? sampleTan : 1);
            output[outputOffset + i] = Math.max(-1f, Math.min(1f, result));
        }
    }
}
