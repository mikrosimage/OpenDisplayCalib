/*
 * ZooperLocalHost.cpp
 *
 *  Created on: 8 oct. 2009
 *      Author: mfe
 */

#include "ZooperLocalHost.h"
#include "ColorKeeperModel.h"
#ifdef __WIN32__
#include "windows.h"
#endif
#ifdef __linux__	
#include <GNULUTLoader.h>
#endif

#include <fstream>
#include <iostream>
#include <sstream>
#include <cstdlib>
#include <QDesktopWidget>
#include <QApplication>

using namespace std;

////functor
//struct rectSort {
//	bool _isQTIndexDisorder;
//	rectSort(bool isQTIndexDisorder) :
//		_isQTIndexDisorder(isQTIndexDisorder) {
//
//	}
//	bool operator()(unsigned int a, unsigned int b) {
//		QDesktopWidget qdw;
//		QRect rectA = qdw.screenGeometry(a);
//		QRect rectB = qdw.screenGeometry(b);
//
//		int x, y, width, height;
//		rectA.getRect(&x, &y, &width, &height);
//		rectB.getRect(&x, &y, &width, &height);
//		if (_isQTIndexDisorder) // means
//			return !(rectB.x() > rectA.x());
//		else
//			return (rectB.x() > rectA.x());
//	}
//};

