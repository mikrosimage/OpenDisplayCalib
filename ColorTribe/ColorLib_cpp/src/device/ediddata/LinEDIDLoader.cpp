/*
 * LinEDIDLoader.cpp
 *
 *  Created on: 23 janv. 2009
 *      Author: mfe
 */

#include "LinEDIDLoader.h"

#ifdef __linux__

#include "EDIDData.h"
#include "LRMIGetEDID.h"
#include "NVGetEDID.h"
#include <vector>
#include <iostream>

# include <X11/Xos.h>
# include <X11/Xlib.h>
# include <X11/Xutil.h>
# include <X11/extensions/xf86vmode.h>

using namespace std;

int LinEDIDLoader::loadEDID(std::vector<EDIDData> &edidVector) {

	int error=0;
	if(NVGetEDID::isGetEDIDEnable())
		error = NVGetEDID::getInstance().getEDID(edidVector);
	else if( LRMIGetEDID::isGetEDIDEnable())
		error = LRMIGetEDID::getInstance().getEDID(edidVector);
	return error;
}
#endif
