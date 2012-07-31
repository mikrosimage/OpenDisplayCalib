/*
 * AbstractEDIDLoader.h
 *
 *  Created on: 23 janv. 2009
 *      Author: mfe
 */

#ifndef ABSTRACTEDIDLOADER_H_
#define ABSTRACTEDIDLOADER_H_

#include <vector>
class EDIDData;

class AbstractEDIDLoader {
public:
	AbstractEDIDLoader();
	virtual ~AbstractEDIDLoader();
	virtual int loadEDID(std::vector<EDIDData> &edidVector)=0;
};

#endif /* ABSTRACTEDIDLOADER_H_ */
