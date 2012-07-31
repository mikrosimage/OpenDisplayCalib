/* Inspired from Read-Edid (c) 2000,2001,2002 John Fremlin */
/* patched by mfe for Linux 64 - 2009 */

#ifdef __linux__
#include "NVGetEDID.h"
#include "EDIDData.h"
#include <string.h>

#include <nv_control.h>
#include <NvCtrlAttributes.h>
#include <iostream>
#include <sys/io.h>

using namespace std;

static char *display_device_name(int mask)
{
	switch (mask) {
		case (1 << 0): return "CRT-0"; break;
		case (1 << 1): return "CRT-1"; break;
		case (1 << 2): return "CRT-2"; break;
		case (1 << 3): return "CRT-3"; break;
		case (1 << 4): return "CRT-4"; break;
		case (1 << 5): return "CRT-5"; break;
		case (1 << 6): return "CRT-6"; break;
		case (1 << 7): return "CRT-7"; break;

		case (1 << 8): return "TV-0"; break;
		case (1 << 9): return "TV-1"; break;
		case (1 << 10): return "TV-2"; break;
		case (1 << 11): return "TV-3"; break;
		case (1 << 12): return "TV-4"; break;
		case (1 << 13): return "TV-5"; break;
		case (1 << 14): return "TV-6"; break;
		case (1 << 15): return "TV-7"; break;

		case (1 << 16): return "DFP-0"; break;
		case (1 << 17): return "DFP-1"; break;
		case (1 << 18): return "DFP-2"; break;
		case (1 << 19): return "DFP-3"; break;
		case (1 << 20): return "DFP-4"; break;
		case (1 << 21): return "DFP-5"; break;
		case (1 << 22): return "DFP-6"; break;
		case (1 << 23): return "DFP-7"; break;
		default: return "Unknown";
	}

}

int
NVGetEDID::getEDID(vector<EDIDData> &edidDataVector )
{

	int res = 0;
	Display *dpy;
	Bool ret;
	int screen, display_devices, mask;

	int nDisplayDevice;

	/*
	 * Open a display connection, and make sure the NV-CONTROL X
	 * extension is present on the screen we want to use.
	 */

	dpy = XOpenDisplay(NULL);
	if (!dpy) {
		return CANT_RETRIEVE_CONNECTED_DISPLAY;
	}

	screen = DefaultScreen(dpy);

	if (!XNVCTRLIsNvScreen(dpy, screen)) {
		return NOT_A_NV_SCREEN_ERROR;
	}

	//	ret = XNVCTRLQueryVersion(dpy, &major, &minor);
	//	if (ret != True) {
	//		return;
	//	}
	//
	//	printf("\nUsing NV-CONTROL extension %d.%d on %s\n",
	//			major, minor, XDisplayName(NULL));

	/*
	 * query the connected display devices on this X screen and print
	 * basic information about each X screen
	 */

	ret = XNVCTRLQueryAttribute(dpy, screen, 0,
			NV_CTRL_CONNECTED_DISPLAYS, &display_devices);

	if (!ret) {
		return NOT_A_NV_SCREEN_ERROR;
	}

	nDisplayDevice = 0;
	for (mask = 1; mask < (1 << 24); mask <<= 1) {

		if (display_devices & mask) {

			//char *displayDeviceNames[8];
			//			XNVCTRLQueryStringAttribute(dpy, screen, mask,
			//					NV_CTRL_STRING_DISPLAY_DEVICE_NAME,
			//					&str);
			//
			//			displayDeviceNames[nDisplayDevice++] = str;
			//
			//			//			printf("  %s (0x%08x): %s\n",
			//			//					display_device_name(mask), mask, str);
			//			printf("  (0x%08x): %s\n",
			//					mask, str);
			int edidAvalaible = 0;
			unsigned char *data = NULL;
			int len = 0;
			ret = XNVCTRLQueryAttribute(dpy, screen, mask, NV_CTRL_EDID_AVAILABLE, &edidAvalaible);
			if(ret && edidAvalaible) {
				ret = XNVCTRLQueryBinaryData (
						dpy,//Display *dpy
						screen,//int target_id, --> a priori c'est bien 0
						mask,//unsigned int display_mask,
						NV_CTRL_BINARY_DATA_EDID,
						&data,
						&len
				);

				EDIDData edid(data, len);
				if(edid.isCheckSumOk()) {
					edidDataVector.push_back(edid);
				}

			}

			else {
				continue;
			}

		}
	}

	//
	return res;
}

bool NVGetEDID::isGetEDIDEnable()
{

	Display *dpy;
	int screen;

	/*
	 * Open a display connection, and make sure the NV-CONTROL X
	 * extension is present on the screen we want to use.
	 */
	dpy = XOpenDisplay(NULL);
	if (!dpy) {
		return false;
	}

	screen = DefaultScreen(dpy);
	///
	if (!XNVCTRLIsNvScreen(dpy, screen)) {
		return false;
	}
	return true;

}

void NVGetEDID::dumpError(int error) {
	switch (error) {
		case SUCCESS:
		break;
		case OPEN_DISPLAY_ERROR:
		cout<<"Can't open display"<<endl;
		break;
		case NOT_A_NV_SCREEN_ERROR:
		cout<<"Not a NV screen"<<endl;
		break;
		case CANT_RETRIEVE_CONNECTED_DISPLAY:
		cout<<"Can't retrieve connected display"<<endl;
		break;

		default:
		cout<<"Unknown code : "<<error<<endl;
		break;
	}
}

int NVGetEDID::getNVControlPrimaryScreen(std::map<std::string,std::string> &maskToName) {

	///Test
	int res = 0;
	Display *dpy;
	Bool ret;
	int screen, display_devices, mask;
	char *str;
	char *displayDeviceNames[8];
	int nDisplayDevice;

	/*
	 * Open a display connection, and make sure the NV-CONTROL X
	 * extension is present on the screen we want to use.
	 */
	dpy = XOpenDisplay(NULL);
	if (!dpy) {
		return CANT_RETRIEVE_CONNECTED_DISPLAY;
	}

	screen = DefaultScreen(dpy);

	if (!XNVCTRLIsNvScreen(dpy, screen)) {
		return NOT_A_NV_SCREEN_ERROR;
	}

	ret = XNVCTRLQueryAttribute(dpy, screen, 0,
			NV_CTRL_CONNECTED_DISPLAYS, &display_devices);

	if (!ret) {
		return 1;
	}

	nDisplayDevice = 0;
	for (mask = 1; mask < (1 << 24); mask <<= 1) {

		if (display_devices & mask) {

			XNVCTRLQueryStringAttribute(dpy, screen, mask,
					NV_CTRL_STRING_DISPLAY_DEVICE_NAME,
					&str);
			displayDeviceNames[nDisplayDevice++] = str;
			maskToName.insert(std::make_pair(display_device_name(mask), str));

		}
	}

	return res;
}

#endif

