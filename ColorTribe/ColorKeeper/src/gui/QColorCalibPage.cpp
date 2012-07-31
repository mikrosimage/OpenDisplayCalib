/*
 * QColorCalibPage.cpp
 *
 *  Created on: 11 mars 2009
 *      Author: mfe
 */

#include "QColorCalibPage.h"
#include "QCalibWindow.h"
#include "../core/ColorKeeperModel.h"
#include "GuiSizeDef.h"
#include "QDrawArea.h"
#include "QPatternsPushButton.h"

#include <QGroupBox>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QPushButton>
#include <QLineEdit>
#include <QLabel>
#include <QUdpSocket>
#include <QComboBox>
#include <QScrollArea>

#include <string>
#include <iostream>
#include <sstream>

using namespace std;

QColorCalibPage::QColorCalibPage(const ZooperDisplayDevice & dispDev,
		QWidget *parent) :
	QWidget(parent), _calibWindow(NULL), _dispDev(dispDev) {
	ColorKeeperModel & model = ColorKeeperModel::Instance();



	if (!model.isQTIndexDisorder()) {
		_calibWindow = new QCalibWindow(_dispDev.getOSIndex());
	} else
		_calibWindow = new QCalibWindow(!_dispDev.getOSIndex());

	///////display pattern group
	ostringstream os;
	os << "Display patterns on screen " << _dispDev.getDesktopIndex() + 1;

	QGroupBox *patternDisplayGroup = new QGroupBox(tr(os.str().c_str()));

	QPixmap pixRamp("./img/pattern_grayRamps_icon.png");
	QPushButton *displayRampButton = new QPushButton(QIcon(pixRamp), "", NULL);
	displayRampButton->setIconSize(QSize(PATTERNICON_SIZE, PATTERNICON_SIZE));
	displayRampButton->setFixedSize(PATTERNICON_SIZE + 15, PATTERNICON_SIZE
			+ 15);
	connect(displayRampButton, SIGNAL(clicked()), this, SLOT(showGrayRamp()));

	QPixmap pixDeath("./img/pattern_death_icon.png");
	QPatternsPushButton *displayDeathButton = new QPatternsPushButton(
			_calibWindow, QIcon(pixDeath), QString(
					"./patterns/mireofthedeath_1500.jpg"));

	QPixmap pixFlite("./img/pattern_flite_icon.png");
	QPatternsPushButton *displayFliteButton = new QPatternsPushButton(
			_calibWindow, QIcon(pixFlite), QString("./patterns/flite.png"));

	QPixmap pixMarcie("./img/pattern_marcie_icon.png");
	QPatternsPushButton *displayMarcieButton = new QPatternsPushButton(
			_calibWindow, QIcon(pixMarcie), QString(
					"./patterns/dlad_2048X1556lin.jpg"));

	///add custom patterns buttons
	QStringList::const_iterator constIterator;
	QStringList customPatterns =
			ColorKeeperModel::Instance().getCustomPatterns();
	const std::vector<QPixmap>& icons =
			ColorKeeperModel::Instance().getCustomPatternsIcons();
	QScrollArea * frame = new QScrollArea;
	frame->setWidgetResizable(true);
	QGridLayout* patternDisplayLayout = new QGridLayout();
	int gridX = 0;
	int gridY = 0;
	QWidget *patternWidget = new QWidget;
	patternWidget->setSizePolicy(QSizePolicy::Minimum, QSizePolicy::Minimum);
	patternWidget->setLayout(patternDisplayLayout);
	frame->setWidget(patternWidget);
	patternDisplayLayout->addWidget(displayRampButton, gridX, gridY);
	gridY++;
	patternDisplayLayout->addWidget(displayDeathButton, gridX, gridY);
	gridY++;
	patternDisplayLayout->addWidget(displayFliteButton, gridX, gridY);
	gridY++;
	patternDisplayLayout->addWidget(displayMarcieButton, gridX, gridY);
	gridY++;
	int i = 0;
	for (constIterator = customPatterns.constBegin(); constIterator
			!= customPatterns.constEnd(); ++constIterator) {
		QString path = (*constIterator);
		path.prepend(ColorKeeperModel::CUSTOM_PATTERNS_PATH.c_str());
		QPatternsPushButton * button = new QPatternsPushButton(_calibWindow,
				QIcon(icons.at(i)), path);
		patternDisplayLayout->addWidget(button, gridX, gridY);
		gridY++;
		if (gridY == 6) {
			gridX++;
			gridY = 0;
		}
		i++;
	}

	QVBoxLayout*generalPatternDisplayLayout = new QVBoxLayout();
	generalPatternDisplayLayout->addWidget(frame);
	patternDisplayGroup->setLayout(generalPatternDisplayLayout);

	/////// launch calib group
	ostringstream os2;
	os2 << "Calibration of screen " << _dispDev.getOSIndex() + 1;
	QGroupBox *launchCalibGroup = new QGroupBox(tr(os2.str().c_str()));
	QLabel * label = new QLabel(tr("IP address : "));

	_ipTxtField = new QLineEdit(model.getIpAdress(_dispDev.getOSIndex()));
	_ipTxtField->setMaxLength(15);
	_ipTxtField->setMaximumWidth(200);

	QPushButton *launchCalibButton = new QPushButton(tr("launch Calib"));
	QHBoxLayout* launchCalibLayout = new QHBoxLayout;
	launchCalibLayout->addWidget(label);
	launchCalibLayout->addWidget(_ipTxtField);
	launchCalibLayout->addWidget(launchCalibButton);
	launchCalibLayout->setAlignment(label, Qt::AlignLeft);
	launchCalibLayout->setAlignment(_ipTxtField, Qt::AlignLeft);
	launchCalibLayout->setAlignment(launchCalibButton, Qt::AlignLeft);
	launchCalibGroup->setLayout(launchCalibLayout);

	connect(launchCalibButton, SIGNAL(clicked()), this, SLOT(
			sendBroadCastCalibRequest()));

	/////// main layout
	QVBoxLayout* groupLayout = new QVBoxLayout();
	groupLayout->addWidget(launchCalibGroup);
	groupLayout->addWidget(patternDisplayGroup);
	setLayout(groupLayout);
	CKSocketClient
			*client =
					const_cast<CKSocketClient *> (&(ColorKeeperModel::Instance().getSocketServer()));
	connect(client, SIGNAL(displayLumContPatt(float, unsigned int, bool)), this, SLOT(showLumContPattern(float,unsigned int, bool)));
	connect(client, SIGNAL(displayPatch(unsigned int, float,float, float, bool)), this, SLOT(showPatch(unsigned int, float,float, float, bool)));
	connect(client, SIGNAL(displayFullScreenRec(unsigned int, float,float, float)), this, SLOT(showFullScreenRec(unsigned int, float,float, float)));
	connect(&(ColorKeeperModel::Instance()), SIGNAL(measuresDone()), this,
			SLOT(conditionnalShowMireOfTheDeath()));
}

