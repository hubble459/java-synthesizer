package nl.hubble.synth.controls;

import nl.hubble.synth.Synthesizer;
import nl.hubble.synth.WaveTable;
import nl.hubble.synth.utils.ReferenceWrapper;
import nl.hubble.synth.utils.Utils;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class Oscillator extends SynthControlContainer {
	private static final int TONE_OFFSET_LIMIT = 2000;

	private final ReferenceWrapper<Integer> toneOffset = new ReferenceWrapper<>(0);
	private final ReferenceWrapper<Integer> volume = new ReferenceWrapper<>(100);
	private WaveTable waveTable = WaveTable.Sine;
	private double keyFrequency;
	private int waveTableStepSize;
	private int waveTableIndex;

	public Oscillator(Synthesizer synthesizer) {
		super(synthesizer);
		JComboBox<WaveTable> comboBox = new JComboBox<>(WaveTable.values());
		comboBox.setFocusable(false);
		comboBox.setSelectedItem(WaveTable.Sine);
		comboBox.setBounds(10, 10, 75, 25);
		comboBox.addItemListener(l -> {
			if (l.getStateChange() == ItemEvent.SELECTED) {
				waveTable = (WaveTable) l.getItem();
			}
			synthesizer.updateWaveViewer();
		});
		this.add(comboBox);

		JLabel toneParameter = new JLabel(" x0.00");
		toneParameter.setBounds(165, 65, 50, 25);
		toneParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
		Utils.ParameterHandling.addParameterMouseListeners(toneParameter, this, -TONE_OFFSET_LIMIT, TONE_OFFSET_LIMIT, 1, toneOffset, () -> {
			applyToneOffset();
			toneParameter.setText(" x" + String.format("%.3f", getToneOffset()));
			synthesizer.updateWaveViewer();
		});
		this.add(toneParameter);

		JLabel toneText = new JLabel("Tone");
		toneText.setBounds(172, 40, 75, 25);
		this.add(toneText);

		JLabel volumeParameter = new JLabel(" 100%");
		volumeParameter.setBounds(222, 65, 50, 25);
		volumeParameter.setBorder(Utils.WindowDesign.LINE_BORDER);
		Utils.ParameterHandling.addParameterMouseListeners(volumeParameter, this, 0, 100, 1, volume, () -> {
			volumeParameter.setText(" " + volume.value + "%");
			synthesizer.updateWaveViewer();
		});
		this.add(volumeParameter);

		JLabel volumeText = new JLabel("Volume");
		volumeText.setBounds(225, 40, 75, 25);
		this.add(volumeText);

		JCheckBox onParameter = new JCheckBox("On", true);
		onParameter.setFocusable(false);
		onParameter.setBounds(230, 10, 45, 20);
		onParameter.addItemListener(e -> {
			this.setOn(e.getStateChange() == ItemEvent.SELECTED);
			synthesizer.updateWaveViewer();
		});
		this.add(onParameter);

		this.setSize(279, 100);
		this.setBorder(Utils.WindowDesign.LINE_BORDER);
		this.setLayout(null);
	}

	public double getNextSample() {
		double sample = waveTable.getSamples()[waveTableIndex] * getVolumeMultiplier();
		waveTableIndex = (waveTableIndex + waveTableStepSize) % WaveTable.SIZE;
		return sample;
	}

	public void setKeyFrequency(double frequency) {
		this.keyFrequency = frequency;
		applyToneOffset();
	}

	public double[] getSampleWaveForm(int numSamples) {
		double[] samples = new double[numSamples];
		double frequency = 1.0 / (numSamples / (double) Synthesizer.AudioInfo.SAMPLE_RATE) * 3.0;
		int index = 0;
		int stepSize = (int) (WaveTable.SIZE * Utils.Math.offsetTone(frequency, getToneOffset()) / Synthesizer.AudioInfo.SAMPLE_RATE);
		for (int i = 0; i < numSamples; i++) {
			samples[i] = waveTable.getSamples()[index] * getVolumeMultiplier();
			index = (index + stepSize) % WaveTable.SIZE;
		}
		return samples;
	}

	private double getToneOffset() {
		return toneOffset.value / 1000d;
	}

	public double getVolumeMultiplier() {
		return isOn() ? volume.value / 100d : 0;
	}

	private void applyToneOffset() {
		waveTableStepSize = (int) (WaveTable.SIZE * Utils.Math.offsetTone(keyFrequency, getToneOffset()) / Synthesizer.AudioInfo.SAMPLE_RATE);
	}
}
