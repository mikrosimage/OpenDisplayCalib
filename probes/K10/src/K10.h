/*
 * K10.h
 *
 *  Created on: 27 avr. 2010
 *      Author: mfe
 */

#ifndef K10_H_
#define K10_H_
#include "ComManager.h"
#include <string>

class K10 {
	const std::string portName;
	const static int SPEED = 9600;//baud
	const static int SIZE = 8; //byte
	const static ComManager::Parity PARITY = ComManager::NONE;
	const static int STOPBITS = 1;
	const static std::string K10_NAME;
	ComManager manager;
	K10();
	float getRawValue(unsigned char byte1, unsigned char byte2,
			unsigned char byte3);
public:
	K10(const std::string &portName);
	virtual ~K10();
	/**
	 * isConnected()
	 * Find if a K-10 is connected.
	 * Return "NOT_FOUND" or the name of the com port
	 */
	static const std::string isConnected();
	/**
	 * init()
	 * Init the probe
	 * Return the serial id
	 */
	const std::string init(); //9 chars
	/**
	 * getXYZ()
	 * Mesure and return XYZ values
	 */
	void getXYZ(float &X, float &Y, float &Z);

	void release();
};

#endif /* K10_H_ */
