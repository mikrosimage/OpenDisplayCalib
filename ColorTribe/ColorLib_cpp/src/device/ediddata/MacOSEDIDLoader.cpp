/*
 * LinEDIDLoader.cpp
 *
 *  Created on: 23 janv. 2009
 *      Author: mfe
 */

#include "MacOSXEDIDLoader.h"

#ifdef __APPLE__

#include "EDIDData.h"
#include <vector>
#include <iostream>

using namespace std;

#include <assert.h>
#include <stdio.h>
#include <unistd.h>

const static unsigned EDID_BLOCK_SIZE = 128;

extern "C" {

#include <IOKit/IOKitLib.h>
#include <ApplicationServices/ApplicationServices.h>
#include <IOKit/i2c/IOI2CInterface.h>
	//#include <Foundation/NSString.h>


	////test
	static const int screenNumberMax = 10;

	typedef struct osXScreenRef {
		CGDirectDisplayID *activeDspys;
		CGDisplayCount dspyCnt;
		int numScr;
	}osXScreenRef;

	void* getScreenContext(const int screenIndex) {
		osXScreenRef *ref = (osXScreenRef*)calloc( 1, sizeof(osXScreenRef));
		if (ref) {
			// Lists all active displays.
			ref->activeDspys = (CGDirectDisplayID*)calloc(screenNumberMax,
					sizeof(CGDirectDisplayID));
			ref->dspyCnt = 0;
			/*CGDisplayErr err =*/CGGetActiveDisplayList(screenNumberMax,
					ref->activeDspys, &(ref->dspyCnt));
			ref->numScr = screenIndex;
		}
		return (void*)ref;
	}
	////

	SInt32 EDIDSum( const UInt8 * bytes, IOByteCount len )
	{
		unsigned int i,j;
		UInt8 sum;

		for (j=0; j < len; j += 128)
		{
			sum = 0;
			for (i=0; i < 128; i++)
			sum += bytes[j+i];
			if(sum)
			return (j/128);
		}
		return (-1);
	}

	void EDIDDump( const UInt8 * bytes, IOByteCount len )
	{
		unsigned int i;

		fprintf(stderr, "/*    0    1    2    3    4    5    6    7    8    9    A    B    C    D    E    F */");
		for (i = 0; i < len; i++)
		{
			if( 0 == (i & 15))
			fprintf(stderr, "\n    ");
			fprintf(stderr, "0x%02x,", bytes[i]);
		}
		fprintf(stderr, "\n");
	}

	void EDIDRead( IOI2CConnectRef connect, vector<EDIDData> &edidDataVector)
	{
		kern_return_t kr;
		IOI2CRequest request;
		UInt8 data[EDID_BLOCK_SIZE];
		int i;

		bzero( &request, sizeof(request) );

		request.commFlags = 0;

		request.sendAddress = 0xA0;
		request.sendTransactionType = kIOI2CSimpleTransactionType;
		request.sendBuffer = (vm_address_t) &data[0];
		request.sendBytes = 0x01;
		data[0] = 0x00;

		request.replyAddress = 0xA1;
		request.replyTransactionType = kIOI2CSimpleTransactionType;
		request.replyBuffer = (vm_address_t) &data[0];
		request.replyBytes = sizeof(data);
		bzero( &data[0], request.replyBytes );

		kr = IOI2CSendRequest( connect, kNilOptions, &request );
		assert( kIOReturnSuccess == kr );
		//fprintf(stderr, "/* Read result 0x%x, 0x%lx bytes */\n", request.result, request.replyBytes);
		if( kIOReturnSuccess != request.result)
		return;

		//EDIDDump( data, request.replyBytes );

		i = EDIDSum( &data[0], request.replyBytes );

		if( i >= 0) {
			//fprintf(stderr, "/* Block %d checksum bad */\n", i);
			EDIDData voidData;
			edidDataVector.push_back(voidData);
		}
		else {
			//			fprintf(stderr, "/* Checksums ok */\n");
			EDIDData edid(data, EDID_BLOCK_SIZE);
			edidDataVector.push_back(edid);
		}

		//		if( save)
		//		write( STDOUT_FILENO, data, request.replyBytes );
	}

	int getEDIDs( vector<EDIDData> &edidDataVector )
	{
		kern_return_t kr;
		io_service_t framebuffer, interface;
		io_string_t path;
		IOOptionBits bus;
		IOItemCount busCount;

		osXScreenRef* ref =(osXScreenRef*) getScreenContext(0);
		int nbScreen = ref->dspyCnt;
		for(int i = 0 ; i < nbScreen ; i++) {
			framebuffer = CGDisplayIOServicePort(ref->activeDspys[i]);

			{
				kr = IORegistryEntryGetPath(framebuffer, kIOServicePlane, path);
				assert( KERN_SUCCESS == kr );

				kr = IOFBGetI2CInterfaceCount( framebuffer, &busCount );
				assert( kIOReturnSuccess == kr );
				for( bus = 0; bus < busCount; bus++ )
				{
					IOI2CConnectRef connect;

					kr = IOFBCopyI2CInterfaceForBus(framebuffer, bus, &interface);
					if( kIOReturnSuccess != kr)
					continue;

					kr = IOI2CInterfaceOpen( interface, kNilOptions, &connect );

					IOObjectRelease(interface);
					assert( kIOReturnSuccess == kr );
					if( kIOReturnSuccess != kr)
					continue;

					EDIDRead( connect, edidDataVector );

					IOI2CInterfaceClose( connect, kNilOptions );
				}
			}
		}

		return(0);
	}

}

int MacOSXEDIDLoader::loadEDID(std::vector<EDIDData> &edidVector) {
	return getEDIDs(edidVector);
}

#endif
