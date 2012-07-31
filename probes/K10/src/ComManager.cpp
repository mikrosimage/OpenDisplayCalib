/*
 * ComManager.cpp
 *
 *  Created on: 27 avr. 2010
 *      Author: mfe
 */

#include "ComManager.h"
using namespace std;
ComManager::ComManager() {
	g_hCom = 0;
}

ComManager::~ComManager() {
}



bool ComManager::openCom(const char *portName,const int speed,const int size, const Parity parity,const int stopBits) {
	//open Com
	g_hCom = CreateFile(portName, GENERIC_READ | GENERIC_WRITE, 0, NULL,
			OPEN_EXISTING, FILE_FLAG_WRITE_THROUGH | FILE_FLAG_NO_BUFFERING,
			NULL);

	if (g_hCom == INVALID_HANDLE_VALUE) {
		return false;
	} else {
		// on vide les tampons d'émission et de réception, mise à 1 DTR
		PurgeComm(g_hCom, PURGE_TXABORT | PURGE_RXABORT | PURGE_TXCLEAR
				| PURGE_RXCLEAR);

		// On paramètre le port série
		g_DCB.DCBlength = sizeof(DCB);

		//Configuration actuelle
		GetCommState(g_hCom, &g_DCB);

		//Modification du DCB
		g_DCB.BaudRate = speed;
		g_DCB.ByteSize = size;

		g_DCB.fDtrControl = DTR_CONTROL_ENABLE; // (DISABLE)

		// Gestion de la parité parmi paire, impaire et aucune
		if (parity == NONE)
			g_DCB.Parity = NOPARITY;
		if (parity == EVEN)
			g_DCB.Parity = EVENPARITY;
		if (parity == ODD)
			g_DCB.Parity = ODDPARITY;

		// Gestion du Stop Bit
		if (stopBits == 1)
			g_DCB.StopBits = ONESTOPBIT;
		//if ( StopBits == 1.5 )
		//dcb.StopBits = ONE5STOPBITS;
		if (stopBits == 2)
			g_DCB.StopBits = TWOSTOPBITS;

		//Configuration de la liaison serie
		SetCommState(g_hCom, &g_DCB);

		return true;
	}
}

bool ComManager::sendCom(const char* message) {
	DWORD NumBytes = 0;
	int TailleChaine = strlen(message);
	if (g_hCom != NULL) {
		//Emission de la Chaine
		if (WriteFile(g_hCom, message, TailleChaine, &NumBytes, NULL) == 0) {
			//printf("\n Erreur emission.\n");
			return false;
		} else {
			//printf("\n Chaine envoyee : %s.\n", message);
			return true;
		}
	} else
		return true;
}

bool ComManager::receiveCom(char *message, int nMaxChars, int* nReadChars) {
	COMSTAT Stat;
	DWORD Errors;
	int nCharsToRead;
	DWORD NCarLus = 0;

	if (g_hCom != NULL) {

		//avoid time out
		Sleep(500);
		//number of bits in buffer In
		ClearCommError(g_hCom, &Errors, &Stat);
		nCharsToRead = Stat.cbInQue;
		//read
		if ((nCharsToRead > 0) && (nCharsToRead <= nMaxChars)) {

			if (ReadFile(g_hCom, message, nCharsToRead, &NCarLus, NULL) == 0) {
				//printf("\n Erreur reception.\n");
				return FALSE;
			}
		}
		//Finition de la Chaine
		*nReadChars = NCarLus;
		message[NCarLus] = '\0';

	} else
		return false;
	return true;
}

DWORD ComManager::getAvalaibleComPorts(vector<string> &ports, DWORD lendata) {
	HKEY hkey;
	char buff[256], szval[256], *c, *p;
	DWORD idx = (DWORD) - 1, r, len;
	DWORD n = 0;
	if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, "HARDWARE\\DEVICEMAP\\SERIALCOMM", 0,
			KEY_READ, &hkey))
		goto allcomEXIT;
	nextVALUE: idx++;
	r = len = 256;
	if (RegEnumValue(hkey, idx, szval, &len, 0, 0, (BYTE*) buff, &r))
		goto closeKEY;
	c = (char*) (buff - 1);
	findCOM: do {
		c++;
		if (!*c)
			goto nextVALUE;
		if (*c == 'C')
			break;
	} while (1);

	if (*(c + 1) == 0)
		goto nextVALUE;
	if (*(c + 1) != 'O')
		goto findCOM;
	if (*(c + 2) == 0)
		goto nextVALUE;
	if (*(c + 2) != 'M')
		goto findCOM;

	//found value :
	p = c;
	ports.push_back(c);
	c += 3;
	while ((*c >= 48) && (*c <= 57))
		c++;
	len = c - p + 1;
	if (len > lendata)
		goto closeKEY;
	lendata -= len;
	*c = 0;

	n++;
	if (lendata >= 5)
		goto nextVALUE;

	closeKEY: RegCloseKey(hkey);
	allcomEXIT: return n;
}

bool ComManager::closeCom() {
	if (g_hCom != NULL) {
		CloseHandle(g_hCom);
		return true;
	}
	return false;
}
