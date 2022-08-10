package nl.hubble.synth.controls;

import nl.hubble.synth.Synthesizer;

import javax.swing.*;
import java.awt.*;

public abstract class SynthControlContainer extends JPanel {
	private Point mouseClickLocation;
	private final Synthesizer synthesizer;
	private boolean on = true;

	public SynthControlContainer(Synthesizer synthesizer) {
		this.synthesizer = synthesizer;
	}

	public Point getMouseClickLocation() {
		return mouseClickLocation;
	}

	public void setMouseClickLocation(Point mouseClickLocation) {
		this.mouseClickLocation = mouseClickLocation;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	@Override
	public Component add(Component comp) {
		comp.addKeyListener(synthesizer.getKeyAdapter());
		return super.add(comp);
	}

	@Override
	public Component add(String name, Component comp) {
		comp.addKeyListener(synthesizer.getKeyAdapter());
		return super.add(name, comp);
	}

	@Override
	public Component add(Component comp, int index) {
		comp.addKeyListener(synthesizer.getKeyAdapter());
		return super.add(comp, index);
	}

	@Override
	public void add(Component comp, Object constraints) {
		comp.addKeyListener(synthesizer.getKeyAdapter());
		super.add(comp, constraints);
	}

	@Override
	public void add(Component comp, Object constraints, int index) {
		comp.addKeyListener(synthesizer.getKeyAdapter());
		super.add(comp, constraints, index);
	}
}
