/*
 * WinAPIDataLoader.h
 *
 *  Created on: 18 feb. 2009
 *      Author: mfe
 */

#ifndef WINAPIDATALOADER_H_
#define WINAPIDATALOADER_H_
#include "AbstractAPIDataLoader.h"
class APIData;
#include <vector>

class WinAPIDataLoader : public AbstractAPIDataLoader {
public:
	WinAPIDataLoader(){}
	virtual ~WinAPIDataLoader(){}
#ifdef __WIN32__ 
	int dumpGDIIbnfo();
	int loadAPIData(std::vector<APIData> & apiVector);


#else
	int dumpGDIIbnfo()=0;
	int loadAPIData(std::vector<APIData> & apiVector)=0;
#endif

};

#endif /* WINAPIDATALOADER_H_ */
