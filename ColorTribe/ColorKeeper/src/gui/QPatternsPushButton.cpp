/*
 * QPatternsPushButton.cpp
 *
 *  Created on: 31 mai 2010
 *      Author: mfe
 */

#include "QPatternsPushButton.h"
#include "QCalibWindow.h"
#include "GuiSizeDef.h"
#include <QString>
#include <QIcon>

QPatternsPushButton::QPatternsPushButton(QCalibWindow * calibWindow,
		QIcon icon, QString path) :
	QPushButton(icon, "", NULL), _refCalibWindow(calibWindow), _path(path) {
	setIconSize(QSize(PATTERNICON_SIZE, PATTERNICON_SIZE));
	setFixedSize(PATTERNICON_SIZE + 15, PATTERNICON_SIZE + 15);
	setSizePolicy(QSizePolicy::Minimum, QSizePolicy::Minimum);
	setToolTip(path);
}

QPatternsPushButton::~QPatternsPushButton() {
}

void QPatternsPushButton::mousePressEvent(QMouseEvent *) {
	_refCalibWindow->showWin();
	_refCalibWindow->setImagePath(_path);
	_refCalibWindow->setMode(QDrawArea::IMAGE);
}
