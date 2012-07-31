/*
 * EDIDData.cpp
 *
 *  Created on: 22 janv. 2009
 *      Author: mfe
 */

#include "EDIDData.h"
#include <iostream>
#include <sstream>
#include <cstdlib>
#include <cstring>
#include <cstdio>
using namespace std;
const unsigned char EDIDData::edid_v1_descriptor_flag[] = { 0x00, 0x00 };

std::string _manufacturerName;
std::string _modelName;
long _serialNumber;
std::string _serialSNNumber;
int _weekOfManufacture;
int _yearOfManufacture;
unsigned int _edidMinorVersion;
unsigned int _edidMajorVersion;
std::string _modelConstructorID;

EDIDData::EDIDData() :
	_EDIDBuff(0), _buffSize(0), _manufacturerName("UNSET"),
			_modelName("UNSET"), _serialNumber(0), _serialSNNumber("UNSET"),
			_weekOfManufacture(0), _yearOfManufacture(0), _edidMinorVersion(0),
			_edidMajorVersion(0), _modelConstructorID("UNSET") {
}

/*EDIDData::EDIDData(const EDIDData& data): _EDIDBuff(data._EDIDBuff), _buffSize(data._buffSize),
 _manufacturerName(""), _modelName(""), _serialSNNumber("") {
 //TODO
 initMembers();
 }*/

EDIDData::EDIDData(unsigned char * EDIDBuffer, size_t bufferSize) :
	_EDIDBuff(EDIDBuffer, EDIDBuffer + bufferSize), _buffSize(bufferSize) {

	//std::vector<unsigned char> v(ctabEDIDdata, ctabEDIDdata+1024);
	//std::copy(v.begin(), v.end(), std::ostream_iterator<unsigned char>(std::cout, " "));
	initMembers();
}

void EDIDData::printInfo() const {
	cout << "EDID version : " << _edidMajorVersion << "." << _edidMinorVersion
			<< endl;
	cout << "Constructeur : " << _manufacturerName << endl;
	cout << "Model : " << _modelName << endl;
	cout << "Model Identification : " << getMonitorIdentification() << endl;
	if (_serialNumber == -1)
		cout << "Serial number : numeric UID unset | S/N " << _serialSNNumber
				<< endl;
	else
		cout << "Serial number : " << _serialNumber << " | S/N "
				<< _serialSNNumber << endl;
	cout << "Date : " << _weekOfManufacture << " / " << _yearOfManufacture
			<< endl;
}

void EDIDData::initMembers() {
	if (isHeaderOk()) {
		_edidMinorVersion = _EDIDBuff[0x13];
		_edidMajorVersion = _EDIDBuff[0x12];
		initManufacturerName();
		initModelName();
		initSerialNumber();
		initManufactureDate();
		initASCIISerial();
		initMonitorModelID();
	}
}

std::string EDIDData::getMonitorIdentification() const {
	string tmp = "";
	tmp.append(_manufacturerName);
	tmp.append(_modelConstructorID);
	return tmp;
}

void EDIDData::initManufacturerName() {

	unsigned short h;
	/*
	 08h	WORD	big-endian manufacturer ID (see #00136)
	 bits 14-10: first letter (01h='A', 02h='B', etc.)
	 bits 9-5: second letter
	 bits 4-0: third letter
	 */
	char manufacturerName[s_MANUFACTURERNAME_SIZE];
	memset(manufacturerName, '\0', s_MANUFACTURERNAME_SIZE);
	h = (((unsigned int) _EDIDBuff[s_MANUFACTURE_NAME_OFFSET]) << 8)
			| (unsigned int) _EDIDBuff[s_MANUFACTURE_NAME_OFFSET + 1]; //COMBINE_HI_8LO(block[0], block[1]);
	manufacturerName[0] = ((h >> 10) & 0x1f) + 'A' - 1;
	manufacturerName[1] = ((h >> 5) & 0x1f) + 'A' - 1;
	manufacturerName[2] = (h & 0x1f) + 'A' - 1;
	manufacturerName[3] = '\0';
	_manufacturerName.append(manufacturerName);

}

int EDIDData::block_type(unsigned char* block) {
	if (!strncmp((const char *) edid_v1_descriptor_flag, (const char *) block,
			2)) {

		/* descriptor */
		if (block[2] != 0)
			return -1;

		return block[3];

	} else {
		/* detailed timing block */
		return -2;
	}
}

