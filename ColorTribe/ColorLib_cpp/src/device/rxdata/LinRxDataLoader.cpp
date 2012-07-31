/*
 * LinRxDataLoader.cpp
 *
 *  Created on: 4 mars 2009
 *      Author: mfe
 */

#include "LinRxDataLoader.h"

LinRxDataLoader::LinRxDataLoader() {

}

LinRxDataLoader::~LinRxDataLoader() {
}

#if defined(__linux__) || defined(__APPLE__)
#include <unistd.h>
#include <netdb.h>
#include <cstring>

int LinRxDataLoader::loadRxData(std::string & hostName, std::string & domain) {
	char host[256];
	int res = gethostname(host,256);
	hostName = host;


	char *dn;
	struct hostent *hp;

	gethostname(host, 254);
	hp = gethostbyname(host);
	dn = strchr(hp->h_name, '.');
	if (dn != NULL) {
		domain = ++dn;
	} else domain = "unknown";

	return res;
}
#endif
