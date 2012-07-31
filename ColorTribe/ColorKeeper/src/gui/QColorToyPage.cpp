/*
 * QColorToyPage.cpp
 *
 *  Created on: 20 mars 2009
 *      Author: mfe
 */

#include "QColorToyPage.h"

#include "../core/ColorKeeperModel.h"
#include "QLutDisplay.h"
#include "QGammaSlider.h"
#include "QPLSlider.h"
#include "GuiSizeDef.h"
#include <QLabel>
#include <QHBoxLayout>
#include <QGroupBox>
#include <string>
#include <QSlider>
#include <QPushButton>
#include <QComboBox>
#include <QDoubleSpinBox>
#include <iostream>
#include <sstream>

QColorToyPage::QColorToyPage(const ZooperDisplayDevice & dispDev,
		QWidget *parent) :
	QWidget(parent) {

	_OSScreenIndex = dispDev.getOSIndex();
	_desktopScreenIndex = dispDev.getDesktopIndex();
	QVBoxLayout *groupLayout = new QVBoxLayout;
	QVBoxLayout *lutLayout = new QVBoxLayout;
	QHBoxLayout *mainLayout = new QHBoxLayout;

	///////
	QGroupBox *profilGroup = new QGroupBox(tr("Profil"));
	groupLayout->addWidget(profilGroup);
	_toggleCorrection = new QPushButton("Disable correction");
	_toggleCorrection->setFixedSize(150, 25);
	connect(_toggleCorrection, SIGNAL(clicked()), this,
			SLOT(toggleCorrection()));

	QGroupBox *previewGroup = new QGroupBox(tr("Preview"));
	_customLuts = new QComboBox();
	_customLuts->setFixedSize(250, 25);
	_customLuts->addItem(0, QString(""));
	_customLuts->addItems(ColorKeeperModel::Instance().getCustomLuts());

	connect(_customLuts, SIGNAL(currentIndexChanged ( const QString & )), this, SLOT(customLutChanged(const QString & )));

	QDoubleSpinBox *plRLab = new QDoubleSpinBox();
	plRLab->setValue(0.0f);
	plRLab->setMaximum(ColorKeeperModel::s_PLUpBound);
	plRLab->setMinimum(ColorKeeperModel::s_PLDownBound);
	plRLab->setDecimals(0);
	plRLab->setSingleStep(1);
	_plRSlider = new QPLSlider(_OSScreenIndex, ColorKeeperModel::RED, plRLab);
	_plRSlider->setFixedSize(130, 20);
	QHBoxLayout *plRLayout = new QHBoxLayout;
	plRLayout->addWidget(plRLab);
	plRLayout->addWidget(_plRSlider);
	QDoubleSpinBox *plGLab = new QDoubleSpinBox();

	plGLab->setValue(0.0f);
	plGLab->setMaximum(ColorKeeperModel::s_PLUpBound);
	plGLab->setMinimum(ColorKeeperModel::s_PLDownBound);
	plGLab->setDecimals(0);
	plGLab->setSingleStep(1);
	_plGSlider = new QPLSlider(_OSScreenIndex, ColorKeeperModel::GREEN, plGLab);
	_plGSlider->setFixedSize(130, 20);
	QHBoxLayout *plGLayout = new QHBoxLayout;
	plGLayout->addWidget(plGLab);
	plGLayout->addWidget(_plGSlider);
	QDoubleSpinBox *plBLab = new QDoubleSpinBox();
	plBLab->setValue(0.0f);
	plBLab->setMaximum(ColorKeeperModel::s_PLUpBound);
	plBLab->setMinimum(ColorKeeperModel::s_PLDownBound);
	plBLab->setDecimals(0);
	plBLab->setSingleStep(1);
	_plBSlider = new QPLSlider(_OSScreenIndex, ColorKeeperModel::BLUE, plBLab);
	_plBSlider->setFixedSize(130, 20);
	QHBoxLayout *plBLayout = new QHBoxLayout;
	plBLayout->addWidget(plBLab);
	plBLayout->addWidget(_plBSlider);

	connect(plRLab, SIGNAL(valueChanged(double)), this, SLOT(redPLChanged(double)));
	connect(plGLab, SIGNAL(valueChanged(double)), this, SLOT(greenPLChanged(double)));
	connect(plBLab, SIGNAL(valueChanged(double)), this, SLOT(bluePLChanged(double)));

	QVBoxLayout *previewLayout = new QVBoxLayout;
	previewLayout->setAlignment(Qt::AlignTop);
	previewLayout->addWidget(_customLuts);
	previewLayout->setAlignment(_customLuts, Qt::AlignHCenter | Qt::AlignCenter);

	previewLayout->addLayout(plRLayout);
	previewLayout->setAlignment(plRLayout, Qt::AlignHCenter | Qt::AlignCenter);
	previewLayout->addLayout(plGLayout);
	previewLayout->setAlignment(plGLayout, Qt::AlignHCenter | Qt::AlignCenter);
	previewLayout->addLayout(plBLayout);
	previewLayout->setAlignment(plBLayout, Qt::AlignHCenter | Qt::AlignCenter);
	previewGroup->setLayout(previewLayout);

	groupLayout->addWidget(previewGroup);

	QGroupBox *gammaGroup = new QGroupBox(tr("Gamma"));
	//_slidLab = new QLabel("1.0");
	_slidLab = new QDoubleSpinBox();
	_slidLab->setValue(1.0f);
	_slidLab->setMaximum(ColorKeeperModel::s_gammaUpBound);
	_slidLab->setMinimum(ColorKeeperModel::s_gammaDownBound);
	_slidLab->setDecimals(1);
	_slidLab->setSingleStep(0.1);
	connect(_slidLab, SIGNAL(valueChanged(double)), this, SLOT(spinChanged(double)));
	_slider = new QGammaSlider(_OSScreenIndex, _slidLab);
	_slider->setFixedSize(130, 20);
	QHBoxLayout *profilLayout = new QHBoxLayout;
	profilLayout->addWidget(_toggleCorrection);
	profilGroup->setLayout(profilLayout);

	QHBoxLayout *gammaLayout = new QHBoxLayout;
	gammaLayout->addWidget(_slidLab);
	gammaLayout->addWidget(_slider);
	gammaGroup->setLayout(gammaLayout);
	groupLayout->addWidget(gammaGroup);

	////

	_lutDisplay = new QLutDisplay(
			&ColorKeeperModel::Instance().getScreenProfil(_OSScreenIndex),
			_OSScreenIndex);
	_lutDisplay->setFixedSize(LUTCANVAS_SIZE, LUTCANVAS_SIZE);
	lutLayout->addWidget(_lutDisplay);
	lutLayout->setAlignment(_lutDisplay, Qt::AlignHCenter | Qt::AlignCenter);

	mainLayout->addLayout(groupLayout);
	mainLayout->addLayout(lutLayout);
	setLayout(mainLayout);
}

