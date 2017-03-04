package icetone.effects;

public class PlayAudioEffect extends AbstractEffect {
	
	private String uri;
	private float volume = 1f;

	public PlayAudioEffect(String uri, float volume) {
		this.uri = uri;
		this.volume = volume;
	}

	@Override
	public boolean isConflict(IEffect effect) {
		return false;
	}

	public String getUri() {
		return uri;
	}

	public PlayAudioEffect setUri(String uri) {
		this.uri = uri;
		return this;
	}

	public float getVolume() {
		return volume;
	}

	public PlayAudioEffect setVolume(float volume) {
		this.volume = volume;
		return this;
	}

	@Override
	public void update(float tpf) {
		if (effectManager.getScreen().getUseUIAudio()) {
			effectManager.getScreen().playAudioNode(uri, volume);
		}
		setIsActive(false);
	}

}
