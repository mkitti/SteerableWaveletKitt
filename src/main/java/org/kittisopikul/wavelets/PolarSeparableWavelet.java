package org.kittisopikul.wavelets;

import org.kittisopikul.wavelets.angular.AngularGaussianWavelet;
import org.kittisopikul.wavelets.angular.AngularWavelet;
import org.kittisopikul.wavelets.angular.CosineWavelet;
import org.kittisopikul.wavelets.angular.SineWavelet;
import org.kittisopikul.wavelets.radial.RadialWavelet;
import org.kittisopikul.wavelets.radial.SimoncelliWavelet;
import org.kittisopikul.wavelets.radial.VanGinkelWavelet;

import net.imglib2.Cursor;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.InvertibleRealTransform;
import net.imglib2.realtransform.ScaledPolarToTranslatedCartesianTransform2D;
import net.imglib2.type.numeric.real.DoubleType;

public class PolarSeparableWavelet extends PolarWavelet {
	
	final RadialWavelet rw;
	final AngularWavelet aw;

	protected PolarSeparableWavelet(InvertibleRealTransform transform,RadialWavelet rw,AngularWavelet aw) {
		super(transform);
		this.rw = rw;
		this.aw = aw;
	}
	public PolarSeparableWavelet(RadialWavelet rw,AngularWavelet aw) {
		this(rw.transform,rw,aw);
	}
	public PolarSeparableWavelet(double tx,double ty,double sr, double st, RadialWavelet rw, AngularWavelet aw) {
		this(new ScaledPolarToTranslatedCartesianTransform2D(tx,ty,sr,st), rw, aw);
	}
	public PolarSeparableWavelet(double tx,double ty,RadialWavelet rw, AngularWavelet aw) {
		this(tx,ty,1.0,1.0, rw, aw);
	}
	
	@Override
	public DoubleType get() {
		transform.applyInverse(polarCoords, this);
		t.set(polarFunction(polarCoords));
		return t;
	}
	
	public double polarFunction( final double rc , final double theta)
	{
		return rw.radialFunction(rc)*aw.angularFunction(theta);
	}
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 512, 512 };
		final Img< DoubleType > img = new ArrayImgFactory<>( new DoubleType() ).create( dimensions );

		final AngularWavelet aw = new AngularGaussianWavelet(1.0/3.0);
		final RadialWavelet rw = new SimoncelliWavelet(257,257,8.0/512.0,2);
		final RealRandomAccess< DoubleType > psw = new PolarSeparableWavelet(rw,aw);

		final double scale = 1;
		final double[] offset = new double[] { 0,0 };

		final Cursor< DoubleType > cursor = img.localizingCursor();
		while( cursor.hasNext() )
		{
			cursor.fwd();
			for ( int d = 0; d < 2; ++d ) {
				psw.setPosition( scale * cursor.getIntPosition( d ) + offset[ d ], d );
			}
			cursor.get().set( psw.get() );
		}

		ImageJFunctions.show( img );
	}
}
