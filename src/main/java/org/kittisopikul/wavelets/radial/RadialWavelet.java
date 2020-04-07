/**
 * 
 */
package org.kittisopikul.wavelets.radial;

import net.imglib2.Cursor;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.realtransform.PolarToCartesianTransform2D;
import org.kittisopikul.wavelets.PolarWavelet;



/**
 * @author mkitti
 *
 */
public class RadialWavelet extends PolarWavelet {
	
	public RadialWavelet(double tx,double ty,double sr, double st) {
		super(tx,ty,sr,st);
	}
	public RadialWavelet() {
		super();
	}
	
	public double polarFunction( final double rc , final double theta) {
		return radialFunction(rc);
	}
	
	public double radialFunction( final double rc )
	{
		return rc;
	}
	
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 512, 512 };
		final Img< DoubleType > img = new ArrayImgFactory<>( new DoubleType() ).create( dimensions );

		final RealRandomAccess< DoubleType > rw = new RadialWavelet(257,257,1,0.5);

		final double scale = 1;
		final double[] offset = new double[] { 0,0 };

		final Cursor< DoubleType > cursor = img.localizingCursor();
		while( cursor.hasNext() )
		{
			cursor.fwd();
			for ( int d = 0; d < 2; ++d ) {
				rw.setPosition( scale * cursor.getIntPosition( d ) + offset[ d ], d );
				//System.out.println("RW: " + rw);
			}
			//System.out.println(rw.get());
			cursor.get().set( rw.get() );
		}

		ImageJFunctions.show( img );
	}


}
