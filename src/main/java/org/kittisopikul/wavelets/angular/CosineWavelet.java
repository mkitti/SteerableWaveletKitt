package org.kittisopikul.wavelets.angular;

import java.lang.Math;

import net.imglib2.Cursor;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

public class CosineWavelet extends AngularWavelet {
	
	public CosineWavelet(double tx,double ty,double sr, double st) {
		super(tx,ty,sr,st);
	}
	public CosineWavelet(double tx,double ty) {
		super(tx,ty);
	}
	public CosineWavelet() {
		super();
	}
	
	@Override
	public float angularFunction( final float theta )
	{
		return (float)Math.cos(theta+angularOffset);
	}
	
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 512, 512 };
		final Img< FloatType > img = new ArrayImgFactory<>( new FloatType() ).create( dimensions );

		final RealRandomAccess< FloatType > rw = new CosineWavelet(257,257,1,2);

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
