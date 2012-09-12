/*
 * ColorKeeperModel.cpp
 *
 *  Created on: 9 mars 2009
 *      Author: mfe
 */

#include "ColorKeeperModel.h"
#include "ZooperLocalHost.h"
#include "../gui/GuiSizeDef.h"

#include <tri_logger.hpp>
#include <DisplayDevice.h>
#include <DisplayDeviceScout.h>

#include <AbstractLutLoader.h>

#include <ColorRamDacException.h>
#include <CannotFindLutException.h>
#include <CannotGetLutException.h>
#include <CannotSetLutException.h>
#include <BinarySearchCurveException.h>

#ifdef __WIN32__ 
#include <WinLUTLoader.h>
#endif
#ifdef __APPLE__
#include <MacLUTLoader.h>
#endif
#ifdef __linux__
#include <GNULUTLoader.h>

#endif

#include <cstdlib>
#include <cstdio>
#include <QDesktopWidget>
#include <QString>
#include <QStringList>
#include <QStringListIterator>
#include <QDir>
#include <QSettings>
#include <QApplication>
#include <QFile>
#include <QFileInfo>
#include <QDateTime>

#include <cmath>
#include <algorithm>
#include <map>
#include <fstream>
#include <iostream>
#include <sstream>
#include <cstdlib>
using namespace std;

const string ColorKeeperModel::CORRECTION_PATH = "./corrections/";
const string ColorKeeperModel::LOCK_PATH = "./lock/";
const string ColorKeeperModel::CALIB_REQUEST_PATH = "./calibrequests/";
const string ColorKeeperModel::INVALID_SCREEN_PATH = "./blacklist/";
const string ColorKeeperModel::CUSTOM_LUTS_PATH = "./custom_luts/";
const string ColorKeeperModel::CUSTOM_PATTERNS_PATH = "./custom_patterns/";
const string ColorKeeperModel::MAILER_INI_PATH = "./mailer.ini";
const string ColorKeeperModel::version_minor = ".0";
const float ColorKeeperModel::s_gammaUpBound = 3.2f;
const float ColorKeeperModel::s_gammaDownBound = 0.3f;
const float ColorKeeperModel::s_PLUpBound = 128.f;
const float ColorKeeperModel::s_PLDownBound = -128.f;
const unsigned int ColorKeeperModel::s_ProfilTimeLimit = 7; //90;
const unsigned int ColorKeeperModel::s_CronTimingMinutes = 30;
bool ColorKeeperModel::shouldDelete = false;
QString ColorKeeperModel::s_smtpServer;
QString ColorKeeperModel::s_mailFrom;
QString ColorKeeperModel::s_mailTo;
bool ColorKeeperModel::_isMailRequestEnable;
const QString ColorKeeperModel::s_unsetValue = "none";
//functor

struct rectSort {
	bool _isQTIndexDisorder;
	rectSort(bool isQTIndexDisorder) :
			_isQTIndexDisorder(isQTIndexDisorder) {

	}
	bool operator()(unsigned int a, unsigned int b) {
		QDesktopWidget qdw;
		QRect rectA = qdw.screenGeometry(a);
		QRect rectB = qdw.screenGeometry(b);

		int x, y, width, height;
		rectA.getRect(&x, &y, &width, &height);
		rectB.getRect(&x, &y, &width, &height);
		if (_isQTIndexDisorder) // means
			return !(rectB.x() > rectA.x());
		else
			return (rectB.x() > rectA.x());
	}
};

void ColorKeeperModel::getOut() {
	//TODO remove (was for the protec)
}

