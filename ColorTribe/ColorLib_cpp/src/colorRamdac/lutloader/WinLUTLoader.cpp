#include "WinLUTLoader.h"
#include <iostream>
#include "../../calibration/correction/LUT2DCorrection.h"

//////Windows API
#ifdef __WIN32__ 
#include <windows.h>
#include <wingdi.h>

//////
using namespace std;

int WinLUTLoader::setGammaRamp(const int screenIndex,
		const LUT2DCorrection * const lut) const throw (CannotSetLutException) {
	int rampSize = lut->getRampSize();
	if (rampSize != 256)
		throw CannotSetLutException("Ramp size != 256.");
	unsigned short * ramp = lut->get16BitsLUT();
	HDC *ref= NULL;
	try {
		ref = (HDC*)getScreenContext(screenIndex);
	} catch(string s) {
		throw CannotSetLutException(s);
	}
	unsigned short gammaArray[3][rampSize];
	for (int i = 0; i < 256; i++) {
		gammaArray[0][i] = ramp[i*3];
		gammaArray[1][i] = ramp[i*3+1];
		gammaArray[2][i] = ramp[i*3+2];
	}

	bool res = SetDeviceGammaRamp(*ref, gammaArray);

	if (res == false)
		throw CannotSetLutException("SetDeviceGammaRamp returned false.");
	delete ramp;
	return 0;
}

unsigned short* WinLUTLoader::getGammaRamp(int screenIndex, int * rampSize) const
		throw (CannotGetLutException) {

	unsigned short *tmplut = new unsigned short[3*256];
	HDC *ref= NULL;
	try {
		ref = (HDC*)getScreenContext(screenIndex);
	} catch(string s) {
		throw CannotGetLutException(s);
	}
	bool ret = GetDeviceGammaRamp( *ref, tmplut);
	unsigned short *lut = new unsigned short[3*256];
	for (int i = 0; i < 256; i++) {
		lut[i*3] = tmplut[i];
		lut[i*3+1] = tmplut[i+256];
		lut[i*3+2] = tmplut[i+256*2];
	}
	if (ret == false) {
		throw CannotGetLutException("GetDeviceGammaRamp returned false.");
	}
	*rampSize=256;
	return lut;
}

static const int screenNumberMax = 4;
BOOL CALLBACK EnumMonitorsProc(HMONITOR hmon, HDC /*hdc*/, LPRECT /*prect*/,
		LPARAM dwData) {
	HDC *refs = reinterpret_cast<HDC*>(dwData);
	static int count=0;

	if (count < (screenNumberMax-1)) {
		MONITORINFOEX mix;
		memset(&mix, 0, sizeof(MONITORINFOEX));
		mix.cbSize = sizeof(MONITORINFOEX);
		if ( GetMonitorInfo(hmon, &mix)) {
			refs[count++] = CreateDC("DISPLAY"/*NULL*/, mix.szDevice, NULL, NULL );
		}

	}
	return TRUE;
}

void* WinLUTLoader::getScreenContext(const int screenIndex) const
		throw (string) {
	static HDC refs[screenNumberMax];
	BOOL res = EnumDisplayMonitors( NULL, NULL, EnumMonitorsProc, reinterpret_cast<LPARAM>(refs));
	if (res==FALSE)
		throw string("EnumDisplayMonitor failed.");
	return (void*)(refs+screenIndex);
}

#endif
