package elicode.parkour.util.tuplet;

public class Quadruple<F, S, T, FO> extends Triple<F, S, T> {

	public final FO fourth;

	public Quadruple(F first, S second, T third, FO fourth){
		super(first, second, third);
		this.fourth = fourth;
	}

}
