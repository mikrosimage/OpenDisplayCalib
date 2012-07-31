/*
 * test.cpp
 *
 *  Created on: 27 avr. 2010
 *      Author: mfe
 */

#include <windows.h>
#include <winbase.h>
#include <stdio.h>
#include <conio.h>
#include <string.h>
#include <cmath>
#include "K10.h"
#include <iostream>
using namespace std;

int main() {
	string port = K10::isConnected();
	cout << "K-10 is connected on port " << port << endl;
	K10 probe(port);
	string serial = probe.init();
	cout << "probe serial : " << serial << endl;
	float X = 0, Y = 0, Z = 0;
	probe.getXYZ(X, Y, Z);
	float sumXYZ = X + Y + Z;
	float x = X / sumXYZ;
	float y = Y / sumXYZ;
	printf("measure : %f %f %f\n", x, y, Y);
	printf("measure : %f %f %f\n", X, Y, Z);
	probe.release();
	return 0;
}
