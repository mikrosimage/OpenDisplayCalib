#ifndef ABSTRACTLUTLOADER_H_
#define ABSTRACTLUTLOADER_H_

#include "../../exception/CannotSetLutException.h"
#include "../../exception/CannotGetLutException.h"

class LUT2DCorrection;

class AbstractLutLoader
{
public:
	AbstractLutLoader();
	virtual ~AbstractLutLoader();

	virtual int setGammaRamp(const int screenIndex, const LUT2DCorrection * const lut) const throw (CannotSetLutException) =0;
	virtual unsigned short* getGammaRamp(const int screenIndex, int * RampSampleCount) const throw (CannotGetLutException) =0;

};

#endif /*ABSTRACTLUTLOADER_H_*/