void QColorToyPage::sliderRelease() {
	ColorKeeperModel::Instance().emitCorrectionChanged(_OSScreenIndex);
}

void QColorToyPage::customLutChanged(const QString & name) {

	ColorKeeperModel::Instance().setSelectedCustomLut(_OSScreenIndex, name);
	QStringList descriptions =
			ColorKeeperModel::Instance().getCustomLutsDescription();
	int index = _customLuts->currentIndex() - 1;
	if (index >= 0) {
		QString des = descriptions.at(index);
		_customLuts->setToolTip(des);
	}
}
void QColorToyPage::toggleCorrection() {
	if (ColorKeeperModel::Instance().shouldApplyCorrection(_OSScreenIndex)) {
		_toggleCorrection->setText("Enable correction");
		ColorKeeperModel::Instance().setScreenShouldApplyCorrection(
				_OSScreenIndex, false);
	} else {
		_toggleCorrection->setText("Disable correction");
		ColorKeeperModel::Instance().setScreenShouldApplyCorrection(
				_OSScreenIndex, true);
	}
}

void QColorToyPage::spinChanged(double d) {
	_slider->setGamma(_OSScreenIndex, d);
	ColorKeeperModel::Instance().setScreenProfilGamma(_OSScreenIndex, d);
}

void QColorToyPage::redPLChanged(double d) {
	_plRSlider->setPLValue(_OSScreenIndex, d, ColorKeeperModel::RED);
	ColorKeeperModel::Instance().setScreenProfilPL(_OSScreenIndex,
					d, ColorKeeperModel::RED);
}
void QColorToyPage::greenPLChanged(double d) {
	_plGSlider->setPLValue(_OSScreenIndex, d,ColorKeeperModel::GREEN);
	ColorKeeperModel::Instance().setScreenProfilPL(_OSScreenIndex,
						d, ColorKeeperModel::GREEN);
}
void QColorToyPage::bluePLChanged(double d) {
	_plBSlider->setPLValue(_OSScreenIndex, d,  ColorKeeperModel::BLUE);
	ColorKeeperModel::Instance().setScreenProfilPL(_OSScreenIndex,
						d, ColorKeeperModel::BLUE);
}

QColorToyPage::~QColorToyPage() {
	//	delete _lutDisplay;
	//	delete _slidLab;
	//	delete _slider;
	//	delete _toggleCorrection;
	//	delete _customLuts;

}
