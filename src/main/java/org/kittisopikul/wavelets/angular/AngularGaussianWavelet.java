package org.kittisopikul.wavelets.angular;

import java.lang.Math;

import net.imglib2.Cursor;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

public class AngularGaussianWavelet extends AngularWavelet {
	
	final double sigma2;
	
	public AngularGaussianWavelet(double tx,double ty,double sr, double st, double sigma) {
		super(tx,ty,sr,st);
		this.sigma2 = sigma*sigma;
	}
	public AngularGaussianWavelet(double tx,double ty, double sigma) {
		super(tx,ty);
		this.sigma2 = sigma*sigma;
	}
	public AngularGaussianWavelet(double sigma) {
		super();
		this.sigma2 = sigma*sigma;
	}
	public AngularGaussianWavelet() {
		this(1);
	}
	
	@Override
	public float angularFunction( final float theta )
	{
		//System.out.println("radialFunction: " + rc);
		double theta_p = (theta+Math.PI+angularOffset) % (2*Math.PI);
		if(theta_p < 0)
			theta_p = theta_p + Math.PI*2;
		theta_p = theta_p - Math.PI;
		return (float) Math.exp(-theta_p*theta_p/2/sigma2);
	}
	
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 560, 560 };
		final Img< FloatType > img = new ArrayImgFactory<>( new FloatType() ).create( dimensions );

		final RealRandomAccess< FloatType > aw = new AngularGaussianWavelet(281,281,1,2,0.5);
		((AngularWavelet) aw).setAngularOffset(Math.PI/2*0.6);

		final double scale = 1;
		final double[] offset = new double[] { 0,0 };

		final Cursor< FloatType > cursor = img.localizingCursor();
		while( cursor.hasNext() )
		{
			cursor.fwd();
			for ( int d = 0; d < 2; ++d ) {
				aw.setPosition( scale * cursor.getIntPosition( d ) + offset[ d ], d );
			}
			cursor.get().set( aw.get() );
		}

		ImageJFunctions.show( img );
	}

}
