/*
 * DisplayDevice.cpp
 *
 *  Created on: 10 fevr. 2009
 *      Author: mfe
 *
 */

#include "DisplayDevice.h"
#include <iostream>
#include <sstream>
using namespace std;

DisplayDevice::DisplayDevice() :
	_manufacturerName("NONE"), _modelName("NONE"), _serialNumber(0),
			_weekOfManufacture(0), _yearOfManufacture(0), _videoOutputIndex(0),
			_type(UNSET) {

}

DisplayDevice::DisplayDevice(std::string manufacturerName,
		std::string modelName, long serialNumber, std::string serialSNNumber,
		unsigned char weekOfManufacture, unsigned int yearOfManufacture,
		std::string modelConstructorID, unsigned char videoOutputIndex,
		std::string deviceName, DisplayDeviceType type,
		std::string cardDescription, std::string cardID) :
	_manufacturerName(manufacturerName), _modelName(modelName), _serialNumber(
			serialNumber), _serialSNNumber(serialSNNumber), _weekOfManufacture(
			weekOfManufacture), _yearOfManufacture(yearOfManufacture),
			_modelConstructorID(modelConstructorID), _videoOutputIndex(
					videoOutputIndex), _deviceName(deviceName), _type(type),
			_cardDescription(cardDescription), _cardID(cardID) {

}

DisplayDevice::~DisplayDevice() {
}

void DisplayDevice::printInfo() const {
	cout << getFullText();
}

const std::string DisplayDevice::getFullText() const {
	ostringstream os;
	os << "Constructeur : " << _manufacturerName << endl;
	os << "Model : " << _modelName << endl;
	os << "Model Identification : " << _modelConstructorID << endl;
	if (_serialNumber == -1)
		os << "Serial number :  \n- numeric UID unset \n- S/N "
				<< _serialSNNumber << endl;
	else
		os << "Serial number : \n- " << _serialNumber << "\n-  S/N "
				<< _serialSNNumber << endl;
	os << "Date : " << (int) _weekOfManufacture << " / " << _yearOfManufacture
			<< endl;
	os << "Monitor index : " << _videoOutputIndex << " (" << _deviceName << ")"
			<< endl;
	os << "Type : " << getDisplayDeviceTypeText(_type) << endl;
	os << "Graphic card : " << _cardDescription << endl;
	//os << "Graphic card ID : "<<_cardID<<endl;
	return os.str();
}

bool DisplayDevice::fileAbleChar(const char &c) {
	if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c
			<= '9') || c == '-' || c == '_' || c == '.' || c == ' ')
		return true;

	return false;
}
const std::string DisplayDevice::getFullSerialNumber() const {
	ostringstream os;
	string wSerialNumber = _serialSNNumber;
	for (unsigned int i = 0; i < _serialSNNumber.size(); i++) {
		if (!fileAbleChar(_serialSNNumber.at(i)))
			wSerialNumber.replace(i, 1, "-");
	}

	os << _modelConstructorID << "_";
	if (_serialNumber == -1)
		os << "unset_" << wSerialNumber;
	else
		os << _serialNumber << "_" << wSerialNumber;

	return os.str();
}

string DisplayDevice::getDisplayDeviceTypeText(DisplayDeviceType type) const {
	switch (type) {
	case UNSET:
		return string("unset");
	case LCD:
		return string("LCD");
	case PROJECTOR:
		return string("Projector");
	case CRT:
		return string("CRT");
	default:
		return string("unknown");
	}
}

const std::string DisplayDevice::getFullName(bool space) const {
	string separator = "_";
	if (space)
		separator = " ";
	if (_manufacturerName.compare("UNSET") == 0 && _modelName.compare("UNSET")
			== 0) {
		return getModelConstructorID();
	} else if (_manufacturerName.compare("UNSET") == 0 || _modelName.compare(
			"UNSET") == 0)
		return getModelConstructorID();
	else
		return (_manufacturerName + separator + _modelName);
}
