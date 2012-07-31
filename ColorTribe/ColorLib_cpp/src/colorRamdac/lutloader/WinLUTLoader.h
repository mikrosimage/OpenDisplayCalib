#ifndef WINLUTLOADER_H_
#define WINLUTLOADER_H_

#include "AbstractLutLoader.h"

class WinLUTLoader : public AbstractLutLoader
{
public:
	
	WinLUTLoader(){};
	virtual ~WinLUTLoader(){};
#ifdef __WIN32__ 
	int setGammaRamp(const int screenIndex, const LUT2DCorrection * const lut) const throw (CannotSetLutException);
	unsigned short* getGammaRamp(const int screenIndex, int * rampSize) const throw (CannotGetLutException);	
#else
	int setGammaRamp(const int screenIndex, const LUT2DCorrection * const lut) const throw (CannotSetLutException) =0;
	unsigned short* getGammaRamp(const int screenIndex , int * rampSize) const throw (CannotGetLutException) =0;
#endif
	
private:
	void* getScreenContext(const int screenIndex ) const throw (std::string);

};

#endif /*WINLUTLOADER_H_*/
