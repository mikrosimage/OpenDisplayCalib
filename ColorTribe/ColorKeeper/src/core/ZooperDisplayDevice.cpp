/*
 * ZooperDisplayDevice.cpp
 *
 *  Created on: 12 oct. 2009
 *      Author: mfe
 */

#include "ZooperDisplayDevice.h"
#include "ColorKeeperModel.h"
#include <iostream>
#include <sstream>
using namespace std;

ZooperDisplayDevice::ZooperDisplayDevice(std::string manufacturerName,
		std::string modelName, long serialNumber, std::string serialSNNumber,
		unsigned char weekOfManufacture, unsigned int yearOfManufacture,
		std::string modelConstructorID, unsigned char videoOutputIndex,
		std::string deviceName, DisplayDeviceType type,
		std::string cardDescription, std::string cardID,
		unsigned int desktopIndex) :
	DisplayDevice(manufacturerName, modelName, serialNumber, serialSNNumber,
			weekOfManufacture, yearOfManufacture, modelConstructorID,
			videoOutputIndex, deviceName, type, cardDescription, cardID),
			_desktopIndex(desktopIndex), _isUnvalidtSerial(false), _isSNNValid(
					true), _isSerialValid(true) {
	detectFakeUIDs();
}

ZooperDisplayDevice::ZooperDisplayDevice(const DisplayDevice& display) :
	DisplayDevice(display.getManufacturerName(), display.getModelName(),
			display.getSerialNumber(), display.getSerialSNNumber(),
			display.getWeekOfManufacture(), display.getYearOfManufacture(),
			display.getMonitorIdentification(), display.getVideoOutputIndex(),
			display.getDeviceName(), display.getType(),
			display.getCardDescription(), display.getCardID()), _desktopIndex(
			display.getVideoOutputIndex()), _isUnvalidtSerial(false),
			_isSNNValid(true), _isSerialValid(true) {
	detectFakeUIDs();
}

ZooperDisplayDevice::ZooperDisplayDevice(unsigned int index) :
	DisplayDevice("UNSET", "UNSET", -1, "UNSET", 0, 0, "UNSET", index, "UNSET",
			DisplayDevice::UNSET, "UNSET", "UNSET"), _desktopIndex(index),
			_isUnvalidtSerial(true), _isSNNValid(false), _isSerialValid(false) {

}

ZooperDisplayDevice::ZooperDisplayDevice(const DisplayDevice& display,
		unsigned int osIndex, unsigned int desktopIndex) :
	DisplayDevice(display.getManufacturerName(), display.getModelName(),
			display.getSerialNumber(), display.getSerialSNNumber(),
			display.getWeekOfManufacture(), display.getYearOfManufacture(),
			display.getMonitorIdentification(), osIndex,
			display.getDeviceName(), display.getType(),
			display.getCardDescription(), display.getCardID()), _desktopIndex(
			desktopIndex), _isUnvalidtSerial(false), _isSNNValid(true),
			_isSerialValid(true) {
	detectFakeUIDs();
}

void ZooperDisplayDevice::detectFakeUIDs() {

	//	long _serialNumber;
	//	std::string _serialSNNumber;
	//	unsigned char _weekOfManufacture;
	//	unsigned int _yearOfManufacture;

	if (_yearOfManufacture == 0 && _weekOfManufacture == 0) {
		ostringstream os;
		os << _videoOutputIndex << " : wrong date of Manufacture." << std::endl;
		ColorKeeperModel::logMessage(os.str());
	}

	if ((_serialSNNumber.compare("") == 0) || (_serialSNNumber.compare("unset")
			== 0) || (_serialSNNumber.compare("UNSET") == 0)) {
		_isSNNValid = false;
	}
	if (_serialNumber <= 0) {
		_isSerialValid = false;
	}
	if (!_isSNNValid && !_isSerialValid) {
		_isUnvalidtSerial = true;
		ostringstream os;
		os << _videoOutputIndex << " : unvalid serial." << std::endl;
		ColorKeeperModel::logMessage(os.str());
	}
}

ZooperDisplayDevice::~ZooperDisplayDevice() {
	// TODO Auto-generated destructor stub
}
