/*
 * ILocalHost.h
 *
 *  Created on: 8 oct. 2009
 *      Author: mfe
 */

#ifndef ILOCALHOST_H_
#define ILOCALHOST_H_

#include <string>
#include <qstring>
#include <vector>
#include "ZooperDisplayDevice.h"

typedef std::vector<DisplayDevice> DISPDEV_VECTOR;
typedef DISPDEV_VECTOR::iterator DISPDEV_ITR;
typedef DISPDEV_VECTOR::const_iterator DISPDEV_CITR;

class ILocalHost {
public:

	virtual ~ILocalHost() =0;

	virtual void printInfo() = 0;
	virtual unsigned char getEffectiveDisplayDeviceNumber() const =0;
	virtual unsigned char getCalibrableDisplayDeviceNumber() const =0;
	virtual unsigned char getNotCalibrableDisplayDeviceNumber() const =0;
	virtual QString getScreenDisorderErrorText() const = 0;
	virtual const std::string& getHostName() const =0;
	virtual const std::string& getUserName() const =0;
	virtual const std::string& getDomainName() const =0;
	//	virtual const DISPDEV_VECTOR& getDisplayDevices() const=0;
	virtual const ZooperDisplayDevice& getCalibrableDisplayDevice(
			const unsigned int &index) const=0;
	virtual const ZooperDisplayDevice& getNotCalibrableDisplayDevice(
			const unsigned int &index) const=0;
	//virtual const DisplayDevice& getEffectiveDisplayDevice(const unsigned int &index) const=0;
	virtual const unsigned char& getXScreensNumber() const=0;
	virtual const bool& isXScreenDisorder() const =0;
	virtual const bool& isQTIndexDisorder() const =0;
	virtual const bool& isQTNbScreenDisorder() const=0;
	virtual const bool& isSeparateXScreenDisorder() const =0;
	virtual const bool& isPlugged_But_UnusedScreenDisorder() const =0;
	virtual const bool& isMoreDesktopThanEdid() const=0;
	virtual const bool& isUnexpectedIssue() const=0;
};

#endif /* ILOCALHOST_H_ */