ColorKeeperModel::ColorKeeperModel() :
		_calibWindowsZoom(1.0f), _calibWindowsTranslateX(0), _calibWindowsTranslateY(
				0), _mailer(NULL) {

	/////init mailer
	_isMailRequestEnable = false;
	QSettings mailSettings(QString(MAILER_INI_PATH.c_str()),
			QSettings::IniFormat);
	s_smtpServer =
			mailSettings.value("mailer/smtpServer", s_unsetValue).toString();
	s_mailFrom = mailSettings.value("mailer/from", s_unsetValue).toString();
	s_mailTo = mailSettings.value("mailer/to", s_unsetValue).toString();

	if (s_smtpServer.compare(s_unsetValue) != 0
			&& s_mailFrom.compare(s_unsetValue) != 0
			&& s_mailTo.compare(s_unsetValue) != 0) { //TODO add some tests here
		_isMailRequestEnable = true;
		ostringstream os;
		os << "Mailer settings  : " << s_smtpServer.toStdString() << " "
				<< s_mailFrom.toStdString() << " " << s_mailTo.toStdString()
				<< endl;
		ColorKeeperModel::logMessage(os.str());

	}

	ColorKeeperModel::logMessage("Bienvenu !");
	cron = NULL;
	_deviceInfo = new ZooperLocalHost();

	QSettings settings(getIniFilePath(), QSettings::IniFormat);

	string path = CORRECTION_PATH;
	path.append("default.gamma");
	_default = new LUT2DCorrection(path, 256);
	for (int i = 0; i < _deviceInfo->getCalibrableDisplayDeviceNumber(); i++) {
		_shouldApplyCorrection.push_back(true);
		_isCustomEDID.push_back(false);
		_screenOrder.push_back(i);
		QString key("screen");
		key.append(QString::number(i));
		settings.beginGroup(key);
		QString ipAdress = (QString) settings.value("ipAdress").toString();
		_ipAdressSetting.push_back(ipAdress);
		settings.endGroup();
	}

	sort(_screenOrder.begin(), _screenOrder.end(),
			rectSort(_deviceInfo->isQTIndexDisorder()));

	//init
	initCustomLuts();
	initCustomPatterns();
	initCorrections();
	launchCron();
}

void ColorKeeperModel::launchCron() {

	if (cron == NULL) {
		cron = new CronThread(this);
	}

}

void CronThread::run()

{

	int sleep = ColorKeeperModel::s_CronTimingMinutes * 60 * 1000;
	while (runCron) {
		QThread::msleep(sleep);
		if (!runCron)
			return;
		ColorKeeperModel & model = ColorKeeperModel::Instance();
		model.applyCorrections();
		ostringstream os;
		os << "Cron apply correction : "
				<< QDateTime::currentDateTime().toString().toStdString()
				<< endl;
		ColorKeeperModel::logMessage(os.str());
	}

}

ColorKeeperModel::ColorKeeperModel(const ColorKeeperModel &) {
}

ColorKeeperModel::~ColorKeeperModel() {
	if (_default != NULL)
		delete _default;
	if (cron != NULL) {
		cron->stopCron();
		delete cron;
	}
	delete _deviceInfo;

}
void ColorKeeperModel::writeSettings() const {
	QSettings settings(getIniFilePath(), QSettings::IniFormat);
	int calibableScreen = _deviceInfo->getCalibrableDisplayDeviceNumber();
	for (int i = 0; i < calibableScreen; i++) {
		const DisplayDevice &dev = _deviceInfo->getCalibrableDisplayDevice(i);
		unsigned int osScreenIndex = dev.getOSIndex();
		QString key("screen");
		key.append(QString::number(osScreenIndex));
		settings.beginGroup(key);
		settings.setValue("gamma",
				((*_correctionLuts.find(osScreenIndex)).second).getGamma());
		settings.setValue("redPL",
				((*_correctionLuts.find(osScreenIndex)).second).getRedPL());
		settings.setValue("greenPL",
				((*_correctionLuts.find(osScreenIndex)).second).getGreenPL());
		settings.setValue("bluePL",
				((*_correctionLuts.find(osScreenIndex)).second).getBluePL());
		settings.setValue("ipAdress", _ipAdressSetting[osScreenIndex]);
		settings.endGroup();
		////
		key.prepend("EDID_");
		settings.beginGroup(key);
		settings.setValue("manufacturer", dev.getManufacturerName().c_str());
		settings.setValue("model", dev.getModelName().c_str());
		settings.setValue("UID", dev.getFullSerialNumber().c_str());
		settings.setValue("CustomEDID", _isCustomEDID[osScreenIndex]);
		settings.endGroup();
	}

	////

}

QString ColorKeeperModel::getIpAdress(const unsigned int &screenIndex) {
	return _ipAdressSetting[screenIndex];
}
void ColorKeeperModel::setIpAdress(const unsigned int &screenIndex,
		QString ipadress) {
	_ipAdressSetting[screenIndex] = ipadress;

}
ColorKeeperModel & ColorKeeperModel::operator=(const ColorKeeperModel &) {
	return *this;
}

float ColorKeeperModel::getBGColorFromGamma(const float & gamma) {
	return (pow(10, log(0.18 / (1 / gamma))));
}

