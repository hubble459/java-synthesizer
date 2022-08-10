package nl.hubble.synth.controls;

import nl.hubble.synth.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WaveViewer extends JPanel {
	private Oscillator[] oscillators;

	public WaveViewer(Oscillator[] oscillators) {
		this.oscillators = oscillators;
		setBorder(Utils.WindowDesign.LINE_BORDER);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D graphics2D = (Graphics2D) g;
		final int PAD = 25;
		super.paintComponent(g);
		int numSamples = getWidth() - PAD * 2;
		int midY = getHeight() / 2;
		double[] mixedSamples = new double[numSamples];
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int onOscillators = (int) Arrays.stream(oscillators).filter(SynthControlContainer::isOn).count();
		for (Oscillator oscillator : oscillators) {
			if (oscillator.isOn()) {
				double[] samples = oscillator.getSampleWaveForm(numSamples);
				for (int i = 0; i < samples.length; i++) {
					mixedSamples[i] += samples[i] / onOscillators;
				}
			}
		}
		Function<Double, Integer> sampleToYCoord = sample -> (int) (midY + sample * (midY - PAD));

		graphics2D.drawLine(PAD, midY, getWidth() - PAD, midY);
		graphics2D.drawLine(PAD, PAD, PAD, getHeight() - PAD);
		for (int i = 0; i < numSamples; i++) {
			int nextY = i == numSamples - 1 ? sampleToYCoord.apply(mixedSamples[i]) : sampleToYCoord.apply(mixedSamples[i + 1]);
			graphics2D.drawLine(PAD + i, sampleToYCoord.apply(mixedSamples[i]), PAD + i + 1, nextY);
		}
	}
}
