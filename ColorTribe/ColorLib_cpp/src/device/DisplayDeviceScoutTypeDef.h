/*
 * DisplayDeviceScoutTypeDef.h
 *
 *  Created on: 24 févr. 2009
 *      Author: mfe
 */

#ifndef DISPLAYDEVICESCOUTTYPEDEF_H_
#define DISPLAYDEVICESCOUTTYPEDEF_H_

#include "ediddata/EDIDData.h"
#include "apidata/APIData.h"
#include <vector>

typedef std::vector<EDIDData> EDID_VECTOR;
typedef EDID_VECTOR::iterator EDID_VECTOR_ITR;
typedef EDID_VECTOR::const_iterator EDID_VECTOR_CONST_ITR;

typedef std::vector<APIData> API_VECTOR;
typedef API_VECTOR::iterator API_VECTOR_ITR;
typedef API_VECTOR::const_iterator API_VECTOR_CONST_ITR;

#endif /* DISPLAYDEVICESCOUTTYPEDEF_H_ */