//unsigned char ColorKeeperModel::getDisplayDeviceNumber() const {
//	return _deviceInfo->getEffectiveDisplayDeviceNumber();
//}

const std::string& ColorKeeperModel::getHostName() const {
	return _deviceInfo->getHostName();
}

const CKSocketClient& ColorKeeperModel::getSocketServer() const {
	return _socketClient;
}
void ColorKeeperModel::startSocketServer(const QString &serverAddress,
		unsigned int osScreenIndex) {
	_socketClient.startCom(serverAddress, osScreenIndex);

}

void ColorKeeperModel::applyCorrections() const {
	int calibableScreen = _deviceInfo->getCalibrableDisplayDeviceNumber();

	for (int i = 0; i < calibableScreen; i++) {
		applyCorrection(i);
	}
	//	const DISPDEV_VECTOR& displayDevices = getDisplayDevices();
	//	for (DISPDEV_CITR dispDevIt = displayDevices.begin(); dispDevIt
	//			!= displayDevices.end(); ++dispDevIt) {
	//		unsigned int osScreenIndex = (*dispDevIt).getOSIndex();
	//		if (osScreenIndex < _deviceInfo->getXScreensNumber())
	//			applyCorrection(osScreenIndex);
	//
	//	}
}

void ColorKeeperModel::initCorrections() {
	_correctionLuts.clear();

	int calibableScreen = _deviceInfo->getCalibrableDisplayDeviceNumber();
	for (int i = 0; i < calibableScreen; i++) {
		const DisplayDevice &dev = _deviceInfo->getCalibrableDisplayDevice(i);
		unsigned int osScreenIndex = dev.getOSIndex();
		std::string path = getScreenProfilPath(osScreenIndex);
		LUT2DCorrection lut(path, 256);
		_correctionLuts.insert(std::make_pair(osScreenIndex, lut));
		////
		ostringstream os;
		os << dev.getFullName() << " osScreenIndex " << osScreenIndex
				<< " (xscreen nb = " << (int) _deviceInfo->getXScreensNumber()
				<< ")" << endl;
		os << "Screen path : " << getScreenProfilNamePath(osScreenIndex)
				<< endl;
		logMessage(os.str());
		if (osScreenIndex < _deviceInfo->getXScreensNumber()) {
			int count;
			unsigned short * originalRamp = _lutLoader.getGammaRamp(
					osScreenIndex, &count);
			LUT2DCorrection originalLut(originalRamp, count);
			//std::cout<<(*dispDevIt).getModelConstructorID() <<std::endl;
			//originalLut.dump16BitsLut();//
			delete originalRamp;
			_orignalLuts.insert(std::make_pair(osScreenIndex, originalLut));
		}
	}

}

void ColorKeeperModel::initCustomLuts() {
	QDir direc(QString(CUSTOM_LUTS_PATH.c_str()));
	QStringList filters(QString("*.gamma"));
	direc.setNameFilters(filters);
	_customLuts = direc.entryList();
	QStringList::const_iterator constIterator;
	for (constIterator = _customLuts.constBegin();
			constIterator != _customLuts.constEnd(); ++constIterator) {
		QString path = (*constIterator);
		path.remove(".gamma", Qt::CaseInsensitive);
		path.append(QString(".readme"));
		path.prepend(QString(CUSTOM_LUTS_PATH.c_str()));
		QFile file(path);
		QString line("");
		if (!file.open(QIODevice::ReadOnly | QIODevice::Text)) {
			_customLutsDescription.append(line);
			continue;
		}
		QTextStream in(&file);
		while (!in.atEnd()) {
			line.append(in.readLine());
			line.append("\n");
		}
		line.remove(line.size() - 1, 1);
		_customLutsDescription.append(line);
	}

}
void ColorKeeperModel::initCustomPatterns() {
	QDir direc(QString(CUSTOM_PATTERNS_PATH.c_str()));
	QStringList filters(QString("*.jpg"));
	direc.setNameFilters(filters);
	_customPatterns = direc.entryList();
	QStringList::const_iterator constIterator;
	for (constIterator = _customPatterns.constBegin();
			constIterator != _customPatterns.constEnd(); ++constIterator) {
		QString path = (*constIterator);
		path.prepend(CUSTOM_PATTERNS_PATH.c_str());
		QPixmap img(path);
		img = img.scaled(PATTERNICON_SIZE, PATTERNICON_SIZE);
		_patternsIcons.push_back(img);
	}

}

