/*
 * LRMIGetEDID.h
 *
 *  Created on: 28 juil. 2009
 *      Author: mfe
 */

#ifndef GETEDID_H_
#define GETEDID_H_
#ifdef __linux__
#include <stdio.h>
#include "AbstractEDIDLoader.h"
#include <NVCtrl.h>
#include <NVCtrlLib.h>

typedef unsigned char byte;
typedef unsigned long uint32;

class LRMI_regs;

class LRMIGetEDID {

public :
	enum EError {SUCCESS=0,REAL_MODE_ERROR, REGISTER_ERROR, UNKNOW_SERVICE, MEMORY_ERROR, VBE_CALL_FAILED, OUT_BLOCK_UNCHANGE, LRMI_INIT_FAILED, EDID_CHECKSUM_UNCORRECT};
	int
	getEDID(std:: vector<EDIDData> &edidDataVector);

	static LRMIGetEDID& getInstance() {
		static LRMIGetEDID instance;
		return instance;
	}

	static void dumpError(int error);
	static bool isGetEDIDEnable();
private:

	LRMIGetEDID() {;}
	LRMIGetEDID(const LRMIGetEDID&);
	LRMIGetEDID& operator=(const LRMIGetEDID&);

	const static unsigned MAGIC = 0x13;
	const static unsigned EDID_BLOCK_SIZE = 128;
	const static unsigned MAX_TRIES = 4;//TODO up


	int do_vbe_service(unsigned AX,unsigned BX,LRMI_regs* regs);
	int read_edid( unsigned controller, std::vector<EDIDData> &edidDataVector );
};

#endif

#endif /* GETEDID_H_ */
