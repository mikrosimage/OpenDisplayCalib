/*
 * AppMainWin.cpp
 *
 *  Created on: 5 mars 2009
 *      Author: mfe
 */
#include "QKeeperMainWindow.h"
#include "GuiSizeDef.h"
#include "QHeadedMainWidget.h"
#include "QMiniToyWindow.h"
#include "QBalloonDialog.h"
#include "QManagedApplication.h"
#include "../core/ColorKeeperModel.h"

#include <QSize>
#include <QTabWidget>
#include <QVBoxLayout>
#include <QMenu>
#include <QAction>
#include <QCloseEvent>
#include <QSettings>
#include <QDesktopWidget>
#include <QRect>
//
#include <stdlib.h>
#include <string>
#include <iostream>
#include <sstream>

QKeeperMainWindow::QKeeperMainWindow(QApplication &app) :
	QMainWindow(NULL, Qt::CustomizeWindowHint | Qt::WindowTitleHint
			| Qt::WindowMinimizeButtonHint | Qt::WindowCloseButtonHint), _app(
			app) {

	resize(QSize(APPLICATION_WIDTH, APPLICATION_HEIGHT));
#ifdef __Apple__
	setAttribute(Qt::WA_MacShowFocusRect, false);
#endif
	QSettings settings(ColorKeeperModel::Instance().getIniFilePath(),
			QSettings::IniFormat);
	int x = settings.value("GUI/mainX").toInt();
	int y = settings.value("GUI/mainY").toInt();
	if((x==0)&&(y==0))
	{
		QDesktopWidget qdw;
		QRect rectA = qdw.screenGeometry(0);
		x = rectA.width()/2 - APPLICATION_WIDTH/2;
		y = rectA.height()/2 - APPLICATION_HEIGHT/2;
	}
	move(x, y);
	setWindowTitle(tr("ColorKeeper"));
	buildUI();

	createActions();
	createTrayIcon();
	QPixmap pix("./img/icon.png");
	QIcon icon(pix);
	setWindowIcon(icon);
	_trayIcon->setIcon(icon);

	_trayIcon->show();

	_miniToy = new QMiniToyWindow();
	x = settings.value("GUI/miniX").toInt();
	y = settings.value("GUI/miniY").toInt();
	_miniToy->move(x, y);

	ColorKeeperModel &model = ColorKeeperModel::Instance();
	const ILocalHost &host = model.getDeviceInfo();
	int calibrableScreen = host.getCalibrableDisplayDeviceNumber();
	for (int i = 0; i < calibrableScreen; i++) {
		const ZooperDisplayDevice &dev = host.getCalibrableDisplayDevice(i);
		unsigned int osScreenIndex = dev.getOSIndex();
		QString key("screen");
		key.append(QString::number(osScreenIndex));

		settings.beginGroup(key);
		float gamma = (float) settings.value("gamma").toDouble();
		float redPL = (float) settings.value("redPL").toDouble();
		float greenPL = (float) settings.value("greenPL").toDouble();
		float bluePL = (float) settings.value("bluePL").toDouble();
		if ((gamma > ColorKeeperModel::s_gammaUpBound) || (gamma
				< ColorKeeperModel::s_gammaDownBound))
			gamma = 1.0;
		if ((redPL > ColorKeeperModel::s_PLUpBound) || (redPL
				< ColorKeeperModel::s_PLDownBound))
			redPL = 0;
		if ((greenPL > ColorKeeperModel::s_PLUpBound) || (greenPL
				< ColorKeeperModel::s_PLDownBound))
			greenPL = 0;
		if ((bluePL > ColorKeeperModel::s_PLUpBound) || (bluePL
				< ColorKeeperModel::s_PLDownBound))
			bluePL = 0;
		model.setScreenProfilGamma(osScreenIndex, gamma);
		model.setScreenProfilPL(osScreenIndex, redPL, ColorKeeperModel::RED);
		model.setScreenProfilPL(osScreenIndex, greenPL, ColorKeeperModel::GREEN);
		model.setScreenProfilPL(osScreenIndex, bluePL, ColorKeeperModel::BLUE);
		settings.endGroup();

	}
	if (host.isPlugged_But_UnusedScreenDisorder()) {
		QString
				msg(
						"Screen configuration issue.\nSee TroubleShootings doc to see \nhow to avoid it !");
//		ColorKeeperModel::Instance().emitPopMessage(QString("Bad news"), msg,
//				true);
		ColorKeeperModel::Instance().logMessage(msg.toStdString());
	}
	if (host.isMoreDesktopThanEdid()) {
		QString
				msg2(
						"EDID(s) not found.\nSee TroubleShootings doc to see \nhow to avoid it !");

//		ColorKeeperModel::Instance().emitPopMessage(QString("Bad news"), msg2,
//				true);
		ColorKeeperModel::Instance().logMessage(msg2.toStdString());
	}
	if (host.isUnexpectedIssue()) {
		QString
				msg4(
						"Unexpected screen disorder.\nSee TroubleShootings doc to see \nhow to avoid it !");

//		ColorKeeperModel::Instance().emitPopMessage(QString("Bad news"), msg4,
//				true);
		ColorKeeperModel::Instance().logMessage(msg4.toStdString());
	}
#ifdef __WIN32__
	if(host.isQTNbScreenDisorder()) {
		QString msg3("QT screen count error.\nSee TroubleShootings doc to see \nhow to avoid it !");
		ColorKeeperModel::Instance().emitPopMessage(QString("Bad news"),
				msg3, true);
		ColorKeeperModel::Instance().logMessage(msg3.toStdString());
	}
#endif
#ifdef __linux__
	if(host.isSeparateXScreenDisorder()) {
		QString msg("Separate XScreens.\nSee TroubleShootings doc to see \nhow to avoid it !");
		ColorKeeperModel::Instance().logMessage(msg.toStdString());
//		ColorKeeperModel::Instance().emitPopMessage(QString("Bad news"),
//				msg, true);
	}
	if(host.isXScreenDisorder()) {
		QString msg("XScreen disorder.\nSee TroubleShootings doc to see \nhow to avoid it !");
		ColorKeeperModel::Instance().logMessage(msg.toStdString());
//		ColorKeeperModel::Instance().emitPopMessage(QString("Bad news"),
//				msg, true);
	}

#endif


}

