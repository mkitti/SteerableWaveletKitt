package org.kittisopikul.wavelets.radial;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RealRandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class OctantCursor<T> implements Cursor<T> {
	
	private final double radius;

	private final int dimX;

	private final int dimY;
	
	private final boolean includeDiagonal;
	
	private final Cursor<T> parentCursor;
	
	private long fastForward = 0;
	
	public OctantCursor(final Cursor<T> parentCursor, final int dimX, final int dimY, final double radius, final boolean includeDiagonal) {
		this.parentCursor = parentCursor;
		this.dimX = dimX;
		this.dimY = dimY;
		this.radius = radius;
		this.includeDiagonal = includeDiagonal;
	}
	
	public OctantCursor(final Cursor<T> parentCursor, final int dimX, final int dimY, final double radius) {
		this(parentCursor,dimX,dimY,radius,true);
	}
	
	public OctantCursor(final Cursor<T> parentCursor, final int dimX, final int dimY, final boolean includeDiagonal) {
		this(parentCursor,dimX,dimY,Double.POSITIVE_INFINITY,includeDiagonal);
	}
	
	public OctantCursor(final Cursor<T> parentCursor, final int dimX, final int dimY) {
		this(parentCursor,dimX,dimY,Double.POSITIVE_INFINITY,true);
	}
	
	public OctantCursor(final Cursor<T> parentCursor, final double radius) {
		this(parentCursor,0,1,radius);
	}
	
	public OctantCursor(final Cursor<T> parentCursor) {
		this(parentCursor,0,1);
	}
	
	public boolean inOctant(final double positionX, final double positionY) {
		if(positionX < 0 || positionY < 0)
			return false;
		if(positionX == positionY)
			return includeDiagonal && (radius == Double.POSITIVE_INFINITY) ||  Math.sqrt(2*positionX*positionX) <= radius;
		else if(positionX < positionY)
			return (radius == Double.POSITIVE_INFINITY) || Math.sqrt(positionX*positionX + positionY*positionY) <= radius;
		else
			return false;
	}
	
	public boolean inOctant(final Cursor<T> c) {
		return inOctant(c.getDoublePosition(dimX),c.getDoublePosition(dimY));
	}

	@Override
	public void localize(float[] position) {
		// TODO Auto-generated method stub
		parentCursor.localize(position);
	}

	@Override
	public void localize(double[] position) {
		// TODO Auto-generated method stub
		parentCursor.localize(position);
	}

	@Override
	public float getFloatPosition(int d) {
		// TODO Auto-generated method stub
		return parentCursor.getFloatPosition(d);
	}

	@Override
	public double getDoublePosition(int d) {
		// TODO Auto-generated method stub
		return parentCursor.getDoublePosition(d);
	}

	@Override
	public int numDimensions() {
		// TODO Auto-generated method stub
		return parentCursor.numDimensions();
	}

	@Override
	public T get() {
		// TODO Auto-generated method stub
		return parentCursor.get();
	}

	@Override
	public Sampler<T> copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void jumpFwd(long steps) {
		// TODO Auto-generated method stub
		for(long i=0; i < steps; i++) {
			fwd();
		}
	}

	@Override
	public void fwd() {
		// TODO Auto-generated method stub
		if(fastForward > 0) {
			parentCursor.jumpFwd(fastForward);
			fastForward = 0;
		} else {
			parentCursor.fwd();
		}
		while(!inOctant(parentCursor)) {
			parentCursor.fwd();
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		parentCursor.reset();
		fastForward = 0;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		
		// Cached hasNext
		if(fastForward > 0)
			return true;
		if(parentCursor.hasNext()) {
			Cursor<T> testCursor = parentCursor.copyCursor();
			while(testCursor.hasNext()) {
				testCursor.fwd();
				fastForward++;
				if(inOctant(testCursor))
					return true;
			}
			fastForward = 0;
			return false;
		} else {
			fastForward = 0;
			return false;
		}
	}

	@Override
	public T next() {
		fwd();
		return get();
	}

	@Override
	public void localize(int[] position) {
		// TODO Auto-generated method stub
		parentCursor.localize(position);
	}

	@Override
	public void localize(long[] position) {
		// TODO Auto-generated method stub
		parentCursor.localize(position);
	}

	@Override
	public int getIntPosition(int d) {
		// TODO Auto-generated method stub
		return parentCursor.getIntPosition(d);
	}

	@Override
	public long getLongPosition(int d) {
		// TODO Auto-generated method stub
		return parentCursor.getLongPosition(d);
	}

	@Override
	public OctantCursor<T> copyCursor() {
		// TODO Auto-generated method stub
		return new OctantCursor<T>(parentCursor.copyCursor(),dimX,dimY,radius,includeDiagonal);
	}
	
	public OctantCursor<T> diagonalMirror() {
		//swap X and Y, and invert includeDiagonal
		Cursor<T> cur = parentCursor.copyCursor();
		cur.reset();
		return new OctantCursor<T>(cur,dimY,dimX,radius,!includeDiagonal);
	}
	public static void main( final String[] args )
	{
		final int[] dimensions = new int[] { 281, 281 };
		final Img< FloatType > img = new ArrayImgFactory<>( new FloatType() ).create( dimensions );

		final RealRandomAccess< FloatType > rw = new RadialWavelet(0,0,1,0.5);

		final double scale = 1;
		final double[] offset = new double[] { 0,0 };

		OctantCursor< FloatType > cursor = new OctantCursor< FloatType >(img.localizingCursor(),281.0);
		while( cursor.hasNext() )
		{
			cursor.fwd();
			for ( int d = 0; d < 2; ++d ) {
				rw.setPosition( scale * cursor.getIntPosition( d ) + offset[ d ], d );
			}
			cursor.get().set( rw.get() );
		}
		ImageJFunctions.show(img);
	}

}
