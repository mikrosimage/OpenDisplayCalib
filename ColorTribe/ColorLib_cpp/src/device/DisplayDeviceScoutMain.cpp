/*
 * main.cpp
 *
 *  Created on: 21 janv. 2009
 *      Author: mfe
 */

//#include "readEdidWin_test.h"
//#include "parse_edid_test.h"

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

	return 0;
}
