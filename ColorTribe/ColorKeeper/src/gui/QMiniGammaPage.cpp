/*
 * QMiniGammaPage.cpp
 *
 *  Created on: 20 juil. 2009
 *      Author: mfe
 */

#include "QMiniGammaPage.h"

#include "../core/ColorKeeperModel.h"

#include "QGammaSlider.h"
#include "GuiSizeDef.h"
#include <QLabel>
#include <QHBoxLayout>
#include <string>
#include <QSlider>
#include <QDoubleSpinBox>
#include <iostream>

QMiniGammaPage::QMiniGammaPage(const ZooperDisplayDevice & dispDev, QWidget *parent) :
	QWidget(parent) {
	_OSScreenIndex = dispDev.getOSIndex();
	QHBoxLayout *mainLayout = new QHBoxLayout;

	_slidLab = new QDoubleSpinBox ();
	_slidLab->setValue(1.0f);
	_slidLab->setMaximum (ColorKeeperModel::s_gammaUpBound);
	_slidLab->setMinimum(ColorKeeperModel::s_gammaDownBound);
	_slidLab->setDecimals(1);
	_slidLab->setSingleStep(0.1);
	_slider = new QGammaSlider(_OSScreenIndex, _slidLab);
	_slider->setFixedSize(110, 20);
	connect(_slidLab, SIGNAL(valueChanged(double)), this, SLOT(spinChanged(double)));
	QHBoxLayout *gammaLayout = new QHBoxLayout;
	gammaLayout->addWidget(_slidLab);
	gammaLayout->addWidget(_slider);

	mainLayout->addLayout(gammaLayout);

	setLayout(mainLayout);

}

void QMiniGammaPage::sliderRelease() {
	ColorKeeperModel::Instance().emitCorrectionChanged(_OSScreenIndex);
}

void QMiniGammaPage::spinChanged(double d){
	_slider->setGamma(_OSScreenIndex,d);
	ColorKeeperModel::Instance().setScreenProfilGamma(_OSScreenIndex,
				d);
}

QMiniGammaPage::~QMiniGammaPage() {
	if (_slidLab != NULL)
		delete _slidLab;
	if (_slider != NULL)
		delete _slider;

}
