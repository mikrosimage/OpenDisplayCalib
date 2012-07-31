#ifndef GNULUTLOADER_H_
#define GNULUTLOADER_H_

#include "AbstractLutLoader.h"

class GNULUTLoader : public AbstractLutLoader {
public:
	GNULUTLoader(){};
	virtual ~GNULUTLoader(){};
#ifdef __linux__	
	int
			setGammaRamp(const int screenIndex,
					const LUT2DCorrection * const lut) const throw (CannotSetLutException);
	unsigned short* getGammaRamp(const int screenIndex, int * rampSize) const throw (CannotGetLutException);
#else
	int setGammaRamp(const int screenIndex, const LUT2DCorrection * const lut) const throw (CannotSetLutException) =0;
	unsigned short* getGammaRamp(const int screenIndex, int * rampSize) const throw (CannotGetLutException) =0;
#endif

};

#endif /*GNULUTLOADER_H_*/
