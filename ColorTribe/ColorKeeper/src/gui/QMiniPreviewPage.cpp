/*
 * QMiniGammaPage.cpp
 *
 *  Created on: 20 juil. 2009
 *      Author: mfe
 */

#include "QMiniPreviewPage.h"

#include "../core/ColorKeeperModel.h"

#include "GuiSizeDef.h"
#include "QLutDisplay.h"

#include <QComboBox>
#include <QHBoxLayout>
#include <string>

#include <iostream>

QMiniPreviewPage::QMiniPreviewPage(const ZooperDisplayDevice & dispDev,
		QWidget *parent) :
	QWidget(parent) {
	_OSScreenIndex = dispDev.getOSIndex();
	QHBoxLayout *mainLayout = new QHBoxLayout;

	_customLuts = new QComboBox();
	_customLuts->setFixedSize(120, 25);
	_customLuts->addItem(0, QString(""));
	_customLuts->addItems(ColorKeeperModel::Instance().getCustomLuts());
	connect(_customLuts, SIGNAL(currentIndexChanged ( const QString & )), this, SLOT(customLutChanged(const QString & )));

//	_lutDisplay = new QLutDisplay(
//			&ColorKeeperModel::Instance().getScreenProfil(_OSScreenIndex),
//			_OSScreenIndex);
//	_lutDisplay->setFixedSize(MINI_LUTCANVAS_SIZE, MINI_LUTCANVAS_SIZE);

	QHBoxLayout *previewLayout = new QHBoxLayout;
	previewLayout->addWidget(_customLuts);
//	previewLayout->addWidget(_lutDisplay);

	mainLayout->addLayout(previewLayout);

	setLayout(mainLayout);

}

void QMiniPreviewPage::customLutChanged(const QString & name) {
	ColorKeeperModel::Instance().setSelectedCustomLut(_OSScreenIndex, name);
}

QMiniPreviewPage::~QMiniPreviewPage() {
//	if (_customLuts != NULL)
//		delete _customLuts;
//	if (_lutDisplay != NULL)
//		delete _lutDisplay;
}
