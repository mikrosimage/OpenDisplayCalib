/*
 * LinRxDataLoader.h
 *
 *  Created on: 4 mars 2009
 *      Author: mfe
 */

#ifndef LINRXDATALOADER_H_
#define LINRXDATALOADER_H_
#include "AbstractRxDataLoader.h"

class LinRxDataLoader : public AbstractRxDataLoader{
public:
	LinRxDataLoader();
	virtual ~LinRxDataLoader();
#if defined(__linux__) || defined(__APPLE__)
	virtual int loadRxData(std::string & hostName, std::string & domainName);
#else
#endif
};

#endif /* LinRxDataLoader_H_ */
