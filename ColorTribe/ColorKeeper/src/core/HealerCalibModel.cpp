/*
 * HealerCalibModel.cpp
 *
 *  Created on: 12 juin 2009
 *      Author: mfe
 */

#include "HealerCalibModel.h"
#include "ColorKeeperModel.h"

#include <QString>
#include <QFile>
#include <QDir>
#include <QIODevice>
#include <QByteArray>
#include <QFileInfo>
#include <QDateTime>
#include <iostream>
using namespace std;

HealerCalibModel::HealerCalibModel(const unsigned int &screenIndex, const bool &isHighPriority) :
	_screenIndex(screenIndex), _lutSize(0), _isSizeSet(false), _shouldDisplay(
			false), _isHighPriority(isHighPriority), _corr(NULL) {

}

HealerCalibModel::~HealerCalibModel() {
	if (_corr)
		delete _corr;
}

HealerCalibModel::HealerCalibModel(const HealerCalibModel & calib) :
	_screenIndex(calib._screenIndex), _lutSize(calib._lutSize), _isSizeSet(
			calib._isSizeSet), _shouldDisplay(calib._shouldDisplay),  _isHighPriority(calib._isHighPriority), _values(
			calib._values) {
	if (calib._corr != NULL) {
		_corr = new LUT2DCorrection(*calib._corr);
	} else
		_corr = NULL;
}

HealerCalibModel& HealerCalibModel::operator=(const HealerCalibModel & calib) {
	_screenIndex = calib._screenIndex;
	_lutSize = calib._lutSize;
	_isSizeSet = calib._isSizeSet;
	_shouldDisplay = calib._shouldDisplay;
	_values = calib._values;
	if (calib._corr != NULL) {
		_corr = new LUT2DCorrection(*calib._corr);
	} else
		_corr = NULL;

	return *this;
}

const LUT2DCorrection* HealerCalibModel::getCorrection() const {
	return _corr;
}

void HealerCalibModel::addValue(const unsigned short& r,
		const unsigned short& g, const unsigned short& b) {
	if ((_isSizeSet) && (_values.size() < _lutSize * 3)) {

		_values.push_back(r);
		_values.push_back(g);
		_values.push_back(b);

		if (_values.size() == _lutSize * 3) { // end of LUT
			if (_corr != NULL)
				delete _corr;
			_corr = new LUT2DCorrection(_values, _lutSize);
		}
	}

}

void HealerCalibModel::updateCorrection(const QString & infos) {

	//TODO rajouter des exceptions
	string path = ColorKeeperModel::Instance().getScreenProfilNamePath(
			_screenIndex);
	bool saveBak = true;
	char *cpath = new char[path.size() + 1];


	strcpy(cpath, path.c_str());

	ostringstream os;
	os << "cpath written : " << cpath << std::endl;
	os << "cpath written2 : "
			<< ColorKeeperModel::Instance().getScreenProfilNamePath(
					_screenIndex).c_str() << std::endl;
	ColorKeeperModel::logMessage(os.str());
	if (path.size() > 0 && _corr != NULL && _values.size() == _lutSize * 3) {
		QFile file(cpath);
		if (file.exists()) {
			if (saveBak) {
				QString fileOutName(cpath);
				int indexExt = fileOutName.lastIndexOf(QString("gamma"));
				if (indexExt > -1)
					fileOutName.remove(indexExt, 5);
				QString dirPath(fileOutName);
				indexExt = dirPath.lastIndexOf(QString("/"));
				dirPath.remove(indexExt + 1, dirPath.size() - indexExt);

				QString
						deviceName(
								ColorKeeperModel::Instance().getDeviceInfo().getCalibrableDisplayDevice(
										_screenIndex).getFullSerialNumber().c_str());
				deviceName.append('/');
				fileOutName.insert(indexExt + 1, deviceName);
				QDir dir(dirPath);
				dir.mkdir(deviceName);
				QFileInfo fileInfo(file);
				QDateTime date = fileInfo.lastModified();
				fileOutName.append(date.toString(Qt::ISODate));
				fileOutName.replace(QChar(':'), QChar('-'));
				QFile::copy(file.fileName(), fileOutName);
			}
		}
		if (file.open(QIODevice::WriteOnly)) {
			ColorKeeperModel::logMessage("open ");
			unsigned short* lut = _corr->get16BitsLUT();
			for (unsigned int i = 0; i < _corr->getRampSize() * 3; i += 3) {
				QString line;
				line.sprintf("%d\t%d\t%d\t%d\n", i / 3, lut[i], lut[i + 1],
						lut[i + 2]);
				file.write(line.toAscii(), line.length());
			}
			QString modInfos = infos;
			modInfos.prepend(QString("\n"));
			file.write(modInfos.toAscii(), modInfos.length());
			file.close();
			delete[] lut;
		}

	}
}