void ZooperLocalHost::searchDisorders() {

	QSettings settings(getIniFilePath(), QSettings::IniFormat);
	QDesktopWidget* qdw = QApplication::desktop();
	///// retrieve metrics
	_qtNbScreens = qdw->numScreens(); //in QT 4.6, should be qdw->screenCount()

	_xNbScreens = getXScreensNumber();
	_edidNbScreens = _autoHost.getDisplayDeviceNumber();
	_calibableScreenNumber = _xNbScreens;
	ostringstream os;

	os << "Screens count : QT found " << (int) _qtNbScreens << ", DDS found "
			<< (int) _edidNbScreens << ", X found " << (int) _xNbScreens;

	/// Interpret find the real number of screens
#ifdef __WIN32__
	_visibleScreen = GetSystemMetrics(80);//// SM_CMONITORS
	os<<", Visible screens :  "<<(int) _visibleScreen;
#endif
	os << endl;
	ColorKeeperModel::logMessage(os.str());

	os.clear();
#ifdef __linux__
	if(_qtNbScreens == _xNbScreens && _xNbScreens == _edidNbScreens) { //OK
		_effectiveScreenNumber = _qtNbScreens;
		_calibableScreenNumber = _xNbScreens;

	} else if(_qtNbScreens == _xNbScreens) {//one or more screen(s) may be plugged but not configured
		_effectiveScreenNumber = _qtNbScreens;
		_calibableScreenNumber = _xNbScreens;
		os <<"Plugged but not configured screen"<<endl;
		os <<"_effectiveScreenNumber = "<< _effectiveScreenNumber<<endl;
		os <<"_calibableScreenNumber = "<< _calibableScreenNumber<<endl;
		_isPlugged_But_UnusedScreenDisorder = true;

	} else if(_xNbScreens == _edidNbScreens) {//separate x screen configuration
		_effectiveScreenNumber = _edidNbScreens;
		_calibableScreenNumber = _edidNbScreens;//_qtNbScreens;
		os << "separate XScreen disorder" << endl;
		os <<"_effectiveScreenNumber = "<< _effectiveScreenNumber<<endl;
		os <<"_calibableScreenNumber = "<< _calibableScreenNumber<<endl;
		_isSeparateXScreenDisorder = true;
	} else if(_qtNbScreens == _edidNbScreens) {// one x screen for several screens	

		if(GNULUTLoader::isScreenGammaAble(1)){
			os<<"XScreenDisorder suppected but several LUTs can be loaded. Calibrable devices : "<<_qtNbScreens<<endl;
			_effectiveScreenNumber = _qtNbScreens;
			_calibableScreenNumber = _qtNbScreens;
		}else{
			_effectiveScreenNumber = _edidNbScreens;
			_calibableScreenNumber = _xNbScreens;
			os<<"XScreen disorder"<<endl;
			os <<"_effectiveScreenNumber = "<< _effectiveScreenNumber<<endl;
			os <<"_calibableScreenNumber = "<< _calibableScreenNumber<<endl;
			_isXScreenDisorder = true;
		}

	} else if(_qtNbScreens != _edidNbScreens && _qtNbScreens>_edidNbScreens) {
		os<<"XScreen disorder and one or more EDID(s) not found"<<endl;
		os <<"_effectiveScreenNumber = "<< _effectiveScreenNumber<<endl;
		os <<"_calibableScreenNumber = "<< _calibableScreenNumber<<endl;
		_isXScreenDisorder = true;
		_isMoreDesktopThanEdid = true;
		_effectiveScreenNumber = _xNbScreens;
		_calibableScreenNumber = _xNbScreens;

		

	} else {
		os<<"Unexpected disorder. Try with nbScreen = "<<_xNbScreens<<endl;
		os <<"_effectiveScreenNumber = "<< _effectiveScreenNumber<<endl;
		os <<"_calibableScreenNumber = "<< _calibableScreenNumber<<endl;
		_effectiveScreenNumber = _xNbScreens;
		_calibableScreenNumber = _xNbScreens;
		_isUnexpectedIssue = true;
	}
#endif
#ifdef __WIN32__

	if(_qtNbScreens == _xNbScreens && _xNbScreens == _edidNbScreens && _edidNbScreens == _visibleScreen) { //OK
		_effectiveScreenNumber = _qtNbScreens;
		_calibableScreenNumber = _xNbScreens;
	} else if(_qtNbScreens < _edidNbScreens && _qtNbScreens == _visibleScreen ) {//one or more screen(s) may be plugged but not configured
		_effectiveScreenNumber = _qtNbScreens;
		_calibableScreenNumber = _qtNbScreens;
		os <<"Plugged but not configured screen or splitter cable issue"<<endl;

		_isPlugged_But_UnusedScreenDisorder = true;
	} else if(_qtNbScreens > _edidNbScreens && _qtNbScreens == _visibleScreen ) {//EDID not found
		_effectiveScreenNumber = _qtNbScreens;
		_calibableScreenNumber = _qtNbScreens;
		int difference = _qtNbScreens - _edidNbScreens;
		os <<difference<<" EDID(s) not found"<<endl;
		_isMoreDesktopThanEdid = true;
	} else if(_qtNbScreens != _visibleScreen) {
		_effectiveScreenNumber = _visibleScreen;
		_calibableScreenNumber = _visibleScreen;
		os <<"QT count error"<<endl;
		_isQTNbScreenDisorder =true;
	} else {
		os<<"Unexpected disorder. Try with nbScreen = "<<_visibleScreen<<endl;
		_effectiveScreenNumber = _visibleScreen;
		_calibableScreenNumber = _visibleScreen;
		_isUnexpectedIssue = true;
	}
#endif
	ColorKeeperModel::logMessage(os.str());
	os.clear();
	///// find fake EDID


	///// find primary screen

	_primaryScreenID = getMainScreenID(settings);

	if (qdw->primaryScreen() != (int) _primaryScreenID) {
		//_isQTIndexDisorder = true;
		os << "QT Primary index != from Nvidia's." << endl;
		ColorKeeperModel::logMessage(os.str());
		os.clear();
	}
	os << "Primary screen : " << (int) _primaryScreenID << endl;
	ColorKeeperModel::logMessage(os.str());
	os.clear();

	///// fill calibrableDevices
	if ((!_isXScreenDisorder && !_isQTNbScreenDisorder
			&& !_isSeparateXScreenDisorder
			&& !_isPlugged_But_UnusedScreenDisorder && !_isMoreDesktopThanEdid)
			|| _isQTNbScreenDisorder) {
		DISPDEV_CITR it;
		const DISPDEV_VECTOR& displayDevices = _autoHost.getDisplayDevices();
		for (it = displayDevices.begin(); it != displayDevices.end(); it++) {
			ZooperDisplayDevice display((*it));
			_calibrableDevices.push_back(display);
		}
	} else if (_isMoreDesktopThanEdid) {
		int difference = _qtNbScreens - _edidNbScreens;
		if (difference == (int) _calibableScreenNumber) {//simple case
			for (unsigned int i = 0; i < _calibableScreenNumber; i++) {
				ZooperDisplayDevice display(i);
				_calibrableDevices.push_back(display);
			}
		} else {
			vector<unsigned int> missingID;
			for (unsigned int i = 0; i < _calibableScreenNumber; i++) {
				const DISPDEV_VECTOR& displayDevices =
						_autoHost.getDisplayDevices();
				bool found = false;
				for (DISPDEV_CITR it = displayDevices.begin(); it
						!= displayDevices.end(); it++) {
					if ((*it).getOSIndex() == i) {
						found = true;
						ZooperDisplayDevice display((*it));
						_calibrableDevices.push_back(display);
						break;
					}
				}
				if (!found) {
					missingID.push_back(i);
				}
			}

			for (vector<unsigned int>::iterator intit = missingID.begin(); intit
					!= missingID.end(); intit++) {
				ZooperDisplayDevice display((*intit));
				_calibrableDevices.push_back(display);
			}
		}
	} else if (_isXScreenDisorder) {
		DISPDEV_CITR it;
		const DISPDEV_VECTOR& displayDevices = _autoHost.getDisplayDevices();

		for (it = displayDevices.begin(); it != displayDevices.end(); it++) {
			if (_primaryScreenID == (*it).getOSIndex()) {
				ZooperDisplayDevice display((*it), 0, (*it).getOSIndex());
				_calibrableDevices.push_back(display);
			} else {
				ZooperDisplayDevice display((*it), 0, (*it).getOSIndex());
				_notCalibrableDevices.push_back(display);
			}
		}
	} else if (_isSeparateXScreenDisorder) {
		QDesktopWidget qdw;
		QRect rectA = qdw.screenGeometry(0);
		int x, y, width, height;
		rectA.getRect(&x, &y, &width, &height);
		os << x << " " << y << " " << width << " " << height << endl;
		ColorKeeperModel::logMessage(os.str());
		os.clear();
		DISPDEV_CITR it;
		const DISPDEV_VECTOR& displayDevices = _autoHost.getDisplayDevices();
		for (it = displayDevices.begin(); it != displayDevices.end(); it++) {
			ZooperDisplayDevice display((*it), _primaryScreenID
					- (*it).getOSIndex(), 0);//TODO verifier le coup de primaryScreen -
			_calibrableDevices.push_back(display);
		}
	}
	//// find OSID PB (osID 1 alors qu'il n'ya qu'1 ecran par exemple)
	int found = 0;
	// FIXME calibable => calibratable ?
	for (unsigned int i = 0; i < _calibableScreenNumber; i++) {
		found = getDisplayIndexFromOSID(i);
		if (found == -1) {
			break;
		}
	}
	if (found == -1) {
		os << "Numbering issue" << endl;
		for (unsigned int i = 0; i < _calibableScreenNumber; i++) {
			// FIXME : if _calibableDevices is empty, it simply raise an out of range exception
			// and gently abort
			ZooperDisplayDevice* disp = &(_calibrableDevices.at(i));
			os << "-> Renumbering " << disp->getFullName() << " from "
					<< disp->getOSIndex() << " to " << i << endl;
			disp->setOSID(i);
		}
		ColorKeeperModel::logMessage(os.str());
	}

}

