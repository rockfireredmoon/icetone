package icetone.extras.controls;

import java.util.Objects;

import com.jme3.math.Vector4f;

import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.Form;
import icetone.core.Orientation;
import icetone.core.event.UIChangeEvent;
import icetone.core.layout.mig.MigLayout;

public class Vector4fControl extends AbstractVectorControl<Vector4f> {

	public enum Type {
		VECTOR, RGBA
	}

	private final Vector4f val;
	private Spinner<Float> x;
	private Spinner<Float> y;
	private Spinner<Float> z;
	private Spinner<Float> w;
	private Spinner<Float> a;
	private int precision = -1;
	private Label xLabel;
	private Label yLabel;
	private Label zLabel;
	private Label wLabel;

	public Vector4fControl(float min, float max, float inc, Vector4f initial, boolean all) {
		this(BaseScreen.get(), min, max, inc, initial, all);
	}

	public Vector4fControl(BaseScreen screen, float min, float max, float inc, Vector4f initial, boolean all) {
		this(screen, min, max, inc, initial, false, all);
	}

	public Vector4fControl(float min, float max, float inc, Vector4f initial, boolean cycle,
			boolean all) {
		this(BaseScreen.get(), min, max, inc, initial, cycle, all);
	}

	public Vector4fControl(BaseScreen screen, float min, float max, float inc, Vector4f initial, boolean cycle,
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
					val.z = evt.getNewValue();
					z.runAdjusting(() -> z.setSelectedValueWithCallback(val.z));
					val.w = evt.getNewValue();
					w.runAdjusting(() -> w.setSelectedValueWithCallback(val.w));
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
				Vector4f oldValue = val.clone();
				if (oldValue.x != evt.getNewValue()) {
					val.x = evt.getNewValue();
					setA();
					changeSupport.fireEvent(
							new UIChangeEvent<AbstractVectorControl<Vector4f>, Vector4f>(this, oldValue, getValue()));
				}
			}
		});
		x.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.x));
		addElement(x);
		addElement(xLabel = new Label(screen, "X"), "wrap");

		// y
		y = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		y.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				Vector4f oldValue = val.clone();
				if (oldValue.y != evt.getNewValue()) {
					val.y = evt.getNewValue();
					setA();
					changeSupport.fireEvent(
							new UIChangeEvent<AbstractVectorControl<Vector4f>, Vector4f>(this, oldValue, getValue()));
				}
			}
		});
		y.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.y));
		addElement(y);
		addElement(yLabel = new Label(screen, "Y"), "wrap");

		// y
		z = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		z.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {

				Vector4f oldValue = val.clone();
				if (oldValue.z != evt.getNewValue()) {
					val.z = evt.getNewValue();
					setA();
					changeSupport.fireEvent(
							new UIChangeEvent<AbstractVectorControl<Vector4f>, Vector4f>(this, oldValue, getValue()));
				}
			}
		});
		z.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.z));
		addElement(z);
		addElement(zLabel = new Label(screen, "Z"), "wrap");

		// y
		w = new Spinner<Float>(screen, Orientation.HORIZONTAL, cycle);
		w.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {

				Vector4f oldValue = val.clone();
				if (oldValue.w != evt.getNewValue()) {
					val.w = evt.getNewValue();
					setA();
					changeSupport.fireEvent(
							new UIChangeEvent<AbstractVectorControl<Vector4f>, Vector4f>(this, oldValue, getValue()));
				}
			}
		});
		w.setSpinnerModel(new FloatRangeSpinnerModel(min, max, inc, val.z));
		addElement(w);
		addElement(wLabel = new Label(screen, "W"), "wrap");

		//
		setInterval(5f);
	}

	public void setType(Type type) {
		switch (type) {
		case RGBA:
			xLabel.setText("R");
			yLabel.setText("G");
			zLabel.setText("B");
			wLabel.setText("A");
			break;
		case VECTOR:
			xLabel.setText("X");
			yLabel.setText("Y");
			zLabel.setText("Z");
			wLabel.setText("W");
			break;
		}
	}

	public void setPrecision(int precision) {
		this.precision = precision;
		((FloatRangeSpinnerModel) x.getSpinnerModel()).setPrecision(precision);
		((FloatRangeSpinnerModel) y.getSpinnerModel()).setPrecision(precision);
		((FloatRangeSpinnerModel) z.getSpinnerModel()).setPrecision(precision);
		((FloatRangeSpinnerModel) w.getSpinnerModel()).setPrecision(precision);
		if (a != null)
			((FloatRangeSpinnerModel) a.getSpinnerModel()).setPrecision(precision);
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
		form.addFormElement(z);
		form.addFormElement(w);
	}

	public void setInterval(float callsPerSeconds) {
		if (a != null) {
			a.setInterval(callsPerSeconds);
		}
		x.setInterval(callsPerSeconds);
		y.setInterval(callsPerSeconds);
		z.setInterval(callsPerSeconds);
		w.setInterval(callsPerSeconds);
	}

	public void setValue(Vector4f newValue) {
		Vector4f oldValue = this.val.clone();
		if (!Objects.equals(oldValue, newValue)) {
			this.val.set(newValue);
			this.x.setSelectedValueWithCallback(this.val.x);
			this.y.setSelectedValueWithCallback(this.val.y);
			this.z.setSelectedValueWithCallback(this.val.z);
			this.w.setSelectedValueWithCallback(this.val.w);
			setA();
			changeSupport
					.fireEvent(new UIChangeEvent<AbstractVectorControl<Vector4f>, Vector4f>(this, oldValue, newValue));
		}
	}

	public Vector4f getValue() {
		return val;
	}

	private void setA() {
		if (a != null) {
			float avg = (val.x + val.y + val.z) / 2;
			a.setSelectedValue(avg);
		}
	}

	public void snapToIncr(boolean withCallback) {
		Vector4f oldValue = getValue().clone();

		x.runAdjusting(() -> ((FloatRangeSpinnerModel) x.getSpinnerModel()).snapToIncr());
		y.runAdjusting(() -> ((FloatRangeSpinnerModel) y.getSpinnerModel()).snapToIncr());
		z.runAdjusting(() -> ((FloatRangeSpinnerModel) z.getSpinnerModel()).snapToIncr());
		w.runAdjusting(() -> ((FloatRangeSpinnerModel) z.getSpinnerModel()).snapToIncr());

		val.x = (Float) x.getSpinnerModel().getCurrentValue();
		val.y = (Float) y.getSpinnerModel().getCurrentValue();
		val.z = (Float) z.getSpinnerModel().getCurrentValue();
		val.w = (Float) z.getSpinnerModel().getCurrentValue();

		if (!Objects.equals(oldValue, val)) {
			setA();
			changeSupport.fireEvent(
					new UIChangeEvent<AbstractVectorControl<Vector4f>, Vector4f>(this, oldValue, getValue()));
		}
	}
}
