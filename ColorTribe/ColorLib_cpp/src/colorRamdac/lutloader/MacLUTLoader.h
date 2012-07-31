#ifndef MACLUTLOADER_H_
#define MACLUTLOADER_H_

#include "AbstractLutLoader.h"
#include <iostream>



class MacLUTLoader : public AbstractLutLoader {
public:
	MacLUTLoader(){};
	virtual ~MacLUTLoader(){};
#ifdef __APPLE__	
	int
			setGammaRamp(const int screenIndex,
					const LUT2DCorrection * const lut) const throw (CannotSetLutException);
	unsigned short* getGammaRamp(const int screenIndex, int * rampSize) const throw (CannotGetLutException);
#else
	int setGammaRamp(const int screenIndex, const LUT2DCorrection * const lut) const throw (CannotSetLutException) =0;
	unsigned short* getGammaRamp(const int screenIndex, int * rampSize) const throw (CannotGetLutException) =0;
#endif

private:
	void* getScreenContext(const int screenIndex ) const;
	static std::string getErrorMessage(int err);
};

#endif /*MACLUTLOADER_H_*/
