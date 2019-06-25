/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package icetone.effects;

import java.util.HashMap;
import java.util.Map;

import com.jme3.math.FastMath;

/**
 * Borrowed from LibGdx
 * @author Nathan Sweet
 */
public abstract class Interpolation {

	private final static Map<String, Interpolation> interpolations = new HashMap<>();
	
	abstract public float apply (float a);
	public float apply (float start, float end, float a) {
		return start + (end - start) * apply(a);
	}

	public static final Interpolation linear = new Interpolation("linear") {
		@Override
		public float apply (float a) {
			return a;
		}
	};
	public static final Interpolation fade = new Interpolation("fade") {
		@Override
		public float apply (float a) {
			return FastMath.clamp(a * a * a * (a * (a * 6 - 15) + 10), 0, 1);
		}
	};
	public static final Interpolation circle = new Interpolation("circle") {
		@Override
		public float apply (float a) {
			if (a <= 0.5f) {
				a *= 2;
				return (1 - FastMath.sqrt(1 - a * a)) / 2;
			}
			a--;
			a *= 2;
			return (FastMath.sqrt(1 - a * a) + 1) / 2;
		}
	};
	public static final Interpolation circleIn = new Interpolation("circleIn") {
		@Override
		public float apply (float a) {
			return 1 - FastMath.sqrt(1 - a * a);
		}
	};
	public static final Interpolation circleOut = new Interpolation("circleOut") {
		@Override
		public float apply (float a) {
			a--;
			return FastMath.sqrt(1 - a * a);
		}
	};
	public static final Interpolation sine = new Interpolation("sine") {
		@Override
		public float apply (float a) {
			return (1 - FastMath.cos(a * FastMath.PI)) / 2;
		}
	};
	public static final Interpolation sineIn = new Interpolation("sineIn") {
		@Override
		public float apply (float a) {
			return 1 - FastMath.cos(a * FastMath.PI / 2);
		}
	};
	public static final Interpolation sineOut = new Interpolation("sineOut") {
		@Override
		public float apply (float a) {
			return FastMath.sin(a * FastMath.PI / 2);
		}
	};
	public static final Interpolation exp10 = new Exp(2, 10, "exp10");
	public static final Interpolation exp10In = new ExpIn(2, 10, "exp10In");
	public static final Interpolation exp10Out = new ExpOut(2, 10, "exp10Out");
	public static final Interpolation exp5 = new Exp(2, 5, "exp5");
	public static final Interpolation exp5In = new ExpIn(2, 5, "exp5In");
	public static final Interpolation exp5Out = new ExpOut(2, 5, "exp5Out");
	public static final Elastic elastic = new Elastic(2, 10, "elastic");
	public static final Elastic elasticIn = new ElasticIn(2, 10, "elasticIn");
	public static final Elastic elasticOut = new ElasticOut(2, 10, "elasticOut");
	public static final Interpolation swing = new Swing(1.5f, "swing");
	public static final Interpolation swingIn = new SwingIn(2f, "swingIn");
	public static final Interpolation swingOut = new SwingOut(2f, "swingOut");
	public static final Interpolation bounce = new Bounce(4, "bounce");
	public static final Interpolation bounceIn = new BounceIn(4, "bounceIn");
	public static final Interpolation bounceOut = new BounceOut(4, "bounceOut");
	public static final Pow pow2 = new Pow(2, "pow2");
	public static final PowIn pow2In = new PowIn(2, "pow2In");
	public static final PowOut pow2Out = new PowOut(2, "pow2Out");
	public static final Pow pow3 = new Pow(3, "pow3");
	public static final PowIn pow3In = new PowIn(3, "pow3In");
	public static final PowOut pow3Out = new PowOut(3, "pow3Out");
	public static final Pow pow4 = new Pow(4, "pow4");
	public static final PowIn pow4In = new PowIn(4, "pow4In");
	public static final PowOut pow4Out = new PowOut(4, "pow4Out");
	public static final Pow pow5 = new Pow(5, "pow5");
	public static final PowIn pow5In = new PowIn(5, "pow5In");
	public static final PowOut pow5Out = new PowOut(5, "pow5Out");
	
	public static class Pow extends Interpolation {
		final int power;

		public Pow (int power, String name) {
			super(name);
			this.power = power;
		}

		@Override
		public float apply (float a) {
			if (a <= 0.5f) return FastMath.pow(a * 2, power) / 2;
			return FastMath.pow((a - 1) * 2, power) / (power % 2 == 0 ? -2 : 2) + 1;
		}
	}
	public static class PowIn extends Pow {
		public PowIn (int power, String name) {
			super(power, name);
		}

		@Override
		public float apply (float a) {
			return FastMath.pow(a, power);
		}
	}
	public static class PowOut extends Pow {
		public PowOut (int power, String name) {
			super(power, name);
		}

		@Override
		public float apply (float a) {
			return FastMath.pow(a - 1, power) * (power % 2 == 0 ? -1 : 1) + 1;
		}
	}
	public static class Exp extends Interpolation {
		final float value, power, min, scale;

		public Exp (float value, float power, String name) {
			super(name);
			this.value = value;
			this.power = power;
			min = (float)Math.pow(value, -power);
			scale = 1 / (1 - min);
		}

		@Override
		public float apply (float a) {
			if (a <= 0.5f) return (FastMath.pow(value, power * (a * 2 - 1)) - min) * scale / 2;
			return (2 - (FastMath.pow(value, -power * (a * 2 - 1)) - min) * scale) / 2;
		}
	};
	public static class ExpIn extends Exp {
		public ExpIn (float value, float power, String name) {
			super(value, power, name);
		}

