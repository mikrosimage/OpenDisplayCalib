#include "MacLUTLoader.h"
#include <iostream>
#include "../../calibration/correction/LUT2DCorrection.h"

//MacOS API
#ifdef __APPLE__
using namespace std;
#include <Carbon/Carbon.h>
#include <ApplicationServices/ApplicationServices.h>

typedef struct osXScreenRef {
	CGDirectDisplayID *activeDspys;
	CGDisplayCount dspyCnt;
	int numScr;
} osXScreenRef;

int MacLUTLoader::setGammaRamp(const int screenIndex,
		const LUT2DCorrection * const lut) const throw (CannotSetLutException) {
	const unsigned short * const ramp = lut->get16BitsLUT();
	int rampSize = lut->getRampSize();
	CGGammaValue values[3*rampSize];
	CGDisplayErr err;
	unsigned int i;
	for (i=0; i<256; i++) {
		values[i] = (float)ramp[i*3] / 65535.f;
		values[i+rampSize] = (float)ramp[i*3+1] / 65535.f;
		values[i+rampSize*2] = (float)ramp[i*3+2] / 65535.f;
	}

	osXScreenRef* ref =(osXScreenRef*) getScreenContext(screenIndex);

	err = CGSetDisplayTransferByTable(ref->activeDspys[ref->numScr],
			rampSize, values, values+rampSize, values+rampSize*2);

	if (err!=kCGErrorSuccess) {
		string message = "CGSetDisplayTransferByTable failed : ";
		message.append(getErrorMessage(err));
		throw CannotSetLutException(message);
	}

	return 0;
}

static const int screenNumberMax = 10;

void* MacLUTLoader::getScreenContext(const int screenIndex) const {
	osXScreenRef *ref = (osXScreenRef*)calloc( 1, sizeof(osXScreenRef));
	if (ref) {
		// Lists all active displays.
		ref->activeDspys = (CGDirectDisplayID*)calloc(screenNumberMax,
				sizeof(CGDirectDisplayID));
		ref->dspyCnt = 0;
		/*CGDisplayErr err =*/CGGetActiveDisplayList(screenNumberMax,
				ref->activeDspys, &(ref->dspyCnt));
		ref->numScr = screenIndex;
	}
	return (void*)ref;
}

unsigned short* MacLUTLoader::getGammaRamp(int screenIndex, int * rampSize) const
		throw (CannotGetLutException) {

	CGGammaValue values[256*3];
	CGDisplayErr err;
	CGTableCount samples;
	unsigned short*lut = new unsigned short[256*3];

	// Gets the display transfer tables.
	// capacity indicates the number of samples each array can hold.
	// sampleCount is filled in with the number of samples actually copied in.
	err = CGGetDisplayTransferByTable((CGDirectDisplayID) screenIndex, 256,
			values, values+256, values+512, &samples);
	if (err!=kCGErrorSuccess) {
		string message = "CGSetDisplayTransferByTable failed : ";
		message.append(getErrorMessage(err));
		throw CannotGetLutException(message);
	} else if (samples != 256) {
		//	throw CannotGetLutException("Error : interpolation is not yet implemented !");
		/// pas sure du code ci-dessous : corrigé sans pouvoir tester
		// Il va falloir interpoler les valeurs
		int i=0, j;
		float  u;
		for (i=0; i<256; i++) {
			u = ( (float)i / 255.f ) * (samples-1);
			j = (int)(u );
			u -= j;
			if (u < 0.00001f)
				lut[i*3] = (unsigned short)(values[j] * 65535.f + .5f );
			else
				lut[i*3] = (unsigned short)( (values[j] + (values[j+1]-values[j])
						* u )* 65535.f + .5f );
		}
		for (i=0; i<256; i++) {
			u = ( (float)i / 255.f ) * (samples-1);
			j = (int)(u );
			u -= j;
			if (u < 0.00001f)
				lut[i*3+1] = (unsigned short)(values[256+j] * 65535.f + .5f );
			else
				lut[i*3+1] = (unsigned short)( (values[256+j] + (values[256+j+1]
						-values[256+j]) * u )* 65535.f + .5f );
		}
		for (i=0; i<256; i++) {
			u = ( (float)i / 255.f ) * (samples-1);
			j = (int)(u );
			u -= j;
			if (u < 0.00001f)
				lut[i*3+2] = (unsigned short)(values[512+j] * 65535.f + .5f );
			else
				lut[i*3+2] = (unsigned short)( (values[512+j] + (values[512+j+1]
						-values[512+j]) * u )* 65535.f + .5f );
		}
	} else {
		for (int i=0; i<256; i++) {
			lut[i*3] = (unsigned short)(values[i] * 65535.f + .5f );
			lut[i*3+1] = (unsigned short)(values[i+256] * 65535.f + .5f );
			lut[i*3+2] = (unsigned short)(values[i+512] * 65535.f + .5f );
		}
	}
	*rampSize = 256;
	return lut;
}

string MacLUTLoader::getErrorMessage(int err) {
	switch (err) {
	case kCGErrorSuccess:
		return "no error";
	case kCGErrorFailure:
		return "A general failure occurred.";
	case kCGErrorIllegalArgument:
		return "One or more of the parameters passed to a function are invalid. Check for NULL pointers.";
	case kCGErrorInvalidConnection:
		return "The parameter representing a connection to the window server is invalid.";
	case kCGErrorInvalidContext:
		return "The CPSProcessSerNum or context identifier parameter is not valid.";
	case kCGErrorCannotComplete:
		return "The requested operation is inappropriate for the parameters passed in, or the current system state.";
	case kCGErrorNameTooLong:
		return "A parameter, typically a C string, is too long to be used without truncation.";
	case kCGErrorNotImplemented:
		return "Return value from obsolete function stubs present for binary compatibility, but not normally called.";
	case kCGErrorRangeCheck:
		return "A parameter passed in has a value that is inappropriate, or which does not map to a useful operation or value.";
	case kCGErrorTypeCheck:
		return "A data type or token was encountered that did not match the expected type or token.";
	case kCGErrorNoCurrentPoint:
		return "An operation relative to a known point or coordinate could not be done, as there is no known point.";
	case kCGErrorInvalidOperation:
		return "The requested operation is not valid for the parameters passed in, or the current system state.";
	case kCGErrorNoneAvailable:
		return "The requested operation could not be completed as the indicated resources were not found.";
	default:
		return "unknown message";

	}
	return "";
}

#endif

