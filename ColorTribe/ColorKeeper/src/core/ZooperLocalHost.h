/*
 * ZooperLocalHost.h
 * ZooperLocalHost is an implementation of LocalHost that handle screen detection issues, especially on Linux.
 *  Created on: 8 oct. 2009
 *      Author: mfe
 */

#ifndef ZOOPERLOCALHOST_H_
#define ZOOPERLOCALHOST_H_

#include "ILocalHost.h"
#include "ZooperDisplayDevice.h"
#include <LocalHost.h>
#include <QString>
#include <QSettings>
#include <string>

typedef std::vector<ZooperDisplayDevice> ZOOPDEV_VECTOR;
typedef ZOOPDEV_VECTOR::iterator ZOOPPDEV_ITR;
typedef ZOOPDEV_VECTOR::const_iterator ZOOPDEV_CITR;

class ZooperLocalHost: public ILocalHost {

	LocalHost _autoHost;

	bool _isXScreenDisorder;
	bool _isQTIndexDisorder;
	bool _isQTNbScreenDisorder;
	bool _isSeparateXScreenDisorder;
	bool _isPlugged_But_UnusedScreenDisorder;
	bool _isMoreDesktopThanEdid;
	bool _isUnexpectedIssue;
	unsigned int _primaryScreenID;

	unsigned int _effectiveScreenNumber;
	unsigned int _calibableScreenNumber;

	unsigned int _qtNbScreens;
	unsigned int _xNbScreens;
	unsigned int _edidNbScreens;
	unsigned int _visibleScreen;

	void searchDisorders();
	ZOOPDEV_VECTOR _calibrableDevices;
	ZOOPDEV_VECTOR _notCalibrableDevices;
	int getDisplayIndexFromOSID(const unsigned int &osIndex) const;
public:
	ZooperLocalHost();
	virtual ~ZooperLocalHost();

	virtual void printInfo();

	unsigned char getEffectiveDisplayDeviceNumber() const;
	unsigned char getCalibrableDisplayDeviceNumber() const;
	unsigned char getNotCalibrableDisplayDeviceNumber() const;
	const std::string& getHostName() const;
	const std::string& getUserName() const;
	const std::string& getDomainName() const;
	//const DISPDEV_VECTOR& getDisplayDevices() const;


	const ZooperDisplayDevice& getCalibrableDisplayDevice(
			const unsigned int &index) const;
	const ZooperDisplayDevice& getNotCalibrableDisplayDevice(const unsigned int &index) const;
	const unsigned char& getXScreensNumber() const;
	unsigned int deducePrimaryScreenID() const;
	unsigned int getMainScreenID(QSettings &settings) const;
	QString getIniFilePath() const;

	const bool& isXScreenDisorder() const {
		return _isXScreenDisorder;
	}
	const bool& isQTIndexDisorder() const {
		return _isQTIndexDisorder;
	}
	const bool& isQTNbScreenDisorder() const {
		return _isQTNbScreenDisorder;
	}
	const bool& isSeparateXScreenDisorder() const {
		return _isSeparateXScreenDisorder;
	}
	const bool& isPlugged_But_UnusedScreenDisorder() const {
		return _isPlugged_But_UnusedScreenDisorder;
	}
	const bool& isMoreDesktopThanEdid() const {
		return _isMoreDesktopThanEdid;
	}
	const bool& isUnexpectedIssue() const {
		return _isUnexpectedIssue;
	}
	QString getScreenDisorderErrorText() const;



};

#endif /* ZOOPERLOCALHOST_H_ */
