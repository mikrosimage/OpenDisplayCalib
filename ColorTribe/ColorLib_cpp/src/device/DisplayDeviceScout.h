/*
 * DisplayDeviceScout.h
 *
 *  Created on: 10 fevr. 2009
 *      Author: mfe
 */

#ifndef DISPLAYDEVICESCOUT_H_
#define DISPLAYDEVICESCOUT_H_

#include "DisplayDevice.h"
#include "DisplayDeviceScoutTypeDef.h"

#include <string>

class APIData;

class DeviceScout {
private:
	DeviceScout();
	DeviceScout(const DeviceScout&);
	virtual ~DeviceScout();
	DeviceScout& operator=(const DeviceScout&);

	EDID_VECTOR_CONST_ITR getEdidIndex(const std::string &apiDataMonID,
			const EDID_VECTOR &edidVector) const;
	API_VECTOR_CONST_ITR getAPIIndex(const std::string &apiDataMonID,
			const API_VECTOR &apiVector) const;

public:
	static DeviceScout* Instance() {
		static DeviceScout _instance;
		return &_instance;
	}

	void getDisplayDeviceList(std::vector<DisplayDevice> & displayDeviceVector);
	static void getHostName(std::string & hostName, std::string & domainName);
	static void getUserName(std::string & userName);

};

#endif /* DISPLAYDEVICESCOUT_H_ */
