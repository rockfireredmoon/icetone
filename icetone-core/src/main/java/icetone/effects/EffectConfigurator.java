package icetone.effects;

public interface EffectConfigurator<E extends IEffect> {

	void configureEffect(E effect);
}
