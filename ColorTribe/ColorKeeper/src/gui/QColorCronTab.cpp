/*
 * QColorCronTab.cpp
 *
 *  Created on: 9 mars 2009
 *      Author: mfe
 */

#include "QColorCronTab.h"
#include "QDisplayDevicesPanel.h"
#include "../core/ColorKeeperModel.h"
#include "QColorCronPage.h"

#include <QHBoxLayout>
#include <QStackedWidget>
#include <QListWidgetItem>

using namespace std;

QColorCronTab::QColorCronTab(QDisplayDevicePanel * dispDevPan, QWidget *parent) :
		QWidget(parent), _dispDevPan(dispDevPan) {
	setWindowTitle(tr("ColorCron"));

	_pagesWidget = new QStackedWidget;
	createPages();

	QVBoxLayout *verticalLayout = new QVBoxLayout;
//	verticalLayout->addWidget(_dispDevPan);
//	verticalLayout->setAlignment(_dispDevPan, Qt::AlignHCenter);
	verticalLayout->addWidget(_pagesWidget, 1);
	setLayout(verticalLayout);

}

void QColorCronTab::createPages() {
	ColorKeeperModel & model = ColorKeeperModel::Instance();
	const vector<unsigned int> & screenOrder = model.getScreenResOrder();
	for (unsigned int i = 0; i < screenOrder.size(); i++) {
		const ZooperDisplayDevice & dispDev = model.getDeviceInfo().getCalibrableDisplayDevice(
				screenOrder[i]);

		_pagesWidget->addWidget(new QColorCronPage(dispDev));
	}
connect(_dispDevPan->_contentsWidget, SIGNAL(currentItemChanged(QListWidgetItem *, QListWidgetItem *)), this, SLOT(changePage(QListWidgetItem *, QListWidgetItem*)));
}

void QColorCronTab::changePage(QListWidgetItem *current,
		QListWidgetItem *previous) {
	if (!current)
		current = previous;

	_pagesWidget->setCurrentIndex(_dispDevPan->_contentsWidget->row(current));
}

QColorCronTab::~QColorCronTab() {
	//delete _pagesWidget;
}