void EDIDData::initModelName() {

	char modelName[s_MODELNAME_SIZE];
	memset(modelName, '\0', s_MODELNAME_SIZE);
	unsigned char * ptr = &_EDIDBuff[s_DETAILED_TIMING_DESCRIPTIONS_START];
	for (unsigned int i = 0; i < s_NO_DETAILED_TIMING_DESCRIPTIONS; i++, ptr
			+= s_DETAILED_TIMING_DESCRIPTION_SIZE) {
		if (block_type(ptr) == s_MONITOR_NAME) {
			//monitor_name = get_monitor_name(block);
			ptr += s_DESCRIPTOR_DATA;
			for (unsigned int j = 0; j < s_MODELNAME_SIZE; j++, ptr++) {

				if (*ptr == 0xa) {
					modelName[j] = '\0';
					break;
				}
				modelName[j] = *ptr;
			}

		}

	}
	if (modelName[0] == '\0')
		sprintf(modelName, "%02x%02x", _EDIDBuff[s_MODEL_NAME_OFFSET],
				_EDIDBuff[s_MODEL_NAME_OFFSET + 1]);

	_modelName.append(modelName);

}

void EDIDData::initASCIISerial() {

	char serialSNNumber[s_SNSERIALSIZE];
	memset(serialSNNumber, '\0', s_SNSERIALSIZE);
	unsigned char * ptr = &_EDIDBuff[s_DETAILED_TIMING_DESCRIPTIONS_START];
	for (unsigned int i = 0; i < s_NO_DETAILED_TIMING_DESCRIPTIONS; i++, ptr
			+= s_DETAILED_TIMING_DESCRIPTION_SIZE) {

		if ((block_type(ptr) == s_MONITOR_SN) || (block_type(ptr)
				== s_MONITOR_ASCII)) {

			ptr += s_DESCRIPTOR_DATA;
			for (unsigned int j = 0; j < s_SNSERIALSIZE; j++, ptr++) {

				if (*ptr == 0xa) {
					serialSNNumber[j] = '\0';
					break;
				}
				serialSNNumber[j] = (char) (*ptr);
			}

		}
	}

	_serialSNNumber.append(serialSNNumber);

}

bool EDIDData::isCheckSumOk() const {
	int checkSum = getCheckSum();
	if (checkSum > 0 && checkSum % 256 == 0)
		return true;
	else
		return false;
}

int EDIDData::getCheckSum() const {
	int checksum = 0;
	vector<unsigned char>::const_iterator it;
	for (it = _EDIDBuff.begin(); it != _EDIDBuff.end(); it++) {
		checksum += (int) (*it);
	}
	return checksum;
}

void EDIDData::dumpEdid() const {
	vector<unsigned char>::const_iterator it;
	cout << "<Dump EDID>" << endl;
	for (it = _EDIDBuff.begin(); it != _EDIDBuff.end(); it++) {
		cout << (*it);
	}
	cout << endl << "</Dump EDID> " << endl;
}

void EDIDData::initSerialNumber() {
	_serialNumber = (unsigned int) (_EDIDBuff[s_SERIAL_OFFSET])
			| (unsigned int) (_EDIDBuff[s_SERIAL_OFFSET + 1] << 8)
			| (unsigned int) (_EDIDBuff[s_SERIAL_OFFSET + 2] << 16)
			| (unsigned int) (_EDIDBuff[s_SERIAL_OFFSET + 3] << 24);
	if ((_serialNumber == 16843009) || (_serialNumber == 0))// 1 1 1 1
		_serialNumber = -1;
}

void EDIDData::initMonitorModelID() {
	ostringstream os;
	os.flags(ios::right | ios::hex | ios::uppercase);
	os.fill('0');
	os.width(2);
	os << (int) _EDIDBuff[s_MODEL_NAME_OFFSET + 1]
			<< (int) _EDIDBuff[s_MODEL_NAME_OFFSET];

	_modelConstructorID = os.str();
}

void EDIDData::initManufactureDate() {
	_weekOfManufacture = _EDIDBuff[s_WEEK_OFFSET];
	_yearOfManufacture = _EDIDBuff[s_YEAR_OFFSET] + 1990;
}

bool EDIDData::isHeaderOk() const{
	//#ifndef __linux__
	if (_EDIDBuff[0] != 0 || _EDIDBuff[1] != 0xff || _EDIDBuff[2] != 0xff
			|| _EDIDBuff[3] != 0xff || _EDIDBuff[4] != 0xff || _EDIDBuff[5]
			!= 0xff || _EDIDBuff[6] != 0xff || _EDIDBuff[7] != 0) {
		return false;
	}
	//#endif

	return true;
}

bool EDIDData::isValid() const{
	return (_buffSize > 0 && isHeaderOk() && isCheckSumOk());
}

EDIDData::~EDIDData() {

}
