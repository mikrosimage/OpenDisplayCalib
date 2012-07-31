/*
 * APIData.h
 *
 *  Created on: 20 feb. 2009
 *      Author: mfe
 */

#ifndef APIDATA_H_
#define APIDATA_H_

#include <string>

class APIData {

	unsigned int _outputIndex;
	std::string _deviceName;
	std::string _monitorIdentification;
	std::string _cardDescription;
	std::string _cardID;

	void initOutPutIndex();
	APIData();
public:

	APIData(const std::string deviceName,
			const std::string monitorIdentification,
			const std::string cardDescription, const std::string cardID);
	APIData(const char* deviceName, const char * monitorIdentification,
			const char * cardDescription, const char * cardID);
	APIData(const std::string deviceName,
			const std::string monitorIdentification,
			const std::string cardDescription, const std::string cardID,
			const unsigned int &outputIndex);
	APIData(const char* deviceName, const char * monitorIdentification,
			const char * cardDescription, const char * cardID,
			const unsigned int &outputIndex);
	virtual ~APIData();
	void printInfo() const;

	std::string getCardID() const {
		return _cardID;
	}

	std::string getCardDescription() const {
		return _cardDescription;
	}

	unsigned int getOutputIndex() const {
		return _outputIndex;
	}

	std::string getDeviceName() const {
		return _deviceName;
	}

	std::string getMonitorIdentification() const {
		return _monitorIdentification;
	}
	std::string extractMonitorIdentification() const {
		if (_monitorIdentification.size() > 15)
			return _monitorIdentification.substr(8, 7);
		else
			return _monitorIdentification; // TODO Linux case
	}

};

#endif /* APIDATA_H_ */
