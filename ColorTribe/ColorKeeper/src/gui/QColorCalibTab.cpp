/*
 * QColorCalibTab.cpp
 *
 *  Created on: 11 mars 2009
 *      Author: mfe
 */

#include "QColorCalibTab.h"
#include "QDisplayDevicesPanel.h"
#include "../core/ColorKeeperModel.h"
#include "QColorCalibPage.h"

#include <QHBoxLayout>
#include <QStackedWidget>
#include <QListWidgetItem>

using namespace std;

QColorCalibTab::QColorCalibTab(QDisplayDevicePanel * dispDevPan, QWidget *parent) : QWidget(parent), _dispDevPan(dispDevPan){
	setWindowTitle(tr("ColorCalib"));
	//_dispDevPan = new QDisplayDevicePanel();
	_pagesWidget = new QStackedWidget;
	createPages();

	QVBoxLayout *verticalLayout = new QVBoxLayout;
//	verticalLayout->addWidget(_dispDevPan);
//	verticalLayout->setAlignment(_dispDevPan,  Qt::AlignTop|Qt::AlignHCenter);
	verticalLayout->addWidget(_pagesWidget, 1);
	setLayout(verticalLayout);
}

void QColorCalibTab::createPages() {
	ColorKeeperModel & model = ColorKeeperModel::Instance();
	const vector<unsigned int> & screenOrder = model.getScreenResOrder();
		for(unsigned int i = 0 ; i < screenOrder.size() ; i++){
			const ZooperDisplayDevice & dispDev = model.getDeviceInfo().getCalibrableDisplayDevice(screenOrder[i]);

		_pagesWidget->addWidget(new QColorCalibPage(dispDev));
	}
	connect(_dispDevPan->_contentsWidget, SIGNAL(currentItemChanged(QListWidgetItem *, QListWidgetItem *)), this, SLOT(changePage(QListWidgetItem *, QListWidgetItem*)));
}

void QColorCalibTab::changePage(QListWidgetItem *current,
		QListWidgetItem *previous) {
	if (!current)
		current = previous;

	_pagesWidget->setCurrentIndex(_dispDevPan->_contentsWidget->row(current));
}

QColorCalibTab::~QColorCalibTab() {
	//delete _pagesWidget;
}
