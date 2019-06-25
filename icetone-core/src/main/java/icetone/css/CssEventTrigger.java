package icetone.css;

import icetone.effects.EffectConfigurator;
import icetone.effects.IEffect;

public class CssEventTrigger<E extends IEffect> {

	private CssEvent event;
	private EffectConfigurator<E> configurator;
	private boolean processed;

	public CssEventTrigger(CssEvent event) {
		this(event, null);
	}

	public CssEventTrigger(CssEvent event, EffectConfigurator<E> configurator) {
		this.event = event;
		this.configurator = configurator;
	}

	public CssEvent getEvent() {
		return event;
	}

	public EffectConfigurator<E> getConfigurator() {
		return configurator;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CssEventTrigger other = (CssEventTrigger) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		return true;
	}

}
