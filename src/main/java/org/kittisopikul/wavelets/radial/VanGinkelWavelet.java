package org.kittisopikul.wavelets.radial;

import net.imglib2.Cursor;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.DoubleType;

public class VanGinkelWavelet extends RadialWavelet {
	public VanGinkelWavelet(double tx,double ty,double sr, double st) {
		super(tx,ty,sr,st);
	}
	public VanGinkelWavelet() {
		super();
	}
	public double radialFunction( final double f )
	{
		return f*Math.exp(-f*f/2-1/2);
	}
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 512, 512 };
		final Img< DoubleType > img = new ArrayImgFactory<>( new DoubleType() ).create( dimensions );

		final RealRandomAccess< DoubleType > rw = new VanGinkelWavelet(257,257,8.0/512.0,1);

		final double scale = 1;
		final double[] offset = new double[] { 0,0 };

		final Cursor< DoubleType > cursor = img.localizingCursor();
		while( cursor.hasNext() )
		{
			cursor.fwd();
			for ( int d = 0; d < 2; ++d ) {
				rw.setPosition( scale * cursor.getIntPosition( d ) + offset[ d ], d );
			}
			cursor.get().set( rw.get() );
		}

		ImageJFunctions.show( img );
	}
}
