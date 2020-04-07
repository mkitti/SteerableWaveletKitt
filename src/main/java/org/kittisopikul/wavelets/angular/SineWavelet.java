package org.kittisopikul.wavelets.angular;

import java.lang.Math;

import net.imglib2.Cursor;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.DoubleType;

public class SineWavelet extends AngularWavelet {
	
	public SineWavelet(double tx,double ty,double sr, double st) {
		super(tx,ty,sr,st);
	}
	public SineWavelet(double tx,double ty) {
		super(tx,ty);
	}
	public SineWavelet() {
		super();
	}
	
	@Override
	public double angularFunction( final double theta )
	{
		return Math.sin(theta+angularOffset);
	}
	
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 512, 512 };
		final Img< DoubleType > img = new ArrayImgFactory<>( new DoubleType() ).create( dimensions );

		final RealRandomAccess< DoubleType > rw = new SineWavelet(257,257,1,2);

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
