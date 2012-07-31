/*
 * QColorToyPage.h
 *
 *  Created on: 20 mars 2009
 *      Author: mfe
 */

#ifndef QCOLORTOYPAGE_H_
#define QCOLORTOYPAGE_H_

#include <QWidget>

class ZooperDisplayDevice;
class QLutDisplay;
class QLabel;
class QGammaSlider;
class QPushButton;
class QComboBox;
class QDoubleSpinBox;
class QPLSlider;

class QColorToyPage: public QWidget {
	Q_OBJECT
public:
	QColorToyPage(const ZooperDisplayDevice & dispDev, QWidget *parent = 0);
	virtual ~QColorToyPage();
private:
	QLutDisplay * _lutDisplay;
//	QLabel *_slidLab;
	QDoubleSpinBox  *_slidLab;
	QGammaSlider *_slider;
	QPushButton * _toggleCorrection;
	QComboBox * _customLuts;
	QPLSlider*_plRSlider;
	QPLSlider*_plGSlider;
	QPLSlider*_plBSlider;

	unsigned int _OSScreenIndex;
	unsigned int _desktopScreenIndex;

private slots:
	void sliderRelease();
	void toggleCorrection();
	void customLutChanged(const QString & name);
	void spinChanged(double d);
	void redPLChanged(double d);
	void greenPLChanged(double d);
	void bluePLChanged(double d);

};

#endif /* QCOLORTOYPAGE_H_ */