void QKeeperMainWindow::buildUI() {
	_mainWidget = new QHeadedMainWidget();
	_mainWidget->resize(QSize(COLORTAB_WIDTH, COLORTAB_HEIGHT));
	layout()->setAlignment(_mainWidget, Qt::AlignHCenter | Qt::AlignTop);
	setCentralWidget(_mainWidget);
	//showMaximized();
}

QKeeperMainWindow::~QKeeperMainWindow() {
	//delete _mainWidget;
}

void QKeeperMainWindow::createActions() {
	_minimizeAction = new QAction(tr("Mi&nimize"), this);
	connect(_minimizeAction, SIGNAL(triggered()), this, SLOT(hide()));

	_restoreAction = new QAction(tr("&Restore"), this);
	connect(_restoreAction, SIGNAL(triggered()), this, SLOT(showNormal()));

	_quitAction = new QAction(tr("&Quit"), this);
	connect(_quitAction, SIGNAL(triggered()), this, SLOT(quit()));

	_showMiniToy = new QAction(tr("Show/hide &MiniToy"), this);
	connect(_showMiniToy, SIGNAL(triggered()), this, SLOT(showMiniToy()));
}

void QKeeperMainWindow::createTrayIcon() {
	_trayIconMenu = new QMenu(this);
	_trayIconMenu->addAction(_minimizeAction);

	_trayIconMenu->addAction(_restoreAction);
	_trayIconMenu->addAction(_showMiniToy);
	_trayIconMenu->addSeparator();
	_trayIconMenu->addAction(_quitAction);

	_trayIcon = new QSystemTrayIcon(this);
	_trayIcon->setContextMenu(_trayIconMenu);
	_trayIcon->setToolTip(QString("ColorKeeper"));

	connect(_trayIcon, SIGNAL(activated(QSystemTrayIcon::ActivationReason)),
			this, SLOT(systrayActivated(QSystemTrayIcon::ActivationReason)));
connect(&(ColorKeeperModel::Instance()), SIGNAL(
				popMessage(QString, QString, bool)), this, SLOT(showMessage(QString,
						QString, bool)));
}

void QKeeperMainWindow::setVisible(bool visible) {
	_minimizeAction->setEnabled(visible);
	_restoreAction->setEnabled(isMaximized() || !visible);
	QMainWindow::setVisible(visible);
}

void QKeeperMainWindow::closeEvent(QCloseEvent *event) {

	if (_trayIcon->isVisible()) {
		hide();
		event->ignore();
	}
}

void QKeeperMainWindow::showMessage(QString title, QString message,
		bool weAreInTrouble) {
	QRect sysTrayGeom = _trayIcon->geometry();
	QPoint position;
#ifdef __linux__
	position = pos();
#else
	position = sysTrayGeom.topLeft();
#endif

	QBalloonDialog *balloonInfo = new QBalloonDialog(title, message,
			position.x(), position.y(), weAreInTrouble);
	balloonInfo->open();

}
void QKeeperMainWindow::showMiniToy() {
	if (_miniToy->isHidden()) {
		//_showMiniToy->setText("Hide &MiniToy");
		_miniToy->showWin();
	} else {
		//_showMiniToy->setText("Show &MiniToy");
		_miniToy->hideWin();
	}
}

void QKeeperMainWindow::quit() {
	ColorKeeperModel &model = ColorKeeperModel::Instance();
	std::ostringstream os;
	os << "settings : " << model.getIniFilePath().toStdString() << std::endl;
	QSettings settings(model.getIniFilePath(), QSettings::IniFormat);
	settings.setPath(QSettings::IniFormat, QSettings::SystemScope,
			model.getIniFilePath());
	settings.setValue("GUI/mainX", this->x());
	settings.setValue("GUI/mainY", this->y());
	settings.setValue("GUI/miniX", _miniToy->x());
	settings.setValue("GUI/miniY", _miniToy->y());
	model.writeSettings();
	model.getOut();
	os << "Setting status " << settings.status() << std::endl;
	ColorKeeperModel::logMessage(os.str());

	ColorKeeperModel::deleteLockFile();

	_app.closeAllWindows();
	_app.quit();

}

void QKeeperMainWindow::systrayActivated(
		QSystemTrayIcon::ActivationReason reason) {
	if (reason == QSystemTrayIcon::DoubleClick) {
		showNormal();
		raise();
	}
}

QWidget* QKeeperMainWindow::getMainWidget(){
	return _mainWidget;
}