		@Override
		public float apply (float a) {
			return (FastMath.pow(value, power * (a - 1)) - min) * scale;
		}
	}
	public static class ExpOut extends Exp {
		public ExpOut (float value, float power, String name) {
			super(value, power, name);
		}

		@Override
		public float apply (float a) {
			return 1 - (FastMath.pow(value, -power * a) - min) * scale;
		}
	}
	public static class Elastic extends Interpolation {
		final float value, power;

		public Elastic (float value, float power, String name) {
			super(name);
			this.value = value;
			this.power = power;
		}

		@Override
		public float apply (float a) {
			if (a <= 0.5f) {
				a *= 2;
				return FastMath.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f / 2;
			}
			a = 1 - a;
			a *= 2;
			return 1 - (float)Math.pow(value, power * (a - 1)) * FastMath.sin((a) * 20) * 1.0955f / 2;
		}
	}
	public static class ElasticIn extends Elastic {
		public ElasticIn (float value, float power, String name) {
			super(value, power, name);
		}

		@Override
		public float apply (float a) {
			return FastMath.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f;
		}
	}
	public static class ElasticOut extends Elastic {
		public ElasticOut (float value, float power, String name) {
			super(value, power, name);
		}

		@Override
		public float apply (float a) {
			a = 1 - a;
			return (1 - FastMath.pow(value, power * (a - 1)) * FastMath.sin(a * 20) * 1.0955f);
		}
	}
	public static class Bounce extends BounceOut {
		public Bounce (float[] widths, float[] heights, String name) {
			super(widths, heights, name);
		}

		public Bounce (int bounces, String name) {
			super(bounces, name);
		}

		private float out (float a) {
			float test = a + widths[0] / 2;
			if (test < widths[0]) return test / (widths[0] / 2) - 1;
			return super.apply(a);
		}

		@Override
		public float apply (float a) {
			if (a <= 0.5f) return (1 - out(1 - a * 2)) / 2;
			return out(a * 2 - 1) / 2 + 0.5f;
		}
	}
	public static class BounceOut extends Interpolation {
		final float[] widths, heights;

		public BounceOut (float[] widths, float[] heights, String name) {
			super(name);
			if (widths.length != heights.length)
				throw new IllegalArgumentException("Must be the same number of widths and heights.");
			this.widths = widths;
			this.heights = heights;
		}

		public BounceOut (int bounces, String name) {
			super(name);
			if (bounces < 2 || bounces > 5) throw new IllegalArgumentException("bounces cannot be < 2 or > 5: " + bounces);
			widths = new float[bounces];
			heights = new float[bounces];
			heights[0] = 1;
			switch (bounces) {
			case 2:
				widths[0] = 0.6f;
				widths[1] = 0.4f;
				heights[1] = 0.33f;
				break;
			case 3:
				widths[0] = 0.4f;
				widths[1] = 0.4f;
				widths[2] = 0.2f;
				heights[1] = 0.33f;
				heights[2] = 0.1f;
				break;
			case 4:
				widths[0] = 0.34f;
				widths[1] = 0.34f;
				widths[2] = 0.2f;
				widths[3] = 0.15f;
				heights[1] = 0.26f;
				heights[2] = 0.11f;
				heights[3] = 0.03f;
				break;
			case 5:
				widths[0] = 0.3f;
				widths[1] = 0.3f;
				widths[2] = 0.2f;
				widths[3] = 0.1f;
				widths[4] = 0.1f;
				heights[1] = 0.45f;
				heights[2] = 0.3f;
				heights[3] = 0.15f;
				heights[4] = 0.06f;
				break;
			}
			widths[0] *= 2;
		}

		@Override
		public float apply (float a) {
			a += widths[0] / 2;
			float width = 0, height = 0;
			for (int i = 0, n = widths.length; i < n; i++) {
				width = widths[i];
				if (a <= width) {
					height = heights[i];
					break;
				}
				a -= width;
			}
			a /= width;
			float z = 4 / width * height * a;
			return 1 - (z - z * a) * width;
		}
	}
	public static class BounceIn extends BounceOut {
		public BounceIn (float[] widths, float[] heights, String name) {
			super(widths, heights, name);
		}

		public BounceIn (int bounces, String name) {
			super(bounces, name);
		}

		@Override
		public float apply (float a) {
			return 1 - super.apply(1 - a);
		}
	}
	public static class Swing extends Interpolation {
		private final float scale;

		public Swing (float scale, String name) {
			super(name);
			this.scale = scale * 2;
		}

		@Override
		public float apply (float a) {
			if (a <= 0.5f) {
				a *= 2;
				return a * a * ((scale + 1) * a - scale) / 2;
			}
			a--;
			a *= 2;
			return a * a * ((scale + 1) * a + scale) / 2 + 1;
		}
	}
	public static class SwingOut extends Interpolation {
		private final float scale;

		public SwingOut (float scale, String name) {
			super(name);
			this.scale = scale;
		}

		@Override
		public float apply (float a) {
			a--;
			return a * a * ((scale + 1) * a + scale) + 1;
		}
	}
	public static class SwingIn extends Interpolation {
		private final float scale;

		public SwingIn (float scale, String name) {
			super(name);
			this.scale = scale;
		}

		@Override
		public float apply (float a) {
			return a * a * ((scale + 1) * a - scale);
		}
	}
	
	private Interpolation(String name) {
		interpolations.put(name, this);
	}
	
	public static Interpolation fromName(String name) {
		return interpolations.get(name);
	}
}
