package icetone.extras.controls;

import java.util.Objects;

import com.jme3.math.Vector2f;

import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.Form;
import icetone.core.Orientation;
import icetone.core.Screen;
import icetone.core.event.UIChangeEvent;
import icetone.core.layout.mig.MigLayout;

public class Vector2fControl extends AbstractVectorControl<Vector2f> {

	private final Vector2f val;
	private Spinner<Float> x;
	private Spinner<Float> y;
	private Spinner<Float> a;
	private int precision = -1;

	public Vector2fControl() {
		this(Screen.get());
	}

	public Vector2fControl(BaseScreen screen) {
		this(screen, Float.MIN_VALUE, Float.MAX_VALUE, 1, Vector2f.ZERO, true);
	}

	public Vector2fControl(float min, float max, float inc, Vector2f initial, boolean all) {
		this(Screen.get(), min, max, inc, initial, true, all);
	}
	
	public Vector2fControl(BaseScreen screen, float min, float max, float inc, Vector2f initial, boolean all) {
		this(screen, min, max, inc, initial, false, all);
	}

	public Vector2fControl(BaseScreen screen, float min, float max, float inc, Vector2f initial, boolean cycle,
			boolean all) {
		super(screen);
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
				}
			});
			a.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.x));
			addElement(a);
			addElement(new Label(screen, "*"), "wrap");
		}

		// x
		x = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		x.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				Vector2f oldValue = val.clone();
				if (oldValue.x != evt.getNewValue()) {
					val.x = evt.getNewValue();
					setA();
					changeSupport.fireEvent(
							new UIChangeEvent<AbstractVectorControl<Vector2f>, Vector2f>(this, oldValue, getValue()));
				}
			}
		});
		x.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.x));
		addElement(x);
		addElement(new Label(screen, "X"), "wrap");

		// y
		y = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		y.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				Vector2f oldValue = val.clone();
				if (oldValue.y != evt.getNewValue()) {
					val.y = evt.getNewValue();
					setA();
					changeSupport.fireEvent(
							new UIChangeEvent<AbstractVectorControl<Vector2f>, Vector2f>(this, oldValue, getValue()));
				}
			}
		});
		y.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.y));
		addElement(y);
		addElement(new Label(screen, "Y"), "wrap");

		//
		setInterval(5f);
	}

	public int getPrecision() {
		return precision;
	}

	public void addToForm(Form form) {
		if (a != null) {
			form.addFormElement(a);
		}
		form.addFormElement(x);
		form.addFormElement(y);
	}

	public void setPrecision(int precision) {
		this.precision = precision;
		((FloatRangeSpinnerModel) x.getSpinnerModel()).setPrecision(precision);
		((FloatRangeSpinnerModel) y.getSpinnerModel()).setPrecision(precision);
		if (a != null)
			((FloatRangeSpinnerModel) a.getSpinnerModel()).setPrecision(precision);
	}

	public void setInterval(float callsPerSeconds) {
		if (a != null) {
			a.setInterval(callsPerSeconds);
		}
		x.setInterval(callsPerSeconds);
		y.setInterval(callsPerSeconds);
	}

	public void setValue(Vector2f newValue) {
		Vector2f oldValue = this.val.clone();
		if (!Objects.equals(oldValue, newValue)) {
			this.val.set(newValue);
			this.x.setSelectedValueWithCallback(this.val.x);
			this.y.setSelectedValueWithCallback(this.val.y);
			setA();
			changeSupport
					.fireEvent(new UIChangeEvent<AbstractVectorControl<Vector2f>, Vector2f>(this, oldValue, newValue));
		}
	}

	public Vector2f getValue() {
		return val;
	}

	private void setA() {
		if (a != null) {
			float avg = (val.x + val.y) / 2;
			a.setSelectedValue(avg);
		}
	}

	public void snapToIncr(boolean withCallback) {
		Vector2f oldValue = getValue().clone();

		x.runAdjusting(() -> ((FloatRangeSpinnerModel) x.getSpinnerModel()).snapToIncr());
		y.runAdjusting(() -> ((FloatRangeSpinnerModel) y.getSpinnerModel()).snapToIncr());

		val.x = (Float) x.getSpinnerModel().getCurrentValue();
		val.y = (Float) y.getSpinnerModel().getCurrentValue();

		if (!Objects.equals(oldValue, val)) {
			setA();
			changeSupport.fireEvent(
					new UIChangeEvent<AbstractVectorControl<Vector2f>, Vector2f>(this, oldValue, getValue()));
		}
	}
}
