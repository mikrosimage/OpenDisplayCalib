/*
 * APIData.cpp
 *
 *  Created on: 20 feb. 2009
 *      Author: mfe
 */

#include "APIData.h"
#include "../../trivialLogger/tri_logger.hpp"
#include <iostream>
#include <sstream>
#include <cstdlib>

using namespace std;

APIData::APIData() : _outputIndex(0) {

}

APIData::APIData(const std::string deviceName, const std::string monitorIdentification,  const std::string cardDescription, const std::string cardID) : _outputIndex(0), _deviceName(deviceName) , _monitorIdentification(monitorIdentification), _cardDescription(cardDescription), _cardID(cardID) {
	initOutPutIndex();
}

APIData::APIData(const std::string deviceName, const std::string monitorIdentification,  const std::string cardDescription, const std::string cardID, const unsigned int &outputIndex) : _outputIndex(outputIndex), _deviceName(deviceName) , _monitorIdentification(monitorIdentification), _cardDescription(cardDescription), _cardID(cardID) {
}

APIData::APIData(const char* deviceName, const char * monitorIdentification, const char * cardDescription, const char * cardID) : _outputIndex(0){
	_deviceName.append(deviceName);
	_monitorIdentification.append(monitorIdentification);
	_cardDescription.append(cardDescription);
	_cardID.append(cardID);
	initOutPutIndex();
}

APIData::APIData(const char* deviceName, const char * monitorIdentification, const char * cardDescription, const char * cardID, const unsigned int &outputIndex) : _outputIndex(outputIndex){
	_deviceName.append(deviceName);
	_monitorIdentification.append(monitorIdentification);
	_cardDescription.append(cardDescription);
	_cardID.append(cardID);
}

void APIData::initOutPutIndex(){

	char cOutPutID = _deviceName.at(11);
	_outputIndex = atoi(&cOutPutID) - 1; // device id from 1..N, api's screen index from 0..N-1
}

void APIData::printInfo() const {
	ostringstream os;

	os<<"Device name : "<<_deviceName<<endl;
	os<<"OutputIndex : "<<_outputIndex<<endl;
	os<<"Monitor Identification : "<<_monitorIdentification.substr(8,7)<<endl;
	os<<"Graphic card : "<<_cardDescription<<endl;
	TRI_MSG_STR(os.str());
}

APIData::~APIData() {
}
