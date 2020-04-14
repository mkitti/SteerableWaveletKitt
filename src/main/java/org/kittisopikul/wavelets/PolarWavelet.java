/**
 * 
 */
package org.kittisopikul.wavelets;

import net.imglib2.Cursor;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.realtransform.InvertibleRealTransform;
import net.imglib2.realtransform.ScaledPolarToTranslatedCartesianTransform2D;


/**
 * @author mkitti
 *
 */
public class PolarWavelet extends RealPoint implements RealRandomAccess<FloatType> {
	
	final FloatType t;
	protected final InvertibleRealTransform transform;
	final RealPoint polarCoords;

	protected PolarWavelet(InvertibleRealTransform transform) {
		super(2);
		this.transform = transform;
		this.t = new FloatType();
		this.polarCoords = new RealPoint(2);
	}
	public PolarWavelet(double tx,double ty,double sr, double st) {
		this(new ScaledPolarToTranslatedCartesianTransform2D(tx,ty,sr,st));
	}
	public PolarWavelet(double tx,double ty) {
		this(tx,ty,1.0,1.0);
	}
	public PolarWavelet() {
		this(0.0,0.0,1.0,1.0);
	}
	
	
	@Override
	public FloatType get() {
		transform.applyInverse(polarCoords, this);
		t.set(polarFunction(polarCoords));
		return t;
	}

	@Override
	public PolarWavelet copy() {
		final PolarWavelet a = new PolarWavelet();
		a.setPosition( this );
		return a;
	}

	@Override
	public PolarWavelet copyRealRandomAccess() {
		return copy();
	}
	
	public final float polarFunction( final RealLocalizable polarCoords)
	{
		return polarFunction(polarCoords.getFloatPosition(0),polarCoords.getFloatPosition(1));
	}
	
	public float polarFunction( final float rc , final float theta)
	{
		//System.out.println("radialFunction: " + rc);
		return rc*theta;
	}
	
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 512, 512 };
		final Img< FloatType > img = new ArrayImgFactory<>( new FloatType() ).create( dimensions );

		final RealRandomAccess< FloatType > rw = new PolarWavelet(257,257);

		//final double scale = 0.000125;
		//final double[] offset = new double[] { -1.3875, 0.045 };

		final double scale = 1;
		final double[] offset = new double[] { 0,0 };

		final Cursor< FloatType > cursor = img.localizingCursor();
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
