/*
 * AbstractAPIDataLoader.h
 *
 *  Created on: 18 feb. 2009
 *      Author: mfe
 */

#ifndef ABSTRACTAPIDATALOADER_H_
#define ABSTRACTAPIDATALOADER_H_

class APIData;
#include <vector>

class AbstractAPIDataLoader {
public:
	AbstractAPIDataLoader();
	virtual ~AbstractAPIDataLoader();
	virtual int dumpGDIIbnfo()=0;
	virtual int loadAPIData(std::vector<APIData>& apiVector)=0;
};

#endif /* ABSTRACTAPIDATALOADER_H_ */
