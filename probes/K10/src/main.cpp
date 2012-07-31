/*
 * main.cpp
 *
 *  Created on: 28 avr. 2010
 *      Author: mfe
 */
#include "K10.h"
#include <cstring>
#include <memory>
#if defined(__MSVC__)
#define DllExport   __declspec( dllexport )
#else
#define DllExport
#endif

extern "C" {
struct XYZ{
	XYZ():X(0),Y(0),Z(0){}
	float X;
	float Y;
	float Z;
};
std::auto_ptr<K10> k10Probe;
DllExport const char* isK10Connected() {
	return K10::isConnected().c_str();
}

DllExport const char* initK10(const char* port) {
	k10Probe.reset(new K10(port));
	return k10Probe->init().c_str();
}

DllExport XYZ getXYZ() {
	XYZ values;
	if (k10Probe.get() != NULL) {
		float X,Y,Z;
		k10Probe->getXYZ(X,Y,Z);
		values.X = X;
		values.Y = Y;
		values.Z = Z;
		return values;
	}
	return values;

}
DllExport void releaseK10() {
	if (k10Probe.get() != NULL) {
		k10Probe->release();
	}
}


}
