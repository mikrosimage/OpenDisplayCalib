/*
 * LocalHost.h
 *
 *  Created on: 24 fevr. 2009
 *      Author: mfe
 */

#ifndef LOCALHOST_H_
#define LOCALHOST_H_

#include <string>
#include <vector>
#include <map>
#include "DisplayDevice.h"


typedef std::vector<DisplayDevice> DISPDEV_VECTOR;
typedef DISPDEV_VECTOR::iterator DISPDEV_ITR;
typedef DISPDEV_VECTOR::const_iterator DISPDEV_CITR;

class LocalHost {
private:
	std::vector<DisplayDevice> _displayDevices;
	std::string _hostName;
	std::string _userName;
	std::string _domainName;
	unsigned char _XScreenNumber;
#ifdef __linux__
	std::map<std::string,std::string> _maskToName;
#endif

public:
	LocalHost();
	virtual ~LocalHost();
	void printInfo() const;
	void setScreenEnvironnementVariables() const;

	unsigned char getDisplayDeviceNumber() const ;
	const std::string& getHostName() const ;
	const std::string& getUserName() const ;
	const std::string& getDomainName() const ;
	const DISPDEV_VECTOR& getDisplayDevices() const;
	const unsigned char& getXScreensNumber() const;
#ifdef __linux__
	const std::map<std::string,std::string>  getMaskToNameMap() const{
		return _maskToName;
	}
#endif
};

#endif /* LOCALHOST_H_ */
