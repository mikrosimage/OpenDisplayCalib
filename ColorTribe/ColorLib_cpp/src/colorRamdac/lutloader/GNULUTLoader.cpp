#include "GNULUTLoader.h"
#include "../../calibration/correction/LUT2DCorrection.h"
#include <iostream>

//////Windows API
#ifdef __linux__
# include <X11/Xos.h>
# include <X11/Xlib.h>
# include <X11/Xutil.h>
# include <X11/extensions/xf86vmode.h>
# include <linux/types.h>

int GNULUTLoader::setGammaRamp(const int screenIndex,
		const LUT2DCorrection * const lut) const throw (CannotSetLutException) {
	//	Bool XF86VidModeGetGammaRamp(
	//	    Display*                    /* dpy */,
	//	    int                         /* screen */,
	//	    int                         /* size */,
	//	    unsigned short*             /* red array */,
	//	    unsigned short*             /* green array */,
	//	    unsigned short*             /* blue array */
	//	);

	Display* dpy = XOpenDisplay(NULL);//prend la valeur par defaut

	int *cardRampSize = new int;
	bool res2;
	try {
		res2 = XF86VidModeGetGammaRampSize(dpy, screenIndex, cardRampSize);
	} catch(...) {
		res2 =false;
	}
	if (res2 == false)
	{
		throw CannotGetLutException("XF86VidModeGetGammaRampSize returned false.");
	}

	//int rampSize = lut->getRampSize();

	//printf("card size : %d, lut size : %d \n", *cardRampSize, rampSize);

	unsigned short * ramp = lut->get16BitsLUT(*cardRampSize);

	unsigned short gammaArray[3][*cardRampSize];
	for (int i = 0; i < *cardRampSize; i++) {
		gammaArray[0][i] = ramp[i*3];
		gammaArray[1][i] = ramp[i*3+1];
		gammaArray[2][i] = ramp[i*3+2];
	}

	bool res = false;
	try {
		res = XF86VidModeSetGammaRamp(dpy, screenIndex, *cardRampSize,
				gammaArray[0], gammaArray[1], gammaArray[2]);
	} catch(...) {
		res =false;
	}

	//	XCloseDisplay(dpy);
	delete ramp;
	if (res == false) {
		throw CannotSetLutException("XF86VidModeSetGammaRamp returned false.");
	}

	XCloseDisplay(dpy) ;
	return 0;
}

bool GNULUTLoader::isScreenGammaAble(const unsigned int screenIndex){
	Display* dpy = XOpenDisplay(NULL);
	bool res;
	int rampSize = -1;
	try {
		res = XF86VidModeGetGammaRampSize(dpy, screenIndex, &rampSize);
	} catch(...) {
		res=false;
	}

	if(rampSize<=0)
		res=false;

	XCloseDisplay(dpy) ;

	return res;

}

unsigned short* GNULUTLoader::getGammaRamp(const int screenIndex, int * rampSize) const
throw (CannotGetLutException) {

	Display* dpy = XOpenDisplay(NULL);//prend la valeur par defaut


	bool res;
	try {
		res = XF86VidModeGetGammaRampSize(dpy, screenIndex, rampSize);
	} catch(...) {
		res=false;
	}

	if (res == false)
	{
		throw CannotGetLutException("XF86VidModeGetGammaRampSize returned false.");
	}

	unsigned short * r_ramp = new unsigned short[*rampSize];
	unsigned short * g_ramp = new unsigned short[*rampSize];
	unsigned short * b_ramp = new unsigned short[*rampSize];

	XF86VidModeGetGammaRamp(dpy, screenIndex, *rampSize, r_ramp, g_ramp, b_ramp);
	if (res == false)
	{
		throw CannotGetLutException("XF86VidModeGetGammaRamp returned false.");
	}
	unsigned short*lut = new unsigned short[*rampSize*3];
	for (int i = 0; i < *rampSize; i++) {
		lut[i*3] = r_ramp[i];
		lut[i*3+1] = g_ramp[i];
		lut[i*3+2] = b_ramp[i];

	}

	XCloseDisplay(dpy) ;
	//printf("last value : %d %d %d", r_ramp[(*rampSize) - 1], g_ramp[(*rampSize) - 1], g_ramp[(*rampSize) - 1]);
	delete r_ramp;
	delete g_ramp;
	delete b_ramp;

	return lut;
}
#endif
