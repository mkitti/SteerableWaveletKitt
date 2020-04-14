package org.kittisopikul.wavelets.angular;

import org.kittisopikul.wavelets.PolarWavelet;

public class AngularWavelet extends PolarWavelet {
	
	protected double angularOffset = 0;
	
	public AngularWavelet(double tx,double ty,double sr, double st) {
		super(tx,ty,sr,st);
	}
	public AngularWavelet(double tx, double ty) {
		super(tx, ty);
	}
	public AngularWavelet() {
		super();
	}
	
	public void setAngularOffset(final double angularOffset) {
		this.angularOffset = angularOffset;
	}
	
	@Override
	public final float polarFunction( final float rc , final float theta) {
		return angularFunction(theta);
	}
	
	public float angularFunction( final float theta )
	{
		return theta;
	}

}
