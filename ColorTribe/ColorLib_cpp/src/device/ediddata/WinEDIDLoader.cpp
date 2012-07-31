/*
 * WinEDIDLoader.cpp
 *
 *  Created on: 23 janv. 2009
 *      Author: mfe
 */

#include "WinEDIDLoader.h"

#ifdef __WIN32__ 

#include "windows.h"
#include "setupapi.h"
#include "initguid.h"
#include "stdio.h"
#include "EDIDData.h"
#include <vector>
#include <tchar.h>
#include "Winreg.h"

#define NAME_SIZE   128
#define PRINT(_x_) printf _x_

using namespace std;

DEFINE_GUID (GUID_CLASS_MONITOR,
		0x4d36e96e, 0xe325, 0x11ce, 0xbf, 0xc1, 0x08, 0x00, 0x2b, 0xe1,
		0x03, 0x18);

#define REGSTR_VAL_INFPATH      TEXT("InfPath")
#define REGSTR_VAL_PROVIDER_NAME                TEXT("ProviderName")
#define REGSTR_VAL_INFSECTION   TEXT("InfSection")
#define REGSTR_VAL_DRVDESC      TEXT("DriverDesc")
//#define REGSTR_VAL_DEVICEDRIVER TEXT("DeviceDriver")    // value of DRV
#define REGSTR_VAL_MATCHINGDEVID TEXT("MatchingDeviceId")
//#define REGSTR_VAL_PORTNAME     TEXT("PortName")
//#define REGSTR_VAL_PROPERTIES   TEXT("Properties")


BOOL FindADriverInfo(HDEVINFO Devs,PSP_DEVINFO_DATA DevInfo, LPCTSTR valueType) {
	SP_DEVINSTALL_PARAMS deviceInstallParams;

	WCHAR driverValue[LINE_LEN];
	HKEY hKey = NULL;
	DWORD RegDataLength;
	DWORD RegDataType;

	long regerr;
	//
	// The following method works in Win2k, but it's slow and painful.
	//
	// First, get driver key - if it doesn't exist, no driver
	//
	hKey = SetupDiOpenDevRegKey(Devs,
			DevInfo,
			DICS_FLAG_GLOBAL,
			0,
			DIREG_DRV,
			KEY_READ
	);

	if(hKey == INVALID_HANDLE_VALUE) {
		//
		// no such value exists, so there can't be an associated driver
		//
		RegCloseKey(hKey);
		return FALSE;
	}

	//
	// obtain
	//
	RegDataLength = sizeof(deviceInstallParams.DriverPath); // bytes!!!
	regerr = RegQueryValueEx(hKey,valueType,NULL, &RegDataType,(PBYTE)driverValue, &RegDataLength );

	if((regerr != ERROR_SUCCESS) || (RegDataType != REG_SZ)) {
		//
		// no such value exists, so no associated driver
		//
		RegCloseKey(hKey);
		return FALSE;
	} else {
		printf ("%s = %s\n",(char*)valueType ,(char*) driverValue);
	}
	RegCloseKey(hKey);
	return TRUE;
}

static void getEDID(IN HDEVINFO devInfo, IN PSP_DEVINFO_DATA devInfoData, vector<
		EDIDData> &edidDataVector) {

	HKEY hDevRegKey;
	DWORD uniID[123];

	if (SetupDiGetDeviceRegistryProperty(
					devInfo, devInfoData, SPDRP_DEVICEDESC,//SPDRP_UI_NUMBER,
					NULL, (PBYTE) (&uniID), sizeof(uniID), NULL)) {
		//printf("UID: %s\n", (char*)uniID); // ??
	} else {
		printf("ERROR: %d\n", (int)GetLastError());
		//TODO : throw exception
	}

	//SetupDiOpenDevRegKey( DeviceInfoSet,  DeviceInfoData,  Scope,  HwProfile,  KeyType,  samDesired );
	hDevRegKey = SetupDiOpenDevRegKey(devInfo, devInfoData, DICS_FLAG_GLOBAL,
			0, DIREG_DEV, KEY_QUERY_VALUE);

	if (hDevRegKey) {
		LONG retValue, i;
		DWORD dwType, AcutalValueNameLength = NAME_SIZE;

		CHAR valueName[NAME_SIZE];
		for (i = 0, retValue = ERROR_SUCCESS; retValue != ERROR_NO_MORE_ITEMS && retValue != ERROR_INVALID_HANDLE; i++) {
			unsigned char ctabEDIDdata[1048];

			//DWORD j ;
			DWORD edidsize = sizeof(ctabEDIDdata);

			retValue = RegEnumValue(hDevRegKey, i, &valueName[0],
					&AcutalValueNameLength, NULL,//reserved
					&dwType, ctabEDIDdata, // buffer
					&edidsize); // buffer size
			//printf(" avt valueName %s \n", valueName);
			if (retValue == ERROR_SUCCESS) {
				//printf("valueName %s\n", valueName);
				if (!strcmp(valueName, "EDID")) {
					//	unsigned char * vectorValue = new unsigned char[1024];
					//	memcpy(vectorValue, ctabEDIDdata, 1024);
					EDIDData data(ctabEDIDdata, edidsize);
					edidDataVector.push_back(data);
					//
					//FindADriverInfo(devInfo,devInfoData, REGSTR_VAL_PROVIDER_NAME);
					//					//					FindADriverInfo(devInfo,devInfoData, REGSTR_VAL_DRVDESC);
					//					//					FindADriverInfo(devInfo,devInfoData, REGSTR_VAL_MATCHINGDEVID);
					//					//					FindADriverInfo(devInfo,devInfoData, REGSTR_VAL_INFSECTION);
					//
					break;
				}

			}
		}

		RegCloseKey(hDevRegKey);
	} else {
		printf("ERROR:%d\n", (int)GetLastError());
	}

}

int WinEDIDLoader::loadEDID(std::vector<EDIDData> &edidVector) {

	HDEVINFO devInfo = NULL;
	SP_DEVINFO_DATA devInfoData;
	//SP_DEVINFO_LIST_DETAIL_DATA devInfoSetDetailData;
	ULONG i = 0;

	do {
		devInfo = SetupDiGetClassDevsEx(&GUID_CLASS_MONITOR, //class GUID
				NULL, //enumerator
				NULL, //HWND
				DIGCF_PRESENT, // Flags //DIGCF_ALLCLASSES|
				NULL, // device info, create a new one.
				NULL, // machine name, local machine
				NULL);// reserved
		if (NULL == devInfo) {
			break;
		}

		for (i = 0;ERROR_NO_MORE_ITEMS != GetLastError(); i++) {
			memset(&devInfoData, 0, sizeof(devInfoData));
			devInfoData.cbSize = sizeof(devInfoData);

			if (SetupDiEnumDeviceInfo(devInfo, i, &devInfoData)) {
				getEDID(devInfo, &devInfoData, edidVector);
			}
		}

	}while (FALSE);
	return i;
}

#endif
