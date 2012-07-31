/*
 * QHeadBand.cpp
 *
 *  Created on: 23 mars 2009
 *      Author: mfe
 */

#include "QHeadedMainWidget.h"
#include "GuiSizeDef.h"
#include "QColorCronTab.h"
#include "QColorCalibTab.h"
#include "QColorToyTab.h"
#include "GuiSizeDef.h"
#include "QDisplayDevicesPanel.h"

#include <QSize>
#include <QPixmap>
#include <QLabel>
#include <QVBoxLayout>
#include <QTabWidget>

QHeadedMainWidget::QHeadedMainWidget(QWidget *parent) :
	QWidget(parent) {
	QLabel * headCont = new QLabel("pouet");
	QPixmap headerPix("./img/bandeau.png");
	headCont->setPixmap(headerPix);

	QVBoxLayout *mainLayout = new QVBoxLayout;
	mainLayout->addWidget(headCont);
	mainLayout->setAlignment(headCont, Qt::AlignHCenter | Qt::AlignTop);
//
	_dispDevPan = new QDisplayDevicePanel(this, DISPDEVPAN_WIDTH,
			DISPDEVPAN_HEIGHT, DISPDEVICON_SIZE, 10);
	mainLayout->addWidget(_dispDevPan);
	mainLayout->setAlignment(_dispDevPan, Qt::AlignTop | Qt::AlignHCenter);
	_dispDevPan->setFixedSize(DISPDEVPAN_WIDTH, DISPDEVPAN_HEIGHT);
//
	_tabWidget = new QTabWidget();
	_tabWidget->resize(QSize(COLORTAB_WIDTH, COLORTAB_HEIGHT));
	_tabWidget->setFixedSize(COLORTAB_WIDTH, COLORTAB_HEIGHT);
	_tabWidget->addTab(new QColorCronTab(_dispDevPan, _tabWidget), tr(
			"ColorCron"));
	_tabWidget->addTab(new QColorToyTab(_dispDevPan, _tabWidget),
			tr("ColorToy"));
	_tabWidget->addTab(new QColorCalibTab(_dispDevPan, _tabWidget), tr(
			"ColorCalib"));
	mainLayout->addWidget(_tabWidget);
	mainLayout->setAlignment(_tabWidget, Qt::AlignHCenter | Qt::AlignTop);
	setLayout(mainLayout);
}

QHeadedMainWidget::~QHeadedMainWidget() {
//	if (_tabWidget != NULL)
//		//delete _tabWidget;
//	if (_dispDevPan != NULL)
//		//delete _dispDevPan;
}
