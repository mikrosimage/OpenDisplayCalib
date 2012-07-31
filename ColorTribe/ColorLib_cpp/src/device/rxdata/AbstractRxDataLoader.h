/*
 * AbstractRxDataLoader.h
 *
 *  Created on: 4 mars 2009
 *      Author: mfe
 */

#ifndef ABSTRACTRXDATALOADER_H_
#define ABSTRACTRXDATALOADER_H_

#include <string>

class AbstractRxDataLoader {
public:
	AbstractRxDataLoader();
	virtual ~AbstractRxDataLoader();
	virtual int loadRxData(std::string & hostName, std::string & domain)=0;
};

#endif /* ABSTRACTRXDATALOADER_H_ */