const LUT2DCorrection* ColorKeeperModel::getSelectedCustomLut(
		const unsigned int &screenIndex) {
	map<unsigned int, LUT2DCorrection>::iterator it = _selectedCustomLut.find(
			screenIndex);
	if (it != _selectedCustomLut.end())
		return &((*it).second);
	else
		return NULL;
}

void ColorKeeperModel::setSelectedCustomLut(const unsigned int &screenIndex,
		const QString &selectedLut) {
	if (_selectedCustomLut.find(screenIndex) != _selectedCustomLut.end())
		_selectedCustomLut.erase(screenIndex);
	if (selectedLut.size() > 0) {
		std::string path = CUSTOM_LUTS_PATH;
		path.append(selectedLut.toStdString());
		LUT2DCorrection tmpLut(path, 256);
		LUT2DCorrection * transformLut =
				getScreenProfil(screenIndex).getCombinedLUTCorrection(&tmpLut);
		_selectedCustomLut.insert(std::make_pair(screenIndex, *transformLut));
		delete transformLut;
	} //else no custom lut selected
	applyCorrection(screenIndex);
	emit correctionChanged(screenIndex);
}

void ColorKeeperModel::applyCorrection(
		const unsigned int &osScreenIndex) const {
	//	if (isScreenCorrected(osScreenIndex)) {
	try {

		map<unsigned int, HealerCalibModel>::const_iterator it = _calib.find(
				osScreenIndex);
		if ((it != _calib.end()) && ((*it).second.shouldDisplay())) {
			_lutLoader.setGammaRamp((int) osScreenIndex,
					(*it).second.getCorrection());
		} else if (shouldApplyCorrection(osScreenIndex)) {
			std::string path = getScreenProfilPath(osScreenIndex);
			if (_selectedCustomLut.find(osScreenIndex)
					!= _selectedCustomLut.end()) {
				_lutLoader.setGammaRamp((int) osScreenIndex,
						&((*_selectedCustomLut.find(osScreenIndex)).second));
			} else {
				_lutLoader.setGammaRamp((int) osScreenIndex,
						&(getScreenProfil(osScreenIndex)));
			}
		} else {
			_lutLoader.setGammaRamp((int) osScreenIndex, _default);
		}
		//			std::cout << "Screen " << osScreenIndex << " is corrected with "
		//					<< path << "." << std::endl;

	} catch (CannotFindLutException cfle) {
		ostringstream os;
		os << cfle.what() << endl;
		logDebug(os.str());
	} catch (CannotGetLutException cgle) {
		ostringstream os;
		os << cgle.what() << endl;
		logDebug(os.str());
	} catch (CannotSetLutException csle) {
		ostringstream os;
		os << csle.what() << endl;
		logDebug(os.str());

	} catch (BinarySearchCurveException bsce) {
		ostringstream os;
		os << bsce.what() << endl;
		logDebug(os.str());
	}
	//}
}

void ColorKeeperModel::setScreenProfilGamma(const unsigned int &screenIndex,
		float gamma) {
	//if (isScreenCorrected(screenIndex)) {
	((*_correctionLuts.find(screenIndex)).second).setGamma(gamma);
	if (_selectedCustomLut.find(screenIndex) != _selectedCustomLut.end())
		((*_selectedCustomLut.find(screenIndex)).second).setGamma(gamma);
	applyCorrection(screenIndex);
	emit correctionChanged(screenIndex);
	emit gammaChanged(screenIndex, gamma);
	//}

}

void ColorKeeperModel::setScreenProfilPL(const unsigned int &screenIndex,
		float plValue, PrinterLights whichPL) {
	switch (whichPL) {
	case RED:
		((*_correctionLuts.find(screenIndex)).second).setRedPL(plValue);
		if (_selectedCustomLut.find(screenIndex) != _selectedCustomLut.end())
			((*_selectedCustomLut.find(screenIndex)).second).setRedPL(plValue);
		break;
	case GREEN:
		((*_correctionLuts.find(screenIndex)).second).setGreenPL(plValue);
		if (_selectedCustomLut.find(screenIndex) != _selectedCustomLut.end())
			((*_selectedCustomLut.find(screenIndex)).second).setGreenPL(
					plValue);
		break;
	case BLUE:
		((*_correctionLuts.find(screenIndex)).second).setBluePL(plValue);
		if (_selectedCustomLut.find(screenIndex) != _selectedCustomLut.end())
			((*_selectedCustomLut.find(screenIndex)).second).setBluePL(plValue);
		break;
	}

	applyCorrection(screenIndex);
	emit correctionChanged(screenIndex);
	emit plChanged(screenIndex, plValue, whichPL);

}

