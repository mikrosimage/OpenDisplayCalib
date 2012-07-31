/*
 * LocalHost.cpp
 *
 *  Created on: 24 fevr. 2009
 *      Author: mfe
 */

#include "LocalHost.h"
using namespace std;

#include "DisplayDeviceScout.h"

#include <iostream>
#include <sstream>
#include <stdlib.h>

#ifdef __WIN32__
#include <windows.h>
#endif

#ifdef __linux__
#include "ediddata/NVGetEDID.h"
# include <X11/Xos.h>
# include <X11/Xlib.h>
# include <X11/Xutil.h>
# include <X11/extensions/xf86vmode.h>
#include <nv_control.h>
#include <NvCtrlAttributes.h>
#include <map>

#endif

LocalHost::LocalHost() :
	_hostName(""), _userName(""), _domainName("") {
	DeviceScout* scout = DeviceScout::Instance();
	scout->getDisplayDeviceList(_displayDevices);
	scout->getHostName(_hostName, _domainName);
	scout->getUserName(_userName);


#ifdef __linux__
	Display* dpy = XOpenDisplay(NULL);//prend la valeur par defaut
	_XScreenNumber = ScreenCount(dpy);
	NVGetEDID::getNVControlPrimaryScreen(_maskToName);
#else
	_XScreenNumber = _displayDevices.size();
#endif
}

const unsigned char& LocalHost::getXScreensNumber() const {
	return _XScreenNumber;
}

LocalHost::~LocalHost() {
}

void LocalHost::printInfo() const {

	cout << "***** Local host : " << _hostName <<" Domain : " << _domainName << " *****" << endl << endl;

	vector<DisplayDevice>::const_iterator it;
	cout << "--------- Display Devices ---------" << endl << endl;
	int count = 0;
	for (it = _displayDevices.begin(); it != _displayDevices.end(); it++) {
		count++;
		cout << "------ Display " << count << endl;
		(*it).printInfo();
		cout << endl;
	}
}



void LocalHost::setScreenEnvironnementVariables() const {
	vector<DisplayDevice>::const_iterator it;
	for (it = _displayDevices.begin(); it != _displayDevices.end(); it++) {
		int index = (*it).getOSIndex();
		ostringstream varName;
		varName << "SCREEN" << index;
		ostringstream cmd;
		cmd << (*it).getManufacturerName() << "_" << (*it).getModelName();
		//system(os.str().c_str());
#ifdef __WIN32__
		if (! SetEnvironmentVariable(varName.str().c_str(), cmd.str().c_str()))
		cout<<"Error in setting var envs"<<endl;
		else cout<<"set " <<varName.str()<<"="<< cmd.str()<<endl;
#else
		cout
				<< "SET SCREEN ENV VAR NOT IMPL ON THIS PLATEFORM - see function in LocalHost.cpp"
				<< endl;
#endif

	}
}

unsigned char LocalHost::getDisplayDeviceNumber() const {
	return _displayDevices.size();
}

const std::string& LocalHost::getHostName() const {
	return _hostName;
}
const std::string& LocalHost::getUserName() const {
	return _userName;
}
const std::string& LocalHost::getDomainName() const {
	return _domainName;
}

const DISPDEV_VECTOR& LocalHost::getDisplayDevices() const {
	return _displayDevices;
}
