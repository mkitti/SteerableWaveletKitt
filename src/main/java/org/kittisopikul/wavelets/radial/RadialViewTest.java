package org.kittisopikul.wavelets.radial;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.view.Views;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.DoubleType;

public class RadialViewTest {
	public static void main(String[] args)
	{
		final long[] dimensions = new long[] { 256, 256 };
		final Img< DoubleType > img = new ArrayImgFactory<>( new DoubleType() ).create( dimensions );

		final RealRandomAccess< DoubleType > rw = new VanGinkelWavelet(0,0,8.0/256.0,1);

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

		long[] min = new long[2];
		long[] max = dimensions;
		min[0] = 1-max[0];
		min[1] = 1-max[1];
		ImageJFunctions.show( img );
		ImageJFunctions.show( Views.interval( Views.expandMirrorSingle( img, 512,512 ),min,max) );
	}
}