void ColorKeeperModel::emitCorrectionChanged(unsigned int screenIndex) {
	emit correctionChanged(screenIndex);
}
void ColorKeeperModel::emitPopMessage(QString title, QString message,
		bool weAreInTrouble) {
	emit popMessage(title, message, weAreInTrouble);
}

void ColorKeeperModel::setScreenShouldApplyCorrection(
		const unsigned int &screenIndex, bool should) {

	_shouldApplyCorrection[screenIndex] = should;
	applyCorrection(screenIndex);
	emit correctionChanged(screenIndex);
}

bool ColorKeeperModel::shouldApplyCorrection(
		const unsigned int &screenIndex) const {

	return _shouldApplyCorrection[screenIndex];

}

const LUT2DCorrection& ColorKeeperModel::getScreenProfil(
		const unsigned int &osScreenIndex) const {
	return ((*_correctionLuts.find(osScreenIndex)).second);
}

bool ColorKeeperModel::isScreenCorrected(
		const unsigned int &osScreenIndex) const {
	std::string path = CORRECTION_PATH;
	path.append(getScreenProfilName(osScreenIndex));
	FILE *pf;
	pf = fopen(path.c_str(), "r");
	if (pf) {
		fclose(pf);
		return true;
	} else
		return false;
}
unsigned int ColorKeeperModel::isScreenCorrectionHasBeen(
		const unsigned int &osScreenIndex) const {
	std::string path = CORRECTION_PATH;
	path.append(getScreenProfilName(osScreenIndex));
	QFile file(path.c_str());
	if (file.exists()) {
		QFileInfo fileInfo(file);
		QDateTime creationTime = fileInfo.lastModified();
		QDateTime currentTime(QDateTime::currentDateTime());

		unsigned int day = creationTime.daysTo(currentTime);
		if (day > s_ProfilTimeLimit)
			return day - s_ProfilTimeLimit;
		else
			return 0;
	}
	return 0;

}

std::string ColorKeeperModel::getScreenProfilName(
		const unsigned int &osScreenIndex) const {
	//name is a combinaison of
	const DisplayDevice & currScreen = _deviceInfo->getCalibrableDisplayDevice(
			osScreenIndex);
	char osChar = '_';
#ifdef __WIN32__ 
	osChar = 'w';
#endif
#ifdef __linux__
	osChar = 'g';
#endif
#ifdef __APPLE__
	osChar = 'm';
#endif

	ostringstream os;
	os << osChar << getHostName() << "_" << osScreenIndex << "_"
			<< currScreen.getFullSerialNumber() << ".gamma";
	return os.str();
}

std::string ColorKeeperModel::getScreenProfilNamePath(
		const unsigned int &osScreenIndex) const {
	std::string path = CORRECTION_PATH;
	path.append(getScreenProfilName(osScreenIndex));
	return path;
}

std::string ColorKeeperModel::getScreenProfilPath(
		const unsigned int &osScreenIndex) const {
	std::string path = CORRECTION_PATH;
	if (isScreenCorrected(osScreenIndex))
		path.append(getScreenProfilName(osScreenIndex));
	else
		path.append("default.gamma");

	return path;
}

bool ColorKeeperModel::addCalibModelForScreen(const unsigned int &screenIndex) {
	if (_calib.find(screenIndex) != _calib.end()) {
		return true; // already exist
	}
	bool isNotCorrected = !isScreenCorrected(screenIndex);
	HealerCalibModel calib(screenIndex, isNotCorrected);
	_calib.insert(std::make_pair(screenIndex, calib));

	return false;
}

bool ColorKeeperModel::setCalibSizeForScreen(const unsigned int &screenIndex,
		const unsigned int & size) {
	map<unsigned int, HealerCalibModel>::iterator it = _calib.find(screenIndex);
	if (it != _calib.end()) {
		(*it).second.setSize(size);
		return true;
	} else
		return false;
}

