/*
 * QColorCalibPage.h
 *
 *  Created on: 11 mars 2009
 *      Author: mfe
 */

#ifndef QCOLORCALIBPAGE_H_
#define QCOLORCALIBPAGE_H_

#include <QWidget>

class ZooperDisplayDevice;
class QCalibWindow;
class QLineEdit;
class QComboBox;

class QColorCalibPage: public QWidget {
Q_OBJECT
public:
	QColorCalibPage(const ZooperDisplayDevice & dispDev, QWidget *parent = 0);
	virtual ~QColorCalibPage();

	QCalibWindow * _calibWindow;
	const ZooperDisplayDevice & _dispDev;
	QLineEdit * _ipTxtField;

private slots:
	void toggleCalibWindow();
	void showGrayRamp();
	void showMireOfTheDeath();
	void conditionnalShowMireOfTheDeath();
	void showFliteMire();
	void showMarcie();
	void sendBroadCastCalibRequest();
	void showLumContPattern(float, unsigned int, bool showMosaic);
	void showPatch(unsigned int isIndex, float r, float g, float b, bool halo);
	void showFullScreenRec(unsigned int isIndex, float r, float g, float b);

};

#endif /* QCOLORCALIBPAGE_H_ */
