/*
 * QPLSlider.cpp
 *
 *  Created on: 23 mars 2009
 *      Author: mfe
 */

#include "QPLSlider.h"
#include <QDoubleSpinBox>
#include "../core/ColorKeeperModel.h"
#include <QMouseEvent>

QPLSlider::QPLSlider(unsigned int OSScreenIndex,
		ColorKeeperModel::PrinterLights whichPL, QDoubleSpinBox *slidLab = NULL) :
	QSlider(Qt::Horizontal), _OSScreenIndex(OSScreenIndex), _shouldUpdateModel(
			true), _whichPL(whichPL) {
	_slidLab = slidLab;
	//_slidLab->setFixedSize(50, 50);
 	//setFixedSize(200, 20);
	setSliderPosition(getSliderValue(0.0));
	setPageStep(1);


	if (_slidLab != NULL)
		_slidLab->setValue(0);
	installEventFilter(this);
connect(this, SIGNAL(valueChanged(int)), this, SLOT(setValue(int)));
connect(&(ColorKeeperModel::Instance()), SIGNAL(plChanged(unsigned int, float, ColorKeeperModel::PrinterLights)), this, SLOT( setPLValue(unsigned int , float, ColorKeeperModel::PrinterLights )));
}

QPLSlider::~QPLSlider() {
}

bool QPLSlider::eventFilter(QObject*, QEvent* e) {
	if (e->type() == QEvent::MouseButtonDblClick) {
		setSliderPosition(getSliderValue(0.0));

		return true; // eat event
	}
	// standard event processing
	return false;
}

int QPLSlider::getSliderValue(float gamma) {
	return (int) (100 / (ColorKeeperModel::s_PLUpBound
			- ColorKeeperModel::s_PLDownBound) * (gamma
			- ColorKeeperModel::s_PLDownBound) + 0.5f);
}

float QPLSlider::getPLValue(int value) {
	return (int) (((ColorKeeperModel::s_PLUpBound
			- ColorKeeperModel::s_PLDownBound) / 100 * value
			+ ColorKeeperModel::s_PLDownBound) * 100) / 100.0;
}
void QPLSlider::setValue(int value) {
	float gammaValue = getPLValue(value);
	QString str;
	str.setNum((int) gammaValue);
	if (_slidLab != NULL)
		_slidLab->setValue((int) gammaValue);
	if (_shouldUpdateModel)
		ColorKeeperModel::Instance().setScreenProfilPL(_OSScreenIndex,
				gammaValue, _whichPL);
	else
		_shouldUpdateModel = true;

}

void QPLSlider::setPLValue(unsigned int osScreenID, float plValue,
		ColorKeeperModel::PrinterLights whichPL) {
	if (osScreenID == _OSScreenIndex && whichPL == _whichPL) {
		int currentSliderPos = value();
		int newSliderPos = getSliderValue(plValue);
		if (currentSliderPos != newSliderPos) {
			_shouldUpdateModel = false;
			setSliderPosition(newSliderPos);
		}
	}
}
