/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.xitren.data;

/**
 * @author xitre
 */
public class FIR {
    protected int defaultDelayInSamples;

    /**
     * The length of the filter (number of coefficients).
     */
    private int m_nLength;

    /**
     * The filter coefficients.
     */
    private double[] m_afCoefficients;

    /**
     * The buffer for past input values.
     * This stores the input values needed for convolution.
     * The buffer is used as a circular buffer.
     */
    private double[] m_afBuffer;

    /**
     * The index into m_afBuffer.
     * Since m_afBuffer is used as a circular buffer,
     * a buffer pointer is needed.
     */
    private int m_nBufferIndex;


    /**
     * Init a FIR filter with coefficients.
     *
     * @param afCoefficients The array of filter coefficients.
     */
    public FIR(double[] afCoefficients) {
        initialize(afCoefficients);
    }

    private void initialize(double[] afCoefficients) {
        m_nLength = afCoefficients.length;
        m_afCoefficients = new double[m_nLength];
        System.arraycopy(afCoefficients, 0, m_afCoefficients, 0, m_nLength);
        m_afBuffer = new double[m_nLength];
        m_nBufferIndex = 0;
        defaultDelayInSamples = m_afCoefficients.length / 2;
    }

    /**
     * Process an input sample and calculate an output sample.
     * Call this method to use the filter.
     *
     * @param fInput
     * @return
     */
    public double process(double fInput) {
        m_nBufferIndex = (m_nBufferIndex + 1) % m_nLength;
        m_afBuffer[m_nBufferIndex] = fInput;
        int nBufferIndex = m_nBufferIndex;
        double fOutput = 0.0F;
        for (int i = 0; i < m_nLength; i++) {
            fOutput += m_afCoefficients[i] * m_afBuffer[nBufferIndex];
            nBufferIndex--;
            if (nBufferIndex < 0) {
                nBufferIndex += m_nLength;
            }
        }
        return fOutput;
    }

    public double[] process(double[] input, int delayInSamples) {
        // Number of output samples is equal to the number of input samples.
        double[] output = new double[input.length];
//        float[] output = new float[input.length + m_nLength - 1];

        double sampleOut;
        for (int i = 0; i < input.length; i++) {
            sampleOut = process(input[i]);
            if (i - delayInSamples >= 0) {
                output[i - delayInSamples] = sampleOut;
            }
        }
        //  Now process the tail
        for (int i = input.length; i < input.length + delayInSamples; i++) {
            sampleOut = process(0);
            if (i - delayInSamples >= 0) {
                output[i - delayInSamples] = sampleOut;
            }
        }
        return output;
    }

    public void process(double[] output, double[] input, int delayInSamples) {
        double sampleOut;
        for (int i = 0; i < input.length; i++) {
            sampleOut = process(input[i]);
            if (i - delayInSamples >= 0) {
                output[i - delayInSamples] = sampleOut;
            }
        }
        //  Now process the tail
        for (int i = input.length; i < input.length + delayInSamples; i++) {
            sampleOut = process(0);
            if (i - delayInSamples >= 0) {
                output[i - delayInSamples] = sampleOut;
            }
        }
    }


    public double[] process(double[] input) {
        return process(input, defaultDelayInSamples);
    }


    public void process(double[] output, double[] input) {
        process(output, input, defaultDelayInSamples);
    }


    /**
     * Returns the length of the filter.
     * This returns the length of the filter
     * (the number of coefficients). Note that this is not
     * the same as the order of the filter. Commonly,
     * the 'order' of a FIR filter is said to be the number
     * of coefficients minus 1: Since a single coefficient
     * is only an amplifier/attenuator, this is considered
     * order zero.
     *
     * @return The length of the filter (the number of coefficients).
     */
    public int getLength() {
        return m_nLength;
    }


    /**
     * Get the frequency response of the filter at a specified frequency.
     * This method calculates the frequency response of the filter
     * for a specified frequency. Calling this method is allowed
     * at any time, even while the filter is operating. It does not
     * affect the operation of the filter.
     *
     * @param dOmega The frequency for which the frequency response
     *               should be calculated. Has to be given as omega values
     *               ([-PI .. +PI]).
     * @return The calculated frequency response.
     */
    public double getFrequencyResponse(double dOmega) {
        double dReal = 0.0;
        double dImag = 0.0;
        for (int i = 0; i < getLength(); i++) {
            dReal += m_afCoefficients[i] * Math.cos(i * dOmega);
            dImag += m_afCoefficients[i] * Math.sin(i * dOmega);
        }
        double dResult = Math.sqrt(dReal * dReal + dImag * dImag);
        return dResult;
    }


    /**
     * Get the phase response of the filter at a specified frequency.
     * This method calculates the phase response of the filter
     * for a specified frequency. Calling this method is allowed
     * at any time, even while the filter is operating. It does not
     * affect the operation of the filter.
     *
     * @param dOmega The frequency for which the phase response
     *               should be calculated. Has to be given as omega values
     *               ([-PI .. +PI]).
     * @return The calculated phase response.
     */
    public double getPhaseResponse(double dOmega) {
        double dReal = 0.0;
        double dImag = 0.0;
        for (int i = 0; i < getLength(); i++) {
            dReal += m_afCoefficients[i] * Math.cos(i * dOmega);
            dImag += m_afCoefficients[i] * Math.sin(i * dOmega);
        }
        double dResult = Math.atan2(dImag, dReal);
        return dResult;
    }
}
