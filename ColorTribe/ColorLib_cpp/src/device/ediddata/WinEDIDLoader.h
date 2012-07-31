/*
 * WinEDIDLoader.h
 *
 *  Created on: 23 janv. 2009
 *      Author: mfe
 */

#ifndef WINEDIDLOADER_H_
#define WINEDIDLOADER_H_
#include "AbstractEDIDLoader.h"

class WinEDIDLoader : public AbstractEDIDLoader {
public:
	WinEDIDLoader(){};
	virtual ~WinEDIDLoader(){};
#ifdef __WIN32__ 
	int loadEDID(std::vector<EDIDData>  &edidVector);
#else
	int loadEDID(std::vector<EDIDData> &edidVector)=0;

#endif
};

#endif /* WINEDIDLOADER_H_ */
