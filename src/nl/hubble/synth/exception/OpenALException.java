package nl.hubble.synth.exception;

import static org.lwjgl.openal.AL10.*;

public class OpenALException extends RuntimeException {
	public OpenALException(int errorCode) {
		super(String.format("Internal %s OpenAL exception", errorCode == AL_INVALID_NAME ? "invalid name" : errorCode == AL_INVALID_ENUM ? "invalid enum" : errorCode == AL_INVALID_VALUE ? "invalid value" : errorCode == AL_INVALID_OPERATION ? "invalid operation" : "unknown"));
	}
}
