/*
 * QPatternsPushButton.cpp
 *
 *  Created on: 31 mai 2010
 *      Author: mfe
 */

#include "QPatternsWidgetItem.h"
#include "QCalibWindow.h"
#include "GuiSizeDef.h"
#include <QString>
#include <QIcon>

QPatternsWidgetItem::QPatternsWidgetItem(QCalibWindow * calibWindow,
		QIcon icon, QString & path,QListWidget *list) :
			QListWidgetItem(icon, "", list), _refCalibWindow(calibWindow), _path(path) {
	//	setIconSize(QSize(PATTERNICON_SIZE, PATTERNICON_SIZE));
	//	setFixedSize(PATTERNICON_SIZE + 15, PATTERNICON_SIZE + 15);
}

QPatternsWidgetItem::~QPatternsWidgetItem() {
}

void QPatternsWidgetItem::mousePressEvent(QMouseEvent *) {
	_refCalibWindow->showWin();
	_refCalibWindow->setImagePath(_path);
	_refCalibWindow->setMode(QDrawArea::IMAGE);
}