int ZooperLocalHost::getDisplayIndexFromOSID(const unsigned int &osIndex) const {
	ZOOPDEV_CITR it;
	int i = 0;
	for (it = _calibrableDevices.begin(); it != _calibrableDevices.end(); it++) {
		if ((*it).getOSIndex() == osIndex)
			return i;
		i++;
	}
	return -1;
}

ZooperLocalHost::ZooperLocalHost() :
	_isXScreenDisorder(false), _isQTIndexDisorder(false),
			_isQTNbScreenDisorder(false), _isSeparateXScreenDisorder(false),
			_isPlugged_But_UnusedScreenDisorder(false), _isMoreDesktopThanEdid(
					false), _isUnexpectedIssue(false) {
	searchDisorders();
}


QString ZooperLocalHost::getIniFilePath() const {
	QString message("./conf/");
	message.append(getHostName().c_str());
	message.append("/");
	message.append(getUserName().c_str());
	message.append(QString(".ini"));
	return message;
}

ZooperLocalHost::~ZooperLocalHost() {
}

void ZooperLocalHost::printInfo() {
	_autoHost.printInfo();
}

unsigned char ZooperLocalHost::getEffectiveDisplayDeviceNumber() const {
	return _effectiveScreenNumber;

}
unsigned char ZooperLocalHost::getCalibrableDisplayDeviceNumber() const {
	return _calibableScreenNumber;

}

unsigned char ZooperLocalHost::getNotCalibrableDisplayDeviceNumber() const {
	return _notCalibrableDevices.size();
}

const std::string& ZooperLocalHost::getHostName() const {
	return _autoHost.getHostName();
}
const std::string& ZooperLocalHost::getUserName() const {
	return _autoHost.getUserName();
}
const std::string& ZooperLocalHost::getDomainName() const {
	return _autoHost.getDomainName();
}

const ZooperDisplayDevice& ZooperLocalHost::getCalibrableDisplayDevice(
		const unsigned int &osIndex) const {
	int index = getDisplayIndexFromOSID(osIndex);
	if (index >= 0)
		return _calibrableDevices.at(index);
	ostringstream os;
	os << "Screen not found" << osIndex << endl;
	ColorKeeperModel::logMessage(os.str());
	throw new std::string("error"); //TODO exception
}
const ZooperDisplayDevice& ZooperLocalHost::getNotCalibrableDisplayDevice(
		const unsigned int &index) const {
	if (index < getNotCalibrableDisplayDeviceNumber())
		return _notCalibrableDevices.at(index);
	ostringstream os;
	os << "Screen not found" << index << endl;
	ColorKeeperModel::logMessage(os.str());
	throw new std::string("error"); //TODO exception
}

const unsigned char& ZooperLocalHost::getXScreensNumber() const {
	return _autoHost.getXScreensNumber();
}

