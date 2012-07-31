/*
 * LinEDIDLoader.h
 *
 *  Created on: 23 janv. 2009
 *      Author: mfe
 */

#ifndef MAOSEDIDLOADER_H_
#define MAOSEDIDLOADER_H_
#include "AbstractEDIDLoader.h"

class MacOSXEDIDLoader : public AbstractEDIDLoader {
public:
	MacOSXEDIDLoader(){};
	virtual ~MacOSXEDIDLoader(){};
#ifdef __APPLE__

	int loadEDID(std::vector<EDIDData>  &edidVector);
#else
	int loadEDID(std::vector<EDIDData> &edidVector)=0;

#endif
};

#endif /* MAOSEDIDLOADER_H_ */
