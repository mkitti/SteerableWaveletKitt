package org.kittisopikul.wavelets.radial;

import net.imglib2.Cursor;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

public class ShannonWavelet extends RadialWavelet {
	public ShannonWavelet(double tx,double ty,double sr, double st) {
		super(tx,ty,sr,st);
	}
	public ShannonWavelet() {
		super();
	}
	public double radialFunction( final double f )
	{
		if(0.5 <= f && f <= 1)
			return 1;
		else
			return 0;
	}
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 512, 512 };
		final Img< FloatType > img = new ArrayImgFactory<>( new FloatType() ).create( dimensions );

		final RealRandomAccess< FloatType > rw = new ShannonWavelet(257,257,8.0/512.0,1);

		final double scale = 1;
		final double[] offset = new double[] { 0,0 };

		final Cursor< FloatType > cursor = img.localizingCursor();
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
