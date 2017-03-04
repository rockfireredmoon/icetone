package icetone.effects;

import java.util.List;

import icetone.core.BaseElement;

public interface IEffect extends Comparable<IEffect> {
	
	List<IEffect> getEffects();
	
	EffectChannel getChannel();
	
	boolean isConflict(IEffect effect);

	boolean getIsActive();
	
	int getPriority();

	void update(float tpf);

	IEffect setEffectManager(EffectManager effectManager);

	IEffect setIsActive(boolean b);

	IEffect setElement(BaseElement element);

	BaseElement getElement();

	IEffect setChannel(EffectChannel channel);

}
