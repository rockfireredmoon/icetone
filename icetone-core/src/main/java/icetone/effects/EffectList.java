package icetone.effects;

import java.util.Arrays;
import java.util.LinkedList;

public class EffectList extends AbstractListEffect<LinkedList<IEffect>> {

	private IEffect currentEffect;

	public EffectList(IEffect... effects) {
		super(new LinkedList<>());
		this.effects.addAll(Arrays.asList(effects));
	}

	@Override
	public void update(float tpf) {
		if (isActive) {
			if (currentEffect == null && !effects.isEmpty()) {
				currentEffect = effects.getFirst();
			}
			if (currentEffect == null)
				isActive = false;
			else {
				if (!currentEffect.getIsActive()) {
					effects.removeFirst();
					currentEffect = null;
				} else
					currentEffect.update(tpf);
			}
		}
	}

}
