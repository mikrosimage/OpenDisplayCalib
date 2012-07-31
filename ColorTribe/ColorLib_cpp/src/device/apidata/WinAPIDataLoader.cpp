/*
 * WinAPIDataLoader.cpp
 *
 *  Created on: 18 feb. 2009
 *      Author: mfe
 */

#include "WinAPIDataLoader.h"
#include "APIData.h"

#ifdef __WIN32__ 
#include <windows.h>
#include <wingdi.h>
#include <iostream>

//////
using namespace std;

int WinAPIDataLoader::loadAPIData(vector<APIData>  &apiVector) {

	DWORD iDevNum = 0;
	DWORD iMonNum = 0;
	DISPLAY_DEVICE lpDisplayDevice;

	DWORD dwFlags = 1;
	char* devName;

	lpDisplayDevice.cb = sizeof(lpDisplayDevice);

	while(EnumDisplayDevices(NULL, iDevNum, &lpDisplayDevice, dwFlags))
	{
		iDevNum++;

		if( lpDisplayDevice.StateFlags & DISPLAY_DEVICE_ATTACHED_TO_DESKTOP )
		{

			devName = lpDisplayDevice.DeviceName;
			iMonNum=0;

			DISPLAY_DEVICE dispdev2;
			memset(&dispdev2, 0, sizeof(DISPLAY_DEVICE));
			dispdev2.cb = sizeof(dispdev2);

			while(EnumDisplayDevices(devName,iMonNum, (DISPLAY_DEVICE*)&dispdev2,dwFlags )) {

				++iMonNum;

				if( dispdev2.StateFlags & DISPLAY_DEVICE_ATTACHED_TO_DESKTOP )
				{

					APIData data( lpDisplayDevice.DeviceName,dispdev2.DeviceID,lpDisplayDevice.DeviceString, lpDisplayDevice.DeviceID  );
					apiVector.push_back(data);

				}

				devName = lpDisplayDevice.DeviceName;
				memset(&dispdev2, 0, sizeof(DISPLAY_DEVICE));
				dispdev2.cb = sizeof(dispdev2);
			}
		}

		memset(&lpDisplayDevice, 0, sizeof(DISPLAY_DEVICE));
		lpDisplayDevice.cb = sizeof(lpDisplayDevice);

	}
	return 0;
}

int WinAPIDataLoader::dumpGDIIbnfo() {

	DWORD iDevNum = 0;
	DWORD iMonNum = 0;
	DISPLAY_DEVICE lpDisplayDevice;
	//DISPLAY_DEVICE dispdev2;
	DWORD dwFlags = 1;
	char* devName;

	lpDisplayDevice.cb = sizeof(lpDisplayDevice);
	//dispdev2.cb = sizeof(dispdev2);

	while(EnumDisplayDevices(NULL, iDevNum, &lpDisplayDevice, dwFlags))
	{
		iDevNum++;

		if( lpDisplayDevice.StateFlags & DISPLAY_DEVICE_ATTACHED_TO_DESKTOP )
		{
			cout<<"GDI *******monitor "<<iDevNum<<"**********"<<endl;
			//cout << "Display Device: "<< iDevNum << endl;
			cout << " Device Name: " << lpDisplayDevice.DeviceName << endl;
			cout << " Device Description: " << lpDisplayDevice.DeviceString << endl;
			cout << " Device Status: " << lpDisplayDevice.StateFlags<< endl; //DISPLAY_DEVICE_PRIMARY_DEVICE //
			cout << " Device ID : " << lpDisplayDevice.DeviceID << endl;
			cout << " Device key : " << lpDisplayDevice.DeviceKey << endl;

			// To get monitor info for a display device, call EnumDisplayDevices
			// a second time, passing dispdev.DeviceName (from the first call) as
			// the first parameter.
			devName = lpDisplayDevice.DeviceName;
			iMonNum=0;

			DISPLAY_DEVICE dispdev2;
			memset(&dispdev2, 0, sizeof(DISPLAY_DEVICE));
			dispdev2.cb = sizeof(dispdev2);

			while(EnumDisplayDevices(devName,iMonNum, (DISPLAY_DEVICE*)&dispdev2,dwFlags )) {

				++iMonNum;

				if( dispdev2.StateFlags & DISPLAY_DEVICE_ATTACHED_TO_DESKTOP )
				{
					if((dispdev2.StateFlags & DISPLAY_DEVICE_PRIMARY_DEVICE) == 0) {
						cout << "Primary device" << endl;
					} else cout << "Secondary device" << endl;

					cout << " Monitor Name: " << dispdev2.DeviceName << endl;
					cout << " Monitor Description: " << dispdev2.DeviceString << endl;
					cout << " Monitor Status: " << dispdev2.StateFlags << endl; //DISPLAY_DEVICE_PRIMARY_DEVICE
					cout << " Monitor ID : " << dispdev2.DeviceID << endl;
					cout << " Monitor key : " << dispdev2.DeviceKey << endl;
				}

				devName = lpDisplayDevice.DeviceName;
				memset(&dispdev2, 0, sizeof(DISPLAY_DEVICE));
				dispdev2.cb = sizeof(dispdev2);
			}
		}

		memset(&lpDisplayDevice, 0, sizeof(DISPLAY_DEVICE));
		lpDisplayDevice.cb = sizeof(lpDisplayDevice);

	}

	return 0;

}

#endif