void QColorCalibPage::toggleCalibWindow() {
	_calibWindow->showWin();
}

void QColorCalibPage::showGrayRamp() {
	_calibWindow->showWin();
	_calibWindow->setMode(QDrawArea::GRAYRAMP);
}
void QColorCalibPage::conditionnalShowMireOfTheDeath() {
	_calibWindow->showWin();
	if (_calibWindow->getMode() != QDrawArea::IMAGE && _calibWindow->getMode()
			!= QDrawArea::GRAYRAMP)
		showMireOfTheDeath();
}
void QColorCalibPage::showMireOfTheDeath() {
	_calibWindow->showWin();
	_calibWindow->setImagePath(QString("./patterns/mireofthedeath_1500.jpg"));
	_calibWindow->setMode(QDrawArea::IMAGE);
}
void QColorCalibPage::showMarcie() {
	_calibWindow->showWin();
	_calibWindow->setImagePath(QString("./patterns/dlad_2048X1556lin.jpg"));
	_calibWindow->setMode(QDrawArea::IMAGE);
}

void QColorCalibPage::showFliteMire() {
	_calibWindow->showWin();
	_calibWindow->setImagePath(QString("./patterns/flite.png"));
	_calibWindow->setMode(QDrawArea::IMAGE);
}
void QColorCalibPage::showLumContPattern(float value, unsigned int osIndex,
		bool showMosaic) {
	if (osIndex == _dispDev.getOSIndex()) {
		if (value > 0)
			_calibWindow->setGamma(value);
		_calibWindow->setMosaicEnable(showMosaic);
		_calibWindow->setMode(QDrawArea::LUMCONTPATT);
		_calibWindow->showWin();
	}
}

void QColorCalibPage::showPatch(unsigned int osIndex, float r, float g,
		float b, bool halo) {
	if (osIndex == _dispDev.getOSIndex()) {
		_calibWindow->setPatch(r, g, b, halo);
		_calibWindow->setMode(QDrawArea::PATCH);
		_calibWindow->showWin();
	}
}
void QColorCalibPage::showFullScreenRec(unsigned int osIndex, float r, float g,
		float b) {
	if (osIndex == _dispDev.getOSIndex()) {
		_calibWindow->setPatch(r, g, b, false);
		_calibWindow->setMode(QDrawArea::SCREEN_REC);
		_calibWindow->showWin();
	}
}

void QColorCalibPage::sendBroadCastCalibRequest() {
	if (!ColorKeeperModel::Instance().getSocketServer().isConnected()) {
		ColorKeeperModel::Instance().startSocketServer(_ipTxtField->text(),
				_dispDev.getOSIndex());
		ColorKeeperModel::Instance().setIpAdress(_dispDev.getOSIndex(),
				_ipTxtField->text());
		_calibWindow->setPatch(0.25f, 0.25f, 0.25f, true);
		_calibWindow->setMode(QDrawArea::PATCH);
		_calibWindow->showWin();
	}
}

QColorCalibPage::~QColorCalibPage() {
	if (_calibWindow != NULL) {
		_calibWindow->hideWin();
		//delete _calibWindow;
	}
	//delete _ipTxtField;
}
