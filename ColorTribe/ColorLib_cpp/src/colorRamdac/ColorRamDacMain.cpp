#include <iostream>
#include <sstream>
#include <cstdlib>

#include "lutloader/AbstractLutLoader.h"
#include "../calibration/correction/LUT2DCorrection.h"
#include "../exception/ColorRamDacException.h"
#include "../exception/CannotFindLutException.h"
#include "../exception/CannotGetLutException.h"
#include "../exception/CannotSetLutException.h"
#include "../exception/BinarySearchCurveException.h"
#include "../trivialLogger/tri_logger.hpp"

#ifdef __WIN32__ 
#include "lutloader/WinLUTLoader.h"
#endif
#ifdef __APPLE__
#include "lutloader/MacLUTLoader.h"
#endif
#ifdef __linux__
#include "lutloader/GNULUTLoader.h"
#endif

using namespace std;

void displayErrorAndQuit(ColorRamDacException *e) {
	ostringstream os;
	os << e->what() << endl;
	os << "ColorRamDac exit." << endl;
	TRI_MSG_STR(os.str());
	cout<<os.str()<<endl;
	exit(1);
}

int main(int argc, const char* argv[]) {
	ostringstream os;

	os << ":: ColorRamadac ::" << endl;
	TRI_MSG_STR(os.str());
	cout<<os.str()<<endl;
	os.clear();
	string path = "";
	AbstractLutLoader *lutLoader = NULL;
	int screenNumber = 0;
	if (argc == 2) {
		path = argv[1];
	} else if (argc == 3) {
		screenNumber = atoi(argv[1]);
		path = argv[2];
	} else {
		//		path = "./testFiles/_Cineon.gamma";
		//		cout<<"/!\\ Warning : default gamma file is used ("<<path<<")"<<endl;
		os << "Available syntaxes are : " << endl;
		os << "- ColorRamdac <path to the gamma file> " << endl;
		os << "- ColorRamdac <screen number> <path to the gamma file> " << endl;
		TRI_MSG_STR(os.str());
		cout<<os.str()<<endl;
		return 0;
	}
#ifdef __WIN32__ 
	lutLoader= new WinLUTLoader();
#endif
#ifdef __APPLE__
	lutLoader= new MacLUTLoader();
#endif
#ifdef __linux__
	lutLoader= new GNULUTLoader();
#endif
	try {
		LUT2DCorrection lut(path, 256);
		if (lutLoader != NULL) {
			int count;
			unsigned short * originalRamp = lutLoader->getGammaRamp(
					screenNumber, &count);
			LUT2DCorrection originalLut(originalRamp, count);

			delete originalRamp;
			lutLoader->setGammaRamp(screenNumber, &lut);

			os << "Your screen"<<screenNumber<<" is corrected with " << path << "." << endl;
			TRI_MSG_STR(os.str());
			cout<<os.str()<<endl;
			os.clear();
		}

#ifdef __APPLE__
		os<<"Appuyez sur entree pr quitter"<<endl;
		TRI_MSG_STR(os.str());
		cout<<os.str()<<endl;
		os.clear();
		getchar();
#endif
	}
	catch(CannotFindLutException cfle) {
		displayErrorAndQuit((ColorRamDacException*) &cfle);
	}
	catch(CannotGetLutException cgle) {
		displayErrorAndQuit((ColorRamDacException*) &cgle);
	}
	catch(CannotSetLutException csle) {
		displayErrorAndQuit((ColorRamDacException*) &csle);
	}
	catch( BinarySearchCurveException bsce) {
		displayErrorAndQuit((ColorRamDacException*) &bsce);
	}

	///Test binary Curve
	/*try{
	 vector<Point2D> v;
	 v.push_back( Point2D(0,0) );
	 v.push_back( Point2D(127,32767) );
	 v.push_back( Point2D(255,65535) );
	 BinarySearchCurve b(v);
	 for(int i = 0 ; i < 256 ; i++){
	 printf(" %d %d \n",(int) i ,(int) b.getValue(i));
	 }

	 }catch(	BinarySearchCurveException bsce){
	 displayErrorAndQuit((ColorRamDacException*)&bsce);
	 }*/

	return 0;
}
