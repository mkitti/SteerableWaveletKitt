package org.kittisopikul.wavelets;

import org.kittisopikul.wavelets.angular.AngularGaussianWavelet;
import org.kittisopikul.wavelets.angular.AngularWavelet;
import org.kittisopikul.wavelets.angular.CosineWavelet;
import org.kittisopikul.wavelets.angular.SineWavelet;
import org.kittisopikul.wavelets.radial.RadialWavelet;
import org.kittisopikul.wavelets.radial.SimoncelliWavelet;
import org.kittisopikul.wavelets.radial.VanGinkelWavelet;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.algorithm.fft2.FFT;
import net.imglib2.algorithm.fft2.FFTConvolution;
import net.imglib2.algorithm.fft2.FFTMethods;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.InvertibleRealTransform;
import net.imglib2.realtransform.ScaledPolarToTranslatedCartesianTransform2D;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import net.imglib2.converter.ComplexPowerFloatConverter;
import net.imglib2.converter.ComplexPhaseFloatConverter;
import net.imglib2.converter.ComplexImaginaryFloatConverter;
import net.imglib2.converter.ComplexRealFloatConverter;
import net.imglib2.converter.ComplexPowerGLogFloatConverter;
import net.imglib2.converter.Converters;
import net.imglib2.converter.readwrite.SamplerConverter;

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
		final int[] dimensions = new int[] {281, 281};
		final Img< DoubleType > img = new ArrayImgFactory<>( new DoubleType() ).create( dimensions );

		final AngularWavelet aw = new AngularGaussianWavelet(0.5/3.0);
		//final RadialWavelet rw = new SimoncelliWavelet(0,0,8.0/512.0,2);
		final RadialWavelet rw = new VanGinkelWavelet(0,0,16.0/256.0,1);
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
		

		long[] min = {0,0};
		long[] max = {559,559};
		ImageJFunctions.show( img , "Image");
		//ImageJFunctions.show( Views.interval( Views.expandMirrorSingle( img, 511,511 ),min,max) );
		ImageJFunctions.show( Views.interval( Views.extendMirrorSingle( img ),min,max), "Mirrored");
		final ImgFactory<ComplexFloatType> fftFactory = img.factory().imgFactory( new ComplexFloatType() );
		final Img<ComplexFloatType> img_hat = FFT.realToComplex(Views.interval( Views.extendMirrorSingle( img ),min,max),fftFactory);
		
		final ComplexRealFloatConverter<ComplexFloatType> realConv = new ComplexRealFloatConverter<ComplexFloatType>();
		final ComplexImaginaryFloatConverter<ComplexFloatType> imagConv = new ComplexImaginaryFloatConverter<ComplexFloatType>(); 
		
		final ComplexPowerFloatConverter<ComplexFloatType> powerConv = new ComplexPowerFloatConverter<ComplexFloatType>();
		
		final ComplexPhaseFloatConverter<ComplexFloatType> phaseConv = new ComplexPhaseFloatConverter<ComplexFloatType>(); 
		
		ImageJFunctions.show( img_hat , "Image Hat");
		
		
		ImageJFunctions.show( Converters.convert((RandomAccessibleInterval<ComplexFloatType>)img_hat, powerConv,new FloatType()) , "Power");
		ImageJFunctions.show( Converters.convert((RandomAccessibleInterval<ComplexFloatType>)img_hat, realConv,new FloatType()) , "Real");

		ImageJFunctions.show( Converters.convert((RandomAccessibleInterval<ComplexFloatType>)img_hat, imagConv,new FloatType()) , "Imag");
		ImageJFunctions.show( Converters.convert((RandomAccessibleInterval<ComplexFloatType>)img_hat, phaseConv,new FloatType()) , "Phase");
		
		RandomAccessibleInterval<FloatType> real_img =  Converters.convert((RandomAccessibleInterval<ComplexFloatType>)img_hat, realConv,new FloatType());
		
		min[0] = -280;
		min[1] = -280;
		max[0] = 279;
		max[1] = 279;
		ImageJFunctions.show( Views.interval( Views.extendMirrorSingle(real_img), min,max) , "Real Mirror");

	}
}
