/*
 * ComManager.h
 *
 *  Created on: 27 avr. 2010
 *      Author: mfe
 */

#ifndef COMMANAGER_H_
#define COMMANAGER_H_

#include <windows.h>
#include <winbase.h>
#include <conio.h>
#include <vector>
#include <string>

#define MEMDISPO  (MEM_RESERVE | MEM_COMMIT | MEM_TOP_DOWN)
class ComManager {
private:
	DCB g_DCB;
	HANDLE g_hCom;
public:
	static const int DEFAULT_LENGTH = 4096;

	enum Parity {
		NONE, ODD, EVEN
	};
	ComManager();
	virtual ~ComManager();
	/**
	 * OpenCom(const char *portName, const int speed, const int size,
	 const Parity parity, const int stopBits)

	 * open communication on <portName> port with property listed.
	 */
	bool openCom(const char *portName, const int speed, const int size,
			const Parity parity, const int stopBits);
	/**
	 * sendCom(const char* message)
	 * Send a message
	 */
	bool sendCom(const char* message);
	/**
	 * receiveCom(char *message, int nMaxChars, int* nReadChars)
	 * Read the reception buffer and put it in message (limited by nMaxChars)
	 */
	bool receiveCom(char *message, int nMaxChars, int* nReadChars);

	/**
	 * getAvalaibleComPorts(std::vector<std::string> &ports, DWORD lendata)
	 * Return names of available com ports (including USB emulated port)
	 */
	DWORD getAvalaibleComPorts(std::vector<std::string> &ports, DWORD lendata);

	/**
	 * closeCom()
	 * Close communication
	 */

	bool closeCom();
};

#endif /* COMMANAGER_H_ */
