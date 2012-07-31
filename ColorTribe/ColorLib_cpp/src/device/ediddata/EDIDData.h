/*
 * EDIDData.h
 *
 *  Created on: 22 janv. 2009
 *      Author: mfe
 */

#ifndef EDIDDATA_H_
#define EDIDDATA_H_

#include <string>
#include <vector>

class EDIDData {
	static const unsigned char s_MODELNAME_SIZE = 15;
	static const unsigned char s_MANUFACTURERNAME_SIZE = 4;
	static const unsigned char s_SNSERIALSIZE = 13;
	std::vector<unsigned char>_EDIDBuff;
	size_t _buffSize;
	std::string _manufacturerName;
	std::string _modelName;
	long _serialNumber;
	std::string _serialSNNumber;
	int _weekOfManufacture;
	int _yearOfManufacture;
	unsigned int _edidMinorVersion;
	unsigned int _edidMajorVersion;
	std::string _modelConstructorID;
	static const unsigned char s_MANUFACTURE_NAME_OFFSET = 0x08;
	static const unsigned char s_MODEL_NAME_OFFSET = 0x0A;
	static const unsigned char s_SERIAL_OFFSET = 0x0C;
	static const unsigned char s_WEEK_OFFSET = 0x10;
	static const unsigned char s_YEAR_OFFSET = 0x11;
	static const unsigned char s_DETAILED_TIMING_DESCRIPTIONS_START = 0x36;
	static const unsigned char s_DETAILED_TIMING_DESCRIPTION_SIZE = 18;
	static const unsigned char s_NO_DETAILED_TIMING_DESCRIPTIONS = 4;
	static const unsigned char s_MONITOR_NAME = 0xfc;
	static const unsigned char s_MONITOR_SN = 0xff;
	static const unsigned char s_MONITOR_ASCII = 0xfe;
	static const unsigned char s_DESCRIPTOR_DATA = 5;
	static const unsigned char edid_v1_descriptor_flag[];
	int block_type(unsigned char *block);
	void initMembers();
	bool isHeaderOk() const;
	void initManufacturerName();
	void initModelName();
	void initSerialNumber();
	void initManufactureDate();
	void initASCIISerial();
	void initMonitorModelID();
public:
	EDIDData();
//	EDIDData(const EDIDData&);
	EDIDData(unsigned char *EDIDBuffer, size_t bufferSize);
	virtual ~EDIDData();
	void printInfo() const;
	void dumpEdid() const;
	bool isCheckSumOk() const;
	bool isValid() const;
	int getCheckSum() const;
	std::string getMonitorIdentification() const;

	std::string getManufacturerName() const {
		return _manufacturerName;
	}

	std::string getModelName() const {
		return _modelName;
	}

	long getSerialNumber() const {
		return _serialNumber;
	}

	std::string getSerialSNNumber() const {
		return _serialSNNumber;
	}

	int getWeekOfManufacture() const {
		return _weekOfManufacture;
	}

	int getYearOfManufacture() const {
		return _yearOfManufacture;
	}

	std::string getModelConstructorID() const {
		return _modelConstructorID;
	}

};

#endif /* EDIDDATA_H_ */
