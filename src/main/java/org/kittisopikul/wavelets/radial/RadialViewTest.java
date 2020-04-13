package org.kittisopikul.wavelets.radial;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.algorithm.fft2.FFT;
import net.imglib2.converter.ComplexImaginaryFloatConverter;
import net.imglib2.converter.ComplexPhaseFloatConverter;
import net.imglib2.converter.ComplexPowerFloatConverter;
import net.imglib2.converter.ComplexRealFloatConverter;
import net.imglib2.converter.Converters;
import net.imglib2.view.Views;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class RadialViewTest {
	public static void main(String[] args)
	{
		final long[] dimensions = new long[] { 281, 281 };
		final Img< DoubleType > img = new ArrayImgFactory<>( new DoubleType() ).create( dimensions );

		final RealRandomAccess< DoubleType > rw = new VanGinkelWavelet(0,0,16.0/256.0,1);

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

		//long[] min = new long[2];
		//long[] max = dimensions;
		//min[0] = 1-max[0];
		//min[1] = 1-max[1];
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
		
		min[0] = -230;
		min[1] = -230;
		max[0] = 229;
		max[1] = 229;
		ImageJFunctions.show( Views.interval( Views.extendMirrorSingle(real_img), min,max) , "Real Mirror");

	}
}
