package wulcan;

public class Color32 {
	private final double r;
	private final double g;
	private final double b;
	private final double a;

	public Color32(final double r, final double g, final double b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1;
	}

	public Color32(final double r, final double g, final double b, final double a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color32 shade(final double x) {
		return new Color32(this.r * x, this.g * x, this. b * x, this.a);
	}

	public double getR() {
		return this.r;
	}

	public double getG() {
		return this.g;
	}

	public double getB() {
		return this.b;
	}

	public double getA() {
		return this.a;
	}
	
}
