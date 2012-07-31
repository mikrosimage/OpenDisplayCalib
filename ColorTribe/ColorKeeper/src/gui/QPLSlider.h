/*
 * QPLSlider.h
 *
 *  Created on: 23 mars 2009
 *      Author: mfe
 */

#ifndef QPLSLIDER_H_
#define QPLDSLIDER_H_

#include "../core/ColorKeeperModel.h"
#include <QSlider>
#include <QLabel>
#include <QDoubleSpinBox>
class QObject;
class QEvent;

class QPLSlider : public QSlider {
	Q_OBJECT
private:

	unsigned int _OSScreenIndex;
	QDoubleSpinBox * _slidLab;
	bool _shouldUpdateModel;
	ColorKeeperModel::PrinterLights _whichPL;
	QPLSlider(){}
public:
	QPLSlider(unsigned int OSScreenIndex, ColorKeeperModel::PrinterLights whichPL, QDoubleSpinBox *slidLab);
	virtual ~QPLSlider();
	bool eventFilter(QObject* o, QEvent* e);
	static float getPLValue(int value);
	static int getSliderValue(float value);
private slots:
void setValue(int value);
public slots:
void setPLValue(unsigned int osScreenID, float gamma, ColorKeeperModel::PrinterLights whichPL);
};

#endif /* QPLSLIDER_H_ */
