/*
 * DisplayDevice.h
 *
 *  Created on: 10 fevr. 2009
 *      Author: mfe
 */

#ifndef DISPLAYDEVICE_H_
#define DISPLAYDEVICE_H_
#include<string>

class DisplayDevice {
public:
	enum DisplayDeviceType {
		UNSET, LCD, PROJECTOR, CRT
	};

	std::string getDisplayDeviceTypeText(DisplayDeviceType type) const;

	DisplayDevice(std::string manufacturerName, std::string modelName,
			long serialNumber, std::string serialSNNumber,
			unsigned char weekOfManufacture, unsigned int yearOfManufacture,
			std::string modelConstructorID, unsigned char videoOutputIndex,
			std::string deviceName, DisplayDeviceType type,
			std::string cardDescription, std::string cardID);
	virtual ~DisplayDevice();

protected:
	DisplayDevice();
	std::string _manufacturerName;
	std::string _modelName;
	long _serialNumber;
	std::string _serialSNNumber;
	unsigned char _weekOfManufacture;
	unsigned int _yearOfManufacture;

	std::string _modelConstructorID;

	unsigned int _videoOutputIndex;
	std::string _deviceName;
	DisplayDeviceType _type;
	std::string _cardDescription;
	std::string _cardID;

	static bool fileAbleChar(const char &c);
public:

	const std::string getManufacturerName() const {
		return _manufacturerName;
	}

	std::string getMonitorIdentification() const {
		std::string tmp = "";
		tmp.append(_manufacturerName);
		tmp.append(_modelConstructorID);
		return tmp;
	}

	const std::string getModelName() const {
		return _modelName;
	}

	const std::string getFullName(bool space=true) const;

	const std::string getFullText() const;

	long getSerialNumber() const {
		return _serialNumber;
	}

	const std::string getSerialSNNumber() const {
		return _serialSNNumber;
	}

	unsigned char getWeekOfManufacture() const {
		return _weekOfManufacture;
	}

	unsigned int getYearOfManufacture() const {
		return _yearOfManufacture;
	}

	unsigned int getVideoOutputIndex() const {
		return _videoOutputIndex;
	}

	DisplayDeviceType getType() const {
		return _type;
	}
	const std::string getStringType() const {
		return getDisplayDeviceTypeText(_type);
	}

	std::string getDeviceName() const {
		return _deviceName;
	}
	std::string getCardDescription() const {
		return _cardDescription;
	}
	std::string getCardID() const {
		return _cardID;
	}
	unsigned int getOSIndex() const {
		//unsigned int res = (unsigned int)((_deviceName.at(_deviceName.size() - 1) - 48 - 1));////TODO check if its ok on Linux
		return getVideoOutputIndex();
	}

	std::string getModelConstructorID() const {
		return _modelConstructorID;
	}

	const std::string getFullSerialNumber() const;

	void printInfo() const;

};

#endif /* DISPLAYDEVICE_H_ */