bool ColorKeeperModel::addCalibValueForScreen(const unsigned int &screenIndex,
		const unsigned short &r, const unsigned short &g,
		const unsigned short &b) {
	map<unsigned int, HealerCalibModel>::iterator it = _calib.find(screenIndex);
	if (it != _calib.end()) {
		(*it).second.addValue(r, g, b);
		return true;
	} else
		return false;
}

bool ColorKeeperModel::setCalibDoneForScreen(const unsigned int &screenIndex) {
	shoudDisplayCalibForScreen(screenIndex, true);
	emit measuresDone();
	return true;
}

bool ColorKeeperModel::shoudDisplayCalibForScreen(
		const unsigned int &screenIndex, const bool &should) {
	map<unsigned int, HealerCalibModel>::iterator it = _calib.find(screenIndex);
	if (it != _calib.end()) {
		(*it).second.setShouldDisplay(should);
		ColorKeeperModel::Instance().applyCorrection(screenIndex);
		return true;
	} else
		return false;
}

bool ColorKeeperModel::updateCorrection(const unsigned int &screenIndex,
		const QString & infos) {
	map<unsigned int, HealerCalibModel>::iterator calibIt = _calib.find(
			screenIndex);
	if (calibIt != _calib.end()) {
		(*calibIt).second.updateCorrection(infos);
		std::string path = getScreenProfilPath(screenIndex);
		LUT2DCorrection lut(path, 256);
		map<unsigned int, LUT2DCorrection>::iterator lutIt =
				_correctionLuts.find(screenIndex);
		_correctionLuts.erase(lutIt);
		_correctionLuts.insert(std::make_pair(screenIndex, lut));
		(*calibIt).second.setShouldDisplay(false);
		ColorKeeperModel::Instance().applyCorrection(screenIndex);
		setScreenShouldApplyCorrection(screenIndex, true);
		emit correctionChanged(screenIndex);
		emit correctionApplied(screenIndex);
		const DisplayDevice & dispDev = _deviceInfo->getCalibrableDisplayDevice(
				screenIndex);
		deleteCalibRequestFile(dispDev.getFullName(false), screenIndex);
		return true;
	} else
		return false;
}

bool ColorKeeperModel::endCalib(const unsigned int &screenIndex) {
	setScreenShouldApplyCorrection(screenIndex, true);
	map<unsigned int, HealerCalibModel>::iterator calibIt = _calib.find(
			screenIndex);
	if (calibIt != _calib.end()) {
		_calib.erase(calibIt);
	}
	//emit correctionChanged(screenIndex);
	//ColorKeeperModel::Instance().getSocketServer().
	return true;
}

QString ColorKeeperModel::getIniFilePath() const {
	return _deviceInfo->getIniFilePath();
}
void ColorKeeperModel::logMessage(std::string message) {
	TRI_MSG_STR(message);
}
void ColorKeeperModel::logDebug(std::string message) {
	TRI_LOG_STR(message);
}

void ColorKeeperModel::setCalibWindowsZoom(float zoom) {
	_calibWindowsZoom = zoom;
	emit calibWindowZoomChanged();
}
void ColorKeeperModel::setCalibWindowsTranslate(int transX, int transY) {
	_calibWindowsTranslateX = transX;
	_calibWindowsTranslateY = transY;
	emit calibWindowTransChanged();
}

void ColorKeeperModel::deleteLockFile() {
	if (!shouldDelete)
		return;
	QFile lockFile(
			QString::fromStdString(ColorKeeperModel::getLockFilePath("lock")));
	if (lockFile.exists()) {
		if (lockFile.remove()) {
			shouldDelete = false;
		}

	}

}

std::string ColorKeeperModel::getLockFilePath(std::string end) {

	std::string hostName;
	std::string domainName;
	std::string userName;
	DeviceScout::getHostName(hostName, domainName);
	DeviceScout::getUserName(userName);

	std::string lockFilePath;
	lockFilePath.append(ColorKeeperModel::LOCK_PATH);
	lockFilePath.append("/");
	lockFilePath.append(hostName);
	lockFilePath.append(".");
	lockFilePath.append(userName);
	lockFilePath.append(".");
	lockFilePath.append(end);
	return lockFilePath;

}

