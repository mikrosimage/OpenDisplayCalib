/*
 * QDisplayDevicePage.cpp
 *
 *  Created on: 10 mars 2009
 *      Author: mfe
 */

#include "QColorCronPage.h"
#include "QLutDisplay.h"
#include "GuiSizeDef.h"
#include "../core/ColorKeeperModel.h"

#include <QLabel>
#include <QHBoxLayout>
#include <QVBoxLayout>
#include <QGroupBox>
#include <string>
#include <QCheckBox>

using namespace std;

QColorCronPage::QColorCronPage(const ZooperDisplayDevice & dispDev,
		QWidget *parent) :
	QWidget(parent) {

	unsigned int index = dispDev.getOSIndex();
	QHBoxLayout *groupLayout = new QHBoxLayout;

	///////
	QGroupBox *deviceInfoGroup = new QGroupBox(tr("Device Info"));
	QLabel *devLabel = new QLabel(tr(dispDev.getFullText().c_str()));
	QVBoxLayout *devLayout = new QVBoxLayout;
	devLayout->addWidget(devLabel);
	deviceInfoGroup->setLayout(devLayout);

	QCheckBox * customID = new QCheckBox("enable CUSTOM EDID");
	if (dispDev.needCustomID())
		customID->setVisible(true);
	else
		customID->setVisible(false);
	devLayout->addWidget(customID);
	devLayout->setAlignment(devLabel, Qt::AlignTop);

	//////
	QGroupBox *correctionInfoGroup = new QGroupBox(tr("Correction Info"));
	QVBoxLayout *correctionInfoLayout = new QVBoxLayout;
	string profilePath = "";
	profilePath.append(ColorKeeperModel::Instance().getScreenProfilPath(index));

	QVBoxLayout *labLayout = new QVBoxLayout;
	profLavel = new QLabel(tr(profilePath.c_str()));
	labLayout->addWidget(profLavel);

	QVBoxLayout *lutLayout = new QVBoxLayout;
	_lutDisplay = new QLutDisplay(
			&ColorKeeperModel::Instance().getScreenProfil(index), index);
	_lutDisplay->setFixedSize(SMALL_LUTCANVAS_SIZE, SMALL_LUTCANVAS_SIZE);
	lutLayout->addWidget(_lutDisplay);
	lutLayout->setAlignment(_lutDisplay, Qt::AlignHCenter | Qt::AlignCenter);

	QVBoxLayout *statusLayout = new QVBoxLayout;
	ColorKeeperModel & model = ColorKeeperModel::Instance();
	statusLabel = new QLabel();
	QString msg("");
	unsigned int unvalidDays = model.isScreenCorrectionHasBeen(
			dispDev.getOSIndex());
	if (unvalidDays > 0) {
		msg.append("<font color='orange'><b>Correction is obsolete for ");
		msg.append(QString::number(unvalidDays));
		msg.append(" day(s) !</b></font>");

	} else if (!model.isScreenCorrected(dispDev.getOSIndex())) {
		msg.append("<font color='red'><b>Correction was not found !</b></font>");

	} else {
		msg.append("Correction is ok.");
	}

	statusLabel->setText(msg);
	statusLayout->addWidget(statusLabel);

	correctionInfoLayout->addLayout(labLayout);
	correctionInfoLayout->setAlignment(labLayout, Qt::AlignTop);
	correctionInfoLayout->setAlignment(lutLayout, Qt::AlignHCenter
			| Qt::AlignCenter);
	correctionInfoLayout->setAlignment(statusLayout, Qt::AlignHCenter
			| Qt::AlignCenter);
	correctionInfoLayout->addLayout(lutLayout);
	correctionInfoLayout->addLayout(statusLayout);
	correctionInfoGroup->setLayout(correctionInfoLayout);

	///////
	groupLayout->addWidget(deviceInfoGroup);
	groupLayout->addWidget(correctionInfoGroup);

	setLayout(groupLayout);

	connect(&(ColorKeeperModel::Instance()), SIGNAL(correctionApplied(unsigned int)), this, SLOT(correctionStatusChanged(unsigned int)));
}

void QColorCronPage::correctionStatusChanged(unsigned int index) {
	string profilePath = "Correction file : ";
	profilePath.append(ColorKeeperModel::Instance().getScreenProfilPath(index));
	profLavel->setText(tr(profilePath.c_str()));

	statusLabel->setText("Correction is ok.");
}

QColorCronPage::~QColorCronPage() {
	//	delete _lutDisplay;
	//	delete profLavel;
}
