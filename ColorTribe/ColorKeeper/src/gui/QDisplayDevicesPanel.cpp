/*
 * QDispplayDevicePanel.cpp
 *
 *  Created on: 9 mars 2009
 *      Author: mfe
 */

#include "QDisplayDevicesPanel.h"
#include "../core/ColorKeeperModel.h"
#include "QColorCronPage.h"
#include "GuiSizeDef.h"

#include <QGroupBox>
#include <QHBoxLayout>
#include <QPushButton>
#include <QString>
#include <string>
#include <vector>
#include <QListWidgetItem>
#include <QSize>

#include "GuiSizeDef.h"

using namespace std;

QDisplayDevicePanel::QDisplayDevicePanel(QWidget *parent, size_t maxWidth,
		size_t maxHeight, size_t iconSize, size_t spacing) :
	QWidget(parent), _isMini(false) {

	_contentsWidget = new QListWidget;
	_contentsWidget->setViewMode(QListView::IconMode);
	_contentsWidget->setIconSize(QSize(iconSize, iconSize));
	if (iconSize == MINI_DISPDEVICON_SIZE)
		_isMini = true;
	_contentsWidget->setMovement(QListView::Static);
	_contentsWidget->setMaximumWidth(maxWidth);
	_contentsWidget->setMaximumHeight(maxHeight);
	_contentsWidget->setSpacing(spacing);
	_contentsWidget->setUniformItemSizes(true);
	createListItem();
	_contentsWidget->setCurrentRow(0);

	connect(&(ColorKeeperModel::Instance()), SIGNAL(correctionApplied(unsigned int)), this, SLOT(correctionStatusChanged(unsigned int)));

	QVBoxLayout *verticalLayout = new QVBoxLayout;
	verticalLayout->addWidget(_contentsWidget);
	verticalLayout->setAlignment(_contentsWidget, Qt::AlignHCenter);
	setLayout(verticalLayout);
}

void QDisplayDevicePanel::createListItem() {
	ColorKeeperModel & model = ColorKeeperModel::Instance();
	//int nbScreen = model.getDisplayDeviceNumber();
	const ILocalHost &localHost = model.getDeviceInfo();
	const vector<unsigned int> & screenOrder = model.getScreenResOrder();
	for (unsigned int i = 0; i < screenOrder.size(); i++) {
		const ZooperDisplayDevice
				& dispDev =localHost.getCalibrableDisplayDevice(
						screenOrder[i]);
		QListWidgetItem *configButton = new QListWidgetItem(_contentsWidget);
		unsigned int unvalidDays = model.isScreenCorrectionHasBeen(
				dispDev.getOSIndex());
		if (unvalidDays > 0) {
			QString msg("Correction of Screen ");
			msg.append(QString::number(dispDev.getOSIndex()));
			msg.append(" (");
			msg.append(dispDev.getFullName().c_str());
			msg.append(")\nis obsolete for ");
			msg.append(QString::number(unvalidDays));
			msg.append(" day(s) !\nPlease ask for a calib !");

			//			ColorKeeperModel::Instance().emitPopMessage(QString("Warning"),
			//					msg, true);
			ColorKeeperModel::Instance().logMessage(msg.toStdString());

			if (_isMini) {
				QPixmap pix("./img/mini_monitor_icon_warning.png");
				configButton->setIcon(QIcon(pix));
			} else {
				QPixmap pix("./img/flat_monitor_icon_warning.png");
				configButton->setIcon(QIcon(pix));
			}
			//			configButton->setTextColor(QColor(230,150,60));

		} else if (!model.isScreenCorrected(dispDev.getOSIndex())) {
			if (_isMini) {
				QPixmap pix("./img/mini_monitor_icon_pb.png");
				configButton->setIcon(QIcon(pix));
			} else {
				QPixmap pix("./img/flat_monitor_icon_pb.png");
				configButton->setIcon(QIcon(pix));
			}
			QString msg("Screen ");
			msg.append(QString::number(dispDev.getOSIndex()));
			msg.append(" (");
			msg.append(dispDev.getFullName().c_str());
			configButton->setTextColor(QColor(0, 0, 0));
			msg.append(")\nis not corrected !\nPlease ask for a calib !");
			//			configButton->setTextColor(QColor(230,70,60));
			//			ColorKeeperModel::Instance().emitPopMessage(QString("Bad news"),
			//					msg, true);
			ColorKeeperModel::Instance().logMessage(msg.toStdString());
		} else {
			if (_isMini) {
				QPixmap pix("./img/mini_monitor_icon_ok.png");
				configButton->setIcon(QIcon(pix));

			} else {
				QPixmap pix("./img/flat_monitor_icon_ok.png");
				configButton->setIcon(QIcon(pix));
			}
			//			configButton->setTextColor(QColor(30,190,50));
		}

		string deviceName = dispDev.getFullName();

		if (_isMini) {
			deviceName = dispDev.getManufacturerName() + "\n"
					+ dispDev.getModelName();
		}

		configButton->setText(tr(deviceName.c_str()));
		configButton->setFlags(Qt::ItemIsSelectable | Qt::ItemIsEnabled);
	}

	/////add disable screens
	if (!_isMini) {
		unsigned char nbNotCalibrableScreens =
				localHost.getNotCalibrableDisplayDeviceNumber();
		for (int i = 0; i < nbNotCalibrableScreens; i++) {
			const ZooperDisplayDevice
							& dispDev =
									localHost.getNotCalibrableDisplayDevice(
									i);

			QListWidgetItem *configButton =
					new QListWidgetItem(_contentsWidget);
			string deviceName = dispDev.getFullName();
			QPixmap pix("./img/flat_monitor_icon_wa.png");
			configButton->setIcon(QIcon(pix));
			configButton->setToolTip(localHost.getScreenDisorderErrorText() );
			configButton->setText(tr(deviceName.c_str()));
			configButton->setFlags(!Qt::ItemIsSelectable | !Qt::ItemIsEnabled);

		}
	}

}

void QDisplayDevicePanel::correctionStatusChanged(unsigned int index) {
	ColorKeeperModel & model = ColorKeeperModel::Instance();
	const vector<unsigned int> & screenOrder = model.getScreenResOrder();
	QListWidgetItem *configButton = _contentsWidget->item(screenOrder[index]);
	if (model.isScreenCorrected(index))
		configButton->setIcon(QIcon("./img/flat_monitor_icon_ok.png"));
	else
		configButton->setIcon(QIcon("./img/flat_monitor_icon_pb.png"));
}

QDisplayDevicePanel::~QDisplayDevicePanel() {
	//delete _contentsWidget;
}
