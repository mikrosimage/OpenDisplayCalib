/*
 * QColorToyTab.cpp
 *
 *  Created on: 20 mars 2009
 *      Author: mfe
 */

#include "QColorToyTab.h"

#include "QDisplayDevicesPanel.h"
#include "../core/ColorKeeperModel.h"
#include "QColorToyPage.h"

#include <QHBoxLayout>
#include <QStackedWidget>
#include <QListWidgetItem>

using namespace std;

QColorToyTab::QColorToyTab(QDisplayDevicePanel * dispDevPan, QWidget *parent) :
	QWidget(parent), _dispDevPan(dispDevPan) {
	setWindowTitle(tr("ColorToy"));
	_pagesWidget = new QStackedWidget;
	createPages();


	QVBoxLayout *verticalLayout = new QVBoxLayout;
	//	verticalLayout->addWidget(_dispDevPan);
	//	verticalLayout->setAlignment(_dispDevPan, Qt::AlignHCenter);
	verticalLayout->addWidget(_pagesWidget);
	setLayout(verticalLayout);
}

void QColorToyTab::createPages() {
	ColorKeeperModel & model = ColorKeeperModel::Instance();
	const vector<unsigned int> & screenOrder = model.getScreenResOrder();
	for (unsigned int i = 0; i < screenOrder.size(); i++) {
		const ZooperDisplayDevice & dispDev = model.getDeviceInfo().getCalibrableDisplayDevice(
				screenOrder[i]);
		_pagesWidget->addWidget(new QColorToyPage(dispDev));
	}
connect(_dispDevPan->_contentsWidget, SIGNAL(currentItemChanged(QListWidgetItem *, QListWidgetItem *)), this, SLOT(changePage(QListWidgetItem *, QListWidgetItem*)));
}

void QColorToyTab::changePage(QListWidgetItem *current,
		QListWidgetItem *previous) {
	if (!current)
		current = previous;

	_pagesWidget->setCurrentIndex(_dispDevPan->_contentsWidget->row(current));
}

QColorToyTab::~QColorToyTab() {
	//delete _pagesWidget;
}