std::string ColorKeeperModel::getCalibRequestFilePath(string screenFullName) {

	std::string hostName;
	std::string domainName;
	std::string userName;
	DeviceScout::getHostName(hostName, domainName);
	DeviceScout::getUserName(userName);

	std::string calibRequestFile;
	calibRequestFile.append(ColorKeeperModel::CALIB_REQUEST_PATH);
	calibRequestFile.append("/");
	calibRequestFile.append(hostName);
	calibRequestFile.append(".");
	calibRequestFile.append(screenFullName);
	calibRequestFile.append(".asked");
	return calibRequestFile;

}

void ColorKeeperModel::replaceSpaceCharacters(std::string &astring) {
	std::replace(astring.begin(), astring.end(), ' ', '_');
}

std::string ColorKeeperModel::getInvalidateFilePath(string manufacturerName,
		string modelName) {

	replaceSpaceCharacters(manufacturerName);
	replaceSpaceCharacters(modelName);

	std::string calibRequestFile;
	calibRequestFile.append(ColorKeeperModel::INVALID_SCREEN_PATH);
	calibRequestFile.append("/");
	calibRequestFile.append(manufacturerName);
	calibRequestFile.append("_");
	calibRequestFile.append(modelName);

	calibRequestFile.append(".invalid");

	return calibRequestFile;
}

std::string ColorKeeperModel::getInvalidateFilePath(string machine) {

	std::string hostName;
	std::string domainName;
	std::string userName;
	DeviceScout::getHostName(hostName, domainName);
	DeviceScout::getUserName(userName);

	std::string calibRequestFile;
	calibRequestFile.append(ColorKeeperModel::INVALID_SCREEN_PATH);
	calibRequestFile.append("/");
	calibRequestFile.append(machine);
	calibRequestFile.append(".invalid");
	return calibRequestFile;

}

bool ColorKeeperModel::isScreenModelInvalidate(string manufacturerName,
		string modelName) {

	QFile invalidFile(
			QString::fromStdString(
					ColorKeeperModel::getInvalidateFilePath(manufacturerName,
							modelName)));
	return invalidFile.exists();

}

bool ColorKeeperModel::isMachineInvalidate() {
	std::string hostName;
	std::string domainName;
	DeviceScout::getHostName(hostName, domainName);
	QFile invalidFile(
			QString::fromStdString(
					ColorKeeperModel::getInvalidateFilePath(hostName)));
	return invalidFile.exists();
}

void ColorKeeperModel::createCalibRequestAskedFile(string screenFullName,
		string message) {
	QFile calibRequestFile(
			QString::fromStdString(
					ColorKeeperModel::getCalibRequestFilePath(screenFullName)));
	if (calibRequestFile.open(QIODevice::WriteOnly)) {
		calibRequestFile.write(QString::fromStdString(message).toAscii(),
				QString::fromStdString(message).length());
		calibRequestFile.close();
	}

}

bool ColorKeeperModel::isCalibResquestAsked(string screenFullName) {
	QFile calibRequestFile(
			QString::fromStdString(
					ColorKeeperModel::getCalibRequestFilePath(screenFullName)));
	return calibRequestFile.exists();
}

/**
 * This function return NULL if no mail config is set
 */
MailSender* ColorKeeperModel::getMailer() {

	if (!_isMailRequestEnable)
		return NULL;
	if (_mailer == NULL)
		_mailer = new MailSender(s_smtpServer, s_mailFrom, s_mailTo);
	return _mailer;
}

void ColorKeeperModel::deleteCalibRequestFile(string screenFullName,
		const unsigned int &screenIndex) {
	QFile calibRequestFile(
			QString::fromStdString(
					ColorKeeperModel::getCalibRequestFilePath(screenFullName)));
	if (calibRequestFile.exists()) {
		calibRequestFile.remove();
		const ILocalHost &host = ColorKeeperModel::Instance().getDeviceInfo();

		QString osName;
#ifdef __WIN32__
		osName = "Windows";
#endif
#ifdef __linux__
		osName = "Linux";
#endif
#ifdef __APPLE__
		osName = "MacOSX";
#endif

		QString hostname = QString(host.getHostName().c_str());
		QString domain = QString(host.getDomainName().c_str());
		bool isHighPriority = false;

		map<unsigned int, HealerCalibModel>::iterator calibIt =
				ColorKeeperModel::Instance()._calib.find(screenIndex);
		if (calibIt != ColorKeeperModel::Instance()._calib.end()) {
			isHighPriority = (*calibIt).second.isHighPriority();
		}

		MailSender* mailer = ColorKeeperModel::Instance().getMailer();
		if (mailer == NULL)
			return;

		//CalibRequest
		QString title = "CalibRequest For " + hostname + " on " + osName;
		if (isHighPriority)
			title = "URGENT " + title;
		QString message =
				"[CLOSED]\nMessage send by ColorKeeper after a calibration with ColorHealer.";
		mailer->send(QString::fromLocal8Bit(title.toStdString().c_str()),
				QString::fromLocal8Bit(message.toStdString().c_str()));

	}
}

