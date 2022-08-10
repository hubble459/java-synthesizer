package nl.hubble.synth;

import nl.hubble.synth.audio.Polyphony;
import nl.hubble.synth.controls.Oscillator;
import nl.hubble.synth.controls.WaveViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Synthesizer extends JFrame {
	private final Oscillator[] oscillators = new Oscillator[3];
	private final WaveViewer waveViewer = new WaveViewer(oscillators);
	private final Polyphony polyphony = new Polyphony(oscillators);

	private final KeyAdapter keyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			polyphony.start(e.getKeyChar());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			polyphony.stop(e.getKeyChar());
		}
	};

	public Synthesizer() throws HeadlessException {
		int y = 0;
		for (int i = 0; i < oscillators.length; i++) {
			oscillators[i] = new Oscillator(this);
			oscillators[i].setLocation(5, y);
			this.add(oscillators[i]);
			y += 105;
		}
		this.addKeyListener(keyAdapter);
		this.waveViewer.setBounds(290, 0, 310, 310);
		this.add(waveViewer);
		this.setTitle("Synthesizer");
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				polyphony.closeAll();
			}
		});
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(613, 345);
		this.setResizable(false);
		this.setLayout(null);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public KeyAdapter getKeyAdapter() {
		return keyAdapter;
	}

	public void updateWaveViewer() {
		waveViewer.repaint();
	}

	public static class AudioInfo {
		public static final int SAMPLE_RATE = 44100;
	}
}
