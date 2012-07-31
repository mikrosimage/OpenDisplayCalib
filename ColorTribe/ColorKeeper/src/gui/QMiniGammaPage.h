/*
 * QMiniGammaPage.h
 *
 *  Created on: 20 juil. 2009
 *      Author: mfe
 */

#ifndef QMINIGAMMAPAGE_H_
#define QMINIGAMMAPAGE_H_

#include <QWidget>

class ZooperDisplayDevice;

class QDoubleSpinBox;
class QGammaSlider;

class QMiniGammaPage: public QWidget {
Q_OBJECT
public:
	QMiniGammaPage(const ZooperDisplayDevice & dispDev, QWidget *parent = 0);
	virtual ~QMiniGammaPage();
private:
	QDoubleSpinBox *_slidLab;
	QGammaSlider *_slider;
	unsigned int _OSScreenIndex;

private slots:
	void sliderRelease();
	void spinChanged(double d);
};

#endif /* QMINIGAMMAPAGE_H_ */
