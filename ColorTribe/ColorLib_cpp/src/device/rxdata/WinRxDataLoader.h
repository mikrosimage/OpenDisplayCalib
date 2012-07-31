/*
 * WinRxDataLoader.h
 *
 *  Created on: 4 mars 2009
 *      Author: mfe
 */

#ifndef WINRXDATALOADER_H_
#define WINRXDATALOADER_H_
#include "AbstractRxDataLoader.h"

class WinRxDataLoader : public AbstractRxDataLoader{
public:
	WinRxDataLoader();
	virtual ~WinRxDataLoader();
#ifdef __WIN32__ 
	virtual int loadRxData(std::string & hostName, std::string & domain);
#else
	virtual int loadRxData(std::string & hostName)=0;
#endif
};

#endif /* WINRXDATALOADER_H_ */
