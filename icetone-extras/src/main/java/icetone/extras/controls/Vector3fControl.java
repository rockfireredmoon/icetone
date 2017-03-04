package icetone.extras.controls;

import java.util.Objects;

import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.core.ElementManager;
import icetone.core.Form;
import icetone.core.Orientation;
import icetone.core.event.UIChangeEvent;
import icetone.core.layout.mig.MigLayout;

public class Vector3fControl extends AbstractVectorControl<Vector3f> {

	private final Vector3f val;
	private Spinner<Float> x;
	private Spinner<Float> y;
	private Spinner<Float> z;
	private Spinner<Float> a;
	private int precision = -1;

	public Vector3fControl(ElementManager<?> screen, float min, float max, float inc, Vector3f initial, boolean all) {
		this(screen, min, max, inc, initial, false, all);
	}

	public Vector3fControl(ElementManager<?> screen, float min, float max, float inc, Vector3f initial, boolean cycle,
			boolean all) {
		this(screen, null, min, max, inc, initial, cycle, all);
	}

	public Vector3fControl(ElementManager<?> screen, String styleId, float min, float max, float inc, Vector3f initial,
			boolean all) {
		this(screen, styleId, min, max, inc, initial, false, all);
	}

	public Vector3fControl(ElementManager<?> screen, String styleId, float min, float max, float inc, Vector3f initial,
			boolean cycle, boolean all) {
		super(screen, styleId);
		val = initial.clone();
		setLayoutManager(new MigLayout(screen, "ins 0", "[grow][]"));

		// all
		if (all) {
			a = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
			a.onChange(evt -> {
				if (!evt.getSource().isAdjusting()) {
					val.x = evt.getNewValue();
					x.runAdjusting(() -> x.setSelectedValueWithCallback(val.x));
					val.y = evt.getNewValue();
					y.runAdjusting(() -> y.setSelectedValueWithCallback(val.y));
					val.z = evt.getNewValue();
					z.runAdjusting(() -> z.setSelectedValueWithCallback(val.z));
				}
			});
			a.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.x));
			addElement(a);
			addElement(new Label("*", screen), "wrap");
		}

		// x
		x = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		x.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				Vector3f oldValue = val.clone();
				if (oldValue.x != evt.getNewValue()) {
					val.x = evt.getNewValue();
					setA();
					changeSupport.fireEvent(
							new UIChangeEvent<AbstractVectorControl<Vector3f>, Vector3f>(this, oldValue, getValue()));
				}
			}
		});
		x.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.x));
		addElement(x);
		addElement(new Label("X", screen), "wrap");

		// y
		y = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		y.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				Vector3f oldValue = val.clone();
				if (oldValue.y != evt.getNewValue()) {
					val.y = evt.getNewValue();
					setA();
					changeSupport.fireEvent(
							new UIChangeEvent<AbstractVectorControl<Vector3f>, Vector3f>(this, oldValue, getValue()));
				}
			}
		});
		y.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.y));
		addElement(y);
		addElement(new Label("Y", screen), "wrap");

		// y
		z = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		z.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {

				Vector3f oldValue = val.clone();
				if (oldValue.z != evt.getNewValue()) {
					val.z = evt.getNewValue();
					setA();
					changeSupport.fireEvent(
							new UIChangeEvent<AbstractVectorControl<Vector3f>, Vector3f>(this, oldValue, getValue()));
				}
			}
		});
		z.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.z));
		addElement(z);
		addElement(new Label("Z", screen), "wrap");

		//
		setInterval(5f);
	}

	public void addToForm(Form form) {
		if (a != null) {
			form.addFormElement(a);
		}
		form.addFormElement(x);
		form.addFormElement(y);
		form.addFormElement(z);
	}

	public void setInterval(float callsPerSeconds) {
		if (a != null) {
			a.setInterval(callsPerSeconds);
		}
		x.setInterval(callsPerSeconds);
		y.setInterval(callsPerSeconds);
		z.setInterval(callsPerSeconds);
	}

	public int getPrecision() {
		return precision;
	}

	public void setValue(Vector3f newValue) {
		Vector3f oldValue = this.val.clone();
		if (!Objects.equals(oldValue, newValue)) {
			this.val.set(newValue);
			this.x.setSelectedValueWithCallback(this.val.x);
			this.y.setSelectedValueWithCallback(this.val.y);
			this.z.setSelectedValueWithCallback(this.val.z);
			setA();
			changeSupport
					.fireEvent(new UIChangeEvent<AbstractVectorControl<Vector3f>, Vector3f>(this, oldValue, newValue));
		}
	}

	public void setPrecision(int precision) {
		this.precision = precision;
		((FloatRangeSpinnerModel) x.getSpinnerModel()).setPrecision(precision);
		((FloatRangeSpinnerModel) y.getSpinnerModel()).setPrecision(precision);
		((FloatRangeSpinnerModel) z.getSpinnerModel()).setPrecision(precision);
		if (a != null)
			((FloatRangeSpinnerModel) a.getSpinnerModel()).setPrecision(precision);
	}

	public Vector3f getValue() {
		return val;
	}

	private void setA() {
		if (a != null) {
			float avg = (val.x + val.y + val.z) / 2;
			a.setSelectedValue(avg);
		}
	}

	public void snapToIncr(boolean withCallback) {
		Vector3f oldValue = getValue().clone();

		x.runAdjusting(() -> ((FloatRangeSpinnerModel) x.getSpinnerModel()).snapToIncr());
		y.runAdjusting(() -> ((FloatRangeSpinnerModel) y.getSpinnerModel()).snapToIncr());
		z.runAdjusting(() -> ((FloatRangeSpinnerModel) z.getSpinnerModel()).snapToIncr());

		val.x = (Float) x.getSpinnerModel().getCurrentValue();
		val.y = (Float) y.getSpinnerModel().getCurrentValue();
		val.z = (Float) z.getSpinnerModel().getCurrentValue();

		if (!Objects.equals(oldValue, val)) {
			setA();
			changeSupport.fireEvent(
					new UIChangeEvent<AbstractVectorControl<Vector3f>, Vector3f>(this, oldValue, getValue()));
		}
	}
}