unsigned int ZooperLocalHost::getMainScreenID(QSettings &settings) const {
	int index = settings.value("mainScreen/index", 99).toInt();
	if (index == 99) {
		index = deducePrimaryScreenID();
		QString key("mainScreen");
		settings.beginGroup(key);
		settings.setValue("index",index);
		settings.endGroup();
	}
	return index;
}

unsigned int ZooperLocalHost::deducePrimaryScreenID() const {
	QDesktopWidget qdw;
	int xorgPrimaryScreen = qdw.primaryScreen();

	ostringstream os;
	os << "QT primary screen found :" << xorgPrimaryScreen << endl;
	ColorKeeperModel::logMessage(os.str());
#ifdef __linux__
	string line;

	//TODO renforcer a
	ifstream xorgConf("/etc/X11/xorg.conf");
	if (xorgConf.is_open()) {
		while (!xorgConf.eof()) {
			getline(xorgConf, line);
			size_t found = line.find("TwinViewXineramaInfoOrder");
			if (found != string::npos) {
				found = line.find('#');//and line isn't a comment
				if (found == string::npos) {
					found = line.find('-');//and line isn't a comment
					if (found != string::npos) {
						if (found + 1 < line.size())
						{
						
							///////////////// old method /////////////						
//							ostringstream os;
//							os <<
//							"Xorg info found :"<< line << endl;
//							ColorKeeperModel::logMessage(os.str());
//							xorgPrimaryScreen = atoi(&line.at(found + 1));
							////////////// new method //////////////
							ostringstream os;

							// FIXME found-3<0 found is an unsigned int and 3 is considered as
							// an unsigned int, so found-3<0 is always false and continue is never executed
							// What was the original idea here ?
							//if(found-3<0 && found+1>= line.size() )
							//continue;
							std::string mask = line.substr(found -3,5 );

							os <<
							"TwinViewXineramaInfoOrder mask :"<< mask<< endl;
							ColorKeeperModel::logMessage(os.str());
							////find name
							const std::map<std::string,std::string> maskToName = _autoHost.getMaskToNameMap();

							///////////Test
							os.clear();

							for(map<std::string,std::string>::const_iterator it = maskToName.begin();it != maskToName.end();++it)
							os<<"pair : "<<(*it).first<<" + "<<(*it).second<<endl;
							ColorKeeperModel::logMessage(os.str());

							///////

							std::string name = (*maskToName.find(mask)).second;

							os.clear();
							os<<"Mask to name give : "<<name.c_str()<<endl;
							ColorKeeperModel::logMessage(os.str());
							////find DisplayDevice
							const DISPDEV_VECTOR& displayDevices = _autoHost.getDisplayDevices();
							for (DISPDEV_CITR dispDevIt = displayDevices.begin(); dispDevIt
									!= displayDevices.end(); ++dispDevIt) {
								if (name.find((*dispDevIt).getModelName())!=std::string::npos) {
									unsigned int osScreenIndex = (*dispDevIt).getOSIndex();
									os.clear();
									os<<"--> Primary Screen index is : "<<osScreenIndex <<" (according to NVlib)"<<endl;
									ColorKeeperModel::logMessage(os.str());
									xorgPrimaryScreen =osScreenIndex;
									xorgConf.close();
									return xorgPrimaryScreen;
								}

							}

							//name not found, we deduce index from the mask
							int deducedIndex = atoi(&mask.at(4));
							if(deducedIndex < getEffectiveDisplayDeviceNumber()) {
								xorgPrimaryScreen = deducedIndex;
								os.clear();
								os<<"--> Primary Screen index was deduced from mask : "<<xorgPrimaryScreen <<endl;
								ColorKeeperModel::logMessage(os.str());
							}
							
							///////////////////// end new method
						}
					}
				}
			}
		}

		xorgConf.close();
	}
#endif

	return xorgPrimaryScreen;
}

QString ZooperLocalHost::getScreenDisorderErrorText() const {
	int xNbScreens = getXScreensNumber();
	int edidNbScreens = _autoHost.getDisplayDeviceNumber();
	QString message(QString::number(xNbScreens));
	message.append(QString(" XScreen(s) found for "));
	message.append(QString::number(edidNbScreens));
	message.append(QString(" monitor(s).\n\n"));
	if (_isXScreenDisorder) {
		message.append(QString(" ::Explanation::\n"));
		message.append(
				QString(
						" On Linux, calibration relies on XScreens. In your dual screen set up, only one XScreen is shared by every\nmonitors.\n"));
		message.append(
				QString(
						" This means that only one calibration profil can be computed and will be shared by both screens. \n"));
		message.append(
				QString(
						" Consequently, calibration must be done on the reference monitor which is the primary \nscreen. \n"));
	}
	return message;
}
