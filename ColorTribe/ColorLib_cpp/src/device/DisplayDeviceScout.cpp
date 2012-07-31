/*
 * DisplayDeviceScout.cpp
 *
 *  Created on: 10 fevr. 2009
 *      Author: mfe
 */

#include "DisplayDeviceScout.h"
#include "ediddata/AbstractEDIDLoader.h"
#include "apidata/AbstractAPIDataLoader.h"
#include "rxdata/AbstractRxDataLoader.h"
#include "../trivialLogger/tri_logger.hpp"
#ifdef __WIN32__ 
#include "ediddata/WinEDIDLoader.h"
#include "apidata/WinAPIDataLoader.h"
#include "rxdata/WinRxDataLoader.h"
#endif

#if defined(__linux__) || defined(__APPLE__)
#include "ediddata/LinEDIDLoader.h"
#include "rxdata/LinRxDataLoader.h"
#endif
#if defined(__linux__)
#include "ediddata/LinEDIDLoader.h"
#endif
#if defined(__APPLE__)
#include "ediddata/MacOSXEDIDLoader.h"
#endif

#include <iostream>
#include <memory>
#include <stdlib.h>
#include <sstream>

using namespace std;

DeviceScout::DeviceScout() {

}

DeviceScout::~DeviceScout() {

}

EDID_VECTOR_CONST_ITR DeviceScout::getEdidIndex(const string &apiDataMonID,
		const EDID_VECTOR &edidVector) const {

	for (EDID_VECTOR_CONST_ITR itr = edidVector.begin(); itr
			!= edidVector.end(); ++itr) {
		size_t pos = apiDataMonID.find(itr->getMonitorIdentification());
		if (pos != string::npos) {
			return itr;
		}
	}
	return edidVector.end();
}
API_VECTOR_CONST_ITR DeviceScout::getAPIIndex(const string &apiDataMonID,
		const API_VECTOR &apiVector) const {
	for (API_VECTOR_CONST_ITR itr = apiVector.begin(); itr != apiVector.end(); ++itr) {
		size_t pos = (itr->getMonitorIdentification()).find(apiDataMonID);
		if (pos != string::npos)
			return itr;
	}
	return apiVector.end();
}
#ifdef __WIN32__ 
void DeviceScout::getDisplayDeviceList(
		vector<DisplayDevice> &displayDeviceVector) {
	EDID_VECTOR edidVector;
	API_VECTOR apiVector;

	std::auto_ptr<AbstractEDIDLoader> edidLoady( new WinEDIDLoader());
	std::auto_ptr<AbstractAPIDataLoader> apiDataLoady(new WinAPIDataLoader());

	edidLoady->loadEDID(edidVector);
	apiDataLoady->loadAPIData(apiVector);

	//// if edidVector size > apiVector size, an unplugged screen may be detected by EDID
	if(edidVector.size() > apiVector.size()) {
		API_VECTOR_CONST_ITR apiDataIt;
		ostringstream os;
		os<<"edidVector "<<edidVector.size()<<" api vector "<<apiVector.size()<<endl;
		TRI_MSG_STR(os.str());
		for (apiDataIt = apiVector.begin(); apiDataIt != apiVector.end(); apiDataIt++) {
			EDID_VECTOR_CONST_ITR edidIndex = getEdidIndex(apiDataIt->getMonitorIdentification(), edidVector);
			if (edidIndex == edidVector.end()) {
				//no EDID info found
				DisplayDevice
				display("UNSET", "UNSET", 0, "UNSET", 0, 0,
						apiDataIt->extractMonitorIdentification(), apiDataIt->getOutputIndex(),
						apiDataIt->getDeviceName(),
						DisplayDevice::UNSET,
						apiDataIt->getCardDescription(),
						apiDataIt->getCardID());
				displayDeviceVector.push_back(display);
			} else {
				//EDID info found
				DisplayDevice
				display(edidIndex->getManufacturerName(),
						edidIndex->getModelName(),
						edidIndex->getSerialNumber(),
						edidIndex->getSerialSNNumber(),
						edidIndex->getWeekOfManufacture(),
						edidIndex->getYearOfManufacture(),
						edidIndex->getMonitorIdentification(),
						apiDataIt->getOutputIndex(),
						apiDataIt->getDeviceName(),
						DisplayDevice::UNSET,
						apiDataIt->getCardDescription(),
						apiDataIt->getCardID());
				displayDeviceVector.push_back(display);
			}
		}
	} else { //
		EDID_VECTOR_CONST_ITR edidDataIt;
		cout<<"edidVector "<<edidVector.size()<<" api vector "<<apiVector.size()<<endl;
		//	for (EDID_VECTOR_CONST_ITR edidIndex = edidVector.begin(); edidIndex!= edidVector.end(); ++edidIndex, ++i) {
		int fakeIndex = 0;
		for (edidDataIt = edidVector.begin(); edidDataIt != edidVector.end(); edidDataIt++) {
			API_VECTOR_CONST_ITR apiIndex = getAPIIndex(edidDataIt->getMonitorIdentification(), apiVector);
			if (apiIndex == apiVector.end()) {
				//no API info found
				DisplayDevice
				display(edidDataIt->getManufacturerName(),
						edidDataIt->getModelName(),
						edidDataIt->getSerialNumber(),
						edidDataIt->getSerialSNNumber(),
						edidDataIt->getWeekOfManufacture(),
						edidDataIt->getYearOfManufacture(),
						edidDataIt->getMonitorIdentification(),
						fakeIndex,
						"UNSET",
						DisplayDevice::UNSET,
						"UNSET",
						"UNSET");
				displayDeviceVector.push_back(display);
			} else {
				//API info found
				DisplayDevice
				display(edidDataIt->getManufacturerName(),
						edidDataIt->getModelName(),
						edidDataIt->getSerialNumber(),
						edidDataIt->getSerialSNNumber(),
						edidDataIt->getWeekOfManufacture(),
						edidDataIt->getYearOfManufacture(),
						edidDataIt->getMonitorIdentification(),
						apiIndex->getOutputIndex(),
						apiIndex->getDeviceName(),
						DisplayDevice::UNSET,
						apiIndex->getCardDescription(),
						apiIndex->getCardID());
				displayDeviceVector.push_back(display);
			}
			fakeIndex++;
		}
	}

}
#endif

