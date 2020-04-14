/**
 * 
 */
package org.kittisopikul.wavelets.radial;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.realtransform.PolarToCartesianTransform2D;
import org.kittisopikul.wavelets.PolarWavelet;

import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;



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
	
	public float polarFunction( final float rc , final float theta) {
		return radialFunction(rc);
	}
	
	public float radialFunction( final float rc )
	{
		return rc;
	}
	
	public static void main( final String[] args )
	{
		final long[] dimensions = new long[] { 560, 560 };
		final Img< FloatType > img = new ArrayImgFactory<>( new FloatType() ).create( dimensions );
		final RealRandomAccess< FloatType > rw = new RadialWavelet(0,0,1,0.5);

		// Fill first octant
		final long[] origin = new long[] {279, 279};
		IterableInterval<FloatType> centeredView = Views.offset(img, origin);
		OctantCursor< FloatType > cursor = new OctantCursor< FloatType >(centeredView.localizingCursor());
		while( cursor.hasNext() )
		{
			cursor.fwd();
			for ( int d = 0; d < 2; ++d ) {
				rw.setPosition( cursor.getIntPosition(d), d );
			}
			cursor.get().set( rw.get() );
		}
		
		ImageJFunctions.show( img , "First Octant");
		
		// Fill second octant to make a quadrant
		cursor = cursor.diagonalMirror();
		RandomAccess< FloatType > ra = Views.moveAxis(Views.offset(img, origin), 0, 1).randomAccess();
		while( cursor.hasNext() )
		{
			cursor.fwd();
			ra.setPosition(cursor);
			cursor.get().set( ra.get() );
		}
		
		ImageJFunctions.show( img , "Quadrant");
		
		// Crop to the quadrant
		IntervalView<FloatType> quadrant = Views.interval(img, origin, new long[] {dimensions[0]-1,dimensions[1]-1} );
		// Extend the quadrant with mirroring
		RandomAccessible<FloatType> mirroredQuadrant = Views.extendMirrorSingle(quadrant);
		
		// Get the quadrant centered in the middle of the image
		RandomAccessibleInterval<FloatType> fullCentered = Views.interval(mirroredQuadrant, img);
		ImageJFunctions.show( fullCentered , "Centered");
		
		// Get the quadrant with origin in the top left
		RandomAccessibleInterval<FloatType> topLeftOrigin = Views.interval(mirroredQuadrant, centeredView);
		ImageJFunctions.show( topLeftOrigin , "Top Left Origin");

		// Copy the centered mirrored quadrant into the image
		// Origin might be offset by a few pixels, might need to translate
		Cursor<FloatType> imgCursor = img.localizingCursor();
		ra = fullCentered.randomAccess();
		while( imgCursor.hasNext() )
		{
			imgCursor.fwd();
			ra.setPosition(imgCursor);
			imgCursor.get().set( ra.get() );
		}

		ImageJFunctions.show( img , "Img");
	}


}
