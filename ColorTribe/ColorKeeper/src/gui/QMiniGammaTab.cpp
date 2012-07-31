/*
 * QMiniGammaTab.cpp
 *
 *  Created on: 20 juil. 2009
 *      Author: mfe
 */

#include "QMiniGammaTab.h"

#include "QDisplayDevicesPanel.h"
#include "QMiniGammaPage.h"
#include "../core/ColorKeeperModel.h"

#include <QHBoxLayout>
#include <QStackedWidget>
#include <QListWidgetItem>

using namespace std;

QMiniGammaTab::QMiniGammaTab(QDisplayDevicePanel * dispDevPan, QWidget *parent) :
	QWidget(parent), _dispDevPan(dispDevPan) {
	setWindowTitle(tr("MiniGamma"));
	_pagesWidget = new QStackedWidget;
	createPages();

	QVBoxLayout *verticalLayout = new QVBoxLayout;
	verticalLayout->addWidget(_pagesWidget, 1);
	setLayout(verticalLayout);
}

void QMiniGammaTab::createPages() {
	ColorKeeperModel & model = ColorKeeperModel::Instance();
	const vector<unsigned int> & screenOrder = model.getScreenResOrder();
	for (unsigned int i = 0; i < screenOrder.size(); i++) {
		const ZooperDisplayDevice & dispDev = model.getDeviceInfo().getCalibrableDisplayDevice(
				screenOrder[i]);
		_pagesWidget->addWidget(new QMiniGammaPage(dispDev));
	}
connect(_dispDevPan->_contentsWidget, SIGNAL(currentItemChanged(QListWidgetItem *, QListWidgetItem *)), this, SLOT(changePage(QListWidgetItem *, QListWidgetItem*)));
}

void QMiniGammaTab::changePage(QListWidgetItem *current,
		QListWidgetItem *previous) {
	if (!current)
		current = previous;

	_pagesWidget->setCurrentIndex(_dispDevPan->_contentsWidget->row(current));
}

QMiniGammaTab::~QMiniGammaTab() {
//	if (_pagesWidget != NULL)
//		delete _pagesWidget;
}