#ifdef __linux__

# include <X11/Xos.h>
# include <X11/Xlib.h>
# include <X11/Xutil.h>

void DeviceScout::getDisplayDeviceList(
		vector<DisplayDevice> &displayDeviceVector) {

	EDID_VECTOR edidVector;
	API_VECTOR apiVector;

	std::auto_ptr<AbstractEDIDLoader> edidLoady( new LinEDIDLoader());
	edidLoady->loadEDID(edidVector);

	unsigned int i = 0;
	for (EDID_VECTOR_CONST_ITR edidIndex = edidVector.begin(); edidIndex
			!= edidVector.end(); ++edidIndex, ++i) {
		DisplayDevice display(edidIndex->getManufacturerName(),
				edidIndex->getModelName(), edidIndex->getSerialNumber(),
				edidIndex->getSerialSNNumber(),
				edidIndex->getWeekOfManufacture(),
				edidIndex->getYearOfManufacture(),
				edidIndex->getMonitorIdentification(), i, "UNSET",
				DisplayDevice::UNSET, "UNSET", "UNSET");
		displayDeviceVector.push_back(display);
	}

}
#endif
#ifdef __APPLE__

using namespace std;

void DeviceScout::getDisplayDeviceList(
		vector<DisplayDevice> &displayDeviceVector) {

	EDID_VECTOR edidVector;
	API_VECTOR apiVector;
	std::auto_ptr<AbstractEDIDLoader> edidLoady( new MacOSXEDIDLoader());
	edidLoady->loadEDID(edidVector);

	unsigned int i = 0;
	for (EDID_VECTOR_CONST_ITR edidIndex = edidVector.begin(); edidIndex
			!= edidVector.end(); ++edidIndex, ++i) {
		DisplayDevice display(edidIndex->getManufacturerName(),
				edidIndex->getModelName(), edidIndex->getSerialNumber(),
				edidIndex->getSerialSNNumber(),
				edidIndex->getWeekOfManufacture(),
				edidIndex->getYearOfManufacture(),
				edidIndex->getMonitorIdentification(), i, "UNSET",
				DisplayDevice::UNSET, "UNSET", "UNSET");
		displayDeviceVector.push_back(display);
	}
}
#endif

void DeviceScout::getHostName(std::string & hostName, std::string & domainName) {
#ifdef __WIN32__
	std::auto_ptr<AbstractRxDataLoader> rxLoady( new WinRxDataLoader());
#endif
#if defined(__linux__) || defined(__APPLE__)
	std::auto_ptr<AbstractRxDataLoader> rxLoady( new LinRxDataLoader());
#endif
	rxLoady->loadRxData(hostName, domainName);
}



void DeviceScout::getUserName(std::string & userName) {
	char *user = getenv("USERNAME");

	if (user != NULL)
		userName.append(user);
	else {
		user = getenv("USER");
		if (user != NULL)
			userName.append(user);
		else
			cout
					<< " !!!!!!!!!!!!!!! SET env USER or USERNAME !!!!!!!!!!!!!!!!"
					<< endl;
	}
}

