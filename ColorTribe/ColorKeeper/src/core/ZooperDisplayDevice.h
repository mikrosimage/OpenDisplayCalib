/*
 * ZooperDisplayDevice.h
 *
 *
 *
 *  Created on: 12 oct. 2009
 *      Author: mfe
 */

#ifndef ZOOPERDISPLAYDEVICE_H_
#define ZOOPERDISPLAYDEVICE_H_

#include <DisplayDevice.h>

class ZooperDisplayDevice: public DisplayDevice {

	unsigned int _desktopIndex;
	bool _isUnvalidtSerial;
	bool _isSNNValid;
	bool _isSerialValid;
	void detectFakeUIDs();

public:
	ZooperDisplayDevice(std::string manufacturerName, std::string modelName,
			long serialNumber, std::string serialSNNumber,
			unsigned char weekOfManufacture, unsigned int yearOfManufacture,
			std::string modelConstructorID, unsigned char videoOutputIndex,
			std::string deviceName, DisplayDeviceType type,
			std::string cardDescription, std::string cardID,
			unsigned int desktopIndex);

	ZooperDisplayDevice(const DisplayDevice& display);
	ZooperDisplayDevice(unsigned int index);
	ZooperDisplayDevice(const DisplayDevice& display, unsigned int osIndex,
			unsigned int desktopIndex);

	unsigned int getDesktopIndex() const {
		return _desktopIndex;
	}
	void setOSID(unsigned int newID){
		_videoOutputIndex = newID;
	}

	bool needCustomID() const{
		return _isUnvalidtSerial;
	}

	virtual ~ZooperDisplayDevice();
};

#endif /* ZOOPERDISPLAYDEVICE_H_ */
