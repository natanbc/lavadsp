package com.github.natanbc.lavadsp.karaoke;

public class KaraokeConverter {
    private final int sampleRate;
    private float level = 1;
    private float monoLevel = 1;
    private float filterBand = 220;
    private float filterWidth = 100;
    private float A;
    private float B;
    private float C;
    private float y1;
    private float y2;
    
    public KaraokeConverter(int sampleRate) {
        if(sampleRate < 1) {
            throw new IllegalArgumentException("Sample rate < 1");
        }
        this.sampleRate = sampleRate;
    }
    
    public void setLevel(float level) {
        this.level = level;
    }
    
    public void setMonoLevel(float monoLevel) {
        this.monoLevel = monoLevel;
    }
    
    public void setFilterBand(float filterBand) {
        this.filterBand = filterBand;
        updateFilters();
    }
    
    public void setFilterWidth(float filterWidth) {
        this.filterWidth = filterWidth;
        updateFilters();
    }
    
    public void process(float[] leftIn, float[] rightIn, int inputOffset,
                        float[] leftOut, float[] rightOut, int outputOffset, int samples) {
        for(int i = 0; i < samples; i++) {
            float l = leftIn[inputOffset + i];
            float r = rightIn[inputOffset + i];
            /* do filtering */
            float y = (A * ((l + r) / 2.0f) - B * y1) - C * y2;
            y2 = y1;
            y1 = y;
            /* filter mono signal */
            float o = y * monoLevel * level;
            /* now cut the center */
            leftOut[outputOffset + i] = l - (r * level) + o;
            rightOut[outputOffset + i] = r - (l * level) + o;
        }
    }
    
    private void updateFilters() {
        C = (float)Math.exp(-2 * Math.PI * filterWidth / sampleRate);
        B = (float)(-4 * C / (1 + C) * Math.cos(2 * Math.PI * filterBand / sampleRate));
        A = (float)(Math.sqrt(1 - B * B / (4 * C)) * (1 - C));
        
        y1 = 0;
        y2 = 0;
    }
}
