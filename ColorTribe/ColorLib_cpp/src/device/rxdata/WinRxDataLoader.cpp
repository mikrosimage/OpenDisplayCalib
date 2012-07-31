/*
 * WinRxDataLoader.cpp
 *
 *  Created on: 4 mars 2009
 *      Author: mfe
 */

#include "WinRxDataLoader.h"

WinRxDataLoader::WinRxDataLoader() {

}

WinRxDataLoader::~WinRxDataLoader() {
}

#ifdef __WIN32__ 
#include <winsock2.h>
#include <windows.h>
#include <iphlpapi.h>

int WinRxDataLoader::loadRxData(std::string & hostName, std::string & domain) {
	////get host name
	static int init_done=0;
	WORD wVersionRequested;
	WSADATA wsaData;

	if( ! init_done )
	{
		wVersionRequested = MAKEWORD( 1, 0 );
		if( WSAStartup( wVersionRequested, &wsaData ) == 0 )
		init_done = 1;
	}
	char name[255];
	gethostname(name, 255);
	hostName.append(name);

	//get DNS Suffixe
	FIXED_INFO *net_params = NULL;
	unsigned long length = 0;

	GetNetworkParams(net_params, &length);
	net_params = static_cast<FIXED_INFO *>(::operator new(length));
	GetNetworkParams(net_params, &length);

	domain.append(net_params->DomainName);
	::operator delete(net_params);

	return 0;
}
#endif
