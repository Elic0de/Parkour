package elicode.parkour.util.tuplet;

public class Quintuple<F, S, T, FO, FI> extends Quadruple<F, S, T, FO> {

	public final FI fifth;

	public Quintuple(F first, S second, T third, FO fourth, FI fifth){
		super(first, second, third, fourth);
		this.fifth = fifth;
	}

}
