/*
 * QDoubleClickedSlider.cpp
 *
 *  Created on: 23 mars 2009
 *      Author: mfe
 */

#include "QGammaSlider.h"
#include "../core/ColorKeeperModel.h"
#include <QMouseEvent>
#include <QDoubleSpinBox>

QGammaSlider::QGammaSlider(unsigned int OSScreenIndex, QDoubleSpinBox *slidLab = NULL) :
	QSlider(Qt::Horizontal), _OSScreenIndex(OSScreenIndex), _shouldUpdateModel(true) {
	_slidLab = slidLab;
	setFixedSize(200, 20);
	setSliderPosition(getSliderValue(1.0));
	installEventFilter(this);
connect(this, SIGNAL(valueChanged(int)), this, SLOT(setValue(int)));
connect(&(ColorKeeperModel::Instance()), SIGNAL(gammaChanged(unsigned int, float)), this, SLOT( setGamma(unsigned int , float )));
}

QGammaSlider::~QGammaSlider() {
}

bool QGammaSlider::eventFilter(QObject*, QEvent* e) {
	if (e->type() == QEvent::MouseButtonDblClick) {
		setSliderPosition(getSliderValue(1.0));

		return true; // eat event
	}
	// standard event processing
	return false;
}

int QGammaSlider::getSliderValue(float gamma) {
	return (int) (99 / (ColorKeeperModel::s_gammaUpBound - ColorKeeperModel::s_gammaDownBound) * (gamma-ColorKeeperModel::s_gammaDownBound) +0.5f);
}

float QGammaSlider::getGammaValue(int value) {
	return (int) (((ColorKeeperModel::s_gammaUpBound - ColorKeeperModel::s_gammaDownBound) / 99 * value + ColorKeeperModel::s_gammaDownBound) * 100)
			/ 100.0;
}
void QGammaSlider::setValue(int value) {
	float gammaValue = getGammaValue(value);
	QString str;
	str.setNum(gammaValue);
	if (_slidLab != NULL)
		_slidLab->setValue(gammaValue);
	if(_shouldUpdateModel)
	ColorKeeperModel::Instance().setScreenProfilGamma(_OSScreenIndex,
			gammaValue);
	else _shouldUpdateModel = true;

}

void QGammaSlider::setGamma(unsigned int osScreenID, float gamma) {
	if (osScreenID == _OSScreenIndex) {
		int currentSliderPos = value();
		int newSliderPos = getSliderValue(gamma);
		if(currentSliderPos != newSliderPos) {
			_shouldUpdateModel = false;
			setSliderPosition(newSliderPos);
		}
	}
}
