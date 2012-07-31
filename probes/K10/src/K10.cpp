/*
 * K10.cpp
 *
 *  Created on: 27 avr. 2010
 *      Author: mfe
 */

#include "K10.h"
#include <sstream>
#include <string>
#include <vector>
#include <iostream>
#include <cmath>
using namespace std;

const std::string K10::K10_NAME = "P0K-10";

K10::K10() :
	portName("") {
}
K10::K10(const string &name) :
	portName(name.c_str()) {
}

K10::~K10() {
}

const std::string K10::isConnected() {
	ComManager manager;

	vector<string> ports;
	//
	manager.getAvalaibleComPorts(ports, ComManager::DEFAULT_LENGTH);

	for (vector<string>::iterator it = ports.begin(); it != ports.end(); it++) {
		if (manager.openCom((*it).c_str(), SPEED, SIZE, PARITY, STOPBITS)) {
			if (manager.sendCom("P0\r")) {
				int nSerialChars = 21;
				char receiveMessage[nSerialChars];
				int nReceivedChars;
				if (manager.receiveCom(receiveMessage, nSerialChars,
						&nReceivedChars)) {
					if (nReceivedChars != 0) {
						string receiveString = receiveMessage;
						if (receiveString.compare(0, 6, K10_NAME)==0) {
							manager.closeCom();
							return (*it);
						}
					}
				}
			}
			manager.closeCom();
		}
	}

	return "NOT_FOUND";
}
const std::string K10::init() {
	if (manager.openCom(portName.c_str(), SPEED, SIZE, PARITY, STOPBITS)) {
		if (manager.sendCom("P0\r")) {
			int nSerialChars = 21;
			char receiveMessage[nSerialChars];
			int nReceivedChars;
			if (manager.receiveCom(receiveMessage, nSerialChars,
					&nReceivedChars)) {
				if (nReceivedChars != 0) {
					if (K10_NAME.compare(0, 6, receiveMessage)) {
						string message = receiveMessage;
						return message.substr(7, 9);
					}
				}
			}
		}
	}
	return "";
}

float K10::getRawValue(unsigned char byte1, unsigned char byte2,
		unsigned char byte3) {
	int sign = 1;
	if (byte1 >= 128) {
		byte1 -= 128;
		sign = -1;
	}

	float fraction = (byte1 + byte2 / 256.f) / 256.f;
	int exp = byte3;
	if (exp > 128) {
		exp -= 256;
	}

	return sign * fraction * pow(2.f, exp);
}

void K10::getXYZ(float &X, float &Y, float &Z) {
	if (manager.sendCom("N5\r")) {
		int nSerialChars = 15;
		char receiveMessage[nSerialChars];
		int nReceivedChars;
		if (manager.receiveCom(receiveMessage, nSerialChars, &nReceivedChars)) {
			if (nReceivedChars == 15) {
				X = getRawValue(receiveMessage[2], receiveMessage[3],
						receiveMessage[4]);
				Y = getRawValue(receiveMessage[5], receiveMessage[6],
						receiveMessage[7]);
				Z = getRawValue(receiveMessage[8], receiveMessage[9],
						receiveMessage[10]);
			}
		}
	}
}

void K10::release() {
	manager.closeCom();
}
