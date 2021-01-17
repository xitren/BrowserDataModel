package com.gusev.data;

public class RMS {
    protected int defaultDelayInSamples;

    /**
     * The length of the filter (number of coefficients).
     */
    private int m_nLength;

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
     * @param nLength The array of filter length.
     */
    public RMS(int nLength) {
        initialize(nLength);
    }

    private void initialize(int nLength) {
        m_nLength = nLength;
        m_afBuffer = new double[m_nLength];
        m_nBufferIndex = 0;
        defaultDelayInSamples = nLength / 2;
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
            fOutput += m_afBuffer[nBufferIndex] * m_afBuffer[nBufferIndex];
            nBufferIndex--;
            if (nBufferIndex < 0) {
                nBufferIndex += m_nLength;
            }
        }
        return Math.sqrt(fOutput / m_nLength);
    }
}
