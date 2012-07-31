/*
 * NVGetEDID.h
 *
 *  Created on: 28 juil. 2009
 *      Author: mfe
 */

#ifndef NVGETEDID_H_
#define NvGetEDID
#ifdef __linux__
#include <stdio.h>
#include "AbstractEDIDLoader.h"
#include <NVCtrl.h>
#include <NVCtrlLib.h>
#include <map>
#include <string>



class NVGetEDID {

public :
	enum EError {SUCCESS=0,OPEN_DISPLAY_ERROR, NOT_A_NV_SCREEN_ERROR, CANT_RETRIEVE_CONNECTED_DISPLAY};
	int
	getEDID(std:: vector<EDIDData> &edidDataVector);

	static NVGetEDID& getInstance() {
		static NVGetEDID instance;
		return instance;
	}
	static bool isGetEDIDEnable();
	void dumpError(int error);
	static int getNVControlPrimaryScreen(std::map<std::string,std::string> &maskToName);
private:

	NVGetEDID() {;}
	NVGetEDID(const NVGetEDID&);
	NVGetEDID& operator=(const NVGetEDID&);



};

#endif

#endif /* NVGetEDID */
