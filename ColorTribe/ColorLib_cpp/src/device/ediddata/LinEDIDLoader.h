/*
 * LinEDIDLoader.h
 *
 *  Created on: 23 janv. 2009
 *      Author: mfe
 */

#ifndef LINEDIDLOADER_H_
#define LINEDIDLOADER_H_
#include "AbstractEDIDLoader.h"

class LinEDIDLoader : public AbstractEDIDLoader {
public:
	LinEDIDLoader(){};
	virtual ~LinEDIDLoader(){};
#ifdef __linux__

	int loadEDID(std::vector<EDIDData>  &edidVector);
#else
	int loadEDID(std::vector<EDIDData> &edidVector)=0;

#endif
};

#endif /* WINEDIDLOADER_H_ */
