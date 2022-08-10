package nl.hubble.synth.audio;

import nl.hubble.synth.controls.Oscillator;
import nl.hubble.synth.controls.SynthControlContainer;
import nl.hubble.synth.utils.ReferenceWrapper;
import nl.hubble.synth.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;

public class Polyphony {
	private static final HashMap<Character, Double> KEY_FREQUENCIES = new HashMap<>();
	private static final int VOICES = 1;
	private final Voice[] voices = new Voice[VOICES];
	private final Oscillator[] oscillators;

	static {
		final int STARTING_KEY = 16;
		final int KEY_FREQUENCY_INCREMENT = 2;
		final char[] KEYS = "zxcvbnm,./asdfghjkl;'qwertyuiop[]".toCharArray();
		for (int i = STARTING_KEY, key = 0; i < KEYS.length * KEY_FREQUENCY_INCREMENT + STARTING_KEY; i += KEY_FREQUENCY_INCREMENT, ++key) {
			KEY_FREQUENCIES.put(KEYS[key], Utils.Math.getKeyFrequency(i));
		}
	}

	public Polyphony(Oscillator[] oscillators) {
		this.oscillators = oscillators;
		for (int i = 0; i < VOICES; i++) {
			voices[i] = new Voice(oscillators);
		}
	}

	public void closeAll() {
		for (Voice voice : voices) {
			voice.audioThread.close();
		}
	}

	public synchronized void start(char keyChar) {
		if (KEY_FREQUENCIES.containsKey(keyChar) && getVoice(keyChar) == null) {
			for (Voice voice : voices) {
				AudioThread audioThread = voice.audioThread;
				if (!audioThread.isRunning()) {
					for (Oscillator oscillator : oscillators) {
						oscillator.setKeyFrequency(KEY_FREQUENCIES.get(keyChar));
					}
					voice.shouldGenerate.value = true;
					voice.key.value = keyChar;
					audioThread.triggerPlayback();
					break;
				}
			}
		}
	}

	public synchronized void stop(char keyChar) {
		Voice voice = getVoice(keyChar);
		if (voice != null) {
			voice.shouldGenerate.value = false;
			voice.key.value = ' ';
		}
	}

	private Voice getVoice(char key) {
		for (Voice voice : voices) {
			if (voice.key.value == key) {
				return voice;
			}
		}
		return null;
	}

	private static class Voice {
		private final ReferenceWrapper<Boolean> shouldGenerate = new ReferenceWrapper<>(false);
		private final ReferenceWrapper<Character> key = new ReferenceWrapper<>(' ');
		private final AudioThread audioThread;

		public Voice(Oscillator[] oscillators) {
			this.audioThread = new AudioThread(() -> {
				if (!shouldGenerate.value) {
					return null;
				} else {
					short[] s = new short[AudioThread.BUFFER_SIZE];
					int onOscillators = (int) Arrays.stream(oscillators).filter(SynthControlContainer::isOn).count();
					for (int i = 0; i < AudioThread.BUFFER_SIZE; i++) {
						double d = 0;
						for (Oscillator oscillator : oscillators) {
							if (oscillator.isOn()) {
								d += oscillator.getNextSample() / onOscillators;
							}
						}
						s[i] = (short) (Short.MAX_VALUE * d);
					}
					return s;
				}
			});
		}
	}
}
