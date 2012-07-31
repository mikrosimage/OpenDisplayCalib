/*
 * DisplayDeviceEnvVarSetter.cpp
 *
 *  Created on: 19 sept. 2011
 *      Author: mfe
 */

#include "ediddata/AbstractEDIDLoader.h"
#include "apidata/AbstractAPIDataLoader.h"
#include "LocalHost.h"
#ifdef __WIN32__
#include "ediddata/WinEDIDLoader.h"
#include "apidata/WinAPIDataLoader.h"
#endif

#include <vector>
#include <iostream>
#include <memory>
using namespace std;


int main() {
	LocalHost host;
	host.printInfo();
	host.setScreenEnvironnementVariables();
	return 0;
}