void ColorKeeperModel::sendAMailCalibrationRequest() {

	MailSender* mailer = ColorKeeperModel::Instance().getMailer();
	if (mailer == NULL) {
		ostringstream os;
		os << "\nCoudn't find mailer configuration.\n" << endl;
		ColorKeeperModel::logMessage(os.str());
		return;
	}
	const ILocalHost &host = getDeviceInfo();
	const vector<unsigned int> & screenOrder = getScreenResOrder();
	QStringList screensToCalibrate;
	QStringList screensToCalibrateFullNames;
	bool isHighPriority = false;
	for (unsigned int i = 0; i < screenOrder.size(); i++) {
		const ZooperDisplayDevice & dispDev = host.getCalibrableDisplayDevice(
				screenOrder[i]);
		string fullname = dispDev.getFullName(false);
		if (isCalibResquestAsked(fullname)
				|| isScreenModelInvalidate(dispDev.getManufacturerName(),
						dispDev.getModelName()) || isMachineInvalidate())
			continue;
		int isHasBeen = isScreenCorrectionHasBeen(dispDev.getOSIndex());
		bool isNotCorrected = !isScreenCorrected(dispDev.getOSIndex());

		if (isHasBeen > 0) {
			QString message = "";
			message += "Average Priority : "
					+ QString(dispDev.getFullName().c_str()) + " has a "
					+ QString::number(isHasBeen)
					+ " days obsolete profile and need to be re-calibrate.";
			//			QString message = "";
			//			message
			//					+= "" + QString(
			//							dispDev.getFullName().c_str())
			//							+ " has no calibration profile and need to be calibrate very quickly.";

			screensToCalibrate << message;
			screensToCalibrateFullNames << QString::fromStdString(fullname);
		} else if (isNotCorrected) {
			QString message = "";
			message +=
					"High Priority : " + QString(dispDev.getFullName().c_str())
							+ " has no calibration profile and need to be calibrate very quickly.";

			screensToCalibrate << message;
			screensToCalibrateFullNames << QString::fromStdString(fullname);
			isHighPriority = true;
		}

	}

	int nbScreensToCalibrate = screensToCalibrate.size();
	if (nbScreensToCalibrate > 0) {

		QString osName;
#ifdef __WIN32__
		osName = "Windows";
#endif
#ifdef __linux__
		osName = "Linux";
#endif
#ifdef __APPLE__
		osName = "MacOSX";
#endif

		QString hostname = QString(host.getHostName().c_str());
		QString domain = QString(host.getDomainName().c_str());
		QString message =
				"Dear calibrators,\n\nColorKeeper has detected a lack of calibration for machine "
						+ hostname + " (domain : " + domain + ", user : "
						+ QString(host.getUserName().c_str()) + " ) on "
						+ osName + " : \n";
		//CalibRequest
		QString title = "CalibRequest For " + hostname + " on " + osName;
		if (isHighPriority)
			title = "URGENT " + title;

		for (int i = 0; i < nbScreensToCalibrate; i++) {
			message += "- " + screensToCalibrate.at(i) + "\n";
			ColorKeeperModel::createCalibRequestAskedFile(
					screensToCalibrateFullNames.at(i).toStdString(),
					message.toStdString());
		}
		message += "\nThanks !\n\n";

#ifdef __linux__
		message += "Reminder : on Linux, only one screen can be calibrated, ColorKeeper choose the primary screen in Nvidia-settings.\nSo when you're calibrating a dual screen linux station, check first that the primary screen if the best of the two (if not change the primary screen setting in Nivdia-settings).\n\n";
#endif

		mailer->send(QString::fromLocal8Bit(title.toStdString().c_str()),
				QString::fromLocal8Bit(message.toStdString().c_str()));

		ColorKeeperModel::logMessage("\nA request calib mail was sent.\n");

	}

}

